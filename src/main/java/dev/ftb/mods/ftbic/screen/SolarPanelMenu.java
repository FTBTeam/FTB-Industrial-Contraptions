package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.DataSlot;

public class SolarPanelMenu extends ElectricBlockMenu {
	public final DataSlot sunBar = DataSlot.standalone();

	public SolarPanelMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
		super(FTBICMenus.SOLAR_PANEL.get(), id, playerInv, buf);
		addDataSlot(sunBar);
	}

	public SolarPanelMenu(int id, Inventory playerInv, ElectricBlockEntity be) {
		super(FTBICMenus.SOLAR_PANEL.get(), id, playerInv, be);
		addDataSlot(sunBar);
	}

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();
		if (blockEntity != null && blockEntity.getLevel() != null) {
			boolean sunny = blockEntity.getLevel().isBrightOutside()
					&& blockEntity.getLevel().canSeeSky(blockEntity.getBlockPos().above());
			sunBar.set(sunny ? 14 : 0);
		}
	}

	public int getSunBar() {
		return sunBar.get();
	}
}
