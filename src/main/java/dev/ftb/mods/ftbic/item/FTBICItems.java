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
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;

public interface FTBICItems {
	DeferredRegister.Items REGISTRY = DeferredRegister.createItems(FTBIC.MOD_ID);
	List<MaterialItem> MATERIALS = new ArrayList<>();

	static Item.Properties props(net.minecraft.resources.Identifier name) {
		return new Item.Properties().setId(ResourceKey.create(Registries.ITEM, name));
	}

	static double safeGet(net.neoforged.neoforge.common.ModConfigSpec.DoubleValue value, double fallback) {
		try {
			return value.get();
		} catch (IllegalStateException e) {
			return fallback;
		}
	}

	static DeferredItem<BlockItem> blockItem(String id, Supplier<? extends Block> sup) {
		return REGISTRY.register(id, name -> new BlockItem(sup.get(), props(name)));
	}

	static DeferredItem<BlockItem> electricBlockItem(String id, ElectricBlockInstance instance) {
		return REGISTRY.register(id, name -> new ElectricBlockItem(instance, props(name)));
	}

	static DeferredItem<Item> basicItem(String id) {
		return REGISTRY.register(id, name -> new Item(props(name)));
	}

	static MaterialItem material(String id) {
		MaterialItem m = new MaterialItem(id);
		m.item = basicItem(id);
		MATERIALS.add(m);
		return m;
	}

	DeferredItem<BlockItem> RUBBER_SHEET = blockItem("rubber_sheet", FTBICBlocks.RUBBER_SHEET);
	DeferredItem<BlockItem> REINFORCED_STONE = blockItem("reinforced_stone", FTBICBlocks.REINFORCED_STONE);
	DeferredItem<BlockItem> REINFORCED_GLASS = blockItem("reinforced_glass", FTBICBlocks.REINFORCED_GLASS);
	DeferredItem<BlockItem> MACHINE_BLOCK = blockItem("machine_block", FTBICBlocks.MACHINE_BLOCK);
	DeferredItem<BlockItem> ADVANCED_MACHINE_BLOCK = blockItem("advanced_machine_block", FTBICBlocks.ADVANCED_MACHINE_BLOCK);
	DeferredItem<BlockItem> IRON_FURNACE = blockItem("iron_furnace", FTBICBlocks.IRON_FURNACE);
	DeferredItem<BlockItem> LV_CABLE = blockItem("lv_cable", FTBICBlocks.LV_CABLE);
	DeferredItem<BlockItem> MV_CABLE = blockItem("mv_cable", FTBICBlocks.MV_CABLE);
	DeferredItem<BlockItem> HV_CABLE = blockItem("hv_cable", FTBICBlocks.HV_CABLE);
	DeferredItem<BlockItem> EV_CABLE = blockItem("ev_cable", FTBICBlocks.EV_CABLE);
	DeferredItem<BlockItem> IV_CABLE = blockItem("iv_cable", FTBICBlocks.IV_CABLE);
	DeferredItem<BlockItem> BURNT_CABLE = blockItem("burnt_cable", FTBICBlocks.BURNT_CABLE);
	DeferredItem<BlockItem> LV_REINFORCED_CABLE = blockItem("lv_reinforced_cable", FTBICBlocks.LV_REINFORCED_CABLE);
	DeferredItem<BlockItem> MV_REINFORCED_CABLE = blockItem("mv_reinforced_cable", FTBICBlocks.MV_REINFORCED_CABLE);
	DeferredItem<BlockItem> HV_REINFORCED_CABLE = blockItem("hv_reinforced_cable", FTBICBlocks.HV_REINFORCED_CABLE);
	DeferredItem<BlockItem> EV_REINFORCED_CABLE = blockItem("ev_reinforced_cable", FTBICBlocks.EV_REINFORCED_CABLE);
	DeferredItem<BlockItem> IV_REINFORCED_CABLE = blockItem("iv_reinforced_cable", FTBICBlocks.IV_REINFORCED_CABLE);
	DeferredItem<BlockItem> BURNT_REINFORCED_CABLE = blockItem("burnt_reinforced_cable", FTBICBlocks.BURNT_REINFORCED_CABLE);
	DeferredItem<BlockItem> LANDMARK = blockItem("landmark", FTBICBlocks.LANDMARK);
	DeferredItem<BlockItem> EXFLUID = blockItem("exfluid", FTBICBlocks.EXFLUID);
	DeferredItem<BlockItem> NUCLEAR_REACTOR_CHAMBER = blockItem("nuclear_reactor_chamber", FTBICBlocks.NUCLEAR_REACTOR_CHAMBER);
	DeferredItem<BlockItem> NUKE = blockItem("nuke", FTBICBlocks.NUKE);
	DeferredItem<BlockItem> ACTIVE_NUKE = blockItem("active_nuke", FTBICBlocks.ACTIVE_NUKE);

