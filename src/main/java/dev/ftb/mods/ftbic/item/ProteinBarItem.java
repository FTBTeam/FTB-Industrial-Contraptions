package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

public class ProteinBarItem extends Item {
	public ProteinBarItem() {
		super(new Properties().tab(FTBIC.TAB).food(new FoodProperties.Builder().nutrition(8).saturationMod(0.75F).alwaysEat().fast().meat().build()));
	}
}
