package dev.ftb.mods.ftbic.client.gui;

import dev.ftb.mods.ftbic.screen.QuarryMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.world.entity.player.Inventory;

public class QuarryScreen extends ElectricBlockScreen<QuarryMenu> {
	public QuarryScreen(QuarryMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
		energyX = 126;
		energyY = 33;
		drawDefaultArrow = false;
	}

	@Override
	protected void extractOverlays(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 6; x++) {
				drawSlot(g, leftPos + 7 + x * 18, topPos + 16 + y * 18);
			}
		}
		for (int i = 0; i < 4; i++) {
			drawSlot(g, leftPos + 151, topPos + 7 + i * 18);
		}
		drawSlot(g, leftPos + 124, topPos + 52);
		drawSmallPauseButton(g, leftPos + 124, topPos + 17, mouseX, mouseY, this.menu.isPaused());
	}

	@Override
	protected void extractOverlayTooltips(GuiGraphicsExtractor g, int mouseX, int mouseY) {
		super.extractOverlayTooltips(g, mouseX, mouseY);
		if (isIn(mouseX, mouseY, leftPos + 129, topPos + 17, 9, 10)) {
			g.setTooltipForNextFrame(
					Component.literal(this.menu.isPaused() ? "Paused — click to resume" : "Running — click to pause"),
					mouseX, mouseY);
		}
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean dragging) {
		int mx = (int) event.x();
		int my = (int) event.y();
		if (isIn(mx, my, leftPos + 129, topPos + 17, 9, 10)) {
			Minecraft.getInstance().player.connection.send(
					new ServerboundContainerButtonClickPacket(menu.containerId, 0));
			return true;
		}
		return super.mouseClicked(event, dragging);
	}
}
