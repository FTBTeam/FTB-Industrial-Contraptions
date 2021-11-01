package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.util.PowerTier;

public class SingularityCompressorBlockEntity extends MachineBlockEntity {
	public SingularityCompressorBlockEntity() {
		super(FTBICElectricBlocks.SINGULARITY_COMPRESSOR.blockEntity.get(), 2, 2);
		inputPowerTier = PowerTier.MV;
	}
}