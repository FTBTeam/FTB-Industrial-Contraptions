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

public interface FTBICItems {
	DeferredRegister.Items REGISTRY = DeferredRegister.createItems(FTBIC.MOD_ID);
	List<MaterialItem> MATERIALS = new ArrayList<>();

	static Item.Properties props(net.minecraft.resources.Identifier name) {
		return new Item.Properties().setId(ResourceKey.create(Registries.ITEM, name));
	}

	/**
	 * Resolves a config value or returns a fallback if configs aren't loaded yet (datagen context).
	 * Battery items take a final {@code double} capacity at registration; during {@code runData} the
	 * config files don't exist yet, so calling {@code .get()} eagerly would crash.
	 */
	static double safeGet(net.neoforged.neoforge.common.ModConfigSpec.DoubleValue value, double fallback) {
		try {
			return value.get();
		} catch (IllegalStateException e) {
			return fallback;
		}
	}

	static Supplier<BlockItem> blockItem(String id, Supplier<? extends Block> sup) {
		DeferredItem<BlockItem> registered = REGISTRY.register(id, name -> new BlockItem(sup.get(), props(name)));
		return () -> registered.get();
	}

	static Supplier<BlockItem> electricBlockItem(String id, dev.ftb.mods.ftbic.block.ElectricBlockInstance instance) {
		DeferredItem<BlockItem> registered = REGISTRY.register(id, name -> new ElectricBlockItem(instance, props(name)));
		return () -> registered.get();
	}

	static Supplier<Item> basicItem(String id) {
		DeferredItem<Item> reg = REGISTRY.register(id, name -> new Item(props(name)));
		return () -> reg.get();
	}

