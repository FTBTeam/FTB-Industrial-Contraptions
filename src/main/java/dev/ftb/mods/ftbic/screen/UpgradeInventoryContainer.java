package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.machine.UpgradeInventory;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class UpgradeInventoryContainer implements Container {
	private final UpgradeInventory inv;

	public UpgradeInventoryContainer(UpgradeInventory inv) {
		this.inv = inv;
	}

	@Override
	public int getContainerSize() {
		return inv.getSlots();
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < inv.getSlots(); i++) {
			if (!inv.getStackInSlot(i).isEmpty()) return false;
		}
		return true;
	}

	@Override
	public ItemStack getItem(int slot) {
		if (slot < 0 || slot >= inv.getSlots()) return ItemStack.EMPTY;
		return inv.getStackInSlot(slot);
	}

	@Override
	public ItemStack removeItem(int slot, int count) {
		ItemStack stack = getItem(slot);
		if (stack.isEmpty() || count <= 0) return ItemStack.EMPTY;
		ItemStack taken = stack.copy();
		taken.setCount(Math.min(count, stack.getCount()));
		stack.shrink(taken.getCount());
		if (stack.isEmpty()) {
			inv.setStackInSlot(slot, ItemStack.EMPTY);
		}
		setChanged();
		return taken;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		ItemStack stack = getItem(slot);
		if (stack.isEmpty()) return ItemStack.EMPTY;
		inv.setStackInSlot(slot, ItemStack.EMPTY);
		return stack;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		if (slot < 0 || slot >= inv.getSlots()) return;
		inv.setStackInSlot(slot, stack);
		setChanged();
	}

	@Override
	public void setChanged() {
		inv.entity.setChanged();
	}

	@Override
	public boolean stillValid(Player player) {
		return !inv.entity.isRemoved();
	}

	@Override
	public void clearContent() {
		for (int i = 0; i < inv.getSlots(); i++) {
			inv.setStackInSlot(i, ItemStack.EMPTY);
		}
		setChanged();
	}

	@Override
	public int getMaxStackSize() {
		return inv.limit;
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return inv.isItemValid(slot, stack);
	}
}
