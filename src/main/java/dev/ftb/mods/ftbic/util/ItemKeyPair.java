package dev.ftb.mods.ftbic.util;

import net.minecraft.world.item.ItemStack;

public final class ItemKeyPair {
	public final ItemKey a, b;

	public ItemKeyPair(ItemStack _a, ItemStack _b) {
		a = new ItemKey(_a);
		b = new ItemKey(_b);
	}

	@Override
	public boolean equals(Object o) {
		ItemKeyPair e = (ItemKeyPair) o;
		return a.equals(e.a) && b.equals(e.b);
	}

	@Override
	public int hashCode() {
		return a.hashCode() * 31 + b.hashCode();
	}
}
