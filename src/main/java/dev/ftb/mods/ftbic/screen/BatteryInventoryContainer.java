package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.machine.BatteryInventory;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class BatteryInventoryContainer implements Container {
	private final BatteryInventory inv;

	public BatteryInventoryContainer(BatteryInventory inv) {
		this.inv = inv;
	}

	@Override
	public int getContainerSize() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return inv.getStackInSlot(0).isEmpty();
	}

	@Override
	public ItemStack getItem(int slot) {
		return slot == 0 ? inv.getStackInSlot(0) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItem(int slot, int count) {
		ItemStack s = getItem(slot);
		if (s.isEmpty() || count <= 0) return ItemStack.EMPTY;
		ItemStack taken = s.copy();
		taken.setCount(Math.min(count, s.getCount()));
		s.shrink(taken.getCount());
		if (s.isEmpty()) inv.setStackInSlot(0, ItemStack.EMPTY);
		setChanged();
		return taken;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		ItemStack s = getItem(slot);
		if (s.isEmpty()) return ItemStack.EMPTY;
		inv.setStackInSlot(0, ItemStack.EMPTY);
		return s;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		if (slot != 0) return;
		inv.setStackInSlot(0, stack);
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
		inv.setStackInSlot(0, ItemStack.EMPTY);
		setChanged();
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return slot == 0 && inv.isItemValid(0, stack);
	}
}
