package dev.ftb.mods.ftbic.screen;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class PickaxeSlot extends Slot {
	public PickaxeSlot(Container container, int index, int x, int y) {
		super(container, index, x, y);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return stack.is(ItemTags.PICKAXES);
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		return 1;
	}
}
