package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public interface FTBICMenus {
	DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, FTBIC.MOD_ID);

	static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> register(String id, IContainerFactory<T> factory) {
		return REGISTRY.register(id, () -> new MenuType<>(factory, FeatureFlags.DEFAULT_FLAGS));
	}

	DeferredHolder<MenuType<?>, MenuType<MachineMenu>> MACHINE = register("machine", MachineMenu::new);
	DeferredHolder<MenuType<?>, MenuType<BasicGeneratorMenu>> BASIC_GENERATOR = register("basic_generator", BasicGeneratorMenu::new);
	DeferredHolder<MenuType<?>, MenuType<GeothermalGeneratorMenu>> GEOTHERMAL_GENERATOR = register("geothermal_generator", GeothermalGeneratorMenu::new);
	DeferredHolder<MenuType<?>, MenuType<SolarPanelMenu>> SOLAR_PANEL = register("solar_panel", SolarPanelMenu::new);
	DeferredHolder<MenuType<?>, MenuType<NuclearReactorMenu>> NUCLEAR_REACTOR = register("nuclear_reactor", NuclearReactorMenu::new);
	DeferredHolder<MenuType<?>, MenuType<BatteryBoxMenu>> BATTERY_BOX = register("battery_box", BatteryBoxMenu::new);
	DeferredHolder<MenuType<?>, MenuType<AntimatterConstructorMenu>> ANTIMATTER_CONSTRUCTOR = register("antimatter_constructor", AntimatterConstructorMenu::new);
	DeferredHolder<MenuType<?>, MenuType<PoweredCraftingTableMenu>> POWERED_CRAFTING_TABLE = register("powered_crafting_table", PoweredCraftingTableMenu::new);
	DeferredHolder<MenuType<?>, MenuType<QuarryMenu>> QUARRY = register("quarry", QuarryMenu::new);
	DeferredHolder<MenuType<?>, MenuType<PumpMenu>> PUMP = register("pump", PumpMenu::new);
	DeferredHolder<MenuType<?>, MenuType<TeleporterMenu>> TELEPORTER = register("teleporter", TeleporterMenu::new);
	DeferredHolder<MenuType<?>, MenuType<IronFurnaceMenu>> IRON_FURNACE = register("iron_furnace", IronFurnaceMenu::new);
	DeferredHolder<MenuType<?>, MenuType<ReactorSimulatorMenu>> REACTOR_SIMULATOR = register("reactor_simulator", ReactorSimulatorMenu::new);
}
