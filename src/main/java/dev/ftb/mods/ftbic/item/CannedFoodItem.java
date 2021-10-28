package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

public class CannedFoodItem extends Item {
	public CannedFoodItem() {
		super(new Properties().stacksTo(24).tab(FTBIC.TAB).food(new FoodProperties.Builder().nutrition(4).saturationMod(0.5F).alwaysEat().fast().meat().build()));
	}
}
