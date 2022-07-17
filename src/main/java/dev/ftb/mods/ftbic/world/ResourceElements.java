package dev.ftb.mods.ftbic.world;

import java.util.Arrays;
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
    ENDERIUM(Requirements.builder().all().remove(ORE, CHUNK)),
	DIAMOND(Requirements.builder().add(DUST)),
	BRONZE(Requirements.builder().all().remove(ORE, CHUNK, WIRE)),
	IRON(Requirements.builder().all().remove(ORE, INGOT, BLOCK, CHUNK, NUGGET, WIRE)),
	COPPER(Requirements.builder().all().remove(ORE, INGOT, BLOCK, CHUNK)),
	GOLD(Requirements.builder().all().remove(ORE, INGOT, BLOCK, CHUNK, NUGGET)),
	OBSIDIAN(Requirements.builder().add(DUST)),
	ENDER(Requirements.builder().add(DUST)),
	COAL(Requirements.builder().add(DUST)),
	CHARCOAL(Requirements.builder().add(DUST));

    public static final List<ResourceElements> VALUES = Arrays.asList(values());

	/**
	 * Creates a map of the resource types which link together the elements that the resource contains.
	 * For example, if you get the {@link ResourceType#ORE} from the list, you will not get an ore version of {@link ResourceElements#ENDERIUM}
	 */
	public static final Map<ResourceType, List<ResourceElements>> RESOURCES_BY_REQUIREMENT = ResourceType.VALUES.stream().collect(Collectors.toMap(Function.identity(), e -> VALUES.stream().filter(a -> a.requirements.has(e)).collect(Collectors.toList())));

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

	/**
	 * Accepts a resource element type then gives back the non-deepslate version. This method is safe to use on
	 * non-deepslated types as it'll just find itself.
	 */
	public static Optional<ResourceElements> getNonDeepslateVersion(ResourceElements element) {
		var name = element.getName().replace("deepslate_", "");

		return VALUES.stream().filter(e -> e.getName().equals(name)).findFirst();
	}

	/**
	 * Stores the requirements of the resource, you can use this to remove and add different resources.
	 * Use an 8 bit space to store all the resource requirements, this is kind overkill... but I kinda love it.
	 *
	 * @implNote defaults to no requirements! We also don't allow users to do this as OR order is important here.
	 */
	public static class Requirements {
		private int requirements = 0; // Start with nothing

		private Requirements() {
		}

		/**
		 * Use AND to check if we have the correct bite
		 */
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

		/**
		 * OR the bit of the type
		 */
		public Requirements add(ResourceType... type) {
			for (ResourceType resourceType : type) {
				this.requirements |= resourceType.bit;
			}
			return this;
		}

		/**
		 * NOT the bit of the type to remove it
		 */
		public Requirements remove(ResourceType... type) {
			for (ResourceType resourceType : type) {
				this.requirements &= ~resourceType.bit;
			}
			return this;
		}
	}
}
