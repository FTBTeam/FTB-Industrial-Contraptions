package dev.ftb.mods.ftbic.world;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static dev.ftb.mods.ftbic.world.ResourceType.*;

public enum ResourceElements {
	TIN(Requirements.builder().all().remove(WIRE)),
	LEAD(Requirements.builder().all().remove(WIRE)),
	URANIUM(Requirements.builder().all().remove(WIRE)),
	IRIDIUM(Requirements.builder().all().remove(WIRE)),
	ALUMINUM,
	DEEPSLATE_TIN(Requirements.builder().add(ORE)),
	DEEPSLATE_LEAD(Requirements.builder().add(ORE)),
	DEEPSLATE_URANIUM(Requirements.builder().add(ORE)),
	DEEPSLATE_IRIDIUM(Requirements.builder().add(ORE)),
	DEEPSLATE_ALUMINUM(Requirements.builder().add(ORE)),
	ENDERIUM(Requirements.builder().all().remove(ORE, RAW)),
	DIAMOND(Requirements.builder().add(DUST)),
	BRONZE(Requirements.builder().all().remove(ORE, RAW, WIRE)),
	IRON(Requirements.builder().all().remove(ORE, INGOT, BLOCK, RAW, NUGGET, WIRE)),
	COPPER(Requirements.builder().all().remove(ORE, INGOT, BLOCK, RAW)),
	GOLD(Requirements.builder().all().remove(ORE, INGOT, BLOCK, RAW, NUGGET)),
	OBSIDIAN(Requirements.builder().add(DUST)),
	ENDER(Requirements.builder().add(DUST)),
	COAL(Requirements.builder().add(DUST)),
	CHARCOAL(Requirements.builder().add(DUST));

	public static final List<ResourceElements> VALUES = Arrays.stream(values()).sorted(Comparator.comparing(ResourceElements::getName)).toList();

	public static final Map<ResourceType, List<ResourceElements>> RESOURCES_BY_REQUIREMENT = ResourceType.VALUES.stream()
			.collect(Collectors.toMap(Function.identity(), e -> VALUES.stream().filter(a -> a.requirements.has(e)).collect(Collectors.toList())));

	private final Requirements requirements;

	ResourceElements(Requirements requirements) {
		this.requirements = requirements;
	}

	ResourceElements() {
		this(Requirements.builder().all());
	}

	public String getName() {
		return name().toLowerCase(Locale.ENGLISH);
	}

	public Requirements requirements() {
		return requirements;
	}

	public static Optional<ResourceElements> getNonDeepslateVersion(ResourceElements element) {
		var name = element.getName().replace("deepslate_", "");
		return VALUES.stream().filter(e -> e.getName().equals(name)).findFirst();
	}

	public static class Requirements {
		private int requirements = 0;

		private Requirements() {
		}

		public boolean has(ResourceType type) {
			return (requirements & type.bit) != 0;
		}

		public static Requirements builder() {
			return new Requirements();
		}

		public Requirements all() {
			this.requirements = -1;
			return this;
		}

		public Requirements add(ResourceType... type) {
			for (ResourceType resourceType : type) {
				this.requirements |= resourceType.bit;
			}
			return this;
		}

		public Requirements remove(ResourceType... type) {
			for (ResourceType resourceType : type) {
				this.requirements &= ~resourceType.bit;
			}
			return this;
		}
	}
}
