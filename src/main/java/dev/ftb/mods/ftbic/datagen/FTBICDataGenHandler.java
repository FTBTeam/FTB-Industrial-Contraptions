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
import dev.ftb.mods.ftbic.world.ResourceElements;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
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
import net.minecraftforge.client.model.generators.loaders.MultiLayerModelBuilder;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
			gen.addProvider(new FTBICLang(gen, MODID, "en_us"));
			gen.addProvider(new FTBICTextures(gen, MODID, event.getExistingFileHelper()));
			gen.addProvider(new FTBICBlockModels(gen, MODID, event.getExistingFileHelper()));
			gen.addProvider(new FTBICBlockStates(gen, MODID, event.getExistingFileHelper()));
			gen.addProvider(new FTBICItemModels(gen, MODID, event.getExistingFileHelper()));
		}

		if (event.includeServer()) {
			ICBlockTags blockTags = new ICBlockTags(gen, MODID, efh);
			gen.addProvider(blockTags);
			gen.addProvider(new FTBICItemTags(gen, blockTags, MODID, efh));
			gen.addProvider(new FTBICComponentRecipes(gen));
			gen.addProvider(new FTBICUpgradeRecipes(gen));
			gen.addProvider(new FTBICCableRecipes(gen));
			gen.addProvider(new FTBICBatteryRecipes(gen));
			gen.addProvider(new FTBICGeneratorRecipes(gen));
			gen.addProvider(new FTBICMachineRecipes(gen));
			gen.addProvider(new FTBICEnergyStorageRecipes(gen));
			gen.addProvider(new FTBICToolRecipes(gen));
			gen.addProvider(new FTBICArmorRecipes(gen));
			gen.addProvider(new FTBICNuclearRecipes(gen));
			gen.addProvider(new FTBICGeneratorFuelRecipes(gen));
			gen.addProvider(new FTBICVanillaRecipes(gen));
			gen.addProvider(new FTBICLootTableProvider(gen));
		}
	}

	private static class FTBICLang extends LanguageProvider {
		public FTBICLang(DataGenerator gen, String modid, String locale) {
			super(gen, modid, locale);
		}

		private void addBlock(Supplier<Block> block) {
			addBlockWithSuffix(block, "");
		}

		private void addBlockWithSuffix(Supplier<Block> block, String suffix) {
			addBlock(block, Arrays.stream(block.get().getRegistryName().getPath().split("_")).map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1)).collect(Collectors.joining(" ")) + suffix);
		}

		private void addItemWithSuffix(Supplier<Item> item, String suffix) {
			addItem(item, Arrays.stream(item.get().getRegistryName().getPath().split("_")).map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1)).collect(Collectors.joining(" ")));
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

			FTBICBlocks.RESOURCES.values()
					.forEach(this::addBlock);

			FTBICItems.RESOURCES_CHUNKS.forEach((k, v) -> {
				if (!k.hasChunk()) return;
				addItemWithSuffix(v, " Chunk");
			});

			FTBICItems.RESOURCES_INGOTS.values().forEach(e -> addItemWithSuffix(e, " Ingot"));
			FTBICItems.RESOURCES_DUSTS.values().forEach(e -> addItemWithSuffix(e, " Dust"));
			addItemWithSuffix(FTBICItems.DIAMOND_DUST, " Dust");
			FTBICItems.RESOURCES_NUGGETS.values().forEach(e -> addItemWithSuffix(e, " Nugget"));
			FTBICItems.RESOURCES_PLATES.values().forEach(e -> addItemWithSuffix(e, " Plate"));
			FTBICItems.RESOURCES_RODS.values().forEach(e -> addItemWithSuffix(e, " Rod"));
			FTBICItems.RESOURCES_GEARS.values().forEach(e -> addItemWithSuffix(e, " Gear"));

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
			add("ftbic.fuse_info", "Right-click with a Fuse on this machine to repair it!");
			add("ftbic.any_item", "Any Item");
			add("ftbic.requires_chestplate", "Requires Chestplate to function");
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

			// Ores (Taken from EmendatusEnigmatica, thanks guys!)
			FTBICBlocks.RESOURCES.forEach((key, value) -> {
				ResourceLocation registryName = value.get().getRegistryName();
				String overlayTexture = "block/ore_overlays/" + key.getName().replace("deepslate_", ""); // no deepslate textures

				models().getBuilder(registryName.getPath())
						.parent(new ModelFile.UncheckedModelFile(mcLoc("block/block")))
						.texture("particle", modLoc(overlayTexture))
						.transforms()
						.transform(ModelBuilder.Perspective.THIRDPERSON_LEFT)
						.rotation(75F, 45F, 0F)
						.translation(0F, 2.5F, 0)
						.scale(0.375F, 0.375F, 0.375F)
						.end()
						.transform(ModelBuilder.Perspective.THIRDPERSON_RIGHT)
						.rotation(75F, 45F, 0F)
						.translation(0F, 2.5F, 0)
						.scale(0.375F, 0.375F, 0.375F)
						.end()
						.end()
						.customLoader(MultiLayerModelBuilder::begin)
						.submodel(RenderType.solid(), this.models().nested().parent(this.models().getExistingFile(key.getName().contains("deepslate") ? mcLoc("block/deepslate") : mcLoc("block/stone"))))
						.submodel(RenderType.translucent(), this.models().nested().parent(this.models().getExistingFile(mcLoc("block/cube_all"))).texture("all", modLoc(overlayTexture))						)
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

			singleTexture("landmark", mcLoc("item/generated"), "layer0", modLoc("block/landmark_ns"));
			withExistingParent(FTBICBlocks.EXFLUID.get().getRegistryName().getPath(), mcLoc("block/dead_horn_coral_block"));
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

			FTBICBlocks.RESOURCES.values().forEach(this::basicBlockItem);
			FTBICItems.RESOURCES_CHUNKS.forEach((k, v) -> {
				if (!k.hasChunk()) return;
				basicItem(v);
			});
			FTBICItems.RESOURCES_INGOTS.values().forEach(this::basicItem);
			FTBICItems.RESOURCES_DUSTS.values().forEach(this::basicItem);
			basicItem(FTBICItems.DIAMOND_DUST);
			FTBICItems.RESOURCES_NUGGETS.values().forEach(this::basicItem);
			FTBICItems.RESOURCES_PLATES.values().forEach(this::basicItem);
			FTBICItems.RESOURCES_RODS.values().forEach(this::basicItem);
			FTBICItems.RESOURCES_GEARS.values().forEach(this::basicItem);
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

			// Ore tags
			Block[] resourceOres = FTBICBlocks.RESOURCES.values().stream().map(Supplier::get).toArray(Block[]::new);
			tag(BlockTags.MINEABLE_WITH_PICKAXE).add(resourceOres);
			tag(Tags.Blocks.ORES).add(resourceOres);
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
