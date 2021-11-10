package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.generator.SolarPanelBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import org.jetbrains.annotations.Nullable;

public class SolarPanelMenu extends ElectricBlockMenu<SolarPanelBlockEntity> {
	public SolarPanelMenu(int id, Inventory playerInv, SolarPanelBlockEntity r, ContainerData d) {
		super(FTBICMenus.SOLAR_PANEL.get(), id, playerInv, r, d, null);
	}

	public SolarPanelMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
		this(id, playerInv, (SolarPanelBlockEntity) playerInv.player.level.getBlockEntity(buf.readBlockPos()), new SimpleContainerData(2));
	}

	@Override
	public void addBlockSlots(@Nullable Object extra) {
		addSlot(new SimpleItemHandlerSlot(entity.chargeBatteryInventory, 0, 98, 44));
	}

	public int getLightValue() {
		return containerData.get(0);
	}

	public int getEnergyBar() {
		return containerData.get(1);
	}
}
