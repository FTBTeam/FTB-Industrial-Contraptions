package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.GeneratorBlockEntity;
import dev.ftb.mods.ftbic.net.TeleporterListPayload;
import dev.ftb.mods.ftbic.registry.TeleporterChunkTickets;
import dev.ftb.mods.ftbic.screen.TeleporterMenu;
import dev.ftb.mods.ftbic.util.TeleporterEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import dev.ftb.mods.ftbic.util.CachedEnergyStorage;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TeleporterBlockEntity extends GeneratorBlockEntity {
	public static final int SEND_SLOTS = 9;
	public static final int RECEIVE_SLOTS = 9;
	public static final int TANK_CAPACITY = 16_000;
	public static final int ITEMS_PER_FLUSH = 8;
	public static final int FLUID_PER_FLUSH = 2_000;

	private static final Set<TeleporterBlockEntity> ALL_LOADED =
			Collections.newSetFromMap(new ConcurrentHashMap<>());

	public static void purgeLevel(Level level) {
		ALL_LOADED.removeIf(t -> t.level == level || t.level == null || t.isRemoved());
	}

	public static void purgeAll() {
		ALL_LOADED.clear();
	}

	public BlockPos linkedPos;
	public ResourceKey<Level> linkedDimension;
	public String linkedName = "";
	public int warmup;
	public int cooldown;
	public boolean isPublic;
	public String name = "";

	public final ItemStack[] sendItems = new ItemStack[SEND_SLOTS];
	public final ItemStack[] receiveItems = new ItemStack[RECEIVE_SLOTS];
	public Fluid sendFluid = Fluids.EMPTY;
	public int sendFluidAmount = 0;
	public Fluid receiveFluid = Fluids.EMPTY;
	public int receiveFluidAmount = 0;

	private long lastSendTick = Long.MIN_VALUE;
	private boolean peerChunkForced;
	private ResourceKey<Level> forcedChunkDim;
	private int forcedChunkX;
	private int forcedChunkZ;

	public TeleporterBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICElectricBlocks.TELEPORTER, pos, state);
		Arrays.fill(sendItems, ItemStack.EMPTY);
		Arrays.fill(receiveItems, ItemStack.EMPTY);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		maxEnergyOutputTransfer = FTBICConfig.ENERGY.HV_TRANSFER_RATE.get();
	}

	@Override
	public boolean isValidEnergyInputSide(Direction direction) {
		return true;
	}

	@Override
	public void handleGeneration() {
	}

	@Override
	public CachedEnergyStorage[] getConnectedEnergyBlocks() {
		CachedEnergyStorage[] all = super.getConnectedEnergyBlocks();
		if (all.length == 0) return all;
		int keep = 0;
		for (CachedEnergyStorage s : all) {
			if (!(s.blockEntity instanceof TeleporterBlockEntity)) keep++;
		}
		if (keep == all.length) return all;
		CachedEnergyStorage[] out = new CachedEnergyStorage[keep];
		int idx = 0;
		for (CachedEnergyStorage s : all) {
			if (!(s.blockEntity instanceof TeleporterBlockEntity)) out[idx++] = s;
		}
		return out;
	}

	@Override
	public void setLevel(Level level) {
		super.setLevel(level);
		if (!level.isClientSide()) ALL_LOADED.add(this);
	}

	@Override
	public void setRemoved() {
		releaseChunkTicketIfHeld();
		super.setRemoved();
		ALL_LOADED.remove(this);
	}

	@Override
	public void onBroken(Level lvl, BlockPos pos) {
		releaseChunkTicketIfHeld();
		for (ItemStack s : sendItems) if (!s.isEmpty()) Block.popResource(lvl, pos, s);
		for (ItemStack s : receiveItems) if (!s.isEmpty()) Block.popResource(lvl, pos, s);
		super.onBroken(lvl, pos);
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv) {
		return new TeleporterMenu(id, inv, this);
	}

	@Override
	public void openMenu(ServerPlayer player) {
		super.openMenu(player);
		List<TeleporterEntry> peers = collectPeers(player);
		PacketDistributor.sendToPlayer(player, new TeleporterListPayload(peers));
	}

	private List<TeleporterEntry> collectPeers(ServerPlayer player) {
		List<TeleporterEntry> out = new ArrayList<>();
		if (level == null) return out;
		for (TeleporterBlockEntity t : ALL_LOADED) {
			if (t == this || t.level == null || t.isRemoved()) continue;
			if (!t.isPublic && !Util.NIL_UUID.equals(t.placerId) && !t.placerId.equals(player.getUUID())) continue;
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
		saveStackArray(output, "SendItems", sendItems);
		saveStackArray(output, "ReceiveItems", receiveItems);
		saveTank(output, "SendFluid", "SendAmount", sendFluid, sendFluidAmount);
		saveTank(output, "ReceiveFluid", "ReceiveAmount", receiveFluid, receiveFluidAmount);
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
		loadStackArray(input, "SendItems", sendItems);
		loadStackArray(input, "ReceiveItems", receiveItems);
		sendFluid = loadFluid(input, "SendFluid");
		sendFluidAmount = Mth.clamp(input.getIntOr("SendAmount", 0), 0, TANK_CAPACITY);
		if (sendFluid == Fluids.EMPTY) sendFluidAmount = 0;
		receiveFluid = loadFluid(input, "ReceiveFluid");
		receiveFluidAmount = Mth.clamp(input.getIntOr("ReceiveAmount", 0), 0, TANK_CAPACITY);
		if (receiveFluid == Fluids.EMPTY) receiveFluidAmount = 0;
	}

	private static void saveStackArray(ValueOutput output, String key, ItemStack[] stacks) {
		boolean any = false;
		for (ItemStack s : stacks) if (!s.isEmpty()) { any = true; break; }
		if (!any) return;
		ValueOutput.TypedOutputList<ElectricBlockEntity.SlotStack> list = output.list(key, ElectricBlockEntity.SlotStack.CODEC);
		for (int i = 0; i < stacks.length; i++) {
			if (!stacks[i].isEmpty()) list.add(new ElectricBlockEntity.SlotStack(i, stacks[i]));
		}
	}

	private static void loadStackArray(ValueInput input, String key, ItemStack[] stacks) {
		Arrays.fill(stacks, ItemStack.EMPTY);
		input.listOrEmpty(key, ElectricBlockEntity.SlotStack.CODEC).forEach(e -> {
			if (e.slot() >= 0 && e.slot() < stacks.length) stacks[e.slot()] = e.stack();
		});
	}

	private static void saveTank(ValueOutput output, String fluidKey, String amountKey, Fluid f, int amount) {
		if (amount <= 0 || f == Fluids.EMPTY) return;
		Identifier fid = BuiltInRegistries.FLUID.getKey(f);
		if (fid == null) return;
		output.putString(fluidKey, fid.toString());
		output.putInt(amountKey, amount);
	}

	private static Fluid loadFluid(ValueInput input, String key) {
		String id = input.getStringOr(key, "");
		if (id.isEmpty()) return Fluids.EMPTY;
		Identifier parsed = Identifier.tryParse(id);
		if (parsed == null) return Fluids.EMPTY;
		Fluid f = BuiltInRegistries.FLUID.getValue(ResourceKey.create(Registries.FLUID, parsed));
		return f == null ? Fluids.EMPTY : f;
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
		if (!(level instanceof ServerLevel)) {
			super.tick();
			return;
		}
		if (cooldown > 0) cooldown--;
		if (warmup > 0) warmup--;
		if (cooldown <= 0 && linkedDimension != null && linkedPos != null
				&& energy >= getEnergyUse(linkedDimension, linkedPos)) {
			active = true;
		}

		if (linkedDimension != null && linkedPos != null) {
			long now = level.getGameTime();
			boolean active20 = (now - lastSendTick) <= FTBICConfig.MACHINES.TELEPORTER_ACTIVE_WINDOW_TICKS.get();
			if (active20) active = true;

			if (active20 && (now % 20) == 0 && energy > 0D) {
				double drain = Math.min(energy, FTBICConfig.MACHINES.TELEPORTER_TRANSPORT_DRAIN.get());
				energy -= drain;
				setChanged();
			}

			if ((now % 5) == 0) balancePairEnergy();

			if ((now % 4) == 0) flushSendBuffersToPeer();

			boolean wantForced = (now - lastSendTick) <= FTBICConfig.MACHINES.TELEPORTER_CHUNK_LOAD_IDLE_TICKS.get();
			updateChunkTicket(wantForced);
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
		name = clampName(newName == null ? "" : newName.trim(), 32);
		isPublic = newPublic;
		setChanged();
		if (level != null) {
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
		}
	}

	private static String clampName(String s, int maxChars) {
		if (s.length() <= maxChars) return s;
		String out = s.substring(0, maxChars);
		if (!out.isEmpty() && Character.isHighSurrogate(out.charAt(out.length() - 1))) {
			out = out.substring(0, out.length() - 1);
		}
		return out;
	}

	public void unlink(ServerPlayer player) {
		if (!placerId.equals(player.getUUID())) {
			player.sendSystemMessage(Component.translatable("block.ftbic.teleporter.perm_error").withStyle(ChatFormatting.RED));
			return;
		}
		releaseChunkTicketIfHeld();
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
		if (level != null && d.equals(level.dimension()) && p.equals(worldPosition)) {
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
		if (t.isPublic || Util.NIL_UUID.equals(t.placerId) || t.placerId.equals(player.getUUID())) {
			releaseChunkTicketIfHeld();
			linkedDimension = d;
			linkedPos = p;
			linkedName = t.name;
			setChanged();
			if (level != null) {
				level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
			}

			if (t.linkedPos == null || t.linkedDimension == null) {
				t.releaseChunkTicketIfHeld();
				t.linkedDimension = level.dimension();
				t.linkedPos = worldPosition;
				t.linkedName = this.name;
				t.setChanged();
				if (t.getLevel() != null) {
					t.getLevel().sendBlockUpdated(t.getBlockPos(), t.getBlockState(), t.getBlockState(), 3);
				}
			}
		}
	}

	public TeleporterBlockEntity resolvePeer() {
		if (linkedPos == null || linkedDimension == null) return null;
		if (!(level instanceof ServerLevel sl)) return null;
		ServerLevel peerLevel = sl.getServer().getLevel(linkedDimension);
		if (peerLevel == null || !peerLevel.isLoaded(linkedPos)) return null;
		return peerLevel.getBlockEntity(linkedPos) instanceof TeleporterBlockEntity t ? t : null;
	}

	public int tryInsertItem(ItemResource resource, int amount, TransactionContext tx) {
		if (resource.isEmpty() || amount <= 0) return 0;
		int inserted = 0;
		for (int i = 0; i < sendItems.length && inserted < amount; i++) {
			inserted += insertItemAt(i, resource, amount - inserted, tx);
		}
		return inserted;
	}

	public int insertItemAt(int index, ItemResource resource, int amount, TransactionContext tx) {
		if (index < 0 || index >= sendItems.length) return 0;
		if (resource.isEmpty() || amount <= 0) return 0;
		ItemStack existing = sendItems[index];
		int cap = Math.min(resource.getMaxStackSize(), 64);
		int room;
		if (existing.isEmpty()) room = cap;
		else if (!resource.matches(existing)) return 0;
		else room = cap - existing.getCount();
		int accepted = Math.min(amount, room);
		if (accepted <= 0) return 0;
		snapshotItems(tx);
		if (existing.isEmpty()) sendItems[index] = resource.toStack(accepted);
		else { ItemStack grown = existing.copy(); grown.grow(accepted); sendItems[index] = grown; }
		return accepted;
	}

	public int tryExtractItem(ItemResource resource, int amount, TransactionContext tx) {
		if (resource.isEmpty() || amount <= 0) return 0;
		int extracted = 0;
		for (int i = 0; i < receiveItems.length && extracted < amount; i++) {
			extracted += extractItemAt(i, resource, amount - extracted, tx);
		}
		return extracted;
	}

	public int extractItemAt(int slotOffset, ItemResource resource, int amount, TransactionContext tx) {
		if (slotOffset < 0 || slotOffset >= receiveItems.length) return 0;
		if (resource.isEmpty() || amount <= 0) return 0;
		ItemStack existing = receiveItems[slotOffset];
		if (existing.isEmpty() || !resource.matches(existing)) return 0;
		int taken = Math.min(amount, existing.getCount());
		snapshotItems(tx);
		ItemStack shrunk = existing.copy();
		shrunk.shrink(taken);
		receiveItems[slotOffset] = shrunk.isEmpty() ? ItemStack.EMPTY : shrunk;
		return taken;
	}

	public int tryInsertFluid(FluidResource resource, int amount, TransactionContext tx) {
		if (resource.isEmpty() || amount <= 0) return 0;
		Fluid f = resource.getFluid();
		if (sendFluid != Fluids.EMPTY && sendFluid != f) return 0;
		int room = TANK_CAPACITY - sendFluidAmount;
		int accepted = Math.min(amount, room);
		if (accepted <= 0) return 0;
		snapshotFluids(tx);
		sendFluid = f;
		sendFluidAmount += accepted;
		return accepted;
	}

	public int tryExtractFluid(FluidResource resource, int amount, TransactionContext tx) {
		if (resource.isEmpty() || amount <= 0) return 0;
		Fluid f = resource.getFluid();
		if (receiveFluid == Fluids.EMPTY || receiveFluid != f) return 0;
		int taken = Math.min(amount, receiveFluidAmount);
		if (taken <= 0) return 0;
		snapshotFluids(tx);
		receiveFluidAmount -= taken;
		if (receiveFluidAmount <= 0) { receiveFluid = Fluids.EMPTY; receiveFluidAmount = 0; }
		return taken;
	}

	public int insertIntoReceiveDirect(ItemResource resource, int amount) {
		if (resource.isEmpty() || amount <= 0) return 0;
		int cap = Math.min(resource.getMaxStackSize(), 64);
		int inserted = 0;
		for (int i = 0; i < receiveItems.length && inserted < amount; i++) {
			ItemStack existing = receiveItems[i];
			int want = amount - inserted;
			if (existing.isEmpty()) {
				int take = Math.min(want, cap);
				receiveItems[i] = resource.toStack(take);
				inserted += take;
			} else if (resource.matches(existing)) {
				int room = cap - existing.getCount();
				if (room <= 0) continue;
				int take = Math.min(want, room);
				ItemStack grown = existing.copy();
				grown.grow(take);
				receiveItems[i] = grown;
				inserted += take;
			}
		}
		if (inserted > 0) setChanged();
		return inserted;
	}

	public int insertIntoReceiveFluidDirect(Fluid fluid, int amount) {
		if (fluid == Fluids.EMPTY || amount <= 0) return 0;
		if (receiveFluid != Fluids.EMPTY && receiveFluid != fluid) return 0;
		int room = TANK_CAPACITY - receiveFluidAmount;
		int inserted = Math.min(amount, room);
		if (inserted <= 0) return 0;
		receiveFluid = fluid;
		receiveFluidAmount += inserted;
		setChanged();
		return inserted;
	}

	private void flushSendBuffersToPeer() {
		TeleporterBlockEntity peer = resolvePeer();
		if (peer == null) return;

		int itemBudget = ITEMS_PER_FLUSH;
		for (int i = 0; i < sendItems.length && itemBudget > 0; i++) {
			ItemStack src = sendItems[i];
			if (src.isEmpty()) continue;
			int want = Math.min(src.getCount(), itemBudget);
			int moved = peer.insertIntoReceiveDirect(ItemResource.of(src), want);
			if (moved > 0) {
				ItemStack shrunk = src.copy();
				shrunk.shrink(moved);
				sendItems[i] = shrunk.isEmpty() ? ItemStack.EMPTY : shrunk;
				itemBudget -= moved;
				markActive();
				setChanged();
			}
		}

		if (sendFluidAmount > 0 && sendFluid != Fluids.EMPTY) {
			int want = Math.min(sendFluidAmount, FLUID_PER_FLUSH);
			int moved = peer.insertIntoReceiveFluidDirect(sendFluid, want);
			if (moved > 0) {
				sendFluidAmount -= moved;
				if (sendFluidAmount <= 0) { sendFluid = Fluids.EMPTY; sendFluidAmount = 0; }
				markActive();
				setChanged();
			}
		}
	}

	public void clearStorage() {
		for (int i = 0; i < sendItems.length; i++) {
			ItemStack s = sendItems[i];
			if (s.isEmpty()) continue;
			int moved = insertIntoReceiveDirect(ItemResource.of(s), s.getCount());
			if (moved > 0) {
				ItemStack shrunk = s.copy();
				shrunk.shrink(moved);
				sendItems[i] = shrunk.isEmpty() ? ItemStack.EMPTY : shrunk;
			}
		}
		setChanged();
	}

	public void clearFluids() {
		if (sendFluidAmount <= 0 || sendFluid == Fluids.EMPTY) return;
		int moved = insertIntoReceiveFluidDirect(sendFluid, sendFluidAmount);
		if (moved > 0) {
			sendFluidAmount -= moved;
			if (sendFluidAmount <= 0) { sendFluid = Fluids.EMPTY; sendFluidAmount = 0; }
		}
		setChanged();
	}

	private void markActive() {
		if (level != null) lastSendTick = level.getGameTime();
	}

	public int getItemSlotCount() { return SEND_SLOTS + RECEIVE_SLOTS; }

	public ItemStack getSlot(int index) {
		if (index < 0) return ItemStack.EMPTY;
		if (index < SEND_SLOTS) return sendItems[index];
		if (index < SEND_SLOTS + RECEIVE_SLOTS) return receiveItems[index - SEND_SLOTS];
		return ItemStack.EMPTY;
	}

	public boolean isSendSlot(int index) { return index >= 0 && index < SEND_SLOTS; }
	public boolean isReceiveSlot(int index) { return index >= SEND_SLOTS && index < SEND_SLOTS + RECEIVE_SLOTS; }

	private record ItemSnapshot(ItemStack[] send, ItemStack[] receive) {}
	private record FluidSnapshot(Fluid sendFluid, int sendAmount, Fluid receiveFluid, int receiveAmount) {}

	private final SnapshotJournal<ItemSnapshot> itemJournal = new SnapshotJournal<>() {
		@Override
		protected ItemSnapshot createSnapshot() {
			ItemStack[] s = new ItemStack[sendItems.length];
			ItemStack[] r = new ItemStack[receiveItems.length];
			for (int i = 0; i < s.length; i++) s[i] = sendItems[i].copy();
			for (int i = 0; i < r.length; i++) r[i] = receiveItems[i].copy();
			return new ItemSnapshot(s, r);
		}

		@Override
		protected void revertToSnapshot(ItemSnapshot snap) {
			System.arraycopy(snap.send(), 0, sendItems, 0, sendItems.length);
			System.arraycopy(snap.receive(), 0, receiveItems, 0, receiveItems.length);
		}

		@Override
		protected void onRootCommit(ItemSnapshot original) {
			setChanged();
		}
	};

	private final SnapshotJournal<FluidSnapshot> fluidJournal = new SnapshotJournal<>() {
		@Override
		protected FluidSnapshot createSnapshot() {
			return new FluidSnapshot(sendFluid, sendFluidAmount, receiveFluid, receiveFluidAmount);
		}

		@Override
		protected void revertToSnapshot(FluidSnapshot snap) {
			sendFluid = snap.sendFluid();
			sendFluidAmount = snap.sendAmount();
			receiveFluid = snap.receiveFluid();
			receiveFluidAmount = snap.receiveAmount();
		}

		@Override
		protected void onRootCommit(FluidSnapshot original) {
			setChanged();
		}
	};

	private void snapshotItems(TransactionContext tx) {
		itemJournal.updateSnapshots(tx);
	}

	private void snapshotFluids(TransactionContext tx) {
		fluidJournal.updateSnapshots(tx);
	}

	private void balancePairEnergy() {
		if (!(level instanceof ServerLevel serverLevel)) return;
		ServerLevel peerLevel = serverLevel.getServer().getLevel(linkedDimension);
		if (peerLevel == null || !peerLevel.isLoaded(linkedPos)) return;
		if (!(peerLevel.getBlockEntity(linkedPos) instanceof TeleporterBlockEntity peer)) return;

		double totalCap = energyCapacity + peer.energyCapacity;
		if (totalCap <= 0D) return;
		double totalEnergy = energy + peer.energy;
		double targetSelf = energyCapacity * (totalEnergy / totalCap);
		double delta = targetSelf - energy;
		if (Math.abs(delta) < 0.01D) return;
		energy = Math.max(0D, Math.min(energyCapacity, energy + delta));
		peer.energy = Math.max(0D, Math.min(peer.energyCapacity, peer.energy - delta));
		setChanged();
		peer.setChanged();
	}

	private void updateChunkTicket(boolean wantForced) {
		if (!(level instanceof ServerLevel serverLevel)) return;
		if (wantForced) {
			ServerLevel peerLevel = serverLevel.getServer().getLevel(linkedDimension);
			if (peerLevel == null) {
				releaseChunkTicketIfHeld();
				return;
			}
			int newX = linkedPos.getX() >> 4;
			int newZ = linkedPos.getZ() >> 4;
			if (peerChunkForced && linkedDimension.equals(forcedChunkDim) && newX == forcedChunkX && newZ == forcedChunkZ) return;
			releaseChunkTicketIfHeld();
			boolean added = TeleporterChunkTickets.CONTROLLER.forceChunk(peerLevel, worldPosition, newX, newZ, true, true);
			if (added) {
				peerChunkForced = true;
				forcedChunkDim = linkedDimension;
				forcedChunkX = newX;
				forcedChunkZ = newZ;
			}
		} else {
			releaseChunkTicketIfHeld();
		}
	}

	private void releaseChunkTicketIfHeld() {
		if (!peerChunkForced || forcedChunkDim == null) {
			peerChunkForced = false;
			forcedChunkDim = null;
			return;
		}
		if (level instanceof ServerLevel serverLevel) {
			ServerLevel peerLevel = serverLevel.getServer().getLevel(forcedChunkDim);
			if (peerLevel != null) {
				TeleporterChunkTickets.CONTROLLER.forceChunk(peerLevel, worldPosition,
						forcedChunkX, forcedChunkZ, false, true);
			}
		}
		peerChunkForced = false;
		forcedChunkDim = null;
	}
}
