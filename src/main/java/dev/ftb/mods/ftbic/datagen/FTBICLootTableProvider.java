package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.material.Material;
import dev.ftb.mods.ftbic.material.MaterialComponent;
import dev.ftb.mods.ftbic.material.MaterialEntries;
import dev.ftb.mods.ftbic.material.MaterialEntry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.ValidationContextSource;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class FTBICLootTableProvider extends LootTableProvider {
	public FTBICLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, Set.of(), List.of(
				new SubProviderEntry(MaterialBlockLoot::new, LootContextParamSets.BLOCK),
				new SubProviderEntry(ScrapBoxLootProvider::new, LootContextParamSets.GIFT)
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

	public static final ResourceKey<LootTable> SCRAP_BOX_LOOT_TABLE = ResourceKey.create(
		Registries.LOOT_TABLE, FTBIC.id("gameplay/scrap_box"));

	private static final class ScrapBoxLootProvider implements LootTableSubProvider {

		private final HolderLookup.Provider provider;

		ScrapBoxLootProvider(HolderLookup.Provider provider) {
			this.provider = provider;
		} 

		@Override
		public void generate(BiConsumer<ResourceKey<LootTable>, Builder> builder) {
			builder.accept(SCRAP_BOX_LOOT_TABLE, LootTable.lootTable()
				.withPool(LootPool.lootPool()
					.setRolls(ConstantValue.exactly(1F))
					// Trash tier (40% of the pool)
					.add(LootItem.lootTableItem(Items.DIRT).setWeight(6))
					.add(LootItem.lootTableItem(Items.COARSE_DIRT).setWeight(3))
					.add(LootItem.lootTableItem(Items.GRAVEL).setWeight(5))
					.add(LootItem.lootTableItem(Items.SAND).setWeight(4))
					.add(LootItem.lootTableItem(Items.RED_SAND).setWeight(2))
					.add(LootItem.lootTableItem(Items.COBBLESTONE).setWeight(5))
					.add(LootItem.lootTableItem(Items.COBBLED_DEEPSLATE).setWeight(2))
					.add(LootItem.lootTableItem(Items.TUFF).setWeight(2))
					.add(LootItem.lootTableItem(Items.ANDESITE).setWeight(3))
					.add(LootItem.lootTableItem(Items.DIORITE).setWeight(3))
					.add(LootItem.lootTableItem(Items.GRANITE).setWeight(3))
					.add(LootItem.lootTableItem(Items.NETHERRACK).setWeight(2))
					// Common tier (30% of the pool)
					.add(LootItem.lootTableItem(Items.STICK).setWeight(4))
					.add(LootItem.lootTableItem(Items.LEATHER).setWeight(4))
					.add(LootItem.lootTableItem(FTBICItems.FLUID_CELL).setWeight(3))
					.add(LootItem.lootTableItem(FTBICItems.EMPTY_CAN.item).setWeight(3))
					.add(LootItem.lootTableItem(FTBICItems.CANNED_FOOD).setWeight(2))
					.add(LootItem.lootTableItem(FTBICItems.PROTEIN_BAR).setWeight(1))
					.add(LootItem.lootTableItem(Items.ROTTEN_FLESH).setWeight(3))
					.add(LootItem.lootTableItem(Items.BONE).setWeight(3))
					.add(LootItem.lootTableItem(Items.FEATHER).setWeight(2))
					.add(LootItem.lootTableItem(FTBICItems.RUBBER.item).setWeight(2))
					.add(LootItem.lootTableItem(FTBICItems.FUSE.item).setWeight(2))
					.add(LootItem.lootTableItem(FTBICItems.SINGLE_USE_BATTERY).setWeight(1))
					// Uncommon tier (20% of the pool)
					.add(LootItem.lootTableItem(Items.COPPER_SHOVEL).setWeight(3)
						.apply(EnchantRandomlyFunction.randomApplicableEnchantment(provider)))
					.add(LootItem.lootTableItem(Items.COPPER_PICKAXE).setWeight(3)
						.apply(EnchantRandomlyFunction.randomApplicableEnchantment(provider)))
					.add(LootItem.lootTableItem(Items.COPPER_AXE).setWeight(2)
						.apply(EnchantRandomlyFunction.randomApplicableEnchantment(provider)))
					.add(LootItem.lootTableItem(Items.OBSIDIAN).setWeight(2))
					.add(LootItem.lootTableItem(FTBICItems.ELECTRONIC_CIRCUIT.item).setWeight(2))
					.add(LootItem.lootTableItem(MaterialEntries.get(Material.ALUMINUM, MaterialComponent.DUST).item()))
					.add(LootItem.lootTableItem(MaterialEntries.get(Material.COPPER, MaterialComponent.DUST).item()))
					.add(LootItem.lootTableItem(MaterialEntries.get(Material.GOLD, MaterialComponent.DUST).item()))
					.add(LootItem.lootTableItem(MaterialEntries.get(Material.IRON, MaterialComponent.DUST).item()))
					.add(LootItem.lootTableItem(MaterialEntries.get(Material.LEAD, MaterialComponent.DUST).item()))
					.add(LootItem.lootTableItem(MaterialEntries.get(Material.NICKEL, MaterialComponent.DUST).item()))
					.add(LootItem.lootTableItem(MaterialEntries.get(Material.SILVER, MaterialComponent.DUST).item()))
					.add(LootItem.lootTableItem(MaterialEntries.get(Material.TIN, MaterialComponent.DUST).item()))
					// Rare tier (9% of the pool)
					.add(LootItem.lootTableItem(Items.DIAMOND).setWeight(1))
					.add(LootItem.lootTableItem(Items.AMETHYST_SHARD).setWeight(2))
					.add(LootItem.lootTableItem(Items.POINTED_DRIPSTONE).setWeight(2))
					.add(LootItem.lootTableItem(FTBICItems.OVERCLOCKER_UPGRADE).setWeight(1))
					.add(LootItem.lootTableItem(FTBICItems.MIXED_METAL_BLEND.item).setWeight(3))
					// Jackpot (1% of the pool)
					.add(LootItem.lootTableItem(MaterialEntries.get(Material.IRIDIUM, MaterialComponent.DUST).item()))					
				));
		}
	}
}
