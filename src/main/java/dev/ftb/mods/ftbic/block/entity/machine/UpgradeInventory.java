package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.item.UpgradeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class UpgradeInventory extends ItemStackHandler {
	public final ElectricBlockEntity entity;
	public final int limit;

	public UpgradeInventory(ElectricBlockEntity e, int slots, int stackLimit) {
		super(slots);
		entity = e;
		limit = stackLimit;
	}

	@Override
	protected void onContentsChanged(int slot) {
		if (entity.hasLevel() && !entity.getLevel().isClientSide()) {
			entity.initProperties();
			entity.upgradesChanged();
			entity.setChanged();
		}
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return stack.getItem() instanceof UpgradeItem;
	}

	@Override
	public int getSlotLimit(int slot) {
		return limit;
	}

	public int countUpgrades(Item item) {
		int count = 0;

		for (int i = 0; i < getSlots(); i++) {
			ItemStack stack = getStackInSlot(i);

			if (stack.getItem() == item) {
				count += stack.getCount();
			}
		}

		return count;
	}
}
