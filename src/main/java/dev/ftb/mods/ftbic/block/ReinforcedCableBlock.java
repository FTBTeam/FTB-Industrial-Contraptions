package dev.ftb.mods.ftbic.block;

import dev.ftb.mods.ftbic.util.EnergyTier;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class ReinforcedCableBlock extends CableBlock {
	public ReinforcedCableBlock(BlockBehaviour.Properties props, EnergyTier tier) {
		super(props, tier, 0);
	}

	@Override
	public BlockState getBurntState(BlockState state) {
		return FTBICBlocks.BURNT_REINFORCED_CABLE.get().defaultBlockState();
	}

	@Override
	protected boolean propagatesSkylightDown(BlockState state) {
		return false;
	}
}
