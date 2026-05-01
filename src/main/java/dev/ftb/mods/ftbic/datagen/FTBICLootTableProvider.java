package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.material.Material;
import dev.ftb.mods.ftbic.material.MaterialComponent;
import dev.ftb.mods.ftbic.material.MaterialEntries;
import dev.ftb.mods.ftbic.material.MaterialEntry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContextSource;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class FTBICLootTableProvider extends LootTableProvider {
	public FTBICLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, Set.of(), List.of(
				new SubProviderEntry(MaterialBlockLoot::new, LootContextParamSets.BLOCK)
		), registries);
	}

	@Override
	protected void validate(WritableRegistry<LootTable> registry, ValidationContextSource context, ProblemReporter.Collector reporter) {
	}

	private static final class MaterialBlockLoot extends BlockLootSubProvider {
		private final List<Block> known = new ArrayList<>();

		MaterialBlockLoot(HolderLookup.Provider provider) {
			super(Set.of(), FeatureFlags.DEFAULT_FLAGS, provider);
		}

		@Override
		protected void generate() {
			Set<Block> seen = new HashSet<>();
			for (MaterialEntry entry : MaterialEntries.all()) {
				if (!entry.component().isBlock()) continue;
				Block block = entry.block().get();
				if (!seen.add(block)) continue;
				known.add(block);

				Material mat = entry.material();
				MaterialComponent comp = entry.component();
				if (comp == MaterialComponent.STONE_ORE || comp == MaterialComponent.DEEPSLATE_ORE) {
					MaterialEntry rawOre = MaterialEntries.get(mat, MaterialComponent.RAW_ORE);
					if (rawOre != null) {
						Item drop = rawOre.item().get();
						add(block, createOreDrop(block, drop));
					} else {
						dropSelf(block);
					}
				} else {
					dropSelf(block);
				}
			}
		}

		@Override
		protected Iterable<Block> getKnownBlocks() {
			return known;
		}
	}
}
