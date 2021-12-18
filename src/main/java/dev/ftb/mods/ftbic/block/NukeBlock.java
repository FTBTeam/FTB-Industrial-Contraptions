package dev.ftb.mods.ftbic.block;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.util.NukeExplosion;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
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
		// Replace with remote item to activate it

		if (!level.isClientSide() && !(player instanceof FakePlayer)) {
			NukeExplosion.create((ServerLevel) level, pos, FTBICConfig.NUKE_RADIUS, player.getUUID(), player.getScoreboardName());
			player.sendMessage(new TextComponent("Boom!"), Util.NIL_UUID);
		}

		return InteractionResult.SUCCESS;
	}
}
