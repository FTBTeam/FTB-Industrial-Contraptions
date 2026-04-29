package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.FTBIC;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.Random;
import java.util.UUID;

public final class NuclearExplosion {
	private static final byte FLAG_IN_EXPLOSION = 1 << 0;
	private static final byte FLAG_REINFORCED = 1 << 1;
	private static final byte FLAG_AIR = 1 << 2;
	private static final byte FLAG_BREAKABLE = 1 << 3;
	private static final byte FLAG_DESTROY = 1 << 4;
	private static final byte FLAG_INSIDE = 1 << 5;
	private static final byte FLAG_OUTSIDE = (byte) (1 << 6);
	private static final byte MASK_KIND = FLAG_REINFORCED | FLAG_AIR | FLAG_BREAKABLE;

	private NuclearExplosion() {}

	public static void detonate(ServerLevel level, BlockPos center, double radius, UUID ownerId, String ownerName) {
		long start = System.currentTimeMillis();
		radius = Math.min(radius, 100D);
		int rxz = Mth.ceil(radius);
		double ry = Math.min(radius * 0.75D, 60D);
		double rys = ry / radius;
		double rsq = radius * radius;
		double rsqInner = (radius * 0.65D) * (radius * 0.65D);

		int yLo = Math.max(center.getY() - Mth.ceil(ry * 2D), level.getMinY());
		int yHi = Math.min(center.getY() + Mth.ceil(ry * 2D), level.getMaxY() - 1);

		long seed = level.getGameTime() ^ center.asLong();
		Random rngVolume = new Random(seed);
		Random rngScan = new Random(seed);

		long volume = 0L;
		for (int dx = -rxz; dx <= rxz; dx++) {
			for (int dz = -rxz; dz <= rxz; dz++) {
				for (int y = yHi; y >= yLo; y--) {
					int dy = y - center.getY();
					double y1 = dy / rys;
					double dist = (dx * dx + y1 * y1 + dz * dz) / (1D - rngVolume.nextDouble() * 0.25D);
					if (dist <= rsq) volume++;
				}
			}
		}

		Long2ByteOpenHashMap blocks = new Long2ByteOpenHashMap((int) Math.min(volume, Integer.MAX_VALUE));
		blocks.defaultReturnValue((byte) 0);
		LongList crust = new LongArrayList();
		BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();

		for (int dx = -rxz; dx <= rxz; dx++) {
			for (int dz = -rxz; dz <= rxz; dz++) {
				for (int y = yHi; y >= yLo; y--) {
					int dy = y - center.getY();
					double y1 = dy / rys;
					double dist = (dx * dx + y1 * y1 + dz * dz) / (1D - rngScan.nextDouble() * 0.25D);
					if (dist > rsq) continue;

					mpos.set(center.getX() + dx, y, center.getZ() + dz);
					if (!level.isInWorldBounds(mpos)) continue;

					BlockState state;
					byte kind;
					try {
						state = level.getBlockState(mpos);
					} catch (Exception ex) {
						FTBIC.LOGGER.warn("Error reading block state during nuclear explosion at {}", mpos, ex);
						blocks.put(mpos.asLong(), (byte) (FLAG_IN_EXPLOSION | FLAG_REINFORCED | FLAG_INSIDE));
						continue;
					}

					if (state.isAir()) {
						kind = FLAG_AIR;
					} else if (state.is(FTBICUtils.REINFORCED) || state.getDestroySpeed(level, mpos) < 0F) {
						kind = FLAG_REINFORCED;
					} else {
						kind = FLAG_BREAKABLE;
					}

					long key = mpos.asLong();
					blocks.put(key, (byte) (FLAG_IN_EXPLOSION | kind | FLAG_INSIDE));

					if (dist >= rsqInner) {
						crust.add(key);
					}
				}
			}
		}

		LongList trimmedCrust = new LongArrayList(crust.size());
		BlockPos.MutableBlockPos npos = new BlockPos.MutableBlockPos();
		for (long key : crust) {
			BlockPos p = BlockPos.of(key);
			boolean fullyEnclosed = true;
			for (Direction dir : FTBICUtils.DIRECTIONS) {
				npos.set(p.getX() + dir.getStepX(), p.getY() + dir.getStepY(), p.getZ() + dir.getStepZ());
				if (!blocks.containsKey(npos.asLong())) {
					fullyEnclosed = false;
					break;
				}
			}
			if (!fullyEnclosed) {
				trimmedCrust.add(key);
				byte v = blocks.get(key);
				blocks.put(key, (byte) ((v & ~FLAG_INSIDE) | FLAG_OUTSIDE));
			}
		}

		long centerKey = center.asLong();
		byte centerFlags = blocks.get(centerKey);
		if (centerFlags != 0 && (centerFlags & FLAG_REINFORCED) == 0) {
			blocks.put(centerKey, (byte) (centerFlags | FLAG_DESTROY));
		}

		double step = 0.5D;
		for (long key : trimmedCrust) {
			BlockPos p = BlockPos.of(key);
			traceCrustToCenter(blocks, mpos, center, p, step);
		}

		LongList interior = new LongArrayList();
		for (var entry : blocks.long2ByteEntrySet()) {
			byte v = entry.getByteValue();
			if ((v & FLAG_DESTROY) != 0) continue;
			if ((v & FLAG_REINFORCED) != 0) continue;
			interior.add(entry.getLongKey());
		}
		LongList toMark = new LongArrayList();
		for (long key : interior) {
			BlockPos p = BlockPos.of(key);
			if (canReachCenter(blocks, mpos, center, p, step)) {
				toMark.add(key);
			}
		}
		for (long key : toMark) {
			byte v = blocks.get(key);
			blocks.put(key, (byte) (v | FLAG_DESTROY));
		}

		BlockState air = Blocks.AIR.defaultBlockState();
		int destroyed = 0;
		for (var entry : blocks.long2ByteEntrySet()) {
			byte v = entry.getByteValue();
			if ((v & FLAG_DESTROY) == 0) continue;
			if ((v & FLAG_BREAKABLE) == 0) continue;
			BlockPos p = BlockPos.of(entry.getLongKey());
			level.setBlock(p, air, Block.UPDATE_CLIENTS);
			destroyed++;
		}

		double cx = center.getX() + 0.5D;
		double cy = center.getY() + 0.5D;
		double cz = center.getZ() + 0.5D;
		AABB aabb = new AABB(cx - radius, cy - radius, cz - radius, cx + radius, cy + radius, cz + radius);
		DamageSource src = level.damageSources().explosion(null, null);
		for (Entity e : level.getEntities((Entity) null, aabb)) {
			if (!e.isAlive()) continue;
			double distSq = e.distanceToSqr(cx, cy, cz);
			if (distSq > rsq) continue;
			double dist = Math.sqrt(distSq);
			float damage = (float) Math.max(1D, 200D * (1D - dist / radius));
			e.hurtServer(level, src, damage);
		}

		level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, cx, cy, cz, 1, 0D, 0D, 0D, 0D);
		level.playSound(null, cx, cy, cz, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4F, 0.7F);

