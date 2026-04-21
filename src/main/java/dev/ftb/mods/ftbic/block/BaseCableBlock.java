package dev.ftb.mods.ftbic.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BaseCableBlock extends Block implements SimpleWaterloggedBlock {
	public static final BooleanProperty[] CONNECTION = new BooleanProperty[6];

	static {
		for (Direction dir : Direction.values()) {
			CONNECTION[dir.get3DDataValue()] = BooleanProperty.create(dir.getSerializedName());
		}
	}

	public final int border;
	public final VoxelShape shapeCenter;
	public final VoxelShape shapeD;
	public final VoxelShape shapeU;
	public final VoxelShape shapeN;
	public final VoxelShape shapeS;
	public final VoxelShape shapeW;
	public final VoxelShape shapeE;
	public final VoxelShape[] shapes;

	public BaseCableBlock(BlockBehaviour.Properties props, int b) {
		super(props);
		border = b;
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
				.setValue(CONNECTION[5], false));
	}

	public VoxelShape getShape(int i) {
		if (shapes[i] == null) {
			shapes[i] = shapeCenter;
			if (((i >> 0) & 1) != 0) shapes[i] = Shapes.or(shapes[i], shapeD);
			if (((i >> 1) & 1) != 0) shapes[i] = Shapes.or(shapes[i], shapeU);
			if (((i >> 2) & 1) != 0) shapes[i] = Shapes.or(shapes[i], shapeN);
			if (((i >> 3) & 1) != 0) shapes[i] = Shapes.or(shapes[i], shapeS);
			if (((i >> 4) & 1) != 0) shapes[i] = Shapes.or(shapes[i], shapeW);
			if (((i >> 5) & 1) != 0) shapes[i] = Shapes.or(shapes[i], shapeE);
		}
		return shapes[i];
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.WATERLOGGED, CONNECTION[0], CONNECTION[1], CONNECTION[2], CONNECTION[3], CONNECTION[4], CONNECTION[5]);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		int index = 0;
		for (Direction direction : Direction.values()) {
			if (state.getValue(CONNECTION[direction.ordinal()])) {
				index |= 1 << direction.ordinal();
			}
		}
		return getShape(index);
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction facing, BlockPos facingPos, BlockState facingState, RandomSource random) {
		if (state.getValue(BlockStateProperties.WATERLOGGED)) {
			ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		return state;
	}

	@Override
	protected boolean propagatesSkylightDown(BlockState state) {
		return !state.getValue(BlockStateProperties.WATERLOGGED);
	}

	@Override
	protected boolean isPathfindable(BlockState state, PathComputationType type) {
		return false;
	}
}
