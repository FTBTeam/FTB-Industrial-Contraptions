package dev.ftb.mods.ftbic.item.reactor;

import dev.ftb.mods.ftbic.FTBICConfig;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * 9×6 grid simulation of a Nuclear Reactor. Ticks run in two passes:
 *  - reactorTickPre: plating computes maxHeat/explosionModifier, heatExchangers redistribute heat,
 *    heatVents apply self/reactor/component cooling.
 *  - reactorTickPost: fuel rods produce pulses + damage, neutron reflectors consume durability.
 *
 * `distributeHeat` divides incoming heat across adjacent heat-acceptor items (heatVents, exchangers,
 * coolants) or dumps it into reactor heat if none are available.
 */
public class NuclearReactor {
	public final ItemStack[] inputItems;

	public boolean paused = true;
	public boolean allowRedstoneControl = false;
	public boolean simulation = false;
	public int heat = 0;

	public double energyOutput = 0D;
	public int maxHeat = 10_000;
	public double explosionModifier = 1D;
	public double explosionRadius = 0D;

	public NuclearReactor(ItemStack[] is) {
		inputItems = is;
	}

	public ItemStack getAt(int x, int y) {
		if (x < 0 || x >= 9 || y < 0 || y >= 6) return ItemStack.EMPTY;
		return inputItems[x + y * 9];
	}

	public void setAt(int x, int y, ItemStack stack) {
		if (x < 0 || x >= 9 || y < 0 || y >= 6) return;
		inputItems[x + y * 9] = stack;
	}

	public void addHeat(int h) {
		heat = Math.max(heat + h, 0);
	}

	public void distributeHeat(ItemStack[] around, int incomingHeat) {
		List<ItemStack> acceptors = new ArrayList<>(around.length);
		ItemStack first = ItemStack.EMPTY;
		for (ItemStack stack : around) {
			if (stack.getItem() instanceof ReactorItem ri && ri.isHeatAcceptor(stack)) {
				acceptors.add(stack);
				if (first == ItemStack.EMPTY) first = stack;
			}
		}

		if (acceptors.isEmpty()) {
			addHeat(incomingHeat);
			return;
		}

		int per = incomingHeat / acceptors.size();
		for (ItemStack stack : acceptors) {
			((ReactorItem) stack.getItem()).damageReactorItem(stack, per);
		}
		if (!first.isEmpty()) {
			((ReactorItem) first.getItem()).damageReactorItem(first, incomingHeat % acceptors.size());
		}
	}

	/** Runs one reactor cycle. Returns true if the simulation should stop (i.e. no fuel rods active). */
	public boolean tick() {
		energyOutput = 0D;
		maxHeat = 10_000;
		explosionModifier = 1D;
		explosionRadius = FTBICConfig.NUCLEAR.NUCLEAR_REACTOR_EXPLOSION_BASE_RADIUS.get();
		boolean stopSimulation = true;

		for (int pass = 0; pass < 3; pass++) {
			for (int x = 0; x < 9; x++) {
				for (int y = 0; y < 6; y++) {
					ItemStack stack = getAt(x, y);
					if (stack.getItem() instanceof ReactorItem ri) {
						if (pass == 0) ri.reactorTickPre(this, stack, x, y);
						else if (pass == 1) ri.reactorTickPost(this, stack, x, y);
						else {
							if (simulation && ri.keepSimulationRunning(stack)) stopSimulation = false;
						}
						if (stack.isEmpty() || ri.isItemBroken(stack)) {
							setAt(x, y, ItemStack.EMPTY);
						}
					}
				}
			}
		}

		heat = Math.max(0, heat);
		explosionRadius *= explosionModifier;
		explosionRadius = Math.min(explosionRadius, FTBICConfig.NUCLEAR.NUCLEAR_REACTOR_EXPLOSION_LIMIT.get());
		if (heat >= maxHeat) stopSimulation = true;
		return stopSimulation;
	}
}
