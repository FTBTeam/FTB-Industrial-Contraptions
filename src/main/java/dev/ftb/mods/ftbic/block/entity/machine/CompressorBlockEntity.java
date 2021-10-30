package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;

public class CompressorBlockEntity extends MachineBlockEntity {
	public CompressorBlockEntity() {
		super(FTBICElectricBlocks.COMPRESSOR.blockEntity.get());
		energyCapacity = 8000;
		// use 20
	}
}