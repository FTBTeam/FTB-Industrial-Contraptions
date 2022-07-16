package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.item.FluidCellItem;
import dev.ftb.mods.ftbic.world.ResourceElements;
import dev.ftb.mods.ftbic.world.ResourceType;
import io.alwa.mods.myrtrees.common.item.MyrtreesItems;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class FTBICRecipesGen extends RecipeProvider implements IConditionBuilder {
	public static final String MODID = FTBIC.MOD_ID;
	public static final TagKey<Item> REDSTONE = Tags.Items.DUSTS_REDSTONE;
	public static final TagKey<Item> GLOWSTONE = Tags.Items.DUSTS_GLOWSTONE;
	public static final TagKey<Item> GLASS = Tags.Items.GLASS_COLORLESS;
	public static final TagKey<Item> COAL = ItemTags.COALS;
	public static final TagKey<Item> COAL_BLOCK = Tags.Items.STORAGE_BLOCKS_COAL;
	public static final TagKey<Item> DIAMOND = Tags.Items.GEMS_DIAMOND;
	public static final TagKey<Item> QUARTZ = Tags.Items.GEMS_QUARTZ;
	public static final TagKey<Item> SAND = ItemTags.SAND;
	public static final TagKey<Item> IRON_INGOT = Tags.Items.INGOTS_IRON;
	public static final TagKey<Item> GOLD_INGOT = Tags.Items.INGOTS_GOLD;
	public static final TagKey<Item> COPPER_INGOT = Tags.Items.INGOTS_COPPER;
	public static final TagKey<Item> IRON_ORE = Tags.Items.ORES_IRON;
	public static final TagKey<Item> GOLD_ORE = Tags.Items.ORES_GOLD;
	public static final TagKey<Item> COPPER_ORE = Tags.Items.ORES_COPPER;
	public static final TagKey<Item> IRON_RAW = Tags.Items.RAW_MATERIALS_IRON;
	public static final TagKey<Item> GOLD_RAW = Tags.Items.RAW_MATERIALS_GOLD;
	public static final TagKey<Item> COPPER_RAW = Tags.Items.RAW_MATERIALS_COPPER;
	public static final TagKey<Item> PLATES = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "plates"));
	public static final TagKey<Item> COPPER_PLATE = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "plates/copper"));
	public static final TagKey<Item> LEAD_PLATE = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "plates/lead"));
	public static final TagKey<Item> URANIUM_PLATE = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "plates/uranium"));
	public static final TagKey<Item> IRIDIUM_PLATE = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "plates/iridium"));
	public static final TagKey<Item> TIN_PLATE = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "plates/tin"));
	public static final TagKey<Item> IRON_PLATE = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "plates/iron"));
	public static final TagKey<Item> GOLD_PLATE = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "plates/gold"));
	public static final TagKey<Item> ALUMINUM_PLATE = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "plates/aluminum"));
	public static final TagKey<Item> ENDERIUM_PLATE = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "plates/enderium"));
	public static final TagKey<Item> BRONZE_PLATE = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "plates/bronze"));
	public static final TagKey<Item> TIN_INGOT = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "ingots/tin"));
	public static final TagKey<Item> LEAD_INGOT = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "ingots/lead"));
	public static final TagKey<Item> URANIUM_INGOT = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "ingots/uranium"));
	public static final TagKey<Item> ALUMINUM_INGOT = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "ingots/aluminum"));
	public static final TagKey<Item> ENDERIUM_INGOT = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "ingots/enderium"));
	public static final TagKey<Item> BRONZE_INGOT = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "ingots/bronze"));
	public static final TagKey<Item> TIN_NUGGET = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "nuggets/tin"));
	public static final TagKey<Item> LEAD_NUGGET = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "nuggets/lead"));
	public static final TagKey<Item> URANIUM_NUGGET = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "nuggets/uranium"));
	public static final TagKey<Item> ALUMINUM_NUGGET = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "nuggets/aluminum"));
	public static final TagKey<Item> ENDERIUM_NUGGET = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "nuggets/enderium"));
	public static final TagKey<Item> IRIDIUM_NUGGET = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "nuggets/iridium"));
	public static final TagKey<Item> COPPER_NUGGET = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "nuggets/copper"));
	public static final TagKey<Item> BRONZE_NUGGET = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "nuggets/bronze"));
	public static final TagKey<Item> RODS = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "rods"));
	public static final TagKey<Item> TIN_ROD = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "rods/tin"));
	public static final TagKey<Item> LEAD_ROD = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "rods/lead"));
	public static final TagKey<Item> URANIUM_ROD = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "rods/uranium"));
	public static final TagKey<Item> ALUMINUM_ROD = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "rods/aluminum"));
	public static final TagKey<Item> ENDERIUM_ROD = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "rods/enderium"));
	public static final TagKey<Item> IRIDIUM_ROD = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "rods/iridium"));
	public static final TagKey<Item> GOLD_ROD = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "rods/gold"));
	public static final TagKey<Item> COPPER_ROD = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "rods/copper"));
	public static final TagKey<Item> BRONZE_ROD = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "rods/bronze"));
	public static final TagKey<Item> TIN_ORE = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "ores/tin"));
	public static final TagKey<Item> LEAD_ORE = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "ores/lead"));
	public static final TagKey<Item> URANIUM_ORE = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "ores/uranium"));
	public static final TagKey<Item> ALUMINUM_ORE = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "ores/aluminum"));
	public static final TagKey<Item> IRIDIUM_ORE = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "ores/iridium"));
	public static final TagKey<Item> GEARS = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "gears"));
	public static final TagKey<Item> TIN_GEAR = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "gears/tin"));
	public static final TagKey<Item> LEAD_GEAR = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "gears/lead"));
	public static final TagKey<Item> URANIUM_GEAR = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "gears/uranium"));
	public static final TagKey<Item> ALUMINUM_GEAR = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "gears/aluminum"));
	public static final TagKey<Item> ENDERIUM_GEAR = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "gears/enderium"));
	public static final TagKey<Item> IRIDIUM_GEAR = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "gears/iridium"));
	public static final TagKey<Item> IRON_GEAR = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "gears/iron"));
	public static final TagKey<Item> GOLD_GEAR = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "gears/gold"));
	public static final TagKey<Item> COPPER_GEAR = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "gears/copper"));
	public static final TagKey<Item> BRONZE_GEAR = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "gears/bronze"));
	public static final TagKey<Item> TIN_CHUNK = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "raw_materials/tin"));
	public static final TagKey<Item> LEAD_CHUNK = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "raw_materials/lead"));
	public static final TagKey<Item> URANIUM_CHUNK = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "raw_materials/uranium"));
	public static final TagKey<Item> ALUMINUM_CHUNK = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "raw_materials/aluminum"));
	public static final TagKey<Item> IRIDIUM_CHUNK = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "raw_materials/iridium"));
	public static final TagKey<Item> COAL_DUST = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "dusts/coal"));
	public static final TagKey<Item> CHARCOAL_DUST = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "dusts/charcoal"));
	public static final TagKey<Item> OBSIDIAN_DUST = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "dusts/obsidian"));
	public static final TagKey<Item> ENDER_DUST = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "dusts/ender"));
	public static final TagKey<Item> PLANKS = ItemTags.PLANKS;
	public static final TagKey<Item> LOGS_THAT_BURN = ItemTags.LOGS_THAT_BURN;
	public static final TagKey<Item> STICK = Tags.Items.RODS_WOODEN;
	public static final TagKey<Item> SAPLING = ItemTags.SAPLINGS;
	public static final Item SUGAR_CANE = Items.SUGAR_CANE;
	public static final Item CACTUS = Items.CACTUS;
	public static final TagKey<Item> COBBLESTONE = Tags.Items.COBBLESTONE;
	public static final Item SMOOTH_STONE = Items.SMOOTH_STONE;
	public static final TagKey<Item> OBSIDIAN = Tags.Items.OBSIDIAN;
	public static final TagKey<Item> ENDER_PEARL = Tags.Items.ENDER_PEARLS;
	public static final TagKey<Item> COAL_ORE = Tags.Items.ORES_COAL;
	public static final Item FLINT = Items.FLINT;
	public static final TagKey<Item> STONE = Tags.Items.STONE;
	public static final TagKey<Item> GRAVEL = Tags.Items.GRAVEL;
	public static final TagKey<Item> WOOL = ItemTags.WOOL;
	public static final TagKey<Item> SLIMEBALL = Tags.Items.SLIMEBALLS;
	public static final TagKey<Item> SILICON = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "silicon"));
	public static final TagKey<Item> URANIUM_DUST = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "dusts/uranium"));
	public static final TagKey<Item> IRIDIUM_INGOT = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "ingots/iridium"));
	public static final TagKey<Item> BLAZE_ROD = Tags.Items.RODS_BLAZE;
	public static final TagKey<Item> LAPIS = Tags.Items.GEMS_LAPIS;
	public static final Item PISTON = Items.PISTON;
	public static final Supplier<Item> LATEX = MyrtreesItems.LATEX;
	public static final Supplier<Item> TREE_TAP = MyrtreesItems.TREE_TAP;
	public static final Supplier<Item> RUBBERWOOD_LOG = MyrtreesItems.RUBBERWOOD_LOG;
	public static final Supplier<Item> RUBBERWOOD_SAPLING = MyrtreesItems.RUBBERWOOD_SAPLING;
	public static final Supplier<Item> RUBBERWOOD_LEAVES = MyrtreesItems.RUBBERWOOD_LEAVES;
	public static final TagKey<Item> IRON_ROD = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "rods/iron"));
	public static final TagKey<Item> IRON_DUST = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "dusts/iron"));
	public static final TagKey<Item> LEAD_DUST = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "dusts/lead"));
	public static final TagKey<Item> IRIDIUM_DUST = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "dusts/iridium"));
	public static final TagKey<Item> ENDERIUM_DUST = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "dusts/enderium"));
	public static final TagKey<Item> BRONZE_DUST = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "dusts/bronze"));
	public static final TagKey<Item> ELECTRUM_DUST = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "dusts/electrum"));
	public static final TagKey<Item> CONSTANTAN_DUST = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "dusts/constantan"));
	public static final TagKey<Item> TIN_DUST = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "dusts/tin"));
	public static final TagKey<Item> ALUMINUM_DUST = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "dusts/aluminum"));
	public static final TagKey<Item> DIAMOND_DUST = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "dusts/diamond"));
	public static final TagKey<Item> GOLD_DUST = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "dusts/gold"));
	public static final TagKey<Item> COPPER_DUST = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "dusts/copper"));
	public static final TagKey<Item> TIN_BLOCK = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "storage_blocks/tin"));
	public static final TagKey<Item> LEAD_BLOCK = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "storage_blocks/lead"));
	public static final TagKey<Item> URANIUM_BLOCK = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "storage_blocks/uranium"));
	public static final TagKey<Item> ALUMINUM_BLOCK = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "storage_blocks/aluminum"));
	public static final TagKey<Item> IRIDIUM_BLOCK = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "storage_blocks/iridium"));
	public static final TagKey<Item> ENDERIUM_BLOCK = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "storage_blocks/enderium"));
	public static final TagKey<Item> BRONZE_BLOCK = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "storage_blocks/bronze"));

	public static final Supplier<Item> SILICON_ITEM = FTBICItems.SILICON.item;
	public static final Supplier<Item> OBSIDIAN_DUST_ITEM = FTBICItems.getResourceFromType(ResourceElements.OBSIDIAN, ResourceType.DUST).orElseThrow();
	public static final Supplier<Item> ENDER_DUST_ITEM = FTBICItems.getResourceFromType(ResourceElements.ENDER, ResourceType.DUST).orElseThrow();
