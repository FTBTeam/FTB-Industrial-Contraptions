package dev.ftb.mods.ftbic.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.fluids.FluidStack;

public class ElectricBlockScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
	public static final ResourceLocation BASE_TEXTURE = new ResourceLocation(FTBIC.MOD_ID, "textures/gui/base.png");

	public ElectricBlockScreen(T m, Inventory inv, Component c) {
		super(m, inv, c);
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
		renderBackground(poseStack);
		super.render(poseStack, mouseX, mouseY, delta);
		renderTooltip(poseStack, mouseX, mouseY);
	}

	public void drawProgressBar(PoseStack poseStack, int x, int y, int progress) {
		if (progress < 24) {
			blit(poseStack, x + progress, y, getBlitOffset(), progress, 0, 24 - progress, 17, 64, 32);
		}

		if (progress > 0) {
			blit(poseStack, x, y, getBlitOffset(), 0, 18, progress, 17, 64, 32);
		}
	}

	public void drawEnergy(PoseStack poseStack, int x, int y, int energy) {
		if (energy < 14) {
			blit(poseStack, x, y, 0, 240, 14, 14 - energy);
		}

		if (energy > 0) {
			blit(poseStack, x, y + (14 - energy), 15, 240 + (14 - energy), 14, energy);
		}
	}

	public void drawFuel(PoseStack poseStack, int x, int y, int fuel) {
		if (fuel < 14) {
			blit(poseStack, x, y, 30, 240, 14, 14 - fuel);
		}

		if (fuel > 0) {
			blit(poseStack, x, y + (14 - fuel), 45, 240 + (14 - fuel), 14, fuel);
		}
	}

	public void drawSun(PoseStack poseStack, int x, int y, int fuel) {
		if (fuel < 14) {
			blit(poseStack, x, y, 60, 240, 14, 14 - fuel);
		}

		if (fuel > 0) {
			blit(poseStack, x, y + (14 - fuel), 75, 240 + (14 - fuel), 14, fuel);
		}
	}

	public void drawSlot(PoseStack poseStack, int x, int y) {
		blit(poseStack, x, y, 0, 167, 18, 18);
	}

	public void drawLargeSlot(PoseStack poseStack, int x, int y) {
		blit(poseStack, x, y, 0, 186, 26, 26);
	}

	public void drawCombinedSlot(PoseStack poseStack, int x, int y) {
		blit(poseStack, x, y, 0, 213, 47, 26);
	}

	public void drawTank(PoseStack poseStack, int x, int y, FluidStack fluid, int capacity) {
		blit(poseStack, x, y, 48, 167, 18, 54);

		// render fluid here properly

		double d = fluid.getAmount() / (double) capacity;
		int h = Mth.ceil(d * 52);

		if (h > 0) {
			fillGradient(poseStack, x + 1, y + 1 + (52 - h), x + 17, y + 53, 0xFFFFB600, 0xFFD84C45);
		}

		minecraft.getTextureManager().bind(BASE_TEXTURE);
		blit(poseStack, x, y, 67, 167, 18, 54);
	}

	@Override
	protected void renderBg(PoseStack poseStack, float delta, int mouseX, int mouseY) {
		RenderSystem.color4f(1F, 1F, 1F, 1F);
		RenderSystem.defaultBlendFunc();
		minecraft.getTextureManager().bind(BASE_TEXTURE);
		int x = leftPos;
		int y = topPos;
		blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);
	}
}
