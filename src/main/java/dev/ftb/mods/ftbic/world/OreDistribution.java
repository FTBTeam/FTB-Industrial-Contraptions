package dev.ftb.mods.ftbic.world;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class OreDistribution {
	private final List<Pair<String, Pair<Placement, Holder<ConfiguredFeature<OreConfiguration, ?>>>>> placements;

	Pair<Placement, Holder<ConfiguredFeature<OreConfiguration, ?>>> normal;
	Pair<Placement, Holder<ConfiguredFeature<OreConfiguration, ?>>> large;
	Pair<Placement, Holder<ConfiguredFeature<OreConfiguration, ?>>> small;
	Pair<Placement, Holder<ConfiguredFeature<OreConfiguration, ?>>> buried;

	public OreDistribution(Pair<Placement, Holder<ConfiguredFeature<OreConfiguration, ?>>> normal, Pair<Placement, Holder<ConfiguredFeature<OreConfiguration, ?>>> large, Pair<Placement, Holder<ConfiguredFeature<OreConfiguration, ?>>> small, Pair<Placement, Holder<ConfiguredFeature<OreConfiguration, ?>>> buried) {
		this.normal = normal;
		this.large = large;
		this.small = small;
		this.buried = buried;

		placements = List.of(Pair.of("normal", this.normal), Pair.of("large", this.large), Pair.of("buried", this.buried), Pair.of("small", this.small));
	}

	public List<Pair<String, Pair<Placement, Holder<ConfiguredFeature<OreConfiguration, ?>>>>> getSetPlacements() {
		return placements.stream().filter(e -> e.getRight().getLeft() != null).toList();
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private OreDistribution.Placement normal;
		private OreDistribution.Placement large;
		private OreDistribution.Placement small;
		private OreDistribution.Placement buried;
		private Holder<ConfiguredFeature<OreConfiguration, ?>> normalFeature;
		private Holder<ConfiguredFeature<OreConfiguration, ?>> largeFeature;
		private Holder<ConfiguredFeature<OreConfiguration, ?>> smallFeature;
		private Holder<ConfiguredFeature<OreConfiguration, ?>> buriedFeature;

		public Builder setNormal(OreDistribution.Placement normal, Holder<ConfiguredFeature<OreConfiguration, ?>> featureHolder) {
			this.normal = normal;
			this.normalFeature = featureHolder;
			return this;
		}

		public Builder setLarge(OreDistribution.Placement large, Holder<ConfiguredFeature<OreConfiguration, ?>> featureHolder) {
			this.large = large;
			this.largeFeature = featureHolder;
			return this;
		}

		public Builder setSmall(OreDistribution.Placement small, Holder<ConfiguredFeature<OreConfiguration, ?>> featureHolder) {
			this.small = small;
			this.smallFeature = featureHolder;
			return this;
		}

		public Builder setBuried(OreDistribution.Placement buried, Holder<ConfiguredFeature<OreConfiguration, ?>> featureHolder) {
			this.buried = buried;
			this.buriedFeature = featureHolder;
			return this;
		}

		public OreDistribution build() {
			return new OreDistribution(Pair.of(normal, normalFeature), Pair.of(large, largeFeature), Pair.of(small, smallFeature), Pair.of(buried, buriedFeature));
		}
	}

	public record Placement(Rarity rarity, int count, PlacementModifier placementType) {
		public static Builder builder() {
			return new Builder();
		}

		public static final class Builder {
			private int count;
			private Rarity rarity;
			private PlacementModifier placementType;

			public Builder() {
				this.count = 90;
				this.rarity = Rarity.COMMON;
				this.placementType = HeightRangePlacement.triangle(VerticalAnchor.absolute(80), VerticalAnchor.absolute(384));
			}

			public Builder rare() {
				this.rarity = Rarity.RARE;
				return this;
			}

			public Builder common() {
				this.rarity = Rarity.COMMON;
				return this;
			}

			public Builder count(int count) {
				this.count = count;
				return this;
			}

			public Builder triangle(int low, int high) {
				this.placementType = HeightRangePlacement.triangle(VerticalAnchor.absolute(low), VerticalAnchor.absolute(high));
				return this;
			}

			public Builder triangleAboveBottom(int low, int high) {
				this.placementType = HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(low), VerticalAnchor.aboveBottom(high));
				return this;
			}

			public Builder uniform(int low, int high) {
				this.placementType = HeightRangePlacement.uniform(VerticalAnchor.absolute(low), VerticalAnchor.absolute(high));
				return this;
			}

			public Builder uniformAboveBottom(int low, int high) {
				this.placementType = HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(low), VerticalAnchor.aboveBottom(high));
				return this;
			}

			public Builder customPlacement(PlacementModifier placement) {
				this.placementType = placement;
				return this;
			}

			public OreDistribution.Placement build() {
				return new OreDistribution.Placement(rarity, count, placementType);
			}
		}
	}

	public enum Rarity {
		COMMON,
		RARE;
	}
}
