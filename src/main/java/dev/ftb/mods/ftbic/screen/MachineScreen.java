package dev.ftb.mods.ftbic.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.recipe.MachineRecipeSerializer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MachineScreen extends ElectricBlockScreen<MachineMenu> {
	public final ResourceLocation progressTexture;

	public MachineScreen(MachineMenu m, Inventory inv, Component c) {
		super(m, inv, c);
		progressTexture = new ResourceLocation(FTBIC.MOD_ID, "textures/gui/" + m.serializer.getRegistryName().getPath() + ".png");
	}

	@Override
	protected void renderBg(PoseStack poseStack, float delta, int mouseX, int mouseY) {
		MachineRecipeSerializer s = menu.serializer;

		int wx = leftPos + 78 - (s.guiWidth / 2);
		int wy = topPos + 16;

		energyX = wx - leftPos + s.energyX;
		energyY = wy - topPos + s.energyY;

		super.renderBg(poseStack, delta, mouseX, mouseY);

		minecraft.getTextureManager().bind(progressTexture);
		drawProgressBar(poseStack, wx + s.progressX, wy + s.progressY, menu.getProgressBar());

		minecraft.getTextureManager().bind(BASE_TEXTURE);

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
