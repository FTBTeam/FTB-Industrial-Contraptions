package dev.ftb.mods.ftbic.client;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.client.gui.AntimatterConstructorScreen;
import dev.ftb.mods.ftbic.client.gui.BasicGeneratorScreen;
import dev.ftb.mods.ftbic.client.gui.BatteryBoxScreen;
import dev.ftb.mods.ftbic.client.gui.GeothermalGeneratorScreen;
import dev.ftb.mods.ftbic.client.gui.IronFurnaceScreen;
import dev.ftb.mods.ftbic.client.gui.MachineScreen;
import dev.ftb.mods.ftbic.client.gui.NuclearReactorScreen;
import dev.ftb.mods.ftbic.client.gui.PoweredCraftingTableScreen;
import dev.ftb.mods.ftbic.client.gui.PumpScreen;
import dev.ftb.mods.ftbic.client.gui.QuarryScreen;
import dev.ftb.mods.ftbic.client.gui.ReactorSimulatorScreen;
import dev.ftb.mods.ftbic.client.gui.SolarPanelScreen;
import dev.ftb.mods.ftbic.client.gui.TeleporterScreen;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.machine.DiggingBaseBlockEntity;
import dev.ftb.mods.ftbic.client.renderer.DiggingBeamRenderer;
import dev.ftb.mods.ftbic.client.renderer.NukeArrowRenderer;
import dev.ftb.mods.ftbic.entity.FTBICEntities;
import dev.ftb.mods.ftbic.integration.guideme.FTBICGuide;
import dev.ftb.mods.ftbic.screen.FTBICMenus;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@Mod(value = FTBIC.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = FTBIC.MOD_ID, value = Dist.CLIENT)
public final class FTBICClient {

	public FTBICClient(IEventBus eventBus, ModContainer container) {
		if (ModList.get().isLoaded("guideme")) {
			FTBICGuide.init();
		}
	}

	@SubscribeEvent
	public static void registerMenuScreens(RegisterMenuScreensEvent event) {
		event.register(FTBICMenus.MACHINE.get(), MachineScreen::new);
		event.register(FTBICMenus.BASIC_GENERATOR.get(), BasicGeneratorScreen::new);
		event.register(FTBICMenus.GEOTHERMAL_GENERATOR.get(), GeothermalGeneratorScreen::new);
		event.register(FTBICMenus.SOLAR_PANEL.get(), SolarPanelScreen::new);
		event.register(FTBICMenus.NUCLEAR_REACTOR.get(), NuclearReactorScreen::new);
		event.register(FTBICMenus.BATTERY_BOX.get(), BatteryBoxScreen::new);
		event.register(FTBICMenus.ANTIMATTER_CONSTRUCTOR.get(), AntimatterConstructorScreen::new);
		event.register(FTBICMenus.POWERED_CRAFTING_TABLE.get(), PoweredCraftingTableScreen::new);
		event.register(FTBICMenus.QUARRY.get(), QuarryScreen::new);
		event.register(FTBICMenus.PUMP.get(), PumpScreen::new);
		event.register(FTBICMenus.TELEPORTER.get(), TeleporterScreen::new);
		event.register(FTBICMenus.IRON_FURNACE.get(), IronFurnaceScreen::new);
		event.register(FTBICMenus.REACTOR_SIMULATOR.get(), ReactorSimulatorScreen::new);
	}

	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(FTBICEntities.NUKE_ARROW.get(), NukeArrowRenderer::new);
		@SuppressWarnings("unchecked")
		BlockEntityType<DiggingBaseBlockEntity> quarryType =
				(BlockEntityType<DiggingBaseBlockEntity>)
						(Object) FTBICElectricBlocks.QUARRY.blockEntity.get();
		@SuppressWarnings("unchecked")
		BlockEntityType<DiggingBaseBlockEntity> pumpType =
				(BlockEntityType<DiggingBaseBlockEntity>)
						(Object) FTBICElectricBlocks.PUMP.blockEntity.get();
		event.registerBlockEntityRenderer(quarryType, DiggingBeamRenderer::new);
		event.registerBlockEntityRenderer(pumpType, DiggingBeamRenderer::new);
	}

}
