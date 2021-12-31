package dev.ftb.mods.ftbic.item.reactor;

import net.minecraft.world.item.ItemStack;

public interface ReactorItem {
	default int getRods(ItemStack stack) {
		return 0;
	}

	default boolean keepSimulationRunning(ItemStack stack) {
		return false;
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

			int result = 0;
			int tempHeat = stack.getDamageValue();
			tempHeat += damage;

			if (tempHeat > max) {
				result = max - tempHeat + 1;
				tempHeat = max;
			} else if (tempHeat < 0) {
				result = tempHeat;
				tempHeat = 0;
			}

			stack.setDamageValue(tempHeat);
			return result;
		}

		return damage;
	}

	void reactorTickPre(NuclearReactor reactor, ItemStack stack, int x, int y);

	void reactorTickPost(NuclearReactor reactor, ItemStack stack, int x, int y);
}
