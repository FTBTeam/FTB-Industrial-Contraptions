package dev.ftb.mods.ftbic.client.gui;

import dev.ftb.mods.ftbic.screen.BatteryBoxMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BatteryBoxScreen extends ElectricBlockScreen<BatteryBoxMenu> {
	public BatteryBoxScreen(BatteryBoxMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
		energyX = 81;
		energyY = 36;
		energyType = 1;
		drawDefaultArrow = false;
	}

	@Override
	protected void extractOverlays(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
		drawSlot(g, leftPos + 52, topPos + 34);
		drawSlot(g, leftPos + 108, topPos + 34);
	}
}
