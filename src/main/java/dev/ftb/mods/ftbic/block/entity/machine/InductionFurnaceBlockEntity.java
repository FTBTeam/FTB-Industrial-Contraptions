package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.util.PowerTier;

public class InductionFurnaceBlockEntity extends MachineBlockEntity {
	public InductionFurnaceBlockEntity() {
		super(FTBICElectricBlocks.INDUCTION_FURNACE.blockEntity.get(), 2, 2);
		inputPowerTier = PowerTier.MV;
	}
}