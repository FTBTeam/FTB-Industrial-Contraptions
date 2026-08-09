package dev.ftb.mods.industrialcontraptions.block.entity.storage;

import dev.ftb.mods.industrialcontraptions.block.ICElectricBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class HVTransformerBlockEntity extends TransformerBlockEntity {
	public HVTransformerBlockEntity(BlockPos pos, BlockState state) {
		super(ICElectricBlocks.HV_TRANSFORMER, pos, state);
	}
}
