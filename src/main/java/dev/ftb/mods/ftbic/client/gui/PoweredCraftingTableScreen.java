package dev.ftb.mods.ftbic.client.gui;

import dev.ftb.mods.ftbic.screen.PoweredCraftingTableMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class PoweredCraftingTableScreen extends ElectricBlockScreen<PoweredCraftingTableMenu> {
	public PoweredCraftingTableScreen(PoweredCraftingTableMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
		energyX = 16;
		energyY = 26;
		drawDefaultArrow = false;
	}

	@Override
	protected void extractOverlays(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
// 3×3 grid (matches PoweredCraftingTableMenu#addMachineSlots layout x=30,y=17).
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				drawSlot(g, leftPos + 30 + x * 18 - 1, topPos + 17 + y * 18 - 1);
			}
		}
		// Output slot (matches menu layout x=124,y=35).
		drawLargeSlot(g, leftPos + 119, topPos + 30);
		// Visual progress arrow between grid and result (centred around 95-117).
		drawArrow(g, leftPos + 95, topPos + 35, Math.round(menu.getProgressFraction() * 24F));
	}
}
