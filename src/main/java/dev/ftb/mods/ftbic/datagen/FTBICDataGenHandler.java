package dev.ftb.mods.ftbic.datagen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.CableBlock;
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
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
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
			gen.addProvider(new FTBICCableRecipes(gen));
			gen.addProvider(new FTBICBatteryRecipes(gen));
			gen.addProvider(new FTBICNuclearRecipes(gen));
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

	private static class ICTextures extends CombinedTextureProvider {
		public ICTextures(DataGenerator g, String mod, ExistingFileHelper efh) {
			super(g, mod, efh);
		}

		private void makeThemedElectric(String path, boolean side, boolean advanced) {
			TextureData lightBase = load(modLoc("block/electric/light/" + (advanced ? "advanced_" : "") + (side ? "side" : "top")));
			TextureData darkBase = load(modLoc("block/electric/dark/" + (advanced ? "advanced_" : "") + (side ? "side" : "top")));
			make(modLoc("block/electric/light/" + path + "_on"), lightBase.combine(load(modLoc("block/electric/light/template/" + path + "_on"))));
			make(modLoc("block/electric/dark/" + path + "_on"), darkBase.combine(load(modLoc("block/electric/dark/template/" + path + "_on"))));
			make(modLoc("block/electric/light/" + path + "_off"), lightBase.combine(load(modLoc("block/electric/light/template/" + path + "_off"))));
			make(modLoc("block/electric/dark/" + path + "_off"), darkBase.combine(load(modLoc("block/electric/dark/template/" + path + "_off"))));
		}

		@Override
		public void registerTextures() {
			makeThemedElectric("electric_furnace_front", true, false);
			makeThemedElectric("basic_generator_front", true, false);
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
			electric("electric_furnace_off", "electric_furnace_front_off", "side", "top");
			electric("electric_furnace_on", "electric_furnace_front_on", "side", "top");
			electric("basic_generator_off", "basic_generator_front_off", "side", "top");
			electric("basic_generator_on", "basic_generator_front_on", "side", "top");
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
					getVariantBuilder(machine.block.get()).forAllStatesExcept(state -> {
						boolean on = state.getValue(ElectricBlock.STATE) == ElectricBlockState.ON;
						boolean dark = state.getValue(ElectricBlock.DARK);
						ModelFile modelFile = models().getExistingFile(modLoc("block/electric/" + (dark ? "dark" : "light") + "/" + machine.id + (on ? "_on" : "_off")));

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
					withExistingParent(machine.id, modLoc("block/electric/light/" + machine.id + "_off"));
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
