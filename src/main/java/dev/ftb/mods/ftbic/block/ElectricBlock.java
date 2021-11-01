package dev.ftb.mods.ftbic.block;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class ElectricBlock extends Block implements SprayPaintable {
	public final ElectricBlockInstance electricBlockInstance;

	public ElectricBlock(ElectricBlockInstance m) {
		super(Properties.of(Material.METAL).strength(3.5F).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops());
		electricBlockInstance = m;
		BlockState state = getStateDefinition().any().setValue(SprayPaintable.DARK, false);

		if (electricBlockInstance.facingProperty != null) {
			state = state.setValue(electricBlockInstance.facingProperty, Direction.SOUTH);
		}

		if (electricBlockInstance.stateProperty != null) {
			state = state.setValue(electricBlockInstance.stateProperty, ElectricBlockState.OFF);
		}

		registerDefaultState(state);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public BlockEntity createTileEntity(BlockState state, BlockGetter level) {
		return electricBlockInstance.blockEntity.get().create();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(SprayPaintable.DARK);

		if (ElectricBlockInstance.current.facingProperty != null) {
			builder.add(ElectricBlockInstance.current.facingProperty);
		}

		if (ElectricBlockInstance.current.stateProperty != null) {
			builder.add(ElectricBlockInstance.current.stateProperty);
		}
	}

	@Override
	@Deprecated
	public BlockState rotate(BlockState state, Rotation rotation) {
		return electricBlockInstance.facingProperty == null ? state : state.setValue(electricBlockInstance.facingProperty, rotation.rotate(state.getValue(electricBlockInstance.facingProperty)));
	}

	@Override
	@Deprecated
	public BlockState mirror(BlockState state, Mirror mirror) {
		return electricBlockInstance.facingProperty == null ? state : state.rotate(mirror.getRotation(state.getValue(electricBlockInstance.facingProperty)));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext arg) {
		if (electricBlockInstance.facingProperty == null) {
			return defaultBlockState();
		} else if (electricBlockInstance.facingProperty == BlockStateProperties.HORIZONTAL_FACING) {
			return defaultBlockState().setValue(electricBlockInstance.facingProperty, arg.getHorizontalDirection().getOpposite());
		} else {
			return defaultBlockState().setValue(electricBlockInstance.facingProperty, arg.getNearestLookingDirection().getOpposite());
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
		if (electricBlockInstance.stateProperty == ElectricBlockState.ON_OFF_BURNT && state.getValue(electricBlockInstance.stateProperty) == ElectricBlockState.BURNT) {
			for (int i = 0; i < 5; i++) {
				level.addParticle(ParticleTypes.SMOKE, pos.getX() + random.nextFloat(), pos.getY() + 1D, pos.getZ() + random.nextFloat(), 0D, 0D, 0D);
			}

			level.addParticle(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5D, pos.getY() + 1D, pos.getZ() + 0.5D, 0D, 0D, 0D);
		}
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
		if (!level.isClientSide() && !state.getBlock().is(state1.getBlock())) {
			ElectricBlockEntity.electricNetworkUpdated(level, pos);

			BlockEntity entity = level.getBlockEntity(pos);

			if (entity instanceof ElectricBlockEntity) {
				((ElectricBlockEntity) entity).onBroken(level, pos);
			}
		}

		super.onRemove(state, level, pos, state1, b);
	}

	@Override
	@Deprecated
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos pos1, boolean b) {
		super.neighborChanged(state, level, pos, block, pos1, b);

		if (!level.isClientSide() && !level.getBlockState(pos1).getBlock().is(block)) {
			ElectricBlockEntity.electricNetworkUpdated(level, pos1);
		}
	}

	@Override
	@Deprecated
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		BlockEntity entity = level.getBlockEntity(pos);

		if (entity instanceof ElectricBlockEntity) {
			return ((ElectricBlockEntity) entity).rightClick(player, hand, hit);
		}

		return InteractionResult.PASS;
	}
}
