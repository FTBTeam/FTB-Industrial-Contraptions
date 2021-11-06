package dev.ftb.mods.ftbic.screen;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class SimpleItemHandlerSlot extends SlotItemHandler {
	public SimpleItemHandlerSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	@Override
	public boolean mayPickup(Player playerIn) {
		return true;
	}

	@Override
	@Nonnull
	public ItemStack remove(int amount) {
		ItemStack stack = this.getItem();
		int possible = Math.min(amount, stack.getCount());
		ItemStack returned = ItemHandlerHelper.copyStackWithSize(stack, possible);
		stack.shrink(possible);
		return returned;
	}
}
