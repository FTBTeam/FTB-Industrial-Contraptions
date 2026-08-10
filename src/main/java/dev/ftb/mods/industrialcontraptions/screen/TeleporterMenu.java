package dev.ftb.mods.industrialcontraptions.screen;

import dev.ftb.mods.industrialcontraptions.block.entity.ElectricBlockEntity;
import dev.ftb.mods.industrialcontraptions.util.TeleporterEntry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class TeleporterMenu extends ElectricBlockMenu {
	public List<TeleporterEntry> peers = new ArrayList<>();

	public TeleporterMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
		super(ICMenus.TELEPORTER.get(), id, playerInv, buf);
	}

	public TeleporterMenu(int id, Inventory playerInv, ElectricBlockEntity be) {
		super(ICMenus.TELEPORTER.get(), id, playerInv, be);
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
