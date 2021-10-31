package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;

public class CanningMachineBlockEntity extends MachineBlockEntity {
	public CanningMachineBlockEntity() {
		super(FTBICElectricBlocks.CANNING_MACHINE.blockEntity.get());
		energyCapacity = 8000;
		// energyUse = 10
	}
}