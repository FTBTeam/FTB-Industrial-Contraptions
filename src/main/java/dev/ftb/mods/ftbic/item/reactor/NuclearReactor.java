package dev.ftb.mods.ftbic.item.reactor;

import dev.ftb.mods.ftbic.FTBICConfig;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class NuclearReactor {
	public static final int MAX_COLUMNS = 9;
	public static final int ROWS = 6;
	public static final int MAX_SLOTS = MAX_COLUMNS * ROWS;

	public final ItemStack[] inputItems;

	public boolean paused = true;
	public boolean allowRedstoneControl = false;
	public boolean simulation = false;
	public int heat = 0;
	public int activeColumns = 3;
	public double envCoolingMultiplier = 1.0D;

	public double energyOutput = 0D;
	public int maxHeat = 10_000;
	public double explosionModifier = 1D;
	public double explosionRadius = 0D;

	public NuclearReactor(ItemStack[] is) {
		inputItems = is;
	}

	public static int slotIndex(int x, int y) {
		return y * MAX_COLUMNS + x;
	}

	public ItemStack getAt(int x, int y) {
		if (x < 0 || x >= MAX_COLUMNS || y < 0 || y >= ROWS) return ItemStack.EMPTY;
		return inputItems[slotIndex(x, y)];
	}

	public void setAt(int x, int y, ItemStack stack) {
		if (x < 0 || x >= MAX_COLUMNS || y < 0 || y >= ROWS) return;
		inputItems[slotIndex(x, y)] = stack;
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

	public boolean tick() {
		energyOutput = 0D;
		maxHeat = 10_000;
		explosionModifier = 1D;
		explosionRadius = FTBICConfig.NUCLEAR.NUCLEAR_REACTOR_EXPLOSION_BASE_RADIUS.get();
		boolean stopSimulation = true;

		int cols = Math.max(3, Math.min(MAX_COLUMNS, activeColumns));

		int passes = simulation ? 3 : 2;
		for (int pass = 0; pass < passes; pass++) {
			for (int x = 0; x < cols; x++) {
				for (int y = 0; y < ROWS; y++) {
					ItemStack stack = getAt(x, y);
					if (stack.getItem() instanceof ReactorItem ri) {
						if (pass == 0) ri.reactorTickPre(this, stack, x, y);
						else if (pass == 1) ri.reactorTickPost(this, stack, x, y);
						else if (ri.keepSimulationRunning(stack)) stopSimulation = false;
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
