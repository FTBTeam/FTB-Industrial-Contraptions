package dev.ftb.mods.ftbic.block;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.util.PowerTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.energy.CapabilityEnergy;

public class CableBlock extends Block implements SimpleWaterloggedBlock {
	public static final BooleanProperty[] CONNECTION = new BooleanProperty[6];

	static {
		for (Direction dir : Direction.values()) {
			CONNECTION[dir.get3DDataValue()] = BooleanProperty.create(dir.getSerializedName());
		}
	}

	public final PowerTier tier;
	public final int border;
	public final VoxelShape shapeCenter;
	public final VoxelShape shapeD;
	public final VoxelShape shapeU;
	public final VoxelShape shapeN;
	public final VoxelShape shapeS;
	public final VoxelShape shapeW;
	public final VoxelShape shapeE;
	public final VoxelShape[] shapes;

	public CableBlock(PowerTier _tier, int _border, SoundType soundType) {
		super(Properties.of(Material.METAL).strength(0.9F).sound(soundType));
		tier = _tier;
		border = _border;
		int B0 = border;
		int B1 = 16 - border;
		shapeCenter = box(B0, B0, B0, B1, B1, B1);
		shapeD = box(B0, 0, B0, B1, B0, B1);
		shapeU = box(B0, B1, B0, B1, 16, B1);
		shapeN = box(B0, B0, 0, B1, B1, B0);
		shapeS = box(B0, B0, B1, B1, B1, 16);
		shapeW = box(0, B0, B0, B0, B1, B1);
		shapeE = box(B1, B0, B0, 16, B1, B1);
		shapes = new VoxelShape[64];

		registerDefaultState(getStateDefinition().any()
				.setValue(BlockStateProperties.WATERLOGGED, false)
				.setValue(CONNECTION[0], false)
				.setValue(CONNECTION[1], false)
				.setValue(CONNECTION[2], false)
				.setValue(CONNECTION[3], false)
				.setValue(CONNECTION[4], false)
				.setValue(CONNECTION[5], false)
		);
	}

	public VoxelShape getShape(int i) {
		if (shapes[i] == null) {
			shapes[i] = shapeCenter;

			if (((i >> 0) & 1) != 0) {
				shapes[i] = Shapes.or(shapes[i], shapeD);
			}

			if (((i >> 1) & 1) != 0) {
				shapes[i] = Shapes.or(shapes[i], shapeU);
			}

			if (((i >> 2) & 1) != 0) {
				shapes[i] = Shapes.or(shapes[i], shapeN);
			}

			if (((i >> 3) & 1) != 0) {
				shapes[i] = Shapes.or(shapes[i], shapeS);
			}

			if (((i >> 4) & 1) != 0) {
				shapes[i] = Shapes.or(shapes[i], shapeW);
			}

			if (((i >> 5) & 1) != 0) {
				shapes[i] = Shapes.or(shapes[i], shapeE);
			}
		}

		return shapes[i];
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.WATERLOGGED, CONNECTION[0], CONNECTION[1], CONNECTION[2], CONNECTION[3], CONNECTION[4], CONNECTION[5]);
	}

	@Override
	@Deprecated
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		int index = 0;

		for (Direction direction : Direction.values()) {
			if (state.getValue(CONNECTION[direction.ordinal()])) {
				index |= 1 << direction.ordinal();
			}
		}

		return getShape(index);
	}

	@Override
	@Deprecated
	public FluidState getFluidState(BlockState state) {
		return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	@Deprecated
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos) {
		if (state.getValue(BlockStateProperties.WATERLOGGED)) {
			level.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}

		boolean c = canCableConnectFrom(facingState, level, facingPos, facing.getOpposite());

		if (!level.isClientSide() && facingState.getBlock() != this && c != state.getValue(CONNECTION[facing.ordinal()])) {
			ElectricBlockEntity.electricNetworkUpdated(level, facingPos);
		}

		return state.setValue(CONNECTION[facing.ordinal()], c);
	}

	private boolean canCableConnectFrom(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		if (state.getBlock() instanceof CableBlock) {
			return ((CableBlock) state.getBlock()).tier == tier;
		} else if (state.getBlock() instanceof ElectricBlock) {
			return true;
		} else if (!state.isAir()) {
			BlockEntity t = world.getBlockEntity(pos);
			return t != null && t.getCapability(CapabilityEnergy.ENERGY, face).isPresent();
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

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return !state.getValue(BlockStateProperties.WATERLOGGED);
	}

	@Override
	@Deprecated
	public boolean isPathfindable(BlockState arg, BlockGetter arg2, BlockPos arg3, PathComputationType arg4) {
		return false;
	}
}
