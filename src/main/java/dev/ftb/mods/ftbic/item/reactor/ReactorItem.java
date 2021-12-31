package dev.ftb.mods.ftbic.item.reactor;

import net.minecraft.world.item.ItemStack;

public interface ReactorItem {
	default int getRods(ItemStack stack) {
		return 0;
	}

	default boolean keepSimulationRunning(ItemStack stack) {
		return false;
	}

	default boolean isItemBroken(ItemStack stack) {
		int max = stack.getMaxDamage();
		return max > 0 && stack.getDamageValue() >= max;
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

	default int damageReactorItem(ItemStack stack, int damage) {
		if (damage != 0 && stack.isDamageableItem()) {
			int max = stack.getMaxDamage();

			if (max <= 0) {
				return damage;
			}

			int extra = 0;
			int newDamage = stack.getDamageValue();
			newDamage += damage;

			if (newDamage > max) {
				extra = max - newDamage + 1;
				newDamage = max;
			} else if (newDamage < 0) {
				extra = newDamage;
				newDamage = 0;
			}

			stack.setDamageValue(newDamage);
			return extra;
		}

		return damage;
	}

	void reactorTickPre(NuclearReactor reactor, ItemStack stack, int x, int y);

	void reactorTickPost(NuclearReactor reactor, ItemStack stack, int x, int y);
}
