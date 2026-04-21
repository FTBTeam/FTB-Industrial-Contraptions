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
	private static final int GRID_BOTTOM = GRID_TOP + 6 * 18;
	private static final int CONTROL_STRIP_TOP = GRID_BOTTOM;
	private static final int CONTROL_STRIP_H = 19;
	private static final int INVENTORY_Y = CONTROL_STRIP_TOP + CONTROL_STRIP_H;
	private static final int HOTBAR_Y = INVENTORY_Y + 54;

	public NuclearReactorScreen(NuclearReactorMenu menu, Inventory inv, Component title) {
		super(menu, inv, title, 176, HOTBAR_Y + 22);
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
		this.inventoryLabelX = 8;
		this.inventoryLabelY = CONTROL_STRIP_TOP + 5;
	}

	@Override
	protected void extractOverlays(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
		int activeColumns = this.menu.getActiveColumns();
		int gridLeft = leftPos + 7;
		int slotSize = 18;
		int overlayColor = 0xFF8B8B8B;

		for (int col = 0; col < 9; col++) {
			drawSlot(g, gridLeft + col * 18, topPos + GRID_TOP + 3 * 18);
		}

		if (activeColumns < 9) {
			int maskX = gridLeft + 1 + activeColumns * slotSize;
			int maskW = (9 - activeColumns) * slotSize;
			g.fill(maskX, topPos + GRID_TOP + 1, maskX + maskW, topPos + GRID_BOTTOM + 1, overlayColor);
		}

		g.fill(leftPos + 7, topPos + CONTROL_STRIP_TOP, leftPos + 169, topPos + CONTROL_STRIP_TOP + CONTROL_STRIP_H, overlayColor);

		int stripY = topPos + CONTROL_STRIP_TOP + (CONTROL_STRIP_H - 10) / 2;
		drawNuclearBar(g, leftPos + 115, topPos + 5, this.menu.isRunning() && !this.menu.isPaused());
		drawSmallPauseButton(g, leftPos + 105, topPos + 5, mouseX, mouseY, this.menu.isPaused());
		drawSmallQuestionButton(g, leftPos + 94, topPos + 5, mouseX, mouseY);

		drawHeatBar(g, leftPos + 115, stripY, this.menu.getHeatFraction());
		drawSmallRedstoneButton(g, leftPos + 105, stripY, mouseX, mouseY, this.menu.allowRedstone());

		String energyLabel = this.menu.isPaused() ? "Paused" : (this.menu.getEnergyOutput() + " z/t");
		g.centeredText(font, Component.literal(energyLabel).withStyle(ChatFormatting.WHITE),
				leftPos + 142, topPos + 6, 0xFFFFFF);
		g.centeredText(font, Component.literal(Math.round(this.menu.getHeatFraction() * 100F) + "%")
				.withStyle(ChatFormatting.WHITE), leftPos + 142, stripY + 1, 0xFFFFFF);

		int invX = leftPos + 8;
		int invY = topPos + INVENTORY_Y;
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				drawSlot(g, invX + col * 18 - 1, invY + row * 18 - 1);
			}
		}
		int hotbarY = topPos + HOTBAR_Y;
		for (int col = 0; col < 9; col++) {
			drawSlot(g, invX + col * 18 - 1, hotbarY - 1);
		}
	}

	@Override
	protected void extractOverlayTooltips(GuiGraphicsExtractor g, int mouseX, int mouseY) {
		int stripY = topPos + CONTROL_STRIP_TOP + (CONTROL_STRIP_H - 10) / 2;
		if (isIn(mouseX, mouseY, leftPos + 115, topPos + 5, 54, 10)) {
			Component label = this.menu.isPaused()
					? Component.literal("Paused (" + this.menu.getEnergyOutput() + " z/t when active)")
					: Component.literal("Output: " + this.menu.getEnergyOutput() + " z/t");
			g.setTooltipForNextFrame(label, mouseX, mouseY);
		}
		if (isIn(mouseX, mouseY, leftPos + 115, stripY, 54, 10)) {
			int pct = Math.round(this.menu.getHeatFraction() * 100F);
			g.setTooltipForNextFrame(Component.translatable("ftbic.jade.reactor_heat", pct), mouseX, mouseY);
		}
		if (isIn(mouseX, mouseY, leftPos + 105, topPos + 5, 9, 10)) {
			Component label = this.menu.isPaused()
					? Component.literal("Resume reactor")
					: Component.literal("Pause reactor");
			g.setTooltipForNextFrame(label, mouseX, mouseY);
		}
		if (isIn(mouseX, mouseY, leftPos + 105, stripY, 9, 10)) {
			Component label = this.menu.allowRedstone()
					? Component.literal("Redstone control: enabled")
					: Component.literal("Redstone control: disabled");
			g.setTooltipForNextFrame(label, mouseX, mouseY);
		}
		if (isIn(mouseX, mouseY, leftPos + 94, topPos + 5, 9, 10)) {
			g.setTooltipForNextFrame(Component.literal("Show reactor components in JEI"), mouseX, mouseY);
		}
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean dragging) {
		int mx = (int) event.x();
		int my = (int) event.y();
		int stripY = topPos + CONTROL_STRIP_TOP + (CONTROL_STRIP_H - 10) / 2;
		if (isIn(mx, my, leftPos + 105, topPos + 5, 9, 10)) {
			send(0);
			return true;
		}
		if (isIn(mx, my, leftPos + 105, stripY, 9, 10)) {
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
