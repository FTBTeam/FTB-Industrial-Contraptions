package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.util.EnergyTier;
import dev.ftb.mods.ftbic.util.FTBICArmorMaterial;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

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

	MaterialItem INDUSTRIAL_GRADE_METAL = material("industrial_grade_metal");
	MaterialItem RUBBER = material("rubber");
	MaterialItem FUSE = material("fuse");
	MaterialItem COPPER_WIRE = material("copper_wire");
	MaterialItem GOLD_WIRE = material("gold_wire");
	MaterialItem ALUMINUM_WIRE = material("aluminum_wire");
	MaterialItem ENDERIUM_WIRE = material("enderium_wire");
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

	Supplier<Item> SINGLE_USE_BATTERY = REGISTRY.register("single_use_battery", () -> new BatteryItem(BatteryType.SINGLE_USE, EnergyTier.LV, FTBICConfig.SINGLE_USE_BATTERY_CAPACITY));
	Supplier<Item> BATTERY = REGISTRY.register("battery", () -> new BatteryItem(BatteryType.RECHARGEABLE, EnergyTier.LV, FTBICConfig.LV_BATTERY_CAPACITY));
	Supplier<Item> CRYSTAL_BATTERY = REGISTRY.register("crystal_battery", () -> new BatteryItem(BatteryType.RECHARGEABLE, EnergyTier.MV, FTBICConfig.MV_BATTERY_CAPACITY));
	Supplier<Item> GRAPHENE_BATTERY = REGISTRY.register("graphene_battery", () -> new BatteryItem(BatteryType.RECHARGEABLE, EnergyTier.HV, FTBICConfig.HV_BATTERY_CAPACITY));
	Supplier<Item> IRIDIUM_BATTERY = REGISTRY.register("iridium_battery", () -> new BatteryItem(BatteryType.RECHARGEABLE, EnergyTier.EV, FTBICConfig.EV_BATTERY_CAPACITY));
	Supplier<Item> CREATIVE_BATTERY = REGISTRY.register("creative_battery", () -> new BatteryItem(BatteryType.CREATIVE, EnergyTier.EV, Integer.MAX_VALUE));
	Supplier<Item> FLUID_CELL = REGISTRY.register("fluid_cell", FluidCellItem::new);
	Supplier<Item> COOLANT_10K = REGISTRY.register("coolant_10k", () -> new CoolantItem(10_000));
	Supplier<Item> COOLANT_30K = REGISTRY.register("coolant_30k", () -> new CoolantItem(30_000));
	Supplier<Item> COOLANT_60K = REGISTRY.register("coolant_60k", () -> new CoolantItem(60_000));
	Supplier<Item> URANIUM_FUEL_ROD = REGISTRY.register("uranium_fuel_rod", () -> new FuelRodItem(10_000));
	Supplier<Item> DUAL_URANIUM_FUEL_ROD = REGISTRY.register("dual_uranium_fuel_rod", () -> new FuelRodItem(20_000));
	Supplier<Item> QUAD_URANIUM_FUEL_ROD = REGISTRY.register("quad_uranium_fuel_rod", () -> new FuelRodItem(40_000));
	Supplier<Item> CANNED_FOOD = REGISTRY.register("canned_food", CannedFoodItem::new);
	Supplier<Item> PROTEIN_BAR = REGISTRY.register("protein_bar", ProteinBarItem::new);
	Supplier<Item> DARK_SPRAY_PAINT_CAN = REGISTRY.register("dark_spray_paint_can", () -> new SprayPaintCanItem(true));
	Supplier<Item> LIGHT_SPRAY_PAINT_CAN = REGISTRY.register("light_spray_paint_can", () -> new SprayPaintCanItem(false));
	Supplier<Item> OVERCLOCKER_UPGRADE = REGISTRY.register("overclocker_upgrade", () -> new UpgradeItem(16));
	Supplier<Item> ENERGY_STORAGE_UPGRADE = REGISTRY.register("energy_storage_upgrade", () -> new UpgradeItem(8));
	Supplier<Item> TRANSFORMER_UPGRADE = REGISTRY.register("transformer_upgrade", () -> new UpgradeItem(4));
	Supplier<Item> EJECTOR_UPGRADE = REGISTRY.register("ejector_upgrade", () -> new UpgradeItem(1));
	Supplier<Item> CARBON_HELMET = REGISTRY.register("carbon_helmet", () -> new EnergyArmorItem(FTBICArmorMaterial.CARBON, EquipmentSlot.HEAD));
	Supplier<Item> CARBON_CHESTPLATE = REGISTRY.register("carbon_chestplate", () -> new EnergyArmorItem(FTBICArmorMaterial.CARBON, EquipmentSlot.CHEST));
	Supplier<Item> CARBON_LEGGINGS = REGISTRY.register("carbon_leggings", () -> new EnergyArmorItem(FTBICArmorMaterial.CARBON, EquipmentSlot.LEGS));
	Supplier<Item> CARBON_BOOTS = REGISTRY.register("carbon_boots", () -> new EnergyArmorItem(FTBICArmorMaterial.CARBON, EquipmentSlot.FEET));
	Supplier<Item> QUANTUM_HELMET = REGISTRY.register("quantum_helmet", () -> new EnergyArmorItem(FTBICArmorMaterial.QUANTUM, EquipmentSlot.HEAD));
	Supplier<Item> QUANTUM_CHESTPLATE = REGISTRY.register("quantum_chestplate", () -> new EnergyArmorItem(FTBICArmorMaterial.QUANTUM, EquipmentSlot.CHEST));
	Supplier<Item> QUANTUM_LEGGINGS = REGISTRY.register("quantum_leggings", () -> new EnergyArmorItem(FTBICArmorMaterial.QUANTUM, EquipmentSlot.LEGS));
	Supplier<Item> QUANTUM_BOOTS = REGISTRY.register("quantum_boots", () -> new EnergyArmorItem(FTBICArmorMaterial.QUANTUM, EquipmentSlot.FEET));
}
