package dev.ftb.mods.ftbic.item.reactor;

public class ReactorPlatingItem extends ReactorItem {
	public final int maxHeatBonus;
	public final double explosionModifier;

	public ReactorPlatingItem(int h, double e) {
		super(0);
		maxHeatBonus = h;
		explosionModifier = e;
	}
}
