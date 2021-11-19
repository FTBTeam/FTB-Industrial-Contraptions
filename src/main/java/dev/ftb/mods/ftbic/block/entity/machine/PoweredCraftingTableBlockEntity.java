package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import net.minecraft.nbt.CompoundTag;

public class PoweredCraftingTableBlockEntity extends ElectricBlockEntity {
	public PoweredCraftingTableBlockEntity() {
		super(FTBICElectricBlocks.POWERED_CRAFTING_TABLE);
	}

	@Override
	public void writeData(CompoundTag tag) {
		super.writeData(tag);
	}

	@Override
	public void readData(CompoundTag tag) {
		super.readData(tag);
	}
}