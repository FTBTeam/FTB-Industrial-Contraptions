package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import dev.ftb.mods.ftbic.net.TeleporterListPayload;
import dev.ftb.mods.ftbic.screen.TeleporterMenu;
import dev.ftb.mods.ftbic.util.TeleporterEntry;

public class TeleporterBlockEntity extends ElectricBlockEntityRef {
	private static final java.util.Set<TeleporterBlockEntity> ALL_LOADED =
			java.util.Collections.newSetFromMap(new java.util.concurrent.ConcurrentHashMap<>());

	public BlockPos linkedPos;
	public ResourceKey<Level> linkedDimension;
	public String linkedName = "";
	public int warmup;
	public int cooldown;
	public boolean isPublic;
	public String name = "";

	public TeleporterBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICElectricBlocks.TELEPORTER, pos, state);
	}

	@Override
	public void setLevel(Level level) {
		super.setLevel(level);
		if (!level.isClientSide()) ALL_LOADED.add(this);
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		ALL_LOADED.remove(this);
	}

	@Override
	public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int id, net.minecraft.world.entity.player.Inventory inv) {
		return new TeleporterMenu(id, inv, this);
	}

	@Override
	public void openMenu(ServerPlayer player) {
		super.openMenu(player);
		// Ship the destination list immediately after the menu opens.
		java.util.List<TeleporterEntry> peers = collectPeers(player);
		net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player,
				new TeleporterListPayload(peers));
	}

	private java.util.List<TeleporterEntry> collectPeers(ServerPlayer player) {
		java.util.List<TeleporterEntry> out = new java.util.ArrayList<>();
		if (level == null) return out;
		for (TeleporterBlockEntity t : ALL_LOADED) {
			if (t == this || t.level == null || t.isRemoved()) continue;
			if (!t.isPublic && !t.placerId.equals(player.getUUID())) continue;
			ResourceKey<Level> peerDim = t.level.dimension();
			String display = t.name.isEmpty() ? "Unnamed" : t.name;
			out.add(new TeleporterEntry(
					peerDim, t.getBlockPos(), display, getEnergyUse(peerDim, t.getBlockPos())));
		}
		return out;
	}

	@Override
	public boolean savePlacer() {
		return true;
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		if (linkedPos != null && linkedDimension != null) {
			output.putInt("LinkedX", linkedPos.getX());
			output.putInt("LinkedY", linkedPos.getY());
			output.putInt("LinkedZ", linkedPos.getZ());
			output.putString("LinkedDimension", linkedDimension.identifier().toString());
			if (linkedName != null && !linkedName.isEmpty()) {
				output.putString("LinkedName", linkedName);
			}
		}
		if (warmup > 0) output.putInt("Warmup", warmup);
		if (cooldown > 0) output.putInt("Cooldown", cooldown);
		if (isPublic) output.putBoolean("Public", true);
		if (!name.isEmpty()) output.putString("Name", name);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		linkedPos = null;
		linkedDimension = null;
		linkedName = "";
		String dim = input.getStringOr("LinkedDimension", "");
		if (!dim.isEmpty()) {
			Identifier id = Identifier.tryParse(dim);
			if (id != null) {
				linkedPos = new BlockPos(
						input.getIntOr("LinkedX", 0),
						input.getIntOr("LinkedY", 0),
						input.getIntOr("LinkedZ", 0));
				linkedDimension = ResourceKey.create(Registries.DIMENSION, id);
				linkedName = input.getStringOr("LinkedName", "");
			}
		}
		warmup = input.getIntOr("Warmup", 0);
		cooldown = input.getIntOr("Cooldown", 0);
		isPublic = input.getBooleanOr("Public", false);
		name = input.getStringOr("Name", "");
	}

	@Override
	public void stepOn(ServerPlayer player) {
		if (cooldown > 0 || linkedDimension == null || linkedPos == null || level == null) return;

		double use = getEnergyUse(linkedDimension, linkedPos);
		if (energy < use) return;

		ServerLevel linkedLevel = player.level().getServer().getLevel(linkedDimension);
		if (linkedLevel == null || !linkedLevel.isLoaded(linkedPos)) {
			player.sendSystemMessage(Component.translatable("block.ftbic.teleporter.load_error").withStyle(ChatFormatting.RED));
			return;
		}

		if (warmup < 10) {
			warmup += 2;
			return;
		}

		BlockEntity entity = linkedLevel.getBlockEntity(linkedPos);
		if (!(entity instanceof TeleporterBlockEntity t)) {
			linkedName = "";
			setChanged();
			return;
		}

		Direction direction = t.getFacing(Direction.NORTH);
		energy -= use;
		player.teleportTo(linkedLevel,
				linkedPos.getX() + 0.5D, linkedPos.getY() + 1.1D, linkedPos.getZ() + 0.5D,
				java.util.Collections.emptySet(),
				direction.toYRot() + 90F, 0F, true);
		level.playSound(null, worldPosition.getX() + 0.5, worldPosition.getY() + 1.5, worldPosition.getZ() + 0.5,
				SoundEvents.ENDERMAN_TELEPORT, SoundSource.NEUTRAL, 1F, 1F);
		linkedLevel.playSound(null, player.getX(), player.getEyeY(), player.getZ(),
				SoundEvents.ENDERMAN_TELEPORT, SoundSource.NEUTRAL, 1F, 1F);
		cooldown = 20;
		warmup = 0;
		t.cooldown = 60;
		t.setChanged();
		if (linkedName != null && !linkedName.equals(t.name)) {
			linkedName = t.name;
		}
		setChanged();
	}

	@Override
	public void tick() {
		if (cooldown > 0) cooldown--;
		if (warmup > 0) warmup--;
		if (cooldown <= 0 && linkedDimension != null && linkedPos != null
				&& level != null && energy >= getEnergyUse(linkedDimension, linkedPos)) {
			active = true;
		}
		super.tick();
	}

	public double getEnergyUse(ResourceKey<Level> d, BlockPos p) {
		if (level == null || d != level.dimension()) {
			return FTBICConfig.MACHINES.TELEPORTER_MAX_USE.get();
		}
		double mind = FTBICConfig.MACHINES.TELEPORTER_MIN_DISTANCE.get();
		double maxd = FTBICConfig.MACHINES.TELEPORTER_MAX_DISTANCE.get();
		if (maxd <= mind) {
			return FTBICConfig.MACHINES.TELEPORTER_MAX_USE.get();
		}
		double dx = p.getX() - worldPosition.getX();
		double dz = p.getZ() - worldPosition.getZ();
		double dist = Mth.clamp(Math.sqrt(dx * dx + dz * dz), mind, maxd);
		return Mth.lerp((dist - mind) / (maxd - mind),
				FTBICConfig.MACHINES.TELEPORTER_MIN_USE.get(),
				FTBICConfig.MACHINES.TELEPORTER_MAX_USE.get());
	}

	public void configure(ServerPlayer player, String newName, boolean newPublic) {
		if (!placerId.equals(player.getUUID())) {
			player.sendSystemMessage(Component.translatable("block.ftbic.teleporter.perm_error").withStyle(ChatFormatting.RED));
			return;
		}
		String trimmed = newName == null ? "" : newName.trim();
		if (trimmed.length() > 32) trimmed = trimmed.substring(0, 32);
		name = trimmed;
		isPublic = newPublic;
		setChanged();
		if (level != null) {
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
		}
	}

	public void unlink(ServerPlayer player) {
		if (!placerId.equals(player.getUUID())) {
			player.sendSystemMessage(Component.translatable("block.ftbic.teleporter.perm_error").withStyle(ChatFormatting.RED));
			return;
		}
		linkedDimension = null;
		linkedPos = null;
		linkedName = "";
		setChanged();
		if (level != null) {
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
		}
	}

	public void select(ServerPlayer player, ResourceKey<Level> d, BlockPos p) {
		if (!placerId.equals(player.getUUID())) {
			player.sendSystemMessage(Component.translatable("block.ftbic.teleporter.perm_error").withStyle(ChatFormatting.RED));
			return;
		}
		ServerLevel linkedLevel = player.level().getServer().getLevel(d);
		if (linkedLevel == null || !linkedLevel.isLoaded(p)) {
			player.sendSystemMessage(Component.translatable("block.ftbic.teleporter.load_error").withStyle(ChatFormatting.RED));
			return;
		}
		if (!(linkedLevel.getBlockEntity(p) instanceof TeleporterBlockEntity t)) {
			player.sendSystemMessage(Component.translatable("block.ftbic.teleporter.load_error").withStyle(ChatFormatting.RED));
			return;
		}
		if (t.isPublic || net.minecraft.util.Util.NIL_UUID.equals(t.placerId) || t.placerId.equals(player.getUUID())) {
			linkedDimension = d;
			linkedPos = p;
			linkedName = t.name;
			setChanged();
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
		}
	}
}
