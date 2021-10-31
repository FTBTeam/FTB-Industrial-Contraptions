package dev.ftb.mods.ftbic.util;

import net.minecraft.world.item.Item;

public class ItemPair {
	public final Item a, b;

	public ItemPair(Item _a, Item _b) {
		a = _a;
		b = _b;
	}

	@Override
	public boolean equals(Object o) {
		ItemPair e = (ItemPair) o;
		return a == e.a && b == e.b;
	}

	@Override
	public int hashCode() {
		return a.hashCode() * 31 + b.hashCode();
	}
}
