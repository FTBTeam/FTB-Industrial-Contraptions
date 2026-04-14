package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

/**
 * Menu for the destination-picker GUI. No slots — the client pulls the actual destination list via
 * {@link dev.ftb.mods.ftbic.net.TeleporterListPayload} when the menu opens.
 */
public class TeleporterMenu extends ElectricBlockMenu {
	public TeleporterMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
		super(FTBICMenus.TELEPORTER.get(), id, playerInv, buf);
	}

	public TeleporterMenu(int id, Inventory playerInv, ElectricBlockEntity be) {
		super(FTBICMenus.TELEPORTER.get(), id, playerInv, be);
	}

	/** Override the default machine slot grid — the teleporter has no item slots. */
	@Override
	protected void addMachineSlots(Inventory playerInv) {
		machineSlotCount = 0;
	}
}
