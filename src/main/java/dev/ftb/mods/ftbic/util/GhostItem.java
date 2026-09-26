package dev.ftb.mods.ftbic.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

/** An immutable item/count assignment, never inventory contents. */
public record GhostItem(int slot, ItemStackTemplate item) {
    public static final Codec<GhostItem> CODEC = RecordCodecBuilder.create(i -> i.group(
                    Codec.intRange(0, 8).fieldOf("slot").forGetter(GhostItem::slot),
                    ItemStackTemplate.CODEC.fieldOf("item").forGetter(GhostItem::item))
            .apply(i, GhostItem::new));
    public static final Codec<List<GhostItem>> LIST_CODEC = CODEC.listOf(0, 9);

    public static GhostItem of(int slot, ItemStack stack, int count) {
        return new GhostItem(slot, new ItemStackTemplate(stack.typeHolder(), count, stack.getComponentsPatch()));
    }
}
