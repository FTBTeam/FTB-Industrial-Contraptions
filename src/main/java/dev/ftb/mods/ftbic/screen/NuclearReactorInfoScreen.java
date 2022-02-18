package dev.ftb.mods.ftbic.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.List;

public class NuclearReactorInfoScreen extends Screen {
	public final NuclearReactorScreen prev;
	public final List<Component> info;

	public NuclearReactorInfoScreen(NuclearReactorScreen p, List<Component> i) {
		super(FTBICElectricBlocks.NUCLEAR_REACTOR.block.get().getName());
		prev = p;
		info = i;
	}

	@Override
	protected void init() {
		addWidget(new Button(this.width / 2 - 100, this.height / 4 + 120, 200, 20, CommonComponents.GUI_DONE, arg -> minecraft.setScreen(prev)));
	}

	@Override
	public void onClose() {
		minecraft.setScreen(prev);
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
		this.renderBackground(poseStack);
		drawCenteredString(poseStack, this.font, this.title, this.width / 2, 40, 0xFFFFFF);

		int x = width / 2;
		int y = (height - info.size() * 13) / 2;

		for (int i = 0; i < info.size(); i++) {
			drawCenteredString(poseStack, this.font, info.get(i), x, y + i * 13, 0xFFFFFF);
		}

		super.render(poseStack, mouseX, mouseY, delta);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
