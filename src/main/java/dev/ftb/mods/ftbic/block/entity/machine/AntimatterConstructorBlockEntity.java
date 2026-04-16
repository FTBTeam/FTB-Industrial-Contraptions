package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.AntimatterBoostRecipe;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import dev.ftb.mods.ftbic.item.FTBICItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import dev.ftb.mods.ftbic.screen.AntimatterConstructorMenu;

public class AntimatterConstructorBlockEntity extends ElectricBlockEntityRef {
	public double progress = 0D;
	public double boostCharge = 0D;
	public static final double PRODUCTION_THRESHOLD = 1_000_000D;

	public AntimatterConstructorBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICElectricBlocks.ANTIMATTER_CONSTRUCTOR, pos, state);
	}

	@Override
	public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int id, net.minecraft.world.entity.player.Inventory inv) {
		return new AntimatterConstructorMenu(id, inv, this);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		if (progress > 0D) output.putDouble("Progress", progress);
		if (boostCharge > 0D) output.putDouble("BoostCharge", boostCharge);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		progress = input.getDoubleOr("Progress", 0D);
		boostCharge = input.getDoubleOr("BoostCharge", 0D);
	}

	private double boostFor(ItemStack stack) {
		if (stack.isEmpty() || !(level instanceof ServerLevel server)) return 0D;
		@SuppressWarnings("unchecked")
		net.minecraft.world.item.crafting.RecipeType<AntimatterBoostRecipe> type =
				(net.minecraft.world.item.crafting.RecipeType<AntimatterBoostRecipe>) (net.minecraft.world.item.crafting.RecipeType<?>) FTBICRecipes.ANTIMATTER_BOOST.get();
		for (RecipeHolder<AntimatterBoostRecipe> holder : server.recipeAccess().recipeMap().byType(type)) {
			if (holder.value().ingredient().test(stack)) {
				return holder.value().boost();
			}
		}
		return 0D;
	}

	@Override
	public void tick() {
		super.tick();
		if (level == null || level.isClientSide()) return;

		// Refill boost charge from the input slot if depleted.
		if (boostCharge <= 0D && inputItems.length > 0 && !inputItems[0].isEmpty()) {
			double newBoost = boostFor(inputItems[0]);
			if (newBoost > 0D) {
				boostCharge = newBoost;
				inputItems[0].shrink(1);
				if (inputItems[0].getCount() <= 0) inputItems[0] = ItemStack.EMPTY;
				setChanged();
			}
		}

		double use = electricBlockInstance.energyUsage.get();
		if (energy < use) return;

		double effectiveBoost = boostCharge > 0D ? Math.max(1D, boostCharge) : 1D;
		energy -= use;
		progress += use * effectiveBoost;
		if (boostCharge > 0D) boostCharge = Math.max(0D, boostCharge - use);
		active = true;

		if (progress >= PRODUCTION_THRESHOLD) {
			ItemStack result = new ItemStack(FTBICItems.ANTIMATTER.item.get());
			if (outputItems.length > 0 && (outputItems[0].isEmpty()
					|| (outputItems[0].getItem() == result.getItem()
							&& outputItems[0].getCount() < outputItems[0].getMaxStackSize()))) {
				if (outputItems[0].isEmpty()) outputItems[0] = result;
				else outputItems[0].grow(1);
				progress -= PRODUCTION_THRESHOLD;
				setChanged();
			}
		}
	}
}
