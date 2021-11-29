package dev.ftb.mods.ftbic.item.reactor;

public class FuelRodItem extends ReactorItem {
	public final double[] energy;
	public final int[] heat;

	public FuelRodItem(int durability, double[] e, int[] h) {
		super(durability);
		energy = e;
		heat = h;
	}
}
