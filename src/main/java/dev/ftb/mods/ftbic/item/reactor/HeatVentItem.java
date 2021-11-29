package dev.ftb.mods.ftbic.item.reactor;

public class HeatVentItem extends ReactorItem {
	public final int heatDissipated;
	public final int heatPulledFromReactor;
	public final boolean reactor;

	public HeatVentItem(int durability, int d, int p, boolean r) {
		super(durability);
		heatDissipated = d;
		heatPulledFromReactor = p;
		reactor = r;
	}
}
