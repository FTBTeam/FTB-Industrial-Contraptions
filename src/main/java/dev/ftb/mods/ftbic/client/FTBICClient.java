package dev.ftb.mods.ftbic.client;

import dev.ftb.mods.ftbic.FTBICCommon;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.screen.BasicGeneratorScreen;
import dev.ftb.mods.ftbic.screen.BatteryBoxScreen;
import dev.ftb.mods.ftbic.screen.FTBICMenus;
import dev.ftb.mods.ftbic.screen.GeothermalGeneratorScreen;
import dev.ftb.mods.ftbic.screen.MachineScreen;
import dev.ftb.mods.ftbic.screen.SolarPanelScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class FTBICClient extends FTBICCommon {
	@Override
	public void init() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
	}

	private void setup(FMLClientSetupEvent event) {
		ItemBlockRenderTypes.setRenderLayer(FTBICBlocks.REINFORCED_GLASS.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(FTBICBlocks.GLASS_CABLE.get(), RenderType.cutout());

		MenuScreens.register(FTBICMenus.MACHINE.get(), MachineScreen::new);
		MenuScreens.register(FTBICMenus.BASIC_GENERATOR.get(), BasicGeneratorScreen::new);
		MenuScreens.register(FTBICMenus.GEOTHERMAL_GENERATOR.get(), GeothermalGeneratorScreen::new);
		MenuScreens.register(FTBICMenus.SOLAR_PANEL.get(), SolarPanelScreen::new);
		MenuScreens.register(FTBICMenus.BATTERY_BOX.get(), BatteryBoxScreen::new);
	}
}
