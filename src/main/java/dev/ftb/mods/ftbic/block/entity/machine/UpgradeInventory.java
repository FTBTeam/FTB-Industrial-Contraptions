package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.item.UpgradeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class UpgradeInventory extends ItemStackHandler {
	public final ElectricBlockEntity entity;

	public UpgradeInventory(ElectricBlockEntity e) {
		super(4);
		entity = e;
	}

	@Override
	protected void onContentsChanged(int slot) {
		entity.setChanged();
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return stack.getItem() instanceof UpgradeItem;
	}

	@Override
	public int getSlotLimit(int slot) {
		return 1;
	}

	public int count(Item item) {
		int count = 0;

		for (int i = 0; i < getSlots(); i++) {
			if (getStackInSlot(i).getItem() == item) {
				count++;
			}
		}

		return count;
	}

	public boolean contains(Item item) {
		for (int i = 0; i < getSlots(); i++) {
			if (getStackInSlot(i).getItem() == item) {
				return true;
			}
		}

		return false;
	}
}
