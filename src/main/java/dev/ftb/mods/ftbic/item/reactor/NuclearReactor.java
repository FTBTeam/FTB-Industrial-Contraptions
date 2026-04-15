package dev.ftb.mods.ftbic.item.reactor;

import dev.ftb.mods.ftbic.FTBICConfig;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * 3-row dynamic-width reactor grid. Width = 3 + chamberCount (3..9). Array is always sized
 * {@value MAX_COLUMNS} * {@value ROWS} = 27; cells beyond {@link #activeColumns} are forced empty
 * by the BE. Simulation iterates only the active region.
 */
public class NuclearReactor {
	public static final int MAX_COLUMNS = 9;
	public static final int ROWS = 3;
	public static final int MAX_SLOTS = MAX_COLUMNS * ROWS;

	public final ItemStack[] inputItems;

	public boolean paused = true;
	public boolean allowRedstoneControl = false;
	public boolean simulation = false;
	public int heat = 0;
	public int activeColumns = 3;
	/** 1.0 = no environmental cooling bonus, 2.0 = fully water-clad (scales reactor-hull cooling). */
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

	/** Runs one reactor cycle. Returns true if the simulation should stop (i.e. no fuel rods active). */
	public boolean tick() {
		energyOutput = 0D;
		maxHeat = 10_000;
		explosionModifier = 1D;
		explosionRadius = FTBICConfig.NUCLEAR.NUCLEAR_REACTOR_EXPLOSION_BASE_RADIUS.get();
		boolean stopSimulation = true;

		int cols = Math.max(3, Math.min(MAX_COLUMNS, activeColumns));

		for (int pass = 0; pass < 3; pass++) {
			for (int x = 0; x < cols; x++) {
				for (int y = 0; y < ROWS; y++) {
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
