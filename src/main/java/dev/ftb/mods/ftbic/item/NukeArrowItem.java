package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.entity.NukeArrowEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public class NukeArrowItem extends ArrowItem {
	public NukeArrowItem(Properties props) {
		super(props);
	}

	@Override
	public AbstractArrow createArrow(Level level, ItemStack pickupItemStack, LivingEntity owner, @Nullable ItemStack weapon) {
		return new NukeArrowEntity(level, owner);
	}
}
