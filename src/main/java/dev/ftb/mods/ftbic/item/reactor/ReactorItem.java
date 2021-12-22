package dev.ftb.mods.ftbic.item.reactor;

import net.minecraft.world.item.ItemStack;

public interface ReactorItem {
	default int getRods() {
		return 0;
	}

	default double getRelativeDamage(ItemStack stack) {
		return stack.isDamageableItem() ? stack.getDamageValue() / (double) stack.getMaxDamage() : 0D;
	}

	default boolean isHeatAcceptor(ItemStack stack) {
		return false;
	}

	default boolean isCoolant(ItemStack stack) {
		return isHeatAcceptor(stack);
	}

	default void damageReactorItem(ItemStack stack, int damage) {
		if (damage != 0 && stack.isDamageableItem()) {
			stack.setDamageValue(stack.getDamageValue() + damage);
		}
	}

	void reactorTickPre(NuclearReactor reactor, ItemStack stack, int x, int y);

	void reactorTickPost(NuclearReactor reactor, ItemStack stack, int x, int y);
}
