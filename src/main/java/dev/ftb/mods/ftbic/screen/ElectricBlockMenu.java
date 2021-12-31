package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.screen.sync.SyncedData;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ElectricBlockMenu<E extends ElectricBlockEntity> extends AbstractContainerMenu {
	public final E entity;
	public final Player player;
	public final SyncedData data;
	private final int slotCount;

	public ElectricBlockMenu(MenuType<?> type, int id, Inventory playerInv, E r, @Nullable Object extra) {
		super(type, id);
		entity = r;
		player = playerInv.player;
		data = new SyncedData();

		int prevSlotCount = slots.size();
		addBlockSlots(extra);
		slotCount = slots.size() - prevSlotCount;

		int playerSlotOffset = getPlayerSlotOffset();

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				addSlot(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, playerSlotOffset + y * 18));
			}
		}

		for (int x = 0; x < 9; x++) {
			addSlot(new Slot(playerInv, x, 8 + x * 18, playerSlotOffset + 58));
		}

		entity.addSyncData(data);
		data.setup();
		addDataSlots(data);
	}

	public int getPlayerSlotOffset() {
		return 84;
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
		if (entity.hasLevel() && !entity.getLevel().isClientSide()) {
			data.update();
		}

		super.broadcastChanges();
	}

	@Override
	public boolean clickMenuButton(Player player, int button) {
		return false;
	}
}
