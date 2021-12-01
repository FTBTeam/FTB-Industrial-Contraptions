package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbchunks.data.ClaimedChunk;
import dev.ftb.mods.ftbchunks.data.FTBChunksAPI;
import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.UUID;

public class FTBChunksIntegrationImpl extends FTBChunksIntegration {
	@Override
	public boolean isProtected(ServerLevel level, BlockPos pos, UUID owner) {
		if (FTBChunksAPI.isManagerLoaded()) {
			ClaimedChunk chunk = FTBChunksAPI.getManager().getChunk(new ChunkDimPos(level, pos));
			return chunk != null && !chunk.getTeamData().isTeamMember(owner);
		}

		return false;
	}
}
