package dev.ftb.mods.ftbic.client.gui;

import dev.ftb.mods.ftbic.integration.jei.ClientRecipeCache;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import dev.ftb.mods.ftbic.screen.AntimatterConstructorMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
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

	@Override
	protected void extractOverlayTooltips(GuiGraphicsExtractor g, int mouseX, int mouseY) {
		if (isIn(mouseX, mouseY, leftPos + 81, topPos + 37, 14, 14)) {
			Component label = this.menu.hasBoost()
					? Component.literal("Boosted. Click to show boost items.")
					: Component.literal("Click to show boost items");
			g.setTooltipForNextFrame(label, mouseX, mouseY);
		}
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean dragging) {
		int mx = (int) event.x();
		int my = (int) event.y();
		if (isIn(mx, my, leftPos + 81, topPos + 37, 14, 14)) {
			ClientRecipeCache.showRecipesForType(FTBICRecipes.ANTIMATTER_BOOST.get());
			return true;
		}
		return super.mouseClicked(event, dragging);
	}
}
