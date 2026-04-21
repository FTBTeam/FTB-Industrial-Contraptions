package dev.ftb.mods.ftbic.item.reactor;

import net.minecraft.world.item.ItemStack;

public class ReactorPlatingItem extends BaseReactorItem {
	public final int heatCapacity;
	public final double explosionResistance;

	public ReactorPlatingItem(Properties props, int heatCapacity, double explosionResistance) {
		super(props);
		this.heatCapacity = heatCapacity;
		this.explosionResistance = explosionResistance;
	}

	@Override
	public void reactorTickPre(NuclearReactor reactor, ItemStack stack, int x, int y) {
		reactor.maxHeat += heatCapacity;
		reactor.explosionModifier *= explosionResistance;
	}
}
