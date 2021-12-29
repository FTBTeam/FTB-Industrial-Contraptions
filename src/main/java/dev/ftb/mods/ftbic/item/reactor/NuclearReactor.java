package dev.ftb.mods.ftbic.item.reactor;

import dev.ftb.mods.ftbic.FTBICConfig;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class NuclearReactor {
	public final ItemStack[] inputItems;

	public boolean paused;
	public double energyOutput;
	public int heat;

	public int maxHeat;
	public double explosionRadius;
	public double explosionModifier;

	public NuclearReactor(ItemStack[] is) {
		inputItems = is;
		paused = true;
		energyOutput = 0D;
		heat = 0;
	}

	public ItemStack getAt(int x, int y) {
		if (x < 0 || x >= 9 || y < 0 || y >= 6) {
			return ItemStack.EMPTY;
		}

		return inputItems[x + y * 9];
	}

	public void setAt(int x, int y, ItemStack stack) {
		if (x < 0 || x >= 9 || y < 0 || y >= 6) {
			return;
		}

		inputItems[x + y * 9] = stack;
	}

	public void addHeat(int h) {
		heat = Math.max(heat + h, 0);
	}

	public void distributeHeat(ItemStack[] around, int heat) {
		List<ItemStack> list = new ArrayList<>(around.length);
		ItemStack first = ItemStack.EMPTY;

		for (ItemStack stack : around) {
			if (stack.getItem() instanceof ReactorItem && ((ReactorItem) stack.getItem()).isHeatAcceptor(stack)) {
				list.add(stack);

				if (first == ItemStack.EMPTY) {
					first = stack;
				}
			}
		}

		if (list.isEmpty()) {
			addHeat(heat);
			return;
		}

		for (ItemStack stack : list) {
			((ReactorItem) stack.getItem()).damageReactorItem(stack, heat / list.size());
		}

		((ReactorItem) first.getItem()).damageReactorItem(first, heat % list.size());
	}

	public void tick() {
		energyOutput = 0D;
		maxHeat = 10000;
		explosionRadius = FTBICConfig.NUCLEAR_REACTOR_EXPLOSION_BASE_RADIUS;
		explosionModifier = 1D;

		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 6; y++) {
				ItemStack stack = getAt(x, y);

				if (stack.getItem() instanceof ReactorItem) {
					((ReactorItem) stack.getItem()).reactorTickPre(this, stack, x, y);
				}
			}
		}

		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 6; y++) {
				ItemStack stack = getAt(x, y);

				if (stack.getItem() instanceof ReactorItem) {
					((ReactorItem) stack.getItem()).reactorTickPost(this, stack, x, y);

					if (stack.isEmpty() || stack.isDamageableItem() && stack.getDamageValue() >= stack.getMaxDamage()) {
						setAt(x, y, ItemStack.EMPTY);
					}
				}
			}
		}

		heat = Math.max(0, heat);
		explosionRadius *= explosionModifier;
		explosionRadius = Math.min(explosionRadius, FTBICConfig.NUCLEAR_REACTOR_EXPLOSION_LIMIT);
	}
}
