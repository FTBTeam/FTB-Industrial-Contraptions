package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.UUID;

public final class NuclearExplosion {
	private NuclearExplosion() {}

	public static void detonate(ServerLevel level, BlockPos center, double radius, UUID ownerId, String ownerName) {
		long start = System.currentTimeMillis();
		radius = Math.min(radius, 100D);
		int rxz = Mth.ceil(radius);
		double rsq = radius * radius;
		BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
		BlockState air = Blocks.AIR.defaultBlockState();

		int destroyed = 0;
		for (int dx = -rxz; dx <= rxz; dx++) {
			for (int dz = -rxz; dz <= rxz; dz++) {
				for (int dy = -rxz; dy <= rxz; dy++) {
					double distSq = dx * dx + dy * dy + dz * dz;
					if (distSq > rsq) continue;
					mpos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
					if (!level.isInWorldBounds(mpos)) continue;
					BlockState state = level.getBlockState(mpos);
					if (state.isAir()) continue;
					if (state.is(FTBICUtils.REINFORCED)) continue;
					if (state.getDestroySpeed(level, mpos) < 0F) continue;
					level.setBlock(mpos.immutable(), air, Block.UPDATE_CLIENTS);
					destroyed++;
				}
			}
		}

		double cx = center.getX() + 0.5D;
		double cy = center.getY() + 0.5D;
		double cz = center.getZ() + 0.5D;
		AABB aabb = new AABB(cx - radius, cy - radius, cz - radius, cx + radius, cy + radius, cz + radius);
		DamageSource src = level.damageSources().explosion(null, null);
		List<Entity> entities = level.getEntities((Entity) null, aabb);
		for (Entity e : entities) {
			if (!e.isAlive()) continue;
			double distSq = e.distanceToSqr(cx, cy, cz);
			if (distSq > rsq) continue;
			double dist = Math.sqrt(distSq);
			float damage = (float) Math.max(1D, 200D * (1D - dist / radius));
			e.hurtServer(level, src, damage);
		}

		NuclearFallout.apply(level, center, radius);

		long elapsed = System.currentTimeMillis() - start;
		FTBIC.LOGGER.warn("Nuclear explosion by {}/{} at {} radius {} destroyed {} blocks in {}ms",
				ownerName, ownerId, center, radius, destroyed, elapsed);
	}
}
