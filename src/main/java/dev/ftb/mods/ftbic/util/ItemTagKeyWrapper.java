package dev.ftb.mods.ftbic.util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class ItemTagKeyWrapper {
    private final TagKey<Item> tag;

    private ItemTagKeyWrapper(ResourceLocation name) {
        this.tag = TagKey.create(Registry.ITEM_REGISTRY, name);
    }

    public static ItemTagKeyWrapper create(ResourceLocation tag) {
        return new ItemTagKeyWrapper(tag);
    }

    public TagKey<Item> getTag() {
        return tag;
    }

    public List<Item> getValues() {
        return StreamSupport.stream(Registry.ITEM.getTagOrEmpty(this.tag).spliterator(), false)
                .map(e -> e.unwrap().right())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }
}
