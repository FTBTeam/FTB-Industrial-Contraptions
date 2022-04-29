package dev.ftb.mods.ftbic.world;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.world.OreDistribution.Placement;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceLocation;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OreGeneration {
	public static final List<OreConfiguration.TargetBlockState> TIN_TARGET_LIST = List.of(OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, FTBICBlocks.RESOURCE_ORES.get(ResourceElements.TIN).get().defaultBlockState()), OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, FTBICBlocks.RESOURCE_ORES.get(ResourceElements.DEEPSLATE_TIN).get().defaultBlockState()));
	public static final List<OreConfiguration.TargetBlockState> LEAD_TARGET_LIST = List.of(OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, FTBICBlocks.RESOURCE_ORES.get(ResourceElements.LEAD).get().defaultBlockState()), OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, FTBICBlocks.RESOURCE_ORES.get(ResourceElements.DEEPSLATE_LEAD).get().defaultBlockState()));
	public static final List<OreConfiguration.TargetBlockState> ALUMINUM_TARGET_LIST = List.of(OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, FTBICBlocks.RESOURCE_ORES.get(ResourceElements.ALUMINUM).get().defaultBlockState()), OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, FTBICBlocks.RESOURCE_ORES.get(ResourceElements.DEEPSLATE_ALUMINUM).get().defaultBlockState()));
	public static final List<OreConfiguration.TargetBlockState> URANIUM_TARGET_LIST = List.of(OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, FTBICBlocks.RESOURCE_ORES.get(ResourceElements.URANIUM).get().defaultBlockState()), OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, FTBICBlocks.RESOURCE_ORES.get(ResourceElements.DEEPSLATE_URANIUM).get().defaultBlockState()));
	public static final List<OreConfiguration.TargetBlockState> IRIDIUM_TARGET_LIST = List.of(OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, FTBICBlocks.RESOURCE_ORES.get(ResourceElements.IRIDIUM).get().defaultBlockState()), OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, FTBICBlocks.RESOURCE_ORES.get(ResourceElements.DEEPSLATE_IRIDIUM).get().defaultBlockState()));

	private static final Holder<ConfiguredFeature<OreConfiguration, ?>> TIN = createOreConfig("tin", new OreConfiguration(TIN_TARGET_LIST, 9));
	private static final Holder<ConfiguredFeature<OreConfiguration, ?>> LEAD = createOreConfig("lead", new OreConfiguration(LEAD_TARGET_LIST, 9));
	private static final Holder<ConfiguredFeature<OreConfiguration, ?>> ALUMINUM = createOreConfig("aluminum", new OreConfiguration(ALUMINUM_TARGET_LIST, 9));
	private static final Holder<ConfiguredFeature<OreConfiguration, ?>> IRIDIUM = createOreConfig("iridium", new OreConfiguration(IRIDIUM_TARGET_LIST, 9));

	private static final Map<ResourceElements, OreDistribution> PLACEMENT_DISTRIBUTIONS = new HashMap<>() {{
		put(ResourceElements.TIN, OreDistribution.builder()
				.setSmall(Placement.builder().count(10).common().customPlacement(HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(72))).build(), TIN)
				.setNormal(Placement.builder().count(10).common().triangle(-24, 56).build(), TIN)
				.setLarge(Placement.builder().count(90).common().triangle(80, 384).build(), createOreConfig("tin_small", new OreConfiguration(TIN_TARGET_LIST, 4)))
				.build());

		put(ResourceElements.LEAD, OreDistribution.builder()
				.setSmall(Placement.builder().count(10).common().customPlacement(HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(72))).build(), LEAD)
				.setNormal(Placement.builder().count(10).common().triangle(-24, 56).build(), LEAD)
				.setLarge(Placement.builder().count(90).common().triangle(80, 384).build(), createOreConfig("lead_small", new OreConfiguration(LEAD_TARGET_LIST, 4)))
				.build());

		put(ResourceElements.ALUMINUM, OreDistribution.builder()
				.setSmall(Placement.builder().count(10).common().customPlacement(HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(72))).build(), ALUMINUM)
				.setNormal(Placement.builder().count(10).common().triangle(-24, 56).build(), ALUMINUM)
				.setLarge(Placement.builder().count(90).common().triangle(80, 384).build(), createOreConfig("aluminum_small", new OreConfiguration(ALUMINUM_TARGET_LIST, 4)))
				.build());

		put(ResourceElements.URANIUM, OreDistribution.builder()
				.setSmall(Placement.builder().count(50).common().uniform(32, 256).build(), createOreConfig("uranium_small", new OreConfiguration(URANIUM_TARGET_LIST, 4, 0.5f)))
				.setLarge(Placement.builder().count(4).common().triangle(-64, 32).build(), createOreConfig("uranium_large", new OreConfiguration(URANIUM_TARGET_LIST, 12, 0.7f)))
				.setBuried(Placement.builder().count(8).common().triangle(-32, 32).build(), createOreConfig("uranium_buried", new OreConfiguration(URANIUM_TARGET_LIST, 8, 1.0f)))
				.build());

		put(ResourceElements.IRIDIUM, OreDistribution.builder()
				.setSmall(Placement.builder().count(7).common().triangleAboveBottom(-80, 80).build(), IRIDIUM)
				.setLarge(Placement.builder().count(9).rare().triangleAboveBottom(-80, 80).build(), IRIDIUM)
				.setBuried(Placement.builder().count(4).common().triangleAboveBottom(-80, 80).build(), createOreConfig("iridium_buried", new OreConfiguration(IRIDIUM_TARGET_LIST, 9, 0.5f)))
				.build());
	}};

	private static Holder<ConfiguredFeature<OreConfiguration, ?>> createOreConfig(String id, OreConfiguration config) {
		return FeatureUtils.register(new ResourceLocation(FTBIC.MOD_ID, id).toString(), Feature.ORE, config);
	}

	public static final Map<ResourceElements, List<Holder<PlacedFeature>>> PLACEMENT_FEATURES = new HashMap<>();

	public static void setupConfiguredFeatures() {
		FTBICBlocks.RESOURCE_ORES.keySet().forEach((v) -> {
			OreDistribution oreDistribution = PLACEMENT_DISTRIBUTIONS.get(ResourceElements.getNonDeepslateVersion(v).orElseThrow());
			List<Holder<PlacedFeature>> placementFeatures = oreDistribution.getSetPlacements().stream()
					.map(e -> {
						Placement placement = e.getValue().getKey();
						var modifiers = placement.rarity() == OreDistribution.Rarity.COMMON ? rareOrePlacement(placement.count(), placement.placementType()) : commonOrePlacement(placement.count(), placement.placementType());
						Holder<PlacedFeature> register = PlacementUtils.register(new ResourceLocation(FTBIC.MOD_ID, "overwold_" + v.getName() + "_" + e.getKey()).toString(), e.getValue().getValue(), modifiers);
						return register;
					})
					.toList();

			PLACEMENT_FEATURES.put(v, placementFeatures);
		});
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
