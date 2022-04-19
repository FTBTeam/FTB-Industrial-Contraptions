package dev.ftb.mods.ftbic.world;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ResourceElements {
    TIN,
    DEEPSLATE_TIN(Requirements.builder().add(ResourceType.ORE)),
    LEAD,
    DEEPSLATE_LEAD(Requirements.builder().add(ResourceType.ORE)),
    URANIUM,
    DEEPSLATE_URANIUM(Requirements.builder().add(ResourceType.ORE)),
    IRIDIUM,
    DEEPSLATE_IRIDIUM(Requirements.builder().add(ResourceType.ORE)),
    ALUMINUM,
    DEEPSLATE_ALUMINUM(Requirements.builder().add(ResourceType.ORE)),
    ENDERIUM(Requirements.builder().all().remove(ResourceType.ORE).remove(ResourceType.CHUNK)),
	DIAMOND(Requirements.builder().add(ResourceType.DUST)),
	IRON(Requirements.builder().add(ResourceType.ROD).add(ResourceType.DUST).add(ResourceType.PLATE)),
	COPPER(Requirements.builder().add(ResourceType.PLATE).add(ResourceType.DUST)),
	GOLD(Requirements.builder().add(ResourceType.DUST));

    public static final List<ResourceElements> VALUES = Arrays.stream(ResourceElements.values()).toList();

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
        return name().toLowerCase();
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

		/**
		 * OR every resource, so we have them all, simple math 1 + 2 + 4 + 8 + 16 + 32 + 64 + 128 = 255 (8 bits)
		 */
		public Requirements all() {
			this.requirements = ResourceType.ORE.bit | ResourceType.INGOT.bit | ResourceType.CHUNK.bit | ResourceType.DUST.bit | ResourceType.NUGGET.bit | ResourceType.PLATE.bit | ResourceType.ROD.bit | ResourceType.GEAR.bit | ResourceType.BLOCKOF.bit;
			return this;
		}

		/**
		 * OR the bit of the type
		 */
		public Requirements add(ResourceType type) {
			this.requirements |= type.bit;
			return this;
		}

		/**
		 * NOT the bit of the type to remove it
		 */
		public Requirements remove(ResourceType type) {
			this.requirements &= ~type.bit;
			return this;
		}
	}
}
