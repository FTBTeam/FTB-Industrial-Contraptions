package dev.ftb.mods.ftbic.block;

import dev.ftb.mods.ftbic.block.entity.machine.DiggingBaseBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class LandmarkBlock extends Block implements SimpleWaterloggedBlock {
	private static final VoxelShape SHAPE = Shapes.box(0.4375, 0.0, 0.4375, 0.5625, 0.5, 0.5625);

	public LandmarkBlock(BlockBehaviour.Properties props) {
		super(props.strength(0.1F).sound(SoundType.WOOD).noCollision().noOcclusion().lightLevel(v -> 8));
		registerDefaultState(getStateDefinition().any().setValue(BlockStateProperties.WATERLOGGED, false));
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return SHAPE;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.WATERLOGGED);
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

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		Level world = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		return defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, world.getFluidState(pos).getType() == Fluids.WATER);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (!level.isClientSide()) {
			triggerNearbyResize(level, pos, player);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		if (!level.isClientSide()) {
			triggerNearbyResize(level, pos, placer instanceof Player p ? p : null);
		}
	}

	@Override
	protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
		super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
		triggerNearbyResize(level, pos, null);
	}

	private static void triggerNearbyResize(Level level, BlockPos pos, @Nullable Player player) {
		if (!(level instanceof ServerLevel)) return;
		int radius = 128;
		StringBuilder report = null;
		for (DiggingBaseBlockEntity dig : DiggingBaseBlockEntity.forLevel(level)) {
			if (dig.isRemoved()) continue;
			BlockPos bePos = dig.getBlockPos();
			if (Math.abs(bePos.getX() - pos.getX()) > radius) continue;
			if (Math.abs(bePos.getZ() - pos.getZ()) > radius) continue;
			if (Math.abs(bePos.getY() - pos.getY()) > 128) continue;
			if (!dig.hasAnchorLandmark()) continue;
			dig.resize();
			if (player != null) {
				Identifier key = BuiltInRegistries.BLOCK.getKey(dig.getBlockState().getBlock());
				String machine = key == null ? "machine" : key.getPath();
				if (report == null) report = new StringBuilder();
				else report.append('\n');
				report.append(String.format("%s at %d,%d,%d area: %d x %d",
						machine, bePos.getX(), bePos.getY(), bePos.getZ(),
						dig.sizeX, dig.sizeZ));
			}
		}
		if (player != null && report != null) {
			player.sendSystemMessage(Component.literal(report.toString()));
		}
	}

}
