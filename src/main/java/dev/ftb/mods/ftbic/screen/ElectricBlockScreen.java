package dev.ftb.mods.ftbic.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.entity.machine.MachineBlockEntity;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;

import java.util.Collections;

public class ElectricBlockScreen<T extends ElectricBlockMenu<?>> extends AbstractContainerScreen<T> {
	public static final ResourceLocation BASE_TEXTURE = new ResourceLocation(FTBIC.MOD_ID, "textures/gui/base.png");
	public int energyX = -1;
	public int energyY = -1;

	public ElectricBlockScreen(T m, Inventory inv, Component c) {
		super(m, inv, c);
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
		renderBackground(poseStack);
		super.render(poseStack, mouseX, mouseY, delta);
		renderTooltip(poseStack, mouseX, mouseY);
	}

	@Override
	protected void renderTooltip(PoseStack poseStack, int mouseX, int mouseY) {
		super.renderTooltip(poseStack, mouseX, mouseY);

		if (energyX != -1 && energyY != -1 && mouseX >= leftPos + energyX && mouseY >= topPos + energyY && mouseX < leftPos + energyX + 14 && mouseY < topPos + energyY + 14 && menu.player.inventory.getCarried().isEmpty()) {
			double capacity = menu.entity.energyCapacity;

			if (menu.entity instanceof MachineBlockEntity) {
				capacity += ((MachineBlockEntity) menu.entity).upgradeInventory.countUpgrades(FTBICItems.ENERGY_STORAGE_UPGRADE.get()) * FTBICConfig.STORAGE_UPGRADE;
			}

			double energy = menu.getEnergyBar() * capacity / 30000;

			renderWrappedToolTip(poseStack, Collections.singletonList(new TextComponent("").append(FTBICUtils.formatEnergy(energy).withStyle(ChatFormatting.GRAY)).append(" / ").append(FTBICUtils.formatEnergy(capacity).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY)), mouseX, mouseY, font);
		}
	}

	public void drawProgressBar(PoseStack poseStack, int x, int y, int progress) {
		if (progress < 24) {
			blit(poseStack, x + progress, y, 86 + progress, 167, 24 - progress, 17);
		}

		if (progress > 0) {
			blit(poseStack, x, y, 86, 185, progress, 17);
		}
	}

	public int getEnergyType() {
		return 0;
	}

	public void drawEnergy(PoseStack poseStack, int x, int y, int energy) {
		switch (getEnergyType()) {
			case 1: {
				if (energy < 14) {
					blit(poseStack, x + energy, y, 90 + energy, 240, 14 - energy, 14);
				}

				if (energy > 0) {
					blit(poseStack, x, y, 105, 240, energy, 14);
				}

				break;
			}
			case 2: {
				if (energy < 14) {
					blit(poseStack, x + energy, y, 120 + energy, 240, 14 - energy, 14);
				}

				if (energy > 0) {
					blit(poseStack, x, y, 135, 240, energy, 14);
				}

				break;
			}
			default: {
				if (energy < 14) {
					blit(poseStack, x, y, 0, 240, 14, 14 - energy);
				}

				if (energy > 0) {
					blit(poseStack, x, y + (14 - energy), 15, 240 + (14 - energy), 14, energy);
				}

				break;
			}

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

		minecraft.getTextureManager().bind(TextureAtlas.LOCATION_BLOCKS);
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

		if (energyX != -1 && energyY != -1) {
			int e = menu.getEnergyBar() * 14 / 30000;
			drawEnergy(poseStack, leftPos + energyX, topPos + energyY, e);
		}
	}
}
