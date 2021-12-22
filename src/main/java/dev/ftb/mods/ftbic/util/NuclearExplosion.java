package dev.ftb.mods.ftbic.util;

import com.mojang.authlib.GameProfile;
import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.FakePlayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;

public class NuclearExplosion extends Thread implements Comparator<NuclearExplosion.NukeTask> {
	public static final int FLAG_IN_EXPLOSION = 1 << 0;
	public static final int FLAG_IS_REINFORCED = 1 << 1;
	public static final int FLAG_DESTROY = 1 << 2;
	public static final int FLAG_IS_AIR = 1 << 3;
	public static final int FLAG_IS_BLOCK = 1 << 4;
	public static final int FLAG_INSIDE = 1 << 5;
	public static final int FLAG_OUTSIDE = 1 << 6;

	public static final Runnable NO_ACTION = () -> {
	};

	public static boolean hasFlag(int flags, int flag) {
		return (flags & flag) != 0;
	}

	public static boolean hasFlag(int flags, int flag1, int flag2) {
		return hasFlag(flags, flag1) && hasFlag(flags, flag2);
	}

	public static boolean hasFlag(int flags, int flag1, int flag2, int flag3) {
		return hasFlag(flags, flag1) && hasFlag(flags, flag2) && hasFlag(flags, flag3);
	}

	public static int removeFlag(int flags, int flag) {
		return flags & ~flag;
	}

	public static Builder builder(ServerLevel level, BlockPos pos, double radius, UUID owner, String ownerName) {
		return new Builder(level, pos, radius, owner, ownerName);
	}

	private static class CrustFilter implements Predicate<BlockPos> {
		private final Object2IntOpenHashMap<BlockPos> blocks;
		private final BlockPos.MutableBlockPos mpos;

		private CrustFilter(Object2IntOpenHashMap<BlockPos> blocks, BlockPos.MutableBlockPos mpos) {
			this.blocks = blocks;
			this.mpos = mpos;
		}

		@Override
		public boolean test(BlockPos pos) {
			for (Direction dir : FTBICUtils.DIRECTIONS) {
				mpos.set(pos.getX() + dir.getStepX(), pos.getY() + dir.getStepY(), pos.getZ() + dir.getStepZ());

				if (!blocks.containsKey(mpos)) {
					return false;
				}
			}

			return true;
		}
	}

	public static class NukeTask {
		public final BlockPos pos;

		public NukeTask(BlockPos pos) {
			this.pos = pos;
		}

		public int distance(NuclearExplosion explosion) {
			int x = explosion.pos.getX() - pos.getX();
			int y = explosion.pos.getY() - pos.getY();
			int z = explosion.pos.getZ() - pos.getZ();
			return x * x + y * y + z * z;
		}

		public int horizontalDistance(NuclearExplosion explosion) {
			int x = explosion.pos.getX() - pos.getX();
			int z = explosion.pos.getZ() - pos.getZ();
			return x * x + z * z;
		}

		public int getOrder() {
			return 0;
		}

		public int compare(NuclearExplosion explosion, NukeTask o) {
			return distance(explosion) - o.distance(explosion);
		}

		public double group(NuclearExplosion explosion, double group) {
			return Math.sqrt(distance(explosion)) / group;
		}

		public void execute(NuclearExplosion explosion) {
		}
	}

	public static class BlockModification extends NukeTask {
		public final BlockState state;
		public final int flags;
		public final int neighborUpdates;

		public BlockModification(BlockPos pos, BlockState state, int flags, int neighborUpdates) {
			super(pos);
			this.state = state;
			this.flags = flags;
			this.neighborUpdates = neighborUpdates;
		}

		public BlockModification(BlockPos pos, BlockState state) {
			this(pos, state, 2, 64);
		}

		@Override
		public int getOrder() {
			return state.getBlock() instanceof FireBlock ? 2 : (flags != 2 || neighborUpdates != 0) ? 0 : 1;
		}

		@Override
		public void execute(NuclearExplosion explosion) {
			explosion.level.setBlock(pos, state, ((flags & 1) != 0) ? flags : (flags | 0x80), neighborUpdates);
		}
	}

	public static class LightUpdate extends NukeTask {
		private final BlockState old;
		private final BlockState state;
		private final int oldLight;
		private final int oldOpacity;

