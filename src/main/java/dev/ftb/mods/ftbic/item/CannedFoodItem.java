package dev.ftb.mods.ftbic.item;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

public class CannedFoodItem extends Item {
	public CannedFoodItem(Properties props) {
		super(props.stacksTo(24).food(new FoodProperties.Builder()
				.nutrition(4)
				.saturationModifier(0.5F)
				.alwaysEdible()
				.build())
				.usingConvertsTo(FTBICItems.EMPTY_CAN.item.get()));
	}
}
