package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.util.EnergyTier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public class QuarryBlockEntity extends ElectricBlockEntity {
	public QuarryBlockEntity() {
		super(FTBICElectricBlocks.QUARRY.blockEntity.get(), 0, 0);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		inputEnergyTier = EnergyTier.MV;
		energyCapacity = FTBICConfig.QUARRY_CAPACITY;
	}

	@Override
	public void writeData(CompoundTag tag) {
		super.writeData(tag);
	}

	@Override
	public void readData(CompoundTag tag) {
		super.readData(tag);
	}

	@Override
	public void stepOn(ServerPlayer player) {
		active = true;
	}
}