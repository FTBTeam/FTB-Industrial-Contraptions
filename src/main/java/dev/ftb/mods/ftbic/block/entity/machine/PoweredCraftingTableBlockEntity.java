package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.util.EnergyTier;
import net.minecraft.nbt.CompoundTag;

public class PoweredCraftingTableBlockEntity extends ElectricBlockEntity {
	public PoweredCraftingTableBlockEntity() {
		super(FTBICElectricBlocks.POWERED_CRAFTING_TABLE.blockEntity.get(), 9, 1);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		inputEnergyTier = EnergyTier.LV;
		energyCapacity = FTBICConfig.POWERED_CRAFTING_TABLE_CAPACITY;
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