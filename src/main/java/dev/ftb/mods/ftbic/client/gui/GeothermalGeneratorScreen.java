package dev.ftb.mods.ftbic.client.gui;

import dev.ftb.mods.ftbic.screen.GeothermalGeneratorMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

public class GeothermalGeneratorScreen extends ElectricBlockScreen<GeothermalGeneratorMenu> {
	public GeothermalGeneratorScreen(GeothermalGeneratorMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
		energyX = 63;
		energyY = 36;
		drawDefaultArrow = false;
	}

	@Override
	protected void extractOverlays(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
		drawSlot(g, leftPos + 61, topPos + 16);
		drawSlot(g, leftPos + 61, topPos + 52);
		int amount = Math.round(this.menu.getFluidFraction() * 8000F);
		drawTank(g, leftPos + 97, topPos + 16, new FluidStack(Fluids.LAVA, amount), 8000);
	}

	@Override
	protected void extractOverlayTooltips(GuiGraphicsExtractor g, int mouseX, int mouseY) {
		int amount = Math.round(this.menu.getFluidFraction() * 8000F);
		tankTooltip(g, leftPos + 97, topPos + 16, mouseX, mouseY, new FluidStack(Fluids.LAVA, amount), 8000);
	}
}
