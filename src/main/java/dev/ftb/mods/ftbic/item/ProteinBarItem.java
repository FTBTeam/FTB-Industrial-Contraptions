package dev.ftb.mods.ftbic.item;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

public class ProteinBarItem extends Item {
	public ProteinBarItem(Properties props) {
		super(props.stacksTo(16).food(new FoodProperties.Builder()
				.nutrition(8)
				.saturationModifier(0.75F)
				.alwaysEdible()
				.build()));
	}
}