	static MaterialItem material(String id) {
		MaterialItem m = new MaterialItem(id);
		m.item = basicItem(id);
		MATERIALS.add(m);
		return m;
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

	// Materials
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

	MaterialItem ENDERIUM_INGOT = material("enderium_ingot");
	MaterialItem ENDERIUM_NUGGET = material("enderium_nugget");
	MaterialItem ENDERIUM_DUST = material("enderium_dust");
	MaterialItem ENDERIUM_PLATE = material("enderium_plate");
	MaterialItem ENDERIUM_ROD = material("enderium_rod");
	MaterialItem ENDERIUM_GEAR = material("enderium_gear");
	MaterialItem ENDERIUM_WIRE = material("enderium_wire");
	Supplier<BlockItem> ENDERIUM_BLOCK = blockItem("enderium_block", FTBICBlocks.ENDERIUM_BLOCK);

	// Batteries — capacities resolved lazily so datagen can register without a loaded config.
	Supplier<Item> SINGLE_USE_BATTERY = REGISTRY.register("single_use_battery",
			name -> new BatteryItem(props(name), BatteryType.SINGLE_USE, EnergyTier.LV, safeGet(FTBICConfig.ENERGY.SINGLE_USE_BATTERY_CAPACITY, 8_000D)));
	Supplier<Item> LV_BATTERY = REGISTRY.register("lv_battery",
			name -> new BatteryItem(props(name), BatteryType.RECHARGEABLE, EnergyTier.LV, safeGet(FTBICConfig.ENERGY.LV_BATTERY_CAPACITY, 10_000D)));
	Supplier<Item> MV_BATTERY = REGISTRY.register("mv_battery",
			name -> new BatteryItem(props(name), BatteryType.RECHARGEABLE, EnergyTier.MV, safeGet(FTBICConfig.ENERGY.MV_BATTERY_CAPACITY, 100_000D)));
	Supplier<Item> HV_BATTERY = REGISTRY.register("hv_battery",
			name -> new BatteryItem(props(name), BatteryType.RECHARGEABLE, EnergyTier.HV, safeGet(FTBICConfig.ENERGY.HV_BATTERY_CAPACITY, 1_000_000D)));
	Supplier<Item> EV_BATTERY = REGISTRY.register("ev_battery",
			name -> new BatteryItem(props(name), BatteryType.RECHARGEABLE, EnergyTier.EV, safeGet(FTBICConfig.ENERGY.EV_BATTERY_CAPACITY, 10_000_000D)));
	Supplier<Item> CREATIVE_BATTERY = REGISTRY.register("creative_battery",
			name -> new BatteryItem(props(name), BatteryType.CREATIVE, EnergyTier.IV, Integer.MAX_VALUE));

	Supplier<Item> FLUID_CELL = REGISTRY.register("fluid_cell", name -> new FluidCellItem(props(name)));

	// Reactor components
	Supplier<Item> SMALL_COOLANT_CELL = REGISTRY.register("small_coolant_cell", name -> new CoolantItem(props(name), 10_000));
	Supplier<Item> MEDIUM_COOLANT_CELL = REGISTRY.register("medium_coolant_cell", name -> new CoolantItem(props(name), 30_000));
	Supplier<Item> LARGE_COOLANT_CELL = REGISTRY.register("large_coolant_cell", name -> new CoolantItem(props(name), 60_000));
	Supplier<Item> URANIUM_FUEL_ROD = REGISTRY.register("uranium_fuel_rod", name -> new FuelRodItem(props(name), 20_000, 1, 5, 2));
	Supplier<Item> DUAL_URANIUM_FUEL_ROD = REGISTRY.register("dual_uranium_fuel_rod", name -> new FuelRodItem(props(name), 20_000, 2, 10, 4));
	Supplier<Item> QUAD_URANIUM_FUEL_ROD = REGISTRY.register("quad_uranium_fuel_rod", name -> new FuelRodItem(props(name), 20_000, 4, 20, 8));
	Supplier<Item> HEAT_VENT = REGISTRY.register("heat_vent", name -> new HeatVentItem(props(name), 1_000, 6, 0, 0));
	Supplier<Item> ADVANCED_HEAT_VENT = REGISTRY.register("advanced_heat_vent", name -> new HeatVentItem(props(name), 1_000, 12, 0, 0));
	Supplier<Item> REACTOR_HEAT_VENT = REGISTRY.register("reactor_heat_vent", name -> new HeatVentItem(props(name), 1_000, 5, 5, 0));
	Supplier<Item> COMPONENT_HEAT_VENT = REGISTRY.register("component_heat_vent", name -> new HeatVentItem(props(name), 0, 0, 0, 4));
	Supplier<Item> OVERCLOCKED_HEAT_VENT = REGISTRY.register("overclocked_heat_vent", name -> new HeatVentItem(props(name), 1_000, 20, 36, 0));
	Supplier<Item> HEAT_EXCHANGER = REGISTRY.register("heat_exchanger", name -> new HeatExchangerItem(props(name), 2_500, 12, 4));
	Supplier<Item> ADVANCED_HEAT_EXCHANGER = REGISTRY.register("advanced_heat_exchanger", name -> new HeatExchangerItem(props(name), 10_000, 24, 8));
	Supplier<Item> REACTOR_HEAT_EXCHANGER = REGISTRY.register("reactor_heat_exchanger", name -> new HeatExchangerItem(props(name), 5_000, 0, 72));
	Supplier<Item> COMPONENT_HEAT_EXCHANGER = REGISTRY.register("component_heat_exchanger", name -> new HeatExchangerItem(props(name), 5_000, 36, 0));
	Supplier<Item> REACTOR_PLATING = REGISTRY.register("reactor_plating", name -> new ReactorPlatingItem(props(name), 1_000, 0.95));
	Supplier<Item> CONTAINMENT_REACTOR_PLATING = REGISTRY.register("containment_reactor_plating", name -> new ReactorPlatingItem(props(name), 500, 0.90));
	Supplier<Item> HEAT_CAPACITY_REACTOR_PLATING = REGISTRY.register("heat_capacity_reactor_plating", name -> new ReactorPlatingItem(props(name), 1_700, 0.99));
	Supplier<Item> NEUTRON_REFLECTOR = REGISTRY.register("neutron_reflector", name -> new NeutronReflectorItem(props(name), 30_000));
	Supplier<Item> THICK_NEUTRON_REFLECTOR = REGISTRY.register("thick_neutron_reflector", name -> new NeutronReflectorItem(props(name), 120_000));
	Supplier<Item> IRIDIUM_NEUTRON_REFLECTOR = REGISTRY.register("iridium_neutron_reflector", name -> new NeutronReflectorItem(props(name), 0));

	// Food
	Supplier<Item> CANNED_FOOD = REGISTRY.register("canned_food", name -> new CannedFoodItem(props(name)));
	Supplier<Item> PROTEIN_BAR = REGISTRY.register("protein_bar", name -> new ProteinBarItem(props(name)));

	// Spray paint
	Supplier<Item> DARK_SPRAY_PAINT_CAN = REGISTRY.register("dark_spray_paint_can", name -> new SprayPaintCanItem(props(name), true));
	Supplier<Item> LIGHT_SPRAY_PAINT_CAN = REGISTRY.register("light_spray_paint_can", name -> new SprayPaintCanItem(props(name), false));

	// Upgrades
	Supplier<Item> OVERCLOCKER_UPGRADE = REGISTRY.register("overclocker_upgrade", name -> new UpgradeItem(props(name), 16));
	Supplier<Item> ENERGY_STORAGE_UPGRADE = REGISTRY.register("energy_storage_upgrade", name -> new UpgradeItem(props(name), 8));
	Supplier<Item> TRANSFORMER_UPGRADE = REGISTRY.register("transformer_upgrade", name -> new UpgradeItem(props(name), 4));
	Supplier<Item> EJECTOR_UPGRADE = REGISTRY.register("ejector_upgrade", name -> new UpgradeItem(props(name), 1));

	// Armor + elytra — use Item.Properties.humanoidArmor() so the items equip into the right slot
	// without needing the legacy ArmorItem class. Material picks vanilla DIAMOND for Carbon and
	// NETHERITE for Quantum (durability + base defense values); per-tick energy absorption is
	// applied on top by EnergyArmorDamageHandler.
	Supplier<Item> MECHANICAL_ELYTRA = REGISTRY.register("mechanical_elytra", name ->
			new MechanicalElytraItem(props(name)
					.component(net.minecraft.core.component.DataComponents.GLIDER, net.minecraft.util.Unit.INSTANCE)
					.humanoidArmor(net.minecraft.world.item.equipment.ArmorMaterials.IRON,
							net.minecraft.world.item.equipment.ArmorType.CHESTPLATE)));
	Supplier<Item> CARBON_HELMET = REGISTRY.register("carbon_helmet", name ->
			new DummyEnergyArmorItem(props(name).humanoidArmor(net.minecraft.world.item.equipment.ArmorMaterials.DIAMOND,
					net.minecraft.world.item.equipment.ArmorType.HELMET), EnergyArmorMaterial.CARBON, EquipmentSlot.HEAD));
	Supplier<Item> CARBON_CHESTPLATE = REGISTRY.register("carbon_chestplate", name ->
			new EnergyArmorItem(props(name).humanoidArmor(net.minecraft.world.item.equipment.ArmorMaterials.DIAMOND,
					net.minecraft.world.item.equipment.ArmorType.CHESTPLATE), EnergyArmorMaterial.CARBON));
	Supplier<Item> CARBON_LEGGINGS = REGISTRY.register("carbon_leggings", name ->
			new DummyEnergyArmorItem(props(name).humanoidArmor(net.minecraft.world.item.equipment.ArmorMaterials.DIAMOND,
					net.minecraft.world.item.equipment.ArmorType.LEGGINGS), EnergyArmorMaterial.CARBON, EquipmentSlot.LEGS));
	Supplier<Item> CARBON_BOOTS = REGISTRY.register("carbon_boots", name ->
			new DummyEnergyArmorItem(props(name).humanoidArmor(net.minecraft.world.item.equipment.ArmorMaterials.DIAMOND,
					net.minecraft.world.item.equipment.ArmorType.BOOTS), EnergyArmorMaterial.CARBON, EquipmentSlot.FEET));
	Supplier<Item> QUANTUM_HELMET = REGISTRY.register("quantum_helmet", name ->
			new DummyEnergyArmorItem(props(name).humanoidArmor(net.minecraft.world.item.equipment.ArmorMaterials.NETHERITE,
					net.minecraft.world.item.equipment.ArmorType.HELMET), EnergyArmorMaterial.QUANTUM, EquipmentSlot.HEAD));
	Supplier<Item> QUANTUM_CHESTPLATE = REGISTRY.register("quantum_chestplate", name ->
			new EnergyArmorItem(props(name).humanoidArmor(net.minecraft.world.item.equipment.ArmorMaterials.NETHERITE,
					net.minecraft.world.item.equipment.ArmorType.CHESTPLATE), EnergyArmorMaterial.QUANTUM));
	Supplier<Item> QUANTUM_LEGGINGS = REGISTRY.register("quantum_leggings", name ->
			new DummyEnergyArmorItem(props(name).humanoidArmor(net.minecraft.world.item.equipment.ArmorMaterials.NETHERITE,
					net.minecraft.world.item.equipment.ArmorType.LEGGINGS), EnergyArmorMaterial.QUANTUM, EquipmentSlot.LEGS));
	Supplier<Item> QUANTUM_BOOTS = REGISTRY.register("quantum_boots", name ->
			new DummyEnergyArmorItem(props(name).humanoidArmor(net.minecraft.world.item.equipment.ArmorMaterials.NETHERITE,
					net.minecraft.world.item.equipment.ArmorType.BOOTS), EnergyArmorMaterial.QUANTUM, EquipmentSlot.FEET));
	Supplier<Item> NUKE_ARROW = REGISTRY.register("nuke_arrow", name -> new NukeArrowItem(props(name)));
}
