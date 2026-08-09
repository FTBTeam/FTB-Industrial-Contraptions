package dev.ftb.mods.industrialcontraptions.datagen;

import dev.ftb.mods.industrialcontraptions.IC;
import dev.ftb.mods.industrialcontraptions.block.BaseCableBlock;
import dev.ftb.mods.industrialcontraptions.block.ElectricBlock;
import dev.ftb.mods.industrialcontraptions.block.ElectricBlockInstance;
import dev.ftb.mods.industrialcontraptions.block.ICBlocks;
import dev.ftb.mods.industrialcontraptions.block.ICElectricBlocks;
import dev.ftb.mods.industrialcontraptions.block.SprayPaintable;
import dev.ftb.mods.industrialcontraptions.item.ElectricBlockItem;
import dev.ftb.mods.industrialcontraptions.item.ICItems;
import dev.ftb.mods.industrialcontraptions.material.MaterialComponent;
import dev.ftb.mods.industrialcontraptions.material.MaterialEntries;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Set;
import java.util.stream.Stream;

public class ICModelProvider extends ModelProvider {

	private static final Set<String> CUSTOM_CABLE_ITEMS = Set.of(
			"lv_cable", "mv_cable", "hv_cable", "ev_cable", "iv_cable", "burnt_cable"
	);

	public ICModelProvider(PackOutput output) {
		super(output, IC.MOD_ID);
	}

	@Override
	protected Stream<? extends Holder<Block>> getKnownBlocks() {
		return Stream.empty();
	}

	@Override
	protected Stream<? extends Holder<Item>> getKnownItems() {
		return Stream.empty();
	}

	@Override
	protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
		MaterialEntries.all().forEach(entry -> {
			if (entry.component().isBlock()) {
				blockModels.createTrivialCube(entry.block().get());
			}
		});

		blockModels.createTrivialCube(ICBlocks.REINFORCED_STONE.get());
		blockModels.createTrivialCube(ICBlocks.REINFORCED_GLASS.get());
		blockModels.createTrivialCube(ICBlocks.ENDERIUM_BLOCK.get());

		cubeBottomTop(blockModels, ICBlocks.MACHINE_BLOCK.get(),
				"block/electric/light/basic_top", "block/electric/light/basic_side", "block/electric/light/basic_bottom");
		cubeBottomTop(blockModels, ICBlocks.ADVANCED_MACHINE_BLOCK.get(),
				"block/electric/light/advanced_top", "block/electric/light/advanced_side", "block/electric/light/advanced_bottom");
		cubeBottomTop(blockModels, ICBlocks.NUCLEAR_REACTOR_CHAMBER.get(),
				"block/electric/light/nuclear_reactor_top", "block/electric/light/advanced_side", "block/electric/light/advanced_bottom");

		for (ElectricBlockInstance m : ICElectricBlocks.ALL) {
			String modelPath = "block/electric/light/" + m.id + (m.canBeActive ? "_off" : "");
			blockModels.registerSimpleItemModel(m.item.get(), IC.id(modelPath));
			electricBlockstate(blockModels, m);
		}

		ironFurnace(blockModels);

		cableMultipart(blockModels, ICBlocks.LV_CABLE.get(), "lv_cable");
		cableMultipart(blockModels, ICBlocks.MV_CABLE.get(), "mv_cable");
		cableMultipart(blockModels, ICBlocks.HV_CABLE.get(), "hv_cable");
		cableMultipart(blockModels, ICBlocks.EV_CABLE.get(), "ev_cable");
		cableMultipart(blockModels, ICBlocks.IV_CABLE.get(), "iv_cable");
		cableMultipart(blockModels, ICBlocks.BURNT_CABLE.get(), "burnt_cable");

		reinforcedCableMultipart(blockModels, ICBlocks.LV_REINFORCED_CABLE.get(), "lv_reinforced_cable");
		reinforcedCableMultipart(blockModels, ICBlocks.MV_REINFORCED_CABLE.get(), "mv_reinforced_cable");
		reinforcedCableMultipart(blockModels, ICBlocks.HV_REINFORCED_CABLE.get(), "hv_reinforced_cable");
		reinforcedCableMultipart(blockModels, ICBlocks.EV_REINFORCED_CABLE.get(), "ev_reinforced_cable");
		reinforcedCableMultipart(blockModels, ICBlocks.IV_REINFORCED_CABLE.get(), "iv_reinforced_cable");
		reinforcedCableMultipart(blockModels, ICBlocks.BURNT_REINFORCED_CABLE.get(), "burnt_reinforced_cable");

