package dev.ftb.mods.ftbic.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.entity.generator.NuclearReactorBlockEntity;
import dev.ftb.mods.ftbic.screen.sync.SyncedData;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class NuclearReactorScreen extends ElectricBlockScreen<NuclearReactorMenu> {
	public static final ResourceLocation NUCLEAR_REACTOR_TEXTURE = new ResourceLocation(FTBIC.MOD_ID, "textures/gui/nuclear_reactor.png");

	public NuclearReactorScreen(NuclearReactorMenu m, Inventory inv, Component c) {
		super(m, inv, c);
		imageHeight = 222;
		inventoryLabelY = imageHeight - 94;
	}

	@Override
	public ResourceLocation getScreenTexture() {
		return NUCLEAR_REACTOR_TEXTURE;
	}

	@Override
	protected void renderBg(PoseStack poseStack, float delta, int mouseX, int mouseY) {
		super.renderBg(poseStack, delta, mouseX, mouseY);

		drawNuclearBar(poseStack, leftPos + 115, topPos + 5, !menu.data.get(SyncedData.PAUSED) && menu.data.get(NuclearReactorBlockEntity.ENERGY_OUTPUT) > 0);
		drawHeatBar(poseStack, leftPos + 115, topPos + 127, Mth.clamp(menu.data.get(NuclearReactorBlockEntity.HEAT) / (float) menu.data.get(NuclearReactorBlockEntity.MAX_HEAT), 0F, 1F));
		drawSmallPauseButton(poseStack, leftPos + 105, topPos + 5, mouseX, mouseY, menu.data.get(SyncedData.PAUSED));
	}

	@Override
	protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
		super.renderLabels(poseStack, mouseX, mouseY);

		drawCenteredString(poseStack, font, menu.data.get(SyncedData.PAUSED) ? new TextComponent("Paused") : FTBICUtils.formatEnergy(menu.data.get(NuclearReactorBlockEntity.ENERGY_OUTPUT)), 142, 6, 0xFFFFFF);
		drawCenteredString(poseStack, font, FTBICUtils.formatHeat(menu.data.get(NuclearReactorBlockEntity.HEAT)), 142, 128, 0xFFFFFF);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (isIn((int) mouseX, (int) mouseY, leftPos + 105, topPos + 5, 9, 10)) {
			Minecraft.getInstance().player.connection.send(new ServerboundContainerButtonClickPacket(menu.containerId, 0));
			return true;
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}
}
