package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.FTBICConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class MechanicalElytraItem extends EnergyItem {
	public MechanicalElytraItem(Properties props) {
		super(props.stacksTo(1));
	}

	@Override
	public double getEnergyCapacity(ItemStack stack) {
		return FTBICConfig.EQUIPMENT.MECHANICAL_ELYTRA_CAPACITY.get();
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
		super.inventoryTick(stack, level, entity, slot);

		if (slot != EquipmentSlot.CHEST) return;
		if (!(entity instanceof LivingEntity le)) return;

		if (!le.isFallFlying()) {
			double rechargeRate = FTBICConfig.EQUIPMENT.MECHANICAL_ELYTRA_RECHARGE.get();
			if (rechargeRate <= 0D) return;
			if (!level.isBrightOutside()) return;
			if (!level.canSeeSky(le.blockPosition())) return;
			insertEnergy(stack, rechargeRate, false);
		}
	}
}
