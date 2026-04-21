package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.MachineBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import dev.ftb.mods.ftbic.block.entity.machine.BasicMachineBlockEntity;

public abstract class ElectricBlockMenu extends AbstractContainerMenu {
	@Nullable
	public final ElectricBlockEntity blockEntity;

	public final DataSlot energyScaled = DataSlot.standalone();
	public final DataSlot progressScaled = DataSlot.standalone();
	public final DataSlot maxProgressScaled = DataSlot.standalone();

	protected int machineSlotCount;

	protected ElectricBlockMenu(MenuType<?> type, int id, Inventory playerInv, @Nullable ElectricBlockEntity be) {
		super(type, id);
		this.blockEntity = be;
		addMachineSlots(playerInv);
		addPlayerInventorySlots(playerInv);
		addDataSlot(energyScaled);
		addDataSlot(progressScaled);
		addDataSlot(maxProgressScaled);
	}

	protected ElectricBlockMenu(MenuType<?> type, int id, Inventory playerInv, FriendlyByteBuf buf) {
		this(type, id, playerInv, resolve(playerInv, buf));
	}

	@Nullable
	private static ElectricBlockEntity resolve(Inventory inv, FriendlyByteBuf buf) {
		if (buf == null) return null;
		var pos = buf.readBlockPos();
		return inv.player.level().getBlockEntity(pos) instanceof ElectricBlockEntity be ? be : null;
	}

	protected void addMachineSlots(Inventory playerInv) {
		if (blockEntity == null || blockEntity.getSlotCount() == 0) {
			machineSlotCount = 0;
			return;
		}
		ElectricBlockEntityContainer container = new ElectricBlockEntityContainer(blockEntity);

		int inputs = blockEntity.inputItems.length;
		int outputs = blockEntity.outputItems.length;

		int inputCols = Math.max(1, Math.min(2, inputs));
		int inputXStart = 60 - (inputCols - 1) * 18;
		int inputRows = Math.max(1, (int) Math.ceil(inputs / (double) inputCols));
		int yStart = 35 - ((inputRows - 1) * 9);

		for (int i = 0; i < inputs; i++) {
			int col = i % inputCols;
			int row = i / inputCols;
			addSlot(new Slot(container, i, inputXStart + col * 18, yStart + row * 18));
		}

		int outputCols = Math.max(1, Math.min(2, outputs));
		int outputRows = Math.max(1, (int) Math.ceil(outputs / (double) outputCols));
		int oyStart = 35 - ((outputRows - 1) * 9);
		for (int i = 0; i < outputs; i++) {
			int col = i % outputCols;
			int row = i / outputCols;
			addSlot(new OutputSlot(container, inputs + i, 108 + col * 18, oyStart + row * 18));
		}

		machineSlotCount = inputs + outputs;
	}

	protected void addUpgradeSlots(int x) {
		addUpgradeSlots(x, 8);
	}

	protected void addUpgradeSlots(int x, int yStart) {
		if (!(blockEntity instanceof BasicMachineBlockEntity bm)) return;
		UpgradeInventoryContainer c = new UpgradeInventoryContainer(bm.upgradeInventory);
		for (int i = 0; i < bm.upgradeInventory.getSlots(); i++) {
			addSlot(new UpgradeSlot(c, i, x, yStart + i * 18));
			machineSlotCount++;
		}
	}

	protected void addBatterySlot(int x, int y) {
		if (!(blockEntity instanceof BasicMachineBlockEntity bm)) return;
		BatteryInventoryContainer c = new BatteryInventoryContainer(bm.batteryInventory);
		addSlot(new BatterySlot(c, 0, x, y));
		machineSlotCount++;
	}

	protected static class OutputSlot extends Slot {
		public OutputSlot(Container container, int index, int x, int y) {
			super(container, index, x, y);
		}

		@Override
		public boolean mayPlace(ItemStack stack) {
			return false;
		}
	}

	protected int getPlayerSlotOffset() {
		return 84;
	}

	protected void addPlayerInventorySlots(Inventory inv) {
		int off = getPlayerSlotOffset();
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, off + row * 18));
			}
		}
		for (int col = 0; col < 9; col++) {
			addSlot(new Slot(inv, col, 8 + col * 18, off + 58));
		}
	}

	@Override
	public void broadcastChanges() {
		if (blockEntity != null) {
			energyScaled.set(blockEntity.getEnergyCapacity() <= 0D ? 0
					: (int) Math.round(1000D * blockEntity.getEnergy() / blockEntity.getEnergyCapacity()));
			if (blockEntity instanceof MachineBlockEntity m) {
				progressScaled.set(m.progress);
				maxProgressScaled.set(m.maxProgress);
			}
		}
		super.broadcastChanges();
	}

	public float getEnergyFraction() {
		return energyScaled.get() / 1000F;
	}

	public float getProgressFraction() {
		int max = maxProgressScaled.get();
		if (max <= 0) return 0F;
		return Math.min(1F, progressScaled.get() / (float) max);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		Slot slot = slots.get(index);
		if (!slot.hasItem()) return ItemStack.EMPTY;

		ItemStack stack = slot.getItem();
		ItemStack orig = stack.copy();

		int machineEnd = machineSlotCount;
		int playerStart = machineEnd;
		int playerEnd = slots.size();

		if (index < machineEnd) {
			if (!moveItemStackTo(stack, playerStart, playerEnd, true)) return ItemStack.EMPTY;
		} else {
			int batteryStart = -1, batteryEnd = -1;
			int upgradeStart = -1, upgradeEnd = -1;
			for (int i = 0; i < machineEnd; i++) {
				Slot s = slots.get(i);
				if (s instanceof BatterySlot) {
					if (batteryStart < 0) batteryStart = i;
					batteryEnd = i + 1;
				} else if (s instanceof UpgradeSlot) {
					if (upgradeStart < 0) upgradeStart = i;
					upgradeEnd = i + 1;
				}
			}
			int inputEnd = blockEntity == null ? 0 : blockEntity.inputItems.length;
			boolean moved = false;
			if (batteryStart >= 0 && slots.get(batteryStart).mayPlace(stack)) {
				moved = moveItemStackTo(stack, batteryStart, batteryEnd, false);
			}
			if (!moved && upgradeStart >= 0 && slots.get(upgradeStart).mayPlace(stack)) {
				moved = moveItemStackTo(stack, upgradeStart, upgradeEnd, false);
			}
			if (!moved && inputEnd > 0) {
				moved = moveItemStackTo(stack, 0, inputEnd, false);
			}
			if (!moved) return ItemStack.EMPTY;
		}

		if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
		else slot.setChanged();
		if (stack.getCount() == orig.getCount()) return ItemStack.EMPTY;
		return orig;
	}

	@Override
	public boolean stillValid(Player player) {
		return blockEntity != null && !blockEntity.isRemoved();
	}
}
