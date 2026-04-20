package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.screen.SolarPanelMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public class SolarPanelBlockEntity extends GeneratorBlockEntity {
	public SolarPanelBlockEntity(ElectricBlockInstance type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv) {
		return new SolarPanelMenu(id, inv, this);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		maxEnergyOutputTransfer = Math.max(FTBICConfig.ENERGY.LV_TRANSFER_RATE.get(), maxEnergyOutput);
	}

	@Override
	public void handleGeneration() {
		if (energy < energyCapacity && level.isBrightOutside() && level.canSeeSky(worldPosition.above())) {
			energy += Math.min(energyCapacity - energy, maxEnergyOutput);
			setChanged();
		}
	}
}
