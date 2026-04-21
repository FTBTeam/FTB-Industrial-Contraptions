package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.util.EnergyItemHandler;
import net.minecraft.world.item.ItemStack;

public class BatteryInventory {
	public final ElectricBlockEntity entity;
	public final boolean charge;
	private ItemStack stack = ItemStack.EMPTY;

	public BatteryInventory(ElectricBlockEntity e, boolean c) {
		entity = e;
		charge = c;
	}

	public ItemStack getStackInSlot(int slot) {
		return stack;
	}

	public void setStackInSlot(int slot, ItemStack s) {
		stack = s == null ? ItemStack.EMPTY : s;
		entity.setChanged();
	}

	public void loadItem(ItemStack s) {
		stack = s == null ? ItemStack.EMPTY : s;
	}

	public boolean isItemValid(int slot, ItemStack s) {
		if (!(s.getItem() instanceof EnergyItemHandler handler)) return false;
		return charge ? handler.canInsertEnergy() : handler.canExtractEnergy();
	}

	public int getSlotLimit(int slot) {
		return 1;
	}
}
