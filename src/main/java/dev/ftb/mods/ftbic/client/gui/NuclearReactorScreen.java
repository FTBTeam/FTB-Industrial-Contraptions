package dev.ftb.mods.ftbic.client.gui;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.screen.NuclearReactorMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import dev.ftb.mods.ftbic.integration.jei.ClientRecipeCache;

public class NuclearReactorScreen extends ElectricBlockScreen<NuclearReactorMenu> {
	public static final Identifier NUCLEAR_REACTOR_TEXTURE = FTBIC.id("textures/gui/nuclear_reactor.png");

	private static final int GRID_TOP = 17;
	private static final int UPPER_BOTTOM = GRID_TOP + 3 * 18;   // y=71, end of upper 3-row grid
	private static final int LOWER_TOP    = UPPER_BOTTOM + 18;    // y=89, start of lower 3-row grid
	private static final int LOWER_BOTTOM = LOWER_TOP + 3 * 18;   // y=143, end of lower grid

	public NuclearReactorScreen(NuclearReactorMenu menu, Inventory inv, Component title) {
		super(menu, inv, title, 176, 232);
		drawDefaultArrow = false;
		energyX = -1;
		energyY = -1;
	}

	@Override
	protected Identifier getScreenTexture() {
		return NUCLEAR_REACTOR_TEXTURE;
	}

	@Override
	protected void init() {
		super.init();
		this.titleLabelX = 8;
		// Vanilla positions the "Inventory" label relative to imageHeight; keep it above the hotbar.
		this.inventoryLabelY = this.imageHeight - 94;
	}

	@Override
	protected void extractOverlays(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
		int activeColumns = this.menu.getActiveColumns();
		int gridLeft = leftPos + 7;
		int slotSize = 18;
		int overlayColor = 0xFF8B8B8B;

		// Mask inactive right columns across the upper and lower halves of the grid.
		if (activeColumns < 9) {
			int maskX = gridLeft + 1 + activeColumns * slotSize;
			int maskW = (9 - activeColumns) * slotSize;
			g.fill(maskX, topPos + GRID_TOP + 1, maskX + maskW, topPos + UPPER_BOTTOM + 1, overlayColor);
			g.fill(maskX, topPos + LOWER_TOP + 1, maskX + maskW, topPos + LOWER_BOTTOM + 1, overlayColor);
		}

		// Output bar + pause/question buttons in the top-right corner of the content area.
		drawNuclearBar(g, leftPos + 115, topPos + 5, this.menu.isRunning() && !this.menu.isPaused());
		drawSmallPauseButton(g, leftPos + 105, topPos + 5, mouseX, mouseY, this.menu.isPaused());
		drawSmallQuestionButton(g, leftPos + 94, topPos + 5, mouseX, mouseY);

		// Heat bar + redstone toggle in the middle info strip (y=72..89), between upper/lower grids.
		drawHeatBar(g, leftPos + 115, topPos + 76, this.menu.getHeatFraction());
		drawSmallRedstoneButton(g, leftPos + 105, topPos + 76, mouseX, mouseY, this.menu.allowRedstone());

		String energyLabel = this.menu.isPaused() ? "Paused" : (this.menu.getEnergyOutput() + " z/t");
		g.centeredText(font, Component.literal(energyLabel).withStyle(ChatFormatting.WHITE),
				leftPos + 142, topPos + 6, 0xFFFFFF);
		g.centeredText(font, Component.literal(Math.round(this.menu.getHeatFraction() * 100F) + "%")
				.withStyle(ChatFormatting.WHITE), leftPos + 142, topPos + 77, 0xFFFFFF);
	}

	@Override
	protected void extractOverlayTooltips(GuiGraphicsExtractor g, int mouseX, int mouseY) {
		if (isIn(mouseX, mouseY, leftPos + 115, topPos + 5, 54, 10)) {
			Component label = this.menu.isPaused()
					? Component.literal("Paused (" + this.menu.getEnergyOutput() + " z/t when active)")
					: Component.literal("Output: " + this.menu.getEnergyOutput() + " z/t");
			g.setTooltipForNextFrame(label, mouseX, mouseY);
		}
		if (isIn(mouseX, mouseY, leftPos + 115, topPos + 76, 54, 10)) {
			int pct = Math.round(this.menu.getHeatFraction() * 100F);
			g.setTooltipForNextFrame(Component.translatable("ftbic.jade.reactor_heat", pct), mouseX, mouseY);
		}
		if (isIn(mouseX, mouseY, leftPos + 94, topPos + 5, 9, 10)) {
			g.setTooltipForNextFrame(Component.literal("Show reactor components in JEI"), mouseX, mouseY);
		}
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean dragging) {
		int mx = (int) event.x();
		int my = (int) event.y();
		if (isIn(mx, my, leftPos + 105, topPos + 5, 9, 10)) {
			send(0);
			return true;
		}
		if (isIn(mx, my, leftPos + 105, topPos + 76, 9, 10)) {
			send(1);
			return true;
		}
		if (isIn(mx, my, leftPos + 94, topPos + 5, 9, 10)) {
			ClientRecipeCache.setSearchFilter("@ftbic reactor");
			return true;
		}
		return super.mouseClicked(event, dragging);
	}

	private void send(int buttonId) {
		Minecraft.getInstance().player.connection.send(
				new ServerboundContainerButtonClickPacket(menu.containerId, buttonId));
	}
}
