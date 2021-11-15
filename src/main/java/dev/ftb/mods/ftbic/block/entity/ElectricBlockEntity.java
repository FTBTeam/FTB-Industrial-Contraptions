package dev.ftb.mods.ftbic.block.entity;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.ElectricBlock;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.recipe.RecipeCache;
import dev.ftb.mods.ftbic.util.EnergyHandler;
import dev.ftb.mods.ftbic.util.EnergyTier;
import dev.ftb.mods.ftbic.util.OpenMenuFactory;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Random;
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

	private boolean changed;
	public double energy;
	public double energyAdded;
	public final ItemStack[] inputItems;
	public final ItemStack[] outputItems;
	private LazyOptional<?> thisOptional;
	public boolean active;
	private int changeStateTicks;
	private boolean burnt;

	public double energyCapacity;
	public double maxEnergyOutput;
	public EnergyTier inputEnergyTier;

	public ElectricBlockEntity(BlockEntityType<?> type, int inItems, int outItems) {
		super(type);
		changed = false;
		energy = 0;
		energyAdded = 0;
		inputItems = new ItemStack[inItems];
		outputItems = new ItemStack[outItems];
		Arrays.fill(inputItems, ItemStack.EMPTY);
		Arrays.fill(outputItems, ItemStack.EMPTY);

		if (inputItems.length + outputItems.length > 127) {
			throw new RuntimeException("Internal inventory of " + type.getRegistryName() + " too large!");
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
		if (level != null && !level.isClientSide()) {
			initProperties();
			upgradesChanged();
		} else if (level != null) {
			level.tickableBlockEntities.remove(this);
		}

		super.onLoad();
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

	protected void handleEnergyInput() {
		if (isBurnt() || level.isClientSide()) {
			return;
		}

		if (inputEnergyTier != null && energyAdded > 0) {
			if (energyAdded > inputEnergyTier.transferRate) {
				setBurnt(true);
			} else if (energy < energyCapacity) {
				double e = Math.min(energyAdded, energyCapacity - energy);

				if (e > 0D) {
					energy += e;
					energyChanged(energy - e);
				}
			}

			energyAdded = 0;
		}
	}

	protected void handleChanges() {
		if (changeStateTicks > 0) {
			changeStateTicks--;
		}

		if (changeStateTicks <= 0) {
			if (!isBurnt()) {
				ElectricBlockInstance ebi = ((ElectricBlock) getBlockState().getBlock()).electricBlockInstance;

				if (ebi.canBeActive && getBlockState().getValue(ElectricBlock.ACTIVE) != active) {
					level.setBlock(worldPosition, getBlockState().setValue(ElectricBlock.ACTIVE, active), 3);
					setChanged();
				}

				changeStateTicks = FTBICConfig.STATE_UPDATE_TICKS;
				active = false;
			}

			if (changed) {
				setChangedNow();
			}
		}
	}

	@Override
	public void tick() {
		handleEnergyInput();
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
	public double insertEnergy(double maxInsert, boolean simulate) {
		if (getInputEnergyTier() == null) {
			return 0;
		}

		if (!simulate) {
			energyAdded += maxInsert;
		}

		return maxInsert;
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
		if (!level.isClientSide()) {
			player.sendMessage(new TextComponent("No GUI yet!"), Util.NIL_UUID);
		}

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
	@Nullable
	public final EnergyTier getInputEnergyTier() {
		return inputEnergyTier;
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

	public void onBroken(Level level, BlockPos pos) {
		for (ItemStack stack : inputItems) {
			Block.popResource(level, pos, stack);
		}

		for (ItemStack stack : outputItems) {
			Block.popResource(level, pos, stack);
		}
	}

	public void initProperties() {
		energyCapacity = 40000;
		maxEnergyOutput = 0D;
		inputEnergyTier = null;
	}

	/**
	 * In every instance initProperties() should be called first
	 */
	public void upgradesChanged() {
	}

	@Override
	public int getCount() {
		return 0;
	}

	@Override
	public int get(int id) {
		return 0;
	}

	@Override
	public void set(int id, int value) {
	}

	@Override
	public final void setBurnt(boolean b) {
		if (burnt != b && !level.isClientSide()) {
			ElectricBlockInstance ebi = ((ElectricBlock) getBlockState().getBlock()).electricBlockInstance;

			if (ebi.canBurn) {
				burnt = b;
				setChanged();
				level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 11);
				electricNetworkUpdated(level, worldPosition);

				if (burnt) {
					level.levelEvent(1502, worldPosition, 0);

					if (ebi.canBeActive) {
						level.setBlock(worldPosition, getBlockState().setValue(ElectricBlock.ACTIVE, false), 3);
					}
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
}
