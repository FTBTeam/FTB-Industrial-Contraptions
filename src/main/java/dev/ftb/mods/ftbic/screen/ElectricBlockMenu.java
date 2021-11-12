package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ElectricBlockMenu<E extends ElectricBlockEntity> extends AbstractContainerMenu {
	public final E entity;
	public final Player player;
	public final ContainerData containerData;
	private final int slotCount;

	public ElectricBlockMenu(MenuType<?> type, int id, Inventory playerInv, E r, ContainerData d, @Nullable Object extra) {
		super(type, id);
		entity = r;
		player = playerInv.player;
		containerData = d;

		int prevSlotCount = slots.size();
		addBlockSlots(extra);
		slotCount = slots.size() - prevSlotCount;

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				addSlot(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
			}
		}

		for (int x = 0; x < 9; x++) {
			addSlot(new Slot(playerInv, x, 8 + x * 18, 142));
		}

		addDataSlots(containerData);
	}

	public void addBlockSlots(@Nullable Object extra) {
	}

	@Override
	public ItemStack quickMoveStack(Player player, int slotId) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = slots.get(slotId);

		if (slot != null && slot.hasItem()) {
			ItemStack stack2 = slot.getItem();
			stack = stack2.copy();

			if (slotId < slotCount) {
				if (!moveItemStackTo(stack2, slotCount, slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!moveItemStackTo(stack2, 0, slotCount, false)) {
				return ItemStack.EMPTY;
			}

			if (stack2.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}

		return stack;
	}

	@Override
	public boolean stillValid(Player player) {
		return !entity.isRemoved() && !entity.isBurnt();
	}

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();
	}

	@Override
	public boolean clickMenuButton(Player player, int button) {
		return false;
	}
}
