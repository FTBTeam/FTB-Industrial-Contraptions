package dev.ftb.mods.ftbic.item.reactor;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class BaseReactorItem extends Item implements ReactorItem {
	public BaseReactorItem(Properties props) {
		super(props.stacksTo(1));
	}

	@Override
	public void reactorTickPre(NuclearReactor reactor, ItemStack stack, int x, int y) {
	}

	@Override
	public void reactorTickPost(NuclearReactor reactor, ItemStack stack, int x, int y) {
	}
}
