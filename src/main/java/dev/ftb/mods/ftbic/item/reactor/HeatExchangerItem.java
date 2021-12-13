package dev.ftb.mods.ftbic.item.reactor;

public class HeatExchangerItem extends BaseReactorItem {
	public final int heatTransferToAdjecent;
	public final int heatTransferToCore;

	public HeatExchangerItem(int durability, int a, int c) {
		super(durability);
		heatTransferToAdjecent = a;
		heatTransferToCore = c;
	}
}
