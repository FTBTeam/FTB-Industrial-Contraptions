package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.item.reactor.CoolantItem;
import dev.ftb.mods.ftbic.item.reactor.FuelRodItem;
import dev.ftb.mods.ftbic.item.reactor.HeatExchangerItem;
import dev.ftb.mods.ftbic.item.reactor.HeatVentItem;
import dev.ftb.mods.ftbic.item.reactor.NeutronReflectorItem;
import dev.ftb.mods.ftbic.item.reactor.ReactorPlatingItem;
import dev.ftb.mods.ftbic.util.EnergyArmorMaterial;
import dev.ftb.mods.ftbic.util.EnergyTier;
import dev.ftb.mods.ftbic.world.ResourceElements;
import dev.ftb.mods.ftbic.world.ResourceType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface FTBICItems {
	DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, FTBIC.MOD_ID);
	List<MaterialItem> MATERIALS = new ArrayList<>();

	static Supplier<BlockItem> blockItem(String id, Supplier<Block> sup) {
		return REGISTRY.register(id, () -> new BlockItem(sup.get(), new Item.Properties().tab(FTBIC.TAB)));
	}

	static Supplier<Item> basicItem(String id) {
		return REGISTRY.register(id, () -> new Item(new Item.Properties().tab(FTBIC.TAB)));
	}

	static MaterialItem material(String id) {
		MaterialItem m = new MaterialItem(id);
		m.item = basicItem(id);
		MATERIALS.add(m);
		return m;
	}

	/**
	 * Allows you to get an element from the registry based on the element type and the resource element specifically
	 *
	 * @param element the resource element, lead, tin, etc
	 * @param type    the type of the resource you want, dust, chunk, rod, etc
	 *
	 * @return an {@link Optional<Supplier<Item>>} of the item. Sometimes empty if the item does not exist.
	 */
	static Optional<Supplier<Item>> getResourceFromType(ResourceElements element, ResourceType type) {
		return RESOURCE_TYPE_MAP.get(type).entrySet().stream().filter(e -> e.getKey() == element)
				.findFirst()
				.map(Map.Entry::getValue);
	}

	Supplier<BlockItem> RUBBER_SHEET = blockItem("rubber_sheet", FTBICBlocks.RUBBER_SHEET);
	Supplier<BlockItem> REINFORCED_STONE = blockItem("reinforced_stone", FTBICBlocks.REINFORCED_STONE);
	Supplier<BlockItem> REINFORCED_GLASS = blockItem("reinforced_glass", FTBICBlocks.REINFORCED_GLASS);
	Supplier<BlockItem> MACHINE_BLOCK = blockItem("machine_block", FTBICBlocks.MACHINE_BLOCK);
	Supplier<BlockItem> ADVANCED_MACHINE_BLOCK = blockItem("advanced_machine_block", FTBICBlocks.ADVANCED_MACHINE_BLOCK);
	Supplier<BlockItem> IRON_FURNACE = blockItem("iron_furnace", FTBICBlocks.IRON_FURNACE);
	Supplier<BlockItem> LV_CABLE = blockItem("lv_cable", FTBICBlocks.LV_CABLE);
	Supplier<BlockItem> MV_CABLE = blockItem("mv_cable", FTBICBlocks.MV_CABLE);
	Supplier<BlockItem> HV_CABLE = blockItem("hv_cable", FTBICBlocks.HV_CABLE);
	Supplier<BlockItem> EV_CABLE = blockItem("ev_cable", FTBICBlocks.EV_CABLE);
	Supplier<BlockItem> IV_CABLE = blockItem("iv_cable", FTBICBlocks.IV_CABLE);
	Supplier<BlockItem> BURNT_CABLE = blockItem("burnt_cable", FTBICBlocks.BURNT_CABLE);
	Supplier<BlockItem> LANDMARK = blockItem("landmark", FTBICBlocks.LANDMARK);
	Supplier<BlockItem> EXFLUID = blockItem("exfluid", FTBICBlocks.EXFLUID);
	Supplier<BlockItem> NUCLEAR_REACTOR_CHAMBER = blockItem("nuclear_reactor_chamber", FTBICBlocks.NUCLEAR_REACTOR_CHAMBER);
	Supplier<BlockItem> NUKE = blockItem("nuke", FTBICBlocks.NUKE);
	Supplier<BlockItem> ACTIVE_NUKE = blockItem("active_nuke", FTBICBlocks.ACTIVE_NUKE);

	MaterialItem INDUSTRIAL_GRADE_METAL = material("industrial_grade_metal");
	MaterialItem RUBBER = material("rubber");
	MaterialItem SILICON = material("silicon");
	MaterialItem FUSE = material("fuse");
	MaterialItem COPPER_COIL = material("copper_coil");
	MaterialItem MIXED_METAL_BLEND = material("mixed_metal_blend");
	MaterialItem ADVANCED_ALLOY = material("advanced_alloy");
	MaterialItem IRIDIUM_ALLOY = material("iridium_alloy");
	MaterialItem COAL_BALL = material("coal_ball");
	MaterialItem COMPRESSED_COAL_BALL = material("compressed_coal_ball");
	MaterialItem GRAPHENE = material("graphene");
	MaterialItem CARBON_FIBERS = material("carbon_fibers");
	MaterialItem CARBON_FIBER_MESH = material("carbon_fiber_mesh");
	MaterialItem CARBON_PLATE = material("carbon_plate");
	MaterialItem SCRAP = material("scrap");
	MaterialItem SCRAP_BOX = material("scrap_box");
	MaterialItem ELECTRONIC_CIRCUIT = material("electronic_circuit");
	MaterialItem ADVANCED_CIRCUIT = material("advanced_circuit");
	MaterialItem IRIDIUM_CIRCUIT = material("iridium_circuit");
	MaterialItem EMPTY_CAN = material("empty_can");
	MaterialItem ANTIMATTER = material("antimatter");
	MaterialItem ANTIMATTER_CRYSTAL = material("antimatter_crystal");
	MaterialItem ENERGY_CRYSTAL = material("energy_crystal");
	MaterialItem DENSE_COPPER_PLATE = material("dense_copper_plate");

	Supplier<Item> SINGLE_USE_BATTERY = REGISTRY.register("single_use_battery", () -> new BatteryItem(BatteryType.SINGLE_USE, EnergyTier.LV, FTBICConfig.getRaw(FTBICConfig.ENERGY.SINGLE_USE_BATTERY_CAPACITY)));
	Supplier<Item> LV_BATTERY = REGISTRY.register("lv_battery", () -> new BatteryItem(BatteryType.RECHARGEABLE, EnergyTier.LV, FTBICConfig.getRaw(FTBICConfig.ENERGY.LV_BATTERY_CAPACITY)));
	Supplier<Item> MV_BATTERY = REGISTRY.register("mv_battery", () -> new BatteryItem(BatteryType.RECHARGEABLE, EnergyTier.MV, FTBICConfig.getRaw(FTBICConfig.ENERGY.MV_BATTERY_CAPACITY)));
	Supplier<Item> HV_BATTERY = REGISTRY.register("hv_battery", () -> new BatteryItem(BatteryType.RECHARGEABLE, EnergyTier.HV, FTBICConfig.getRaw(FTBICConfig.ENERGY.HV_BATTERY_CAPACITY)));
	Supplier<Item> EV_BATTERY = REGISTRY.register("ev_battery", () -> new BatteryItem(BatteryType.RECHARGEABLE, EnergyTier.EV, FTBICConfig.getRaw(FTBICConfig.ENERGY.EV_BATTERY_CAPACITY)));
	Supplier<Item> CREATIVE_BATTERY = REGISTRY.register("creative_battery", () -> new BatteryItem(BatteryType.CREATIVE, EnergyTier.IV, Integer.MAX_VALUE));
	Supplier<Item> FLUID_CELL = REGISTRY.register("fluid_cell", FluidCellItem::new);
	Supplier<Item> SMALL_COOLANT_CELL = REGISTRY.register("small_coolant_cell", () -> new CoolantItem(10_000));
	Supplier<Item> MEDIUM_COOLANT_CELL = REGISTRY.register("medium_coolant_cell", () -> new CoolantItem(30_000));
	Supplier<Item> LARGE_COOLANT_CELL = REGISTRY.register("large_coolant_cell", () -> new CoolantItem(60_000));
	Supplier<Item> URANIUM_FUEL_ROD = REGISTRY.register("uranium_fuel_rod", () -> new FuelRodItem(20_000, 1, 5, 2));
	Supplier<Item> DUAL_URANIUM_FUEL_ROD = REGISTRY.register("dual_uranium_fuel_rod", () -> new FuelRodItem(20_000, 2, 10, 4));
	Supplier<Item> QUAD_URANIUM_FUEL_ROD = REGISTRY.register("quad_uranium_fuel_rod", () -> new FuelRodItem(20_000, 4, 20, 8));
	Supplier<Item> HEAT_VENT = REGISTRY.register("heat_vent", () -> new HeatVentItem(1_000, 6, 0, 0));
	Supplier<Item> ADVANCED_HEAT_VENT = REGISTRY.register("advanced_heat_vent", () -> new HeatVentItem(1_000, 12, 0, 0));
	Supplier<Item> REACTOR_HEAT_VENT = REGISTRY.register("reactor_heat_vent", () -> new HeatVentItem(1_000, 5, 5, 0));
	Supplier<Item> COMPONENT_HEAT_VENT = REGISTRY.register("component_heat_vent", () -> new HeatVentItem(0, 0, 0, 4));
	Supplier<Item> OVERCLOCKED_HEAT_VENT = REGISTRY.register("overclocked_heat_vent", () -> new HeatVentItem(1_000, 20, 36, 0));
	Supplier<Item> HEAT_EXCHANGER = REGISTRY.register("heat_exchanger", () -> new HeatExchangerItem(2_500, 12, 4));
	Supplier<Item> ADVANCED_HEAT_EXCHANGER = REGISTRY.register("advanced_heat_exchanger", () -> new HeatExchangerItem(10_000, 24, 8));
	Supplier<Item> REACTOR_HEAT_EXCHANGER = REGISTRY.register("reactor_heat_exchanger", () -> new HeatExchangerItem(5_000, 0, 72));
	Supplier<Item> COMPONENT_HEAT_EXCHANGER = REGISTRY.register("component_heat_exchanger", () -> new HeatExchangerItem(5_000, 36, 0));
	Supplier<Item> REACTOR_PLATING = REGISTRY.register("reactor_plating", () -> new ReactorPlatingItem(1_000, 0.95));
	Supplier<Item> CONTAINMENT_REACTOR_PLATING = REGISTRY.register("containment_reactor_plating", () -> new ReactorPlatingItem(500, 0.90));
	Supplier<Item> HEAT_CAPACITY_REACTOR_PLATING = REGISTRY.register("heat_capacity_reactor_plating", () -> new ReactorPlatingItem(1_700, 0.99));
	Supplier<Item> NEUTRON_REFLECTOR = REGISTRY.register("neutron_reflector", () -> new NeutronReflectorItem(30_000));
	Supplier<Item> THICK_NEUTRON_REFLECTOR = REGISTRY.register("thick_neutron_reflector", () -> new NeutronReflectorItem(120_000));
	Supplier<Item> IRIDIUM_NEUTRON_REFLECTOR = REGISTRY.register("iridium_neutron_reflector", () -> new NeutronReflectorItem(0));
	Supplier<Item> CANNED_FOOD = REGISTRY.register("canned_food", CannedFoodItem::new);
	Supplier<Item> PROTEIN_BAR = REGISTRY.register("protein_bar", ProteinBarItem::new);
	Supplier<Item> DARK_SPRAY_PAINT_CAN = REGISTRY.register("dark_spray_paint_can", () -> new SprayPaintCanItem(true));
	Supplier<Item> LIGHT_SPRAY_PAINT_CAN = REGISTRY.register("light_spray_paint_can", () -> new SprayPaintCanItem(false));
	Supplier<Item> OVERCLOCKER_UPGRADE = REGISTRY.register("overclocker_upgrade", () -> new UpgradeItem(16));
	Supplier<Item> ENERGY_STORAGE_UPGRADE = REGISTRY.register("energy_storage_upgrade", () -> new UpgradeItem(8));
	Supplier<Item> TRANSFORMER_UPGRADE = REGISTRY.register("transformer_upgrade", () -> new UpgradeItem(4));
	Supplier<Item> EJECTOR_UPGRADE = REGISTRY.register("ejector_upgrade", () -> new UpgradeItem(1));
	Supplier<Item> MECHANICAL_ELYTRA = REGISTRY.register("mechanical_elytra", MechanicalElytraItem::new);
	Supplier<Item> CARBON_HELMET = REGISTRY.register("carbon_helmet", () -> new DummyEnergyArmorItem(EnergyArmorMaterial.CARBON, EquipmentSlot.HEAD));
	Supplier<Item> CARBON_CHESTPLATE = REGISTRY.register("carbon_chestplate", () -> new EnergyArmorItem(EnergyArmorMaterial.CARBON));
	Supplier<Item> CARBON_LEGGINGS = REGISTRY.register("carbon_leggings", () -> new DummyEnergyArmorItem(EnergyArmorMaterial.CARBON, EquipmentSlot.LEGS));
	Supplier<Item> CARBON_BOOTS = REGISTRY.register("carbon_boots", () -> new DummyEnergyArmorItem(EnergyArmorMaterial.CARBON, EquipmentSlot.FEET));
	Supplier<Item> QUANTUM_HELMET = REGISTRY.register("quantum_helmet", () -> new DummyEnergyArmorItem(EnergyArmorMaterial.QUANTUM, EquipmentSlot.HEAD));
	Supplier<Item> QUANTUM_CHESTPLATE = REGISTRY.register("quantum_chestplate", () -> new EnergyArmorItem(EnergyArmorMaterial.QUANTUM));
	Supplier<Item> QUANTUM_LEGGINGS = REGISTRY.register("quantum_leggings", () -> new DummyEnergyArmorItem(EnergyArmorMaterial.QUANTUM, EquipmentSlot.LEGS));
	Supplier<Item> QUANTUM_BOOTS = REGISTRY.register("quantum_boots", () -> new DummyEnergyArmorItem(EnergyArmorMaterial.QUANTUM, EquipmentSlot.FEET));
	Supplier<Item> NUKE_ARROW = REGISTRY.register("nuke_arrow", NukeArrowItem::new);

	/**
	 * This goes over all the resource types, finds the resources we require for each element, then registers them for us! so helpful...
	 */
	Map<ResourceType, Map<ResourceElements, Supplier<Item>>> RESOURCE_TYPE_MAP = ResourceType.VALUES.stream().collect(Collectors.toMap(Function.identity(), e -> {
		var elementsForType = ResourceElements.RESOURCES_BY_REQUIREMENT.get(e);
		return elementsForType.stream().collect(Collectors.toMap(Function.identity(), a -> REGISTRY.register(a.getName() + "_" + e.name().toLowerCase(Locale.ENGLISH), () -> e == ResourceType.ORE || e == ResourceType.BLOCK ? new BlockItem((e == ResourceType.BLOCK ? FTBICBlocks.RESOURCE_BLOCKS_OF.get(a) : FTBICBlocks.RESOURCE_ORES.get(a)).get(), new Item.Properties().tab(FTBIC.TAB)) : new ResourceItem(e))));
	}));
}
