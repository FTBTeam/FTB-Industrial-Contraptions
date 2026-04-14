package dev.ftb.mods.ftbic.block;

import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public class ExFluidBlock extends Block {
	public ExFluidBlock(BlockBehaviour.Properties props) {
		super(props.strength(0.9F).sound(SoundType.STONE).noLootTable()
				.isValidSpawn((a, b, c, d) -> false).randomTicks());
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		for (Direction direction : FTBICUtils.DIRECTIONS) {
			if (direction != Direction.DOWN && level.getFluidState(pos.relative(direction)).getType() != Fluids.EMPTY) {
				return;
			}
		}
		level.removeBlock(pos, false);
	}
}
