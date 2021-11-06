package dev.ftb.mods.ftbic.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MachineScreen extends AbstractContainerScreen<MachineMenu> {
	public static final ResourceLocation BASE_TEXTURE = new ResourceLocation(FTBIC.MOD_ID, "textures/gui/base.png");

	public final ResourceLocation progressTexture;

	public MachineScreen(MachineMenu m, Inventory inv, Component c) {
		super(m, inv, c);
		progressTexture = new ResourceLocation(FTBIC.MOD_ID, "textures/gui/" + m.serializer.getRegistryName().getPath() + ".png");
	}

	@Override
	public void render(PoseStack arg, int i, int j, float f) {
		renderBackground(arg);
		super.render(arg, i, j, f);
		renderTooltip(arg, i, j);
	}

	@Override
	protected void renderBg(PoseStack poseStack, float delta, int mouseX, int mouseY) {
		RenderSystem.color4f(1F, 1F, 1F, 1F);
		RenderSystem.defaultBlendFunc();
		minecraft.getTextureManager().bind(BASE_TEXTURE);
		int x = leftPos;
		int y = topPos;
		blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);

		int wx = x + 78 - (menu.serializer.guiWidth / 2);
		int wy = y + 16;
		minecraft.getTextureManager().bind(progressTexture);

		blit(poseStack, wx + menu.serializer.progressX, wy + menu.serializer.progressY, getBlitOffset(), 0, 0, 24, 17, 64, 32);

		int p = menu.getProgress();

		if (p > 0) {
			int p1 = p * 24 / menu.getMaxProgress();
			blit(poseStack, wx + menu.serializer.progressX, wy + menu.serializer.progressY, getBlitOffset(), 0, 18, 24 - p1, 17, 64, 32);
		}

		minecraft.getTextureManager().bind(BASE_TEXTURE);

		blit(poseStack, wx + menu.serializer.powerX, wy + menu.serializer.powerY, 0, 240, 14, 14);

		int e = menu.getEnergy();

		if (e > 0) {
			int e1 = menu.getEnergy() * 14 / menu.getEnergyCapacity();
			blit(poseStack, wx + menu.serializer.powerX, wy + menu.serializer.powerY + (14 - e1), 16, 240 + (14 - e1), 14, e1);
		}

		for (int i = 0; i < menu.entity.inputItems.length; i++) {
			blit(poseStack, wx + i * 18, wy, 0, 167, 18, 18);
		}

		blit(poseStack, wx + menu.serializer.batteryX, wy + menu.serializer.batteryY, 0, 167, 18, 18);

		if (menu.entity.outputItems.length > 1) {
			blit(poseStack, wx + menu.serializer.outputX, wy + menu.serializer.outputY, 0, 213, 47, 26);
		} else {
			blit(poseStack, wx + menu.serializer.outputX, wy + menu.serializer.outputY, 0, 186, 26, 26);
		}
	}
}
