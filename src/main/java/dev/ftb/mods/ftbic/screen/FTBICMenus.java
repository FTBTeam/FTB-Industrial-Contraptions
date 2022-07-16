package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public interface FTBICMenus {
	DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, FTBIC.MOD_ID);

	static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> register(String id, IContainerFactory<T> factory) {
		return REGISTRY.register(id, () -> new MenuType<>(factory));
	}

	RegistryObject<MenuType<MachineMenu>> MACHINE = register("machine", MachineMenu::new);
	RegistryObject<MenuType<BasicGeneratorMenu>> BASIC_GENERATOR = register("basic_generator", BasicGeneratorMenu::new);
	RegistryObject<MenuType<GeothermalGeneratorMenu>> GEOTHERMAL_GENERATOR = register("geothermal_generator", GeothermalGeneratorMenu::new);
	RegistryObject<MenuType<SolarPanelMenu>> SOLAR_PANEL = register("solar_panel", SolarPanelMenu::new);
	RegistryObject<MenuType<NuclearReactorMenu>> NUCLEAR_REACTOR = register("nuclear_reactor", NuclearReactorMenu::new);
	RegistryObject<MenuType<BatteryBoxMenu>> BATTERY_BOX = register("battery_box", BatteryBoxMenu::new);
	RegistryObject<MenuType<AntimatterConstructorMenu>> ANTIMATTER_CONSTRUCTOR = register("antimatter_constructor", AntimatterConstructorMenu::new);
	RegistryObject<MenuType<PoweredCraftingTableMenu>> POWERED_CRAFTING_TABLE = register("powered_crafting_table", PoweredCraftingTableMenu::new);
	RegistryObject<MenuType<QuarryMenu>> QUARRY = register("quarry", QuarryMenu::new);
	RegistryObject<MenuType<PumpMenu>> PUMP = register("pump", PumpMenu::new);
}
