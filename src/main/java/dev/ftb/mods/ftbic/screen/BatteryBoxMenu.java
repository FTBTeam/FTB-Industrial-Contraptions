package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.storage.BatteryBoxBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;

public class BatteryBoxMenu extends ElectricBlockMenu<BatteryBoxBlockEntity> {
	public BatteryBoxMenu(int id, Inventory playerInv, BatteryBoxBlockEntity r) {
		super(FTBICMenus.BATTERY_BOX.get(), id, playerInv, r, null);
	}

	public BatteryBoxMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
		this(id, playerInv, (BatteryBoxBlockEntity) playerInv.player.level.getBlockEntity(buf.readBlockPos()));
	}

	@Override
	public void addBlockSlots(@Nullable Object extra) {
		addSlot(new SimpleItemHandlerSlot(entity.dischargeBatteryInventory, 0, 53, 35));
		addSlot(new SimpleItemHandlerSlot(entity.chargeBatteryInventory, 0, 109, 35));
	}
}
