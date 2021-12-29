package dev.ftb.mods.ftbic.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftbic.block.entity.machine.AntimatterConstructorBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class AntimatterConstructorScreen extends ElectricBlockScreen<AntimatterConstructorMenu> {
	public AntimatterConstructorScreen(AntimatterConstructorMenu m, Inventory inv, Component c) {
		super(m, inv, c);
		energyX = 81;
		energyY = 37;
	}

	@Override
	public int getEnergyType() {
		return menu.data.get(AntimatterConstructorBlockEntity.HAS_BOOST) ? 2 : 1;
	}

	@Override
	protected void renderBg(PoseStack poseStack, float delta, int mouseX, int mouseY) {
		super.renderBg(poseStack, delta, mouseX, mouseY);

		drawSlot(poseStack, leftPos + 52, topPos + 35);
		drawSlot(poseStack, leftPos + 106, topPos + 35);
	}
}
