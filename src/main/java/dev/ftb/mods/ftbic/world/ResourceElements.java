package dev.ftb.mods.ftbic.world;


import java.util.Arrays;
import java.util.List;

public enum ResourceElements {
    TIN,
    DEEPSLATE_TIN,
    LEAD,
    DEEPSLATE_LEAD,
    URANIUM,
    DEEPSLATE_URANIUM,
    IRIDIUM,
    DEEPSLATE_IRIDIUM,
    ALUMINUM,
    DEEPSLATE_ALUMINUM,
    ENDERIUM(false, false);

    public static final List<ResourceElements> VALUES = Arrays.stream(ResourceElements.values()).toList();
    public static final List<ResourceElements> NO_DEEPSLATE_VALUES = VALUES.stream().filter(e -> !e.getName().contains("deepslate_")).toList();

    private final boolean hasOre;
    private final boolean hasChunk;
    ResourceElements(boolean hasOre, boolean hasChunk) {
        this.hasOre = hasOre;
        this.hasChunk = hasChunk;
    }

    ResourceElements() {
        this(true, true);
    }

    public String getName() {
        return name().toLowerCase();
    }

    public boolean hasOre() {
        return hasOre;
    }

    public boolean hasChunk() {
        return hasChunk;
    }
}
