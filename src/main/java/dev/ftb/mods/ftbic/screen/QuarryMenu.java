package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.machine.QuarryBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class QuarryMenu extends ElectricBlockMenu<QuarryBlockEntity> {
	public QuarryMenu(int id, Inventory playerInv, QuarryBlockEntity r) {
		super(FTBICMenus.QUARRY.get(), id, playerInv, r, null);
	}

	public QuarryMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
		this(id, playerInv, (QuarryBlockEntity) playerInv.player.level.getBlockEntity(buf.readBlockPos()));
	}

	@Override
	public void addBlockSlots(@Nullable Object extra) {
		for (int y = 0; y < entity.upgradeInventory.getSlots(); y++) {
			addSlot(new SimpleItemHandlerSlot(entity.upgradeInventory, y, 152, 8 + y * 18));
		}

		addSlot(new SimpleItemHandlerSlot(entity.batteryInventory, 0, 125, 53));

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 6; x++) {
				addSlot(new SimpleItemHandlerSlot(entity, y * 6 + x, 8 + x * 18, 17 + y * 18));
			}
		}
	}

	@Override
	public boolean clickMenuButton(Player player, int button) {
		if (button == 0) {
			entity.paused = !entity.paused;
			entity.syncBlock();
			return true;
		}

		return false;
	}
}
