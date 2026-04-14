package dev.ftb.mods.ftbic.block;

import dev.ftb.mods.ftbic.FTBICConfig;
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

/**
 * Triggers a large vanilla {@link net.minecraft.world.level.Explosion} when right-clicked, then
 * paints the surface around the epicentre with {@link dev.ftb.mods.ftbic.util.NuclearFallout}
 * (podzol/coarse-dirt/burnt-stone/exfluid). The full 1.18.2 threaded {@code NuclearExplosion} with
 * gradient destruction + reinforced-block ray-casting is replaced by vanilla's more efficient
 * spherical destruction; the cosmetic surface fallout matches the original mod's feel.
 */
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

		// Swap to active_nuke and detonate a tick later so the state flip is visible first.
		level.setBlock(pos, FTBICBlocks.ACTIVE_NUKE.get().defaultBlockState(), 3);
		server.getServer().getPlayerList().broadcastSystemMessage(
				Component.translatable("block.ftbic.nuke.broadcast", player.getDisplayName()), false);

		double radius = FTBICConfig.NUCLEAR.NUKE_RADIUS.get();
		level.removeBlock(pos, false);
		server.explode(null,
				null, null,
				pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
				(float) radius,
				true,
				Level.ExplosionInteraction.BLOCK);

		dev.ftb.mods.ftbic.util.NuclearFallout.apply(server, pos, radius);
		return InteractionResult.SUCCESS;
	}
}
