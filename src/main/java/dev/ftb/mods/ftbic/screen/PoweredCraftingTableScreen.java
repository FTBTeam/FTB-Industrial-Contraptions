package dev.ftb.mods.ftbic.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class PoweredCraftingTableScreen extends ElectricBlockScreen<PoweredCraftingTableMenu> {
	public PoweredCraftingTableScreen(PoweredCraftingTableMenu m, Inventory inv, Component c) {
		super(m, inv, c);
		energyX = 16;
		energyY = 26;
	}

	@Override
	protected void renderBg(PoseStack poseStack, float delta, int mouseX, int mouseY) {
		super.renderBg(poseStack, delta, mouseX, mouseY);

		for (int i = 0; i < menu.entity.upgradeInventory.getSlots(); i++) {
			drawSlot(poseStack, leftPos + 151, topPos + 7 + i * 18);
		}

		drawSlot(poseStack, leftPos + 14, topPos + 43);

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				drawSlot(poseStack, leftPos + 43 + x * 18, topPos + 16 + y * 18);
			}
		}

		drawLargeSlot(poseStack, leftPos + 110, topPos + 30);
	}
}