		public LightUpdate(BlockPos pos, BlockState old, BlockState state, int oldLight, int oldOpacity) {
			super(pos);
			this.old = old;
			this.state = state;
			this.oldLight = oldLight;
			this.oldOpacity = oldOpacity;
		}

		@Override
		public int getOrder() {
			return 10;
		}

		@Override
		public int compare(NuclearExplosion explosion, NukeTask o) {
			int i = horizontalDistance(explosion) - o.horizontalDistance(explosion);
			int y1 = pos.getY() - explosion.pos.getY();
			int y2 = o.pos.getY() - explosion.pos.getY();
			return i == 0 ? (y1 - y2) : i;
		}

		@Override
		public double group(NuclearExplosion explosion, double group) {
			return Math.sqrt(horizontalDistance(explosion)) / group;
		}

		@Override
		public void execute(NuclearExplosion explosion) {
			if (state.useShapeForLightOcclusion() || old.useShapeForLightOcclusion() || state.getLightBlock(explosion.level, pos) != oldOpacity || state.getLightValue(explosion.level, pos) != oldLight) {
				explosion.level.getProfiler().push("queueCheckLight");
				explosion.level.getLightEngine().checkBlock(pos);
				explosion.level.getProfiler().pop();
			}
		}
	}

	public static final class Builder {
		private final ServerLevel level;
		private final BlockPos pos;
		private final double radius;
		private final UUID owner;
		private final String ownerName;
		private long delay;
		private Runnable preExplosion;

		private Builder(ServerLevel level, BlockPos pos, double radius, UUID owner, String ownerName) {
			this.level = level;
			this.pos = pos;
			this.radius = Math.min(radius, 100D);
			this.owner = owner;
			this.ownerName = ownerName;
			this.delay = 0L;
			this.preExplosion = NO_ACTION;
		}

		public Builder delay(long delay) {
			this.delay = delay;
			return this;
		}

		public Builder preExplosion(Runnable preExplosion) {
			this.preExplosion = preExplosion;
			return this;
		}

		public void create() {
			NuclearExplosion explosion = new NuclearExplosion(level, pos, radius, owner, ownerName, delay, preExplosion);

			if (FTBICConfig.NUCLEAR_EXPLOSION_DAEMON_THREAD) {
				explosion.setDaemon(true);
			}

			explosion.start();
		}
	}

	public final MinecraftServer server;
	public final ServerLevel level;
	public final BlockPos pos;
	public final double radius;
	public final List<NukeTask> tasks;
	public final UUID owner;
	public final String ownerName;
	public final long delay;
	public final Runnable preExplosion;

	private NuclearExplosion(ServerLevel level, BlockPos pos, double radius, UUID owner, String ownerName, long delay, Runnable preExplosion) {
		super("NuclearExplosion/dn=" + level.dimension().location().getNamespace() + "/dp=" + level.dimension().location().getPath() + "/p=" + pos.getX() + "," + pos.getY() + "," + pos.getZ() + "/r=" + radius + "/o=" + owner + "/on=" + ownerName);
		this.server = level.getServer();
		this.level = level;
		this.pos = pos;
		this.radius = Math.min(radius, 100D);
		this.tasks = new ArrayList<>();
		this.owner = owner;
		this.ownerName = ownerName;
		this.delay = delay;
		this.preExplosion = preExplosion;
	}

	@Override
	public void run() {
		long startTime = System.currentTimeMillis();

		int rxz = Mth.ceil(radius);
		double ry = Math.min(radius * 0.75D, 60);
		double rys = ry / radius;
		int ry0 = Math.max(pos.getY() - Mth.ceil(ry * 2D), 0);
		int ry1 = Math.min(pos.getY() + Mth.ceil(ry * 2D), level.getHeight()) - 1;
		double rsq = radius * radius;
		double rsqc = (radius * 0.65D) * (radius * 0.65D);
		long seed = System.currentTimeMillis() + pos.hashCode();
		Random random0 = new Random(seed);
		Random random = new Random(seed);

		int volume = 0;

		for (int x = -rxz; x <= rxz; x++) {
			for (int z = -rxz; z <= rxz; z++) {
				for (int y = ry1; y >= ry0; y--) {
					int y0 = y - pos.getY();
					double y1 = y0 / rys;
					double dist = (x * x + y1 * y1 + z * z) / (1D - random0.nextDouble() * 0.25D);

					if (dist <= rsq) {
						volume++;
					}
				}
			}
		}

		FTBIC.LOGGER.warn(String.format("%s/%s created a nuclear explosion with radius %f at %s:%d,%d,%d with up to %d blocks to check", ownerName, owner, radius, level.dimension().location(), pos.getX(), pos.getY(), pos.getZ(), volume));

		BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
		Object2IntOpenHashMap<BlockPos> blocks = new Object2IntOpenHashMap<>(volume);
		blocks.defaultReturnValue(0);
		List<BlockPos> crust = new ArrayList<>();
		ServerPlayer player = new FakePlayer(level, new GameProfile(owner, ownerName));
		player.setPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);

