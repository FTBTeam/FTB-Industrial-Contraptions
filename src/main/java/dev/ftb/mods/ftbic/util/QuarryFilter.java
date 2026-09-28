package dev.ftb.mods.ftbic.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** Item samples select placed block types; tag names select block tags. */
public record QuarryFilter(List<GhostItem> blocks, List<String> tags, boolean whitelist, boolean oreOnly) {
    public static final int BLOCK_SLOTS = 9;
    public static final int TAG_SLOTS = 3;
    public static final QuarryFilter DEFAULT = new QuarryFilter(List.of(), List.of("", "", ""), true, false);
    public static final Codec<QuarryFilter> CODEC = RecordCodecBuilder.create(i -> i.group(
                    GhostItem.LIST_CODEC.optionalFieldOf("blocks", List.of()).forGetter(QuarryFilter::blocks),
                    Codec.STRING
                            .listOf(0, TAG_SLOTS)
                            .optionalFieldOf("tags", List.of())
                            .forGetter(QuarryFilter::tags),
                    Codec.BOOL.optionalFieldOf("whitelist", true).forGetter(QuarryFilter::whitelist),
                    Codec.BOOL.optionalFieldOf("ore_only", false).forGetter(QuarryFilter::oreOnly))
            .apply(i, QuarryFilter::new));

    public QuarryFilter {
        blocks = List.copyOf(blocks);
        var normalized = new ArrayList<String>(TAG_SLOTS);
        for (int i = 0; i < TAG_SLOTS; i++) normalized.add(i < tags.size() ? tags.get(i) : "");
        tags = List.copyOf(normalized);
    }

    public ItemStack blockAt(int slot) {
        for (GhostItem entry : blocks)
            if (entry.slot() == slot) return entry.item().create();
        return ItemStack.EMPTY;
    }

    public QuarryFilter withBlock(int slot, ItemStack stack) {
        if (slot < 0 || slot >= BLOCK_SLOTS || !stack.isEmpty() && !(stack.getItem() instanceof BlockItem)) return this;
        var entries = new ArrayList<>(blocks);
        entries.removeIf(entry -> entry.slot() == slot);
        if (!stack.isEmpty()) entries.add(GhostItem.of(slot, stack, 1));
        return new QuarryFilter(entries, tags, whitelist, oreOnly);
    }

    public QuarryFilter withTag(int slot, String value) {
        if (slot < 0 || slot >= TAG_SLOTS) return this;
        var entries = new ArrayList<>(tags);
        entries.set(slot, value);
        return new QuarryFilter(blocks, entries, whitelist, oreOnly);
    }

    public QuarryFilter withWhitelist(boolean enabled) {
        return new QuarryFilter(blocks, tags, enabled, oreOnly);
    }

    public QuarryFilter withOreOnly(boolean enabled) {
        return new QuarryFilter(blocks, tags, whitelist, enabled);
    }

    public boolean matches(BlockState state) {
        if (oreOnly && !state.is(TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("c", "ores"))))
            return false;
        boolean hasEntries = !blocks.isEmpty();
        boolean selected = false;
        Block block = state.getBlock();
        for (GhostItem entry : blocks) {
            ItemStack sample = entry.item().create();
            if (sample.getItem() instanceof BlockItem item && item.getBlock() == block) selected = true;
        }
        for (String name : tags) {
            if (name.isEmpty()) continue;
            hasEntries = true;
            Identifier id = Identifier.tryParse(name);
            if (id != null && state.is(TagKey.create(Registries.BLOCK, id))) selected = true;
        }
        return !hasEntries || selected == whitelist;
    }
}
