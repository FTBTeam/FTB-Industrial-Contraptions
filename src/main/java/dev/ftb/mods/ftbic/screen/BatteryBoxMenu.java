package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.storage.BatteryBoxBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import org.jetbrains.annotations.Nullable;

public class BatteryBoxMenu extends ElectricBlockMenu<BatteryBoxBlockEntity> {
	public BatteryBoxMenu(int id, Inventory playerInv, BatteryBoxBlockEntity r, ContainerData d) {
		super(FTBICMenus.BATTERY_BOX.get(), id, playerInv, r, d, null);
	}

	public BatteryBoxMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
		this(id, playerInv, (BatteryBoxBlockEntity) playerInv.player.level.getBlockEntity(buf.readBlockPos()), new SimpleContainerData(1));
	}

	@Override
	public void addBlockSlots(@Nullable Object extra) {
		addSlot(new SimpleItemHandlerSlot(entity.dischargeBatteryInventory, 0, 53, 35));
		addSlot(new SimpleItemHandlerSlot(entity.chargeBatteryInventory, 0, 109, 35));
	}
}
