package dev.ftb.mods.ftbic.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemTagKeyWrapper {
	private final TagKey<Item> tag;

	private ItemTagKeyWrapper(Identifier name) {
		this.tag = TagKey.create(Registries.ITEM, name);
	}

	public static ItemTagKeyWrapper create(Identifier tag) {
		return new ItemTagKeyWrapper(tag);
	}

	public TagKey<Item> getTag() {
		return tag;
	}

	public List<Item> getValues() {
		List<Item> out = new ArrayList<>();
		BuiltInRegistries.ITEM.getTagOrEmpty(tag).forEach(h -> out.add(h.value()));
		return out;
	}
}
