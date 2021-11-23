package dev.ftb.mods.ftbic.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public final class ItemKey {
	public final Item item;
	public CompoundTag tag;

	public ItemKey(ItemStack stack) {
		item = stack.getItem();
		tag = stack.getTag();

		if (tag != null) {
			if (!tag.isEmpty() && tag.contains("Damage")) {
				tag = tag.copy();
				tag.remove("Damage");
			}

			if (tag.isEmpty()) {
				tag = null;
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		ItemKey e = (ItemKey) o;
		return item == e.item && Objects.equals(tag, e.tag);
	}

	@Override
	public int hashCode() {
		int result = 31 + item.hashCode();

		if (tag != null) {
			result = 31 * result + tag.hashCode();
		}

		return result;
	}
}
