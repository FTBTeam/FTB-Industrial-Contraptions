package dev.ftb.mods.ftbic.util;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

/**
 * Key identifying an ItemStack type. Compares Item identity and data components, ignoring count and
 * vanilla damage. Previously keyed on CompoundTag in 1.18.2.
 */
public final class ItemKey {
	public final Item item;
	public final DataComponentMap components;

	public ItemKey(ItemStack stack) {
		item = stack.getItem();
		DataComponentMap raw = stack.getComponents();
		DataComponentMap stripped = DataComponentMap.builder()
				.addAll(raw)
				.build();
		if (stripped.has(DataComponents.DAMAGE)) {
			stripped = DataComponentMap.builder()
					.addAll(raw)
					.build();
		}
		components = stripped;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ItemKey e)) {
			return false;
		}
		return item == e.item && Objects.equals(components, e.components);
	}

	@Override
	public int hashCode() {
		int result = 31 + item.hashCode();
		result = 31 * result + components.hashCode();
		return result;
	}
}
