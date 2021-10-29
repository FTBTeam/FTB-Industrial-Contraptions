package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.util.PowerTier;

public class RotaryMaceratorBlockEntity extends MachineBlockEntity {
	public RotaryMaceratorBlockEntity() {
		super(FTBICElectricBlocks.ROTARY_MACERATOR.blockEntity.get());
		inputPowerTier = PowerTier.MV;
	}
}