	// Materials
	MaterialItem INDUSTRIAL_GRADE_METAL = material("industrial_grade_metal");
	MaterialItem RUBBER = material("rubber");
	MaterialItem STICKY_RESIN = material("sticky_resin");
	MaterialItem LATEX_BALL = material("latex_ball");
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

	MaterialItem ENDERIUM_INGOT = material("enderium_ingot");
	MaterialItem ENDERIUM_NUGGET = material("enderium_nugget");
	MaterialItem ENDERIUM_DUST = material("enderium_dust");
	MaterialItem ENDERIUM_PLATE = material("enderium_plate");
	MaterialItem ENDERIUM_ROD = material("enderium_rod");
	MaterialItem ENDERIUM_GEAR = material("enderium_gear");
	MaterialItem ENDERIUM_WIRE = material("enderium_wire");
	DeferredItem<BlockItem> ENDERIUM_BLOCK = blockItem("enderium_block", FTBICBlocks.ENDERIUM_BLOCK);

	// Batteries — capacities resolved lazily so datagen can register without a loaded config.
	DeferredItem<Item> SINGLE_USE_BATTERY = REGISTRY.register("single_use_battery",
			name -> new BatteryItem(props(name), BatteryType.SINGLE_USE, EnergyTier.LV, safeGet(FTBICConfig.ENERGY.SINGLE_USE_BATTERY_CAPACITY, 8_000D)));
	DeferredItem<Item> LV_BATTERY = REGISTRY.register("lv_battery",
			name -> new BatteryItem(props(name), BatteryType.RECHARGEABLE, EnergyTier.LV, safeGet(FTBICConfig.ENERGY.LV_BATTERY_CAPACITY, 10_000D)));
	DeferredItem<Item> MV_BATTERY = REGISTRY.register("mv_battery",
			name -> new BatteryItem(props(name), BatteryType.RECHARGEABLE, EnergyTier.MV, safeGet(FTBICConfig.ENERGY.MV_BATTERY_CAPACITY, 100_000D)));
	DeferredItem<Item> HV_BATTERY = REGISTRY.register("hv_battery",
			name -> new BatteryItem(props(name), BatteryType.RECHARGEABLE, EnergyTier.HV, safeGet(FTBICConfig.ENERGY.HV_BATTERY_CAPACITY, 1_000_000D)));
	DeferredItem<Item> EV_BATTERY = REGISTRY.register("ev_battery",
			name -> new BatteryItem(props(name), BatteryType.RECHARGEABLE, EnergyTier.EV, safeGet(FTBICConfig.ENERGY.EV_BATTERY_CAPACITY, 10_000_000D)));
	DeferredItem<Item> CREATIVE_BATTERY = REGISTRY.register("creative_battery",
			name -> new BatteryItem(props(name), BatteryType.CREATIVE, EnergyTier.IV, Integer.MAX_VALUE));

	DeferredItem<Item> FLUID_CELL = REGISTRY.register("fluid_cell", name -> new FluidCellItem(props(name)));

	DeferredItem<Item> LOCATION_CARD = REGISTRY.register("location_card", name -> new LocationCardItem(props(name)));

