package dev.ftb.mods.ftbic.item.reactor;

import net.minecraft.world.item.ItemStack;

public class NeutronReflectorItem extends BaseReactorItem implements NeutronReflectingReactorItem {
	public final int maxDurability;

	public NeutronReflectorItem(Properties props, int maxDurability) {
		super(maxDurability > 0 ? props.durability(maxDurability) : props);
		this.maxDurability = maxDurability;
	}

	@Override
	public void reactorTickPost(NuclearReactor reactor, ItemStack stack, int x, int y) {
		if (reactor.paused || stack.getMaxDamage() <= 0) return;
		for (int i = 0; i < 4; i++) {
			ItemStack item = reactor.getAt(x + FuelRodItem.OFFSET_X[i], y + FuelRodItem.OFFSET_Y[i]);
			if (item.getItem() instanceof ReactorItem ri) {
				damageReactorItem(stack, ri.getRods(item));
			}
		}
	}
}
