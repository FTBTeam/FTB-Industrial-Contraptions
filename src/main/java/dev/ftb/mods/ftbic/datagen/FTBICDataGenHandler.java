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
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.item.MaterialItem;
import dev.ftb.mods.ftbic.util.BurntBlockCondition;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.ConstantIntValue;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeLootTableProvider;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
			gen.addProvider(new ICLang(gen, MODID, "en_us"));
			gen.addProvider(new ICTextures(gen, MODID, event.getExistingFileHelper()));
			gen.addProvider(new ICBlockModels(gen, MODID, event.getExistingFileHelper()));
			gen.addProvider(new ICBlockStates(gen, MODID, event.getExistingFileHelper()));
			gen.addProvider(new ICItemModels(gen, MODID, event.getExistingFileHelper()));
		}

		if (event.includeServer()) {
			ICBlockTags blockTags = new ICBlockTags(gen, MODID, efh);
			gen.addProvider(blockTags);
			gen.addProvider(new ICItemTags(gen, blockTags, MODID, efh));
			gen.addProvider(new FTBICComponentRecipes(gen));
			gen.addProvider(new FTBICUpgradeRecipes(gen));
			gen.addProvider(new FTBICCableRecipes(gen));
			gen.addProvider(new FTBICBatteryRecipes(gen));
			gen.addProvider(new FTBICGeneratorRecipes(gen));
			gen.addProvider(new FTBICMachineRecipes(gen));
			gen.addProvider(new FTBICEnergyStorageRecipes(gen));
			gen.addProvider(new FTBICToolRecipes(gen));
			gen.addProvider(new FTBICNuclearRecipes(gen));
			gen.addProvider(new FTBICGeneratorFuelRecipes(gen));
			gen.addProvider(new FTBICVanillaRecipes(gen));
			gen.addProvider(new FTBICAntimatterRecipes(gen));
			gen.addProvider(new ICLootTableProvider(gen));
		}
	}

	private static class ICLang extends LanguageProvider {
		public ICLang(DataGenerator gen, String modid, String locale) {
			super(gen, modid, locale);
		}

		@Override
		protected void addTranslations() {
			add("itemGroup.ftbic", "FTB Industrial Contraptions");

			addBlock(FTBICBlocks.RUBBER_SHEET, "Rubber Sheet");
			addBlock(FTBICBlocks.REINFORCED_STONE, "Reinforced Stone");
			addBlock(FTBICBlocks.REINFORCED_GLASS, "Reinforced Glass");
			addBlock(FTBICBlocks.MACHINE_BLOCK, "Machine Block");
			addBlock(FTBICBlocks.ADVANCED_MACHINE_BLOCK, "Advanced Machine Block");
			addBlock(FTBICBlocks.IRON_FURNACE, "Iron Furnace");
			addBlock(FTBICBlocks.LV_CABLE, "LV Cable");
			addBlock(FTBICBlocks.MV_CABLE, "MV Cable");
			addBlock(FTBICBlocks.HV_CABLE, "HV Cable");
			addBlock(FTBICBlocks.EV_CABLE, "EV Cable");
			addBlock(FTBICBlocks.IV_CABLE, "IV Cable");
			addBlock(FTBICBlocks.BURNT_CABLE, "Burnt Cable");

			for (ElectricBlockInstance machine : FTBICElectricBlocks.ALL) {
				addBlock(machine.block, machine.name);
			}

			for (MaterialItem item : FTBICItems.MATERIALS) {
				addItem(item.item, item.name);
			}

			addItem(FTBICItems.SINGLE_USE_BATTERY, "Single Use Battery");
			addItem(FTBICItems.BATTERY, "Battery");
			addItem(FTBICItems.CRYSTAL_BATTERY, "Crystal Battery");
			addItem(FTBICItems.GRAPHENE_BATTERY, "Graphene Battery");
			addItem(FTBICItems.IRIDIUM_BATTERY, "Iridium Battery");
			addItem(FTBICItems.CREATIVE_BATTERY, "Creative Battery");
			addItem(FTBICItems.FLUID_CELL, "Fluid Cell");
			addItem(FTBICItems.COOLANT_10K, "10k Coolant Cell");
			addItem(FTBICItems.COOLANT_30K, "30k Coolant Cell");
			addItem(FTBICItems.COOLANT_60K, "60k Coolant Cell");
			addItem(FTBICItems.URANIUM_FUEL_ROD, "Uranium Fuel Cell");
			addItem(FTBICItems.DUAL_URANIUM_FUEL_ROD, "Dual Uranium Fuel Cell");
			addItem(FTBICItems.QUAD_URANIUM_FUEL_ROD, "Quad Uranium Fuel Cell");
			addItem(FTBICItems.CANNED_FOOD, "Canned Food");
			addItem(FTBICItems.PROTEIN_BAR, "Feed The Beast™ Protein Bar");
			addItem(FTBICItems.DARK_SPRAY_PAINT_CAN, "Spray Paint Can (Dark)");
			addItem(FTBICItems.LIGHT_SPRAY_PAINT_CAN, "Spray Paint Can (Light)");
			addItem(FTBICItems.OVERCLOCKER_UPGRADE, "Overclocker Upgrade");
			addItem(FTBICItems.ENERGY_STORAGE_UPGRADE, "Energy Storage Upgrade");
			addItem(FTBICItems.TRANSFORMER_UPGRADE, "Transformer Upgrade");
			addItem(FTBICItems.EJECTOR_UPGRADE, "Ejector Upgrade");
			addItem(FTBICItems.CARBON_HELMET, "Carbon Helmet");
			addItem(FTBICItems.CARBON_CHESTPLATE, "Carbon Chestplate");
			addItem(FTBICItems.CARBON_LEGGINGS, "Carbon Leggings");
			addItem(FTBICItems.CARBON_BOOTS, "Carbon Boots");
			addItem(FTBICItems.QUANTUM_HELMET, "Quantum Helmet");
			addItem(FTBICItems.QUANTUM_CHESTPLATE, "Quantum Chestplate");
			addItem(FTBICItems.QUANTUM_LEGGINGS, "Quantum Leggings");
			addItem(FTBICItems.QUANTUM_BOOTS, "Quantum Boots");

			add("recipe." + FTBIC.MOD_ID + ".macerating", "Macerating");
			add("recipe." + FTBIC.MOD_ID + ".separating", "Separating");
			add("recipe." + FTBIC.MOD_ID + ".compressing", "Compressing");
			add("recipe." + FTBIC.MOD_ID + ".reprocessing", "Reprocessing");
			add("recipe." + FTBIC.MOD_ID + ".canning", "Canning");
			add("recipe." + FTBIC.MOD_ID + ".rolling", "Rolling");
			add("recipe." + FTBIC.MOD_ID + ".extruding", "Extruding");
			add("recipe." + FTBIC.MOD_ID + ".reconstructing", "Reconstructing");

			add("ftbic.energy_output", "Energy Output: %s");
			add("ftbic.max_energy_output", "Max Energy Output: %s");
			add("ftbic.energy_usage", "Energy Usage: %s");
			add("ftbic.max_input", "Max Input: %s");
			add("ftbic.fuse_info", "Right-click with a Fuse on this machine to repair it!");
			add("ftbic.any_item", "Any Item");
		}
	}

	private static class ICTextures extends CombinedTextureProvider {
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

		public ICTextures(DataGenerator g, String mod, ExistingFileHelper efh) {
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

	private static class ICBlockModels extends BlockModelProvider {
		public static final String BASIC_TOP = "basic_top";
		public static final String BASIC_BOTTOM = "basic_bottom";
		public static final String BASIC_SIDE = "basic_side";
		public static final String ADVANCED_TOP = "advanced_top";
		public static final String ADVANCED_BOTTOM = "advanced_bottom";
		public static final String ADVANCED_SIDE = "advanced_side";

		public ICBlockModels(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
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
					.transform(ModelBuilder.Perspective.FIRSTPERSON_RIGHT)
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
					.transform(ModelBuilder.Perspective.FIRSTPERSON_RIGHT)
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
				String id = block.getRegistryName().getPath();

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

			electric("basic_generator_off", "basic_generator_front_off", BASIC_SIDE, BASIC_TOP, BASIC_BOTTOM);
			electric("basic_generator_on", "basic_generator_front_on", BASIC_SIDE, BASIC_TOP, BASIC_BOTTOM);
			electric("geothermal_generator_off", "geothermal_generator_front_off", BASIC_SIDE, BASIC_TOP, BASIC_BOTTOM);
			electric("geothermal_generator_on", "geothermal_generator_front_on", BASIC_SIDE, BASIC_TOP, BASIC_BOTTOM);
			electric("wind_mill", "wind_mill_front", BASIC_SIDE, BASIC_TOP, BASIC_BOTTOM);
			electric("lv_solar_panel", BASIC_SIDE, BASIC_SIDE, "lv_solar_panel_top", BASIC_BOTTOM);
			electric("mv_solar_panel", BASIC_SIDE, BASIC_SIDE, "mv_solar_panel_top", BASIC_BOTTOM);
			electric("hv_solar_panel", ADVANCED_SIDE, ADVANCED_SIDE, "hv_solar_panel_top", ADVANCED_BOTTOM);
			electric("ev_solar_panel", ADVANCED_SIDE, ADVANCED_SIDE, "ev_solar_panel_top", ADVANCED_BOTTOM);
			electric("nuclear_reactor_off", "nuclear_reactor_front_off", ADVANCED_SIDE, ADVANCED_TOP, ADVANCED_BOTTOM);
			electric("nuclear_reactor_on", "nuclear_reactor_front_on", ADVANCED_SIDE, ADVANCED_TOP, ADVANCED_BOTTOM);

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
			electric("charge_pad_off", ADVANCED_SIDE, BASIC_SIDE, "charge_pad_top_off", BASIC_BOTTOM);
			electric("charge_pad_on", ADVANCED_SIDE, BASIC_SIDE, "charge_pad_top_on", BASIC_BOTTOM);

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

	private static class ICBlockStates extends BlockStateProvider {
		public ICBlockStates(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
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
				String id = block.getRegistryName().getPath();
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

			for (ElectricBlockInstance machine : FTBICElectricBlocks.ALL) {
				if (!machine.noModel) {
					List<Property<?>> ignoredProperties = new ArrayList<>();

					// if (machine.stateProperty == ElectricBlockState.OFF_BURNT) {
					// 	ignoredProperties.add(ElectricBlockState.OFF_BURNT);
					// }

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
					}, ignoredProperties.toArray(new Property[0]));
				}
			}
		}
	}

	private static class ICItemModels extends ItemModelProvider {
		public ICItemModels(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
			super(generator, modid, existingFileHelper);
		}

		private void basicItem(Supplier<? extends Item> item) {
			String id = item.get().getRegistryName().getPath();
			singleTexture(id, mcLoc("item/generated"), "layer0", modLoc("item/" + id));
		}

		private void handheldItem(Supplier<? extends Item> item) {
			String id = item.get().getRegistryName().getPath();
			singleTexture(id, mcLoc("item/handheld"), "layer0", modLoc("item/" + id));
		}

		private void basicBlockItem(Supplier<Block> block) {
			String id = block.get().getRegistryName().getPath();
			withExistingParent(id, modLoc("block/" + id));
		}

		@Override
		protected void registerModels() {
			basicBlockItem(FTBICBlocks.RUBBER_SHEET);
			basicBlockItem(FTBICBlocks.REINFORCED_STONE);
			basicBlockItem(FTBICBlocks.REINFORCED_GLASS);
			basicBlockItem(FTBICBlocks.MACHINE_BLOCK);
			basicBlockItem(FTBICBlocks.ADVANCED_MACHINE_BLOCK);
			withExistingParent(FTBICBlocks.IRON_FURNACE.get().getRegistryName().getPath(), modLoc("block/iron_furnace_off"));

			for (Supplier<Block> cable : FTBICBlocks.CABLES) {
				String id = cable.get().getRegistryName().getPath();
				int b0 = ((BaseCableBlock) cable.get()).border;
				int b1 = 16 - b0;

				withExistingParent("item/" + id, "block/block")
						.texture("particle", modLoc("block/" + id))
						.texture("cable", modLoc("block/" + id))
						.transforms()
						.transform(ModelBuilder.Perspective.THIRDPERSON_RIGHT)
						.rotation(75F, 45F, 0F)
						.translation(0F, 1.5F, 0F)
						.scale(0.375F, 0.375F, 0.375F)
						.end()
						.transform(ModelBuilder.Perspective.THIRDPERSON_LEFT)
						.rotation(75F, 45F, 0F)
						.translation(0F, 1.5F, 0F)
						.scale(0.375F, 0.375F, 0.375F)
						.end()
						.transform(ModelBuilder.Perspective.FIRSTPERSON_RIGHT)
						.rotation(0F, 45F, 0F)
						.scale(0.4F, 0.4F, 0.4F)
						.end()
						.transform(ModelBuilder.Perspective.FIRSTPERSON_LEFT)
						.rotation(0F, 225F, 0F)
						.scale(0.4F, 0.4F, 0.4F)
						.end()
						.transform(ModelBuilder.Perspective.GROUND)
						.translation(0F, 3F, 0F)
						.scale(0.25F, 0.25F, 0.25F)
						.end()
						.transform(ModelBuilder.Perspective.GUI)
						.rotation(45F, 45F, 90F)
						.translation(1.75F, 1F, 0F)
						.scale(0.625F, 0.625F, 0.625F)
						.end()
						.transform(ModelBuilder.Perspective.FIXED)
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

			for (ElectricBlockInstance machine : FTBICElectricBlocks.ALL) {
				if (!machine.noModel) {
					withExistingParent(machine.id, modLoc("block/electric/light/" + machine.id + (machine.canBeActive ? "_off" : "")));
				}
			}

			for (MaterialItem item : FTBICItems.MATERIALS) {
				basicItem(item.item);
			}

			basicItem(FTBICItems.SINGLE_USE_BATTERY);
			basicItem(FTBICItems.BATTERY);
			basicItem(FTBICItems.CRYSTAL_BATTERY);
			basicItem(FTBICItems.GRAPHENE_BATTERY);
			basicItem(FTBICItems.IRIDIUM_BATTERY);
			basicItem(FTBICItems.CREATIVE_BATTERY);
			basicItem(FTBICItems.COOLANT_10K);
			basicItem(FTBICItems.COOLANT_30K);
			basicItem(FTBICItems.COOLANT_60K);
			basicItem(FTBICItems.URANIUM_FUEL_ROD);
			basicItem(FTBICItems.DUAL_URANIUM_FUEL_ROD);
			basicItem(FTBICItems.QUAD_URANIUM_FUEL_ROD);
			basicItem(FTBICItems.CANNED_FOOD);
			basicItem(FTBICItems.PROTEIN_BAR);
			basicItem(FTBICItems.DARK_SPRAY_PAINT_CAN);
			basicItem(FTBICItems.LIGHT_SPRAY_PAINT_CAN);
			basicItem(FTBICItems.OVERCLOCKER_UPGRADE);
			basicItem(FTBICItems.ENERGY_STORAGE_UPGRADE);
			basicItem(FTBICItems.TRANSFORMER_UPGRADE);
			basicItem(FTBICItems.EJECTOR_UPGRADE);
			basicItem(FTBICItems.CARBON_HELMET);
			basicItem(FTBICItems.CARBON_CHESTPLATE);
			basicItem(FTBICItems.CARBON_LEGGINGS);
			basicItem(FTBICItems.CARBON_BOOTS);
			basicItem(FTBICItems.QUANTUM_HELMET);
			basicItem(FTBICItems.QUANTUM_CHESTPLATE);
			basicItem(FTBICItems.QUANTUM_LEGGINGS);
			basicItem(FTBICItems.QUANTUM_BOOTS);
		}
	}

	private static class ICBlockTags extends BlockTagsProvider {
		public ICBlockTags(DataGenerator generatorIn, String modId, ExistingFileHelper existingFileHelper) {
			super(generatorIn, modId, existingFileHelper);
		}

		@Override
		protected void addTags() {
			tag(BlockTags.bind(MODID + ":reinforced")).add(FTBICBlocks.REINFORCED_STONE.get(), FTBICBlocks.REINFORCED_GLASS.get());
		}
	}

	private static class ICItemTags extends ItemTagsProvider {
		public ICItemTags(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, String modId, ExistingFileHelper existingFileHelper) {
			super(dataGenerator, blockTagProvider, modId, existingFileHelper);
		}

		@Override
		protected void addTags() {
			tag(ItemTags.bind(MODID + ":reinforced")).add(FTBICItems.REINFORCED_STONE.get(), FTBICItems.REINFORCED_GLASS.get());
			tag(FTBICUtils.UNCANNABLE_FOOD).add(FTBICItems.CANNED_FOOD.get(), FTBICItems.PROTEIN_BAR.get());
		}
	}

	private static class ICLootTableProvider extends ForgeLootTableProvider {
		private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> lootTables = Lists.newArrayList(Pair.of(ICBlockLootTableProvider::new, LootContextParamSets.BLOCK));

		public ICLootTableProvider(DataGenerator dataGeneratorIn) {
			super(dataGeneratorIn);
		}

		@Override
		protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
			return lootTables;
		}
	}

	public static class ICBlockLootTableProvider extends BlockLoot {
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

			for (ElectricBlockInstance machine : FTBICElectricBlocks.ALL) {
				if (machine.canBurn) {
					add(machine.block.get(), LootTable.lootTable()
							.withPool(LootPool.lootPool()
									.when(new BurntBlockCondition.Builder().invert())
									.setRolls(ConstantIntValue.exactly(1))
									.add(LootItem.lootTableItem(machine.item.get()))
							)
							.withPool(LootPool.lootPool()
									.when(new BurntBlockCondition.Builder())
									.setRolls(ConstantIntValue.exactly(1))
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
