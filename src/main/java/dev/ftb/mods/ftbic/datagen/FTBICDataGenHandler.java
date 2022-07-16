package dev.ftb.mods.ftbic.datagen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.BaseCableBlock;
import dev.ftb.mods.ftbic.block.CableBlock;
import dev.ftb.mods.ftbic.block.ElectricBlock;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.SprayPaintable;
import dev.ftb.mods.ftbic.datagen.recipes.FTBICArmorRecipes;
import dev.ftb.mods.ftbic.datagen.recipes.FTBICBatteryRecipes;
import dev.ftb.mods.ftbic.datagen.recipes.FTBICCableRecipes;
import dev.ftb.mods.ftbic.datagen.recipes.FTBICComponentRecipes;
import dev.ftb.mods.ftbic.datagen.recipes.FTBICCraftingRecipes;
import dev.ftb.mods.ftbic.datagen.recipes.FTBICEnergyStorageRecipes;
import dev.ftb.mods.ftbic.datagen.recipes.FTBICExtrudingRecipes;
import dev.ftb.mods.ftbic.datagen.recipes.FTBICFurnaceRecipes;
import dev.ftb.mods.ftbic.datagen.recipes.FTBICGeneratorFuelRecipes;
import dev.ftb.mods.ftbic.datagen.recipes.FTBICGeneratorRecipes;
import dev.ftb.mods.ftbic.datagen.recipes.FTBICMaceratingRecipes;
import dev.ftb.mods.ftbic.datagen.recipes.FTBICMachineRecipes;
import dev.ftb.mods.ftbic.datagen.recipes.FTBICNuclearRecipes;
import dev.ftb.mods.ftbic.datagen.recipes.FTBICRollingRecipes;
import dev.ftb.mods.ftbic.datagen.recipes.FTBICToolRecipes;
import dev.ftb.mods.ftbic.datagen.recipes.FTBICUpgradeRecipes;
import dev.ftb.mods.ftbic.datagen.recipes.FTBICVanillaRecipes;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.item.MaterialItem;
import dev.ftb.mods.ftbic.util.BurntBlockCondition;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import dev.ftb.mods.ftbic.world.ResourceElements;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.client.model.generators.loaders.CompositeModelBuilder;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static dev.ftb.mods.ftbic.datagen.FTBICRecipesGen.*;
import static dev.ftb.mods.ftbic.world.ResourceElements.COAL;
import static dev.ftb.mods.ftbic.world.ResourceElements.DIAMOND;
import static dev.ftb.mods.ftbic.world.ResourceElements.OBSIDIAN;
import static dev.ftb.mods.ftbic.world.ResourceElements.*;
import static dev.ftb.mods.ftbic.world.ResourceType.*;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBIC.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FTBICDataGenHandler {
	public static final String MODID = FTBIC.MOD_ID;

	@SubscribeEvent
	public static void dataGenEvent(GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();
		ExistingFileHelper efh = event.getExistingFileHelper();

		if (event.includeClient()) {
			gen.addProvider(event.includeServer(), new FTBICLang(gen, MODID, "en_us"));
			gen.addProvider(event.includeServer(), new FTBICTextures(gen, MODID, efh));
			gen.addProvider(event.includeServer(), new FTBICBlockModels(gen, MODID, efh));
			gen.addProvider(event.includeServer(), new FTBICBlockStates(gen, MODID, efh));
			gen.addProvider(event.includeServer(), new FTBICItemModels(gen, MODID, efh));
		}

		if (event.includeServer()) {
			ICBlockTags blockTags = new ICBlockTags(gen, MODID, efh);
			gen.addProvider(event.includeServer(), blockTags);
			gen.addProvider(event.includeServer(), new FTBICItemTags(gen, blockTags, MODID, efh));
			gen.addProvider(event.includeServer(), new ICBiomeTags(gen, MODID, efh));
			gen.addProvider(event.includeServer(), new FTBICComponentRecipes(gen));
			gen.addProvider(event.includeServer(), new FTBICUpgradeRecipes(gen));
			gen.addProvider(event.includeServer(), new FTBICCableRecipes(gen));
			gen.addProvider(event.includeServer(), new FTBICBatteryRecipes(gen));
			gen.addProvider(event.includeServer(), new FTBICGeneratorRecipes(gen));
			gen.addProvider(event.includeServer(), new FTBICMachineRecipes(gen));
			gen.addProvider(event.includeServer(), new FTBICEnergyStorageRecipes(gen));
			gen.addProvider(event.includeServer(), new FTBICToolRecipes(gen));
			gen.addProvider(event.includeServer(), new FTBICArmorRecipes(gen));
			gen.addProvider(event.includeServer(), new FTBICNuclearRecipes(gen));
			gen.addProvider(event.includeServer(), new FTBICGeneratorFuelRecipes(gen));
			gen.addProvider(event.includeServer(), new FTBICVanillaRecipes(gen));
			gen.addProvider(event.includeServer(), new FTBICLootTableProvider(gen));
			gen.addProvider(event.includeServer(), new FTBICFurnaceRecipes(gen));
			gen.addProvider(event.includeServer(), new FTBICMaceratingRecipes(gen));
			gen.addProvider(event.includeServer(), new FTBICCraftingRecipes(gen));
			gen.addProvider(event.includeServer(), new FTBICRollingRecipes(gen));
			gen.addProvider(event.includeServer(), new FTBICExtrudingRecipes(gen));
		}

		gen.addProvider(event.includeServer(), new FTBICBiomeModifierDataGen(gen, MODID));
	}

	private static String titleCase(String input) {
		return Character.toUpperCase(input.charAt(0)) + input.substring(1);
	}

	private static class FTBICLang extends LanguageProvider {
		public FTBICLang(DataGenerator gen, String modid, String locale) {
			super(gen, modid, locale);
		}

		private void addBlock(Supplier<Block> block) {
			addBlockWithSuffix(block, "");
		}

		private void addBlockWithSuffix(Supplier<Block> block, String suffix) {
			addBlock(block, Arrays.stream(Registry.BLOCK.getKey(block.get()).getPath().split("_")).map(FTBICDataGenHandler::titleCase).collect(Collectors.joining(" ")) + suffix);
		}

		private void addItemWithSuffix(Supplier<Item> item, String suffix) {
			addItem(item, Arrays.stream(Registry.ITEM.getKey(item.get()).getPath().split("_")).map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1)).collect(Collectors.joining(" ")));
		}

		private void addItem(Supplier<Item> item) {
			addItemWithSuffix(item, "");
		}

		@Override
		protected void addTranslations() {
			add("itemGroup.ftbic", "FTB Industrial Contraptions");

			addBlock(FTBICBlocks.RUBBER_SHEET);
			addBlock(FTBICBlocks.REINFORCED_STONE);
			addBlock(FTBICBlocks.REINFORCED_GLASS);
			addBlock(FTBICBlocks.MACHINE_BLOCK);
			addBlock(FTBICBlocks.ADVANCED_MACHINE_BLOCK);
			addBlock(FTBICBlocks.IRON_FURNACE);
			addBlock(FTBICBlocks.LV_CABLE, "LV Cable");
			addBlock(FTBICBlocks.MV_CABLE, "MV Cable");
			addBlock(FTBICBlocks.HV_CABLE, "HV Cable");
			addBlock(FTBICBlocks.EV_CABLE, "EV Cable");
			addBlock(FTBICBlocks.IV_CABLE, "IV Cable");
			addBlock(FTBICBlocks.BURNT_CABLE);
			addBlock(FTBICBlocks.LANDMARK);
			addBlock(FTBICBlocks.EXFLUID, "Ex-Fluid");
			addBlock(FTBICBlocks.NUCLEAR_REACTOR_CHAMBER);
			addBlock(FTBICBlocks.NUKE);
			addBlock(FTBICBlocks.ACTIVE_NUKE);

//			FTBICBlocks.RESOURCE_ORES.values()
//					.forEach(this::addBlock);

			FTBICItems.RESOURCE_TYPE_MAP.forEach((k, v) -> v.forEach((k2, v2) -> addItemWithSuffix(v2, " " + titleCase(k.name().toLowerCase(Locale.ENGLISH)))));

			for (ElectricBlockInstance machine : FTBICElectricBlocks.ALL) {
				addBlock(machine.block, machine.name);
			}

			for (MaterialItem item : FTBICItems.MATERIALS) {
				addItem(item.item, item.name);
			}

			addItem(FTBICItems.SINGLE_USE_BATTERY);
			addItem(FTBICItems.LV_BATTERY, "LV Battery");
			addItem(FTBICItems.MV_BATTERY, "MV Battery");
			addItem(FTBICItems.HV_BATTERY, "HV Battery");
			addItem(FTBICItems.EV_BATTERY, "EV Battery");
			addItem(FTBICItems.CREATIVE_BATTERY);
			addItem(FTBICItems.FLUID_CELL);
			addItem(FTBICItems.SMALL_COOLANT_CELL);
			addItem(FTBICItems.MEDIUM_COOLANT_CELL);
			addItem(FTBICItems.LARGE_COOLANT_CELL);
			addItem(FTBICItems.URANIUM_FUEL_ROD);
			addItem(FTBICItems.DUAL_URANIUM_FUEL_ROD);
			addItem(FTBICItems.QUAD_URANIUM_FUEL_ROD);
			addItem(FTBICItems.HEAT_VENT);
			addItem(FTBICItems.ADVANCED_HEAT_VENT);
			addItem(FTBICItems.OVERCLOCKED_HEAT_VENT);
			addItem(FTBICItems.REACTOR_HEAT_VENT);
			addItem(FTBICItems.COMPONENT_HEAT_VENT);
			addItem(FTBICItems.HEAT_EXCHANGER);
			addItem(FTBICItems.ADVANCED_HEAT_EXCHANGER);
			addItem(FTBICItems.REACTOR_HEAT_EXCHANGER);
			addItem(FTBICItems.COMPONENT_HEAT_EXCHANGER);
			addItem(FTBICItems.REACTOR_PLATING);
			addItem(FTBICItems.CONTAINMENT_REACTOR_PLATING);
			addItem(FTBICItems.HEAT_CAPACITY_REACTOR_PLATING, "Heat-Capacity Reactor Plating");
			addItem(FTBICItems.NEUTRON_REFLECTOR);
			addItem(FTBICItems.THICK_NEUTRON_REFLECTOR);
			addItem(FTBICItems.IRIDIUM_NEUTRON_REFLECTOR);
			addItem(FTBICItems.CANNED_FOOD);
			addItem(FTBICItems.PROTEIN_BAR, "Feed The Beast™ Protein Bar");
			addItem(FTBICItems.DARK_SPRAY_PAINT_CAN, "Spray Paint Can (Dark)");
			addItem(FTBICItems.LIGHT_SPRAY_PAINT_CAN, "Spray Paint Can (Light)");
			addItem(FTBICItems.OVERCLOCKER_UPGRADE);
			addItem(FTBICItems.ENERGY_STORAGE_UPGRADE);
			addItem(FTBICItems.TRANSFORMER_UPGRADE);
			addItem(FTBICItems.EJECTOR_UPGRADE);
			addItem(FTBICItems.MECHANICAL_ELYTRA);
			addItem(FTBICItems.CARBON_HELMET);
			addItem(FTBICItems.CARBON_CHESTPLATE);
			addItem(FTBICItems.CARBON_LEGGINGS);
			addItem(FTBICItems.CARBON_BOOTS);
			addItem(FTBICItems.QUANTUM_HELMET);
			addItem(FTBICItems.QUANTUM_CHESTPLATE);
			addItem(FTBICItems.QUANTUM_LEGGINGS);
			addItem(FTBICItems.QUANTUM_BOOTS);
			addItem(FTBICItems.NUKE_ARROW);

			add("recipe." + FTBIC.MOD_ID + ".macerating", "Macerating");
			add("recipe." + FTBIC.MOD_ID + ".separating", "Separating");
			add("recipe." + FTBIC.MOD_ID + ".compressing", "Compressing");
			add("recipe." + FTBIC.MOD_ID + ".reprocessing", "Reprocessing");
			add("recipe." + FTBIC.MOD_ID + ".canning", "Canning");
			add("recipe." + FTBIC.MOD_ID + ".rolling", "Rolling");
			add("recipe." + FTBIC.MOD_ID + ".extruding", "Extruding");
			add("recipe." + FTBIC.MOD_ID + ".reconstructing", "Reconstructing");

			add("ftbic.energy_output", "Energy Output: %s");
			add("ftbic.energy_capacity", "Energy Capacity: %s");
			add("ftbic.energy_usage", "Energy Usage: %s");
			add("ftbic.max_input", "Max Input: %s");
			add("ftbic.fuse_info", "This machine's fuse has blown! Right-click it with a fresh one to repair it!");
			add("ftbic.any_item", "Any Item");
			add("ftbic.requires_chestplate", "Requires Chestplate to function");
			add("ftbic.zap_to_fe_conversion", "Allows one-way conversion of %1$s to FE (1 %1$s = %2$s FE)");
			add("item.ftbic.spray_paint_can.tooltip", "Right-click on a machine to change its theme");
			add("block.ftbic.teleporter.perm_error", "Only owner of this teleporter can change it's settings!");
			add("block.ftbic.teleporter.load_error", "The destination chunk has to be loaded!");
			add("block.ftbic.nuke.broadcast", "%s has launched a Nuke!");
			add("block.ftbic.nuclear_reactor.broadcast", "%s forgot to cool their Nuclear Reactor!");
		}
	}

	private static class FTBICTextures extends CombinedTextureProvider {
		public static final int WOOD_UNIVERSAL = 0;
		public static final int BASIC_UNIVERSAL = 1;
		public static final int BASIC_TOP = 2;
		public static final int BASIC_BOTTOM = 3;
		public static final int BASIC_SIDE = 4;
		public static final int ADVANCED_UNIVERSAL = 5;
		public static final int ADVANCED_TOP = 6;
		public static final int ADVANCED_BOTTOM = 7;
		public static final int ADVANCED_SIDE = 8;

		public static final String[] TEMPLATES = {
				"wood_universal",
				"basic_universal",
				"basic_top",
				"basic_bottom",
				"basic_side",
				"advanced_universal",
				"advanced_top",
				"advanced_bottom",
				"advanced_side"
		};

		public FTBICTextures(DataGenerator g, String mod, ExistingFileHelper efh) {
			super(g, mod, efh);
		}

		private void makeThemedElectric(String path, int texture, String source) {
			TextureData lightBase = load(modLoc("block/electric/light/" + TEMPLATES[texture]));
			TextureData darkBase = load(modLoc("block/electric/dark/" + TEMPLATES[texture]));
			make(modLoc("block/electric/light/" + path), lightBase.combine(load(modLoc("block/electric/light/template/" + source))));
			make(modLoc("block/electric/dark/" + path), darkBase.combine(load(modLoc("block/electric/dark/template/" + source))));
		}

		private void makeThemedElectric(String path, int texture) {
			makeThemedElectric(path, texture, path);
		}

		private void makeThemedElectricOnOff(String path, int texture, String source) {
			makeThemedElectric(path + "_on", texture, source + "_on");
			makeThemedElectric(path + "_off", texture, source + "_off");
		}

		private void makeThemedElectricOnOff(String path, int texture) {
			makeThemedElectricOnOff(path, texture, path);
		}

		@Override
		public void registerTextures() {
			makeThemedElectricOnOff("basic_generator_front", BASIC_SIDE);
			makeThemedElectricOnOff("geothermal_generator_front", BASIC_SIDE);
			makeThemedElectric("wind_mill_front", BASIC_SIDE);
			makeThemedElectric("lv_solar_panel_top", BASIC_TOP);
			makeThemedElectric("mv_solar_panel_top", BASIC_TOP);
			makeThemedElectric("hv_solar_panel_top", ADVANCED_TOP);
			makeThemedElectric("ev_solar_panel_top", ADVANCED_TOP);
			makeThemedElectricOnOff("nuclear_reactor_front", ADVANCED_SIDE);
			makeThemedElectric("nuclear_reactor_top", ADVANCED_TOP);

			makeThemedElectricOnOff("powered_furnace_front", BASIC_SIDE);
			makeThemedElectric("macerator_front", BASIC_SIDE);
			makeThemedElectricOnOff("macerator_top", BASIC_TOP);
			makeThemedElectricOnOff("centrifuge_front", BASIC_SIDE);
			makeThemedElectric("centrifuge_top", BASIC_TOP);
			makeThemedElectricOnOff("compressor_front", BASIC_SIDE);
			makeThemedElectric("compressor_top", BASIC_TOP);
			makeThemedElectricOnOff("reprocessor_front", BASIC_SIDE);
			makeThemedElectric("reprocessor_top", BASIC_TOP);
			makeThemedElectricOnOff("canning_machine_front", BASIC_SIDE);
			makeThemedElectricOnOff("roller_front", BASIC_SIDE);
			makeThemedElectricOnOff("extruder_front", BASIC_SIDE);
			makeThemedElectricOnOff("antimatter_constructor_front", ADVANCED_SIDE);
			makeThemedElectric("antimatter_constructor_side", ADVANCED_SIDE);

			makeThemedElectricOnOff("advanced_powered_furnace_front", ADVANCED_SIDE, "powered_furnace_front");
			makeThemedElectric("advanced_macerator_front", ADVANCED_SIDE, "macerator_front");
			makeThemedElectricOnOff("advanced_macerator_top", ADVANCED_TOP, "macerator_top");
			makeThemedElectricOnOff("advanced_centrifuge_front", ADVANCED_SIDE, "centrifuge_front");
			makeThemedElectric("advanced_centrifuge_top", ADVANCED_TOP, "centrifuge_top");
			makeThemedElectricOnOff("advanced_compressor_front", ADVANCED_SIDE, "compressor_front");
			makeThemedElectric("advanced_compressor_top", ADVANCED_TOP, "compressor_top");
			makeThemedElectricOnOff("teleporter_top", ADVANCED_TOP, "teleporter_top");
			makeThemedElectricOnOff("charge_pad_top", BASIC_TOP, "charge_pad_top");
			makeThemedElectric("powered_crafting_table_top", BASIC_TOP, "powered_crafting_table_top");
			makeThemedElectricOnOff("quarry_front", ADVANCED_SIDE);
			makeThemedElectricOnOff("pump_front", ADVANCED_SIDE);

			makeThemedElectric("lv_battery_box_in", WOOD_UNIVERSAL);
			makeThemedElectric("lv_battery_box_out", WOOD_UNIVERSAL);
			makeThemedElectric("lv_transformer_in", WOOD_UNIVERSAL);
			makeThemedElectric("lv_transformer_out", WOOD_UNIVERSAL);

			makeThemedElectric("mv_battery_box_in", BASIC_UNIVERSAL);
			makeThemedElectric("mv_battery_box_out", BASIC_UNIVERSAL);
			makeThemedElectric("mv_transformer_in", BASIC_UNIVERSAL);
			makeThemedElectric("mv_transformer_out", BASIC_UNIVERSAL);

			makeThemedElectric("hv_battery_box_in", ADVANCED_UNIVERSAL);
			makeThemedElectric("hv_battery_box_out", ADVANCED_UNIVERSAL);
			makeThemedElectric("hv_transformer_in", ADVANCED_UNIVERSAL);
			makeThemedElectric("hv_transformer_out", ADVANCED_UNIVERSAL);

			makeThemedElectric("ev_battery_box_in", ADVANCED_UNIVERSAL);
			makeThemedElectric("ev_battery_box_out", ADVANCED_UNIVERSAL);
			makeThemedElectric("ev_transformer_in", ADVANCED_UNIVERSAL);
			makeThemedElectric("ev_transformer_out", ADVANCED_UNIVERSAL);
		}
	}

	private static class FTBICBlockModels extends BlockModelProvider {
		public static final String BASIC_TOP = "basic_top";
		public static final String BASIC_BOTTOM = "basic_bottom";
		public static final String BASIC_SIDE = "basic_side";
		public static final String ADVANCED_TOP = "advanced_top";
		public static final String ADVANCED_BOTTOM = "advanced_bottom";
		public static final String ADVANCED_SIDE = "advanced_side";

		public FTBICBlockModels(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
			super(generator, modid, existingFileHelper);
		}

		private void electric(String id, String front, String side, String top, String bottom) {
			withExistingParent("block/electric/light/" + id, modLoc("block/orientable_2d")).texture("side", modLoc("block/electric/light/" + side)).texture("front", modLoc("block/electric/light/" + front)).texture("top", modLoc("block/electric/light/" + top)).texture("bottom", modLoc("block/electric/light/" + bottom));
			withExistingParent("block/electric/dark/" + id, modLoc("block/orientable_2d")).texture("side", modLoc("block/electric/dark/" + side)).texture("front", modLoc("block/electric/dark/" + front)).texture("top", modLoc("block/electric/dark/" + top)).texture("bottom", modLoc("block/electric/dark/" + bottom));
		}

		private void electric3d(String id, String front, String side) {
			withExistingParent("block/electric/light/" + id, modLoc("block/orientable_3d")).texture("side", modLoc("block/electric/light/" + side)).texture("front", modLoc("block/electric/light/" + front));
			withExistingParent("block/electric/dark/" + id, modLoc("block/orientable_3d")).texture("side", modLoc("block/electric/dark/" + side)).texture("front", modLoc("block/electric/dark/" + front));
		}

		@Override
		protected void registerModels() {
			withExistingParent("block/rubber_sheet", "block/block")
					.texture("texture", modLoc("block/rubber_sheet"))
					.texture("particle", modLoc("block/rubber_sheet"))
					.element()
					.from(0F, 0F, 0F)
					.to(16F, 3F, 16F)
					.face(Direction.DOWN).texture("#texture").cullface(Direction.DOWN).end()
					.face(Direction.UP).texture("#texture").end()
					.face(Direction.NORTH).texture("#texture").cullface(Direction.NORTH).end()
					.face(Direction.SOUTH).texture("#texture").cullface(Direction.SOUTH).end()
					.face(Direction.WEST).texture("#texture").cullface(Direction.WEST).end()
					.face(Direction.EAST).texture("#texture").cullface(Direction.EAST).end()
					.end()
			;

			withExistingParent("block/machine_block", "block/cube_all")
					.texture("all", modLoc("block/electric/light/basic_side"))
					.texture("top", modLoc("block/electric/light/basic_top"))
					.texture("bottom", modLoc("block/electric/light/basic_bottom"))
			;

			withExistingParent("block/advanced_machine_block", "block/cube_all")
					.texture("all", modLoc("block/electric/light/advanced_side"))
					.texture("top", modLoc("block/electric/light/advanced_top"))
					.texture("bottom", modLoc("block/electric/light/advanced_bottom"))
			;

			withExistingParent("block/orientable_2d", "block/block")
					.texture("particle", "#side")
					.transforms()
					.transform(ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND)
					.rotation(0F, 135F, 0F)
					.translation(0F, 0F, 0F)
					.scale(0.4F, 0.4F, 0.4F)
					.end()
					.end()
					.element()
					.from(0F, 0F, 0F)
					.to(16F, 16F, 16F)
					.face(Direction.DOWN).texture("#bottom").cullface(Direction.DOWN).end()
					.face(Direction.UP).texture("#top").cullface(Direction.UP).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
					.face(Direction.NORTH).texture("#front").cullface(Direction.NORTH).end()
					.face(Direction.SOUTH).texture("#side").cullface(Direction.SOUTH).end()
					.face(Direction.WEST).texture("#side").cullface(Direction.WEST).end()
					.face(Direction.EAST).texture("#side").cullface(Direction.EAST).end()
					.end()
			;

			withExistingParent("block/orientable_3d", "block/block")
					.texture("particle", "#side")
					.transforms()
					.transform(ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND)
					.rotation(0F, 135F, 0F)
					.translation(0F, 0F, 0F)
					.scale(0.4F, 0.4F, 0.4F)
					.end()
					.end()
					.element()
					.from(0F, 0F, 0F)
					.to(16F, 16F, 16F)
					.face(Direction.DOWN).texture("#side").cullface(Direction.DOWN).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
					.face(Direction.UP).texture("#side").cullface(Direction.UP).end()
					.face(Direction.NORTH).texture("#front").cullface(Direction.NORTH).end()
					.face(Direction.SOUTH).texture("#side").cullface(Direction.SOUTH).end() // #bottom
					.face(Direction.WEST).texture("#side").cullface(Direction.WEST).rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end()
					.face(Direction.EAST).texture("#side").cullface(Direction.EAST).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end()
					.end()
			;

			for (Supplier<Block> cable : FTBICBlocks.CABLES) {
				BaseCableBlock block = (BaseCableBlock) cable.get();
				String id = Registry.BLOCK.getKey(block).getPath();

				withExistingParent("block/" + id + "_base", "block/block")
						.texture("texture", modLoc("block/" + id))
						.texture("particle", modLoc("block/" + id))
						.element()
						.from(block.border, block.border, block.border)
						.to(16F - block.border, 16F - block.border, 16F - block.border)
						.face(Direction.DOWN).texture("#texture").end()
						.face(Direction.UP).texture("#texture").end()
						.face(Direction.NORTH).texture("#texture").end()
						.face(Direction.SOUTH).texture("#texture").end()
						.face(Direction.WEST).texture("#texture").end()
						.face(Direction.EAST).texture("#texture").end()
						.end();

				withExistingParent("block/" + id + "_connection", "block/block")
						.texture("texture", modLoc("block/" + id))
						.texture("particle", modLoc("block/" + id))
						.element()
						.from(block.border, 0, block.border)
						.to(16F - block.border, block.border, 16F - block.border)
						.face(Direction.DOWN).texture("#texture").cullface(Direction.DOWN).end()
						.face(Direction.NORTH).texture("#texture").end()
						.face(Direction.SOUTH).texture("#texture").end()
						.face(Direction.WEST).texture("#texture").end()
						.face(Direction.EAST).texture("#texture").end()
						.end();
			}

			orientable("block/iron_furnace_off", modLoc("block/iron_furnace_side"), modLoc("block/iron_furnace_front_off"), modLoc("block/iron_furnace_top"));
			orientable("block/iron_furnace_on", modLoc("block/iron_furnace_side"), modLoc("block/iron_furnace_front_on"), modLoc("block/iron_furnace_top"));

			withExistingParent("block/landmark", "block/block")
					.texture("particle", modLoc("block/landmark_ns"))
					.texture("landmark_ns", modLoc("block/landmark_ns"))
					.texture("landmark_we", modLoc("block/landmark_we"))
					.ao(false)
					.element()
					.from(7, 0, 7)
					.to(9, 7, 9)
					.shade(false)
					.face(Direction.DOWN).texture("#landmark_ns").cullface(Direction.DOWN).end()
					.face(Direction.NORTH).texture("#landmark_ns").end()
					.face(Direction.SOUTH).texture("#landmark_ns").end()
					.face(Direction.WEST).texture("#landmark_we").end()
					.face(Direction.EAST).texture("#landmark_we").end()
					.end()
					.element()
					.from(6.97F, 6.97F, 6.97F)
					.to(9.03F, 9.03F, 9.03F)
					.shade(false)
					.face(Direction.DOWN).uvs(7, 7, 9, 9).texture("#landmark_ns").end()
					.face(Direction.UP).uvs(7, 7, 9, 9).texture("#landmark_ns").end()
					.face(Direction.NORTH).uvs(7, 7, 9, 9).texture("#landmark_ns").end()
					.face(Direction.SOUTH).uvs(7, 7, 9, 9).texture("#landmark_ns").end()
					.face(Direction.WEST).uvs(7, 7, 9, 9).texture("#landmark_we").end()
					.face(Direction.EAST).uvs(7, 7, 9, 9).texture("#landmark_we").end()
					.end()
			;

			withExistingParent("block/nuclear_reactor_chamber", "block/cube_bottom_top")
					.texture("top", modLoc("block/electric/light/nuclear_reactor_top"))
					.texture("bottom", modLoc("block/electric/light/advanced_bottom"))
					.texture("side", modLoc("block/electric/light/advanced_side"))
			;

			withExistingParent("block/nuke", "block/tnt")
					.texture("side", modLoc("block/nuke_side"))
			;

			withExistingParent("block/active_nuke", "block/tnt")
					.texture("side", modLoc("block/nuke_side_active"))
			;

			withExistingParent("block/tractor_beam", "block/block")
					.texture("particle", modLoc("block/tractor_beam"))
					.texture("texture", modLoc("block/tractor_beam"))
					.ao(false)
					.element()
					.from(0, 0, 0)
					.to(16, 16, 0)
					.shade(false)
					.face(Direction.NORTH).cullface(Direction.NORTH).texture("#texture").end()
					.face(Direction.SOUTH).cullface(Direction.NORTH).texture("#texture").end()
					.end()
					.element()
					.from(0, 0, 16)
					.to(16, 16, 16)
					.shade(false)
					.face(Direction.NORTH).cullface(Direction.SOUTH).texture("#texture").end()
					.face(Direction.SOUTH).cullface(Direction.SOUTH).texture("#texture").end()
					.end()
					.element()
					.from(0, 0, 0)
					.to(0, 16, 16)
					.shade(false)
					.face(Direction.WEST).cullface(Direction.WEST).texture("#texture").end()
					.face(Direction.EAST).cullface(Direction.WEST).texture("#texture").end()
					.end()
					.element()
					.from(16, 0, 0)
					.to(16, 16, 16)
					.shade(false)
					.face(Direction.WEST).cullface(Direction.EAST).texture("#texture").end()
					.face(Direction.EAST).cullface(Direction.EAST).texture("#texture").end()
					.end()
			;

			electric("basic_generator_off", "basic_generator_front_off", BASIC_SIDE, BASIC_TOP, BASIC_BOTTOM);
			electric("basic_generator_on", "basic_generator_front_on", BASIC_SIDE, BASIC_TOP, BASIC_BOTTOM);
			electric("geothermal_generator_off", "geothermal_generator_front_off", BASIC_SIDE, BASIC_TOP, BASIC_BOTTOM);
			electric("geothermal_generator_on", "geothermal_generator_front_on", BASIC_SIDE, BASIC_TOP, BASIC_BOTTOM);
			electric("wind_mill", "wind_mill_front", BASIC_SIDE, BASIC_TOP, BASIC_BOTTOM);
			electric("lv_solar_panel", BASIC_SIDE, BASIC_SIDE, "lv_solar_panel_top", BASIC_BOTTOM);
			electric("mv_solar_panel", BASIC_SIDE, BASIC_SIDE, "mv_solar_panel_top", BASIC_BOTTOM);
			electric("hv_solar_panel", ADVANCED_SIDE, ADVANCED_SIDE, "hv_solar_panel_top", ADVANCED_BOTTOM);
			electric("ev_solar_panel", ADVANCED_SIDE, ADVANCED_SIDE, "ev_solar_panel_top", ADVANCED_BOTTOM);
			electric("nuclear_reactor_off", "nuclear_reactor_front_off", ADVANCED_SIDE, "nuclear_reactor_top", ADVANCED_BOTTOM);
			electric("nuclear_reactor_on", "nuclear_reactor_front_on", ADVANCED_SIDE, "nuclear_reactor_top", ADVANCED_BOTTOM);

			electric("powered_furnace_off", "powered_furnace_front_off", BASIC_SIDE, BASIC_TOP, BASIC_BOTTOM);
			electric("powered_furnace_on", "powered_furnace_front_on", BASIC_SIDE, BASIC_TOP, BASIC_BOTTOM);
			electric("macerator_off", "macerator_front", BASIC_SIDE, "macerator_top_off", BASIC_BOTTOM);
			electric("macerator_on", "macerator_front", BASIC_SIDE, "macerator_top_on", BASIC_BOTTOM);
			electric("centrifuge_off", "centrifuge_front_off", BASIC_SIDE, "centrifuge_top", BASIC_BOTTOM);
			electric("centrifuge_on", "centrifuge_front_on", BASIC_SIDE, "centrifuge_top", BASIC_BOTTOM);
			electric("compressor_off", "compressor_front_off", BASIC_SIDE, "compressor_top", BASIC_BOTTOM);
			electric("compressor_on", "compressor_front_on", BASIC_SIDE, "compressor_top", BASIC_BOTTOM);
			electric("reprocessor_off", "reprocessor_front_off", BASIC_SIDE, "reprocessor_top", BASIC_BOTTOM);
			electric("reprocessor_on", "reprocessor_front_on", BASIC_SIDE, "reprocessor_top", BASIC_BOTTOM);
			electric("canning_machine_off", "canning_machine_front_off", BASIC_SIDE, BASIC_TOP, BASIC_BOTTOM);
			electric("canning_machine_on", "canning_machine_front_on", BASIC_SIDE, BASIC_TOP, BASIC_BOTTOM);
			electric("roller_off", "roller_front_off", BASIC_SIDE, BASIC_TOP, BASIC_BOTTOM);
			electric("roller_on", "roller_front_on", BASIC_SIDE, BASIC_TOP, BASIC_BOTTOM);
			electric("extruder_off", "extruder_front_off", BASIC_SIDE, BASIC_TOP, BASIC_BOTTOM);
			electric("extruder_on", "extruder_front_on", BASIC_SIDE, BASIC_TOP, BASIC_BOTTOM);
			electric("antimatter_constructor_off", "antimatter_constructor_front_off", "antimatter_constructor_side", ADVANCED_TOP, ADVANCED_BOTTOM);
			electric("antimatter_constructor_on", "antimatter_constructor_front_on", "antimatter_constructor_side", ADVANCED_TOP, ADVANCED_BOTTOM);

			electric("advanced_powered_furnace_off", "advanced_powered_furnace_front_off", ADVANCED_SIDE, ADVANCED_TOP, ADVANCED_BOTTOM);
			electric("advanced_powered_furnace_on", "advanced_powered_furnace_front_on", ADVANCED_SIDE, ADVANCED_TOP, ADVANCED_BOTTOM);
			electric("advanced_macerator_off", "advanced_macerator_front", ADVANCED_SIDE, "advanced_macerator_top_off", ADVANCED_BOTTOM);
			electric("advanced_macerator_on", "advanced_macerator_front", ADVANCED_SIDE, "advanced_macerator_top_on", ADVANCED_BOTTOM);
			electric("advanced_centrifuge_off", "advanced_centrifuge_front_off", ADVANCED_SIDE, "advanced_centrifuge_top", ADVANCED_BOTTOM);
			electric("advanced_centrifuge_on", "advanced_centrifuge_front_on", ADVANCED_SIDE, "advanced_centrifuge_top", ADVANCED_BOTTOM);
			electric("advanced_compressor_off", "advanced_compressor_front_off", ADVANCED_SIDE, "advanced_compressor_top", ADVANCED_BOTTOM);
			electric("advanced_compressor_on", "advanced_compressor_front_on", ADVANCED_SIDE, "advanced_compressor_top", ADVANCED_BOTTOM);
			electric("teleporter_off", ADVANCED_SIDE, ADVANCED_SIDE, "teleporter_top_off", ADVANCED_BOTTOM);
			electric("teleporter_on", ADVANCED_SIDE, ADVANCED_SIDE, "teleporter_top_on", ADVANCED_BOTTOM);
			electric("charge_pad_off", BASIC_SIDE, BASIC_SIDE, "charge_pad_top_off", BASIC_BOTTOM);
			electric("charge_pad_on", BASIC_SIDE, BASIC_SIDE, "charge_pad_top_on", BASIC_BOTTOM);
			electric("powered_crafting_table", BASIC_SIDE, BASIC_SIDE, "powered_crafting_table_top", BASIC_BOTTOM);
			electric("quarry_off", "quarry_front_off", ADVANCED_SIDE, ADVANCED_TOP, ADVANCED_BOTTOM);
			electric("quarry_on", "quarry_front_on", ADVANCED_SIDE, ADVANCED_TOP, ADVANCED_BOTTOM);
			electric("pump_off", "pump_front_off", ADVANCED_SIDE, ADVANCED_TOP, ADVANCED_BOTTOM);
			electric("pump_on", "pump_front_on", ADVANCED_SIDE, ADVANCED_TOP, ADVANCED_BOTTOM);

			electric3d("lv_battery_box", "lv_battery_box_out", "lv_battery_box_in");
			electric3d("lv_transformer", "lv_transformer_in", "lv_transformer_out");
			electric3d("mv_battery_box", "mv_battery_box_out", "mv_battery_box_in");
			electric3d("mv_transformer", "mv_transformer_in", "mv_transformer_out");
			electric3d("hv_battery_box", "hv_battery_box_out", "hv_battery_box_in");
			electric3d("hv_transformer", "hv_transformer_in", "hv_transformer_out");
			electric3d("ev_battery_box", "ev_battery_box_out", "ev_battery_box_in");
			electric3d("ev_transformer", "ev_transformer_in", "ev_transformer_out");
		}
	}

	private static class FTBICBlockStates extends BlockStateProvider {
		public FTBICBlockStates(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
			super(gen, modid, exFileHelper);
		}

		@Override
		protected void registerStatesAndModels() {
			simpleBlock(FTBICBlocks.RUBBER_SHEET.get(), models().getExistingFile(modLoc("block/rubber_sheet")));
			simpleBlock(FTBICBlocks.REINFORCED_STONE.get());
			simpleBlock(FTBICBlocks.REINFORCED_GLASS.get());
			simpleBlock(FTBICBlocks.MACHINE_BLOCK.get(), models().getExistingFile(modLoc("block/machine_block")));
			simpleBlock(FTBICBlocks.ADVANCED_MACHINE_BLOCK.get(), models().getExistingFile(modLoc("block/advanced_machine_block")));

			getVariantBuilder(FTBICBlocks.IRON_FURNACE.get()).forAllStatesExcept(state -> {
				boolean lit = state.getValue(BlockStateProperties.LIT);
				Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);

				return ConfiguredModel.builder()
						.modelFile(models().getExistingFile(modLoc(lit ? "block/iron_furnace_on" : "block/iron_furnace_off")))
						.rotationY(((facing.get2DDataValue() & 3) * 90 + 180) % 360)
						.build();
			});

			for (Supplier<Block> cable : FTBICBlocks.CABLES) {
				BaseCableBlock block = (BaseCableBlock) cable.get();
				String id = Registry.BLOCK.getKey(block).getPath();
				// simpleBlock(cable.get(), models().getExistingFile(modLoc("block/" + id + "_base")));

				MultiPartBlockStateBuilder builder = getMultipartBuilder(cable.get());
				builder.part().modelFile(models().getExistingFile(modLoc("block/" + id + "_base"))).addModel().end();
				builder.part().modelFile(models().getExistingFile(modLoc("block/" + id + "_connection"))).rotationX(0).rotationY(0).addModel().condition(CableBlock.CONNECTION[0], true).end();
				builder.part().modelFile(models().getExistingFile(modLoc("block/" + id + "_connection"))).rotationX(180).rotationY(0).addModel().condition(CableBlock.CONNECTION[1], true).end();
				builder.part().modelFile(models().getExistingFile(modLoc("block/" + id + "_connection"))).rotationX(90).rotationY(180).addModel().condition(CableBlock.CONNECTION[2], true).end();
				builder.part().modelFile(models().getExistingFile(modLoc("block/" + id + "_connection"))).rotationX(90).rotationY(0).addModel().condition(CableBlock.CONNECTION[3], true).end();
				builder.part().modelFile(models().getExistingFile(modLoc("block/" + id + "_connection"))).rotationX(90).rotationY(90).addModel().condition(CableBlock.CONNECTION[4], true).end();
				builder.part().modelFile(models().getExistingFile(modLoc("block/" + id + "_connection"))).rotationX(90).rotationY(270).addModel().condition(CableBlock.CONNECTION[5], true).end();
			}

			simpleBlock(FTBICBlocks.LANDMARK.get(), models().getExistingFile(modLoc("block/landmark")));

			simpleBlock(FTBICBlocks.EXFLUID.get(),
					new ConfiguredModel(models().getExistingFile(mcLoc("block/dead_horn_coral_block"))),
					new ConfiguredModel(models().getExistingFile(mcLoc("block/dead_fire_coral_block"))),
					new ConfiguredModel(models().getExistingFile(mcLoc("block/dead_bubble_coral_block"))),
					new ConfiguredModel(models().getExistingFile(mcLoc("block/dead_brain_coral_block"))),
					new ConfiguredModel(models().getExistingFile(mcLoc("block/dead_tube_coral_block")))
			);

			simpleBlock(FTBICBlocks.NUCLEAR_REACTOR_CHAMBER.get(), models().getExistingFile(modLoc("block/nuclear_reactor_chamber")));
			simpleBlock(FTBICBlocks.NUKE.get(), models().getExistingFile(modLoc("block/nuke")));
			simpleBlock(FTBICBlocks.ACTIVE_NUKE.get(), models().getExistingFile(modLoc("block/active_nuke")));

			FTBICBlocks.RESOURCE_BLOCKS_OF.values().forEach(e -> simpleBlock(e.get()));

			// Ores (Taken from EmendatusEnigmatica, thanks guys!)
			FTBICBlocks.RESOURCE_ORES.forEach((key, value) -> {
				ResourceLocation registryName = Registry.BLOCK.getKey(value.get());
				String overlayTexture = "block/ore_overlays/" + key.getName().replace("deepslate_", ""); // no deepslate textures

				models().getBuilder(registryName.getPath())
						.parent(new ModelFile.UncheckedModelFile(mcLoc("block/block")))
						.texture("particle", modLoc(overlayTexture))
						.transforms()
						.transform(ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND)
						.rotation(75F, 45F, 0F)
						.translation(0F, 2.5F, 0)
						.scale(0.375F, 0.375F, 0.375F)
						.end()
						.transform(ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND)
						.rotation(75F, 45F, 0F)
						.translation(0F, 2.5F, 0)
						.scale(0.375F, 0.375F, 0.375F)
						.end()
						.end()
						.customLoader(CompositeModelBuilder::begin)
						.child("solid", this.models().nested().parent(this.models().getExistingFile(key.getName().contains("deepslate") ? mcLoc("block/deepslate") : mcLoc("block/stone"))).renderType("solid"))
						.child("translucent", this.models().nested().parent(this.models().getExistingFile(mcLoc("block/cube_all"))).texture("all", modLoc(overlayTexture)).renderType("translucent"))
						.end();

				simpleBlock(value.get(), new ModelFile.UncheckedModelFile(modLoc("block/" + registryName.getPath())));
			});

			for (ElectricBlockInstance machine : FTBICElectricBlocks.ALL) {
				if (!machine.noModel) {
					getVariantBuilder(machine.block.get()).forAllStatesExcept(state -> {
						boolean on = machine.canBeActive && state.getValue(ElectricBlock.ACTIVE);
						boolean dark = state.getValue(SprayPaintable.DARK);
						ModelFile modelFile = models().getExistingFile(modLoc("block/electric/" + (dark ? "dark" : "light") + "/" + machine.id + (machine.canBeActive ? (on ? "_on" : "_off") : "")));

						if (machine.facingProperty == null) {
							return ConfiguredModel.builder()
									.modelFile(modelFile)
									.build();
						} else if (machine.facingProperty == BlockStateProperties.HORIZONTAL_FACING) {
							Direction facing = state.getValue(machine.facingProperty);

							return ConfiguredModel.builder()
									.modelFile(modelFile)
									.rotationY(((facing.get2DDataValue() & 3) * 90 + 180) % 360)
									.build();
						} else {
							Direction facing = state.getValue(machine.facingProperty);

							return ConfiguredModel.builder()
									.modelFile(modelFile)
									.rotationX(facing == Direction.DOWN ? 90 : (facing.getAxis().isHorizontal() ? 0 : 270))
									.rotationY(facing.getAxis().isVertical() ? 0 : ((facing.get2DDataValue() & 3) * 90 + 180) % 360)
									.build();
						}
					});
				}
			}
		}
	}

	private static class FTBICItemModels extends ItemModelProvider {
		public FTBICItemModels(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
			super(generator, modid, existingFileHelper);
		}

		private void basicItem(Supplier<? extends Item> item) {
			String id = Registry.ITEM.getKey(item.get()).getPath();
			singleTexture(id, mcLoc("item/generated"), "layer0", modLoc("item/" + id));
		}

		private void handheldItem(Supplier<? extends Item> item) {
			String id = Registry.ITEM.getKey(item.get()).getPath();
			singleTexture(id, mcLoc("item/handheld"), "layer0", modLoc("item/" + id));
		}

		private void basicBlockItem(Supplier<Block> block) {
			String id = Registry.BLOCK.getKey(block.get()).getPath();
			withExistingParent(id, modLoc("block/" + id));
		}

		@Override
		protected void registerModels() {
			basicBlockItem(FTBICBlocks.RUBBER_SHEET);
			basicBlockItem(FTBICBlocks.REINFORCED_STONE);
			basicBlockItem(FTBICBlocks.REINFORCED_GLASS);
			basicBlockItem(FTBICBlocks.MACHINE_BLOCK);
			basicBlockItem(FTBICBlocks.ADVANCED_MACHINE_BLOCK);
			withExistingParent(Registry.BLOCK.getKey(FTBICBlocks.IRON_FURNACE.get()).getPath(), modLoc("block/iron_furnace_off"));

			for (Supplier<Block> cable : FTBICBlocks.CABLES) {
				String id = Registry.BLOCK.getKey(cable.get()).getPath();
				int b0 = ((BaseCableBlock) cable.get()).border;
				int b1 = 16 - b0;

				withExistingParent("item/" + id, "block/block")
						.texture("particle", modLoc("block/" + id))
						.texture("cable", modLoc("block/" + id))
						.transforms()
						.transform(ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND)
						.rotation(75F, 45F, 0F)
						.translation(0F, 1.5F, 0F)
						.scale(0.375F, 0.375F, 0.375F)
						.end()
						.transform(ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND)
						.rotation(75F, 45F, 0F)
						.translation(0F, 1.5F, 0F)
						.scale(0.375F, 0.375F, 0.375F)
						.end()
						.transform(ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND)
						.rotation(0F, 45F, 0F)
						.scale(0.4F, 0.4F, 0.4F)
						.end()
						.transform(ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND)
						.rotation(0F, 225F, 0F)
						.scale(0.4F, 0.4F, 0.4F)
						.end()
						.transform(ItemTransforms.TransformType.GROUND)
						.translation(0F, 3F, 0F)
						.scale(0.25F, 0.25F, 0.25F)
						.end()
						.transform(ItemTransforms.TransformType.GUI)
						.rotation(45F, 45F, 90F)
						.translation(1.75F, 1F, 0F)
						.scale(0.625F, 0.625F, 0.625F)
						.end()
						.transform(ItemTransforms.TransformType.FIXED)
						.translation(0F, -1.5F, 0F)
						.scale(0.5F, 0.5F, 0.5F)
						.end()
						.end()
						.element()
						.from(b0, 0, b0)
						.to(b1, b1, b1)
						.face(Direction.NORTH).texture("#cable").uvs(b0, b0, b1, 16).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
						.face(Direction.EAST).texture("#cable").uvs(b0, b0, b1, 16).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
						.face(Direction.SOUTH).texture("#cable").uvs(b0, b0, b1, 16).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
						.face(Direction.WEST).texture("#cable").uvs(b0, b0, b1, 16).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
						.face(Direction.UP).texture("#cable").uvs(b0, b0, b1, b1).end()
						.face(Direction.DOWN).texture("#cable").uvs(b0, b0, b1, b1).end()
						.end()
						.element()
						.from(b0, b1, b0)
						.to(b1, b1 * 2, b1)
						.face(Direction.NORTH).texture("#cable").uvs(b1, 16, b0, b0).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
						.face(Direction.EAST).texture("#cable").uvs(b1, 16, b0, b0).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
						.face(Direction.SOUTH).texture("#cable").uvs(b1, 16, b0, b0).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
						.face(Direction.WEST).texture("#cable").uvs(b1, 16, b0, b0).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
						.face(Direction.UP).texture("#cable").uvs(b1, b1, b0, b0).end()
						.face(Direction.DOWN).texture("#cable").uvs(b1, b1, b0, b0).end()
						.end()
				;
			}

			singleTexture("landmark", mcLoc("item/generated"), "layer0", modLoc("block/landmark_ns"));
			withExistingParent(Registry.BLOCK.getKey(FTBICBlocks.EXFLUID.get()).getPath(), mcLoc("block/dead_horn_coral_block"));
			basicBlockItem(FTBICBlocks.NUCLEAR_REACTOR_CHAMBER);
			basicBlockItem(FTBICBlocks.NUKE);
			basicBlockItem(FTBICBlocks.ACTIVE_NUKE);

			for (ElectricBlockInstance machine : FTBICElectricBlocks.ALL) {
				if (!machine.noModel) {
					withExistingParent(machine.id, modLoc("block/electric/light/" + machine.id + (machine.canBeActive ? "_off" : "")));
				}
			}

			for (MaterialItem item : FTBICItems.MATERIALS) {
				basicItem(item.item);
			}

			basicItem(FTBICItems.SINGLE_USE_BATTERY);
			basicItem(FTBICItems.LV_BATTERY);
			basicItem(FTBICItems.MV_BATTERY);
			basicItem(FTBICItems.HV_BATTERY);
			basicItem(FTBICItems.EV_BATTERY);
			basicItem(FTBICItems.CREATIVE_BATTERY);
			basicItem(FTBICItems.SMALL_COOLANT_CELL);
			basicItem(FTBICItems.MEDIUM_COOLANT_CELL);
			basicItem(FTBICItems.LARGE_COOLANT_CELL);
			basicItem(FTBICItems.URANIUM_FUEL_ROD);
			basicItem(FTBICItems.DUAL_URANIUM_FUEL_ROD);
			basicItem(FTBICItems.QUAD_URANIUM_FUEL_ROD);
			basicItem(FTBICItems.HEAT_VENT);
			basicItem(FTBICItems.ADVANCED_HEAT_VENT);
			basicItem(FTBICItems.OVERCLOCKED_HEAT_VENT);
			basicItem(FTBICItems.REACTOR_HEAT_VENT);
			basicItem(FTBICItems.COMPONENT_HEAT_VENT);
			basicItem(FTBICItems.HEAT_EXCHANGER);
			basicItem(FTBICItems.ADVANCED_HEAT_EXCHANGER);
			basicItem(FTBICItems.REACTOR_HEAT_EXCHANGER);
			basicItem(FTBICItems.COMPONENT_HEAT_EXCHANGER);
			basicItem(FTBICItems.REACTOR_PLATING);
			basicItem(FTBICItems.CONTAINMENT_REACTOR_PLATING);
			basicItem(FTBICItems.HEAT_CAPACITY_REACTOR_PLATING);
			basicItem(FTBICItems.NEUTRON_REFLECTOR);
			basicItem(FTBICItems.THICK_NEUTRON_REFLECTOR);
			basicItem(FTBICItems.IRIDIUM_NEUTRON_REFLECTOR);
			basicItem(FTBICItems.CANNED_FOOD);
			basicItem(FTBICItems.PROTEIN_BAR);
			basicItem(FTBICItems.DARK_SPRAY_PAINT_CAN);
			basicItem(FTBICItems.LIGHT_SPRAY_PAINT_CAN);
			basicItem(FTBICItems.OVERCLOCKER_UPGRADE);
			basicItem(FTBICItems.ENERGY_STORAGE_UPGRADE);
			basicItem(FTBICItems.TRANSFORMER_UPGRADE);
			basicItem(FTBICItems.EJECTOR_UPGRADE);
			basicItem(FTBICItems.MECHANICAL_ELYTRA);
			basicItem(FTBICItems.CARBON_HELMET);
			basicItem(FTBICItems.CARBON_CHESTPLATE);
			basicItem(FTBICItems.CARBON_LEGGINGS);
			basicItem(FTBICItems.CARBON_BOOTS);
			basicItem(FTBICItems.QUANTUM_HELMET);
			basicItem(FTBICItems.QUANTUM_CHESTPLATE);
			basicItem(FTBICItems.QUANTUM_LEGGINGS);
			basicItem(FTBICItems.QUANTUM_BOOTS);
			basicItem(FTBICItems.NUKE_ARROW);

			FTBICBlocks.RESOURCE_ORES.values().forEach(this::basicBlockItem);
			FTBICBlocks.RESOURCE_BLOCKS_OF.values().forEach(this::basicBlockItem);

			FTBICItems.RESOURCE_TYPE_MAP.forEach((k, v) -> {
				// Don't register ores as items
				if (k.equals(ORE) || k.equals(BLOCK)) {
					return;
				}

				v.forEach((k2, v2) -> basicItem(v2));
			});
		}
	}

	private static class ICBlockTags extends BlockTagsProvider {
		public ICBlockTags(DataGenerator generatorIn, String modId, ExistingFileHelper existingFileHelper) {
			super(generatorIn, modId, existingFileHelper);
		}

		@Override
		protected void addTags() {
			tag(FTBICUtils.REINFORCED).add(
					FTBICBlocks.REINFORCED_STONE.get(),
					FTBICBlocks.REINFORCED_GLASS.get(),
					Blocks.BEDROCK,
					Blocks.BARRIER,
					Blocks.COMMAND_BLOCK
			);

			tag(BlockTags.DRAGON_IMMUNE).add(FTBICBlocks.REINFORCED_STONE.get(), FTBICBlocks.REINFORCED_GLASS.get());
			tag(BlockTags.WITHER_IMMUNE).add(FTBICBlocks.REINFORCED_STONE.get(), FTBICBlocks.REINFORCED_GLASS.get());

			// Ore tags (Make them minable)
			Block[] resourceOres = FTBICBlocks.RESOURCE_ORES.values().stream().map(Supplier::get).toArray(Block[]::new);
			Block[] blockOfResources = FTBICBlocks.RESOURCE_BLOCKS_OF.values().stream().map(Supplier::get).toArray(Block[]::new);
			tag(BlockTags.MINEABLE_WITH_PICKAXE).add(resourceOres);
			tag(BlockTags.MINEABLE_WITH_PICKAXE).add(blockOfResources);
			tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
					FTBICBlocks.ADVANCED_MACHINE_BLOCK.get(),
					FTBICBlocks.MACHINE_BLOCK.get(),
					FTBICBlocks.REINFORCED_GLASS.get(),
					FTBICBlocks.REINFORCED_STONE.get(),
					FTBICBlocks.IRON_FURNACE.get(),
					FTBICBlocks.NUCLEAR_REACTOR_CHAMBER.get(),
					FTBICBlocks.RUBBER_SHEET.get(),
					FTBICBlocks.BURNT_CABLE.get(),
					FTBICBlocks.LANDMARK.get(),
					FTBICBlocks.EXFLUID.get(),
					FTBICBlocks.NUKE.get()
			);

			Block[] cables = FTBICBlocks.CABLES.stream().map(Supplier::get).toArray(Block[]::new);
			tag(BlockTags.MINEABLE_WITH_PICKAXE).add(cables);

			Block[] blocks = FTBICElectricBlocks.ALL.stream().map(e -> e.block.get()).toArray(Block[]::new);
			tag(BlockTags.MINEABLE_WITH_PICKAXE).add(blocks);

			// Register the tags
			FTBICBlocks.RESOURCE_ORES.forEach((k, v) -> {
				// Use the same tag name for deepslate &
				var name = k.getName().replace("deepslate_", "");
				tag(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("forge", "ores/" + name))).add(FTBICBlocks.RESOURCE_ORES.get(k).get());
			});

			tag(Tags.Blocks.ORES).add(resourceOres);
		}
	}


	private static class ICBiomeTags extends BiomeTagsProvider {
		public ICBiomeTags(DataGenerator generatorIn, String modId, ExistingFileHelper existingFileHelper) {
			super(generatorIn, modId, existingFileHelper);
		}

		@Override
		protected void addTags() {
			tag(dev.ftb.mods.ftbic.world.ICBiomeTags.ORE_SPAWN_BLACKLIST).addTags(
					BiomeTags.IS_END,
					BiomeTags.IS_NETHER
			);

		}
	}

	private static class FTBICItemTags extends ItemTagsProvider {
		public FTBICItemTags(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, String modId, ExistingFileHelper existingFileHelper) {
			super(dataGenerator, blockTagProvider, modId, existingFileHelper);
		}

		@Override
		protected void addTags() {
			tag(FTBICUtils.UNCANNABLE_FOOD).add(FTBICItems.CANNED_FOOD.get(), FTBICItems.PROTEIN_BAR.get());
			tag(ItemTags.ARROWS).add(FTBICItems.NUKE_ARROW.get());

			//INGOTS
			tag(TIN_INGOT).add(FTBICItems.getResourceFromType(TIN, INGOT).orElseThrow().get());
			tag(LEAD_INGOT).add(FTBICItems.getResourceFromType(LEAD, INGOT).orElseThrow().get());
			tag(URANIUM_INGOT).add(FTBICItems.getResourceFromType(URANIUM, INGOT).orElseThrow().get());
			tag(IRIDIUM_INGOT).add(FTBICItems.getResourceFromType(IRIDIUM, INGOT).orElseThrow().get());
			tag(ENDERIUM_INGOT).add(FTBICItems.getResourceFromType(ENDERIUM, INGOT).orElseThrow().get());
			tag(ALUMINUM_INGOT).add(FTBICItems.getResourceFromType(ALUMINUM, INGOT).orElseThrow().get());
			tag(BRONZE_INGOT).add(FTBICItems.getResourceFromType(BRONZE, INGOT).orElseThrow().get());

			tag(Tags.Items.INGOTS).addTag(TIN_INGOT);
			tag(Tags.Items.INGOTS).addTag(LEAD_INGOT);
			tag(Tags.Items.INGOTS).addTag(URANIUM_INGOT);
			tag(Tags.Items.INGOTS).addTag(IRIDIUM_INGOT);
			tag(Tags.Items.INGOTS).addTag(ENDERIUM_INGOT);
			tag(Tags.Items.INGOTS).addTag(ALUMINUM_INGOT);
			tag(Tags.Items.INGOTS).addTag(BRONZE_INGOT);

			//BLOCKS
			tag(TIN_BLOCK).add(FTBICItems.getResourceFromType(TIN, BLOCK).orElseThrow().get());
			tag(LEAD_BLOCK).add(FTBICItems.getResourceFromType(LEAD, BLOCK).orElseThrow().get());
			tag(URANIUM_BLOCK).add(FTBICItems.getResourceFromType(URANIUM, BLOCK).orElseThrow().get());
			tag(IRIDIUM_BLOCK).add(FTBICItems.getResourceFromType(IRIDIUM, BLOCK).orElseThrow().get());
			tag(ENDERIUM_BLOCK).add(FTBICItems.getResourceFromType(ENDERIUM, BLOCK).orElseThrow().get());
			tag(ALUMINUM_BLOCK).add(FTBICItems.getResourceFromType(ALUMINUM, BLOCK).orElseThrow().get());
			tag(BRONZE_BLOCK).add(FTBICItems.getResourceFromType(BRONZE, BLOCK).orElseThrow().get());

			tag(Tags.Items.STORAGE_BLOCKS).addTag(TIN_BLOCK);
			tag(Tags.Items.STORAGE_BLOCKS).addTag(LEAD_BLOCK);
			tag(Tags.Items.STORAGE_BLOCKS).addTag(URANIUM_BLOCK);
			tag(Tags.Items.STORAGE_BLOCKS).addTag(IRIDIUM_BLOCK);
			tag(Tags.Items.STORAGE_BLOCKS).addTag(ENDERIUM_BLOCK);
			tag(Tags.Items.STORAGE_BLOCKS).addTag(ALUMINUM_BLOCK);
			tag(Tags.Items.STORAGE_BLOCKS).addTag(BRONZE_BLOCK);

			//CHUNKS
			tag(TIN_CHUNK).add(FTBICItems.getResourceFromType(TIN, CHUNK).orElseThrow().get());
			tag(LEAD_CHUNK).add(FTBICItems.getResourceFromType(LEAD, CHUNK).orElseThrow().get());
			tag(URANIUM_CHUNK).add(FTBICItems.getResourceFromType(URANIUM, CHUNK).orElseThrow().get());
			tag(IRIDIUM_CHUNK).add(FTBICItems.getResourceFromType(IRIDIUM, CHUNK).orElseThrow().get());
			tag(ALUMINUM_CHUNK).add(FTBICItems.getResourceFromType(ALUMINUM, CHUNK).orElseThrow().get());

			tag(Tags.Items.RAW_MATERIALS).addTag(TIN_CHUNK);
			tag(Tags.Items.RAW_MATERIALS).addTag(LEAD_CHUNK);
			tag(Tags.Items.RAW_MATERIALS).addTag(URANIUM_CHUNK);
			tag(Tags.Items.RAW_MATERIALS).addTag(IRIDIUM_CHUNK);
			tag(Tags.Items.RAW_MATERIALS).addTag(ALUMINUM_CHUNK);

			//DUSTS
			tag(TIN_DUST).add(FTBICItems.getResourceFromType(TIN, DUST).orElseThrow().get());
			tag(LEAD_DUST).add(FTBICItems.getResourceFromType(LEAD, DUST).orElseThrow().get());
			tag(URANIUM_DUST).add(FTBICItems.getResourceFromType(URANIUM, DUST).orElseThrow().get());
			tag(IRIDIUM_DUST).add(FTBICItems.getResourceFromType(IRIDIUM, DUST).orElseThrow().get());
			tag(ENDERIUM_DUST).add(FTBICItems.getResourceFromType(ENDERIUM, DUST).orElseThrow().get());
			tag(ALUMINUM_DUST).add(FTBICItems.getResourceFromType(ALUMINUM, DUST).orElseThrow().get());
			tag(DIAMOND_DUST).add(FTBICItems.getResourceFromType(DIAMOND, DUST).orElseThrow().get());
			tag(IRON_DUST).add(FTBICItems.getResourceFromType(IRON, DUST).orElseThrow().get());
			tag(COPPER_DUST).add(FTBICItems.getResourceFromType(COPPER, DUST).orElseThrow().get());
			tag(GOLD_DUST).add(FTBICItems.getResourceFromType(GOLD, DUST).orElseThrow().get());
			tag(BRONZE_DUST).add(FTBICItems.getResourceFromType(BRONZE, DUST).orElseThrow().get());
			tag(OBSIDIAN_DUST).add(FTBICItems.getResourceFromType(OBSIDIAN, DUST).orElseThrow().get());
			tag(COAL_DUST).add(FTBICItems.getResourceFromType(COAL, DUST).orElseThrow().get());
			tag(CHARCOAL_DUST).add(FTBICItems.getResourceFromType(CHARCOAL, DUST).orElseThrow().get());
			tag(ENDER_DUST).add(FTBICItems.getResourceFromType(ENDER, DUST).orElseThrow().get());

			tag(Tags.Items.DUSTS).addTag(TIN_DUST);
			tag(Tags.Items.DUSTS).addTag(LEAD_DUST);
			tag(Tags.Items.DUSTS).addTag(URANIUM_DUST);
			tag(Tags.Items.DUSTS).addTag(IRIDIUM_DUST);
			tag(Tags.Items.DUSTS).addTag(ENDERIUM_DUST);
			tag(Tags.Items.DUSTS).addTag(ALUMINUM_DUST);
			tag(Tags.Items.DUSTS).addTag(DIAMOND_DUST);
			tag(Tags.Items.DUSTS).addTag(IRON_DUST);
			tag(Tags.Items.DUSTS).addTag(COPPER_DUST);
			tag(Tags.Items.DUSTS).addTag(GOLD_DUST);
			tag(Tags.Items.DUSTS).addTag(BRONZE_DUST);
			tag(Tags.Items.DUSTS).addTag(OBSIDIAN_DUST);
			tag(Tags.Items.DUSTS).addTag(COAL_DUST);
			tag(Tags.Items.DUSTS).addTag(CHARCOAL_DUST);
			tag(Tags.Items.DUSTS).addTag(ENDER_DUST);

			//PLATES
			tag(TIN_PLATE).add(FTBICItems.getResourceFromType(TIN, PLATE).orElseThrow().get());
			tag(LEAD_PLATE).add(FTBICItems.getResourceFromType(LEAD, PLATE).orElseThrow().get());
			tag(URANIUM_PLATE).add(FTBICItems.getResourceFromType(URANIUM, PLATE).orElseThrow().get());
			tag(IRIDIUM_PLATE).add(FTBICItems.getResourceFromType(IRIDIUM, PLATE).orElseThrow().get());
			tag(ENDERIUM_PLATE).add(FTBICItems.getResourceFromType(ENDERIUM, PLATE).orElseThrow().get());
			tag(ALUMINUM_PLATE).add(FTBICItems.getResourceFromType(ALUMINUM, PLATE).orElseThrow().get());
			tag(IRON_PLATE).add(FTBICItems.getResourceFromType(IRON, PLATE).orElseThrow().get());
			tag(GOLD_PLATE).add(FTBICItems.getResourceFromType(GOLD, PLATE).orElseThrow().get());
			tag(COPPER_PLATE).add(FTBICItems.getResourceFromType(COPPER, PLATE).orElseThrow().get());
			tag(BRONZE_PLATE).add(FTBICItems.getResourceFromType(BRONZE, PLATE).orElseThrow().get());

			tag(PLATES).addTag(TIN_PLATE);
			tag(PLATES).addTag(LEAD_PLATE);
			tag(PLATES).addTag(URANIUM_PLATE);
			tag(PLATES).addTag(IRIDIUM_PLATE);
			tag(PLATES).addTag(ENDERIUM_PLATE);
			tag(PLATES).addTag(ALUMINUM_PLATE);
			tag(PLATES).addTag(IRON_PLATE);
			tag(PLATES).addTag(GOLD_PLATE);
			tag(PLATES).addTag(COPPER_PLATE);
			tag(PLATES).addTag(BRONZE_PLATE);

			//NUGGETS
			tag(TIN_NUGGET).add(FTBICItems.getResourceFromType(TIN, NUGGET).orElseThrow().get());
			tag(LEAD_NUGGET).add(FTBICItems.getResourceFromType(LEAD, NUGGET).orElseThrow().get());
			tag(URANIUM_NUGGET).add(FTBICItems.getResourceFromType(URANIUM, NUGGET).orElseThrow().get());
			tag(IRIDIUM_NUGGET).add(FTBICItems.getResourceFromType(IRIDIUM, NUGGET).orElseThrow().get());
			tag(ENDERIUM_NUGGET).add(FTBICItems.getResourceFromType(ENDERIUM, NUGGET).orElseThrow().get());
			tag(ALUMINUM_NUGGET).add(FTBICItems.getResourceFromType(ALUMINUM, NUGGET).orElseThrow().get());
			tag(COPPER_NUGGET).add(FTBICItems.getResourceFromType(COPPER, NUGGET).orElseThrow().get());
			tag(BRONZE_NUGGET).add(FTBICItems.getResourceFromType(BRONZE, NUGGET).orElseThrow().get());


			tag(Tags.Items.NUGGETS).addTag(TIN_NUGGET);
			tag(Tags.Items.NUGGETS).addTag(LEAD_NUGGET);
			tag(Tags.Items.NUGGETS).addTag(URANIUM_NUGGET);
			tag(Tags.Items.NUGGETS).addTag(IRIDIUM_NUGGET);
			tag(Tags.Items.NUGGETS).addTag(ENDERIUM_NUGGET);
			tag(Tags.Items.NUGGETS).addTag(ALUMINUM_NUGGET);
			tag(Tags.Items.NUGGETS).addTag(COPPER_NUGGET);
			tag(Tags.Items.NUGGETS).addTag(BRONZE_NUGGET);

			//RODS
			tag(TIN_ROD).add(FTBICItems.getResourceFromType(TIN, ROD).orElseThrow().get());
			tag(LEAD_ROD).add(FTBICItems.getResourceFromType(LEAD, ROD).orElseThrow().get());
			tag(URANIUM_ROD).add(FTBICItems.getResourceFromType(URANIUM, ROD).orElseThrow().get());
			tag(IRIDIUM_ROD).add(FTBICItems.getResourceFromType(IRIDIUM, ROD).orElseThrow().get());
			tag(ENDERIUM_ROD).add(FTBICItems.getResourceFromType(ENDERIUM, ROD).orElseThrow().get());
			tag(ALUMINUM_ROD).add(FTBICItems.getResourceFromType(ALUMINUM, ROD).orElseThrow().get());
			tag(IRON_ROD).add(FTBICItems.getResourceFromType(IRON, ROD).orElseThrow().get());
			tag(COPPER_ROD).add(FTBICItems.getResourceFromType(COPPER, ROD).orElseThrow().get());
			tag(GOLD_ROD).add(FTBICItems.getResourceFromType(GOLD, ROD).orElseThrow().get());
			tag(BRONZE_ROD).add(FTBICItems.getResourceFromType(BRONZE, ROD).orElseThrow().get());


			tag(RODS).addTag(TIN_ROD);
			tag(RODS).addTag(LEAD_ROD);
			tag(RODS).addTag(URANIUM_ROD);
			tag(RODS).addTag(IRIDIUM_ROD);
			tag(RODS).addTag(ENDERIUM_ROD);
			tag(RODS).addTag(ALUMINUM_ROD);
			tag(RODS).addTag(IRON_ROD);
			tag(RODS).addTag(COPPER_ROD);
			tag(RODS).addTag(GOLD_ROD);
			tag(RODS).addTag(BRONZE_ROD);


			//GEARS
			tag(TIN_GEAR).add(FTBICItems.getResourceFromType(TIN, GEAR).orElseThrow().get());
			tag(LEAD_GEAR).add(FTBICItems.getResourceFromType(LEAD, GEAR).orElseThrow().get());
			tag(URANIUM_GEAR).add(FTBICItems.getResourceFromType(URANIUM, GEAR).orElseThrow().get());
			tag(IRIDIUM_GEAR).add(FTBICItems.getResourceFromType(IRIDIUM, GEAR).orElseThrow().get());
			tag(ENDERIUM_GEAR).add(FTBICItems.getResourceFromType(ENDERIUM, GEAR).orElseThrow().get());
			tag(ALUMINUM_GEAR).add(FTBICItems.getResourceFromType(ALUMINUM, GEAR).orElseThrow().get());
			tag(IRON_GEAR).add(FTBICItems.getResourceFromType(IRON, GEAR).orElseThrow().get());
			tag(GOLD_GEAR).add(FTBICItems.getResourceFromType(GOLD, GEAR).orElseThrow().get());
			tag(COPPER_GEAR).add(FTBICItems.getResourceFromType(COPPER, GEAR).orElseThrow().get());
			tag(BRONZE_GEAR).add(FTBICItems.getResourceFromType(BRONZE, GEAR).orElseThrow().get());

			tag(GEARS).addTag(TIN_GEAR);
			tag(GEARS).addTag(LEAD_GEAR);
			tag(GEARS).addTag(URANIUM_GEAR);
			tag(GEARS).addTag(IRIDIUM_GEAR);
			tag(GEARS).addTag(ENDERIUM_GEAR);
			tag(GEARS).addTag(ALUMINUM_GEAR);
			tag(GEARS).addTag(IRON_GEAR);
			tag(GEARS).addTag(GOLD_GEAR);
			tag(GEARS).addTag(COPPER_GEAR);
			tag(GEARS).addTag(BRONZE_GEAR);


			//ORES
			tag(TIN_ORE).add(FTBICItems.getResourceFromType(TIN, ORE).orElseThrow().get());
			tag(TIN_ORE).add(FTBICItems.getResourceFromType(DEEPSLATE_TIN, ORE).orElseThrow().get());
			tag(LEAD_ORE).add(FTBICItems.getResourceFromType(LEAD, ORE).orElseThrow().get());
			tag(LEAD_ORE).add(FTBICItems.getResourceFromType(DEEPSLATE_LEAD, ORE).orElseThrow().get());
			tag(URANIUM_ORE).add(FTBICItems.getResourceFromType(URANIUM, ORE).orElseThrow().get());
			tag(URANIUM_ORE).add(FTBICItems.getResourceFromType(DEEPSLATE_URANIUM, ORE).orElseThrow().get());
			tag(IRIDIUM_ORE).add(FTBICItems.getResourceFromType(IRIDIUM, ORE).orElseThrow().get());
			tag(IRIDIUM_ORE).add(FTBICItems.getResourceFromType(DEEPSLATE_IRIDIUM, ORE).orElseThrow().get());
			tag(ALUMINUM_ORE).add(FTBICItems.getResourceFromType(ALUMINUM, ORE).orElseThrow().get());
			tag(ALUMINUM_ORE).add(FTBICItems.getResourceFromType(DEEPSLATE_ALUMINUM, ORE).orElseThrow().get());

			tag(Tags.Items.ORES).addTag(TIN_ORE);
			tag(Tags.Items.ORES).addTag(LEAD_ORE);
			tag(Tags.Items.ORES).addTag(URANIUM_ORE);
			tag(Tags.Items.ORES).addTag(IRIDIUM_ORE);
			tag(Tags.Items.ORES).addTag(ALUMINUM_ORE);

			tag(SILICON).add(SILICON_ITEM.get());
		}
	}

	private static class FTBICLootTableProvider extends LootTableProvider {
		private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> lootTables = Lists.newArrayList(Pair.of(FTBICBlockLootTableProvider::new, LootContextParamSets.BLOCK));

		public FTBICLootTableProvider(DataGenerator dataGeneratorIn) {
			super(dataGeneratorIn);
		}

		@Override
		protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
		}

		@Override
		protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
			return lootTables;
		}
	}

	public static class FTBICBlockLootTableProvider extends BlockLoot {
		private final Map<ResourceLocation, LootTable.Builder> tables = Maps.newHashMap();

		@Override
		protected void addTables() {
			dropSelf(FTBICBlocks.RUBBER_SHEET.get());
			dropSelf(FTBICBlocks.REINFORCED_STONE.get());
			dropSelf(FTBICBlocks.REINFORCED_GLASS.get());
			dropSelf(FTBICBlocks.MACHINE_BLOCK.get());
			dropSelf(FTBICBlocks.ADVANCED_MACHINE_BLOCK.get());
			dropSelf(FTBICBlocks.IRON_FURNACE.get());

			for (Supplier<Block> cable : FTBICBlocks.CABLES) {
				dropSelf(cable.get());
			}

			dropSelf(FTBICBlocks.LANDMARK.get());
			dropSelf(FTBICBlocks.NUCLEAR_REACTOR_CHAMBER.get());
			dropSelf(FTBICBlocks.NUKE.get());

			FTBICBlocks.RESOURCE_BLOCKS_OF.forEach((k, v) -> dropSelf(v.get()));

			// Ore drops
			FTBICBlocks.RESOURCE_ORES.forEach((k, v) -> {
				ResourceElements.getNonDeepslateVersion(k).ifPresent(e -> {
					if (e.requirements().has(CHUNK)) {
						add(v.get(), createOreDrop(v.get(), FTBICItems.getResourceFromType(e, CHUNK).orElseThrow().get()));
					}
				});
			});

			for (ElectricBlockInstance machine : FTBICElectricBlocks.ALL) {
				if (machine.canBurn) {
					add(machine.block.get(), LootTable.lootTable()
							.withPool(LootPool.lootPool()
									.when(new BurntBlockCondition.Builder().invert())
									.setRolls(ConstantValue.exactly(1))
									.add(LootItem.lootTableItem(machine.item.get()))
							)
							.withPool(LootPool.lootPool()
									.when(new BurntBlockCondition.Builder())
									.setRolls(ConstantValue.exactly(1))
									.add(LootItem.lootTableItem(machine.advanced ? FTBICItems.ADVANCED_MACHINE_BLOCK.get() : FTBICItems.MACHINE_BLOCK.get()))
							)
					);
				} else {
					dropSelf(machine.block.get());
				}
			}
		}

		@Override
		public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
			addTables();

			for (ResourceLocation rs : new ArrayList<>(tables.keySet())) {
				if (rs != BuiltInLootTables.EMPTY) {
					LootTable.Builder builder = tables.remove(rs);

					if (builder == null) {
						throw new IllegalStateException(String.format("Missing loottable '%s'", rs));
					}

					consumer.accept(rs, builder);
				}
			}

			if (!tables.isEmpty()) {
				throw new IllegalStateException("Created block loot tables for non-blocks: " + tables.keySet());
			}
		}

		@Override
		protected void add(Block blockIn, LootTable.Builder table) {
			tables.put(blockIn.getLootTable(), table);
		}
	}
}
