package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.ElectricBlockState;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.RecipeCache;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public class BasicGeneratorBlockEntity extends GeneratorBlockEntity {
	public int fuelTicks = 0;

	public BasicGeneratorBlockEntity() {
		super(FTBICElectricBlocks.BASIC_GENERATOR.blockEntity.get(), 1, 0);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		energyCapacity = 16000;
	}

	@Override
	public void writeData(CompoundTag tag) {
		super.writeData(tag);
		tag.putInt("FuelTicks", fuelTicks);
	}

	@Override
	public void readData(CompoundTag tag) {
		super.readData(tag);
		fuelTicks = tag.getInt("FuelTicks");
	}

	@Override
	public void handleGeneration() {
		if (fuelTicks > 0) {
			fuelTicks--;

			if (energy < energyCapacity) {
				energy += Math.min(energyCapacity - energy, FTBICConfig.BASIC_GENERATOR_OUTPUT);
			}

			if (fuelTicks == 0) {
				changeState = ElectricBlockState.OFF;
				setChanged();
			}
		}
	}

	@Override
	public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide()) {
			RecipeCache recipeCache = getRecipeCache();

			if (recipeCache != null) {
				int fuel = recipeCache.getBasicGeneratorFuelTicks(level, player.getItemInHand(hand).getItem());

				if (fuel > 0) {
					if (!player.isCrouching()) {
						player.getItemInHand(hand).shrink(1);
					}

					fuelTicks += fuel;
					changeState = ElectricBlockState.ON;
					setChanged();
					return InteractionResult.SUCCESS;
				}
			}
		}

		return super.rightClick(player, hand, hit);
	}
}
