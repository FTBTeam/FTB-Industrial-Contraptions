package dev.ftb.mods.ftbic.item.reactor;

import dev.ftb.mods.ftbic.FTBICConfig;
import net.minecraft.world.item.ItemStack;

public class FuelRodItem extends BaseReactorItem implements NeutronReflectingReactorItem {
	public static final int[] OFFSET_X = {0, 0, -1, 1};
	public static final int[] OFFSET_Y = {-1, 1, 0, 0};

	public final int maxDurability;
	public final int rods;
	public final int pulses;
	public final double energyMultiplier;
	public final double heatMultiplier;

	public FuelRodItem(Properties props, int maxDurability, int rods, double energyMultiplier, double heatMultiplier) {
		super(props.durability(Math.max(1, maxDurability)));
		this.maxDurability = maxDurability;
		this.rods = rods;
		this.pulses = rods == 1 ? 1 : rods == 2 ? 2 : 3;
		this.energyMultiplier = energyMultiplier;
		this.heatMultiplier = heatMultiplier;
	}

	@Override
	public int getRods(ItemStack stack) {
		return rods;
	}

	@Override
	public boolean keepSimulationRunning(ItemStack stack) {
		return true;
	}

	@Override
	public void reactorTickPre(NuclearReactor reactor, ItemStack stack, int x, int y) {
		if (reactor.paused) return;
		reactor.explosionRadius += getRods(stack) * FTBICConfig.NUCLEAR.NUCLEAR_REACTOR_EXPLOSION_MULTIPLIER.get();
	}

	@Override
	public void reactorTickPost(NuclearReactor reactor, ItemStack stack, int x, int y) {
		if (reactor.paused) return;
		int p = pulses;
		ItemStack[] around = new ItemStack[4];
		for (int i = 0; i < 4; i++) {
			around[i] = reactor.getAt(x + OFFSET_X[i], y + OFFSET_Y[i]);
			if (around[i].getItem() instanceof NeutronReflectingReactorItem) p++;
		}
		reactor.energyOutput += p * energyMultiplier;
		reactor.distributeHeat(around, (int) (heatMultiplier * p * (p + 1)));
		damageReactorItem(stack, 1);
	}
}
