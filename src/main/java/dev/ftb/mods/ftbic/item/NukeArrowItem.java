package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.entity.NukeArrowEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class NukeArrowItem extends ArrowItem {
	public NukeArrowItem() {
		super(new Properties().tab(FTBIC.TAB));
	}

	@Override
	public AbstractArrow createArrow(Level level, ItemStack stack, LivingEntity entity) {
		return new NukeArrowEntity(level, entity);
	}
}
