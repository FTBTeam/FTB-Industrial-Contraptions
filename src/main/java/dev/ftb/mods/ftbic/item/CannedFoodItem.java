package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemHandlerHelper;

public class CannedFoodItem extends Item {
	public CannedFoodItem() {
		super(new Properties().stacksTo(24).tab(FTBIC.TAB).food(new FoodProperties.Builder().nutrition(4).saturationMod(0.5F).alwaysEat().fast().meat().build()));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		ItemStack is = entity.eat(level, stack);

		if (entity instanceof ServerPlayer && !((ServerPlayer) entity).isCreative()) {
			if (is.isEmpty()) {
				return new ItemStack(FTBICItems.EMPTY_CAN.item.get());
			}

			ItemHandlerHelper.giveItemToPlayer((ServerPlayer) entity, new ItemStack(FTBICItems.EMPTY_CAN.item.get()));
		}

		return stack;
	}
}
