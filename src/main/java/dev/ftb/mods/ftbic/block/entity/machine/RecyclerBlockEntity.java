package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.util.PowerTier;

public class RecyclerBlockEntity extends MachineBlockEntity {
	public RecyclerBlockEntity() {
		super(FTBICElectricBlocks.RECYCLER.blockEntity.get(), 1, 1);
		inputPowerTier = PowerTier.MV;
		energyCapacity = 40000;
		// energyUse = 80;
	}
}