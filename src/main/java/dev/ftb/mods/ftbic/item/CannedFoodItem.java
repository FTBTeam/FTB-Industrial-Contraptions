package dev.ftb.mods.ftbic.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CannedFoodItem extends Item {
	public CannedFoodItem(Properties props) {
		super(props.stacksTo(24).food(new FoodProperties.Builder()
				.nutrition(4)
				.saturationModifier(0.5F)
				.alwaysEdible()
				.build()));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		ItemStack result = super.finishUsingItem(stack, level, entity);

		if (entity instanceof ServerPlayer player && !player.isCreative()) {
			ItemStack can = new ItemStack(FTBICItems.EMPTY_CAN.item.get());
			if (result.isEmpty()) return can;
			if (!player.getInventory().add(can)) player.drop(can, false);
		}

		return result;
	}
}
