package dev.ftb.mods.ftbic.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftbic.recipe.MachineRecipeSerializer;
import dev.ftb.mods.ftbic.screen.sync.SyncedData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MachineScreen extends ElectricBlockScreen<MachineMenu> {
	public MachineScreen(MachineMenu m, Inventory inv, Component c) {
		super(m, inv, c);
	}

	@Override
	protected void renderBg(PoseStack poseStack, float delta, int mouseX, int mouseY) {
		MachineRecipeSerializer s = menu.serializer;

		int wx = leftPos + 78 - (s.guiWidth / 2);
		int wy = topPos + 16;

		energyX = wx - leftPos + s.energyX;
		energyY = wy - topPos + s.energyY;

		super.renderBg(poseStack, delta, mouseX, mouseY);

		drawArrow(poseStack, wx + s.progressX, wy + s.progressY, menu.data.get(SyncedData.BAR));

		for (int i = 0; i < menu.entity.inputItems.length; i++) {
			drawSlot(poseStack, wx + i * 18, wy);
		}

		for (int i = 0; i < menu.entity.upgradeInventory.getSlots(); i++) {
			drawSlot(poseStack, leftPos + 151, topPos + 7 + i * 18);
		}

		drawSlot(poseStack, wx + s.batteryX, wy + s.batteryY);

		if (menu.entity.outputItems.length > 1) {
			drawCombinedSlot(poseStack, wx + s.outputX, wy + s.outputY);
		} else {
			drawLargeSlot(poseStack, wx + s.outputX, wy + s.outputY);
		}
	}
}
