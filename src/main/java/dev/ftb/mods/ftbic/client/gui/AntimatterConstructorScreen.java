package dev.ftb.mods.ftbic.client.gui;

import dev.ftb.mods.ftbic.screen.AntimatterConstructorMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class AntimatterConstructorScreen extends ElectricBlockScreen<AntimatterConstructorMenu> {
	public AntimatterConstructorScreen(AntimatterConstructorMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
		energyX = 81;
		energyY = 37;
		drawDefaultArrow = false;
	}

	@Override
	protected void extractOverlays(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
		energyType = this.menu.hasBoost() ? 2 : 1;
		drawSlot(g, leftPos + 52, topPos + 35);
		drawSlot(g, leftPos + 106, topPos + 35);
	}
}
