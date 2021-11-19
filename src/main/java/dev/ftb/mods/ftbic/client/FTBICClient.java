package dev.ftb.mods.ftbic.client;

import dev.ftb.mods.ftbic.FTBICCommon;
import dev.ftb.mods.ftbic.block.ElectricBlock;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.screen.BasicGeneratorScreen;
import dev.ftb.mods.ftbic.screen.BatteryBoxScreen;
import dev.ftb.mods.ftbic.screen.FTBICMenus;
import dev.ftb.mods.ftbic.screen.GeothermalGeneratorScreen;
import dev.ftb.mods.ftbic.screen.MachineScreen;
import dev.ftb.mods.ftbic.screen.SolarPanelScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class FTBICClient extends FTBICCommon {
	@Override
	public void init() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
	}

	private void setup(FMLClientSetupEvent event) {
		ItemBlockRenderTypes.setRenderLayer(FTBICBlocks.REINFORCED_GLASS.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(FTBICBlocks.IV_CABLE.get(), RenderType.cutout());

		MenuScreens.register(FTBICMenus.MACHINE.get(), MachineScreen::new);
		MenuScreens.register(FTBICMenus.BASIC_GENERATOR.get(), BasicGeneratorScreen::new);
		MenuScreens.register(FTBICMenus.GEOTHERMAL_GENERATOR.get(), GeothermalGeneratorScreen::new);
		MenuScreens.register(FTBICMenus.SOLAR_PANEL.get(), SolarPanelScreen::new);
		MenuScreens.register(FTBICMenus.BATTERY_BOX.get(), BatteryBoxScreen::new);

		ClientRegistry.bindTileEntityRenderer((BlockEntityType) FTBICElectricBlocks.QUARRY.blockEntity.get(), QuarryRenderer::new);
	}

	@Override
	public void playLaserSound(long tick, BlockState state, double x, double minY, double z) {
		boolean low = tick % 160L == 0L;
		boolean high = tick % 40L == 0L;

		if (!((low || high) && state.getValue(ElectricBlock.ACTIVE))) {
			return;
		}

		Player player = Minecraft.getInstance().player;

		if (player == null) {
			return;
		}

		double y = Math.max(minY, player.getEyeY());

		if (low) {
			player.level.playLocalSound(x, y, z, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 2F, 0.5F, false);
		}

		if (high) {
			player.level.playLocalSound(x, y, z, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 2F, 2F, false);
		}
	}
}
