package dev.ftb.mods.ftbic.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.entity.generator.GeothermalGeneratorTank;
import dev.ftb.mods.ftbic.screen.sync.SyncedData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

import java.text.NumberFormat;

public class GeothermalGeneratorScreen extends ElectricBlockScreen<GeothermalGeneratorMenu> {
	public GeothermalGeneratorScreen(GeothermalGeneratorMenu m, Inventory inv, Component c) {
		super(m, inv, c);
		energyX = 63;
		energyY = 36;
	}

	@Override
	protected void renderBg(PoseStack poseStack, float delta, int mouseX, int mouseY) {
		super.renderBg(poseStack, delta, mouseX, mouseY);

		drawTank(poseStack, leftPos + 97, topPos + 16, new FluidStack(Fluids.LAVA, menu.data.get(SyncedData.BAR)), FTBICConfig.MACHINES.GEOTHERMAL_GENERATOR_TANK_SIZE.get());

		drawSlot(poseStack, leftPos + 61, topPos + 16);
		drawSlot(poseStack, leftPos + 61, topPos + 52);
	}

	@Override
	protected void renderTooltip(PoseStack poseStack, int mouseX, int mouseY) {
		super.renderTooltip(poseStack, mouseX, mouseY);

		if (isIn(mouseX, mouseY, leftPos + 97, topPos + 16, 18, 54)) {
			int maxAmount = this.menu.entity.getTankOptional()
					.map(GeothermalGeneratorTank::getCapacity)
					.orElse(1000);

			var numberFormat = NumberFormat.getInstance();
			renderTooltip(poseStack, new TextComponent("%s / %s".formatted(numberFormat.format(menu.data.get(SyncedData.BAR)), numberFormat.format(maxAmount))).withStyle(ChatFormatting.GRAY), mouseX, mouseY);
		}
	}
}
