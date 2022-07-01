package dev.ftb.mods.ftbic.block.entity.storage;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class EVTransformerBlockEntity extends TransformerBlockEntity {
	public EVTransformerBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICElectricBlocks.EV_TRANSFORMER, pos, state);
	}
}
