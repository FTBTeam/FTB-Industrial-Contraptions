package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.QuarryBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class QuarryMenu extends ElectricBlockMenu {
	public final DataSlot pausedSlot = DataSlot.standalone();

	public QuarryMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
		super(FTBICMenus.QUARRY.get(), id, playerInv, buf);
		addDataSlot(pausedSlot);
	}

	public QuarryMenu(int id, Inventory playerInv, ElectricBlockEntity be) {
		super(FTBICMenus.QUARRY.get(), id, playerInv, be);
		addDataSlot(pausedSlot);
	}

	@Override
	protected void addMachineSlots(Inventory playerInv) {
		if (blockEntity == null || blockEntity.getSlotCount() == 0) {
			machineSlotCount = 0;
			return;
		}
		ElectricBlockEntityContainer container = new ElectricBlockEntityContainer(blockEntity);
		int inputs = blockEntity.inputItems.length;
		int outputs = blockEntity.outputItems.length;
		for (int i = 0; i < outputs; i++) {
			int row = i / 6, col = i % 6;
			addSlot(new OutputSlot(container, inputs + i, 8 + col * 18, 17 + row * 18));
		}
		machineSlotCount = inputs + outputs;
		if (blockEntity instanceof QuarryBlockEntity q) {
			addSlot(new PickaxeSlot(q.pickaxeContainer(), 0, 125, 35));
			machineSlotCount++;
		}
		addBatterySlot(125, 53);
		addUpgradeSlots(152);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		Slot slot = slots.get(index);
		if (!slot.hasItem()) return ItemStack.EMPTY;
		ItemStack stack = slot.getItem();
		if (index >= machineSlotCount && stack.is(ItemTags.PICKAXES)) {
			for (int i = 0; i < machineSlotCount; i++) {
				if (slots.get(i) instanceof PickaxeSlot pickaxe && pickaxe.mayPlace(stack)) {
					ItemStack orig = stack.copy();
					if (moveItemStackTo(stack, i, i + 1, false)) {
						if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
						else slot.setChanged();
						return orig;
					}
					break;
				}
			}
		}
		return super.quickMoveStack(player, index);
	}

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();
		if (blockEntity instanceof QuarryBlockEntity q) {
			pausedSlot.set(q.isEffectivelyPaused() ? 1 : 0);
		}
	}

	public boolean isPaused() { return pausedSlot.get() == 1; }

	@Override
	public boolean clickMenuButton(Player player, int id) {
		if (id == 0 && blockEntity instanceof QuarryBlockEntity q) {
			if (q.redstonePaused) {
				if (player instanceof net.minecraft.server.level.ServerPlayer sp) {
					sp.sendSystemMessage(net.minecraft.network.chat.Component.literal(
							"Pause is currently overridden by an active redstone signal"), true);
				}
				return true;
			}
			q.paused = !q.paused;
			q.setChanged();
			return true;
		}
		return super.clickMenuButton(player, id);
	}
}
