package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;

public class ElectricFurnaceBlockEntity extends MachineBlockEntity {
	public ElectricFurnaceBlockEntity() {
		super(FTBICElectricBlocks.ELECTRIC_FURNACE.blockEntity.get());
		energyCapacity = 4160;
		// use 30
	}
}