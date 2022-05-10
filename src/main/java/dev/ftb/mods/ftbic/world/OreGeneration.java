package dev.ftb.mods.ftbic.world;

import dev.ftb.mods.ftbic.block.FTBICBlocks;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.util.valueproviders.UniformInt;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OreGeneration {
	public static final List<OreConfiguration.TargetBlockState> TIN_TARGET_LIST;
	public static final List<OreConfiguration.TargetBlockState> LEAD_TARGET_LIST;
	public static final List<OreConfiguration.TargetBlockState> ALUMINUM_TARGET_LIST;
	public static final List<OreConfiguration.TargetBlockState> URANIUM_TARGET_LIST;
	public static final List<OreConfiguration.TargetBlockState> IRIDIUM_TARGET_LIST;

	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> ORE_TIN_CONFIG;
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> ORE_TIN_SMALL_CONFIG;
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> ORE_LEAD_CONFIG;
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> ORE_LEAD_SMALL_CONFIG;
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> ORE_ALUMINUM_CONFIG;
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> ORE_ALUMINUM_SMALL_CONFIG;
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> ORE_URANIUM_CONFIG;
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> ORE_URANIUM_BURIED_CONFIG;
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> ORE_IRIDIUM_SMALL_CONFIG;
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> ORE_IRIDIUM_LARGE_CONFIG;
	public static final Holder<ConfiguredFeature<OreConfiguration, ?>> ORE_IRIDIUM_BURIED_CONFIG;

	public static final Holder<PlacedFeature> ORE_TIN_UPPER;
	public static final Holder<PlacedFeature> ORE_TIN_MIDDLE;
	public static final Holder<PlacedFeature> ORE_TIN_SMALL;
	public static final Holder<PlacedFeature> ORE_LEAD_UPPER;
	public static final Holder<PlacedFeature> ORE_LEAD_MIDDLE;
	public static final Holder<PlacedFeature> ORE_LEAD_SMALL;
	public static final Holder<PlacedFeature> ORE_ALUMINUM_UPPER;
	public static final Holder<PlacedFeature> ORE_ALUMINUM_MIDDLE;
	public static final Holder<PlacedFeature> ORE_ALUMINUM_SMALL;
	public static final Holder<PlacedFeature> ORE_URANIUM_EXTRA;
	public static final Holder<PlacedFeature> ORE_URANIUM;
	public static final Holder<PlacedFeature> ORE_URANIUM_LOWER;
	public static final Holder<PlacedFeature> ORE_IRIDIUM;
	public static final Holder<PlacedFeature> ORE_IRIDIUM_LARGE;
	public static final Holder<PlacedFeature> ORE_IRIDIUM_BURIED;

	public static final Set<Holder<PlacedFeature>> PLACEMENTS = new HashSet<>();

	public static void init() {}

	static {
		TIN_TARGET_LIST = List.of(OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, FTBICBlocks.RESOURCE_ORES.get(ResourceElements.TIN).get().defaultBlockState()), OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, FTBICBlocks.RESOURCE_ORES.get(ResourceElements.DEEPSLATE_TIN).get().defaultBlockState()));
		LEAD_TARGET_LIST = List.of(OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, FTBICBlocks.RESOURCE_ORES.get(ResourceElements.LEAD).get().defaultBlockState()), OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, FTBICBlocks.RESOURCE_ORES.get(ResourceElements.DEEPSLATE_LEAD).get().defaultBlockState()));
		ALUMINUM_TARGET_LIST = List.of(OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, FTBICBlocks.RESOURCE_ORES.get(ResourceElements.ALUMINUM).get().defaultBlockState()), OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, FTBICBlocks.RESOURCE_ORES.get(ResourceElements.DEEPSLATE_ALUMINUM).get().defaultBlockState()));
		URANIUM_TARGET_LIST = List.of(OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, FTBICBlocks.RESOURCE_ORES.get(ResourceElements.URANIUM).get().defaultBlockState()), OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, FTBICBlocks.RESOURCE_ORES.get(ResourceElements.DEEPSLATE_URANIUM).get().defaultBlockState()));
		IRIDIUM_TARGET_LIST = List.of(OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, FTBICBlocks.RESOURCE_ORES.get(ResourceElements.IRIDIUM).get().defaultBlockState()), OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, FTBICBlocks.RESOURCE_ORES.get(ResourceElements.DEEPSLATE_IRIDIUM).get().defaultBlockState()));

		ORE_TIN_CONFIG = FeatureUtils.register("ore_tin", Feature.ORE, new OreConfiguration(TIN_TARGET_LIST, 9));
		ORE_TIN_SMALL_CONFIG = FeatureUtils.register("ore_tin_small", Feature.ORE, new OreConfiguration(TIN_TARGET_LIST, 4));
		ORE_LEAD_CONFIG = FeatureUtils.register("ore_lead", Feature.ORE, new OreConfiguration(LEAD_TARGET_LIST, 9));
		ORE_LEAD_SMALL_CONFIG = FeatureUtils.register("ore_lead_small", Feature.ORE, new OreConfiguration(LEAD_TARGET_LIST, 4));
		ORE_ALUMINUM_CONFIG = FeatureUtils.register("ore_aluminum", Feature.ORE, new OreConfiguration(ALUMINUM_TARGET_LIST, 9));
		ORE_ALUMINUM_SMALL_CONFIG = FeatureUtils.register("ore_aluminum_small", Feature.ORE, new OreConfiguration(ALUMINUM_TARGET_LIST, 4));
		ORE_URANIUM_CONFIG = FeatureUtils.register("ore_uranium", Feature.ORE, new OreConfiguration(URANIUM_TARGET_LIST, 9));
		ORE_URANIUM_BURIED_CONFIG = FeatureUtils.register("ore_uranium_buried", Feature.ORE, new OreConfiguration(URANIUM_TARGET_LIST, 9, 0.5F));
		ORE_IRIDIUM_SMALL_CONFIG = FeatureUtils.register("ore_iridium_small", Feature.ORE, new OreConfiguration(IRIDIUM_TARGET_LIST, 4, 0.5F));
		ORE_IRIDIUM_LARGE_CONFIG = FeatureUtils.register("ore_iridium_large", Feature.ORE, new OreConfiguration(IRIDIUM_TARGET_LIST, 12, 0.7F));
		ORE_IRIDIUM_BURIED_CONFIG = FeatureUtils.register("ore_iridium_buried", Feature.ORE, new OreConfiguration(IRIDIUM_TARGET_LIST, 8, 1.0F));

		ORE_TIN_UPPER = PlacementUtils.register("ore_tin_upper", ORE_TIN_CONFIG, commonOrePlacement(90, HeightRangePlacement.triangle(VerticalAnchor.absolute(80), VerticalAnchor.absolute(384))));
		ORE_TIN_MIDDLE = PlacementUtils.register("ore_tin_middle", ORE_TIN_CONFIG, commonOrePlacement(10, HeightRangePlacement.triangle(VerticalAnchor.absolute(-24), VerticalAnchor.absolute(56))));
		ORE_TIN_SMALL = PlacementUtils.register("ore_tin_small", ORE_TIN_SMALL_CONFIG, commonOrePlacement(10, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(72))));
		ORE_LEAD_UPPER = PlacementUtils.register("ore_lead_upper", ORE_LEAD_CONFIG, commonOrePlacement(90, HeightRangePlacement.triangle(VerticalAnchor.absolute(80), VerticalAnchor.absolute(384))));
		ORE_LEAD_MIDDLE = PlacementUtils.register("ore_lead_middle", ORE_LEAD_CONFIG, commonOrePlacement(10, HeightRangePlacement.triangle(VerticalAnchor.absolute(-24), VerticalAnchor.absolute(56))));
		ORE_LEAD_SMALL = PlacementUtils.register("ore_lead_small", ORE_LEAD_SMALL_CONFIG, commonOrePlacement(10, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(72))));
		ORE_ALUMINUM_UPPER = PlacementUtils.register("ore_aluminum_upper", ORE_ALUMINUM_CONFIG, commonOrePlacement(90, HeightRangePlacement.triangle(VerticalAnchor.absolute(80), VerticalAnchor.absolute(384))));
		ORE_ALUMINUM_MIDDLE = PlacementUtils.register("ore_aluminum_middle", ORE_ALUMINUM_CONFIG, commonOrePlacement(10, HeightRangePlacement.triangle(VerticalAnchor.absolute(-24), VerticalAnchor.absolute(56))));
		ORE_ALUMINUM_SMALL = PlacementUtils.register("ore_aluminum_small", ORE_ALUMINUM_SMALL_CONFIG, commonOrePlacement(10, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(72))));
		ORE_URANIUM_EXTRA = PlacementUtils.register("ore_uranium_extra", ORE_URANIUM_CONFIG, commonOrePlacement(50, HeightRangePlacement.uniform(VerticalAnchor.absolute(32), VerticalAnchor.absolute(256))));
		ORE_URANIUM = PlacementUtils.register("ore_uranium", ORE_URANIUM_BURIED_CONFIG, commonOrePlacement(4, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(32))));
		ORE_URANIUM_LOWER = PlacementUtils.register("ore_uranium_lower", ORE_URANIUM_BURIED_CONFIG, orePlacement(CountPlacement.of(UniformInt.of(0, 1)), HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(-48))));
		ORE_IRIDIUM = PlacementUtils.register("ore_iridium", ORE_IRIDIUM_SMALL_CONFIG, commonOrePlacement(7, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80))));
		ORE_IRIDIUM_LARGE = PlacementUtils.register("ore_iridium_large", ORE_IRIDIUM_LARGE_CONFIG, rareOrePlacement(9, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80))));
		ORE_IRIDIUM_BURIED = PlacementUtils.register("ore_iridium_buried", ORE_IRIDIUM_BURIED_CONFIG, commonOrePlacement(4, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80))));

		PLACEMENTS.addAll(List.of(ORE_TIN_UPPER, ORE_TIN_MIDDLE, ORE_TIN_SMALL, ORE_LEAD_UPPER, ORE_LEAD_MIDDLE, ORE_LEAD_SMALL, ORE_ALUMINUM_UPPER, ORE_ALUMINUM_MIDDLE, ORE_ALUMINUM_SMALL, ORE_URANIUM_EXTRA, ORE_URANIUM, ORE_URANIUM_LOWER, ORE_IRIDIUM, ORE_IRIDIUM_LARGE, ORE_IRIDIUM_BURIED));
	}

	/**
	 * Stolen from Minecraft
	 */
	private static List<PlacementModifier> orePlacement(PlacementModifier modifier, PlacementModifier modifier2) {
		return List.of(modifier, InSquarePlacement.spread(), modifier2, BiomeFilter.biome());
	}

	private static List<PlacementModifier> commonOrePlacement(int count, PlacementModifier modifier) {
		return orePlacement(CountPlacement.of(count), modifier);
	}

	private static List<PlacementModifier> rareOrePlacement(int count, PlacementModifier modifier) {
		return orePlacement(RarityFilter.onAverageOnceEvery(count), modifier);
	}
}
