package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.PumpBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.material.Fluids;

public class PumpMenu extends ElectricBlockMenu {
	public final DataSlot fluidAmount = DataSlot.standalone();
	/** 0 = empty, 1 = water, 2 = lava. */
	public final DataSlot fluidKind = DataSlot.standalone();
	public final DataSlot pausedSlot = DataSlot.standalone();

	public PumpMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
		super(FTBICMenus.PUMP.get(), id, playerInv, buf);
		addDataSlot(fluidAmount);
		addDataSlot(fluidKind);
		addDataSlot(pausedSlot);
	}

	public PumpMenu(int id, Inventory playerInv, ElectricBlockEntity be) {
		super(FTBICMenus.PUMP.get(), id, playerInv, be);
		addDataSlot(fluidAmount);
		addDataSlot(fluidKind);
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
		if (inputs > 0) addSlot(new Slot(container, 0, 53, 17));
		if (outputs > 0) addSlot(new OutputSlot(container, inputs, 53, 53));
		machineSlotCount = inputs + outputs;
		addBatterySlot(107, 53);
		addUpgradeSlots(152);
	}

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();
		if (blockEntity instanceof PumpBlockEntity pump) {
			fluidAmount.set(Math.min(Short.MAX_VALUE, pump.fluidAmount));
			int kind = 0;
			if (pump.storedFluid == Fluids.WATER) kind = 1;
			else if (pump.storedFluid == Fluids.LAVA) kind = 2;
			fluidKind.set(kind);
			pausedSlot.set(pump.isEffectivelyPaused() ? 1 : 0);
		}
	}

	public int getFluidAmount()  { return fluidAmount.get(); }
	public int getFluidKind()    { return fluidKind.get(); }
	public boolean isPaused()    { return pausedSlot.get() == 1; }

	@Override
	public boolean clickMenuButton(Player player, int id) {
		if (id == 0 && blockEntity instanceof PumpBlockEntity pump) {
			if (pump.redstonePaused) {
				if (player instanceof net.minecraft.server.level.ServerPlayer sp) {
					sp.sendSystemMessage(net.minecraft.network.chat.Component.literal(
							"Pause is currently overridden by an active redstone signal"), true);
				}
				return true;
			}
			pump.paused = !pump.paused;
			pump.setChanged();
			return true;
		}
		return super.clickMenuButton(player, id);
	}
}
