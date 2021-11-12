package dev.ftb.mods.ftbic.block;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.util.EnergyHandler;
import dev.ftb.mods.ftbic.util.EnergyTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;

public class CableBlock extends BaseCableBlock {
	public final EnergyTier tier;

	public CableBlock(EnergyTier _tier, int border, SoundType soundType) {
		super(border, soundType);
		tier = _tier;
	}

	@Override
	@Deprecated
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos) {
		if (state.getValue(BlockStateProperties.WATERLOGGED)) {
			level.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}

		if (facingState.getBlock() instanceof BurntCableBlock) {
			return state;
		}

		boolean c = canCableConnectFrom(facingState, level, facingPos, facing.getOpposite());

		if (!level.isClientSide() && facingState.getBlock() != this && c != state.getValue(CONNECTION[facing.ordinal()])) {
			ElectricBlockEntity.electricNetworkUpdated(level, facingPos);
		}

		return state.setValue(CONNECTION[facing.ordinal()], c);
	}

	private boolean canCableConnectFrom(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		if (state.getBlock() instanceof CableBlock) {
			return state.getBlock() == this;
		} else if (state.getBlock() instanceof ElectricBlock) {
			return true;
		} else if (!state.isAir()) {
			BlockEntity t = world.getBlockEntity(pos);
			//return t != null && t.getCapability(CapabilityEnergy.ENERGY, face).isPresent();
			return t instanceof EnergyHandler;
		}

		return false;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState state = defaultBlockState();

		for (Direction direction : Direction.values()) {
			BlockPos p = pos.relative(direction);
			BlockState s = world.getBlockState(p);

			if (canCableConnectFrom(s, world, p, direction.getOpposite())) {
				state = state.setValue(CONNECTION[direction.ordinal()], true);
			}
		}

		return state.setValue(BlockStateProperties.WATERLOGGED, world.getFluidState(pos).getType() == Fluids.WATER);
	}

	@Override
	@Deprecated
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState state1, boolean b) {
		super.onPlace(state, level, pos, state1, b);

		if (!level.isClientSide() && !state.getBlock().is(state1.getBlock())) {
			ElectricBlockEntity.electricNetworkUpdated(level, pos);
		}
	}

	@Override
	@Deprecated
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState state1, boolean b) {
		super.onRemove(state, level, pos, state1, b);

		if (!level.isClientSide() && !state.getBlock().is(state1.getBlock())) {
			ElectricBlockEntity.electricNetworkUpdated(level, pos);
		}
	}
}
