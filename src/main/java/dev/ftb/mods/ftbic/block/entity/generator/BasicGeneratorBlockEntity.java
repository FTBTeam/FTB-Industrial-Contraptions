package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.BasicGeneratorFuelRecipe;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/** Burns furnace fuel from slot 0 to generate energy. Fuel lookup uses FTBIC's `BasicGeneratorFuelRecipe`. */
public class BasicGeneratorBlockEntity extends GeneratorBlockEntity {
	@Override
	public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int id, net.minecraft.world.entity.player.Inventory inv) {
		return new dev.ftb.mods.ftbic.screen.BasicGeneratorMenu(id, inv, this);
	}

	public int fuelTicks = 0;
	public int maxFuelTicks = 0;

	public BasicGeneratorBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICElectricBlocks.BASIC_GENERATOR, pos, state);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.putInt("FuelTicks", fuelTicks);
		output.putInt("MaxFuelTicks", maxFuelTicks);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		fuelTicks = input.getIntOr("FuelTicks", 0);
		maxFuelTicks = input.getIntOr("MaxFuelTicks", 0);
	}

	private int getFuelTicksFor(ItemStack stack) {
		if (stack.isEmpty() || !(level instanceof ServerLevel server)) {
			return 0;
		}
		@SuppressWarnings("unchecked")
		net.minecraft.world.item.crafting.RecipeType<BasicGeneratorFuelRecipe> type =
				(net.minecraft.world.item.crafting.RecipeType<BasicGeneratorFuelRecipe>) (net.minecraft.world.item.crafting.RecipeType<?>) FTBICRecipes.BASIC_GENERATOR_FUEL.get();
		for (RecipeHolder<BasicGeneratorFuelRecipe> holder : server.recipeAccess().recipeMap().byType(type)) {
			if (holder.value().ingredient().test(stack)) {
				return holder.value().ticks();
			}
		}
		return 0;
	}

	@Override
	public void handleGeneration() {
		if (fuelTicks > 0) {
			fuelTicks--;
			if (energy < energyCapacity) {
				energy += Math.min(energyCapacity - energy, maxEnergyOutput);
			}
			if (fuelTicks == 0) {
				setChanged();
			}
		}

		if (fuelTicks == 0 && energy < energyCapacity && inputItems.length > 0 && !inputItems[0].isEmpty()) {
			int ticks = getFuelTicksFor(inputItems[0]);
			if (ticks > 0) {
				maxFuelTicks = ticks;
				fuelTicks = ticks;

				net.minecraft.world.item.ItemStackTemplate template = inputItems[0].getItem().getCraftingRemainder();
				ItemStack remainder = template == null ? ItemStack.EMPTY : template.create();
				if (inputItems[0].getCount() == 1) {
					inputItems[0] = remainder.isEmpty() ? ItemStack.EMPTY : remainder;
				} else {
					inputItems[0].shrink(1);
					if (!remainder.isEmpty() && level != null) {
						net.minecraft.world.level.block.Block.popResource(level, worldPosition, remainder);
					}
				}
				active = true;
				setChanged();
			}
		}
	}
}
