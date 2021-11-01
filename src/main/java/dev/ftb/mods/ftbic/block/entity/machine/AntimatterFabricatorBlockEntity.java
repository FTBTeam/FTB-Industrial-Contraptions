package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.util.PowerTier;

public class AntimatterFabricatorBlockEntity extends MachineBlockEntity {
	public AntimatterFabricatorBlockEntity() {
		super(FTBICElectricBlocks.ANTIMATTER_FABRICATOR.blockEntity.get(), 0, 1);
		inputPowerTier = PowerTier.EV;
	}
}