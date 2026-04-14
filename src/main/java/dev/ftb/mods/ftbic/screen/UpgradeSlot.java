package dev.ftb.mods.ftbic.screen;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class UpgradeSlot extends Slot {
	public UpgradeSlot(Container container, int index, int x, int y) {
		super(container, index, x, y);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return container.canPlaceItem(getContainerSlot(), stack);
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		return Math.min(stack.getMaxStackSize(), container.getMaxStackSize());
	}
}
