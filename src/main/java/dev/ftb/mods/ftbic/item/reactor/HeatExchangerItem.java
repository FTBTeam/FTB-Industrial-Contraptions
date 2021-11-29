package dev.ftb.mods.ftbic.item.reactor;

public class HeatExchangerItem extends ReactorItem {
	public final int heatTransferToAdjecent;
	public final int heatTransferToCore;
	public final boolean reactor;

	public HeatExchangerItem(int durability, int a, int c, boolean r) {
		super(durability);
		heatTransferToAdjecent = a;
		heatTransferToCore = c;
		reactor = r;
	}
}
