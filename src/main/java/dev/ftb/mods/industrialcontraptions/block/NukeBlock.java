package dev.ftb.mods.industrialcontraptions.block;

import dev.ftb.mods.industrialcontraptions.ICConfig;
import dev.ftb.mods.industrialcontraptions.block.entity.ActiveNukeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class NukeBlock extends Block {
	public NukeBlock(BlockBehaviour.Properties props) {
		super(props.instabreak().sound(SoundType.GRASS));
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		if (!(level instanceof ServerLevel server)) {
			return InteractionResult.PASS;
		}

		level.setBlock(pos, ICBlocks.ACTIVE_NUKE.get().defaultBlockState(), 3);
		if (level.getBlockEntity(pos) instanceof ActiveNukeBlockEntity be) {
			be.arm(ICConfig.NUCLEAR.NUKE_FUSE_TICKS.get(),
					ICConfig.NUCLEAR.NUKE_RADIUS.get(),
					player.getUUID(),
					player.getScoreboardName());
		}

		server.getServer().getPlayerList().broadcastSystemMessage(
				Component.translatable("block.ic.nuke.broadcast", player.getDisplayName()), false);

		return InteractionResult.SUCCESS;
	}
}
