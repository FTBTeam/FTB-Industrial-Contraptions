package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/** Daylight-gated energy producer. */
public class SolarPanelBlockEntity extends GeneratorBlockEntity {
	public SolarPanelBlockEntity(ElectricBlockInstance type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int id, net.minecraft.world.entity.player.Inventory inv) {
		return new dev.ftb.mods.ftbic.screen.SolarPanelMenu(id, inv, this);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		maxEnergyOutputTransfer = Math.max(FTBICConfig.ENERGY.LV_TRANSFER_RATE.get(), maxEnergyOutput);
	}

	@Override
	public void handleGeneration() {
		if (level == null) {
			return;
		}
		if (energy < energyCapacity && level.isBrightOutside() && level.canSeeSky(worldPosition.above())) {
			energy += Math.min(energyCapacity - energy, maxEnergyOutput);
			setChanged();
		}
	}
}
