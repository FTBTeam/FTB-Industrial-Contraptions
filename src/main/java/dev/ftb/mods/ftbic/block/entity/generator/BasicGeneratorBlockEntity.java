package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.RecipeCache;
import dev.ftb.mods.ftbic.screen.BasicGeneratorMenu;
import dev.ftb.mods.ftbic.screen.sync.SyncedData;
import dev.ftb.mods.ftbic.screen.sync.SyncedDataKey;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class BasicGeneratorBlockEntity extends GeneratorBlockEntity {
	public static final SyncedDataKey<Integer> FUEL_BAR = new SyncedDataKey<>("fuel_ticks", 0);

	public int fuelTicks = 0;
	public int maxFuelTicks = 0;

	public BasicGeneratorBlockEntity() {
		super(FTBICElectricBlocks.BASIC_GENERATOR);
	}

	@Override
	public void writeData(CompoundTag tag) {
		super.writeData(tag);
		tag.putInt("FuelTicks", fuelTicks);
		tag.putInt("MaxFuelTicks", maxFuelTicks);
	}

	@Override
	public void readData(CompoundTag tag) {
		super.readData(tag);
		fuelTicks = tag.getInt("FuelTicks");
		maxFuelTicks = tag.getInt("MaxFuelTicks");
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		if (slot == 0) {
			RecipeCache recipeCache = getRecipeCache();
			return recipeCache != null && recipeCache.getBasicGeneratorFuelTicks(level, stack) > 0;
		}

		return false;
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

		if (fuelTicks == 0 && energy < energyCapacity && !inputItems[0].isEmpty()) {
			RecipeCache recipeCache = getRecipeCache();

			if (recipeCache != null) {
				maxFuelTicks = recipeCache.getBasicGeneratorFuelTicks(level, inputItems[0]);
				fuelTicks = maxFuelTicks;

				if (maxFuelTicks > 0) {
					if (inputItems[0].getCount() == 1) {
						inputItems[0] = inputItems[0].getContainerItem();
					} else {
						inputItems[0].shrink(1);
					}

					active = true;
					setChanged();
				}
			}
		}
	}

	@Override
	public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide()) {
			openMenu((ServerPlayer) player, (id, inventory) -> new BasicGeneratorMenu(id, inventory, this));
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public void addSyncData(SyncedData data) {
		super.addSyncData(data);
		data.addShort(SyncedData.BAR, () -> fuelTicks == 0 ? 0 : Mth.clamp(Mth.ceil(fuelTicks * 14D / maxFuelTicks), 0, 14));
	}
}
