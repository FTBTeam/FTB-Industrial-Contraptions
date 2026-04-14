package dev.ftb.mods.ftbic.client.gui;

import dev.ftb.mods.ftbic.integration.jei.ClientRecipeCache;
import dev.ftb.mods.ftbic.screen.MachineMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.RecipeType;

public class MachineScreen extends ElectricBlockScreen<MachineMenu> {
	public MachineScreen(MachineMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
		energyX = 8;
		energyY = 27;
	}

	@Override
	protected void extractOverlays(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
		if (this.menu.blockEntity == null) return;
		int inputs = this.menu.blockEntity.inputItems.length;
		int outputs = this.menu.blockEntity.outputItems.length;

		int inputRows = Math.max(1, (int) Math.ceil(inputs / 2D));
		int yStart = 35 - ((inputRows - 1) * 9);
		for (int i = 0; i < inputs; i++) {
			int row = i / 2, col = i % 2;
			drawSlot(g, leftPos + 47 + col * 18, topPos + yStart - 1 + row * 18);
		}

		int outputRows = Math.max(1, (int) Math.ceil(outputs / 2D));
		int oyStart = 35 - ((outputRows - 1) * 9);
		for (int i = 0; i < outputs; i++) {
			int row = i / 2, col = i % 2;
			drawSlot(g, leftPos + 107 + col * 18, topPos + oyStart - 1 + row * 18);
		}
		drawSlot(g, leftPos + 7, topPos + 52);
		for (int i = 0; i < 4; i++) {
			drawSlot(g, leftPos + 151, topPos + 7 + i * 18);
		}
	}

	@Override
	protected void extractOverlayTooltips(GuiGraphicsExtractor g, int mouseX, int mouseY) {
		super.extractOverlayTooltips(g, mouseX, mouseY);
		if (drawDefaultArrow && isIn(mouseX, mouseY, leftPos + 80, topPos + 34, 24, 17)) {
			int pct = Math.round(this.menu.getProgressFraction() * 100F);
			g.setTooltipForNextFrame(Component.literal("Progress: " + pct + "% — Click to show recipes"), mouseX, mouseY);
		}
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean dragging) {
		int mx = (int) event.x();
		int my = (int) event.y();
		if (drawDefaultArrow && isIn(mx, my, leftPos + 80, topPos + 34, 24, 17)) {
			java.util.List<RecipeType<?>> types = this.menu.getJeiRecipeTypes();
			if (!types.isEmpty()) {
				ClientRecipeCache.showRecipesForTypes(types);
				return true;
			}
		}
		return super.mouseClicked(event, dragging);
	}
}
