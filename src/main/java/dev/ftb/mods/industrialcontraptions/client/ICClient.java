package dev.ftb.mods.industrialcontraptions.client;

import dev.ftb.mods.industrialcontraptions.IC;
import dev.ftb.mods.industrialcontraptions.client.gui.AntimatterConstructorScreen;
import dev.ftb.mods.industrialcontraptions.client.gui.BasicGeneratorScreen;
import dev.ftb.mods.industrialcontraptions.client.gui.BatteryBoxScreen;
import dev.ftb.mods.industrialcontraptions.client.gui.GeothermalGeneratorScreen;
import dev.ftb.mods.industrialcontraptions.client.gui.IronFurnaceScreen;
import dev.ftb.mods.industrialcontraptions.client.gui.MachineScreen;
import dev.ftb.mods.industrialcontraptions.client.gui.NuclearReactorScreen;
import dev.ftb.mods.industrialcontraptions.client.gui.PoweredCraftingTableScreen;
import dev.ftb.mods.industrialcontraptions.client.gui.PumpScreen;
import dev.ftb.mods.industrialcontraptions.client.gui.QuarryScreen;
import dev.ftb.mods.industrialcontraptions.client.gui.ReactorSimulatorScreen;
import dev.ftb.mods.industrialcontraptions.client.gui.SolarPanelScreen;
import dev.ftb.mods.industrialcontraptions.client.gui.TeleporterScreen;
import dev.ftb.mods.industrialcontraptions.block.ICElectricBlocks;
import dev.ftb.mods.industrialcontraptions.block.entity.machine.DiggingBaseBlockEntity;
import dev.ftb.mods.industrialcontraptions.client.renderer.DiggingBeamRenderer;
import dev.ftb.mods.industrialcontraptions.client.renderer.NukeArrowRenderer;
import dev.ftb.mods.industrialcontraptions.entity.ICEntities;
import dev.ftb.mods.industrialcontraptions.integration.guideme.ICGuide;
import dev.ftb.mods.industrialcontraptions.screen.ICMenus;
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

@Mod(value = IC.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = IC.MOD_ID, value = Dist.CLIENT)
public final class ICClient {

	public ICClient(IEventBus eventBus, ModContainer container) {
		if (ModList.get().isLoaded("guideme")) {
			ICGuide.init();
		}
	}

	@SubscribeEvent
	public static void registerMenuScreens(RegisterMenuScreensEvent event) {
		event.register(ICMenus.MACHINE.get(), MachineScreen::new);
		event.register(ICMenus.BASIC_GENERATOR.get(), BasicGeneratorScreen::new);
		event.register(ICMenus.GEOTHERMAL_GENERATOR.get(), GeothermalGeneratorScreen::new);
		event.register(ICMenus.SOLAR_PANEL.get(), SolarPanelScreen::new);
		event.register(ICMenus.NUCLEAR_REACTOR.get(), NuclearReactorScreen::new);
		event.register(ICMenus.BATTERY_BOX.get(), BatteryBoxScreen::new);
		event.register(ICMenus.ANTIMATTER_CONSTRUCTOR.get(), AntimatterConstructorScreen::new);
		event.register(ICMenus.POWERED_CRAFTING_TABLE.get(), PoweredCraftingTableScreen::new);
		event.register(ICMenus.QUARRY.get(), QuarryScreen::new);
		event.register(ICMenus.PUMP.get(), PumpScreen::new);
		event.register(ICMenus.TELEPORTER.get(), TeleporterScreen::new);
		event.register(ICMenus.IRON_FURNACE.get(), IronFurnaceScreen::new);
		event.register(ICMenus.REACTOR_SIMULATOR.get(), ReactorSimulatorScreen::new);
	}

	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(ICEntities.NUKE_ARROW.get(), NukeArrowRenderer::new);
		@SuppressWarnings("unchecked")
		BlockEntityType<DiggingBaseBlockEntity> quarryType =
				(BlockEntityType<DiggingBaseBlockEntity>)
						(Object) ICElectricBlocks.QUARRY.blockEntity.get();
		@SuppressWarnings("unchecked")
		BlockEntityType<DiggingBaseBlockEntity> pumpType =
				(BlockEntityType<DiggingBaseBlockEntity>)
						(Object) ICElectricBlocks.PUMP.blockEntity.get();
		event.registerBlockEntityRenderer(quarryType, DiggingBeamRenderer::new);
		event.registerBlockEntityRenderer(pumpType, DiggingBeamRenderer::new);
	}

}
