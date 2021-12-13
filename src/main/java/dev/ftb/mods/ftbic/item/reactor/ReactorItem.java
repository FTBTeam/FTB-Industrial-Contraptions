package dev.ftb.mods.ftbic.item.reactor;

import net.minecraft.world.item.ItemStack;

public interface ReactorItem {
	default int getRods() {
		return 0;
	}

	default boolean isCoolant(ItemStack stack) {
		return false;
	}

	default void damageReactorItem(ItemStack stack, int damage) {
		if (damage != 0 && stack.isDamageableItem()) {
			stack.setDamageValue(stack.getDamageValue() + damage);
		}
	}

	void reactorTickPre(NuclearReactor reactor, ItemStack stack, int x, int y);

	void reactorTickPost(NuclearReactor reactor, ItemStack stack, int x, int y);
}