		for (DeferredHolder<Item, ? extends Item> holder : ICItems.REGISTRY.getEntries()) {
			Item item = holder.get();
			String id = holder.getId().getPath();

			if (item instanceof ElectricBlockItem) continue;

			if (item instanceof BlockItem) {
				if (CUSTOM_CABLE_ITEMS.contains(id)) {
					blockModels.registerSimpleItemModel(item, IC.id("item/" + id));
				} else if (id.equals("iron_furnace")) {
					blockModels.registerSimpleItemModel(item, IC.id("block/iron_furnace_off"));
				} else if (id.equals("landmark")) {
					blockModels.registerSimpleFlatItemModel(ICBlocks.LANDMARK.get(), "_ns");
				} else if (id.equals("exfluid")) {
					blockModels.registerSimpleItemModel(item, Identifier.withDefaultNamespace("block/dead_horn_coral_block"));
				} else {
					blockModels.registerSimpleItemModel(item, IC.id("block/" + id));
				}
			} else {
				itemModels.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
			}
		}
	}

	private static void electricBlockstate(BlockModelGenerators blockModels, ElectricBlockInstance m) {
		String suffix = m.canBeActive ? "_off" : "";
		MultiVariant lightBase = BlockModelGenerators.plainVariant(IC.id("block/electric/light/" + m.id + suffix));
		MultiVariant darkBase = BlockModelGenerators.plainVariant(IC.id("block/electric/dark/" + m.id + suffix));

		MultiVariantGenerator gen;
		if (m.canBeActive) {
			MultiVariant lightOn = BlockModelGenerators.plainVariant(IC.id("block/electric/light/" + m.id + "_on"));
			MultiVariant darkOn = BlockModelGenerators.plainVariant(IC.id("block/electric/dark/" + m.id + "_on"));
			gen = MultiVariantGenerator.dispatch(m.block.get()).with(
					PropertyDispatch.initial(ElectricBlock.ACTIVE, SprayPaintable.DARK)
							.select(false, false, lightBase)
							.select(true, false, lightOn)
							.select(false, true, darkBase)
							.select(true, true, darkOn));
		} else {
			gen = MultiVariantGenerator.dispatch(m.block.get()).with(
					PropertyDispatch.initial(SprayPaintable.DARK)
							.select(false, lightBase)
							.select(true, darkBase));
		}

		if (m.facingProperty == BlockStateProperties.HORIZONTAL_FACING) {
			gen = gen.with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING);
		} else if (m.facingProperty == BlockStateProperties.FACING) {
			gen = gen.with(BlockModelGenerators.ROTATION_FACING);
		}

		blockModels.blockStateOutput.accept(gen);
	}

	private static void cableMultipart(BlockModelGenerators blockModels, Block block, String cableId) {
		MultiVariant base = BlockModelGenerators.plainVariant(IC.id("block/" + cableId + "_base"));
		MultiVariant conn = BlockModelGenerators.plainVariant(IC.id("block/" + cableId + "_connection"));

		blockModels.blockStateOutput.accept(
				MultiPartGenerator.multiPart(block)
						.with(base)
						.with(BlockModelGenerators.condition(BaseCableBlock.CONNECTION[Direction.DOWN.get3DDataValue()], true), conn)
						.with(BlockModelGenerators.condition(BaseCableBlock.CONNECTION[Direction.UP.get3DDataValue()], true), conn.with(BlockModelGenerators.X_ROT_180))
						.with(BlockModelGenerators.condition(BaseCableBlock.CONNECTION[Direction.NORTH.get3DDataValue()], true), conn.with(BlockModelGenerators.X_ROT_90).with(BlockModelGenerators.Y_ROT_180))
						.with(BlockModelGenerators.condition(BaseCableBlock.CONNECTION[Direction.SOUTH.get3DDataValue()], true), conn.with(BlockModelGenerators.X_ROT_90))
						.with(BlockModelGenerators.condition(BaseCableBlock.CONNECTION[Direction.WEST.get3DDataValue()], true), conn.with(BlockModelGenerators.X_ROT_90).with(BlockModelGenerators.Y_ROT_90))
						.with(BlockModelGenerators.condition(BaseCableBlock.CONNECTION[Direction.EAST.get3DDataValue()], true), conn.with(BlockModelGenerators.X_ROT_90).with(BlockModelGenerators.Y_ROT_270)));
	}

	private static void reinforcedCableMultipart(BlockModelGenerators blockModels, Block block, String cableId) {
		blockModels.blockStateOutput.accept(
				MultiPartGenerator.multiPart(block)
						.with(BlockModelGenerators.plainVariant(IC.id("block/" + cableId))));
	}

	private static void ironFurnace(BlockModelGenerators blockModels) {
		Block block = ICBlocks.IRON_FURNACE.get();
		Material side = new Material(IC.id("block/iron_furnace_side"));
		Material top = new Material(IC.id("block/iron_furnace_top"));
		Material frontOff = new Material(IC.id("block/iron_furnace_front_off"));
		Material frontOn = new Material(IC.id("block/iron_furnace_front_on"));

		TextureMapping offMap = new TextureMapping()
				.put(TextureSlot.SIDE, side).put(TextureSlot.FRONT, frontOff).put(TextureSlot.TOP, top);
		TextureMapping onMap = new TextureMapping()
				.put(TextureSlot.SIDE, side).put(TextureSlot.FRONT, frontOn).put(TextureSlot.TOP, top);

		Identifier offModel = ModelTemplates.CUBE_ORIENTABLE.createWithSuffix(block, "_off", offMap, blockModels.modelOutput);
		Identifier onModel = ModelTemplates.CUBE_ORIENTABLE.createWithSuffix(block, "_on", onMap, blockModels.modelOutput);

		blockModels.blockStateOutput.accept(
				MultiVariantGenerator.dispatch(block)
						.with(BlockModelGenerators.createBooleanModelDispatch(
								BlockStateProperties.LIT,
								BlockModelGenerators.plainVariant(onModel),
								BlockModelGenerators.plainVariant(offModel)))
						.with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING));
	}

	private static void cubeBottomTop(BlockModelGenerators blockModels, Block block, String top, String side, String bottom) {
		TextureMapping mapping = new TextureMapping()
				.put(TextureSlot.TOP, new Material(IC.id(top)))
				.put(TextureSlot.SIDE, new Material(IC.id(side)))
				.put(TextureSlot.BOTTOM, new Material(IC.id(bottom)));
		Identifier modelId = ModelTemplates.CUBE_BOTTOM_TOP.create(block, mapping, blockModels.modelOutput);
		blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, BlockModelGenerators.plainVariant(modelId)));
	}
}
