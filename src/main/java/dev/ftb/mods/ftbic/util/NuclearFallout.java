package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.block.FTBICBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluids;

import java.util.Random;

public final class NuclearFallout {
	private NuclearFallout() {}

	public static void apply(ServerLevel level, BlockPos center, double radius) {
		int rxz = Math.max(1, Mth.ceil(radius * 0.85D));
		long seed = level.getGameTime() ^ center.asLong();
		Random rng = new Random(seed);

		BlockState podzol = Blocks.PODZOL.defaultBlockState();
		BlockState coarseDirt = Blocks.COARSE_DIRT.defaultBlockState();
		BlockState fire = Blocks.FIRE.defaultBlockState();
		BlockState exfluid = FTBICBlocks.EXFLUID.get().defaultBlockState();

		for (int dx = -rxz; dx <= rxz; dx++) {
			for (int dz = -rxz; dz <= rxz; dz++) {
				int distSq = dx * dx + dz * dz;
				if (distSq > rxz * rxz) continue;

				int x = center.getX() + dx;
				int z = center.getZ() + dz;
				int y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - 1;
				if (y < level.getMinY()) continue;

				BlockPos pos = new BlockPos(x, y, z);
				BlockState state = level.getBlockState(pos);

				if (state.is(FTBICUtils.REINFORCED) || state.getDestroySpeed(level, pos) < 0F) continue;
				if (!level.mayInteract(null, pos)) continue;
				if (isShieldedFromCenter(level, center, pos)) continue;

				BlockState replacement = falloutFor(state, level, pos, rng);
				if (replacement == null) continue;
				level.setBlock(pos, replacement, Block.UPDATE_CLIENTS);

				// Occasional fire on top of burnt stone.
				if ((replacement == Blocks.MAGMA_BLOCK.defaultBlockState()
						|| replacement == Blocks.BASALT.defaultBlockState()
						|| replacement == Blocks.BLACKSTONE.defaultBlockState())
						&& rng.nextInt(8) == 0) {
					BlockPos above = pos.above();
					if (level.getBlockState(above).isAir()) {
						level.setBlock(above, fire, Block.UPDATE_CLIENTS);
					}
				}

				// Exfluid pool replaces shallow water/lava puddles.
				if (rng.nextInt(40) == 0
						&& level.getFluidState(pos.above()).getType() != Fluids.EMPTY) {
					level.setBlock(pos.above(), exfluid, Block.UPDATE_CLIENTS);
				}
			}
		}
	}

	private static boolean isShieldedFromCenter(ServerLevel level, BlockPos center, BlockPos surface) {
		int dx = surface.getX() - center.getX();
		int dy = surface.getY() - center.getY();
		int dz = surface.getZ() - center.getZ();
		double dist = Math.sqrt((double) dx * dx + (double) dy * dy + (double) dz * dz);
		if (dist <= 1D) return false;

		BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
		int px = Integer.MIN_VALUE;
		int py = Integer.MIN_VALUE;
		int pz = Integer.MIN_VALUE;
		long surfaceKey = surface.asLong();
		long centerKey = center.asLong();

		for (double l = 0.5D; l < dist; l += 0.5D) {
			int x = center.getX() + Mth.floor(dx * l / dist + 0.5D);
			int y = center.getY() + Mth.floor(dy * l / dist + 0.5D);
			int z = center.getZ() + Mth.floor(dz * l / dist + 0.5D);
			if (px == x && py == y && pz == z) continue;
			px = x;
			py = y;
			pz = z;

			mpos.set(x, y, z);
			long key = mpos.asLong();
			if (key == surfaceKey || key == centerKey) continue;

			BlockState s = level.getBlockState(mpos);
			if (s.is(FTBICUtils.REINFORCED) || s.getDestroySpeed(level, mpos) < 0F) {
				return true;
			}
		}
		return false;
	}

	private static BlockState falloutFor(BlockState state, ServerLevel level, BlockPos pos, Random rng) {
		if (state.getBlock() instanceof GrassBlock) {
			return Blocks.PODZOL.defaultBlockState();
		}
		if (state.is(BlockTags.DIRT)) {
			return Blocks.COARSE_DIRT.defaultBlockState();
		}
		if (state.is(BlockTags.STONE_ORE_REPLACEABLES)
				|| state.is(BlockTags.SAND)
				|| state.is(BlockTags.BASE_STONE_OVERWORLD)) {
			return switch (rng.nextInt(3)) {
				case 0 -> Blocks.MAGMA_BLOCK.defaultBlockState();
				case 1 -> Blocks.BASALT.defaultBlockState();
				default -> Blocks.BLACKSTONE.defaultBlockState();
			};
		}
		return null;
	}
}
