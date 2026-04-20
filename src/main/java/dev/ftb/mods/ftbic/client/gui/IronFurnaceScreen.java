package dev.ftb.mods.ftbic.client.gui;

import dev.ftb.mods.ftbic.integration.jei.ClientRecipeCache;
import dev.ftb.mods.ftbic.screen.IronFurnaceMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

public class IronFurnaceScreen extends AbstractContainerScreen<IronFurnaceMenu> {
	private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("minecraft", "textures/gui/container/furnace.png");
	private static final Identifier LIT_PROGRESS_SPRITE = Identifier.fromNamespaceAndPath("minecraft", "container/furnace/lit_progress");
	private static final Identifier BURN_PROGRESS_SPRITE = Identifier.fromNamespaceAndPath("minecraft", "container/furnace/burn_progress");

	public IronFurnaceScreen(IronFurnaceMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
	}

	@Override
	protected void init() {
		super.init();
		titleLabelX = (imageWidth - font.width(title)) / 2;
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
		super.extractBackground(g, mouseX, mouseY, partialTick);
		int xo = leftPos;
		int yo = topPos;
		g.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, xo, yo, 0F, 0F, imageWidth, imageHeight, 256, 256);
		if (menu.isLit()) {
			int h = Mth.ceil(menu.getLitProgress() * 13F) + 1;
			g.blitSprite(RenderPipelines.GUI_TEXTURED, LIT_PROGRESS_SPRITE, 14, 14, 0, 14 - h, xo + 56, yo + 36 + 14 - h, 14, h);
		}
		int w = Mth.ceil(menu.getBurnProgress() * 24F);
		g.blitSprite(RenderPipelines.GUI_TEXTURED, BURN_PROGRESS_SPRITE, 24, 16, 0, 0, xo + 79, yo + 34, w, 16);
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
		super.extractRenderState(g, mouseX, mouseY, partialTick);
		if (isInArrow(mouseX, mouseY)) {
			int pct = Math.round(menu.getBurnProgress() * 100F);
			g.setTooltipForNextFrame(Component.literal("Progress: " + pct + "%. Click to show recipes."), mouseX, mouseY);
		}
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean dragging) {
		int mx = (int) event.x();
		int my = (int) event.y();
		if (isInArrow(mx, my)) {
			ClientRecipeCache.showRecipesForTypes(List.of(RecipeType.SMELTING));
			return true;
		}
		return super.mouseClicked(event, dragging);
	}

	private boolean isInArrow(int mx, int my) {
		return mx >= leftPos + 79 && mx < leftPos + 79 + 24 && my >= topPos + 34 && my < topPos + 34 + 16;
	}
}