		NuclearFallout.apply(level, center, radius);

		long elapsed = System.currentTimeMillis() - start;
		FTBIC.LOGGER.warn("Nuclear explosion by {}/{} at {} radius {} destroyed {} blocks (volume {}) in {}ms",
				ownerName, ownerId, center, radius, destroyed, volume, elapsed);
	}

	private static void traceCrustToCenter(Long2ByteOpenHashMap blocks, BlockPos.MutableBlockPos mpos, BlockPos center, BlockPos from, double step) {
		int x0 = center.getX() - from.getX();
		int y0 = center.getY() - from.getY();
		int z0 = center.getZ() - from.getZ();
		double dist = Math.sqrt((double) (x0 * x0) + (double) (y0 * y0) + (double) (z0 * z0));
		if (dist <= 0D) return;

		LongList path = new LongArrayList();
		int px = Integer.MIN_VALUE;
		int py = Integer.MIN_VALUE;
		int pz = Integer.MIN_VALUE;

		for (double l = 0D; l <= dist; l += step) {
			int x = Mth.floor(x0 * l / dist + 0.5D);
			int y = Mth.floor(y0 * l / dist + 0.5D);
			int z = Mth.floor(z0 * l / dist + 0.5D);
			if (px == x && py == y && pz == z) continue;
			px = x;
			py = y;
			pz = z;

			mpos.set(from.getX() + x, from.getY() + y, from.getZ() + z);
			long key = mpos.asLong();
			byte flag = blocks.get(key);
			if (flag == 0) continue;
			if ((flag & FLAG_REINFORCED) != 0) return;
			path.add(key);
		}

		for (int i = 0; i < path.size(); i++) {
			long key = path.getLong(i);
			byte flag = blocks.get(key);
			if ((flag & FLAG_DESTROY) == 0) {
				blocks.put(key, (byte) (flag | FLAG_DESTROY));
			}
		}
	}

	private static boolean canReachCenter(Long2ByteOpenHashMap blocks, BlockPos.MutableBlockPos mpos, BlockPos center, BlockPos from, double step) {
		int x0 = center.getX() - from.getX();
		int y0 = center.getY() - from.getY();
		int z0 = center.getZ() - from.getZ();
		double dist = Math.sqrt((double) (x0 * x0) + (double) (y0 * y0) + (double) (z0 * z0));
		if (dist <= 0D) return false;

		int px = Integer.MIN_VALUE;
		int py = Integer.MIN_VALUE;
		int pz = Integer.MIN_VALUE;

		for (double l = 0D; l <= dist; l += step) {
			int x = Mth.floor(x0 * l / dist + 0.5D);
			int y = Mth.floor(y0 * l / dist + 0.5D);
			int z = Mth.floor(z0 * l / dist + 0.5D);
			if (px == x && py == y && pz == z) continue;
			px = x;
			py = y;
			pz = z;

			mpos.set(from.getX() + x, from.getY() + y, from.getZ() + z);
			long key = mpos.asLong();
			byte flag = blocks.get(key);
			if (flag == 0) continue;
			if ((flag & FLAG_REINFORCED) != 0) return false;
		}
		return true;
	}
}
