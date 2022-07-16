package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.util.TeleporterEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;
import java.util.List;

public class TeleporterBlockEntity extends ElectricBlockEntity {
	public BlockPos linkedPos;
	public ResourceKey<Level> linkedDimension;
	public String linkedName;
	public int warmup;
	public int cooldown;
	public boolean isPublic;
	public String name;

	public TeleporterBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICElectricBlocks.TELEPORTER, pos, state);
		linkedPos = null;
		linkedDimension = null;
		linkedName = null;
		warmup = 0;
		cooldown = 0;
		isPublic = false;
		name = "";
	}

	@Override
	public void writeData(CompoundTag tag) {
		super.writeData(tag);

		if (linkedPos != null && linkedDimension != null) {
			tag.putInt("LinkedX", linkedPos.getX());
			tag.putInt("LinkedY", linkedPos.getY());
			tag.putInt("LinkedZ", linkedPos.getZ());
			tag.putString("LinkedDimension", linkedDimension.location().toString());
			tag.putString("LinkedName", linkedName);
		}

		if (warmup > 0) {
			tag.putInt("Warmup", warmup);
		}

		if (cooldown > 0) {
			tag.putInt("Cooldown", cooldown);
		}

		if (isPublic) {
			tag.putBoolean("Public", true);
		}

		if (!name.isEmpty()) {
			tag.putString("Name", name);
		}
	}

	@Override
	public void readData(CompoundTag tag) {
		super.readData(tag);

		linkedPos = null;
		linkedDimension = null;
		linkedName = "";

		if (tag.contains("LinkedDimension")) {
			linkedPos = new BlockPos(tag.getInt("LinkedX"), tag.getInt("LinkedY"), tag.getInt("LinkedZ"));
			linkedDimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(tag.getString("LinkedDimension")));
			linkedName = tag.getString("LinkedName");
		}

		warmup = tag.getInt("Warmup");
		cooldown = tag.getInt("Cooldown");
		isPublic = tag.getBoolean("Public");
		name = tag.getString("Name");
	}

	@Override
	public void writeNetData(CompoundTag tag) {
		super.writeNetData(tag);

		if (linkedName != null && !linkedName.isEmpty()) {
			tag.putString("LinkedName", linkedName);
		}
	}

	@Override
	public void readNetData(CompoundTag tag) {
		super.readNetData(tag);
		if (tag == null) {
			return;
		}

		if (tag.contains("LinkedName")) {
			linkedName = tag.getString("LinkedName");
		}
	}

	@Override
	public void stepOn(ServerPlayer player) {
		if (cooldown > 0 || linkedDimension == null || linkedPos == null) {
			return;
		}

		double use = getEnergyUse(linkedDimension, linkedPos);

		if (energy < use) {
			return;
		}

		ServerLevel linkedLevel = player.server.getLevel(linkedDimension);

		if (linkedLevel != null && linkedLevel.isLoaded(linkedPos)) {
			if (warmup < 10) {
				warmup += 2;
			} else {
				BlockEntity entity = linkedLevel.getBlockEntity(linkedPos);

				if (entity instanceof TeleporterBlockEntity t) {
					Direction direction = t.getFacing(Direction.NORTH);
					energy -= use;
					player.teleportTo(linkedLevel, linkedPos.getX() + 0.5D, linkedPos.getY() + 1.1D, linkedPos.getZ() + 0.5D, direction.toYRot() + 90F, 0F);
					level.playSound(null, worldPosition.getX() + 0.5D, worldPosition.getY() + 1.5D, worldPosition.getZ() + 0.5D, SoundEvents.ENDERMAN_TELEPORT, SoundSource.NEUTRAL, 1F, 1F);
					level.playSound(null, player.getX(), player.getEyeY(), player.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.NEUTRAL, 1F, 1F);
					cooldown = 20;
					warmup = 0;
					setChanged();

					t.cooldown = 60;
					t.setChanged();

					if (linkedName != null && !linkedName.equals(t.name)) {
						linkedName = t.name;
						syncBlock();
					}

					FTBIC.LOGGER.debug(player.getScoreboardName() + " used teleporter to " + linkedDimension.location() + ":" + linkedPos);
				} else {
					linkedName = "";
					syncBlock();
				}
			}
		} else {
			player.displayClientMessage(Component.translatable("block.ftbic.teleporter.load_error").withStyle(ChatFormatting.RED), true);
		}
	}

	@Override
	public void tick() {
		if (cooldown > 0) {
			cooldown--;
		}

		if (warmup > 0) {
			warmup--;
		}

		if (cooldown <= 0 && linkedDimension != null && linkedPos != null && energy >= getEnergyUse(linkedDimension, linkedPos)) {
			active = true;
		}

		super.tick();
	}

	@Override
	public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide()) {
			if (placerId.equals(player.getUUID())) {
				// TODO: add gui here
				// open gui
			} else {
				player.displayClientMessage(Component.translatable("block.ftbic.teleporter.perm_error").withStyle(ChatFormatting.RED), true);
			}
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public boolean savePlacer() {
		return true;
	}

	public double getEnergyUse(ResourceKey<Level> d, BlockPos p) {
		if (d != level.dimension()) {
			return FTBICConfig.MACHINES.TELEPORTER_MAX_USE.get();
		}

		double mind = FTBICConfig.MACHINES.TELEPORTER_MIN_DISTANCE.get();
		double maxd = FTBICConfig.MACHINES.TELEPORTER_MAX_DISTANCE.get();
		double dx = p.getX() - worldPosition.getX();
		double dz = p.getZ() - worldPosition.getZ();

		double dist = Mth.clamp(dx * dx + dz * dz, mind, maxd);
		return Mth.lerp((dist - mind) / (maxd - mind), FTBICConfig.MACHINES.TELEPORTER_MIN_USE.get(), FTBICConfig.MACHINES.TELEPORTER_MAX_USE.get());
	}

	@Override
	public void writeMenu(ServerPlayer player, FriendlyByteBuf buf) {
		super.writeMenu(player, buf);

		List<TeleporterEntry> list = new ArrayList<>();

		// fixme
//		for (BlockEntity entity : level.blockEntityList) {
//			if (entity instanceof TeleporterBlockEntity) {
//				TeleporterBlockEntity teleporter = (TeleporterBlockEntity) entity;
//
//				if (teleporter.isPublic || teleporter.placerId.equals(player.getUUID())) {
//					list.add(new TeleporterEntry(teleporter, getEnergyUse(teleporter.level.dimension(), teleporter.worldPosition)));
//				}
//			}
//		}

		buf.writeVarInt(list.size());

		for (TeleporterEntry e : list) {
			e.write(buf);
		}
	}

	public void select(ServerPlayer player, ResourceKey<Level> d, BlockPos p) {
		if (placerId.equals(player.getUUID())) {
			ServerLevel linkedLevel = player.server.getLevel(d);

			if (linkedLevel != null && linkedLevel.isLoaded(p)) {
				BlockEntity entity = linkedLevel.getBlockEntity(p);

				if (entity instanceof TeleporterBlockEntity t) {

					if (t.isPublic || t.placerId.equals(player.getUUID())) {
						linkedDimension = d;
						linkedPos = p;
						linkedName = t.name;
						syncBlock();
					}
				} else {
					player.displayClientMessage(Component.translatable("block.ftbic.teleporter.load_error").withStyle(ChatFormatting.RED), true);
				}
			} else {
				player.displayClientMessage(Component.translatable("block.ftbic.teleporter.load_error").withStyle(ChatFormatting.RED), true);
			}
		} else {
			player.displayClientMessage(Component.translatable("block.ftbic.teleporter.perm_error").withStyle(ChatFormatting.RED), true);
		}
	}
}