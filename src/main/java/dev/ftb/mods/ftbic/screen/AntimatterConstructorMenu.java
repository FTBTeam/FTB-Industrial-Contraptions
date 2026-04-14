package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.AntimatterConstructorBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;

public class AntimatterConstructorMenu extends ElectricBlockMenu {
	public final DataSlot hasBoostSlot = DataSlot.standalone();

	public AntimatterConstructorMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
		super(FTBICMenus.ANTIMATTER_CONSTRUCTOR.get(), id, playerInv, buf);
		addDataSlot(hasBoostSlot);
	}

	public AntimatterConstructorMenu(int id, Inventory playerInv, ElectricBlockEntity be) {
		super(FTBICMenus.ANTIMATTER_CONSTRUCTOR.get(), id, playerInv, be);
		addDataSlot(hasBoostSlot);
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
		if (inputs > 0) addSlot(new Slot(container, 0, 53, 36));
		if (outputs > 0) addSlot(new OutputSlot(container, inputs, 107, 36));
		machineSlotCount = inputs + outputs;
	}

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();
		if (blockEntity instanceof AntimatterConstructorBlockEntity ac) {
			// Boost is active either when there's stored boost charge OR when an input item is present
			// (which will be consumed next tick into stored charge).
			boolean hasBoost = ac.boostCharge > 0D
					|| (ac.inputItems.length > 0 && !ac.inputItems[0].isEmpty());
			hasBoostSlot.set(hasBoost ? 1 : 0);
		}
	}

	public boolean hasBoost() { return hasBoostSlot.get() == 1; }
}
