package dev.ftb.mods.ftbic.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

public class PumpScreen extends ElectricBlockScreen<PumpMenu> {
	private Fluid prevFilter = null;
	private FluidStack filterStack = FluidStack.EMPTY;

	public PumpScreen(PumpMenu m, Inventory inv, Component c) {
		super(m, inv, c);
		energyX = 108;
		energyY = 36;
	}

	@Override
	protected void renderBg(PoseStack poseStack, float delta, int mouseX, int mouseY) {
		super.renderBg(poseStack, delta, mouseX, mouseY);

		drawSlot(poseStack, leftPos + 52, topPos + 16);
		drawSlot(poseStack, leftPos + 52, topPos + 52);
		drawSlot(poseStack, leftPos + 106, topPos + 52);

		for (int i = 0; i < menu.entity.upgradeInventory.getSlots(); i++) {
			drawSlot(poseStack, leftPos + 151, topPos + 7 + i * 18);
		}

		drawArrowDown(poseStack, leftPos + 54, topPos + 36);
		drawTank(poseStack, leftPos + 79, topPos + 16, menu.entity.fluidStack, menu.entity.getCapacity());

		if (prevFilter != menu.entity.filter) {
			prevFilter = menu.entity.filter;
			filterStack = new FluidStack(prevFilter, FluidAttributes.BUCKET_VOLUME);
		}

		drawFluidSlot(poseStack, leftPos + 20, topPos + 34, filterStack);

		drawPauseButton(poseStack, leftPos + 106, topPos + 16, mouseX, mouseY, menu.getPaused() != 0);
	}
}
