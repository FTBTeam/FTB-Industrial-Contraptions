package dev.ftb.mods.industrialcontraptions.block.entity.storage;

import dev.ftb.mods.industrialcontraptions.block.ICElectricBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class EVTransformerBlockEntity extends TransformerBlockEntity {
	public EVTransformerBlockEntity(BlockPos pos, BlockState state) {
		super(ICElectricBlocks.EV_TRANSFORMER, pos, state);
	}
}
