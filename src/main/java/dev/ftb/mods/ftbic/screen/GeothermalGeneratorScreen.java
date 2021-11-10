package dev.ftb.mods.ftbic.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftbic.FTBICConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

public class GeothermalGeneratorScreen extends ElectricBlockScreen<GeothermalGeneratorMenu> {
	public GeothermalGeneratorScreen(GeothermalGeneratorMenu m, Inventory inv, Component c) {
		super(m, inv, c);
	}

	@Override
	protected void renderBg(PoseStack poseStack, float delta, int mouseX, int mouseY) {
		super.renderBg(poseStack, delta, mouseX, mouseY);

		drawTank(poseStack, leftPos + 97, topPos + 16, new FluidStack(Fluids.LAVA, menu.getFluidAmount()), FTBICConfig.GEOTHERMAL_GENERATOR_TANK_SIZE);
		drawEnergy(poseStack, leftPos + 63, topPos + 36, menu.getEnergyBar());

		drawSlot(poseStack, leftPos + 61, topPos + 16);
		drawSlot(poseStack, leftPos + 61, topPos + 52);
	}
}