		for (int x = -rxz; x <= rxz; x++) {
			for (int z = -rxz; z <= rxz; z++) {
				mpos.set(pos.getX() + x, 0, pos.getZ() + z);

				boolean blockProtected = FTBChunksIntegration.instance.isProtected(level, mpos, owner) || !level.mayInteract(player, mpos);

				if (!Level.isInWorldBounds(mpos)) {
					continue;
				}

				for (int y = ry1; y >= ry0; y--) {
					int y0 = y - pos.getY();
					double y1 = y0 / rys;
					double dist = (x * x + y1 * y1 + z * z) / (1D - random.nextDouble() * 0.25D);

					if (dist <= rsq) {
						mpos.setY(y);

						if (Level.isOutsideBuildHeight(mpos)) {
							continue;
						}

						BlockPos ipos = mpos.immutable();

						try {
							BlockState state = level.getBlockState(mpos);

							if (blockProtected) {
								blocks.put(ipos, FLAG_IN_EXPLOSION | FLAG_IS_REINFORCED | FLAG_INSIDE);
							} else if (state.isAir()) {
								blocks.put(ipos, FLAG_IN_EXPLOSION | FLAG_IS_AIR | FLAG_INSIDE);
							} else if (FTBICUtils.REINFORCED.contains(state.getBlock()) || state.getDestroySpeed(level, mpos) < 0F) {
								blocks.put(ipos, FLAG_IN_EXPLOSION | FLAG_IS_REINFORCED | FLAG_INSIDE);
							} else {
								blocks.put(ipos, FLAG_IN_EXPLOSION | FLAG_IS_BLOCK | FLAG_INSIDE);
							}
						} catch (Exception ex) {
							FTBIC.LOGGER.warn("Error while calculating nuclear explosion", ex);
							blocks.put(ipos, FLAG_IN_EXPLOSION | FLAG_IS_REINFORCED | FLAG_INSIDE);
						}

						if (dist >= rsqc) {
							crust.add(ipos);
						}
					}
				}
			}
		}

		crust.removeIf(new CrustFilter(blocks, mpos));

		List<NukeTask> tasks = new ArrayList<>();
		BlockState air = Blocks.AIR.defaultBlockState();
		BlockState podzol = Blocks.PODZOL.defaultBlockState();
		BlockState coarseDirt = Blocks.COARSE_DIRT.defaultBlockState();
		BlockState fire = Blocks.FIRE.defaultBlockState();
		BlockState cobble = Blocks.COBBLESTONE.defaultBlockState();
		BlockState exfluid = FTBICBlocks.EXFLUID.get().defaultBlockState();

		if (!hasFlag(blocks.getOrDefault(pos, 0), FLAG_IS_REINFORCED)) {
			tasks.add(new BlockModification(pos, air, 3, rxz));
		}

		double step = 0.5D;

		for (BlockPos p : crust) {
			int flags = blocks.getOrDefault(p, 0);

			if (hasFlag(flags, FLAG_IN_EXPLOSION)) {
				blocks.put(p, removeFlag(flags | FLAG_OUTSIDE, FLAG_INSIDE));
			}

			int x0 = pos.getX() - p.getX();
			int y0 = pos.getY() - p.getY();
			int z0 = pos.getZ() - p.getZ();
			int distSq = x0 * x0 + y0 * y0 + z0 * z0;
			double dist = Math.sqrt(distSq);

			int px = 0;
			int py = 0;
			int pz = 0;

			for (double l = 0D; l <= dist; l += step) {
				int x = Mth.floor(x0 * l / dist + 0.5D);
				int y = Mth.floor(y0 * l / dist + 0.5D);
				int z = Mth.floor(z0 * l / dist + 0.5D);

				if (px == x && py == y && pz == z) {
					continue;
				}

				px = x;
				py = y;
				pz = z;

				mpos.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
				int flags1 = blocks.getOrDefault(mpos, 0);

				if (hasFlag(flags1, FLAG_IS_REINFORCED)) {
					break;
				} else if (hasFlag(flags1, FLAG_IN_EXPLOSION) && !hasFlag(flags1, FLAG_DESTROY)) {
					BlockPos p1 = mpos.immutable();
					blocks.put(p1, flags1 | FLAG_DESTROY);
				}
			}
		}

