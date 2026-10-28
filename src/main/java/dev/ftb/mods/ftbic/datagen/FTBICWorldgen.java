package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.material.Material;
import dev.ftb.mods.ftbic.material.MaterialComponent;
import dev.ftb.mods.ftbic.material.MaterialEntries;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/** Placed ore definitions and biome injection share one list so every placement reaches the Overworld. */
public final class FTBICWorldgen {
    private record Ore(String name, Material material, int size, float airExposureDiscard) {}

    private record Placement(String name, String ore, PlacementModifier frequency, HeightRangePlacement height) {}

    private static final List<Ore> ORES = ores();
    private static final List<Placement> PLACEMENTS = placements();

    public static RegistrySetBuilder registries() {
        return new RegistrySetBuilder()
                .add(Registries.CONFIGURED_FEATURE, FTBICWorldgen::configuredFeatures)
                .add(Registries.PLACED_FEATURE, FTBICWorldgen::placedFeatures)
                .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, FTBICWorldgen::biomeModifiers);
    }

    private static List<Ore> ores() {
        List<Ore> ores = new ArrayList<>();
        for (Material material : List.of(Material.ALUMINUM, Material.LEAD, Material.NICKEL, Material.TIN)) {
            ores.add(new Ore("ore_" + material.key(), material, 9, 0));
            ores.add(new Ore("ore_" + material.key() + "_small", material, 4, 0));
        }
        ores.add(new Ore("ore_silver", Material.SILVER, 9, 0.5F));
        ores.add(new Ore("ore_uranium", Material.URANIUM, 9, 0));
        ores.add(new Ore("ore_uranium_buried", Material.URANIUM, 9, 0.5F));
        ores.add(new Ore("ore_iridium_small", Material.IRIDIUM, 4, 0.5F));
        ores.add(new Ore("ore_iridium_large", Material.IRIDIUM, 12, 0.7F));
        ores.add(new Ore("ore_iridium_buried", Material.IRIDIUM, 8, 1));
        return List.copyOf(ores);
    }

    private static List<Placement> placements() {
        List<Placement> placements = new ArrayList<>();
        for (String metal : List.of("tin", "lead", "aluminum", "nickel")) {
            String ore = "ore_" + metal;
            if (!metal.equals("nickel")) {
                placements.add(new Placement(ore + "_upper", ore, CountPlacement.of(90), triangle(80, 384)));
            }
            placements.add(new Placement(ore + "_middle", ore, CountPlacement.of(10), triangle(-24, 56)));
            placements.add(new Placement(ore + "_small", ore + "_small", CountPlacement.of(10), uniform(-64, 72)));
        }
        // Gold's main pass has four attempts per chunk and a vein size of nine.
        placements.add(new Placement("ore_silver", "ore_silver", CountPlacement.of(3), triangle(-32, 15)));
        placements.add(new Placement("ore_uranium", "ore_uranium_buried", CountPlacement.of(2), triangle(-64, 32)));
        placements.add(new Placement(
                "ore_uranium_lower", "ore_uranium_buried", CountPlacement.of(UniformInt.of(0, 1)), uniform(-64, -48)));
        placements.add(new Placement("ore_iridium", "ore_iridium_small", CountPlacement.of(5), triangle(-64, 16)));
        placements.add(new Placement(
                "ore_iridium_large", "ore_iridium_large", RarityFilter.onAverageOnceEvery(12), triangle(-64, 16)));
        placements.add(
                new Placement("ore_iridium_buried", "ore_iridium_buried", CountPlacement.of(3), triangle(-64, 16)));
        return List.copyOf(placements);
    }

    private static void configuredFeatures(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        for (Ore ore : ORES) {
            var stone = MaterialEntries.get(ore.material(), MaterialComponent.STONE_ORE)
                    .block()
                    .get();
            var deepslate = MaterialEntries.get(ore.material(), MaterialComponent.DEEPSLATE_ORE)
                    .block()
                    .get();
            var targets = List.of(
                    OreConfiguration.target(
                            new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES), stone.defaultBlockState()),
                    OreConfiguration.target(
                            new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES), deepslate.defaultBlockState()));
            context.register(
                    configuredKey(ore.name()),
                    new ConfiguredFeature<>(
                            Feature.ORE, new OreConfiguration(targets, ore.size(), ore.airExposureDiscard())));
        }
    }

    private static void placedFeatures(BootstrapContext<PlacedFeature> context) {
        var features = context.lookup(Registries.CONFIGURED_FEATURE);
        for (Placement placement : PLACEMENTS) {
            context.register(
                    placedKey(placement.name()),
                    new PlacedFeature(
                            features.getOrThrow(configuredKey(placement.ore())),
                            List.of(
                                    placement.frequency(),
                                    InSquarePlacement.spread(),
                                    placement.height(),
                                    BiomeFilter.biome())));
        }
    }

    private static void biomeModifiers(BootstrapContext<BiomeModifier> context) {
        var biomes = context.lookup(Registries.BIOME);
        var features = context.lookup(Registries.PLACED_FEATURE);
        context.register(
                ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, FTBIC.id("add_ftbic_ores")),
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                        HolderSet.direct(PLACEMENTS.stream()
                                .map(p -> features.getOrThrow(placedKey(p.name())))
                                .toList()),
                        GenerationStep.Decoration.UNDERGROUND_ORES));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> configuredKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, FTBIC.id(name));
    }

    private static ResourceKey<PlacedFeature> placedKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, FTBIC.id(name));
    }

    private static HeightRangePlacement triangle(int min, int max) {
        return HeightRangePlacement.triangle(VerticalAnchor.absolute(min), VerticalAnchor.absolute(max));
    }

    private static HeightRangePlacement uniform(int min, int max) {
        return HeightRangePlacement.uniform(VerticalAnchor.absolute(min), VerticalAnchor.absolute(max));
    }

    private FTBICWorldgen() {}
}
