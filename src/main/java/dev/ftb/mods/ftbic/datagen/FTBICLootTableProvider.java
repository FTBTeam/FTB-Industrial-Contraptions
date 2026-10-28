package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.material.Material;
import dev.ftb.mods.ftbic.material.MaterialComponent;
import dev.ftb.mods.ftbic.material.MaterialEntries;
import dev.ftb.mods.ftbic.material.MaterialEntry;
import dev.ftb.mods.ftbic.registry.ModDataComponents;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class FTBICLootTableProvider extends LootTableProvider {
    public FTBICLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(
                output,
                Set.of(),
                List.of(
                        new SubProviderEntry(MaterialBlockLoot::new, LootContextParamSets.BLOCK),
                        new SubProviderEntry(
                                provider -> tables -> scrapBox(provider, tables), LootContextParamSets.GIFT)),
                registries);
    }

    private static void scrapBox(
            HolderLookup.Provider provider, BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        var pool = LootPool.lootPool().setRolls(ConstantValue.exactly(1));
        pool.add(scrapReward("minecraft:dirt", 6));
        pool.add(scrapReward("minecraft:coarse_dirt", 3));
        pool.add(scrapReward("minecraft:gravel", 5));
        pool.add(scrapReward("minecraft:sand", 4));
        pool.add(scrapReward("minecraft:red_sand", 2));
        pool.add(scrapReward("minecraft:cobblestone", 5));
        pool.add(scrapReward("minecraft:cobbled_deepslate", 2));
        pool.add(scrapReward("minecraft:tuff", 2));
        pool.add(scrapReward("minecraft:andesite", 3));
        pool.add(scrapReward("minecraft:diorite", 3));
        pool.add(scrapReward("minecraft:granite", 3));
        pool.add(scrapReward("minecraft:netherrack", 2));
        pool.add(scrapReward("minecraft:stick", 4));
        pool.add(scrapReward("minecraft:leather", 4));
        pool.add(scrapReward("ftbic:fluid_cell", 3));
        pool.add(scrapReward("ftbic:empty_can", 3));
        pool.add(scrapReward("ftbic:canned_food", 2));
        pool.add(scrapReward("ftbic:protein_bar", 1));
        pool.add(scrapReward("minecraft:rotten_flesh", 3));
        pool.add(scrapReward("minecraft:bone", 3));
        pool.add(scrapReward("minecraft:feather", 2));
        pool.add(scrapReward("ftbic:rubber", 2));
        pool.add(scrapReward("ftbic:fuse", 2));
        pool.add(scrapReward("ftbic:single_use_battery", 1));
        pool.add(scrapReward("minecraft:copper_shovel", 3)
                .apply(EnchantRandomlyFunction.randomApplicableEnchantment(provider)));
        pool.add(scrapReward("minecraft:copper_pickaxe", 3)
                .apply(EnchantRandomlyFunction.randomApplicableEnchantment(provider)));
        pool.add(scrapReward("minecraft:copper_axe", 2)
                .apply(EnchantRandomlyFunction.randomApplicableEnchantment(provider)));
        pool.add(scrapReward("minecraft:obsidian", 2));
        pool.add(scrapReward("ftbic:electronic_circuit", 2));
        pool.add(scrapReward("ftbic:aluminum_dust", 1));
        pool.add(scrapReward("ftbic:copper_dust", 1));
        pool.add(scrapReward("ftbic:gold_dust", 1));
        pool.add(scrapReward("ftbic:iron_dust", 1));
        pool.add(scrapReward("ftbic:lead_dust", 1));
        pool.add(scrapReward("ftbic:nickel_dust", 1));
        pool.add(scrapReward("ftbic:silver_dust", 1));
        pool.add(scrapReward("ftbic:tin_dust", 1));
        pool.add(scrapReward("minecraft:diamond", 1));
        pool.add(scrapReward("minecraft:amethyst_shard", 2));
        pool.add(scrapReward("minecraft:pointed_dripstone", 2));
        pool.add(scrapReward("ftbic:overclocker_upgrade", 1));
        pool.add(scrapReward("ftbic:mixed_metal_blend", 3));
        pool.add(scrapReward("ftbic:iridium_dust", 1));
        output.accept(
                ResourceKey.create(Registries.LOOT_TABLE, FTBIC.id("gameplay/scrap_box")),
                LootTable.lootTable().withPool(pool));
    }

    private static LootItem.Builder scrapReward(String name, int weight) {
        Item item = BuiltInRegistries.ITEM
                .getOrThrow(ResourceKey.create(Registries.ITEM, Identifier.parse(name)))
                .value();
        return LootItem.lootTableItem(item).setWeight(weight);
    }

    private static final class MaterialBlockLoot extends BlockLootSubProvider {
        private static final Set<String> UNCONDITIONAL_DROPS = Set.of(
                "advanced_canning_machine",
                "advanced_centrifuge",
                "advanced_compressor",
                "advanced_extruder",
                "advanced_hydroponic_accelerator",
                "advanced_macerator",
                "advanced_ore_washer",
                "advanced_powered_furnace",
                "advanced_roller",
                "alloy_smelter",
                "batch_feeder",
                "canning_machine",
                "centrifuge",
                "compressor",
                "ev_rectifier",
                "ev_transformer",
                "extruder",
                "hv_rectifier",
                "hv_transformer",
                "hydroponic_accelerator",
                "iv_rectifier",
                "lv_rectifier",
                "lv_transformer",
                "macerator",
                "mv_rectifier",
                "mv_transformer",
                "ore_washer",
                "powered_crafting_table",
                "powered_furnace",
                "reprocessor",
                "roller");
        private static final Set<String> ENERGY_DROPS = Set.of(
                "ev_battery_box",
                "hv_battery_box",
                "industrial_bank_cell",
                "industrial_bank_port",
                "lv_battery_box",
                "mv_battery_box");
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
            // Material ores retain Silk Touch/Fortune; other blocks keep their existing explosion and energy rules.
            for (var holder : FTBICBlocks.REGISTRY.getEntries()) {
                Block block = holder.get();
                if (!seen.add(block) || block.getLootTable().isEmpty()) continue;
                known.add(block);
                String name = holder.getId().getPath();
                var entry = LootItem.lootTableItem(block);
                if (ENERGY_DROPS.contains(name)) {
                    entry.apply(CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
                            .include(ModDataComponents.ENERGY.get()));
                }
                var pool =
                        LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(entry);
                if (!UNCONDITIONAL_DROPS.contains(name) && !ENERGY_DROPS.contains(name)) {
                    pool.when(ExplosionCondition.survivesExplosion());
                }
                add(block, LootTable.lootTable().withPool(pool));
            }
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return known;
        }
    }
}
