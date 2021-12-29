package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.generator.SolarPanelBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;

public class SolarPanelMenu extends ElectricBlockMenu<SolarPanelBlockEntity> {
	public SolarPanelMenu(int id, Inventory playerInv, SolarPanelBlockEntity r) {
		super(FTBICMenus.SOLAR_PANEL.get(), id, playerInv, r, null);
	}

	public SolarPanelMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
		this(id, playerInv, (SolarPanelBlockEntity) playerInv.player.level.getBlockEntity(buf.readBlockPos()));
	}

	@Override
	public void addBlockSlots(@Nullable Object extra) {
		addSlot(new SimpleItemHandlerSlot(entity.chargeBatteryInventory, 0, 98, 44));
	}
}
