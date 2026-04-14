package dev.ftb.mods.ftbic.block;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * Block class for any FTBIC electric block. Behaviour is mostly delegated to the block entity; this
 * class handles state definitions, placement, rotation/mirror, cable-network notifications on
 * place/remove, fuse-repair right-click, redstone output, the empty-hand `useWithoutItem` menu open,
 * and client-side `animateTick` particle effects (burnt smoke + active-state sparks). Tooltips are
 * rendered by {@link dev.ftb.mods.ftbic.item.ElectricBlockItem}.
 */
public class ElectricBlock extends Block implements EntityBlock, SprayPaintable {
	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

	public final ElectricBlockInstance electricBlockInstance;

	public ElectricBlock(ElectricBlockInstance m, BlockBehaviour.Properties props) {
		super(props.strength(3.5F).requiresCorrectToolForDrops());
		electricBlockInstance = m;
		BlockState state = getStateDefinition().any().setValue(SprayPaintable.DARK, false);

		if (m.facingProperty != null) {
			state = state.setValue(m.facingProperty, Direction.SOUTH);
		}

		if (m.canBeActive) {
			state = state.setValue(ACTIVE, false);
		}

		registerDefaultState(state);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return electricBlockInstance.blockEntity.get().create(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return electricBlockInstance.tickClientSide || !level.isClientSide() ? ElectricBlockEntity::ticker : null;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(SprayPaintable.DARK);

		if (ElectricBlockInstance.current != null) {
			if (ElectricBlockInstance.current.facingProperty != null) {
				builder.add(ElectricBlockInstance.current.facingProperty);
			}
			if (ElectricBlockInstance.current.canBeActive) {
				builder.add(ACTIVE);
			}
		}
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rotation) {
		return electricBlockInstance.facingProperty == null
				? state
				: state.setValue(electricBlockInstance.facingProperty, rotation.rotate(state.getValue(electricBlockInstance.facingProperty)));
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		if (electricBlockInstance.facingProperty == null) return state;
		Rotation rotation = mirror.getRotation(state.getValue(electricBlockInstance.facingProperty));
		return state.setValue(electricBlockInstance.facingProperty, rotation.rotate(state.getValue(electricBlockInstance.facingProperty)));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		if (electricBlockInstance.facingProperty == null) {
			return defaultBlockState();
		} else if (electricBlockInstance.facingProperty == BlockStateProperties.HORIZONTAL_FACING) {
			return defaultBlockState().setValue(electricBlockInstance.facingProperty, ctx.getHorizontalDirection().getOpposite());
		} else {
			return defaultBlockState().setValue(electricBlockInstance.facingProperty, ctx.getNearestLookingDirection().getOpposite());
		}
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource r) {
		if (level.getBlockEntity(pos) instanceof ElectricBlockEntity be) {
			if (be.isBurnt()) {
				if (r.nextInt(2) == 0) {
					double dx = pos.getX() + 0.4 + r.nextDouble() * 0.2;
					double dy = pos.getY() + 0.8 + r.nextDouble() * 0.2;
					double dz = pos.getZ() + 0.4 + r.nextDouble() * 0.2;
					level.addParticle(net.minecraft.core.particles.ParticleTypes.LARGE_SMOKE, dx, dy, dz, 0D, 0.01D, 0D);
				}
			} else if (electricBlockInstance.canBeActive
					&& state.hasProperty(ACTIVE) && state.getValue(ACTIVE)
					&& r.nextInt(6) == 0) {
				double dx = pos.getX() + r.nextDouble();
				double dy = pos.getY() + 0.1 + r.nextDouble() * 0.2;
				double dz = pos.getZ() + r.nextDouble();
				level.addParticle(net.minecraft.core.particles.ParticleTypes.ELECTRIC_SPARK, dx, dy, dz, 0D, 0D, 0D);
			}
		}
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState state1, boolean b) {
		super.onPlace(state, level, pos, state1, b);
		if (!level.isClientSide() && !state.is(state1.getBlock())) {
			ElectricBlockEntity.electricNetworkUpdated(level, pos);
		}
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
		super.setPlacedBy(level, pos, state, entity, stack);
		if (level.getBlockEntity(pos) instanceof ElectricBlockEntity be) {
			be.onPlacedBy(entity, stack);
		}
	}

	@Override
	protected void affectNeighborsAfterRemoval(BlockState state, net.minecraft.server.level.ServerLevel level, BlockPos pos, boolean movedByPiston) {
		ElectricBlockEntity.electricNetworkUpdated(level, pos);
		level.updateNeighbourForOutputSignal(pos, this);
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @org.jetbrains.annotations.Nullable net.minecraft.world.level.redstone.Orientation orientation, boolean movedByPiston) {
		super.neighborChanged(state, level, pos, block, orientation, movedByPiston);
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof ElectricBlockEntity be) {
			be.neighborChanged(pos, block);
		}
	}

	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, net.minecraft.world.InteractionHand hand, BlockHitResult hit) {
		if (!(level.getBlockEntity(pos) instanceof ElectricBlockEntity be)) {
			return InteractionResult.TRY_WITH_EMPTY_HAND;
		}
		if (be.isBurnt() && stack.is(dev.ftb.mods.ftbic.item.FTBICItems.FUSE.item.get())) {
			be.setBurnt(false);
			level.playSound(player, pos, net.minecraft.sounds.SoundEvents.STONE_BUTTON_CLICK_ON,
					net.minecraft.sounds.SoundSource.BLOCKS, 0.3F, 0.6F);
			if (!level.isClientSide() && !player.isCreative()) {
				stack.shrink(1);
			}
			return InteractionResult.SUCCESS;
		}
		if (be.isBurnt()) {
			if (!level.isClientSide()) {
				player.sendSystemMessage(net.minecraft.network.chat.Component.translatable("ftbic.fuse_info"));
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.TRY_WITH_EMPTY_HAND;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof ElectricBlockEntity be && !be.isBurnt()) {
			return be.rightClick(player, net.minecraft.world.InteractionHand.MAIN_HAND, hit);
		}
		return InteractionResult.PASS;
	}

	@Override
	protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof ElectricBlockEntity be) {
			return be.getRedstoneOutputSignalEnergyStorage();
		}
		return 0;
	}

	@Override
	protected boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}
}
