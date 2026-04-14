package dev.ftb.mods.ftbic.client.gui;

import dev.ftb.mods.ftbic.screen.PumpMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

public class PumpScreen extends ElectricBlockScreen<PumpMenu> {
	public PumpScreen(PumpMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
		energyX = 108;
		energyY = 36;
		drawDefaultArrow = false;
	}

	@Override
	protected void extractOverlays(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
		drawSlot(g, leftPos + 52, topPos + 16);
		drawSlot(g, leftPos + 52, topPos + 52);

		Fluid fluid = switch (this.menu.getFluidKind()) {
			case 1 -> Fluids.WATER;
			case 2 -> Fluids.LAVA;
			default -> Fluids.EMPTY;
		};
		FluidStack stored = fluid == Fluids.EMPTY ? FluidStack.EMPTY : new FluidStack(fluid, this.menu.getFluidAmount());
		drawTank(g, leftPos + 79, topPos + 16, stored, 128_000);

		drawSmallPauseButton(g, leftPos + 106, topPos + 17, mouseX, mouseY, this.menu.isPaused());
	}

	@Override
	protected void extractOverlayTooltips(GuiGraphicsExtractor g, int mouseX, int mouseY) {
		Fluid fluid = switch (this.menu.getFluidKind()) {
			case 1 -> Fluids.WATER;
			case 2 -> Fluids.LAVA;
			default -> Fluids.EMPTY;
		};
		FluidStack stored = fluid == Fluids.EMPTY ? FluidStack.EMPTY : new FluidStack(fluid, this.menu.getFluidAmount());
		tankTooltip(g, leftPos + 79, topPos + 16, mouseX, mouseY, stored, 128_000);
		if (isIn(mouseX, mouseY, leftPos + 106, topPos + 17, 9, 10)) {
			g.setTooltipForNextFrame(
					Component.literal(this.menu.isPaused() ? "Paused — click to resume" : "Running — click to pause"),
					mouseX, mouseY);
		}
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean dragging) {
		int mx = (int) event.x();
		int my = (int) event.y();
		if (isIn(mx, my, leftPos + 106, topPos + 17, 9, 10)) {
			Minecraft.getInstance().player.connection.send(
					new ServerboundContainerButtonClickPacket(menu.containerId, 0));
			return true;
		}
		return super.mouseClicked(event, dragging);
	}
}
