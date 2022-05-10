package dev.ftb.mods.ftbic.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftbic.screen.sync.SyncedData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.world.entity.player.Inventory;

public class QuarryScreen extends ElectricBlockScreen<QuarryMenu> {
	private Component title;
	private boolean paused = false;

	public QuarryScreen(QuarryMenu m, Inventory inv, Component c) {
		super(m, inv, c);
		energyX = 126;
		energyY = 36;

		title = m.entity.createDisplayName();
		if (m.entity.paused) {
			paused = true;
			title = title.copy().append(" [Paused]");
		}
	}

	@Override
	protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
		this.font.draw(poseStack, title, (float)this.titleLabelX, (float)this.titleLabelY, 0x404040);
		this.font.draw(poseStack, this.playerInventoryTitle, (float)this.inventoryLabelX, (float)this.inventoryLabelY, 0x404040);
	}

	@Override
	protected void renderBg(PoseStack poseStack, float delta, int mouseX, int mouseY) {
		super.renderBg(poseStack, delta, mouseX, mouseY);

		drawSlot(poseStack, leftPos + 124, topPos + 52);

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 6; x++) {
				drawSlot(poseStack, leftPos + 7 + x * 18, topPos + 16 + y * 18);
			}
		}

		for (int i = 0; i < menu.entity.upgradeInventory.getSlots(); i++) {
			drawSlot(poseStack, leftPos + 151, topPos + 7 + i * 18);
		}

		drawPauseButton(poseStack, leftPos + 124, topPos + 16, mouseX, mouseY, menu.data.get(SyncedData.PAUSED));
	}

	@Override
	protected void containerTick() {
		super.containerTick();

		if (menu.entity.paused != paused) {
			paused = menu.entity.paused;
			title = paused ? title.copy().append(" [Paused]") : menu.entity.createDisplayName();
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (isIn((int) mouseX, (int) mouseY, leftPos + 124, topPos + 16, 18, 18)) {
			Minecraft.getInstance().player.connection.send(new ServerboundContainerButtonClickPacket(menu.containerId, 0));
			return true;
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}
}
