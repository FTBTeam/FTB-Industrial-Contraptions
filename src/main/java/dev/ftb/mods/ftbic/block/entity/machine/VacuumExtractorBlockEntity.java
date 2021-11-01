package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.util.PowerTier;

public class VacuumExtractorBlockEntity extends MachineBlockEntity {
	public VacuumExtractorBlockEntity() {
		super(FTBICElectricBlocks.VACUUM_EXTRACTOR.blockEntity.get(), 2, 2);
		inputPowerTier = PowerTier.MV;
	}
}