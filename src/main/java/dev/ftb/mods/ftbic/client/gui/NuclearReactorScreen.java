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

/**
 * Nuclear reactor screen — uses the dedicated {@code textures/gui/nuclear_reactor.png} background
 * (256×256 atlas with a custom 176×222 frame at 0,0). Renders heat bar + nuclear-output bar +
 * pause/redstone toggle buttons; clicks dispatch {@code ServerboundContainerButtonClickPacket}s
 * which the menu's {@link NuclearReactorMenu#clickMenuButton} handles server-side.
 */
public class NuclearReactorScreen extends ElectricBlockScreen<NuclearReactorMenu> {
	public static final Identifier NUCLEAR_REACTOR_TEXTURE = FTBIC.id("textures/gui/nuclear_reactor.png");

	public NuclearReactorScreen(NuclearReactorMenu menu, Inventory inv, Component title) {
		super(menu, inv, title, 176, 222);
		drawDefaultArrow = false;
		// Energy bar handled inline below.
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
	}

	@Override
	protected void extractOverlays(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
		drawNuclearBar(g, leftPos + 115, topPos + 5, this.menu.isRunning() && !this.menu.isPaused());
		drawHeatBar(g, leftPos + 115, topPos + 127, this.menu.getHeatFraction());
		drawSmallPauseButton(g, leftPos + 105, topPos + 5, mouseX, mouseY, this.menu.isPaused());
		drawSmallQuestionButton(g, leftPos + 94, topPos + 5, mouseX, mouseY);
		drawSmallRedstoneButton(g, leftPos + 105, topPos + 127, mouseX, mouseY, this.menu.allowRedstone());

		String energyLabel = this.menu.isPaused() ? "Paused" : (this.menu.getEnergyOutput() + " z/t");
		g.centeredText(font, Component.literal(energyLabel).withStyle(ChatFormatting.WHITE),
				leftPos + 142, topPos + 6, 0xFFFFFF);
		g.centeredText(font, Component.literal(Math.round(this.menu.getHeatFraction() * 100F) + "%")
				.withStyle(ChatFormatting.WHITE), leftPos + 142, topPos + 128, 0xFFFFFF);
	}

	@Override
	protected void extractOverlayTooltips(GuiGraphicsExtractor g, int mouseX, int mouseY) {
		if (isIn(mouseX, mouseY, leftPos + 115, topPos + 5, 54, 10)) {
			Component label = this.menu.isPaused()
					? Component.literal("Paused (" + this.menu.getEnergyOutput() + " z/t when active)")
					: Component.literal("Output: " + this.menu.getEnergyOutput() + " z/t");
			g.setTooltipForNextFrame(label, mouseX, mouseY);
		}
		if (isIn(mouseX, mouseY, leftPos + 115, topPos + 127, 54, 10)) {
			int pct = Math.round(this.menu.getHeatFraction() * 100F);
			g.setTooltipForNextFrame(Component.translatable("ftbic.jade.reactor_heat", pct), mouseX, mouseY);
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
		if (isIn(mx, my, leftPos + 105, topPos + 127, 9, 10)) {
			send(1);
			return true;
		}
		return super.mouseClicked(event, dragging);
	}

	private void send(int buttonId) {
		Minecraft.getInstance().player.connection.send(
				new ServerboundContainerButtonClickPacket(menu.containerId, buttonId));
	}
}
