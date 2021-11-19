package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;

public class AntimatterConstructorBlockEntity extends ElectricBlockEntity {
	public AntimatterConstructorBlockEntity() {
		super(FTBICElectricBlocks.ANTIMATTER_CONSTRUCTOR);
	}

	@Override
	public void tick() {
		super.tick();
	}
}