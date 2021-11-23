package dev.ftb.mods.ftbic.block.entity;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.ElectricBlock;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.recipe.RecipeCache;
import dev.ftb.mods.ftbic.util.EnergyHandler;
import dev.ftb.mods.ftbic.util.OpenMenuFactory;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ElectricBlockEntity extends BlockEntity implements TickableBlockEntity, EnergyHandler, IItemHandlerModifiable, ContainerData {
	private static final AtomicLong ELECTRIC_NETWORK_CHANGES = new AtomicLong(0L);

	public static void electricNetworkUpdated(LevelAccessor level, BlockPos pos) {
		// TODO: Possibly implement some kind of localized network change counter. But for now, this works
		ELECTRIC_NETWORK_CHANGES.incrementAndGet();
	}

	public static long getCurrentElectricNetwork(LevelAccessor level, BlockPos pos) {
		return ELECTRIC_NETWORK_CHANGES.get();
	}

	public final ElectricBlockInstance electricBlockInstance;
	private boolean changed;
	public double energy;
	public final ItemStack[] inputItems;
	public final ItemStack[] outputItems;
	private LazyOptional<?> thisOptional;
	public boolean active;
	private int changeStateTicks;
	private boolean burnt;

	public double energyCapacity;
	public double maxEnergyOutput;
	public double maxInputEnergy;
	public boolean autoEject;

	public UUID placerId = Util.NIL_UUID;
	public String placerName = "";

	public ElectricBlockEntity(ElectricBlockInstance type) {
		super(type.blockEntity.get());
		electricBlockInstance = type;
		changed = false;
		energy = 0;
		inputItems = new ItemStack[type.inputItemCount];
		outputItems = new ItemStack[type.outputItemCount];
		Arrays.fill(inputItems, ItemStack.EMPTY);
		Arrays.fill(outputItems, ItemStack.EMPTY);

		if (inputItems.length + outputItems.length > 127) {
			throw new RuntimeException("Internal inventory of " + getType().getRegistryName() + " too large!");
		}

		thisOptional = null;
		active = false;
		changeStateTicks = 0;
		burnt = false;
	}

	public void writeData(CompoundTag tag) {
		tag.putDouble("Energy", energy);

		if (inputItems.length + outputItems.length > 0) {
			ListTag inv = new ListTag();

			for (int slot = 0; slot < inputItems.length + outputItems.length; slot++) {
				ItemStack stack = getStackInSlot(slot);

				if (!stack.isEmpty()) {
					CompoundTag tag1 = stack.serializeNBT();
					tag1.putByte("Slot", (byte) slot);
					inv.add(tag1);
				}
			}

			tag.put("Inventory", inv);
		}

		if (burnt) {
			tag.putBoolean("Burnt", true);
		}

		if (!placerId.equals(Util.NIL_UUID)) {
			tag.putUUID("PlacerId", placerId);
			tag.putString("PlacerName", placerName);
		}
	}

	public void readData(CompoundTag tag) {
		energy = tag.getDouble("Energy");

		if (inputItems.length + outputItems.length > 0) {
			Arrays.fill(inputItems, ItemStack.EMPTY);
			Arrays.fill(outputItems, ItemStack.EMPTY);

			ListTag inv = tag.getList("Inventory", Constants.NBT.TAG_COMPOUND);

			for (int i = 0; i < inv.size(); i++) {
				CompoundTag tag1 = inv.getCompound(i);
				setStackInSlot(tag1.getByte("Slot"), ItemStack.of(tag1));
			}
		}

		burnt = tag.getBoolean("Burnt");

		if (tag.hasUUID("PlacerId")) {
			placerId = tag.getUUID("PlacerId");
			placerName = tag.getString("PlacerName");
		} else {
			placerId = Util.NIL_UUID;
			placerName = "";
		}
	}

	public void writeNetData(CompoundTag tag) {
		if (burnt) {
			tag.putBoolean("Burnt", true);
		}
	}

	public void readNetData(CompoundTag tag) {
		burnt = tag.getBoolean("Burnt");
	}

	@Override
	public void load(BlockState state, CompoundTag tag) {
		super.load(state, tag);
		readData(tag);
		initProperties();
		upgradesChanged();
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		super.save(tag);
		writeData(tag);
		return tag;
	}

	@Override
	public void handleUpdateTag(BlockState state, CompoundTag tag) {
		readNetData(tag);
		initProperties();
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		writeNetData(tag);
		return tag;
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		readNetData(pkt.getTag());
		initProperties();
	}

	@Nullable
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		CompoundTag tag = new CompoundTag();
		writeNetData(tag);
		return new ClientboundBlockEntityDataPacket(worldPosition, 0, tag);
	}

	@Override
	public void onLoad() {
		initProperties();

		if (level != null && !level.isClientSide()) {
			upgradesChanged();
		}

		if (level != null && level.isClientSide() && !tickClientSide()) {
			level.tickableBlockEntities.remove(this);
		}

		super.onLoad();
	}

	public boolean tickClientSide() {
		return false;
	}

	public LazyOptional<?> getThisOptional() {
		if (thisOptional == null) {
			thisOptional = LazyOptional.of(() -> this);
		}

		return thisOptional;
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();

		if (thisOptional != null) {
			thisOptional.invalidate();
			thisOptional = null;
		}
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (inputItems.length + outputItems.length) > 0) {
			return getThisOptional().cast();
		}

		return super.getCapability(cap, side);
	}

	protected void handleChanges() {
		if (changeStateTicks > 0) {
			changeStateTicks--;
		}

		if (changeStateTicks <= 0) {
			if (!isBurnt()) {
				if (electricBlockInstance.canBeActive && getBlockState().getValue(ElectricBlock.ACTIVE) != active && !level.isClientSide()) {
					level.setBlock(worldPosition, getBlockState().setValue(ElectricBlock.ACTIVE, active), 3);
					setChanged();
				}

				active = false;
			}

			changeStateTicks = FTBICConfig.STATE_UPDATE_TICKS;

			if (changed) {
				setChangedNow();
			}
		}
	}

	@Override
	public void tick() {
		handleChanges();
	}

	@Override
	public void setChanged() {
		changed = true;
	}

	public void setChangedNow() {
		changed = false;
		level.blockEntityChanged(worldPosition, this);
	}

	@Override
	public final double getEnergyCapacity() {
		return energyCapacity;
	}

	@Override
	public final double getEnergy() {
		return energy;
	}

	@Override
	public final void setEnergyRaw(double e) {
		energy = e;
	}

	public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
		return InteractionResult.SUCCESS;
	}

	public void openMenu(ServerPlayer player, OpenMenuFactory openMenuFactory) {
		NetworkHooks.openGui(player, new MenuProvider() {
			@Override
			public Component getDisplayName() {
				return new TranslatableComponent(getBlockState().getBlock().getDescriptionId());
			}

			@Override
			public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player1) {
				return openMenuFactory.create(id, playerInv);
			}
		}, this::writeMenu);
	}

	public void writeMenu(FriendlyByteBuf buf) {
		buf.writeBlockPos(worldPosition);
	}

	@Override
	public final double getMaxInputEnergy() {
		return maxInputEnergy;
	}

	@Nullable
	public RecipeCache getRecipeCache() {
		return level == null ? null : RecipeCache.get(level);
	}

	@Override
	public int getSlots() {
		return inputItems.length + outputItems.length;
	}

	@NotNull
	@Override
	public ItemStack getStackInSlot(int slot) {
		if (slot < 0 || slot >= getSlots()) {
			throw new RuntimeException("Slot " + slot + " not in valid range - [0," + getSlots() + ")");
		} else if (slot >= inputItems.length) {
			return outputItems[slot - inputItems.length];
		} else {
			return inputItems[slot];
		}
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		if (slot < 0 || slot >= getSlots()) {
			throw new RuntimeException("Slot " + slot + " not in valid range - [0," + getSlots() + ")");
		} else if (slot >= inputItems.length) {
			ItemStack prev = outputItems[slot - inputItems.length];
			outputItems[slot - inputItems.length] = stack;
			inventoryChanged(slot, prev);
		} else {
			ItemStack prev = inputItems[slot];
			inputItems[slot] = stack;
			inventoryChanged(slot, prev);
		}
	}

	@NotNull
	@Override
	public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		if (slot >= inputItems.length || stack.isEmpty() || !isItemValid(slot, stack)) {
			return stack;
		}

		ItemStack existing = inputItems[slot];
		int limit = Math.min(this.getSlotLimit(slot), stack.getMaxStackSize());

		if (!existing.isEmpty()) {
			if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) {
				return stack;
			}

			limit -= existing.getCount();
		}

		if (limit <= 0) {
			return stack;
		}

		boolean reachedLimit = stack.getCount() > limit;

		if (!simulate) {
			if (existing.isEmpty()) {
				ItemStack prev = inputItems[slot];
				inputItems[slot] = reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack;
				inventoryChanged(slot, prev);
			} else {
				ItemStack prev = existing.copy();
				existing.grow(reachedLimit ? limit : stack.getCount());
				inventoryChanged(slot, prev);
			}
		}

		return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
	}

	@NotNull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (slot < inputItems.length || amount <= 0) {
			return ItemStack.EMPTY;
		}

		slot -= inputItems.length;
		ItemStack existing = outputItems[slot];

		if (existing.isEmpty()) {
			return ItemStack.EMPTY;
		}

		int toExtract = Math.min(amount, existing.getMaxStackSize());

		if (existing.getCount() <= toExtract) {
			if (!simulate) {
				outputItems[slot] = ItemStack.EMPTY;
				inventoryChanged(slot, existing);
				return existing;
			} else {
				return existing.copy();
			}
		} else {
			if (!simulate) {
				outputItems[slot] = ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract);
				inventoryChanged(slot, existing);
			}

			return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
		}
	}

	public void inventoryChanged(int slot, @Nullable ItemStack prev) {
		setChanged();
	}

	public void energyChanged(int prev) {
		if (energy == 0 || prev == 0 || energy == energyCapacity) {
			setChanged();
		}
	}

	@Override
	public int getSlotLimit(int slot) {
		return 64;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return slot < inputItems.length;
	}

	public ItemStack addOutputInSlot(int slot, ItemStack stack) {
		if (outputItems[slot].isEmpty()) {
			outputItems[slot] = stack;
			return ItemStack.EMPTY;
		}

		ItemStack existing = outputItems[slot];

		int limit = stack.getMaxStackSize();

		if (!existing.isEmpty()) {
			if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) {
				return stack;
			}

			limit -= existing.getCount();
		}

		if (limit <= 0) {
			return stack;
		}

		boolean reachedLimit = stack.getCount() > limit;

		if (existing.isEmpty()) {
			outputItems[slot] = reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack;
		} else {
			existing.grow(reachedLimit ? limit : stack.getCount());
		}

		inventoryChanged(slot, existing);
		return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
	}

	public ItemStack addOutput(ItemStack stack) {
		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		}

		for (int i = 0; i < outputItems.length; i++) {
			if (outputItems[i].getItem() == stack.getItem()) {
				stack = addOutputInSlot(i, stack);

				if (stack.isEmpty()) {
					return ItemStack.EMPTY;
				}
			}
		}

		for (int i = 0; i < outputItems.length; i++) {
			if (outputItems[i].isEmpty()) {
				stack = addOutputInSlot(i, stack);

				if (stack.isEmpty()) {
					return ItemStack.EMPTY;
				}
			}
		}

		return stack;
	}

	public Direction[] getEjectDirections() {
		if (electricBlockInstance.facingProperty != BlockStateProperties.HORIZONTAL_FACING) {
			return Direction.values();
		}

		Direction rot = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
		Direction[] values = new Direction[6];
		values[0] = Direction.DOWN;
		values[1] = rot.getCounterClockWise();
		values[2] = rot.getOpposite();
		values[3] = rot.getClockWise();
		values[4] = rot;
		values[5] = Direction.UP;
		return values;
	}

	public void shiftInputs() {
		if (inputItems.length <= 1) {
			return;
		}

		List<ItemStack> stacks = new ArrayList<>();

		for (int i = 0; i < inputItems.length; i++) {
			if (!inputItems[i].isEmpty()) {
				stacks.add(inputItems[i]);
				inputItems[i] = ItemStack.EMPTY;
			}
		}

		for (ItemStack stack : stacks) {
			// drop items that failed to shift for some reason? but that should be impossible unless max stack size has changed for that item...
			ItemHandlerHelper.insertItemStacked(this, stack, false);
		}
	}

	public void ejectOutputItems() {
		if (!autoEject) {
			return;
		}

		Direction[] directions = null;

		for (int i = 0; i < outputItems.length; i++) {
			if (!outputItems[i].isEmpty()) {
				for (Direction direction : (directions == null ? (directions = getEjectDirections()) : directions)) {
					BlockEntity entity = level.getBlockEntity(worldPosition.relative(direction));
					IItemHandler itemHandler = entity == null ? null : entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite()).orElse(null);

					if (itemHandler != null) {
						outputItems[i] = ItemHandlerHelper.insertItemStacked(itemHandler, outputItems[i].copy(), false);

						if (outputItems[i].isEmpty()) {
							outputItems[i] = ItemStack.EMPTY;
							break;
						}
					}
				}
			}
		}
	}

	public void onBroken(Level level, BlockPos pos) {
		for (ItemStack stack : inputItems) {
			Block.popResource(level, pos, stack);
		}

		for (ItemStack stack : outputItems) {
			Block.popResource(level, pos, stack);
		}
	}

	public void initProperties() {
		energyCapacity = electricBlockInstance.energyCapacity;
		maxEnergyOutput = electricBlockInstance.maxEnergyOutput;
		maxInputEnergy = electricBlockInstance.maxEnergyInput;
		autoEject = false;
	}

	/**
	 * In every instance initProperties() should be called first
	 */
	public void upgradesChanged() {
	}

	@Override
	public int getCount() {
		return 1;
	}

	@Override
	public int get(int id) {
		if (id == 0) {
			return energy == 0 ? 0 : Mth.clamp(Mth.ceil(energy * 30000D / energyCapacity), 0, 30000);
		}

		return 0;
	}

	@Override
	public void set(int id, int value) {
	}

	@Override
	public final boolean canBurn() {
		return electricBlockInstance.canBurn;
	}

	@Override
	public final void setBurnt(boolean b) {
		if (burnt != b && !level.isClientSide() && canBurn()) {
			burnt = b;
			setChanged();
			syncBlock();
			electricNetworkUpdated(level, worldPosition);

			if (burnt) {
				level.levelEvent(1502, worldPosition, 0);

				if (electricBlockInstance.canBeActive) {
					level.setBlock(worldPosition, getBlockState().setValue(ElectricBlock.ACTIVE, false), 3);
				}
			}
		}
	}

	@Override
	public final boolean isBurnt() {
		return burnt;
	}

	public void stepOn(ServerPlayer player) {
	}

	public void spawnActiveParticles(Level level, double x, double y, double z, BlockState state, Random r) {
	}

	public Direction getFacing(Direction def) {
		if (electricBlockInstance.facingProperty == null) {
			return def;
		}

		BlockState state = getBlockState();

		if (state.getBlock() instanceof ElectricBlock) {
			return state.getValue(electricBlockInstance.facingProperty);
		}

		return def;
	}

	public void onPlacedBy(@Nullable LivingEntity entity, ItemStack stack) {
		if (savePlacer()) {
			if (entity != null) {
				placerId = entity.getUUID();
				placerName = entity.getScoreboardName();
			} else {
				level.removeBlock(worldPosition, false);
			}
		}
	}

	public boolean savePlacer() {
		return false;
	}

	public void syncBlock() {
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 11);
		setChanged();
	}
}
