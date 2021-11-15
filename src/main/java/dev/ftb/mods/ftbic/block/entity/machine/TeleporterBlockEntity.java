package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.util.EnergyTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class TeleporterBlockEntity extends ElectricBlockEntity {
	public BlockPos linkedPos;
	public ResourceKey<Level> linkedLevel;

	public TeleporterBlockEntity() {
		super(FTBICElectricBlocks.TELEPORTER.blockEntity.get(), 0, 0);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		inputEnergyTier = EnergyTier.XV;
		energyCapacity = FTBICConfig.TELEPORTER_CAPACITY;
	}

	@Override
	public void writeData(CompoundTag tag) {
		super.writeData(tag);

		if (linkedPos != null && linkedLevel != null) {
			tag.putInt("LinkedX", linkedPos.getX());
			tag.putInt("LinkedY", linkedPos.getY());
			tag.putInt("LinkedZ", linkedPos.getZ());
			tag.putString("LinkedLevel", linkedLevel.location().toString());
		}
	}

	@Override
	public void readData(CompoundTag tag) {
		super.readData(tag);

		linkedPos = null;
		linkedLevel = null;

		if (tag.contains("LinkedLevel")) {
			linkedPos = new BlockPos(tag.getInt("LinkedX"), tag.getInt("LinkedY"), tag.getInt("LinkedZ"));
			linkedLevel = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(tag.getString("LinkedLevel")));
		}
	}

	@Override
	public void writeNetData(CompoundTag tag) {
		super.writeNetData(tag);
		// write the preview
	}

	@Override
	public void readNetData(CompoundTag tag) {
		super.readNetData(tag);
		// read the preview
	}
}