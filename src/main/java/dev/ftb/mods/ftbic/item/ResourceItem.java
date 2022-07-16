package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.world.ResourceType;
import net.minecraft.world.item.Item;

public class ResourceItem extends Item {
    private final ResourceType type;

    public ResourceItem(ResourceType type) {
        super(new Properties().tab(FTBIC.TAB));
        this.type = type;
    }
}
