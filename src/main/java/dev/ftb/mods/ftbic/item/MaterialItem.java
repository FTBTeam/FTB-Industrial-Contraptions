package dev.ftb.mods.ftbic.item;

import net.minecraft.world.item.Item;

import java.util.function.Supplier;

/**
 * Holder for a plain material item. Populated by FTBICItems.material(id) which sets the `item` Supplier
 * once the DeferredRegister entry is created.
 */
public final class MaterialItem {
	public final String id;
	public Supplier<Item> item;

	public MaterialItem(String id) {
		this.id = id;
	}
}
