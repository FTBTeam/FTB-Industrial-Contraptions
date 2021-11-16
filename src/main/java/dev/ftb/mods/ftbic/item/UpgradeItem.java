package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.world.item.Item;

public class UpgradeItem extends Item {
	public UpgradeItem(int stack) {
		super(new Properties().stacksTo(stack).tab(FTBIC.TAB));
	}
}
