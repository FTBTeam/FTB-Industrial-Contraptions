package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.item.MaterialItem;
import dev.ftb.mods.ftbic.world.ResourceElements;
import dev.ftb.mods.ftbic.world.ResourceType;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Block-state, block-model, and item-model generation. Trivial cubes for every resource block and
 * ore (auto-derived from {@link ResourceElements}); flat item models for every {@link MaterialItem}.
 *
 * <p>Complex blocks (electric machines with ACTIVE / DARK / FACING properties, multipart cables,
 * landmark) keep their hand-edited static JSONs in {@code src/main/resources/assets/ftbic/} —
 * this provider intentionally only declares those known to the generator. The shared resource
 * srcDir resolves any duplicates with {@code DuplicatesStrategy.EXCLUDE} (static wins).
 */
public class FTBICModelProvider extends ModelProvider {
	public FTBICModelProvider(PackOutput output) {
		super(output, FTBIC.MOD_ID);
	}

	@Override
	protected Stream<? extends Holder<Block>> getKnownBlocks() {
		List<Holder<Block>> all = new ArrayList<>();
		for (ResourceElements el : ResourceElements.VALUES) {
			var ore = FTBICBlocks.RESOURCE_ORES.get(el);
			if (ore != null) all.add(ore);
			var block = FTBICBlocks.RESOURCE_BLOCKS_OF.get(el);
			if (block != null) all.add(block);
		}
		return all.stream();
	}

	@Override
	protected Stream<? extends Holder<Item>> getKnownItems() {
		Set<Item> seen = new HashSet<>();
		List<Holder<Item>> items = new ArrayList<>();
		for (MaterialItem m : FTBICItems.MATERIALS) {
			if (m.item == null) continue;
			Item i = m.item.get();
			if (i != null && seen.add(i)) items.add(i.builtInRegistryHolder());
		}
		// Also include all resource items (tin_ingot, tin_dust, tin_chunk, etc.) — every entry of
		// RESOURCE_TYPE_MAP except the BlockItems for ORE/BLOCK (those are wired off the block side).
		for (var typeEntry : FTBICItems.RESOURCE_TYPE_MAP.entrySet()) {
			ResourceType type = typeEntry.getKey();
			if (type == ResourceType.ORE || type == ResourceType.BLOCK) continue;
			for (var sup : typeEntry.getValue().values()) {
				Item i = sup.get();
				if (i != null && seen.add(i)) items.add(i.builtInRegistryHolder());
			}
		}
		return items.stream();
	}

	@Override
	protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
		for (ResourceElements el : ResourceElements.VALUES) {
			var ore = FTBICBlocks.RESOURCE_ORES.get(el);
			if (ore != null) blockModels.createTrivialCube(ore.get());
			var block = FTBICBlocks.RESOURCE_BLOCKS_OF.get(el);
			if (block != null) blockModels.createTrivialCube(block.get());
		}
		for (MaterialItem m : FTBICItems.MATERIALS) {
			if (m.item == null) continue;
			Item i = m.item.get();
			if (i != null) itemModels.generateFlatItem(i, ModelTemplates.FLAT_ITEM);
		}
		for (var typeEntry : FTBICItems.RESOURCE_TYPE_MAP.entrySet()) {
			ResourceType type = typeEntry.getKey();
			if (type == ResourceType.ORE || type == ResourceType.BLOCK) continue;
			for (var sup : typeEntry.getValue().values()) {
				Item i = sup.get();
				if (i != null) itemModels.generateFlatItem(i, ModelTemplates.FLAT_ITEM);
			}
		}
	}
}
