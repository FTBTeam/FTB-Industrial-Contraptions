package dev.ftb.mods.ftbic.world;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.OreFeatures;
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
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class OreGeneration {
	public static final RegistryObject<ConfiguredFeature<?, ?>> ORE_TIN_CONFIG;
	public static final RegistryObject<ConfiguredFeature<?, ?>> ORE_TIN_SMALL_CONFIG;
	public static final RegistryObject<ConfiguredFeature<?, ?>> ORE_LEAD_CONFIG;
	public static final RegistryObject<ConfiguredFeature<?, ?>> ORE_LEAD_SMALL_CONFIG;
	public static final RegistryObject<ConfiguredFeature<?, ?>> ORE_ALUMINUM_CONFIG;
	public static final RegistryObject<ConfiguredFeature<?, ?>> ORE_ALUMINUM_SMALL_CONFIG;
	public static final RegistryObject<ConfiguredFeature<?, ?>> ORE_URANIUM_CONFIG;
	public static final RegistryObject<ConfiguredFeature<?, ?>> ORE_URANIUM_BURIED_CONFIG;
	public static final RegistryObject<ConfiguredFeature<?, ?>> ORE_IRIDIUM_SMALL_CONFIG;
	public static final RegistryObject<ConfiguredFeature<?, ?>> ORE_IRIDIUM_LARGE_CONFIG;
	public static final RegistryObject<ConfiguredFeature<?, ?>> ORE_IRIDIUM_BURIED_CONFIG;

	public static final RegistryObject<PlacedFeature> ORE_TIN_UPPER;
	public static final RegistryObject<PlacedFeature> ORE_TIN_MIDDLE;
	public static final RegistryObject<PlacedFeature> ORE_TIN_SMALL;
	public static final RegistryObject<PlacedFeature> ORE_LEAD_UPPER;
	public static final RegistryObject<PlacedFeature> ORE_LEAD_MIDDLE;
	public static final RegistryObject<PlacedFeature> ORE_LEAD_SMALL;
	public static final RegistryObject<PlacedFeature> ORE_ALUMINUM_UPPER;
	public static final RegistryObject<PlacedFeature> ORE_ALUMINUM_MIDDLE;
	public static final RegistryObject<PlacedFeature> ORE_ALUMINUM_SMALL;
	public static final RegistryObject<PlacedFeature> ORE_URANIUM_EXTRA;
	public static final RegistryObject<PlacedFeature> ORE_URANIUM;
	public static final RegistryObject<PlacedFeature> ORE_URANIUM_LOWER;
	public static final RegistryObject<PlacedFeature> ORE_IRIDIUM;
	public static final RegistryObject<PlacedFeature> ORE_IRIDIUM_LARGE;
	public static final RegistryObject<PlacedFeature> ORE_IRIDIUM_BURIED;

	public static final Set<RegistryObject<PlacedFeature>> PLACEMENTS = new HashSet<>();

	public static final DeferredRegister<ConfiguredFeature<?, ?>> FEATURE_REGISTRY = DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, FTBIC.MOD_ID);
	public static final DeferredRegister<PlacedFeature> PLACED_FEATURE_REGISTRY = DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, FTBIC.MOD_ID);

	public static void init() {}

	static {
		Supplier<List<OreConfiguration.TargetBlockState>> tinTargetList = () -> List.of(OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, FTBICBlocks.RESOURCE_ORES.get(ResourceElements.TIN).get().defaultBlockState()), OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, FTBICBlocks.RESOURCE_ORES.get(ResourceElements.DEEPSLATE_TIN).get().defaultBlockState()));
		Supplier<List<OreConfiguration.TargetBlockState>> leadTargetList = () -> List.of(OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, FTBICBlocks.RESOURCE_ORES.get(ResourceElements.LEAD).get().defaultBlockState()), OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, FTBICBlocks.RESOURCE_ORES.get(ResourceElements.DEEPSLATE_LEAD).get().defaultBlockState()));
		Supplier<List<OreConfiguration.TargetBlockState>> aluminumTargetList = () -> List.of(OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, FTBICBlocks.RESOURCE_ORES.get(ResourceElements.ALUMINUM).get().defaultBlockState()), OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, FTBICBlocks.RESOURCE_ORES.get(ResourceElements.DEEPSLATE_ALUMINUM).get().defaultBlockState()));
		Supplier<List<OreConfiguration.TargetBlockState>> uraniumTargetList = () -> List.of(OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, FTBICBlocks.RESOURCE_ORES.get(ResourceElements.URANIUM).get().defaultBlockState()), OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, FTBICBlocks.RESOURCE_ORES.get(ResourceElements.DEEPSLATE_URANIUM).get().defaultBlockState()));
		Supplier<List<OreConfiguration.TargetBlockState>> iridiumTargetList = () -> List.of(OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, FTBICBlocks.RESOURCE_ORES.get(ResourceElements.IRIDIUM).get().defaultBlockState()), OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, FTBICBlocks.RESOURCE_ORES.get(ResourceElements.DEEPSLATE_IRIDIUM).get().defaultBlockState()));

		ORE_TIN_CONFIG = FEATURE_REGISTRY.register("ore_tin", () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(tinTargetList.get(), 9)));
		ORE_TIN_SMALL_CONFIG = FEATURE_REGISTRY.register("ore_tin_small", () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(tinTargetList.get(), 4)));
		ORE_LEAD_CONFIG = FEATURE_REGISTRY.register("ore_lead", () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(leadTargetList.get(), 9)));
		ORE_LEAD_SMALL_CONFIG = FEATURE_REGISTRY.register("ore_lead_small", () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(leadTargetList.get(), 4)));
		ORE_ALUMINUM_CONFIG = FEATURE_REGISTRY.register("ore_aluminum", () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(aluminumTargetList.get(), 9)));
		ORE_ALUMINUM_SMALL_CONFIG = FEATURE_REGISTRY.register("ore_aluminum_small", () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(aluminumTargetList.get(), 4)));
		ORE_URANIUM_CONFIG = FEATURE_REGISTRY.register("ore_uranium", () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(uraniumTargetList.get(), 9)));
		ORE_URANIUM_BURIED_CONFIG = FEATURE_REGISTRY.register("ore_uranium_buried", () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(uraniumTargetList.get(), 9, 0.5F)));
		ORE_IRIDIUM_SMALL_CONFIG = FEATURE_REGISTRY.register("ore_iridium_small", () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(iridiumTargetList.get(), 4, 0.5F)));
		ORE_IRIDIUM_LARGE_CONFIG = FEATURE_REGISTRY.register("ore_iridium_large", () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(iridiumTargetList.get(), 12, 0.7F)));
		ORE_IRIDIUM_BURIED_CONFIG = FEATURE_REGISTRY.register("ore_iridium_buried", () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(iridiumTargetList.get(), 8, 1.0F)));

		ORE_TIN_UPPER = PLACED_FEATURE_REGISTRY.register("ore_tin_upper", () -> new PlacedFeature(ORE_TIN_CONFIG.getHolder().get(), commonOrePlacement(90, HeightRangePlacement.triangle(VerticalAnchor.absolute(80), VerticalAnchor.absolute(384)))));
		ORE_TIN_MIDDLE = PLACED_FEATURE_REGISTRY.register("ore_tin_middle", () -> new PlacedFeature(ORE_TIN_CONFIG.getHolder().get(), commonOrePlacement(10, HeightRangePlacement.triangle(VerticalAnchor.absolute(-24), VerticalAnchor.absolute(56)))));
		ORE_TIN_SMALL = PLACED_FEATURE_REGISTRY.register("ore_tin_small", () -> new PlacedFeature(ORE_TIN_SMALL_CONFIG.getHolder().get(), commonOrePlacement(10, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(72)))));
		ORE_LEAD_UPPER = PLACED_FEATURE_REGISTRY.register("ore_lead_upper", () -> new PlacedFeature(ORE_LEAD_CONFIG.getHolder().get(), commonOrePlacement(90, HeightRangePlacement.triangle(VerticalAnchor.absolute(80), VerticalAnchor.absolute(384)))));
		ORE_LEAD_MIDDLE = PLACED_FEATURE_REGISTRY.register("ore_lead_middle", () -> new PlacedFeature(ORE_LEAD_CONFIG.getHolder().get(), commonOrePlacement(10, HeightRangePlacement.triangle(VerticalAnchor.absolute(-24), VerticalAnchor.absolute(56)))));
		ORE_LEAD_SMALL = PLACED_FEATURE_REGISTRY.register("ore_lead_small", () -> new PlacedFeature(ORE_LEAD_SMALL_CONFIG.getHolder().get(), commonOrePlacement(10, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(72)))));
		ORE_ALUMINUM_UPPER = PLACED_FEATURE_REGISTRY.register("ore_aluminum_upper", () -> new PlacedFeature(ORE_ALUMINUM_CONFIG.getHolder().get(), commonOrePlacement(90, HeightRangePlacement.triangle(VerticalAnchor.absolute(80), VerticalAnchor.absolute(384)))));
		ORE_ALUMINUM_MIDDLE = PLACED_FEATURE_REGISTRY.register("ore_aluminum_middle", () -> new PlacedFeature(ORE_ALUMINUM_CONFIG.getHolder().get(), commonOrePlacement(10, HeightRangePlacement.triangle(VerticalAnchor.absolute(-24), VerticalAnchor.absolute(56)))));
		ORE_ALUMINUM_SMALL = PLACED_FEATURE_REGISTRY.register("ore_aluminum_small", () -> new PlacedFeature(ORE_ALUMINUM_SMALL_CONFIG.getHolder().get(), commonOrePlacement(10, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(72)))));
		ORE_URANIUM_EXTRA = PLACED_FEATURE_REGISTRY.register("ore_uranium_extra", () -> new PlacedFeature(ORE_URANIUM_CONFIG.getHolder().get(), commonOrePlacement(50, HeightRangePlacement.uniform(VerticalAnchor.absolute(32), VerticalAnchor.absolute(256)))));
		ORE_URANIUM = PLACED_FEATURE_REGISTRY.register("ore_uranium", () -> new PlacedFeature(ORE_URANIUM_BURIED_CONFIG.getHolder().get(), commonOrePlacement(4, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(32)))));
		ORE_URANIUM_LOWER = PLACED_FEATURE_REGISTRY.register("ore_uranium_lower", () -> new PlacedFeature(ORE_URANIUM_BURIED_CONFIG.getHolder().get(), orePlacement(CountPlacement.of(UniformInt.of(0, 1)), HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(-48)))));
		ORE_IRIDIUM = PLACED_FEATURE_REGISTRY.register("ore_iridium", () -> new PlacedFeature(ORE_IRIDIUM_SMALL_CONFIG.getHolder().get(), commonOrePlacement(7, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80)))));
		ORE_IRIDIUM_LARGE = PLACED_FEATURE_REGISTRY.register("ore_iridium_large", () -> new PlacedFeature(ORE_IRIDIUM_LARGE_CONFIG.getHolder().get(), rareOrePlacement(9, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80)))));
		ORE_IRIDIUM_BURIED = PLACED_FEATURE_REGISTRY.register("ore_iridium_buried", () -> new PlacedFeature(ORE_IRIDIUM_BURIED_CONFIG.getHolder().get(), commonOrePlacement(4, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80)))));

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
