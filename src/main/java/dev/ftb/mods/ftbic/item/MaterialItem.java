package dev.ftb.mods.ftbic.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

public final class MaterialItem {
	public final String id;
	public DeferredItem<Item> item;

	public MaterialItem(String id) {
		this.id = id;
	}
}
