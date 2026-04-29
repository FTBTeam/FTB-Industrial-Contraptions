package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.FTBICConfig;
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

public class MachineBlockEntity extends BasicMachineBlockEntity {
	public final MachineRecipeType recipeType;
	public int progress;
	public int maxProgress;
	public boolean starving;

	@Nullable
	private MachineRecipe cachedRecipe;
	private boolean recipeDirty = true;
	private int dirtyTimer = 0;

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
		if (starving) output.putBoolean("Starving", true);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		progress = input.getIntOr("Progress", 0);
		maxProgress = input.getIntOr("MaxProgress", 0);
		starving = input.getBooleanOr("Starving", false);
	}

	private void setStarving(boolean s) {
		if (starving != s) {
			starving = s;
			setChanged();
			if (level != null && !level.isClientSide()) {
				BlockState state = getBlockState();
				level.sendBlockUpdated(worldPosition, state, state, 3);
			}
		}
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
				updateMaxProgress();
				return mr;
			}
		}
		if (recipeType == FTBICRecipes.SMELTING && inputItems.length > 0 && !inputItems[0].isEmpty()) {
			for (RecipeHolder<SmeltingRecipe> holder : server.recipeAccess().recipeMap().byType(RecipeType.SMELTING)) {
				SmeltingRecipe sr = holder.value();
				if (sr.input().test(inputItems[0])) {
					cachedRecipe = adaptCooking(sr, inputItems[0]);
					updateMaxProgress();
					return cachedRecipe;
				}
			}
		}
		cachedRecipe = null;
		maxProgress = 0;
		return null;
	}

	private void updateMaxProgress() {
		if (cachedRecipe == null) {
			maxProgress = 0;
			return;
		}
		double speed = Math.max(1D, progressSpeed);
		maxProgress = Math.max(1, (int) (cachedRecipe.processingTime
				* FTBICConfig.MACHINES.MACHINE_RECIPE_BASE_TICKS.get() / speed));
	}

	@Override
	public void upgradesChanged() {
		super.upgradesChanged();
		updateMaxProgress();
	}

	private MachineRecipe adaptCooking(AbstractCookingRecipe sr, ItemStack inputStack) {
		ItemStack result = sr.assemble(new SingleRecipeInput(inputStack));
		ItemStackTemplate template = new ItemStackTemplate(
				result.typeHolder(), result.getCount(), result.getComponentsPatch());
		double baseTicks = FTBICConfig.MACHINES.MACHINE_RECIPE_BASE_TICKS.get();
		return new MachineRecipe(
				recipeType,
				List.of(new IngredientWithCount(sr.input(), 1)),
				List.of(),
				List.of(new StackWithChance(template, 1D)),
				List.of(),
				sr.cookingTime() / baseTicks,
				false);
	}

	private boolean recipeMatchesInputs(MachineRecipe mr) {
		if (mr.inputs.isEmpty() || inputItems.length == 0) {
			return false;
		}
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
		return true;
	}

	private boolean canFitOutputs(MachineRecipe mr) {
		if (mr.outputs.isEmpty() || outputItems.length == 0) {
			return true;
		}
		ItemStack[] virtual = new ItemStack[outputItems.length];
		for (int i = 0; i < outputItems.length; i++) {
			virtual[i] = outputItems[i].copy();
		}
		for (StackWithChance swc : mr.outputs) {
			ItemStack add = swc.stack();
			int remaining = add.getCount();
			for (int i = 0; i < virtual.length && remaining > 0; i++) {
				if (virtual[i].isEmpty()) {
					ItemStack put = add.copy();
					int take = Math.min(remaining, put.getMaxStackSize());
					put.setCount(take);
					virtual[i] = put;
					remaining -= take;
				} else if (ItemStack.isSameItemSameComponents(virtual[i], add)) {
					int room = virtual[i].getMaxStackSize() - virtual[i].getCount();
					int move = Math.min(room, remaining);
					virtual[i].grow(move);
					remaining -= move;
				}
			}
			if (remaining > 0) return false;
		}
		return true;
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
				dirtyTimer = 0;
			}
			active = false;
			setStarving(recipe != null && energy < energyUse);
			return;
		}

		if (!canFitOutputs(recipe)) {
			active = false;
			setStarving(false);
			return;
		}

		energy -= energyUse;
		progress++;
		active = true;
		setStarving(false);

		if (progress >= maxProgress) {
			consumeInputs(recipe);
			produceOutputs(recipe);
			progress = 0;
			cachedRecipe = null;
			recipeDirty = true;
			setChanged();
			dirtyTimer = 0;
		} else if (++dirtyTimer >= 20) {
			setChanged();
			dirtyTimer = 0;
		}
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
		}
	}
}
