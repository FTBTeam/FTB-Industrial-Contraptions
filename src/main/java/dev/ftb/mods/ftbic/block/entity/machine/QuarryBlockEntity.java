package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.net.MoveLaserMessage;
import dev.ftb.mods.ftbic.screen.QuarryMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class QuarryBlockEntity extends BasicMachineBlockEntity {
	private static final int INVALID_Y = -10000;
	private static final Predicate<ItemEntity> ITEM_ENTITY_PREDICATE = entity -> true;

	public boolean paused = false;
	public long quarryTick = 0L;
	public float laserX = 0.5F;
	public int laserY = INVALID_Y;
	public float laserZ = 0.5F;
	public int offsetX = 1;
	public int offsetZ = -2;
	public int sizeX = 5;
	public int sizeZ = 5;
	public int skippedBlocks = 0;

	// Client side stuff
	public float prevLaserX = 0.5F;
	public float prevLaserZ = 0.5F;
	public float moveLaserX = 0.5F;
	public int moveLaserY = 0;
	public float moveLaserZ = 0.5F;

	public QuarryBlockEntity() {
		super(FTBICElectricBlocks.QUARRY);
	}

	@Override
	public void writeData(CompoundTag tag) {
		super.writeData(tag);
		tag.putFloat("LaserX", laserX);
		tag.putInt("LaserY", laserY);
		tag.putFloat("LaserZ", laserZ);

		if (paused) {
			tag.putBoolean("Paused", true);
		}

		tag.putLong("QuarryTick", quarryTick);
		tag.putByte("OffsetX", (byte) offsetX);
		tag.putByte("OffsetZ", (byte) offsetZ);
		tag.putByte("SizeX", (byte) sizeX);
		tag.putByte("SizeZ", (byte) sizeZ);

		if (skippedBlocks > 0) {
			tag.putShort("SkippedBlocks", (short) skippedBlocks);
		}
	}

	@Override
	public void readData(CompoundTag tag) {
		super.readData(tag);
		laserX = tag.getFloat("LaserX");
		laserY = tag.getInt("LaserY");
		laserZ = tag.getFloat("LaserZ");
		paused = tag.getBoolean("Paused");
		quarryTick = tag.getLong("QuarryTick");
		offsetX = tag.getByte("OffsetX");
		offsetZ = tag.getByte("OffsetZ");
		sizeX = Mth.clamp(tag.getByte("SizeX"), 1, 64);
		sizeZ = Mth.clamp(tag.getByte("SizeZ"), 1, 64);
		skippedBlocks = tag.getShort("SkippedBlocks");
	}

	@Override
	public void writeNetData(CompoundTag tag) {
		super.writeNetData(tag);
		tag.putFloat("LaserX", laserX);
		tag.putInt("LaserY", laserY);
		tag.putFloat("LaserZ", laserZ);

		if (paused) {
			tag.putBoolean("Paused", true);
		}

		tag.putLong("QuarryTick", quarryTick);
		tag.putByte("OffsetX", (byte) offsetX);
		tag.putByte("OffsetZ", (byte) offsetZ);
		tag.putByte("SizeX", (byte) sizeX);
		tag.putByte("SizeZ", (byte) sizeZ);
		tag.putDouble("Speed", progressSpeed);
	}

	@Override
	public void readNetData(CompoundTag tag) {
		super.readNetData(tag);
		prevLaserX = moveLaserX = laserX = tag.getFloat("LaserX");
		moveLaserY = laserY = tag.getInt("LaserY");
		prevLaserZ = moveLaserZ = laserZ = tag.getFloat("LaserZ");
		paused = tag.getBoolean("Paused");
		quarryTick = tag.getLong("QuarryTick");
		offsetX = tag.getByte("OffsetX");
		offsetZ = tag.getByte("OffsetZ");
		sizeX = tag.getByte("SizeX");
		sizeZ = tag.getByte("SizeZ");
	}

	@Override
	public void handleProcessing() {
		active = !paused;

		if (level != null && level.isClientSide()) {
			prevLaserX = laserX;
			prevLaserZ = laserZ;

			laserX = moveLaserX;
			laserY = moveLaserY;
			laserZ = moveLaserZ;

			if (!paused) {
				double x = worldPosition.getX() + laserX;
				double minY = laserY + 0.5D;
				double maxY = worldPosition.getY() + 0.5D;
				double z = worldPosition.getZ() + laserZ;
				FTBIC.PROXY.playLaserSound(level.getGameTime(), x, minY, maxY, z);
			}
		}

		if (!paused && level != null && !level.isClientSide()) {
			if (energy >= energyUse) {
				energy -= energyUse;
			} else {
				return;
			}

			int moveTicks = Math.max((int) (FTBICConfig.QUARRY_MOVE_TICKS / progressSpeed), 1);
			int miningTicks = Math.max((int) (FTBICConfig.QUARRY_MINE_TICKS / progressSpeed), 1);
			int totalTicks = miningTicks + moveTicks;
			int ltick = (int) (quarryTick % (long) totalTicks);

			if (ltick <= moveTicks) {
				long s = (long) sizeX * (long) sizeZ * 2L;
				int lpos0 = (int) (((quarryTick / (long) totalTicks) - 1L) % s);
				int lpos1 = (int) ((quarryTick / (long) totalTicks) % s);

				if (lpos0 < 0) {
					lpos0 += s;
				}

				if (lpos0 >= s / 2L) {
					lpos0 = (int) (s - lpos0 - 1);
				}

				if (lpos1 >= s / 2L) {
					lpos1 = (int) (s - lpos1 - 1);
				}

				int row0 = lpos0 / sizeX;
				int col0 = row0 % 2 == 0 ? (lpos0 % sizeX) : (sizeX - 1 - (lpos0 % sizeX));
				int row1 = lpos1 / sizeX;
				int col1 = row1 % 2 == 0 ? (lpos1 % sizeX) : (sizeX - 1 - (lpos1 % sizeX));
				float lerp = ltick / (float) moveTicks;

				laserX = (offsetX + Mth.lerp(lerp, col0, col1)) + 0.5F;
				laserZ = (offsetZ + Mth.lerp(lerp, row0, row1)) + 0.5F;
				laserY = getY(worldPosition.getX() + Mth.floor(laserX), worldPosition.getZ() + Mth.floor(laserZ));
				sendLaserMove();
			}

			if (ltick == totalTicks - 1) {
				if (laserY == INVALID_Y) {
					skippedBlocks++;

					if (skippedBlocks >= sizeX * sizeZ * 2) {
						paused = true;
						skippedBlocks = 0;
						syncBlock();
					} else {
						quarryTick += miningTicks - 1;
					}
				} else {
					BlockPos miningPos = new BlockPos(worldPosition.getX() + laserX, laserY, worldPosition.getZ() + laserZ);
					BlockState state = level.getBlockState(miningPos);
					skippedBlocks = 0;

					if (!level.isClientSide()) {
						level.getProfiler().push("ftbic_quarry");
						double lx = worldPosition.getX() + laserX;
						double ly = laserY + 0.5D;
						double lz = worldPosition.getZ() + laserZ;

						BlockEntity minedEntity = state.hasTileEntity() ? level.getBlockEntity(miningPos) : null;
						LootContext.Builder lootContext = new LootContext.Builder((ServerLevel) level)
								.withRandom(level.random)
								.withParameter(LootContextParams.ORIGIN, new Vec3(lx, ly, lz))
								.withParameter(LootContextParams.TOOL, new ItemStack(Items.NETHERITE_PICKAXE))
								.withParameter(LootContextParams.BLOCK_STATE, state)
								.withOptionalParameter(LootContextParams.BLOCK_ENTITY, minedEntity);

						List<ItemStack> list = new ArrayList<>(state.getDrops(lootContext));

						level.removeBlock(miningPos, false);
						level.levelEvent(null, 2001, miningPos, Block.getId(state));

						AABB aabb = new AABB(lx - 0.7D, ly - 0.7D, lz - 0.7D, lx + 0.7D, ly + 2.7D, lz + 0.7D);
						List<ItemEntity> itemEntities = level.getEntitiesOfClass(ItemEntity.class, aabb, ITEM_ENTITY_PREDICATE);

						for (ItemEntity itemEntity : itemEntities) {
							list.add(itemEntity.getItem());
							itemEntity.kill();
						}

						if (!list.isEmpty()) {
							ejectOutputItems();

							for (ItemStack stack : list) {
								ItemStack stack1 = addOutput(stack);

								if (!stack1.isEmpty()) {
									Block.popResource(level, worldPosition.relative(getFacing(Direction.NORTH)), stack1);
									paused = true;
								}
							}

							ejectOutputItems();
						}

						level.getProfiler().pop();

						if (paused) {
							syncBlock();
						}
					}
				}
			}

			quarryTick++;
		}
	}

	private boolean isValidBlock(BlockState state, BlockPos pos) {
		return state.getBlock() != Blocks.BEDROCK && !state.isAir() && state.getMaterial().blocksMotion() && state.getDestroySpeed(level, pos) >= 0F;
	}

	private int getY(int x, int z) {
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, 0, z);

		for (int y = worldPosition.getY(); y >= 0; y--) {
			pos.setY(y);

			if (level.isLoaded(pos)) {
				BlockState state = level.getBlockState(pos);

				if (isValidBlock(state, pos)) {
					return y;
				}
			}
		}

		return INVALID_Y;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public AABB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public double getViewDistance() {
		return 256D;
	}

	@Override
	public boolean savePlacer() {
		return true;
	}

	@Override
	public boolean tickClientSide() {
		return true;
	}

	@Override
	public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide()) {
			if (player.isCrouching()) {
				paused = !paused;
				syncBlock();
			} else {
				openMenu((ServerPlayer) player, (id, inventory) -> new QuarryMenu(id, inventory, this, this));
			}
		}

		return InteractionResult.SUCCESS;
	}

	public void resize() {
		Block landmark = FTBICBlocks.LANDMARK.get();
		Direction front = getFacing(Direction.NORTH);
		Direction back = front.getOpposite();
		Direction left = front.getClockWise();
		Direction right = front.getCounterClockWise();

		int offBack = 2;
		int offLeft = 1;
		int offRight = 1;

		for (int i = 2; i <= 64; i++) {
			BlockState state = level.getBlockState(worldPosition.relative(back, i));

			if (state.getBlock() == landmark) {
				offBack = i;
				break;
			}
		}

		for (int i = 1; i <= 64; i++) {
			BlockState state = level.getBlockState(worldPosition.relative(left, i));

			if (state.getBlock() == landmark) {
				offLeft = i;
				break;
			}
		}

		for (int i = 1; i <= 64; i++) {
			BlockState state = level.getBlockState(worldPosition.relative(right, i));

			if (state.getBlock() == landmark) {
				offRight = i;
				break;
			}
		}

		offBack -= 1;
		offLeft -= 1;
		offRight -= 1;

		if (back.getAxis() == Direction.Axis.X) {
			sizeX = offBack;
			sizeZ = (offLeft + offRight + 1);
			offsetX = back.getStepX() == 1 ? 1 : -sizeX;
			offsetZ = -(back.getStepX() == 1 ? offLeft : offRight);
		} else if (back.getAxis() == Direction.Axis.Z) {
			sizeX = (offLeft + offRight + 1);
			sizeZ = offBack;
			offsetX = -(back.getStepZ() == 1 ? offRight : offLeft);
			offsetZ = back.getStepZ() == 1 ? 1 : -sizeZ;
		}

		syncBlock();
	}

	@Override
	public void onPlacedBy(@Nullable LivingEntity entity, ItemStack stack) {
		super.onPlacedBy(entity, stack);
		Direction dir = getFacing(Direction.NORTH).getOpposite();
		laserX = dir.getStepX() + 0.5F;
		laserZ = dir.getStepZ() + 0.5F;

		BlockEntity e = level.getBlockEntity(worldPosition.below());

		if (e instanceof QuarryBlockEntity) {
			QuarryBlockEntity q = (QuarryBlockEntity) e;
			offsetX = q.offsetX;
			offsetZ = q.offsetZ;
			sizeX = q.sizeX;
			sizeZ = q.sizeZ;
			syncBlock();
		} else {
			resize();
		}
	}

	@Override
	public int getCount() {
		return 2;
	}

	@Override
	public int get(int id) {
		if (id == 1) {
			return paused ? 1 : 0;
		} else {
			return super.get(id);
		}
	}

	public void moveLaser(float x, int y, float z) {
		moveLaserX = x;
		moveLaserY = y;
		moveLaserZ = z;
		// FTBIC.LOGGER.info(String.format("Moved laser %d, %d, %d to %f, %f, %f", worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), x, y, z));
	}

	private void sendLaserMove() {
		LevelChunk chunk = level == null ? null : level.getChunkAt(worldPosition);

		if (chunk != null) {
			new MoveLaserMessage(worldPosition, laserX, laserY, laserZ).sendToChunkListeners(chunk);
		}
	}
}