	// Reactor components
	DeferredItem<Item> SMALL_COOLANT_CELL = REGISTRY.register("small_coolant_cell", name -> new CoolantItem(props(name), 10_000));
	DeferredItem<Item> MEDIUM_COOLANT_CELL = REGISTRY.register("medium_coolant_cell", name -> new CoolantItem(props(name), 30_000));
	DeferredItem<Item> LARGE_COOLANT_CELL = REGISTRY.register("large_coolant_cell", name -> new CoolantItem(props(name), 60_000));
	DeferredItem<Item> URANIUM_FUEL_ROD = REGISTRY.register("uranium_fuel_rod", name -> new FuelRodItem(props(name), 20_000, 1, 5, 2));
	DeferredItem<Item> DUAL_URANIUM_FUEL_ROD = REGISTRY.register("dual_uranium_fuel_rod", name -> new FuelRodItem(props(name), 20_000, 2, 10, 4));
	DeferredItem<Item> QUAD_URANIUM_FUEL_ROD = REGISTRY.register("quad_uranium_fuel_rod", name -> new FuelRodItem(props(name), 20_000, 4, 20, 8));
	DeferredItem<Item> HEAT_VENT = REGISTRY.register("heat_vent", name -> new HeatVentItem(props(name), 1_000, 6, 0, 0));
	DeferredItem<Item> ADVANCED_HEAT_VENT = REGISTRY.register("advanced_heat_vent", name -> new HeatVentItem(props(name), 1_000, 12, 0, 0));
	DeferredItem<Item> REACTOR_HEAT_VENT = REGISTRY.register("reactor_heat_vent", name -> new HeatVentItem(props(name), 1_000, 5, 5, 0));
	DeferredItem<Item> COMPONENT_HEAT_VENT = REGISTRY.register("component_heat_vent", name -> new HeatVentItem(props(name), 0, 0, 0, 4));
	DeferredItem<Item> OVERCLOCKED_HEAT_VENT = REGISTRY.register("overclocked_heat_vent", name -> new HeatVentItem(props(name), 1_000, 20, 36, 0));
	DeferredItem<Item> HEAT_EXCHANGER = REGISTRY.register("heat_exchanger", name -> new HeatExchangerItem(props(name), 2_500, 12, 4));
	DeferredItem<Item> ADVANCED_HEAT_EXCHANGER = REGISTRY.register("advanced_heat_exchanger", name -> new HeatExchangerItem(props(name), 10_000, 24, 8));
	DeferredItem<Item> REACTOR_HEAT_EXCHANGER = REGISTRY.register("reactor_heat_exchanger", name -> new HeatExchangerItem(props(name), 5_000, 0, 72));
	DeferredItem<Item> COMPONENT_HEAT_EXCHANGER = REGISTRY.register("component_heat_exchanger", name -> new HeatExchangerItem(props(name), 5_000, 36, 0));
	DeferredItem<Item> REACTOR_PLATING = REGISTRY.register("reactor_plating", name -> new ReactorPlatingItem(props(name), 1_000, 0.95));
	DeferredItem<Item> CONTAINMENT_REACTOR_PLATING = REGISTRY.register("containment_reactor_plating", name -> new ReactorPlatingItem(props(name), 500, 0.90));
	DeferredItem<Item> HEAT_CAPACITY_REACTOR_PLATING = REGISTRY.register("heat_capacity_reactor_plating", name -> new ReactorPlatingItem(props(name), 1_700, 0.99));
	DeferredItem<Item> NEUTRON_REFLECTOR = REGISTRY.register("neutron_reflector", name -> new NeutronReflectorItem(props(name), 30_000));
	DeferredItem<Item> THICK_NEUTRON_REFLECTOR = REGISTRY.register("thick_neutron_reflector", name -> new NeutronReflectorItem(props(name), 120_000));
	DeferredItem<Item> IRIDIUM_NEUTRON_REFLECTOR = REGISTRY.register("iridium_neutron_reflector", name -> new NeutronReflectorItem(props(name), 0));

	// Food
	DeferredItem<Item> CANNED_FOOD = REGISTRY.register("canned_food", name -> new CannedFoodItem(props(name)));
	DeferredItem<Item> PROTEIN_BAR = REGISTRY.register("protein_bar", name -> new ProteinBarItem(props(name)));

	// Spray paint
	DeferredItem<Item> DARK_SPRAY_PAINT_CAN = REGISTRY.register("dark_spray_paint_can", name -> new SprayPaintCanItem(props(name), true));
	DeferredItem<Item> LIGHT_SPRAY_PAINT_CAN = REGISTRY.register("light_spray_paint_can", name -> new SprayPaintCanItem(props(name), false));

	// Upgrades
	DeferredItem<Item> OVERCLOCKER_UPGRADE = REGISTRY.register("overclocker_upgrade", name -> new UpgradeItem(props(name), 16));
	DeferredItem<Item> ENERGY_STORAGE_UPGRADE = REGISTRY.register("energy_storage_upgrade", name -> new UpgradeItem(props(name), 8));
	DeferredItem<Item> TRANSFORMER_UPGRADE = REGISTRY.register("transformer_upgrade", name -> new UpgradeItem(props(name), 4));
	DeferredItem<Item> EJECTOR_UPGRADE = REGISTRY.register("ejector_upgrade", name -> new UpgradeItem(props(name), 1));

