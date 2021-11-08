package dev.ftb.mods.ftbic.block.entity;

import dev.ftb.mods.ftbic.block.ElectricBlock;
import dev.ftb.mods.ftbic.block.ElectricBlockState;
import dev.ftb.mods.ftbic.recipe.RecipeCache;
import dev.ftb.mods.ftbic.util.PowerTier;
import dev.ftb.mods.ftbic.util.TieredEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

public class ElectricBlockEntity extends BlockEntity implements TickableBlockEntity, TieredEnergyStorage, IItemHandlerModifiable {
	private static final AtomicLong ELECTRIC_NETWORK_CHANGES = new AtomicLong(0L);

	public static void electricNetworkUpdated(LevelAccessor level, BlockPos pos) {
		// TODO: Possibly implement some kind of localized network change counter. But for now, this works
		ELECTRIC_NETWORK_CHANGES.incrementAndGet();
	}

	public static long getCurrentElectricNetwork(LevelAccessor level, BlockPos pos) {
		return ELECTRIC_NETWORK_CHANGES.get();
	}

	private boolean changed;
	public int energy;
	public int energyAdded;
	public final ItemStack[] inputItems;
	public final ItemStack[] outputItems;
	private LazyOptional<?> thisOptional;
	public ElectricBlockState changeState;

	public int energyCapacity;
	public PowerTier outputPowerTier;
	public PowerTier inputPowerTier;

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
		changeState = null;
	}

	public void writeData(CompoundTag tag) {
		tag.putInt("Energy", energy);

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
	}

	public void readData(CompoundTag tag) {
		energy = tag.getInt("Energy");

		if (inputItems.length + outputItems.length > 0) {
			Arrays.fill(inputItems, ItemStack.EMPTY);
			Arrays.fill(outputItems, ItemStack.EMPTY);

			ListTag inv = tag.getList("Inventory", Constants.NBT.TAG_COMPOUND);

			for (int i = 0; i < inv.size(); i++) {
				CompoundTag tag1 = inv.getCompound(i);
				setStackInSlot(tag1.getByte("Slot"), ItemStack.of(tag1));
			}
		}
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
		if (cap == CapabilityEnergy.ENERGY) {
			return getThisOptional().cast();
		} else if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (inputItems.length + outputItems.length) > 0) {
			return getThisOptional().cast();
		}

		return super.getCapability(cap, side);
	}

	protected void handleEnergyInput() {
		if (level.isClientSide()) {
			return;
		}

		if (inputPowerTier != null && energyAdded > 0) {
			if (energyAdded > inputPowerTier.transferRate) {
				// TODO: Burn the machine if config is enabled
			}

			if (energy < energyCapacity) {
				int e = Math.min(energyAdded, energyCapacity - energy);

				if (e > 0) {
					energy += e;
					energyChanged(energy - e);
				}
			}

			energyAdded = 0;
		}
	}

	protected void handleChanges() {
		if (changeState != null && level.getGameTime() % 8L == (hashCode() & 7L)) {
			if (getBlockState().getValue(((ElectricBlock) getBlockState().getBlock()).electricBlockInstance.stateProperty) != changeState) {
				level.setBlock(worldPosition, getBlockState().setValue(((ElectricBlock) getBlockState().getBlock()).electricBlockInstance.stateProperty, changeState), 3);
				setChanged();
			}

			changeState = null;
		}

		if (changed) {
			changed = false;
			level.blockEntityChanged(worldPosition, this);
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

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		if (!canReceive()) {
			return 0;
		}

		int energyReceived = Math.min(energyCapacity - energy, maxReceive);

		if (!simulate) {
			energyAdded += energyReceived;
		}

		return energyReceived;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		if (!canExtract()) {
			return 0;
		}

		int energyExtracted = Math.min(energy, maxExtract);

		if (!simulate) {
			energy -= energyExtracted;
			energyChanged(energy + energyExtracted);
		}

		return energyExtracted;
	}

	@Override
	public int getEnergyStored() {
		return energy;
	}

	@Override
	public int getMaxEnergyStored() {
		return energyCapacity;
	}

	@Override
	public boolean canExtract() {
		return outputPowerTier != null;
	}

	@Override
	public boolean canReceive() {
		return inputPowerTier != null;
	}

	public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
		return InteractionResult.PASS;
	}

	@Override
	@Nullable
	public final PowerTier getInputPowerTier() {
		return inputPowerTier;
	}

	@Nullable
	@Override
	public PowerTier getOutputPowerTier() {
		return outputPowerTier;
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
		outputPowerTier = null;
		inputPowerTier = null;
	}

	/**
	 * In every instance initProperties() should be called first
	 */
	public void upgradesChanged() {
	}
}
