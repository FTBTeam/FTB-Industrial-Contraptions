package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Adapter exposing an ElectricBlockEntity's `inputItems` + `outputItems` arrays as a vanilla Container
 * so Slot objects can read and write them. Slot indices 0..inputs-1 map to inputs; inputs..end map
 * to outputs.
 */
public class ElectricBlockEntityContainer implements Container {
	private final ElectricBlockEntity be;

	public ElectricBlockEntityContainer(ElectricBlockEntity be) {
		this.be = be;
	}

	@Override
	public int getContainerSize() {
		return be.getSlotCount();
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < getContainerSize(); i++) {
			if (!getItem(i).isEmpty()) return false;
		}
		return true;
	}

	@Override
	public ItemStack getItem(int slot) {
		if (slot < 0 || slot >= getContainerSize()) return ItemStack.EMPTY;
		return be.getStackInSlot(slot);
	}

	@Override
	public ItemStack removeItem(int slot, int count) {
		ItemStack stack = getItem(slot);
		if (stack.isEmpty() || count <= 0) return ItemStack.EMPTY;
		ItemStack taken = stack.copy();
		taken.setCount(Math.min(count, stack.getCount()));
		stack.shrink(taken.getCount());
		if (stack.isEmpty()) {
			be.setStackInSlot(slot, ItemStack.EMPTY);
		}
		setChanged();
		return taken;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		ItemStack stack = getItem(slot);
		if (stack.isEmpty()) return ItemStack.EMPTY;
		be.setStackInSlot(slot, ItemStack.EMPTY);
		return stack;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		if (slot < 0 || slot >= getContainerSize()) return;
		be.setStackInSlot(slot, stack);
		setChanged();
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return be.isItemValid(slot, stack);
	}

	@Override
	public void setChanged() {
		be.setChanged();
	}

	@Override
	public boolean stillValid(Player player) {
		return !be.isRemoved();
	}

	@Override
	public void clearContent() {
		for (int i = 0; i < getContainerSize(); i++) {
			be.setStackInSlot(i, ItemStack.EMPTY);
		}
		setChanged();
	}

	public int inputCount() {
		return be.inputItems.length;
	}
}
