package dev.ftb.mods.ftbic.block;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.util.EnergyHandler;
import dev.ftb.mods.ftbic.util.EnergyTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.material.Fluids;

public class CableBlock extends BaseCableBlock {
	public final EnergyTier tier;

	public CableBlock(BlockBehaviour.Properties props, EnergyTier tier, int border, SoundType soundType) {
		super(props, border, soundType);
		this.tier = tier;
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks,
			BlockPos pos, Direction facing, BlockPos facingPos, BlockState facingState, RandomSource random) {
		if (state.getValue(BlockStateProperties.WATERLOGGED)) {
			ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		if (facingState.getBlock() instanceof BurntCableBlock) {
			return state;
		}
		boolean connect = canCableConnectFrom(facingState, level, facingPos, facing.getOpposite());
		if (!(level instanceof Level lvl) || lvl.isClientSide() || state.getValue(CONNECTION[facing.ordinal()]) == connect) {
			return state.setValue(CONNECTION[facing.ordinal()], connect);
		}
		ElectricBlockEntity.electricNetworkUpdated(lvl, facingPos);
		return state.setValue(CONNECTION[facing.ordinal()], connect);
	}

	private boolean canCableConnectFrom(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		if (state.getBlock() instanceof CableBlock otherCable) {
			return otherCable == this;
		}
		if (state.getBlock() instanceof ElectricBlock) {
			return true;
		}
		if (!state.isAir()) {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof EnergyHandler) {
				return true;
			}
		}
		return false;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		Level world = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		BlockState state = defaultBlockState();
		for (Direction direction : Direction.values()) {
			BlockPos p = pos.relative(direction);
			BlockState s = world.getBlockState(p);
			if (canCableConnectFrom(s, world, p, direction.getOpposite())) {
				state = state.setValue(CONNECTION[direction.ordinal()], true);
			}
		}
		return state.setValue(BlockStateProperties.WATERLOGGED,
				world.getFluidState(pos).getType() == Fluids.WATER);
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		super.onPlace(state, level, pos, oldState, movedByPiston);
		if (!level.isClientSide() && !state.is(oldState.getBlock())) {
			ElectricBlockEntity.electricNetworkUpdated(level, pos);
		}
	}

	@Override
	protected void affectNeighborsAfterRemoval(BlockState state, net.minecraft.server.level.ServerLevel level, BlockPos pos, boolean movedByPiston) {
		ElectricBlockEntity.electricNetworkUpdated(level, pos);
	}
}
