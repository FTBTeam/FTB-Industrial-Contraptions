package dev.ftb.mods.ftbic.block.entity;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;

public class IronFurnaceBlockEntity extends FurnaceBlockEntity {
	@Override
	public BlockEntityType<?> getType() {
		return FTBICBlockEntities.IRON_FURNACE.get();
	}

	@Override
	protected Component getDefaultName() {
		return new TranslatableComponent(FTBICBlocks.IRON_FURNACE.get().getDescriptionId());
	}

	@Override
	protected int getTotalCookTime() {
		return super.getTotalCookTime() * 8 / FTBICConfig.IRON_FURNACE_ITEMS_PER_COAL;
	}
}
