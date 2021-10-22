package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.util.PowerTier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public interface FTBICItems {
	DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, FTBIC.MOD_ID);

	static Supplier<BlockItem> blockItem(String id, Supplier<Block> sup) {
		return REGISTRY.register(id, () -> new BlockItem(sup.get(), new Item.Properties().tab(FTBIC.TAB)));
	}

	static Supplier<Item> basicItem(String id) {
		return REGISTRY.register(id, () -> new Item(new Item.Properties().tab(FTBIC.TAB)));
	}

	Supplier<BlockItem> RUBBER_SHEET = blockItem("rubber_sheet", FTBICBlocks.RUBBER_SHEET);
	Supplier<BlockItem> REINFORCED_STONE = blockItem("reinforced_stone", FTBICBlocks.REINFORCED_STONE);
	Supplier<BlockItem> REINFORCED_GLASS = blockItem("reinforced_glass", FTBICBlocks.REINFORCED_GLASS);
	Supplier<BlockItem> MACHINE_BLOCK = blockItem("machine_block", FTBICBlocks.MACHINE_BLOCK);
	Supplier<BlockItem> ADVANCED_MACHINE_BLOCK = blockItem("advanced_machine_block", FTBICBlocks.ADVANCED_MACHINE_BLOCK);

	Supplier<Item> RUBBER = basicItem("rubber");
	Supplier<Item> RESIN = basicItem("resin");
	Supplier<Item> MIXED_METAL_INGOT = basicItem("mixed_metal_ingot");
	Supplier<Item> ADVANCED_ALLOY = basicItem("advanced_alloy");
	Supplier<Item> COAL_BALL = basicItem("coal_ball");
	Supplier<Item> COMPRESSED_COAL_BALL = basicItem("compressed_coal_ball");
	Supplier<Item> COAL_CHUNK = basicItem("coal_chunk");
	Supplier<Item> RAW_IRIDIUM = basicItem("raw_iridium");
	Supplier<Item> IRIDIUM_PLATE = basicItem("iridium_plate");
	Supplier<Item> SCRAP = basicItem("scrap");
	Supplier<Item> SCRAP_BOX = basicItem("scrap_box");
	Supplier<Item> SINGLE_USE_BATTERY = basicItem("single_use_battery");
	Supplier<Item> BATTERY = REGISTRY.register("battery", () -> new BatteryItem(PowerTier.LV, FTBICConfig.LV_BATTERY_CAPACITY));
	Supplier<Item> CRYSTAL_BATTERY = REGISTRY.register("crystal_battery", () -> new BatteryItem(PowerTier.MV, FTBICConfig.MV_BATTERY_CAPACITY));
	Supplier<Item> GRAPHENE_BATTERY = REGISTRY.register("graphene_battery", () -> new BatteryItem(PowerTier.HV, FTBICConfig.HV_BATTERY_CAPACITY));
	Supplier<Item> IRIDIUM_BATTERY = REGISTRY.register("iridium_battery", () -> new BatteryItem(PowerTier.EV, FTBICConfig.EV_BATTERY_CAPACITY));
	Supplier<Item> OVERCLOCKER_UPGRADE = basicItem("overclocker_upgrade");
	Supplier<Item> ENERGY_STORAGE_UPGRADE = basicItem("energy_storage_upgrade");
	Supplier<Item> TRANSFORMER_UPGRADE = basicItem("transformer_upgrade");
	Supplier<Item> CLAY_DUST = basicItem("clay_dust");
	Supplier<Item> ELECTRONIC_CIRCUIT = basicItem("electronic_circuit");
	Supplier<Item> ADVANCED_CIRCUIT = basicItem("advanced_circuit");
	Supplier<Item> RAW_CARBON_FIBRE = basicItem("raw_carbon_fibre");
	Supplier<Item> RAW_CARBON_MESH = basicItem("raw_carbon_mesh");
	Supplier<Item> CARBON_PLATE = basicItem("carbon_plate");
}
