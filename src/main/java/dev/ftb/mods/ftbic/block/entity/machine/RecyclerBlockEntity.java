package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.util.PowerTier;

public class RecyclerBlockEntity extends MachineBlockEntity {
	public RecyclerBlockEntity() {
		super(FTBICElectricBlocks.RECYCLER.blockEntity.get());
		inputPowerTier = PowerTier.MV;
	}
}