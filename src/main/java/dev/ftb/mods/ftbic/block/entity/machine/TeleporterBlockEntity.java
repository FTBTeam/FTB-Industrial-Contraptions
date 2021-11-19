package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.item.FTBICItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;

public class TeleporterBlockEntity extends ElectricBlockEntity {
	public BlockPos linkedPos;
	public ResourceKey<Level> linkedDimension;
	public byte[] preview;
	public int teleportWarmup;
	public int teleportCooldown;

	public TeleporterBlockEntity() {
		super(FTBICElectricBlocks.TELEPORTER);
		linkedPos = null;
		linkedDimension = null;
		preview = null;
		teleportWarmup = 0;
		teleportCooldown = 0;
	}

	@Override
	public void writeData(CompoundTag tag) {
		super.writeData(tag);

		if (linkedPos != null && linkedDimension != null) {
			tag.putInt("LinkedX", linkedPos.getX());
			tag.putInt("LinkedY", linkedPos.getY());
			tag.putInt("LinkedZ", linkedPos.getZ());
			tag.putString("LinkedDimension", linkedDimension.location().toString());
		}

		if (preview != null) {
			tag.putByteArray("Preview", preview);
		}

		if (teleportWarmup > 0) {
			tag.putInt("TeleportWarmup", teleportWarmup);
		}

		if (teleportCooldown > 0) {
			tag.putInt("TeleportCooldown", teleportCooldown);
		}
	}

	@Override
	public void readData(CompoundTag tag) {
		super.readData(tag);

		linkedPos = null;
		linkedDimension = null;

		if (tag.contains("LinkedDimension")) {
			linkedPos = new BlockPos(tag.getInt("LinkedX"), tag.getInt("LinkedY"), tag.getInt("LinkedZ"));
			linkedDimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(tag.getString("LinkedDimension")));
		}

		preview = tag.contains("Preview") ? tag.getByteArray("Preview") : null;
		teleportWarmup = tag.getInt("TeleportWarmup");
		teleportCooldown = tag.getInt("TeleportCooldown");
	}

	@Override
	public void writeNetData(CompoundTag tag) {
		super.writeNetData(tag);

		if (preview != null) {
			tag.putByteArray("Preview", preview);
		}
	}

	@Override
	public void readNetData(CompoundTag tag) {
		super.readNetData(tag);
		preview = tag.contains("Preview") ? tag.getByteArray("Preview") : null;
	}

	@Override
	public void stepOn(ServerPlayer player) {
		if (energy < FTBICConfig.TELEPORTER_USE || teleportCooldown > 0 || linkedDimension == null || linkedPos == null) {
			return;
		}

		ServerLevel linkedLevel = player.server.getLevel(linkedDimension);

		if (linkedLevel != null) {
			linkedLevel.getChunk(linkedPos.getX() >> 4, linkedPos.getZ() >> 4); // Force chunk load

			if (teleportWarmup < 10) {
				teleportWarmup += 2;
			} else {
				Direction direction = getFacing(Direction.NORTH);
				energy -= FTBICConfig.TELEPORTER_USE;
				player.teleportTo(linkedLevel, linkedPos.getX() + 0.5D, linkedPos.getY() + 1.1D, linkedPos.getZ() + 0.5D, direction.toYRot() + 90F, 0F);
				level.playSound(null, worldPosition.getX() + 0.5D, worldPosition.getY() + 1.5D, worldPosition.getZ() + 0.5D, SoundEvents.ENDERMAN_TELEPORT, SoundSource.NEUTRAL, 1F, 1F);
				level.playSound(null, player.getX(), player.getEyeY(), player.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.NEUTRAL, 1F, 1F);
				teleportCooldown = 20;
				teleportWarmup = 0;
				setChanged();

				BlockEntity entity = linkedLevel.getBlockEntity(linkedPos);

				if (entity instanceof TeleporterBlockEntity) {
					((TeleporterBlockEntity) entity).teleportCooldown = 60;
					entity.setChanged();
				}
			}
		}
	}

	@Override
	public void tick() {
		if (teleportCooldown > 0) {
			teleportCooldown--;
		}

		if (teleportWarmup > 0) {
			teleportWarmup--;
		}

		if (energy >= FTBICConfig.TELEPORTER_USE && teleportCooldown <= 0 && linkedDimension != null && linkedPos != null) {
			active = true;
		}

		super.tick();
	}

	@Override
	public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide()) {
			ItemStack stack = player.getItemInHand(hand);

			if (stack.getItem() == FTBICItems.LOCATION_CARD.get()) {
				if (stack.hasTag() && stack.getTag().contains("Dimension")) {
					ResourceKey<Level> dim = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(stack.getTag().getString("Dimension")));
					BlockPos pos = new BlockPos(stack.getTag().getInt("PosX"), stack.getTag().getInt("PosY"), stack.getTag().getInt("PosZ"));

					if (dim != level.dimension() || !pos.equals(worldPosition)) {
						linkedDimension = dim;
						linkedPos = pos;
						stack.removeTagKey("Dimension");
						stack.removeTagKey("PosX");
						stack.removeTagKey("PosY");
						stack.removeTagKey("PosZ");
						setChanged();
						player.displayClientMessage(new TextComponent("Location changed!"), true);

						ServerLevel linkedLevel = ((ServerPlayer) player).server.getLevel(linkedDimension);

						if (linkedLevel != null) {
							linkedLevel.getChunk(linkedPos.getX() >> 4, linkedPos.getZ() >> 4); // Force chunk load

							BlockEntity entity = linkedLevel.getBlockEntity(linkedPos);

							if (entity instanceof TeleporterBlockEntity) {
								TeleporterBlockEntity e = (TeleporterBlockEntity) entity;

								if (e.linkedDimension == null) {
									e.linkedDimension = level.dimension();
									e.linkedPos = worldPosition;
									e.setChanged();
								}
							}
						}
					}
				} else {
					stack.addTagElement("Dimension", StringTag.valueOf(level.dimension().location().toString()));
					stack.addTagElement("PosX", IntTag.valueOf(worldPosition.getX()));
					stack.addTagElement("PosY", IntTag.valueOf(worldPosition.getY()));
					stack.addTagElement("PosZ", IntTag.valueOf(worldPosition.getZ()));
					player.displayClientMessage(new TextComponent("Location set!"), true);
				}
			}
		}

		return InteractionResult.SUCCESS;
	}
}