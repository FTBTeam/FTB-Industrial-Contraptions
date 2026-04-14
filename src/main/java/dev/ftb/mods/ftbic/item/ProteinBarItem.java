package dev.ftb.mods.ftbic.item;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

/**
 * Compact survival ration matching the 1.18.2 stats: 8 hunger, 0.75 saturation, always edible.
 * The legacy {@code .fast()} and {@code .meat()} flags from the reference are no-ops on the new
 * {@code FoodProperties.Builder} (the API dropped them in 1.21+).
 */
public class ProteinBarItem extends Item {
	public ProteinBarItem(Properties props) {
		super(props.stacksTo(16).food(new FoodProperties.Builder()
				.nutrition(8)
				.saturationModifier(0.75F)
				.alwaysEdible()
				.build()));
	}
}
