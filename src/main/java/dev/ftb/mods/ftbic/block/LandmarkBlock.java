package dev.ftb.mods.ftbic.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
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
import dev.ftb.mods.ftbic.block.entity.machine.DiggingBaseBlockEntity;

public class LandmarkBlock extends Block implements SimpleWaterloggedBlock {
	private static final VoxelShape SHAPE = Shapes.box(0.4375, 0.0, 0.4375, 0.5625, 0.5, 0.5625);

	public LandmarkBlock(BlockBehaviour.Properties props) {
		super(props.strength(0.1F).sound(SoundType.WOOD).noCollision().noOcclusion().lightLevel(v -> 8));
		registerDefaultState(getStateDefinition().any().setValue(BlockStateProperties.WATERLOGGED, false));
	}

	@Override
	protected VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos, CollisionContext ctx) {
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
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction facing, BlockPos facingPos, BlockState facingState, net.minecraft.util.RandomSource random) {
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
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, net.minecraft.world.entity.@org.jetbrains.annotations.Nullable LivingEntity placer, net.minecraft.world.item.ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		dev.ftb.mods.ftbic.FTBIC.LOGGER.info("Landmark.setPlacedBy at {} side={} placer={}",
				pos, level.isClientSide() ? "CLIENT" : "SERVER", placer);
		if (!level.isClientSide()) {
			triggerNearbyResize(level, pos, placer instanceof Player p ? p : null);
		}
	}

	@Override
	protected void affectNeighborsAfterRemoval(BlockState state, net.minecraft.server.level.ServerLevel level, BlockPos pos, boolean movedByPiston) {
		super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
		dev.ftb.mods.ftbic.FTBIC.LOGGER.info("Landmark.affectNeighborsAfterRemoval at {}", pos);
		triggerNearbyResize(level, pos, null);
	}

	private static void triggerNearbyResize(Level level, BlockPos pos, @org.jetbrains.annotations.Nullable Player player) {
		if (!(level instanceof net.minecraft.server.level.ServerLevel server)) return;
		dev.ftb.mods.ftbic.FTBIC.LOGGER.info("Landmark.triggerNearbyResize at {}", pos);
		int radius = 128;
		int hits = 0;
		int minChunkX = (pos.getX() - radius) >> 4;
		int maxChunkX = (pos.getX() + radius) >> 4;
		int minChunkZ = (pos.getZ() - radius) >> 4;
		int maxChunkZ = (pos.getZ() + radius) >> 4;
		for (int cx = minChunkX; cx <= maxChunkX; cx++) {
			for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
				net.minecraft.world.level.chunk.LevelChunk chunk = server.getChunkSource().getChunkNow(cx, cz);
				if (chunk == null) continue;
				for (BlockPos bePos : chunk.getBlockEntitiesPos()) {
					if (Math.abs(bePos.getX() - pos.getX()) > radius) continue;
					if (Math.abs(bePos.getZ() - pos.getZ()) > radius) continue;
					if (Math.abs(bePos.getY() - pos.getY()) > 128) continue;
					if (chunk.getBlockEntity(bePos) instanceof DiggingBaseBlockEntity dig) {
						if (!dig.hasAnchorLandmark()) {
							dev.ftb.mods.ftbic.FTBIC.LOGGER.info("  found DiggingBase at {} but no anchor landmark; skipping", bePos);
							continue;
						}
						hits++;
						dev.ftb.mods.ftbic.FTBIC.LOGGER.info("  found DiggingBase at {} with anchor; calling resize()", bePos);
						dig.resize();
						if (player != null) {
							String machine = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(dig.getBlockState().getBlock()).getPath();
							player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
									String.format("%s at %d,%d,%d area: %d × %d",
											machine, bePos.getX(), bePos.getY(), bePos.getZ(),
											dig.sizeX, dig.sizeZ)));
						}
					}
				}
			}
		}
		dev.ftb.mods.ftbic.FTBIC.LOGGER.info("Landmark.triggerNearbyResize done — {} machines updated", hits);
	}

}
