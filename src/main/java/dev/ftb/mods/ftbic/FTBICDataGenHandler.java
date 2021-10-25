package dev.ftb.mods.ftbic;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import dev.ftb.mods.ftbic.block.ElectricBlock;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.block.ElectricBlockState;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.item.MaterialItem;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.UpgradeRecipeBuilder;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
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
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.Tags;
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
			gen.addProvider(new ICBlockModels(gen, MODID, event.getExistingFileHelper()));
			gen.addProvider(new ICBlockStates(gen, MODID, event.getExistingFileHelper()));
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
			addBlock(FTBICBlocks.IRON_FURNACE, "Iron Furnace");

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
						.modelFile(models().getExistingFile(modLoc(lit ? "block/iron_furnace_on" : "block/iron_furnace")))
						.rotationY(((facing.get2DDataValue() & 3) * 90 + 180) % 360)
						.build();
			});

			for (ElectricBlockInstance machine : FTBICElectricBlocks.ALL) {
				if (!machine.noModel) {
					getVariantBuilder(machine.block.get()).forAllStatesExcept(state -> {
						boolean on = state.getValue(ElectricBlock.STATE) == ElectricBlockState.ON;
						boolean dark = state.getValue(ElectricBlock.DARK);
						ModelFile modelFile = models().getExistingFile(modLoc("block/electric/" + (dark ? "dark" : "light") + "/" + machine.id + (on ? "_on" : "")));

						if (machine.horizontal) {
							Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);

							return ConfiguredModel.builder()
									.modelFile(modelFile)
									.rotationY(((facing.get2DDataValue() & 3) * 90 + 180) % 360)
									.build();
						} else {
							Direction facing = state.getValue(BlockStateProperties.FACING);

							return ConfiguredModel.builder()
									.modelFile(modelFile)
									.rotationX(facing == Direction.DOWN ? 180 : (facing.getAxis().isHorizontal() ? 90 : 0))
									.rotationY(facing.getAxis().isVertical() ? 0 : ((facing.get2DDataValue() & 3) * 90 + 180) % 360)
									.build();
						}
					});
				}
				// simpleBlock(machine.block.get());
			}
		}
	}

	private static class ICBlockModels extends BlockModelProvider {
		public ICBlockModels(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
			super(generator, modid, existingFileHelper);
		}

		private void electric(String id, String front, String side, String top) {
			orientable("block/electric/light/" + id, modLoc("block/electric/light/" + side), modLoc("block/electric/light/" + front), modLoc("block/electric/light/" + top));
			orientable("block/electric/dark/" + id, modLoc("block/electric/dark/" + side), modLoc("block/electric/dark/" + front), modLoc("block/electric/dark/" + top));
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

			orientable("block/iron_furnace", modLoc("block/iron_furnace_side"), modLoc("block/iron_furnace_front"), modLoc("block/iron_furnace_side"));
			orientable("block/iron_furnace_on", modLoc("block/iron_furnace_side"), modLoc("block/iron_furnace_front_on"), modLoc("block/iron_furnace_side"));
			electric("electric_furnace", "electric_furnace_front", "side", "top");
			electric("electric_furnace_on", "electric_furnace_front_on", "side", "top");
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
			basicBlockItem(FTBICBlocks.IRON_FURNACE);

			for (ElectricBlockInstance machine : FTBICElectricBlocks.ALL) {
				if (!machine.noModel) {
					withExistingParent(machine.id, modLoc("block/electric/light/" + machine.id));
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
		public final Tag<Item> REDSTONE = Tags.Items.DUSTS_REDSTONE;
		public final Tag<Item> GLASS = Tags.Items.GLASS_COLORLESS;
		public final Tag<Item> COAL = ItemTags.COALS;
		public final Tag<Item> DIAMOND = Tags.Items.GEMS_DIAMOND;
		public final Tag<Item> QUARTZ = Tags.Items.GEMS_QUARTZ;
		public final Tag<Item> IRON_INGOT = Tags.Items.INGOTS_IRON;
		public final Tag<Item> COPPER_INGOT = ItemTags.bind("forge:ingots/copper");
		public final Tag<Item> TIN_INGOT = ItemTags.bind("forge:ingots/tin");

		public ICRecipes(DataGenerator generatorIn) {
			super(generatorIn);
		}

		private ResourceLocation modLoc(String s) {
			return new ResourceLocation(MODID, s);
		}

		private ResourceLocation smeltingLoc(String s) {
			return modLoc("smelting/" + s);
		}

		private ResourceLocation campfireCookingLoc(String s) {
			return modLoc("campfire_cooking/" + s);
		}

		private ResourceLocation shapedLoc(String s) {
			return modLoc("shaped/" + s);
		}

		private ResourceLocation shapelessLoc(String s) {
			return modLoc("shapeless/" + s);
		}

		private ResourceLocation smithingLoc(String s) {
			return modLoc("smithing/" + s);
		}

		@Override
		protected void buildShapelessRecipes(Consumer<FinishedRecipe> consumer) {
			// Cooking

			SimpleCookingRecipeBuilder.cooking(FTBICItems.RESIN.ingredient(), FTBICItems.RUBBER.get(), 0F, 150, RecipeSerializer.SMELTING_RECIPE)
					.unlockedBy("has_item", has(FTBICItems.RESIN.get()))
					.save(consumer, smeltingLoc("rubber"));

			SimpleCookingRecipeBuilder.cooking(FTBICItems.RESIN.ingredient(), FTBICItems.RUBBER.get(), 0F, 300, RecipeSerializer.CAMPFIRE_COOKING_RECIPE)
					.unlockedBy("has_item", has(FTBICItems.RESIN.get()))
					.save(consumer, campfireCookingLoc("rubber"));

			// Shaped

			ShapedRecipeBuilder.shaped(FTBICItems.RUBBER_SHEET.get())
					.unlockedBy("has_item", has(FTBICItems.RUBBER_SHEET.get()))
					.group(MODID + ":rubber")
					.pattern("III")
					.define('I', FTBICItems.RUBBER.ingredient())
					.save(consumer, shapedLoc("rubber_sheet"));

			ShapedRecipeBuilder.shaped(FTBICItems.REINFORCED_STONE.get(), 4)
					.unlockedBy("has_item", has(FTBICItems.ADVANCED_ALLOY.get()))
					.group(MODID + ":nuclear")
					.pattern("SSS")
					.pattern("SAS")
					.pattern("SSS")
					.define('S', Items.SMOOTH_STONE)
					.define('A', FTBICItems.ADVANCED_ALLOY.ingredient())
					.save(consumer, shapedLoc("reinforced_stone"));

			ShapedRecipeBuilder.shaped(FTBICItems.REINFORCED_GLASS.get(), 4)
					.unlockedBy("has_item", has(FTBICItems.REINFORCED_STONE.get()))
					.group(MODID + ":nuclear")
					.pattern("RGR")
					.pattern("G G")
					.pattern("RGR")
					.define('R', FTBICItems.REINFORCED_STONE.get())
					.define('G', GLASS)
					.save(consumer, shapedLoc("reinforced_glass"));

			ShapedRecipeBuilder.shaped(FTBICItems.SCRAP_BOX.get())
					.unlockedBy("has_item", has(FTBICItems.SCRAP.get()))
					.group(MODID + ":scrap")
					.pattern("SSS")
					.pattern("SSS")
					.pattern("SSS")
					.define('S', FTBICItems.SCRAP.ingredient())
					.save(consumer, shapedLoc("scrap_box"));

			ShapedRecipeBuilder.shaped(FTBICItems.COAL_BALL.get())
					.unlockedBy("has_item", has(Items.COAL))
					.group(MODID + ":graphene")
					.pattern("CCC")
					.pattern("CFC")
					.pattern("CCC")
					.define('C', Ingredient.of(COAL))
					.define('F', Items.FLINT)
					.save(consumer, shapedLoc("coal_ball"));

			ShapedRecipeBuilder.shaped(FTBICItems.GRAPHENE.get())
					.unlockedBy("has_item", has(FTBICItems.COMPRESSED_COAL_BALL.get()))
					.group(MODID + ":graphene")
					.pattern("CCC")
					.pattern("COC")
					.pattern("CCC")
					.define('C', FTBICItems.COMPRESSED_COAL_BALL.ingredient())
					.define('O', Items.OBSIDIAN)
					.save(consumer, shapedLoc("graphene"));

			ShapedRecipeBuilder.shaped(FTBICItems.ENERGY_CRYSTAL.get())
					.unlockedBy("has_item", has(Items.DIAMOND))
					.group(MODID + ":energy_crystal")
					.pattern("RQR")
					.pattern("QDQ")
					.pattern("RQR")
					.define('R', REDSTONE)
					.define('D', DIAMOND)
					.define('Q', QUARTZ)
					.save(consumer, shapedLoc("energy_crystal"));

			ShapedRecipeBuilder.shaped(FTBICItems.SINGLE_USE_BATTERY.get())
					.unlockedBy("has_item", has(COAL))
					.group(MODID + ":battery")
					.pattern("C")
					.pattern("R")
					.pattern("O")
					.define('C', FTBICItems.RUBBER.ingredient()) // TODO: Change to copper cable
					.define('O', COAL)
					.define('R', REDSTONE)
					.save(consumer, shapedLoc("single_use_battery"));

			ShapedRecipeBuilder.shaped(FTBICItems.BATTERY.get())
					.unlockedBy("has_item", has(TIN_INGOT))
					.group(MODID + ":battery")
					.pattern(" C ")
					.pattern("TRT")
					.pattern("TRT")
					.define('C', FTBICItems.RUBBER.ingredient()) // TODO: Change to copper cable
					.define('T', TIN_INGOT)
					.define('R', REDSTONE)
					.save(consumer, shapedLoc("battery"));

			// Shapeless

			ShapelessRecipeBuilder.shapeless(FTBICItems.RUBBER.get(), 3)
					.unlockedBy("has_item", has(FTBICItems.RUBBER.get()))
					.group(MODID + ":rubber")
					.requires(FTBICItems.RUBBER_SHEET.get())
					.save(consumer, shapelessLoc("rubber"));

			ShapelessRecipeBuilder.shapeless(FTBICItems.SCRAP.get(), 9)
					.unlockedBy("has_item", has(FTBICItems.SCRAP_BOX.get()))
					.group(MODID + ":scrap")
					.requires(FTBICItems.SCRAP_BOX.ingredient())
					.save(consumer, shapelessLoc("scrap"));

			// Smithing

			UpgradeRecipeBuilder.smithing(Ingredient.of(FTBICItems.BATTERY.get()), FTBICItems.ENERGY_CRYSTAL.ingredient(), FTBICItems.CRYSTAL_BATTERY.get())
					.unlocks("has_item", has(FTBICItems.ENERGY_CRYSTAL.get()))
					.save(consumer, smithingLoc("crystal_battery"));

			UpgradeRecipeBuilder.smithing(Ingredient.of(FTBICItems.CRYSTAL_BATTERY.get()), FTBICItems.GRAPHENE.ingredient(), FTBICItems.GRAPHENE_BATTERY.get())
					.unlocks("has_item", has(FTBICItems.GRAPHENE.get()))
					.save(consumer, smithingLoc("graphene_battery"));

			UpgradeRecipeBuilder.smithing(Ingredient.of(FTBICItems.GRAPHENE_BATTERY.get()), FTBICItems.IRIDIUM_PLATE.ingredient(), FTBICItems.IRIDIUM_BATTERY.get())
					.unlocks("has_item", has(FTBICItems.IRIDIUM_PLATE.get()))
					.save(consumer, smithingLoc("iridium_battery"));
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

			for (ElectricBlockInstance machine : FTBICElectricBlocks.ALL) {
				add(machine.block.get(), LootTable.lootTable()
						.withPool(LootPool.lootPool()
								.when(InvertedLootItemCondition.invert(LootItemBlockStatePropertyCondition.hasBlockStateProperties(machine.block.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(ElectricBlock.STATE, ElectricBlockState.BURNT))))
								.setRolls(ConstantIntValue.exactly(1))
								.add(LootItem.lootTableItem(machine.item.get()))
						)
						.withPool(LootPool.lootPool()
								.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(machine.block.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(ElectricBlock.STATE, ElectricBlockState.BURNT)))
								.setRolls(ConstantIntValue.exactly(1))
								.add(LootItem.lootTableItem(machine.advanced ? FTBICItems.ADVANCED_MACHINE_BLOCK.get() : FTBICItems.MACHINE_BLOCK.get()))
						)
				);
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
