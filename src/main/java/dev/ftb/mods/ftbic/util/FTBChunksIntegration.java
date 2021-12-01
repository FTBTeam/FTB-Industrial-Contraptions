package dev.ftb.mods.ftbic.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.UUID;

public class FTBChunksIntegration {
	public static FTBChunksIntegration instance = new FTBChunksIntegration();

	public boolean isProtected(ServerLevel level, BlockPos pos, UUID owner) {
		return false;
	}
}
