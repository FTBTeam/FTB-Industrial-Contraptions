package dev.ftb.mods.ftbic.block.entity.storage;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.util.PowerTier;

public class LVTransformerBlockEntity extends TransformerBlockEntity {
	public LVTransformerBlockEntity() {
		super(FTBICElectricBlocks.LV_TRANSFORMER.blockEntity.get());
		inputPowerTier = PowerTier.MV;
		outputPowerTier = PowerTier.LV;
		energyCapacity = PowerTier.MV.transferRate;
	}
}
