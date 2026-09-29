package dev.ftb.mods.ftbic.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.ftb.mods.ftbic.item.reactor.NuclearReactor;
import dev.ftb.mods.ftbic.item.reactor.ReactorItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record ReactorDesign(int version, int chambers, double water, List<DesignSlot> slots) {
    public static final int CURRENT_VERSION = 1;
    public static final int MAX_JSON_LENGTH = 8192;

    public ReactorDesign {
        slots = List.copyOf(slots);
    }

    /** Validate imported designs before they can affect a real inventory. */
    public boolean isValid() {
        if (version != CURRENT_VERSION
                || chambers < 0
                || chambers > 6
                || !Double.isFinite(water)
                || water < 0
                || water > 1
                || slots.size() > NuclearReactor.MAX_SLOTS) return false;
        var occupied = new HashSet<Integer>();
        for (DesignSlot slot : slots) {
            if (slot.slot() < 0
                    || slot.slot() >= NuclearReactor.MAX_SLOTS
                    || slot.slot() % NuclearReactor.MAX_COLUMNS >= 3 + chambers
                    || !occupied.add(slot.slot())
                    || !(resolveItem(slot) instanceof ReactorItem)) return false;
        }
        return true;
    }

    public static Item resolveItem(DesignSlot slot) {
        return BuiltInRegistries.ITEM.getValue(ResourceKey.create(Registries.ITEM, slot.id()));
    }

    public ItemStack[] previewItems() {
        ItemStack[] items = new ItemStack[NuclearReactor.MAX_SLOTS];
        Arrays.fill(items, ItemStack.EMPTY);
        if (isValid()) {
            for (DesignSlot slot : slots) items[slot.slot()] = new ItemStack(resolveItem(slot));
        }
        return items;
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static final Codec<DesignSlot> SLOT_CODEC = RecordCodecBuilder.create(i -> i.group(
                    Codec.INT.fieldOf("slot").forGetter(DesignSlot::slot),
                    Identifier.CODEC.fieldOf("id").forGetter(DesignSlot::id))
            .apply(i, DesignSlot::new));

    public static final Codec<ReactorDesign> CODEC = RecordCodecBuilder.create(i -> i.group(
                    Codec.INT.optionalFieldOf("version", CURRENT_VERSION).forGetter(ReactorDesign::version),
                    Codec.INT.fieldOf("chambers").forGetter(ReactorDesign::chambers),
                    Codec.DOUBLE.fieldOf("water").forGetter(ReactorDesign::water),
                    SLOT_CODEC.listOf().fieldOf("slots").forGetter(ReactorDesign::slots))
            .apply(i, ReactorDesign::new));

    public record DesignSlot(int slot, Identifier id) {}

    public String toJson() {
        JsonElement json = CODEC.encodeStart(JsonOps.INSTANCE, this).getOrThrow();
        return GSON.toJson(json);
    }

    public static ReactorDesign fromJson(String json) throws IllegalArgumentException {
        if (json == null || json.length() > MAX_JSON_LENGTH) throw new IllegalArgumentException("Design is too large");
        JsonElement parsed;
        try {
            parsed = JsonParser.parseString(json);
        } catch (Exception e) {
            throw new IllegalArgumentException("Not valid JSON: " + e.getMessage());
        }
        var result = CODEC.parse(JsonOps.INSTANCE, parsed);
        ReactorDesign design = result.resultOrPartial(err -> {})
                .orElseThrow(() -> new IllegalArgumentException("Could not decode reactor design"));
        if (!design.isValid()) throw new IllegalArgumentException("Invalid reactor design");
        return design;
    }

    public static ReactorDesign fromReactor(int chambers, double water, ItemStack[] slots) {
        List<DesignSlot> list = new ArrayList<>();
        for (int i = 0; i < slots.length; i++) {
            ItemStack s = slots[i];
            if (s.isEmpty()) continue;
            Identifier key = BuiltInRegistries.ITEM.getKey(s.getItem());
            list.add(new DesignSlot(i, key));
        }
        return new ReactorDesign(CURRENT_VERSION, chambers, water, list);
    }

    public void applyToInventory(ItemStack[] inventory, int activeColumns) {
        for (int i = 0; i < inventory.length; i++) inventory[i] = ItemStack.EMPTY;
        for (DesignSlot slot : slots) {
            if (slot.slot() < 0 || slot.slot() >= inventory.length) continue;
            int col = slot.slot() % NuclearReactor.MAX_COLUMNS;
            int row = slot.slot() / NuclearReactor.MAX_COLUMNS;
            if (row >= NuclearReactor.ROWS || col >= activeColumns) continue;
            Item item = BuiltInRegistries.ITEM.getValue(ResourceKey.create(Registries.ITEM, slot.id()));
            if (item == null) continue;
            inventory[slot.slot()] = new ItemStack(item);
        }
    }
}
