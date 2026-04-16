package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class TeleporterMenu extends ElectricBlockMenu {
	public TeleporterMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
		super(FTBICMenus.TELEPORTER.get(), id, playerInv, buf);
	}

	public TeleporterMenu(int id, Inventory playerInv, ElectricBlockEntity be) {
		super(FTBICMenus.TELEPORTER.get(), id, playerInv, be);
	}

	@Override
	protected void addMachineSlots(Inventory playerInv) {
		machineSlotCount = 0;
	}

	@Override
	protected int getPlayerSlotOffset() {
		return 114;
	}
}
