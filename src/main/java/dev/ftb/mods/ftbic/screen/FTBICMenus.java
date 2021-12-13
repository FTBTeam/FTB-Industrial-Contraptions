package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public interface FTBICMenus {
	DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.CONTAINERS, FTBIC.MOD_ID);

	static <T extends AbstractContainerMenu> Supplier<MenuType<T>> register(String id, IContainerFactory<T> factory) {
		return REGISTRY.register(id, () -> new MenuType<>(factory));
	}

	Supplier<MenuType<MachineMenu>> MACHINE = register("machine", MachineMenu::new);
	Supplier<MenuType<BasicGeneratorMenu>> BASIC_GENERATOR = register("basic_generator", BasicGeneratorMenu::new);
	Supplier<MenuType<GeothermalGeneratorMenu>> GEOTHERMAL_GENERATOR = register("geothermal_generator", GeothermalGeneratorMenu::new);
	Supplier<MenuType<SolarPanelMenu>> SOLAR_PANEL = register("solar_panel", SolarPanelMenu::new);
	Supplier<MenuType<NuclearReactorMenu>> NUCLEAR_REACTOR = register("nuclear_reactor", NuclearReactorMenu::new);
	Supplier<MenuType<BatteryBoxMenu>> BATTERY_BOX = register("battery_box", BatteryBoxMenu::new);
	Supplier<MenuType<AntimatterConstructorMenu>> ANTIMATTER_CONSTRUCTOR = register("antimatter_constructor", AntimatterConstructorMenu::new);
	Supplier<MenuType<PoweredCraftingTableMenu>> POWERED_CRAFTING_TABLE = register("powered_crafting_table", PoweredCraftingTableMenu::new);
	Supplier<MenuType<QuarryMenu>> QUARRY = register("quarry", QuarryMenu::new);
	Supplier<MenuType<PumpMenu>> PUMP = register("pump", PumpMenu::new);
}
