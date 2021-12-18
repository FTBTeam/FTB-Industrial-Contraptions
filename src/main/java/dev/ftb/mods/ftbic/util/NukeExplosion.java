package dev.ftb.mods.ftbic.util;

import com.mojang.authlib.GameProfile;
import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;

public class NukeExplosion {
	public enum BlockType {
		NONE,
		REINFORCED,
		AIR_INSIDE,
		BLOCK_INSIDE,
		AIR_OUTSIDE,
		BLOCK_OUTSIDE;

		public boolean isReinforced() {
			return this == REINFORCED;
		}

		public boolean isInside() {
			return this == AIR_INSIDE || this == BLOCK_INSIDE;
		}

		public boolean isAir() {
			return this == AIR_INSIDE || this == AIR_OUTSIDE;
		}

		public boolean isBlock() {
			return this == BLOCK_INSIDE || this == BLOCK_OUTSIDE;
		}
	}

	private static class CrustFilter implements Predicate<BlockPos> {
		private final Map<BlockPos, BlockType> blocks;
		private final BlockPos.MutableBlockPos mpos;

		private CrustFilter(Map<BlockPos, BlockType> blocks, BlockPos.MutableBlockPos mpos) {
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

		public int distance(NukeThread thread) {
			int x = thread.pos.getX() - pos.getX();
			int y = thread.pos.getY() - pos.getY();
			int z = thread.pos.getZ() - pos.getZ();
			return x * x + y * y + z * z;
		}

		public int horizontalDistance(NukeThread thread) {
			int x = thread.pos.getX() - pos.getX();
			int z = thread.pos.getZ() - pos.getZ();
			return x * x + z * z;
		}

		public int getOrder() {
			return 0;
		}

		public int compare(NukeThread thread, NukeTask o) {
			return distance(thread) - o.distance(thread);
		}

		public double group(NukeThread thread, double group) {
			return Math.sqrt(distance(thread)) / group;
		}

		public void execute(NukeThread thread) {
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
		public void execute(NukeThread thread) {
			thread.level.setBlock(pos, state, ((flags & 1) != 0) ? flags : (flags | 0x80), neighborUpdates);
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
		public int compare(NukeThread thread, NukeTask o) {
			int i = horizontalDistance(thread) - o.horizontalDistance(thread);
			int y1 = pos.getY() - thread.pos.getY();
			int y2 = o.pos.getY() - thread.pos.getY();
			return i == 0 ? (y1 - y2) : i;
		}

		@Override
		public double group(NukeThread thread, double group) {
			return Math.sqrt(horizontalDistance(thread)) / group;
		}

		@Override
		public void execute(NukeThread thread) {
			if (state.useShapeForLightOcclusion() || old.useShapeForLightOcclusion() || state.getLightBlock(thread.level, pos) != oldOpacity || state.getLightValue(thread.level, pos) != oldLight) {
				thread.level.getProfiler().push("queueCheckLight");
				thread.level.getLightEngine().checkBlock(pos);
				thread.level.getProfiler().pop();
			}
		}
	}

	private static class NukeThread extends Thread implements Comparator<NukeTask> {
		private final ServerLevel level;
		private final BlockPos pos;
		private final double radius;
		private final List<NukeTask> tasks;

		public NukeThread(ServerLevel level, BlockPos pos, double radius, List<NukeTask> tasks) {
			super("NukeThread/dn=" + level.dimension().location().getNamespace() + "/dp=" + level.dimension().location().getPath() + "/p=" + pos.getX() + "," + pos.getY() + "," + pos.getZ() + "/r=" + radius + "/m=" + tasks.size());
			this.level = level;
			this.pos = pos;
			this.radius = radius;
			this.tasks = new ArrayList<>(tasks);
		}

		@Override
		public void run() {
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

					if (!level.getServer().isRunning()) {
						return;
					}

					level.getServer().submitAsync(() -> execute(list));

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
			level.getGameRules().getRule(GameRules.RULE_DOBLOCKDROPS).set(false, level.getServer());

			for (NukeTask task : list) {
				task.execute(this);
			}

			level.getGameRules().getRule(GameRules.RULE_DOBLOCKDROPS).set(blockDrops, level.getServer());
		}

		@Override
		public int compare(NukeTask o1, NukeTask o2) {
			int i = o1.getOrder() - o2.getOrder();
			return i == 0 ? o1.compare(this, o2) : i;
		}
	}

	public static void create(ServerLevel level, BlockPos pos, double radius, UUID owner, String ownerName) {
		long startTime = System.currentTimeMillis();
		radius = Math.min(radius, 500D);
		int rxz = Mth.ceil(radius);
		double ry = Math.min(radius * 0.5D, 60);
		int ry0 = Math.max(pos.getY() - Mth.ceil(ry * 2D), 0);
		int ry1 = Math.min(pos.getY() + Mth.ceil(ry * 2D), level.getHeight()) - 1;
		double rsq = radius * radius;
		double rsqc = (radius * 0.65D) * (radius * 0.65D);
		double volume = 4D * Math.PI * rxz * rxz * (ry1 - ry0) / 6D;

		FTBIC.LOGGER.warn(String.format("%s/%s created a nuclear explosion with radius %f at %s:%d,%d,%d with %d blocks to check", ownerName, owner, radius, level.dimension().location(), pos.getX(), pos.getY(), pos.getZ(), Mth.ceil(volume)));

		BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
		Map<BlockPos, BlockType> blocks = new LinkedHashMap<>();
		List<BlockPos> crust = new ArrayList<>();
		ServerPlayer player = new ServerPlayer(level.getServer(), level, new GameProfile(owner, ownerName), new ServerPlayerGameMode(level));

		for (int x = -rxz; x <= rxz; x++) {
			for (int z = -rxz; z <= rxz; z++) {
				mpos.set(pos.getX() + x, 0, pos.getZ() + z);

				boolean blockProtected = FTBChunksIntegration.instance.isProtected(level, mpos, owner) || !level.mayInteract(player, mpos);

				if (!Level.isInWorldBounds(mpos)) {
					continue;
				}

				for (int y = ry1; y >= ry0; y--) {
					int y0 = y - pos.getY();
					double y1 = y0 / (ry / radius);
					double dist = (x * x + y1 * y1 + z * z) / (1D - level.random.nextDouble() * 0.25D);

					if (dist <= rsq) {
						mpos.setY(y);

						if (Level.isOutsideBuildHeight(mpos)) {
							continue;
						}

						BlockState state = level.getBlockState(mpos);
						BlockPos ipos = mpos.immutable();

						if (blockProtected) {
							blocks.put(ipos, BlockType.REINFORCED);
						} else if (state.isAir()) {
							blocks.put(ipos, BlockType.AIR_INSIDE);
						} else if (FTBICUtils.REINFORCED.contains(state.getBlock()) || state.getDestroySpeed(level, mpos) < 0F) {
							blocks.put(ipos, BlockType.REINFORCED);
						} else {
							blocks.put(ipos, BlockType.BLOCK_INSIDE);
						}

						if (dist >= rsqc) {
							crust.add(ipos);
						}
					}
				}
			}
		}

		crust.removeIf(new CrustFilter(blocks, mpos));

		for (BlockPos p : crust) {
			BlockType t = blocks.getOrDefault(p, BlockType.NONE);

			if (t == BlockType.BLOCK_INSIDE) {
				blocks.put(p, BlockType.BLOCK_OUTSIDE);
			} else if (t == BlockType.AIR_INSIDE) {
				blocks.put(p, BlockType.AIR_OUTSIDE);
			}
		}

		BlockState air = Blocks.AIR.defaultBlockState();
		BlockState podzol = Blocks.PODZOL.defaultBlockState();
		BlockState coarseDirt = Blocks.COARSE_DIRT.defaultBlockState();
		BlockState fire = Blocks.FIRE.defaultBlockState();
		BlockState cobble = Blocks.COBBLESTONE.defaultBlockState();
		BlockState exfluid = FTBICBlocks.EXFLUID.get().defaultBlockState();

		List<NukeTask> tasks = new ArrayList<>();
		tasks.add(new BlockModification(pos, air, 3, 64));

		for (Map.Entry<BlockPos, BlockType> entry : blocks.entrySet()) {
			BlockPos p = entry.getKey();

			if (entry.getValue() == BlockType.BLOCK_INSIDE) {
				BlockState state = level.getBlockState(p);
				int oldLight = state.getLightValue(level, pos);
				int oldOpacity = state.getLightBlock(level, pos);

				tasks.add(new BlockModification(p, air, 2, 0));
				tasks.add(new LightUpdate(p, state, air, oldLight, oldOpacity));
			}
		}

		for (Map.Entry<BlockPos, BlockType> entry : blocks.entrySet()) {
			if (entry.getValue() == BlockType.BLOCK_OUTSIDE) {
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
				} else if (state.getMaterial() == Material.STONE) {
					if (level.random.nextInt(10) == 0) {
						BlockState burnt = getBurntBlock(level.random);
						tasks.add(new BlockModification(p, burnt));
						tasks.add(new LightUpdate(p, state, burnt, oldLight, oldOpacity));

						if (level.random.nextInt(8) == 0) {
							BlockPos above = p.above();

							if (blocks.getOrDefault(above, BlockType.NONE).isInside()) {
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
			} else if (entry.getValue() == BlockType.AIR_OUTSIDE) {
				BlockPos p = entry.getKey();
				BlockState state = level.getBlockState(p);
				int oldLight = state.getLightValue(level, pos);
				int oldOpacity = state.getLightBlock(level, pos);

				if (level.random.nextInt(60) == 0) {
					tasks.add(new BlockModification(p, exfluid));
					tasks.add(new LightUpdate(p, state, exfluid, oldLight, oldOpacity));
				} else {
					tasks.add(new LightUpdate(p, state, state, oldLight, oldOpacity));
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

		FTBIC.LOGGER.warn(String.format("It modified %d blocks and it took %d ms to calculate", modifiedBlocks, endTime - startTime));
		NukeThread thread = new NukeThread(level, pos, radius, tasks);

		if (FTBICConfig.NUKE_DAEMON_THREAD) {
			thread.setDaemon(true);
		}

		thread.start();
	}

	private static BlockState getBurntBlock(Random random) {
		switch (random.nextInt(3)) {
			case 0:
				return Blocks.MAGMA_BLOCK.defaultBlockState();
			case 1:
				return Blocks.BASALT.defaultBlockState();
			default:
				return Blocks.BLACKSTONE.defaultBlockState();
		}
	}
}
