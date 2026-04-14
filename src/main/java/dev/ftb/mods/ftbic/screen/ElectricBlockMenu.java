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

/**
 * Base menu for electric block GUIs. Adds synced data slots (energy, progress) and the standard
 * player inventory + hotbar grid at y=84/142. Subclasses add their own machine-specific slots.
 */
public abstract class ElectricBlockMenu extends AbstractContainerMenu {
	@Nullable
	public final ElectricBlockEntity blockEntity;

	/** Energy 0-1000 scaled to keep it inside DataSlot's short range. */
	public final DataSlot energyScaled = DataSlot.standalone();
	public final DataSlot progressScaled = DataSlot.standalone();
	public final DataSlot maxProgressScaled = DataSlot.standalone();

	/** The size of the BE-backed portion of the slot list. Player inventory slots follow after. */
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

	/** Network-side ctor reading the BE position from the payload. */
	protected ElectricBlockMenu(MenuType<?> type, int id, Inventory playerInv, FriendlyByteBuf buf) {
		this(type, id, playerInv, resolve(playerInv, buf));
	}

	@Nullable
	private static ElectricBlockEntity resolve(Inventory inv, FriendlyByteBuf buf) {
		if (buf == null) return null;
		var pos = buf.readBlockPos();
		return inv.player.level().getBlockEntity(pos) instanceof ElectricBlockEntity be ? be : null;
	}

	/**
	 * Default machine-slot layout: input slots in column at x=56, output slots in column at x=116.
	 * Per-machine menus can override this for bespoke layouts (e.g. 2-input canning, 9-slot crafting grid).
	 */
	protected void addMachineSlots(Inventory playerInv) {
		if (blockEntity == null || blockEntity.getSlotCount() == 0) {
			machineSlotCount = 0;
			return;
		}
		ElectricBlockEntityContainer container = new ElectricBlockEntityContainer(blockEntity);

		int inputs = blockEntity.inputItems.length;
		int outputs = blockEntity.outputItems.length;

		int inputRows = Math.max(1, (int) Math.ceil(inputs / 2D));
		int yStart = 35 - ((inputRows - 1) * 9); // centre vertically against y=35

		for (int i = 0; i < inputs; i++) {
			int row = i / 2, col = i % 2;
			addSlot(new Slot(container, i, 48 + col * 18, yStart + row * 18));
		}

		int outputRows = Math.max(1, (int) Math.ceil(outputs / 2D));
		int oyStart = 35 - ((outputRows - 1) * 9);
		for (int i = 0; i < outputs; i++) {
			int row = i / 2, col = i % 2;
			addSlot(new OutputSlot(container, inputs + i, 108 + col * 18, oyStart + row * 18));
		}

		machineSlotCount = inputs + outputs;
	}

	protected void addUpgradeSlots(int x) {
		addUpgradeSlots(x, 8);
	}

	protected void addUpgradeSlots(int x, int yStart) {
		if (!(blockEntity instanceof dev.ftb.mods.ftbic.block.entity.machine.BasicMachineBlockEntity bm)) return;
		UpgradeInventoryContainer c = new UpgradeInventoryContainer(bm.upgradeInventory);
		for (int i = 0; i < bm.upgradeInventory.getSlots(); i++) {
			addSlot(new UpgradeSlot(c, i, x, yStart + i * 18));
			machineSlotCount++;
		}
	}

	protected void addBatterySlot(int x, int y) {
		if (!(blockEntity instanceof dev.ftb.mods.ftbic.block.entity.machine.BasicMachineBlockEntity bm)) return;
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

	/** Standard 3×9 inventory at (8, 84) + hotbar at (8, 142). */
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

	/** Client-side: energy fraction 0-1 for progress bar rendering. */
	public float getEnergyFraction() {
		return energyScaled.get() / 1000F;
	}

	/** Client-side: recipe progress fraction 0-1 for progress bar rendering. */
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
