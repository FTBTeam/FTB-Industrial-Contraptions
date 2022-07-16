package dev.ftb.mods.ftbic.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.entity.generator.NuclearReactorBlockEntity;
import dev.ftb.mods.ftbic.item.reactor.NuclearReactor;
import dev.ftb.mods.ftbic.item.reactor.ReactorItem;
import dev.ftb.mods.ftbic.screen.sync.SyncedData;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

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
		drawSmallQuestionButton(poseStack, leftPos + 94, topPos + 5, mouseX, mouseY);
		drawSmallRedstoneButton(poseStack, leftPos + 105, topPos + 127, mouseX, mouseY, menu.data.get(SyncedData.ALLOW_REDSTONE_CONTROL));
	}

	@Override
	protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
		super.renderLabels(poseStack, mouseX, mouseY);

		drawCenteredString(poseStack, font, menu.data.get(SyncedData.PAUSED) ? Component.literal("Paused") : FTBICUtils.formatEnergy(menu.data.get(NuclearReactorBlockEntity.ENERGY_OUTPUT)), 142, 6, 0xFFFFFF);
		drawCenteredString(poseStack, font, FTBICUtils.formatHeat(menu.data.get(NuclearReactorBlockEntity.HEAT)), 142, 128, 0xFFFFFF);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (isIn((int) mouseX, (int) mouseY, leftPos + 105, topPos + 5, 9, 10)) {
			minecraft.player.connection.send(new ServerboundContainerButtonClickPacket(menu.containerId, 0));
			return true;
		} else if (isIn((int) mouseX, (int) mouseY, leftPos + 105, topPos + 127, 9, 10)) {
			minecraft.player.connection.send(new ServerboundContainerButtonClickPacket(menu.containerId, 1));
			return true;
		} else if (isIn((int) mouseX, (int) mouseY, leftPos + 94, topPos + 5, 9, 10)) {
			List<Component> info = new ArrayList<>();

			NuclearReactor reactor = new NuclearReactor(new ItemStack[menu.entity.reactor.inputItems.length]);
			reactor.paused = false;
			reactor.simulation = true;
			reactor.heat = menu.data.get(NuclearReactorBlockEntity.HEAT);

			for (int i = 0; i < 9 * 6; i++) {
				if (menu.entity.reactor.inputItems[i].getItem() instanceof ReactorItem) {
					reactor.inputItems[i] = menu.entity.reactor.inputItems[i].copy();
				} else {
					reactor.inputItems[i] = ItemStack.EMPTY;
				}
			}

			int runTime = 0;
			double maxEnergyOutput = 0D;
			double totalEnergyOutput = 0D;

			while (true) {
				boolean stop = reactor.tick();
				runTime++;

				maxEnergyOutput = Math.max(maxEnergyOutput, reactor.energyOutput);
				totalEnergyOutput += reactor.energyOutput;

				if (stop) {
					break;
				} else if (runTime >= 10_000_000) {
					info.add(Component.literal("Simulation ran for too long!").withStyle(ChatFormatting.RED));
					break;
				}
			}

			if (maxEnergyOutput <= 0D) {
				info.add(Component.literal("Insert Fuel Rods to check run time!"));
			} else {
				info.add(Component.literal(String.format("This reactor will run for %,d s", runTime)));

				if (reactor.heat >= reactor.maxHeat) {
					info.add(Component.literal("This reactor will explode with " + Mth.ceil(reactor.explosionRadius) + " block radius").withStyle(ChatFormatting.RED));
				} else {
					info.add(Component.literal("This reactor will not explode").withStyle(ChatFormatting.GREEN));
				}

				info.add(Component.literal("Max energy generated: ").append(FTBICUtils.formatEnergy(maxEnergyOutput)).append("/t"));
				info.add(Component.literal("Total energy generated: ").append(FTBICUtils.formatEnergy(totalEnergyOutput)));
			}

			minecraft.setScreen(new NuclearReactorInfoScreen(this, info));
			return true;
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}
}
