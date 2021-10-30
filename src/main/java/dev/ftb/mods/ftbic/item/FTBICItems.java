package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.util.PowerTier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluids;
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
	Supplier<BlockItem> COPPER_WIRE = blockItem("copper_wire", FTBICBlocks.COPPER_WIRE);
	Supplier<BlockItem> COPPER_CABLE = blockItem("copper_cable", FTBICBlocks.COPPER_CABLE);
	Supplier<BlockItem> GOLD_WIRE = blockItem("gold_wire", FTBICBlocks.GOLD_WIRE);
	Supplier<BlockItem> GOLD_CABLE = blockItem("gold_cable", FTBICBlocks.GOLD_CABLE);
	Supplier<BlockItem> ALUMINUM_WIRE = blockItem("aluminum_wire", FTBICBlocks.ALUMINUM_WIRE);
	Supplier<BlockItem> ALUMINUM_CABLE = blockItem("aluminum_cable", FTBICBlocks.ALUMINUM_CABLE);
	Supplier<BlockItem> GLASS_CABLE = blockItem("glass_cable", FTBICBlocks.GLASS_CABLE);

	MaterialItem INDUSTRIAL_GRADE_METAL = material("industrial_grade_metal");
	MaterialItem RUBBER = material("rubber");
	MaterialItem MIXED_METAL_INGOT = material("mixed_metal_ingot");
	MaterialItem ADVANCED_ALLOY = material("advanced_alloy");
	MaterialItem COAL_BALL = material("coal_ball");
	MaterialItem COMPRESSED_COAL_BALL = material("compressed_coal_ball");
	MaterialItem GRAPHENE = material("graphene");
	MaterialItem RAW_IRIDIUM = material("raw_iridium");
	MaterialItem IRIDIUM_PLATE = material("iridium_plate");
	MaterialItem SCRAP = material("scrap");
	MaterialItem SCRAP_BOX = material("scrap_box");
	MaterialItem OVERCLOCKER_UPGRADE = material("overclocker_upgrade");
	MaterialItem ENERGY_STORAGE_UPGRADE = material("energy_storage_upgrade");
	MaterialItem TRANSFORMER_UPGRADE = material("transformer_upgrade");
	MaterialItem EJECTOR_UPGRADE = material("ejector_upgrade");
	MaterialItem ELECTRONIC_CIRCUIT = material("electronic_circuit");
	MaterialItem ADVANCED_CIRCUIT = material("advanced_circuit");
	MaterialItem IRIDIUM_CIRCUIT = material("iridium_circuit");
	MaterialItem RAW_CARBON_FIBRE = material("raw_carbon_fibre");
	MaterialItem RAW_CARBON_MESH = material("raw_carbon_mesh");
	MaterialItem CARBON_PLATE = material("carbon_plate");
	MaterialItem ENERGY_CRYSTAL = material("energy_crystal");
	MaterialItem FUSE = material("fuse");
	MaterialItem EMPTY_CAN = material("empty_can");
	MaterialItem ANTIMATTER = material("antimatter");

	Supplier<Item> SINGLE_USE_BATTERY = REGISTRY.register("single_use_battery", () -> new BatteryItem(BatteryType.SINGLE_USE, PowerTier.LV, FTBICConfig.SINGLE_USE_BATTERY_CAPACITY));
	Supplier<Item> BATTERY = REGISTRY.register("battery", () -> new BatteryItem(BatteryType.RECHARGEABLE, PowerTier.LV, FTBICConfig.LV_BATTERY_CAPACITY));
	Supplier<Item> CRYSTAL_BATTERY = REGISTRY.register("crystal_battery", () -> new BatteryItem(BatteryType.RECHARGEABLE, PowerTier.MV, FTBICConfig.MV_BATTERY_CAPACITY));
	Supplier<Item> GRAPHENE_BATTERY = REGISTRY.register("graphene_battery", () -> new BatteryItem(BatteryType.RECHARGEABLE, PowerTier.HV, FTBICConfig.HV_BATTERY_CAPACITY));
	Supplier<Item> IRIDIUM_BATTERY = REGISTRY.register("iridium_battery", () -> new BatteryItem(BatteryType.RECHARGEABLE, PowerTier.EV, FTBICConfig.EV_BATTERY_CAPACITY));
	Supplier<Item> CREATIVE_BATTERY = REGISTRY.register("creative_battery", () -> new BatteryItem(BatteryType.CREATIVE, PowerTier.EV, Integer.MAX_VALUE));
	Supplier<Item> EMPTY_CELL = REGISTRY.register("empty_cell", () -> new CellItem(Fluids.EMPTY));
	Supplier<Item> WATER_CELL = REGISTRY.register("water_cell", () -> new CellItem(Fluids.WATER));
	Supplier<Item> LAVA_CELL = REGISTRY.register("lava_cell", () -> new CellItem(Fluids.LAVA));
	Supplier<Item> COOLANT_10K = REGISTRY.register("coolant_10k", () -> new CoolantItem(10_000));
	Supplier<Item> COOLANT_30K = REGISTRY.register("coolant_30k", () -> new CoolantItem(30_000));
	Supplier<Item> COOLANT_60K = REGISTRY.register("coolant_60k", () -> new CoolantItem(60_000));
	Supplier<Item> URANIUM_FUEL_ROD = REGISTRY.register("uranium_fuel_rod", () -> new FuelRodItem(10_000));
	Supplier<Item> DUAL_URANIUM_FUEL_ROD = REGISTRY.register("dual_uranium_fuel_rod", () -> new FuelRodItem(20_000));
	Supplier<Item> QUAD_URANIUM_FUEL_ROD = REGISTRY.register("quad_uranium_fuel_rod", () -> new FuelRodItem(40_000));
	Supplier<Item> CANNED_FOOD = REGISTRY.register("canned_food", CannedFoodItem::new);
	Supplier<Item> DARK_SPRAY_PAINT_CAN = REGISTRY.register("dark_spray_paint_can", () -> new SprayPaintCanItem(true));
	Supplier<Item> LIGHT_SPRAY_PAINT_CAN = REGISTRY.register("light_spray_paint_can", () -> new SprayPaintCanItem(false));
}