//	public static final Item SAWDUST_ITEM = Items.PAPER; // ItemHandler.DUST_WOOD;
	public static final Supplier<Item> COAL_DUST_ITEM = FTBICItems.getResourceFromType(ResourceElements.COAL, ResourceType.DUST).orElseThrow();
	public static final Supplier<Item> CHARCOAL_DUST_ITEM = FTBICItems.getResourceFromType(ResourceElements.CHARCOAL, ResourceType.DUST).orElseThrow();
//	public static final Item URANIUM_DUST_ITEM = Items.PAPER; // ItemHandler.backingItemTable.get(ProcessedMaterials.DUST, Materials.URANIUM);
//	public static final Item SULFUR_ITEM = Items.PAPER; // ItemHandler.backingItemTable.get(ProcessedMaterials.GEM, Materials.SULFUR);

	public static final Supplier<BlockItem> RUBBER_SHEET = FTBICItems.RUBBER_SHEET;
	public static final Supplier<BlockItem> REINFORCED_STONE = FTBICItems.REINFORCED_STONE;
	public static final Supplier<BlockItem> REINFORCED_GLASS = FTBICItems.REINFORCED_GLASS;
	public static final Supplier<BlockItem> MACHINE_BLOCK = FTBICItems.MACHINE_BLOCK;
	public static final Supplier<BlockItem> ADVANCED_MACHINE_BLOCK = FTBICItems.ADVANCED_MACHINE_BLOCK;
	public static final Supplier<BlockItem> IRON_FURNACE = FTBICItems.IRON_FURNACE;
	public static final Supplier<BlockItem> LV_CABLE = FTBICItems.LV_CABLE;
	public static final Supplier<BlockItem> MV_CABLE = FTBICItems.MV_CABLE;
	public static final Supplier<BlockItem> HV_CABLE = FTBICItems.HV_CABLE;
	public static final Supplier<BlockItem> EV_CABLE = FTBICItems.EV_CABLE;
	public static final Supplier<BlockItem> IV_CABLE = FTBICItems.IV_CABLE;
	public static final Supplier<BlockItem> BURNT_CABLE = FTBICItems.BURNT_CABLE;
	public static final Supplier<BlockItem> LANDMARK = FTBICItems.LANDMARK;
	public static final Supplier<BlockItem> NUCLEAR_REACTOR_CHAMBER = FTBICItems.NUCLEAR_REACTOR_CHAMBER;
	public static final Supplier<BlockItem> NUKE = FTBICItems.NUKE;

	public static final Supplier<Item> INDUSTRIAL_GRADE_METAL = FTBICItems.INDUSTRIAL_GRADE_METAL.item;
	public static final Supplier<Item> RUBBER = FTBICItems.RUBBER.item;
	public static final Supplier<Item> FUSE = FTBICItems.FUSE.item;
	public static final Supplier<Item> COPPER_WIRE = FTBICItems.getResourceFromType(ResourceElements.COPPER, ResourceType.WIRE).orElseThrow();
	public static final Supplier<Item> GOLD_WIRE = FTBICItems.getResourceFromType(ResourceElements.GOLD, ResourceType.WIRE).orElseThrow();
	public static final Supplier<Item> ALUMINUM_WIRE = FTBICItems.getResourceFromType(ResourceElements.ALUMINUM, ResourceType.WIRE).orElseThrow();
	public static final Supplier<Item> ENDERIUM_WIRE = FTBICItems.getResourceFromType(ResourceElements.ENDERIUM, ResourceType.WIRE).orElseThrow();
	public static final Supplier<Item> COPPER_COIL = FTBICItems.COPPER_COIL.item;
	public static final Supplier<Item> MIXED_METAL_BLEND = FTBICItems.MIXED_METAL_BLEND.item;
	public static final Supplier<Item> ADVANCED_ALLOY = FTBICItems.ADVANCED_ALLOY.item;
	public static final Supplier<Item> IRIDIUM_ALLOY = FTBICItems.IRIDIUM_ALLOY.item;
	public static final Supplier<Item> COAL_BALL = FTBICItems.COAL_BALL.item;
	public static final Supplier<Item> COMPRESSED_COAL_BALL = FTBICItems.COMPRESSED_COAL_BALL.item;
	public static final Supplier<Item> GRAPHENE = FTBICItems.GRAPHENE.item;
	public static final Supplier<Item> CARBON_FIBERS = FTBICItems.CARBON_FIBERS.item;
	public static final Supplier<Item> CARBON_FIBER_MESH = FTBICItems.CARBON_FIBER_MESH.item;
	public static final Supplier<Item> CARBON_PLATE = FTBICItems.CARBON_PLATE.item;
	public static final Supplier<Item> SCRAP = FTBICItems.SCRAP.item;
	public static final Supplier<Item> SCRAP_BOX = FTBICItems.SCRAP_BOX.item;
	public static final Supplier<Item> ELECTRONIC_CIRCUIT = FTBICItems.ELECTRONIC_CIRCUIT.item;
	public static final Supplier<Item> ADVANCED_CIRCUIT = FTBICItems.ADVANCED_CIRCUIT.item;
	public static final Supplier<Item> IRIDIUM_CIRCUIT = FTBICItems.IRIDIUM_CIRCUIT.item;
	public static final Supplier<Item> EMPTY_CAN = FTBICItems.EMPTY_CAN.item;
	public static final Supplier<Item> ANTIMATTER = FTBICItems.ANTIMATTER.item;
	public static final Supplier<Item> ANTIMATTER_CRYSTAL = FTBICItems.ANTIMATTER_CRYSTAL.item;
	public static final Supplier<Item> ENERGY_CRYSTAL = FTBICItems.ENERGY_CRYSTAL.item;
	public static final Supplier<Item> DENSE_COPPER_PLATE = FTBICItems.DENSE_COPPER_PLATE.item;

	public static final Supplier<Item> SINGLE_USE_BATTERY = FTBICItems.SINGLE_USE_BATTERY;
	public static final Supplier<Item> LV_BATTERY = FTBICItems.LV_BATTERY;
	public static final Supplier<Item> MV_BATTERY = FTBICItems.MV_BATTERY;
	public static final Supplier<Item> HV_BATTERY = FTBICItems.HV_BATTERY;
	public static final Supplier<Item> EV_BATTERY = FTBICItems.EV_BATTERY;
	public static final Supplier<Item> FLUID_CELL = FTBICItems.FLUID_CELL;
	public static final Supplier<Item> SMALL_COOLANT_CELL = FTBICItems.SMALL_COOLANT_CELL;
	public static final Supplier<Item> MEDIUM_COOLANT_CELL = FTBICItems.MEDIUM_COOLANT_CELL;
	public static final Supplier<Item> LARGE_COOLANT_CELL = FTBICItems.LARGE_COOLANT_CELL;
	public static final Supplier<Item> URANIUM_FUEL_ROD = FTBICItems.URANIUM_FUEL_ROD;
	public static final Supplier<Item> DUAL_URANIUM_FUEL_ROD = FTBICItems.DUAL_URANIUM_FUEL_ROD;
	public static final Supplier<Item> QUAD_URANIUM_FUEL_ROD = FTBICItems.QUAD_URANIUM_FUEL_ROD;
	public static final Supplier<Item> HEAT_VENT = FTBICItems.HEAT_VENT;
	public static final Supplier<Item> ADVANCED_HEAT_VENT = FTBICItems.ADVANCED_HEAT_VENT;
	public static final Supplier<Item> OVERCLOCKED_HEAT_VENT = FTBICItems.OVERCLOCKED_HEAT_VENT;
	public static final Supplier<Item> REACTOR_HEAT_VENT = FTBICItems.REACTOR_HEAT_VENT;
	public static final Supplier<Item> COMPONENT_HEAT_VENT = FTBICItems.COMPONENT_HEAT_VENT;
	public static final Supplier<Item> HEAT_EXCHANGER = FTBICItems.HEAT_EXCHANGER;
	public static final Supplier<Item> ADVANCED_HEAT_EXCHANGER = FTBICItems.ADVANCED_HEAT_EXCHANGER;
	public static final Supplier<Item> REACTOR_HEAT_EXCHANGER = FTBICItems.REACTOR_HEAT_EXCHANGER;
	public static final Supplier<Item> COMPONENT_HEAT_EXCHANGER = FTBICItems.COMPONENT_HEAT_EXCHANGER;
	public static final Supplier<Item> REACTOR_PLATING = FTBICItems.REACTOR_PLATING;
	public static final Supplier<Item> CONTAINMENT_REACTOR_PLATING = FTBICItems.CONTAINMENT_REACTOR_PLATING;
	public static final Supplier<Item> HEAT_CAPACITY_REACTOR_PLATING = FTBICItems.HEAT_CAPACITY_REACTOR_PLATING;
	public static final Supplier<Item> NEUTRON_REFLECTOR = FTBICItems.NEUTRON_REFLECTOR;
	public static final Supplier<Item> THICK_NEUTRON_REFLECTOR = FTBICItems.THICK_NEUTRON_REFLECTOR;
	public static final Supplier<Item> IRIDIUM_NEUTRON_REFLECTOR = FTBICItems.IRIDIUM_NEUTRON_REFLECTOR;
	public static final Supplier<Item> CANNED_FOOD = FTBICItems.CANNED_FOOD;
	public static final Supplier<Item> PROTEIN_BAR = FTBICItems.PROTEIN_BAR;
	public static final Supplier<Item> DARK_SPRAY_CAN = FTBICItems.DARK_SPRAY_PAINT_CAN;
	public static final Supplier<Item> LIGHT_SPRAY_CAN = FTBICItems.LIGHT_SPRAY_PAINT_CAN;
	public static final Supplier<Item> OVERCLOCKER_UPGRADE = FTBICItems.OVERCLOCKER_UPGRADE;
	public static final Supplier<Item> ENERGY_STORAGE_UPGRADE = FTBICItems.ENERGY_STORAGE_UPGRADE;
	public static final Supplier<Item> TRANSFORMER_UPGRADE = FTBICItems.TRANSFORMER_UPGRADE;
	public static final Supplier<Item> EJECTOR_UPGRADE = FTBICItems.EJECTOR_UPGRADE;
	public static final Supplier<Item> MECHANICAL_ELYTRA = FTBICItems.MECHANICAL_ELYTRA;
	public static final Supplier<Item> CARBON_HELMET = FTBICItems.CARBON_HELMET;
	public static final Supplier<Item> CARBON_CHESTPLATE = FTBICItems.CARBON_CHESTPLATE;
	public static final Supplier<Item> CARBON_LEGGINGS = FTBICItems.CARBON_LEGGINGS;
	public static final Supplier<Item> CARBON_BOOTS = FTBICItems.CARBON_BOOTS;
	public static final Supplier<Item> QUANTUM_HELMET = FTBICItems.QUANTUM_HELMET;
	public static final Supplier<Item> QUANTUM_CHESTPLATE = FTBICItems.QUANTUM_CHESTPLATE;
	public static final Supplier<Item> QUANTUM_LEGGINGS = FTBICItems.QUANTUM_LEGGINGS;
	public static final Supplier<Item> QUANTUM_BOOTS = FTBICItems.QUANTUM_BOOTS;
	public static final Supplier<Item> NUKE_ARROW = FTBICItems.NUKE_ARROW;

	public static final Supplier<BlockItem> BASIC_GENERATOR = FTBICElectricBlocks.BASIC_GENERATOR.item;
	public static final Supplier<BlockItem> GEOTHERMAL_GENERATOR = FTBICElectricBlocks.GEOTHERMAL_GENERATOR.item;
	public static final Supplier<BlockItem> WIND_MILL = FTBICElectricBlocks.WIND_MILL.item;
	public static final Supplier<BlockItem> LV_SOLAR_PANEL = FTBICElectricBlocks.LV_SOLAR_PANEL.item;
	public static final Supplier<BlockItem> MV_SOLAR_PANEL = FTBICElectricBlocks.MV_SOLAR_PANEL.item;
	public static final Supplier<BlockItem> HV_SOLAR_PANEL = FTBICElectricBlocks.HV_SOLAR_PANEL.item;
	public static final Supplier<BlockItem> EV_SOLAR_PANEL = FTBICElectricBlocks.EV_SOLAR_PANEL.item;
	public static final Supplier<BlockItem> NUCLEAR_REACTOR = FTBICElectricBlocks.NUCLEAR_REACTOR.item;
	public static final Supplier<BlockItem> POWERED_FURNACE = FTBICElectricBlocks.POWERED_FURNACE.item;
	public static final Supplier<BlockItem> MACERATOR = FTBICElectricBlocks.MACERATOR.item;
	public static final Supplier<BlockItem> CENTRIFUGE = FTBICElectricBlocks.CENTRIFUGE.item;
	public static final Supplier<BlockItem> COMPRESSOR = FTBICElectricBlocks.COMPRESSOR.item;
	public static final Supplier<BlockItem> REPROCESSOR = FTBICElectricBlocks.REPROCESSOR.item;
	public static final Supplier<BlockItem> CANNING_MACHINE = FTBICElectricBlocks.CANNING_MACHINE.item;
	public static final Supplier<BlockItem> ROLLER = FTBICElectricBlocks.ROLLER.item;
	public static final Supplier<BlockItem> EXTRUDER = FTBICElectricBlocks.EXTRUDER.item;
	public static final Supplier<BlockItem> ANTIMATTER_FABRICATOR = FTBICElectricBlocks.ANTIMATTER_CONSTRUCTOR.item;
	public static final Supplier<BlockItem> ADVANCED_POWERED_FURNACE = FTBICElectricBlocks.ADVANCED_POWERED_FURNACE.item;
	public static final Supplier<BlockItem> ADVANCED_MACERATOR = FTBICElectricBlocks.ADVANCED_MACERATOR.item;
	public static final Supplier<BlockItem> ADVANCED_CENTRIFUGE = FTBICElectricBlocks.ADVANCED_CENTRIFUGE.item;
	public static final Supplier<BlockItem> ADVANCED_COMPRESSOR = FTBICElectricBlocks.ADVANCED_COMPRESSOR.item;
	public static final Supplier<BlockItem> TELEPORTER = FTBICElectricBlocks.TELEPORTER.item;
	public static final Supplier<BlockItem> CHARGE_PAD = FTBICElectricBlocks.CHARGE_PAD.item;
	public static final Supplier<BlockItem> POWERED_CRAFTING_TABLE = FTBICElectricBlocks.POWERED_CRAFTING_TABLE.item;
	public static final Supplier<BlockItem> QUARRY = FTBICElectricBlocks.QUARRY.item;
	public static final Supplier<BlockItem> PUMP = FTBICElectricBlocks.PUMP.item;
	public static final Supplier<BlockItem> LV_BATTERY_BOX = FTBICElectricBlocks.LV_BATTERY_BOX.item;
	public static final Supplier<BlockItem> MV_BATTERY_BOX = FTBICElectricBlocks.MV_BATTERY_BOX.item;
	public static final Supplier<BlockItem> HV_BATTERY_BOX = FTBICElectricBlocks.HV_BATTERY_BOX.item;
	public static final Supplier<BlockItem> EV_BATTERY_BOX = FTBICElectricBlocks.EV_BATTERY_BOX.item;
	public static final Supplier<BlockItem> LV_TRANSFORMER = FTBICElectricBlocks.LV_TRANSFORMER.item;
	public static final Supplier<BlockItem> MV_TRANSFORMER = FTBICElectricBlocks.MV_TRANSFORMER.item;
	public static final Supplier<BlockItem> HV_TRANSFORMER = FTBICElectricBlocks.HV_TRANSFORMER.item;
	public static final Supplier<BlockItem> EV_TRANSFORMER = FTBICElectricBlocks.EV_TRANSFORMER.item;

	public FTBICRecipesGen(DataGenerator generator) {
		super(generator);
	}

	public static ResourceLocation modLoc(String s) {
		return new ResourceLocation(MODID, s);
	}

	public static ResourceLocation smeltingLoc(String s) {
		return modLoc("smelting/" + s);
	}

	public static ResourceLocation blastingLoc(String s) {
		return modLoc("blasting/" + s);
	}

	public static ResourceLocation campfireCookingLoc(String s) {
		return modLoc("campfire_cooking/" + s);
	}

	public static ResourceLocation smokingLoc(String s) {
		return modLoc("smoking/" + s);
	}

	public static ResourceLocation shapedLoc(String s) {
		return modLoc("shaped/" + s);
	}

	public static ResourceLocation shapelessLoc(String s) {
		return modLoc("shapeless/" + s);
	}

	public static ResourceLocation smithingLoc(String s) {
		return modLoc("smithing/" + s);
	}

	public static ResourceLocation maceratingLoc(String s) {
		return modLoc("macerating/" + s);
	}

	public static ResourceLocation separatingLoc(String s) {
		return modLoc("separating/" + s);
	}

	public static ResourceLocation compressingLoc(String s) {
		return modLoc("compressing/" + s);
	}

	public static ResourceLocation canningLoc(String s) {
		return modLoc("canning/" + s);
	}

	public static ResourceLocation rollingLoc(String s) {
		return modLoc("rolling/" + s);
	}

	public static ResourceLocation extrudingLoc(String s) {
		return modLoc("extruding/" + s);
	}

	public static ResourceLocation reconstructingLoc(String s) {
		return modLoc("reconstructing/" + s);
	}

	public static ResourceLocation antimatterBoostLoc(String s) {
		return modLoc("antimatter_boost/" + s);
	}

	@Override
	protected final void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
		add(consumer);
	}

	public abstract void add(Consumer<FinishedRecipe> consumer);

	public Ingredient unbroken(ItemLike item) {
		return new NBTIngredientExt(new ItemStack(item));
	}

	public Ingredient waterCell() {
		return new NBTIngredientExt(FluidCellItem.setFluid(new ItemStack(FLUID_CELL.get()), Fluids.WATER));
	}


}
