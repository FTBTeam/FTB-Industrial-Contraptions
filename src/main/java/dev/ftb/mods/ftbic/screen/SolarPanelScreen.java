package dev.ftb.mods.ftbic.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SolarPanelScreen extends ElectricBlockScreen<SolarPanelMenu> {
	public SolarPanelScreen(SolarPanelMenu m, Inventory inv, Component c) {
		super(m, inv, c);
	}

	@Override
	protected void renderBg(PoseStack poseStack, float delta, int mouseX, int mouseY) {
		super.renderBg(poseStack, delta, mouseX, mouseY);

		drawSun(poseStack, leftPos + 63, topPos + 36, menu.getLightValue());
		drawEnergy(poseStack, leftPos + 99, topPos + 27, menu.getEnergyBar());

		drawSlot(poseStack, leftPos + 97, topPos + 43);
	}
}