		List<Object2IntMap.Entry<BlockPos>> destroyEntries = new ArrayList<>();

		for (Object2IntMap.Entry<BlockPos> entry : blocks.object2IntEntrySet()) {
			int flags = entry.getIntValue();

			if (!hasFlag(flags, FLAG_IS_REINFORCED) && !hasFlag(flags, FLAG_DESTROY)) {
				BlockPos p = entry.getKey();
				int x0 = pos.getX() - p.getX();
				int y0 = pos.getY() - p.getY();
				int z0 = pos.getZ() - p.getZ();
				int distSq = x0 * x0 + y0 * y0 + z0 * z0;
				double dist = Math.sqrt(distSq);

				int px = 0;
				int py = 0;
				int pz = 0;

				for (double l = 0D; l <= dist; l += step) {
					int x = Mth.floor(x0 * l / dist + 0.5D);
					int y = Mth.floor(y0 * l / dist + 0.5D);
					int z = Mth.floor(z0 * l / dist + 0.5D);

					if (px == x && py == y && pz == z) {
						continue;
					}

					px = x;
					py = y;
					pz = z;

					mpos.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
					int flags1 = blocks.getOrDefault(mpos, 0);

					if (hasFlag(flags1, FLAG_IS_REINFORCED)) {
						break;
					} else if (hasFlag(flags1, FLAG_IN_EXPLOSION) && !hasFlag(flags1, FLAG_DESTROY)) {
						destroyEntries.add(entry);
					}
				}
			}
		}

		for (Object2IntMap.Entry<BlockPos> entry : destroyEntries) {
			entry.setValue(entry.getIntValue() | FLAG_DESTROY);
		}

		for (Object2IntMap.Entry<BlockPos> entry : blocks.object2IntEntrySet()) {
			int flags = entry.getIntValue();

			if (hasFlag(flags, FLAG_DESTROY, FLAG_INSIDE, FLAG_IS_BLOCK)) {
				try {
					BlockPos p = entry.getKey();
					BlockState state = level.getBlockState(p);
					int oldLight = state.getLightValue(level, pos);
					int oldOpacity = state.getLightBlock(level, pos);

					tasks.add(new BlockModification(p, air, 2, 0));
					tasks.add(new LightUpdate(p, state, air, oldLight, oldOpacity));
				} catch (Exception ex) {
					FTBIC.LOGGER.warn("Error while calculating nuclear explosion", ex);
				}
			}
		}

