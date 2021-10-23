package dev.ftb.mods.ftbic.block.entity;

import dev.ftb.mods.ftbic.FTBICConfig;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;

public class IronFurnaceBlockEntity extends FurnaceBlockEntity {
	@Override
	public BlockEntityType<?> getType() {
		return FTBICBlockEntities.IRON_FURNACE.get();
	}

	@Override
	protected int getTotalCookTime() {
		return super.getTotalCookTime() * 8 / FTBICConfig.IRON_FURNACE_ITEMS_PER_COAL;
	}
}
