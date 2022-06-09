package dev.ftb.mods.ftbic.block.entity;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class IronFurnaceBlockEntity extends FurnaceBlockEntity {
	public IronFurnaceBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	@Override
	public BlockEntityType<?> getType() {
		return FTBICBlockEntities.IRON_FURNACE.get();
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable(FTBICBlocks.IRON_FURNACE.get().getDescriptionId());
	}

	@Override
	protected int getBurnDuration(ItemStack stack) {
		return super.getBurnDuration(stack) * 8 / FTBICConfig.MACHINES.IRON_FURNACE_ITEMS_PER_COAL.get();
	}
}
