package dev.ftb.mods.ftbic.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BatteryBoxScreen extends ElectricBlockScreen<BatteryBoxMenu> {
	public BatteryBoxScreen(BatteryBoxMenu m, Inventory inv, Component c) {
		super(m, inv, c);
		energyX = 81;
		energyY = 36;
	}

	@Override
	protected void renderBg(PoseStack poseStack, float delta, int mouseX, int mouseY) {
		super.renderBg(poseStack, delta, mouseX, mouseY);

		drawSlot(poseStack, leftPos + 52, topPos + 34);
		drawSlot(poseStack, leftPos + 108, topPos + 34);
	}
}
