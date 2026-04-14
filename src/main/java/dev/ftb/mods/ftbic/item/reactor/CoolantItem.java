package dev.ftb.mods.ftbic.item.reactor;

import net.minecraft.world.item.ItemStack;

/** Passive heat buffer. Accepts heat from adjacent fuel rods via `distributeHeat`. */
public class CoolantItem extends BaseReactorItem {
	public final int maxHeat;

	public CoolantItem(Properties props, int maxHeat) {
		super(props.durability(Math.max(1, maxHeat)));
		this.maxHeat = maxHeat;
	}

	@Override
	public boolean isHeatAcceptor(ItemStack stack) {
		return stack.getMaxDamage() > 0;
	}
}
