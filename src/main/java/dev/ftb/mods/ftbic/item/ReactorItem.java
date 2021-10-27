package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.world.item.Item;

public class ReactorItem extends Item {
	public ReactorItem(int durability) {
		super(new Properties().durability(durability).tab(FTBIC.TAB));
	}
}