		for (Object2IntMap.Entry<BlockPos> entry : blocks.object2IntEntrySet()) {
			int flags = entry.getIntValue();

			if (hasFlag(flags, FLAG_DESTROY, FLAG_IS_BLOCK, FLAG_OUTSIDE)) {
				try {
					BlockPos p = entry.getKey();
					BlockState state = level.getBlockState(p);
					int oldLight = state.getLightValue(level, pos);
					int oldOpacity = state.getLightBlock(level, pos);

					if (state.getBlock() instanceof GrassBlock) {
						tasks.add(new BlockModification(p, podzol));
						tasks.add(new LightUpdate(p, state, podzol, oldLight, oldOpacity));
					} else if (Tags.Blocks.DIRT.contains(state.getBlock())) {
						tasks.add(new BlockModification(p, coarseDirt));
						tasks.add(new LightUpdate(p, state, coarseDirt, oldLight, oldOpacity));
					} else if (state.getMaterial() == Material.STONE || Tags.Blocks.GRAVEL.contains(state.getBlock()) || Tags.Blocks.SAND.contains(state.getBlock())) {
						if (random.nextInt(10) == 0) {
							BlockState burnt = getBurntBlock(random);
							tasks.add(new BlockModification(p, burnt));
							tasks.add(new LightUpdate(p, state, burnt, oldLight, oldOpacity));

							if (random.nextInt(8) == 0) {
								BlockPos above = p.above();
								int aboveFlags = blocks.getOrDefault(above, 0);

								if (hasFlag(aboveFlags, FLAG_INSIDE) && !hasFlag(aboveFlags, FLAG_IS_REINFORCED)) {
									tasks.add(new BlockModification(above, fire, 3, 64));
								}
							}
						} else {
							tasks.add(new BlockModification(p, cobble));
							tasks.add(new LightUpdate(p, state, cobble, oldLight, oldOpacity));
						}
					} else if (state.getMaterial().isLiquid() || level.getFluidState(p).getType() != Fluids.EMPTY) {
						tasks.add(new BlockModification(p, exfluid));
						tasks.add(new LightUpdate(p, state, exfluid, oldLight, oldOpacity));
					} else {
						tasks.add(new LightUpdate(p, state, state, oldLight, oldOpacity));
					}
				} catch (Exception ex) {
					FTBIC.LOGGER.warn("Error while calculating nuclear explosion", ex);
				}
			} else if (hasFlag(flags, FLAG_DESTROY, FLAG_IS_AIR, FLAG_OUTSIDE)) {
				try {
					BlockPos p = entry.getKey();
					BlockState state = level.getBlockState(p);
					int oldLight = state.getLightValue(level, pos);
					int oldOpacity = state.getLightBlock(level, pos);

					if (random.nextInt(60) == 0) {
						tasks.add(new BlockModification(p, exfluid));
						tasks.add(new LightUpdate(p, state, exfluid, oldLight, oldOpacity));
					} else {
						tasks.add(new LightUpdate(p, state, state, oldLight, oldOpacity));
					}
				} catch (Exception ex) {
					FTBIC.LOGGER.warn("Error while calculating nuclear explosion", ex);
				}
			}
		}

		int modifiedBlocks = 0;

		for (NukeTask task : tasks) {
			if (task instanceof BlockModification) {
				modifiedBlocks++;
			}
		}

		long endTime = System.currentTimeMillis();

		FTBIC.LOGGER.warn(String.format("It modified %d/%d blocks and it took %d ms to calculate", modifiedBlocks, blocks.size(), endTime - startTime));

		long delay1 = delay - (System.currentTimeMillis() - startTime);

		if (delay1 > 0L) {
			try {
				Thread.sleep(delay1);
			} catch (Exception ex) {
				FTBIC.LOGGER.warn("Error while calculating nuclear explosion", ex);
			}
		}

		server.submitAsync(preExplosion);

		while (!tasks.isEmpty()) {
			tasks.sort(this);
			int highestOrder = tasks.get(0).getOrder();
			List<NukeTask> lowPriority = new ArrayList<>();

			List<List<NukeTask>> lists = new ArrayList<>();

			double group = 1.5;

			for (int i = Mth.ceil(radius / group); i >= 0; i--) {
				lists.add(new ArrayList<>());
			}

			for (NukeTask task : tasks) {
				if (task.getOrder() > highestOrder) {
					lowPriority.add(task);
				} else {
					lists.get(Mth.clamp(Mth.floor(task.group(this, group)), 0, lists.size() - 1)).add(task);
				}
			}

			tasks.clear();
			tasks.addAll(lowPriority);
			lists.removeIf(List::isEmpty);

			for (int i = 0, listsSize = lists.size(); i < listsSize; i++) {
				List<NukeTask> list = lists.get(i);

				if (!server.isRunning()) {
					return;
				}

				server.submitAsync(() -> execute(list));

				if (i != listsSize - 1) {
					try {
						Thread.sleep(Mth.clamp(lists.get(i + 1).size() / 20L, 50L, 150L));
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}

	private void execute(List<NukeTask> list) {
		boolean blockDrops = level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS);
		level.getGameRules().getRule(GameRules.RULE_DOBLOCKDROPS).set(false, server);

		for (NukeTask task : list) {
			task.execute(this);
		}

		level.getGameRules().getRule(GameRules.RULE_DOBLOCKDROPS).set(blockDrops, server);
	}

	private BlockState getBurntBlock(Random random) {
		switch (random.nextInt(3)) {
			case 0:
				return Blocks.MAGMA_BLOCK.defaultBlockState();
			case 1:
				return Blocks.BASALT.defaultBlockState();
			default:
				return Blocks.BLACKSTONE.defaultBlockState();
		}
	}

	@Override
	public int compare(NukeTask o1, NukeTask o2) {
		int i = o1.getOrder() - o2.getOrder();
		return i == 0 ? o1.compare(this, o2) : i;
	}
}
