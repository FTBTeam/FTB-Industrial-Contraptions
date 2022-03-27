package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.world.ResourceElementTypes;
import net.minecraft.world.item.Item;

public class ResourceItem extends Item {
    private final ResourceElementTypes type;

    public ResourceItem(ResourceElementTypes type) {
        super(new Properties().stacksTo(1).tab(FTBIC.TAB));
        this.type = type;
    }
}
