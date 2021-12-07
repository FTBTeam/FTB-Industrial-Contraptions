package dev.ftb.mods.ftbic.client;

import dev.ftb.mods.ftbic.FTBICCommon;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.screen.AntimatterConstructorScreen;
import dev.ftb.mods.ftbic.screen.BasicGeneratorScreen;
import dev.ftb.mods.ftbic.screen.BatteryBoxScreen;
import dev.ftb.mods.ftbic.screen.FTBICMenus;
import dev.ftb.mods.ftbic.screen.GeothermalGeneratorScreen;
import dev.ftb.mods.ftbic.screen.MachineScreen;
import dev.ftb.mods.ftbic.screen.PoweredCraftingTableScreen;
import dev.ftb.mods.ftbic.screen.PumpScreen;
import dev.ftb.mods.ftbic.screen.QuarryScreen;
import dev.ftb.mods.ftbic.screen.SolarPanelScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
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
		ItemBlockRenderTypes.setRenderLayer(FTBICBlocks.LANDMARK.get(), RenderType.cutout());

		MenuScreens.register(FTBICMenus.MACHINE.get(), MachineScreen::new);
		MenuScreens.register(FTBICMenus.BASIC_GENERATOR.get(), BasicGeneratorScreen::new);
		MenuScreens.register(FTBICMenus.GEOTHERMAL_GENERATOR.get(), GeothermalGeneratorScreen::new);
		MenuScreens.register(FTBICMenus.SOLAR_PANEL.get(), SolarPanelScreen::new);
		MenuScreens.register(FTBICMenus.BATTERY_BOX.get(), BatteryBoxScreen::new);
		MenuScreens.register(FTBICMenus.ANTIMATTER_CONSTRUCTOR.get(), AntimatterConstructorScreen::new);
		MenuScreens.register(FTBICMenus.POWERED_CRAFTING_TABLE.get(), PoweredCraftingTableScreen::new);
		MenuScreens.register(FTBICMenus.QUARRY.get(), QuarryScreen::new);
		MenuScreens.register(FTBICMenus.PUMP.get(), PumpScreen::new);

		ClientRegistry.bindTileEntityRenderer((BlockEntityType) FTBICElectricBlocks.QUARRY.blockEntity.get(), DiggingBlockRenderer::new);
		ClientRegistry.bindTileEntityRenderer((BlockEntityType) FTBICElectricBlocks.PUMP.blockEntity.get(), DiggingBlockRenderer::new);
	}

	@Override
	public void playLaserSound(long tick, double x, double minY, double maxY, double z) {
		boolean high = tick % 10L == 0L;

		if (!high) {
			return;
		}

		Player player = Minecraft.getInstance().player;

		if (player == null) {
			return;
		}

		double y = Mth.clamp(player.getEyeY(), minY, maxY);

		player.level.playLocalSound(x, y, z, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 1F, 0.5F, false);
	}
}
