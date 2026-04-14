package dev.ftb.mods.ftbic.client.gui;

import dev.ftb.mods.ftbic.screen.BasicGeneratorMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BasicGeneratorScreen extends ElectricBlockScreen<BasicGeneratorMenu> {
	public BasicGeneratorScreen(BasicGeneratorMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
		energyX = 99;
		energyY = 27;
		drawDefaultArrow = false;
	}

	@Override
	protected void extractOverlays(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
		drawFuel(g, leftPos + 63, topPos + 27, this.menu.getFuelBar());
		drawSlot(g, leftPos + 61, topPos + 43);
	}

	@Override
	protected void extractOverlayTooltips(GuiGraphicsExtractor g, int mouseX, int mouseY) {
		if (isIn(mouseX, mouseY, leftPos + 63, topPos + 27, 14, 14)) {
			int ticks = this.menu.getFuelTicksRemaining();
			double seconds = ticks / 20.0;
			g.setTooltipForNextFrame(
					net.minecraft.network.chat.Component.literal(String.format("Burn time: %.1fs", seconds)),
					mouseX, mouseY);
		}
	}
}
