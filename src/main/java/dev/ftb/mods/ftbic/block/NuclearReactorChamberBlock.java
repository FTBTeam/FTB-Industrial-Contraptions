package dev.ftb.mods.ftbic.block;

import dev.ftb.mods.ftbic.block.entity.generator.NuclearReactorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class NuclearReactorChamberBlock extends Block {
	public NuclearReactorChamberBlock(BlockBehaviour.Properties props) {
		super(props.strength(3.5F).sound(SoundType.METAL).requiresCorrectToolForDrops());
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor,
			@Nullable Orientation orientation, boolean movedByPiston) {
		super.neighborChanged(state, level, pos, neighbor, orientation, movedByPiston);
		if (!level.isClientSide()) {
			level.invalidateCapabilities(pos);
		}
	}

	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (stack.getItem() instanceof BlockItem) {
			return InteractionResult.PASS;
		}
		InteractionResult result = openReactor(level, pos, player);
		return result == InteractionResult.PASS ? InteractionResult.TRY_WITH_EMPTY_HAND : result;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		InteractionResult result = openReactor(level, pos, player);
		return result == InteractionResult.PASS ? InteractionResult.SUCCESS : result;
	}

	private static InteractionResult openReactor(Level level, BlockPos pos, Player player) {
		if (level.isClientSide()) return InteractionResult.SUCCESS;
		if (!(player instanceof ServerPlayer sp)) return InteractionResult.SUCCESS;
		for (Direction dir : Direction.values()) {
			if (level.getBlockEntity(pos.relative(dir)) instanceof NuclearReactorBlockEntity reactor) {
				reactor.openMenu(sp);
				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.PASS;
	}
}
