package dev.ftb.mods.ftbic.block;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.util.NuclearExplosion;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.FakePlayer;

public class NukeBlock extends Block {
	public NukeBlock() {
		super(Properties.of(Material.EXPLOSIVE).instabreak().sound(SoundType.GRASS));
	}

	@Override
	@Deprecated
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide() && !(player instanceof FakePlayer)) {
			level.setBlock(pos, FTBICBlocks.ACTIVE_NUKE.get().defaultBlockState(), 3);

			NuclearExplosion.builder((ServerLevel) level, pos, FTBICConfig.NUKE_RADIUS, player.getUUID(), player.getScoreboardName())
					.delay(10000L)
					.preExplosion(() -> {
						level.getServer().getPlayerList().broadcastMessage(new TranslatableComponent("block.ftbic.nuke.broadcast", player.getDisplayName()), ChatType.SYSTEM, Util.NIL_UUID);
						level.removeBlock(pos, false);
					})
					.create()
			;
		}

		return InteractionResult.SUCCESS;
	}
}
