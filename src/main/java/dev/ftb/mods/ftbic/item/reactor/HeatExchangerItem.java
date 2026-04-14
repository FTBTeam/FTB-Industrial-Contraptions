package dev.ftb.mods.ftbic.item.reactor;

import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

/**
 * Heat exchanger — moves heat between adjacent heat-acceptor components and the reactor core,
 * proportional to the relative damage gradient between the exchanger and its neighbours. Mirrors
 * the 1.18.2 implementation: each tick we compute heat to push to/pull from each adjacent acceptor
 * (using {@code heatTransferToAdjacent}) and the reactor core (using {@code heatTransferToCore}),
 * then absorb the net delta as durability damage on this exchanger.
 */
public class HeatExchangerItem extends BaseReactorItem {
	public final int maxHeat;
	public final int heatTransferToAdjacent;
	public final int heatTransferToCore;

	public HeatExchangerItem(Properties props, int maxHeat, int heatTransferToAdjacent, int heatTransferToCore) {
		super(maxHeat > 0 ? props.durability(maxHeat) : props);
		this.maxHeat = maxHeat;
		this.heatTransferToAdjacent = heatTransferToAdjacent;
		this.heatTransferToCore = heatTransferToCore;
	}

	@Override
	public boolean isHeatAcceptor(ItemStack stack) {
		return stack.getMaxDamage() > 0;
	}

	@Override
	public void reactorTickPre(NuclearReactor reactor, ItemStack stack, int x, int y) {
		int damage = 0;

		if (heatTransferToAdjacent > 0) {
			for (int i = 0; i < 4; i++) {
				ItemStack is = reactor.getAt(x + FuelRodItem.OFFSET_X[i], y + FuelRodItem.OFFSET_Y[i]);
				if (is.getItem() instanceof ReactorItem ri && ri.isHeatAcceptor(is)) {
					double sh = getRelativeDamage(stack) * 100D;
					double rh = ri.getRelativeDamage(is) * 100D;
					int heat = computeTransfer(sh, rh, is.getMaxDamage(), heatTransferToAdjacent);
					damage -= heat;
					damage += ri.damageReactorItem(is, heat);
				}
			}
		}

		if (heatTransferToCore > 0 && reactor.maxHeat > 0) {
			double sh = getRelativeDamage(stack) * 100D;
			double rh = reactor.heat * 100D / (double) reactor.maxHeat;
			int heat = computeTransfer(sh, rh, reactor.maxHeat, heatTransferToCore);
			damage -= heat;
			reactor.addHeat(heat);
		}

		damageReactorItem(stack, damage);
	}

	/**
	 * Reference 1.18.2 transfer-rate formula. Picks a base step from the relative-damage curve
	 * (low temps move 1 unit, very hot move full transfer rate) then flips sign + zeroes when the
	 * destination is hotter than the source.
	 */
	private static int computeTransfer(double sh, double rh, int max, int transfer) {
		double hh = rh + sh / 2D;
		int add = Math.min(Mth.floor(max * hh / 100D), transfer);

		if (hh < 0.25)      add = 1;
		else if (hh < 0.5)  add = transfer / 8;
		else if (hh < 0.75) add = transfer / 4;
		else if (hh < 1.0)  add = transfer / 2;

		double frh = Mth.floor(rh * 10D) / 10D;
		double fsh = Mth.floor(sh * 10D) / 10D;
		if (frh > fsh)        add -= 2 * add;
		else if (frh == fsh)  add = 0;
		return add;
	}
}
