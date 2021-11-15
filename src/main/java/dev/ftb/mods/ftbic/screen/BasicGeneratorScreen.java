package dev.ftb.mods.ftbic.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BasicGeneratorScreen extends ElectricBlockScreen<BasicGeneratorMenu> {
	public BasicGeneratorScreen(BasicGeneratorMenu m, Inventory inv, Component c) {
		super(m, inv, c);
		energyX = 99;
		energyY = 27;
	}

	@Override
	protected void renderBg(PoseStack poseStack, float delta, int mouseX, int mouseY) {
		super.renderBg(poseStack, delta, mouseX, mouseY);

		drawFuel(poseStack, leftPos + 63, topPos + 27, menu.getFuelBar());

		drawSlot(poseStack, leftPos + 61, topPos + 43);
		drawSlot(poseStack, leftPos + 97, topPos + 43);
	}
}
