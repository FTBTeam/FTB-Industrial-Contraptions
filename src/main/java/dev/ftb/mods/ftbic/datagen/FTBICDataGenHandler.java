package dev.ftb.mods.ftbic.datagen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.CableBlock;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.block.ElectricBlockState;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.SprayPaintable;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.item.MaterialItem;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
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
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
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
			gen.addProvider(new FTBICCircuitRecipes(gen));
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
			addBlock(FTBICBlocks.COPPER_WIRE, "Copper Wire");
			addBlock(FTBICBlocks.COPPER_CABLE, "Copper Cable");
			addBlock(FTBICBlocks.GOLD_WIRE, "Gold Wire");
			addBlock(FTBICBlocks.GOLD_CABLE, "Gold Cable");
			addBlock(FTBICBlocks.ALUMINUM_WIRE, "Aluminum Wire");
			addBlock(FTBICBlocks.ALUMINUM_CABLE, "Aluminum Cable");
			addBlock(FTBICBlocks.GLASS_CABLE, "Glass Cable");

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
			addItem(FTBICItems.EMPTY_CELL, "Empty Cell");
			addItem(FTBICItems.WATER_CELL, "Water Cell");
			addItem(FTBICItems.LAVA_CELL, "Lava Cell");
			addItem(FTBICItems.COOLANT_10K, "10k Coolant Cell");
			addItem(FTBICItems.COOLANT_30K, "30k Coolant Cell");
			addItem(FTBICItems.COOLANT_60K, "60k Coolant Cell");
			addItem(FTBICItems.URANIUM_FUEL_ROD, "Uranium Fuel Cell");
			addItem(FTBICItems.DUAL_URANIUM_FUEL_ROD, "Dual Uranium Fuel Cell");
			addItem(FTBICItems.QUAD_URANIUM_FUEL_ROD, "Quad Uranium Fuel Cell");
			addItem(FTBICItems.CANNED_FOOD, "Canned Food");
			addItem(FTBICItems.DARK_SPRAY_PAINT_CAN, "Spray Paint Can (Dark)");
			addItem(FTBICItems.LIGHT_SPRAY_PAINT_CAN, "Spray Paint Can (Light)");

			add("recipe." + FTBIC.MOD_ID + ".macerating", "Macerating");
			add("recipe." + FTBIC.MOD_ID + ".extracting", "Extracting");
			add("recipe." + FTBIC.MOD_ID + ".compressing", "Compressing");
			add("recipe." + FTBIC.MOD_ID + ".electrolyzing", "Electrolyzing");
			add("recipe." + FTBIC.MOD_ID + ".recycling", "Recycling");
			add("recipe." + FTBIC.MOD_ID + ".canning", "Canning");
		}
	}

	private static class ICTextures extends CombinedTextureProvider {
		public ICTextures(DataGenerator g, String mod, ExistingFileHelper efh) {
			super(g, mod, efh);
		}

		private void makeThemedElectric(String path, boolean side, boolean advanced) {
			TextureData lightBase = load(modLoc("block/electric/light/" + (advanced ? "advanced_" : "") + (side ? "side" : "top")));
			TextureData darkBase = load(modLoc("block/electric/dark/" + (advanced ? "advanced_" : "") + (side ? "side" : "top")));
			make(modLoc("block/electric/light/" + path), lightBase.combine(load(modLoc("block/electric/light/template/" + path))));
			make(modLoc("block/electric/dark/" + path), darkBase.combine(load(modLoc("block/electric/dark/template/" + path))));
		}

		private void makeThemedElectricOnOff(String path, boolean side, boolean advanced) {
			makeThemedElectric(path + "_on", side, advanced);
			makeThemedElectric(path + "_off", side, advanced);
		}

		@Override
		public void registerTextures() {
			makeThemedElectricOnOff("basic_generator_front", true, false);
			makeThemedElectricOnOff("geothermal_generator_front", true, false);
			makeThemedElectric("wind_mill_front", true, false);
			makeThemedElectric("lv_solar_panel_top", false, false);
			makeThemedElectric("mv_solar_panel_top", false, false);
			makeThemedElectric("hv_solar_panel_top", false, true);
			makeThemedElectricOnOff("nuclear_reactor_side", true, true);

			makeThemedElectricOnOff("electric_furnace_front", true, false);
			makeThemedElectric("macerator_front", true, false);
			makeThemedElectricOnOff("macerator_top", false, false);
			makeThemedElectricOnOff("extractor_front", true, false);
			makeThemedElectric("extractor_top", false, false);
			makeThemedElectricOnOff("compressor_front", true, false);
			makeThemedElectric("compressor_top", false, false);
			makeThemedElectricOnOff("electrolyzer_side", true, false);
			makeThemedElectricOnOff("recycler_front", true, false);
			makeThemedElectric("recycler_top", false, false);
			makeThemedElectricOnOff("canning_machine_front", true, false);

			makeThemedElectricOnOff("induction_furnace_front", true, true);
			makeThemedElectric("rotary_macerator_front", true, true);
			makeThemedElectricOnOff("rotary_macerator_top", false, true);
			makeThemedElectricOnOff("vacuum_extractor_front", true, true);
			makeThemedElectric("vacuum_extractor_top", false, true);
			makeThemedElectricOnOff("singularity_compressor_front", true, true);
			makeThemedElectric("singularity_compressor_top", false, true);
			makeThemedElectricOnOff("antimatter_fabricator_front", true, true);
			makeThemedElectric("antimatter_fabricator_side", true, true);
		}
	}

	private static class ICBlockModels extends BlockModelProvider {
		public ICBlockModels(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
			super(generator, modid, existingFileHelper);
		}

		private void electric(String id, String front, String side, String top) {
			//orientable("block/electric/light/" + id, modLoc("block/electric/light/" + side), modLoc("block/electric/light/" + front), modLoc("block/electric/light/" + top));
			//orientable("block/electric/dark/" + id, modLoc("block/electric/dark/" + side), modLoc("block/electric/dark/" + front), modLoc("block/electric/dark/" + top));
			withExistingParent("block/electric/light/" + id, modLoc("block/orientable_2d")).texture("side", modLoc("block/electric/light/" + side)).texture("front", modLoc("block/electric/light/" + front)).texture("top", modLoc("block/electric/light/" + top));
			withExistingParent("block/electric/dark/" + id, modLoc("block/orientable_2d")).texture("side", modLoc("block/electric/dark/" + side)).texture("front", modLoc("block/electric/dark/" + front)).texture("top", modLoc("block/electric/dark/" + top));
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
					.face(Direction.DOWN).texture("#top").cullface(Direction.DOWN).end()
					.face(Direction.UP).texture("#top").cullface(Direction.UP).rotation(ModelBuilder.FaceRotation.UPSIDE_DOWN).end()
					.face(Direction.NORTH).texture("#front").cullface(Direction.NORTH).end()
					.face(Direction.SOUTH).texture("#side").cullface(Direction.SOUTH).end() // #bottom
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
				CableBlock block = (CableBlock) cable.get();
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

			orientable("block/iron_furnace_off", modLoc("block/iron_furnace_side"), modLoc("block/iron_furnace_front_off"), modLoc("block/iron_furnace_side"));
			orientable("block/iron_furnace_on", modLoc("block/iron_furnace_side"), modLoc("block/iron_furnace_front_on"), modLoc("block/iron_furnace_side"));

			electric("basic_generator_off", "basic_generator_front_off", "side", "top");
			electric("basic_generator_on", "basic_generator_front_on", "side", "top");
			electric("geothermal_generator_off", "geothermal_generator_front_off", "side", "top");
			electric("geothermal_generator_on", "geothermal_generator_front_on", "side", "top");
			electric("wind_mill", "wind_mill_front", "side", "top");
			electric("lv_solar_panel", "side", "side", "lv_solar_panel_top");
			electric("mv_solar_panel", "side", "side", "mv_solar_panel_top");
			electric("hv_solar_panel", "advanced_side", "advanced_side", "hv_solar_panel_top");
			electric("nuclear_reactor_off", "nuclear_reactor_side_off", "nuclear_reactor_side_off", "advanced_top");
			electric("nuclear_reactor_on", "nuclear_reactor_side_on", "nuclear_reactor_side_on", "advanced_top");

			electric("electric_furnace_off", "electric_furnace_front_off", "side", "top");
			electric("electric_furnace_on", "electric_furnace_front_on", "side", "top");
			electric("macerator_off", "macerator_front", "side", "macerator_top_off");
			electric("macerator_on", "macerator_front", "side", "macerator_top_on");
			electric("extractor_off", "extractor_front_off", "side", "extractor_top");
			electric("extractor_on", "extractor_front_on", "side", "extractor_top");
			electric("compressor_off", "compressor_front_off", "side", "compressor_top");
			electric("compressor_on", "compressor_front_on", "side", "compressor_top");
			electric("electrolyzer_off", "electrolyzer_side_off", "electrolyzer_side_off", "top");
			electric("electrolyzer_on", "electrolyzer_side_on", "electrolyzer_side_on", "top");
			electric("recycler_off", "recycler_front_off", "side", "recycler_top");
			electric("recycler_on", "recycler_front_on", "side", "recycler_top");
			electric("canning_machine_off", "canning_machine_front_off", "side", "top");
			electric("canning_machine_on", "canning_machine_front_on", "side", "top");

			electric("induction_furnace_off", "induction_furnace_front_off", "advanced_side", "advanced_top");
			electric("induction_furnace_on", "induction_furnace_front_on", "advanced_side", "advanced_top");
			electric("rotary_macerator_off", "rotary_macerator_front", "advanced_side", "rotary_macerator_top_off");
			electric("rotary_macerator_on", "rotary_macerator_front", "advanced_side", "rotary_macerator_top_on");
			electric("vacuum_extractor_off", "vacuum_extractor_front_off", "advanced_side", "vacuum_extractor_top");
			electric("vacuum_extractor_on", "vacuum_extractor_front_on", "advanced_side", "vacuum_extractor_top");
			electric("singularity_compressor_off", "singularity_compressor_front_off", "advanced_side", "singularity_compressor_top");
			electric("singularity_compressor_on", "singularity_compressor_front_on", "advanced_side", "singularity_compressor_top");
			electric("antimatter_fabricator_off", "antimatter_fabricator_front_off", "antimatter_fabricator_side", "advanced_top");
			electric("antimatter_fabricator_on", "antimatter_fabricator_front_on", "antimatter_fabricator_side", "advanced_top");

			electric3d("lv_battery_box", "lv_battery_box_out", "lv_battery_box_in");
			electric3d("mv_battery_box", "mv_battery_box_out", "mv_battery_box_in");
			electric3d("hv_battery_box", "hv_battery_box_out", "hv_battery_box_in");
			electric3d("lv_transformer", "lv_transformer_in", "lv_transformer_out");
			electric3d("mv_transformer", "mv_transformer_in", "mv_transformer_out");
			electric3d("hv_transformer", "hv_transformer_in", "hv_transformer_out");
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
			simpleBlock(FTBICBlocks.MACHINE_BLOCK.get());
			simpleBlock(FTBICBlocks.ADVANCED_MACHINE_BLOCK.get());

			getVariantBuilder(FTBICBlocks.IRON_FURNACE.get()).forAllStatesExcept(state -> {
				boolean lit = state.getValue(BlockStateProperties.LIT);
				Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);

				return ConfiguredModel.builder()
						.modelFile(models().getExistingFile(modLoc(lit ? "block/iron_furnace_on" : "block/iron_furnace_off")))
						.rotationY(((facing.get2DDataValue() & 3) * 90 + 180) % 360)
						.build();
			});

			for (Supplier<Block> cable : FTBICBlocks.CABLES) {
				CableBlock block = (CableBlock) cable.get();
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

					if (machine.stateProperty == ElectricBlockState.OFF_BURNT) {
						ignoredProperties.add(ElectricBlockState.OFF_BURNT);
					}

					getVariantBuilder(machine.block.get()).forAllStatesExcept(state -> {
						boolean hasOnState = machine.hasOnState();
						boolean on = hasOnState && state.getValue(machine.stateProperty) == ElectricBlockState.ON;
						boolean dark = state.getValue(SprayPaintable.DARK);
						ModelFile modelFile = models().getExistingFile(modLoc("block/electric/" + (dark ? "dark" : "light") + "/" + machine.id + (hasOnState ? (on ? "_on" : "_off") : "")));

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
				basicItem(() -> cable.get().asItem());
			}

			for (ElectricBlockInstance machine : FTBICElectricBlocks.ALL) {
				if (!machine.noModel) {
					withExistingParent(machine.id, modLoc("block/electric/light/" + machine.id + (machine.hasOnState() ? "_off" : "")));
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
			basicItem(FTBICItems.EMPTY_CELL);
			basicItem(FTBICItems.WATER_CELL);
			basicItem(FTBICItems.LAVA_CELL);
			basicItem(FTBICItems.COOLANT_10K);
			basicItem(FTBICItems.COOLANT_30K);
			basicItem(FTBICItems.COOLANT_60K);
			basicItem(FTBICItems.URANIUM_FUEL_ROD);
			basicItem(FTBICItems.DUAL_URANIUM_FUEL_ROD);
			basicItem(FTBICItems.QUAD_URANIUM_FUEL_ROD);
			basicItem(FTBICItems.CANNED_FOOD);
			basicItem(FTBICItems.DARK_SPRAY_PAINT_CAN);
			basicItem(FTBICItems.LIGHT_SPRAY_PAINT_CAN);
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
				if (machine.stateProperty == ElectricBlockState.ON_OFF_BURNT) {
					add(machine.block.get(), LootTable.lootTable()
							.withPool(LootPool.lootPool()
									.when(InvertedLootItemCondition.invert(LootItemBlockStatePropertyCondition.hasBlockStateProperties(machine.block.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(machine.stateProperty, ElectricBlockState.BURNT))))
									.setRolls(ConstantIntValue.exactly(1))
									.add(LootItem.lootTableItem(machine.item.get()))
							)
							.withPool(LootPool.lootPool()
									.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(machine.block.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(machine.stateProperty, ElectricBlockState.BURNT)))
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
