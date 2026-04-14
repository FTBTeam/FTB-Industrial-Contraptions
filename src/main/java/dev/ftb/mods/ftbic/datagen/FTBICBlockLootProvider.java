package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.world.ResourceElements;
import dev.ftb.mods.ftbic.world.ResourceType;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Block loot tables for resource ores + storage blocks. Ores drop a `*_chunk` item (silk-touch
 * yields the ore itself + bonus via {@code createOreDrop}); storage blocks drop themselves.
 */
public class FTBICBlockLootProvider extends BlockLootSubProvider {
	protected FTBICBlockLootProvider(HolderLookup.Provider lookup) {
		super(Set.of(), FeatureFlags.REGISTRY.allFlags(), lookup);
	}

	@Override
	protected void generate() {
		Set<Block> seen = new HashSet<>();

		for (ResourceElements el : ResourceElements.VALUES) {
			var oreSup = FTBICBlocks.RESOURCE_ORES.get(el);
			if (oreSup != null) {
				Block ore = oreSup.get();
				if (seen.add(ore)) {
					Item chunk = chunkItemFor(el);
					if (chunk == null) {
						dropSelf(ore);
					} else {
						add(ore, b -> createOreDrop(b, chunk));
					}
				}
			}

			var blockSup = FTBICBlocks.RESOURCE_BLOCKS_OF.get(el);
			if (blockSup != null && seen.add(blockSup.get())) {
				dropSelf(blockSup.get());
			}
		}
	}

	private Item chunkItemFor(ResourceElements el) {
		ResourceElements baseEl = ResourceElements.getNonDeepslateVersion(el).orElse(el);
		var perElement = FTBICItems.RESOURCE_TYPE_MAP.get(ResourceType.RAW);
		if (perElement == null) return null;
		var sup = perElement.get(baseEl);
		return sup == null ? null : sup.get();
	}

	@Override
	protected Iterable<Block> getKnownBlocks() {
		return Stream.concat(
				FTBICBlocks.RESOURCE_ORES.values().stream().map(supplier -> (Block) supplier.get()),
				FTBICBlocks.RESOURCE_BLOCKS_OF.values().stream().map(supplier -> supplier.get())
		).filter(java.util.Objects::nonNull).collect(Collectors.toSet());
	}
}
