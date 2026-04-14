package dev.ftb.mods.ftbic.client.gui;

import dev.ftb.mods.ftbic.screen.SolarPanelMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SolarPanelScreen extends ElectricBlockScreen<SolarPanelMenu> {
	public SolarPanelScreen(SolarPanelMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
		energyX = 99;
		energyY = 27;
		drawDefaultArrow = false;
	}

	@Override
	protected void extractOverlays(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
		drawSun(g, leftPos + 63, topPos + 36, this.menu.getSunBar());
	}

	@Override
	protected void extractOverlayTooltips(GuiGraphicsExtractor g, int mouseX, int mouseY) {
		if (isIn(mouseX, mouseY, leftPos + 63, topPos + 36, 14, 14)) {
			boolean sunny = this.menu.getSunBar() > 0;
			g.setTooltipForNextFrame(Component.literal(sunny ? "Producing" : "No sunlight"), mouseX, mouseY);
		}
	}
}
