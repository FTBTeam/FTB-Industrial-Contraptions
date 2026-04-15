package dev.ftb.mods.ftbic.item.reactor;

import net.minecraft.world.item.ItemStack;

/**
 * Dissipates heat. `selfCooling` repairs the vent's own durability, `reactorCooling` subtracts from
 * the reactor's heat pool, `componentCooling` heals adjacent coolants/exchangers.
 */
public class HeatVentItem extends BaseReactorItem {
	public final int maxHeat;
	public final int selfCool;
	public final int reactorCool;
	public final int componentCool;

	public HeatVentItem(Properties props, int maxHeat, int selfCool, int reactorCool, int componentCool) {
		super(maxHeat > 0 ? props.durability(maxHeat) : props);
		this.maxHeat = maxHeat;
		this.selfCool = selfCool;
		this.reactorCool = reactorCool;
		this.componentCool = componentCool;
	}

	@Override
	public boolean isHeatAcceptor(ItemStack stack) {
		return stack.getMaxDamage() > 0;
	}

	@Override
	public void reactorTickPre(NuclearReactor reactor, ItemStack stack, int x, int y) {
		if (reactorCool > 0) {
			int scaled = (int) Math.round(reactorCool * reactor.envCoolingMultiplier);
			reactor.addHeat(-Math.max(reactorCool, scaled));
		}
		if (selfCool > 0) damageReactorItem(stack, -selfCool);
		if (componentCool > 0) {
			for (int i = 0; i < 4; i++) {
				ItemStack is = reactor.getAt(x + FuelRodItem.OFFSET_X[i], y + FuelRodItem.OFFSET_Y[i]);
				if (is.getItem() instanceof ReactorItem ri && ri.isCoolant(is)) {
					ri.damageReactorItem(is, -componentCool);
				}
			}
		}
	}
}
