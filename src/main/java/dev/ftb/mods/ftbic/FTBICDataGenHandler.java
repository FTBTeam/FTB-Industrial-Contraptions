package dev.ftb.mods.ftbic;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.item.FTBICItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
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
			gen.addProvider(new ICBlockStates(gen, MODID, event.getExistingFileHelper()));
			gen.addProvider(new ICBlockModels(gen, MODID, event.getExistingFileHelper()));
			gen.addProvider(new ICItemModels(gen, MODID, event.getExistingFileHelper()));
		}

		if (event.includeServer()) {
			ICBlockTags blockTags = new ICBlockTags(gen, MODID, efh);
			gen.addProvider(blockTags);
			gen.addProvider(new ICItemTags(gen, blockTags, MODID, efh));
			gen.addProvider(new ICRecipes(gen));
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

			addItem(FTBICItems.RUBBER, "Rubber");
			addItem(FTBICItems.RESIN, "Resin");
			addItem(FTBICItems.MIXED_METAL_INGOT, "Mixed Metal Ingot");
			addItem(FTBICItems.ADVANCED_ALLOY, "Advanced Alloy");
			addItem(FTBICItems.COAL_BALL, "Coal Ball");
			addItem(FTBICItems.COMPRESSED_COAL_BALL, "Compressed Coal Ball");
			addItem(FTBICItems.COAL_CHUNK, "Coal Chunk");
			addItem(FTBICItems.RAW_IRIDIUM, "Raw Iridium");
			addItem(FTBICItems.IRIDIUM_PLATE, "Iridium Plate");
			addItem(FTBICItems.SCRAP, "Scrap");
			addItem(FTBICItems.SCRAP_BOX, "Scrap Box");
			addItem(FTBICItems.SINGLE_USE_BATTERY, "Single Use Battery");
			addItem(FTBICItems.BATTERY, "Battery");
			addItem(FTBICItems.CRYSTAL_BATTERY, "Crystal Battery");
			addItem(FTBICItems.GRAPHENE_BATTERY, "Graphene Battery");
			addItem(FTBICItems.IRIDIUM_BATTERY, "Iridium Battery");
			addItem(FTBICItems.OVERCLOCKER_UPGRADE, "Overclocker Upgrade");
			addItem(FTBICItems.ENERGY_STORAGE_UPGRADE, "Energy Storage Upgrade");
			addItem(FTBICItems.TRANSFORMER_UPGRADE, "Transformer Upgrade");
			addItem(FTBICItems.CLAY_DUST, "Clay Dust");
			addItem(FTBICItems.ELECTRONIC_CIRCUIT, "Electronic Circuit");
			addItem(FTBICItems.ADVANCED_CIRCUIT, "Advanced Circuit");
			addItem(FTBICItems.RAW_CARBON_FIBRE, "Raw Carbon Fibre");
			addItem(FTBICItems.RAW_CARBON_MESH, "Raw Carbon Mesh");
			addItem(FTBICItems.CARBON_PLATE, "Carbon Plate");
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
		}
	}

	private static class ICBlockModels extends BlockModelProvider {
		public ICBlockModels(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
			super(generator, modid, existingFileHelper);
		}

		@Override
		protected void registerModels() {
		}
	}

	private static class ICItemModels extends ItemModelProvider {
		public ICItemModels(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
			super(generator, modid, existingFileHelper);
		}

		private void basicItem(Supplier<Item> item) {
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

			basicItem(FTBICItems.RUBBER);
			basicItem(FTBICItems.RESIN);
			basicItem(FTBICItems.MIXED_METAL_INGOT);
			basicItem(FTBICItems.ADVANCED_ALLOY);
			basicItem(FTBICItems.COAL_BALL);
			basicItem(FTBICItems.COMPRESSED_COAL_BALL);
			basicItem(FTBICItems.COAL_CHUNK);
			basicItem(FTBICItems.RAW_IRIDIUM);
			basicItem(FTBICItems.IRIDIUM_PLATE);
			basicItem(FTBICItems.SCRAP);
			basicItem(FTBICItems.SCRAP_BOX);
			basicItem(FTBICItems.SINGLE_USE_BATTERY);
			basicItem(FTBICItems.BATTERY);
			basicItem(FTBICItems.CRYSTAL_BATTERY);
			basicItem(FTBICItems.GRAPHENE_BATTERY);
			basicItem(FTBICItems.IRIDIUM_BATTERY);
			basicItem(FTBICItems.OVERCLOCKER_UPGRADE);
			basicItem(FTBICItems.ENERGY_STORAGE_UPGRADE);
			basicItem(FTBICItems.TRANSFORMER_UPGRADE);
			basicItem(FTBICItems.CLAY_DUST);
			basicItem(FTBICItems.ELECTRONIC_CIRCUIT);
			basicItem(FTBICItems.ADVANCED_CIRCUIT);
			basicItem(FTBICItems.RAW_CARBON_FIBRE);
			basicItem(FTBICItems.RAW_CARBON_MESH);
			basicItem(FTBICItems.CARBON_PLATE);
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

	private static class ICRecipes extends RecipeProvider {
		public final Tag<Item> IRON_INGOT = ItemTags.bind("forge:ingots/iron");

		public ICRecipes(DataGenerator generatorIn) {
			super(generatorIn);
		}

		@Override
		protected void buildShapelessRecipes(Consumer<FinishedRecipe> consumer) {
			ShapedRecipeBuilder.shaped(FTBICItems.RUBBER_SHEET.get())
					.unlockedBy("has_item", has(FTBICItems.RUBBER.get()))
					.group(MODID + ":rubber")
					.pattern("III")
					.define('I', FTBICItems.RUBBER.get())
					.save(consumer);

			ShapelessRecipeBuilder.shapeless(FTBICItems.RUBBER.get(), 3)
					.unlockedBy("has_item", has(FTBICItems.RUBBER.get()))
					.group(MODID + ":rubber")
					.requires(FTBICItems.RUBBER_SHEET.get())
					.save(consumer);
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
