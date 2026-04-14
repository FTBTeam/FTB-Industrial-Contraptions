package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import dev.ftb.mods.ftbic.recipe.MachineRecipe;
import dev.ftb.mods.ftbic.recipe.MachineRecipeType;
import dev.ftb.mods.ftbic.util.IngredientWithCount;
import dev.ftb.mods.ftbic.util.StackWithChance;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Recipe-driven machine base. Subclasses declare their {@link MachineRecipeType} (e.g. MACERATING);
 * the tick loop finds a matching recipe based on the input buffer, consumes energy, advances progress,
 * and emits outputs when progress hits the recipe's processingTime. Upgrade-slot effects and the
 * battery-slot charging loop live in {@link BasicMachineBlockEntity}; output-chance RNG is honoured
 * by {@link #addOutputs} via the {@link dev.ftb.mods.ftbic.util.StackWithChance} list.
 */
public class MachineBlockEntity extends BasicMachineBlockEntity {
	public final MachineRecipeType recipeType;
	public int progress;
	public int maxProgress;

	@Nullable
	private MachineRecipe cachedRecipe;
	/**
	 * Set whenever input slots change — forces a full recipe-map scan on the next findRecipe() call.
	 * Without this, an idle machine (no matching recipe, non-empty inputs) would iterate the whole
	 * recipe map every tick. We only scan when something about the inputs has actually changed.
	 */
	private boolean recipeDirty = true;

	public MachineBlockEntity(ElectricBlockInstance type, MachineRecipeType recipeType,
			BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.recipeType = recipeType;
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		if (slot < inputItems.length) {
			recipeDirty = true;
		}
		super.setStackInSlot(slot, stack);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		if (progress > 0) output.putInt("Progress", progress);
		if (maxProgress > 0) output.putInt("MaxProgress", maxProgress);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		progress = input.getIntOr("Progress", 0);
		maxProgress = input.getIntOr("MaxProgress", 0);
	}

	@Nullable
	private MachineRecipe findRecipe() {
		if (level == null || !(level instanceof ServerLevel server)) {
			return null;
		}
		if (cachedRecipe != null && recipeMatchesInputs(cachedRecipe)) {
			return cachedRecipe;
		}
		// Only scan the full recipe map when inputs actually changed — otherwise an idle machine
		// with non-matching inputs would iterate every recipe every tick.
		if (!recipeDirty) {
			return null;
		}
		recipeDirty = false;
		for (RecipeHolder<?> holder : server.recipeAccess().recipeMap().byType(recipeType.TYPE.get())) {
			if (holder.value() instanceof MachineRecipe mr && recipeMatchesInputs(mr)) {
				cachedRecipe = mr;
				return mr;
			}
		}
		if (recipeType == FTBICRecipes.SMELTING && inputItems.length > 0 && !inputItems[0].isEmpty()) {
			for (RecipeHolder<SmeltingRecipe> holder : server.recipeAccess().recipeMap().byType(RecipeType.SMELTING)) {
				SmeltingRecipe sr = holder.value();
				if (sr.input().test(inputItems[0])) {
					MachineRecipe synthetic = adaptCooking(sr, inputItems[0]);
					if (canFitOutputs(synthetic)) {
						cachedRecipe = synthetic;
						return synthetic;
					}
				}
			}
		}
		cachedRecipe = null;
		return null;
	}

	private MachineRecipe adaptCooking(AbstractCookingRecipe sr, ItemStack inputStack) {
		ItemStack result = sr.assemble(new SingleRecipeInput(inputStack));
		ItemStackTemplate template = new ItemStackTemplate(
				result.typeHolder(), result.getCount(), result.getComponentsPatch());
		return new MachineRecipe(
				recipeType,
				List.of(new IngredientWithCount(sr.input(), 1)),
				List.of(),
				List.of(new StackWithChance(template, 1D)),
				List.of(),
				(double) sr.cookingTime(),
				false);
	}

	private boolean recipeMatchesInputs(MachineRecipe mr) {
		if (mr.inputs.isEmpty() || inputItems.length == 0) {
			return false;
		}
		// Every ingredient must be satisfied by some input slot.
		boolean[] used = new boolean[inputItems.length];
		for (IngredientWithCount need : mr.inputs) {
			boolean found = false;
			for (int i = 0; i < inputItems.length; i++) {
				if (used[i]) continue;
				if (need.matches(inputItems[i])) {
					used[i] = true;
					found = true;
					break;
				}
			}
			if (!found) return false;
		}
		return canFitOutputs(mr);
	}

	private boolean canFitOutputs(MachineRecipe mr) {
		if (mr.outputs.isEmpty() || outputItems.length == 0) {
			return true;
		}
		// Simple heuristic: at least one output slot empty or stackable with a primary output.
		ItemStack primary = mr.outputs.get(0).stack();
		for (ItemStack out : outputItems) {
			if (out.isEmpty()) return true;
			if (ItemStack.isSameItemSameComponents(out, primary) && out.getCount() + primary.getCount() <= out.getMaxStackSize()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void tick() {
		super.tick();
		if (level == null || level.isClientSide()) {
			return;
		}

		MachineRecipe recipe = findRecipe();

		if (recipe == null || energy < energyUse) {
			if (progress != 0) {
				progress = 0;
				setChanged();
			}
			active = false;
			return;
		}

		maxProgress = (int) recipe.processingTime;
		energy -= energyUse;
		// progressSpeed is the overclocker multiplier (1.0 default, 1.45^N after N overclockers).
		progress += Math.max(1, (int) Math.round(progressSpeed));
		active = true;

		if (progress >= maxProgress) {
			consumeInputs(recipe);
			produceOutputs(recipe);
			progress = 0;
			cachedRecipe = null;
			recipeDirty = true;
		}
		// Persist progress + energy changes so a chunk unload / server stop mid-recipe doesn't lose them.
		setChanged();
	}

	private void consumeInputs(MachineRecipe mr) {
		for (IngredientWithCount need : mr.inputs) {
			for (int i = 0; i < inputItems.length; i++) {
				if (need.matches(inputItems[i])) {
					inputItems[i].shrink(need.count());
					if (inputItems[i].getCount() <= 0) {
						inputItems[i] = ItemStack.EMPTY;
					}
					break;
				}
			}
		}
	}

	private void produceOutputs(MachineRecipe mr) {
		List<StackWithChance> outs = mr.outputs;
		if (outs.isEmpty() || outputItems.length == 0) return;

		for (StackWithChance swc : outs) {
			if (swc.chance() < 1D && level.getRandom().nextDouble() >= swc.chance()) {
				continue;
			}
			ItemStack toAdd = swc.stack().copy();
			// Find a slot that can stack with it first.
			for (int i = 0; i < outputItems.length && !toAdd.isEmpty(); i++) {
				ItemStack existing = outputItems[i];
				if (existing.isEmpty()) {
					outputItems[i] = toAdd;
					toAdd = ItemStack.EMPTY;
				} else if (ItemStack.isSameItemSameComponents(existing, toAdd)) {
					int room = existing.getMaxStackSize() - existing.getCount();
					int move = Math.min(room, toAdd.getCount());
					existing.grow(move);
					toAdd.shrink(move);
				}
			}
			// Leftover overflow — if the output slots are full, pop the rest into the world so the
			// machine doesn't stall and the recipe can continue next tick.
			if (!toAdd.isEmpty() && level != null) {
				net.minecraft.world.level.block.Block.popResource(level, worldPosition, toAdd);
			}
		}
	}
}
