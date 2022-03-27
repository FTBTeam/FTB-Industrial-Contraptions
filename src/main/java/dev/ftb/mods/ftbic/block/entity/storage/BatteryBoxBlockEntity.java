package dev.ftb.mods.ftbic.block.entity.storage;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.block.entity.generator.GeneratorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.BatteryInventory;
import dev.ftb.mods.ftbic.screen.BatteryBoxMenu;
import dev.ftb.mods.ftbic.util.EnergyItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BatteryBoxBlockEntity extends GeneratorBlockEntity {
	public final BatteryInventory dischargeBatteryInventory;
	private LazyOptional<IItemHandler> dischargeBatteryInventoryOptional;
	private LazyOptional<IItemHandler> chargeBatteryInventoryOptional;

	public BatteryBoxBlockEntity(ElectricBlockInstance type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		dischargeBatteryInventory = new BatteryInventory(this, false);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		maxEnergyOutputTransfer = maxEnergyOutput;
	}

	@Override
	public void writeData(CompoundTag tag) {
		super.writeData(tag);

		if (!dischargeBatteryInventory.getStackInSlot(0).isEmpty()) {
			tag.put("DischargeBattery", dischargeBatteryInventory.getStackInSlot(0).serializeNBT());
		}
	}

	@Override
	public void readData(CompoundTag tag) {
		super.readData(tag);

		if (tag.contains("DischargeBattery")) {
			dischargeBatteryInventory.loadItem(ItemStack.of(tag.getCompound("DischargeBattery")));
		} else {
			dischargeBatteryInventory.loadItem(ItemStack.EMPTY);
		}
	}

	@Override
	public void onBroken(Level level, BlockPos pos) {
		super.onBroken(level, pos);

		Block.popResource(level, pos, dischargeBatteryInventory.getStackInSlot(0));
	}

	@Override
	public boolean isValidEnergyOutputSide(Direction direction) {
		return direction == getFacing(Direction.NORTH);
	}

	@Override
	public boolean isValidEnergyInputSide(Direction direction) {
		return direction != getFacing(Direction.NORTH);
	}

	@Override
	public void handleGeneration() {
		if (!isBurnt() && !level.isClientSide() && energy < energyCapacity) {
			ItemStack battery = dischargeBatteryInventory.getStackInSlot(0);

			if (!battery.isEmpty() && battery.getItem() instanceof EnergyItemHandler) {
				EnergyItemHandler item = (EnergyItemHandler) battery.getItem();
				double transfer = item.isCreativeEnergyItem() ? Double.POSITIVE_INFINITY : maxInputEnergy * FTBICConfig.MACHINES.ITEM_TRANSFER_EFFICIENCY.get();
				double e = item.extractEnergy(battery, Math.min(energyCapacity - energy, transfer), false);

				if (e > 0) {
					energy += e;

					if (battery.isEmpty()) {
						dischargeBatteryInventory.setStackInSlot(0, ItemStack.EMPTY);
					}

					setChanged();
				}
			}
		}
	}

	@Override
	public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide()) {
			openMenu((ServerPlayer) player, (id, inventory) -> new BatteryBoxMenu(id, inventory, this));
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public int getSlots() {
		return 2;
	}

	@NotNull
	@Override
	public ItemStack getStackInSlot(int slot) {
		switch (slot) {
			case 0:
				return chargeBatteryInventory.getStackInSlot(0);
			case 1:
				return dischargeBatteryInventory.getStackInSlot(0);
			default:
				throw new RuntimeException("Slot " + slot + " not in valid range - [0," + getSlots() + ")");
		}
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		ItemStack prev;
		switch (slot) {
			case 0:
				prev = chargeBatteryInventory.getStackInSlot(0);
				chargeBatteryInventory.setStackInSlot(0, stack);
				break;
			case 1:
				prev = dischargeBatteryInventory.getStackInSlot(0);
				dischargeBatteryInventory.setStackInSlot(0, stack);
				break;
			default:
				throw new RuntimeException("Slot " + slot + " not in valid range - [0," + getSlots() + ")");
		}
		inventoryChanged(slot, prev);
	}

	@NotNull
	@Override
	public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		if (slot >= getSlots() || stack.isEmpty() || !isItemValid(slot, stack)) {
			return stack;
		}

		ItemStack existing = getStackInSlot(slot);
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
				ItemStack prev = getStackInSlot(slot);
				switch (slot) {
					case 0:
						chargeBatteryInventory.setStackInSlot(0, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
						break;
					case 1:
						dischargeBatteryInventory.setStackInSlot(0, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
						break;
				}
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
		if (slot < 0 || slot > 1) {
			return ItemStack.EMPTY;
		}

		BatteryInventory batteryInv = slot == 0 ? chargeBatteryInventory : dischargeBatteryInventory;
		ItemStack existing = batteryInv.getStackInSlot(0);

		if (existing.isEmpty()) {
			return ItemStack.EMPTY;
		}

		int toExtract = Math.min(amount, existing.getMaxStackSize());

		if (existing.getCount() <= toExtract) {
			if (!simulate) {
				batteryInv.setStackInSlot(0, ItemStack.EMPTY);
				inventoryChanged(slot, existing);
				return existing;
			} else {
				return existing.copy();
			}
		} else {
			if (!simulate) {
				batteryInv.setStackInSlot(0, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
				inventoryChanged(slot, existing);
			}

			return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
		}
	}

	@Override
	public int getSlotLimit(int slot) {
		return 64;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return (slot == 0 && chargeBatteryInventory.isItemValid(0, stack)) || (slot == 1 && dischargeBatteryInventory.isItemValid(0, stack));
	}

	public LazyOptional<?> getDischargeBatteryInventoryOptional() {
		if (dischargeBatteryInventoryOptional == null) {
			dischargeBatteryInventoryOptional = LazyOptional.of(() -> dischargeBatteryInventory);
		}

		return dischargeBatteryInventoryOptional;
	}

	public LazyOptional<?> getChargeBatteryInventoryOptional() {
		if (chargeBatteryInventoryOptional == null) {
			chargeBatteryInventoryOptional = LazyOptional.of(() -> chargeBatteryInventory);
		}

		return chargeBatteryInventoryOptional;
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();

		if (dischargeBatteryInventoryOptional != null) {
			dischargeBatteryInventoryOptional.invalidate();
			dischargeBatteryInventoryOptional = null;
		}

		if (chargeBatteryInventoryOptional != null) {
			chargeBatteryInventoryOptional.invalidate();
			chargeBatteryInventoryOptional = null;
		}
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (side == Direction.UP) {
				return getDischargeBatteryInventoryOptional().cast();
			}

			return getChargeBatteryInventoryOptional().cast();
		}

		return super.getCapability(cap, side);
	}

}