	// Armor + elytra — use Item.Properties.humanoidArmor() so the items equip into the right slot
	// without needing the legacy ArmorItem class. Material picks vanilla DIAMOND for Carbon and
	// NETHERITE for Quantum (durability + base defense values); per-tick energy absorption is
	// applied on top by EnergyArmorDamageHandler.
	DeferredItem<Item> MECHANICAL_ELYTRA = REGISTRY.register("mechanical_elytra", name ->
			new MechanicalElytraItem(props(name)
					.component(net.minecraft.core.component.DataComponents.GLIDER, net.minecraft.util.Unit.INSTANCE)
					.humanoidArmor(net.minecraft.world.item.equipment.ArmorMaterials.IRON,
							net.minecraft.world.item.equipment.ArmorType.CHESTPLATE)));
	DeferredItem<Item> CARBON_HELMET = REGISTRY.register("carbon_helmet", name ->
			new DummyEnergyArmorItem(props(name).humanoidArmor(net.minecraft.world.item.equipment.ArmorMaterials.DIAMOND,
					net.minecraft.world.item.equipment.ArmorType.HELMET), EnergyArmorMaterial.CARBON, EquipmentSlot.HEAD));
	DeferredItem<Item> CARBON_CHESTPLATE = REGISTRY.register("carbon_chestplate", name ->
			new EnergyArmorItem(props(name).humanoidArmor(net.minecraft.world.item.equipment.ArmorMaterials.DIAMOND,
					net.minecraft.world.item.equipment.ArmorType.CHESTPLATE), EnergyArmorMaterial.CARBON));
	DeferredItem<Item> CARBON_LEGGINGS = REGISTRY.register("carbon_leggings", name ->
			new DummyEnergyArmorItem(props(name).humanoidArmor(net.minecraft.world.item.equipment.ArmorMaterials.DIAMOND,
					net.minecraft.world.item.equipment.ArmorType.LEGGINGS), EnergyArmorMaterial.CARBON, EquipmentSlot.LEGS));
	DeferredItem<Item> CARBON_BOOTS = REGISTRY.register("carbon_boots", name ->
			new DummyEnergyArmorItem(props(name).humanoidArmor(net.minecraft.world.item.equipment.ArmorMaterials.DIAMOND,
					net.minecraft.world.item.equipment.ArmorType.BOOTS), EnergyArmorMaterial.CARBON, EquipmentSlot.FEET));
	DeferredItem<Item> QUANTUM_HELMET = REGISTRY.register("quantum_helmet", name ->
			new DummyEnergyArmorItem(props(name).humanoidArmor(net.minecraft.world.item.equipment.ArmorMaterials.NETHERITE,
					net.minecraft.world.item.equipment.ArmorType.HELMET), EnergyArmorMaterial.QUANTUM, EquipmentSlot.HEAD));
	DeferredItem<Item> QUANTUM_CHESTPLATE = REGISTRY.register("quantum_chestplate", name ->
			new EnergyArmorItem(props(name).humanoidArmor(net.minecraft.world.item.equipment.ArmorMaterials.NETHERITE,
					net.minecraft.world.item.equipment.ArmorType.CHESTPLATE), EnergyArmorMaterial.QUANTUM));
	DeferredItem<Item> QUANTUM_LEGGINGS = REGISTRY.register("quantum_leggings", name ->
			new DummyEnergyArmorItem(props(name).humanoidArmor(net.minecraft.world.item.equipment.ArmorMaterials.NETHERITE,
					net.minecraft.world.item.equipment.ArmorType.LEGGINGS), EnergyArmorMaterial.QUANTUM, EquipmentSlot.LEGS));
	DeferredItem<Item> QUANTUM_BOOTS = REGISTRY.register("quantum_boots", name ->
			new DummyEnergyArmorItem(props(name).humanoidArmor(net.minecraft.world.item.equipment.ArmorMaterials.NETHERITE,
					net.minecraft.world.item.equipment.ArmorType.BOOTS), EnergyArmorMaterial.QUANTUM, EquipmentSlot.FEET));
	DeferredItem<Item> NUKE_ARROW = REGISTRY.register("nuke_arrow", name -> new NukeArrowItem(props(name)));
}
