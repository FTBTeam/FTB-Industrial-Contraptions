package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.registry.ModDataComponents;
import dev.ftb.mods.ftbic.util.EnergyItemHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

public class MechanicalElytraItem extends Item implements EnergyItemHandler {
	public MechanicalElytraItem(Properties props) {
		super(props.stacksTo(1));
	}

	public void damageEnergyItem(ItemStack stack, double amount) {
		double energy = getEnergy(stack);
		setEnergy(stack, Math.max(0D, energy - Math.min(energy, amount)));
	}

	@Override
	public double getEnergyCapacity(ItemStack stack) {
		return FTBICConfig.EQUIPMENT.MECHANICAL_ELYTRA_CAPACITY.get();
	}

	@Override
	public boolean canExtractEnergy() {
		return true;
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
		if (slot != EquipmentSlot.CHEST) return;
		if (!(entity instanceof LivingEntity le)) return;

		if (le.isFallFlying()) {
			tickFlight(stack, le);
			return;
		}

		double rechargeRate = FTBICConfig.EQUIPMENT.MECHANICAL_ELYTRA_RECHARGE.get();
		if (rechargeRate <= 0D) return;
		if (!level.isBrightOutside()) return;
		if (!level.canSeeSky(le.blockPosition())) return;
		insertEnergy(stack, rechargeRate, false);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged || !oldStack.is(newStack.getItem());
	}

	private void tickFlight(ItemStack stack, LivingEntity le) {
		double drain = FTBICConfig.EQUIPMENT.ARMOR_FLIGHT_ENERGY.get();
		if (drain > 0D && !isCreativeEnergyItem()) {
			double available = getEnergy(stack);
			if (available < drain) {
				setEnergy(stack, 0D);
				le.stopFallFlying();
				return;
			}
			setEnergy(stack, available - drain);
		}

		int flightTicks = le.getFallFlyingTicks();
		if (flightTicks >= 3 && le.isCrouching()) {
			Vec3 m = le.getDeltaMovement();
			double d = Math.max(Math.abs(m.y), Math.max(Math.abs(m.x), Math.abs(m.z)));
			d = Math.min(d, 1D);
			d = d * 0.91D;
			le.setDeltaMovement(m.multiply(d, d, d));
			damageEnergyItem(stack, FTBICConfig.EQUIPMENT.ARMOR_FLIGHT_STOP.get());
		} else if (flightTicks >= 5 && le instanceof Player p && p.isSprinting()) {
			Vec3 v = le.getLookAngle();
			double d0 = 1.5D;
			double d1 = 0.1D;
			Vec3 m = le.getDeltaMovement();
			le.setDeltaMovement(m.add(v.x * d1 + (v.x * d0 - m.x) * 0.5D, v.y * d1 + (v.y * d0 - m.y) * 0.5D, v.z * d1 + (v.z * d0 - m.z) * 0.5D));
			damageEnergyItem(stack, FTBICConfig.EQUIPMENT.ARMOR_FLIGHT_BOOST.get());
		}
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return stack.has(ModDataComponents.ENERGY.get());
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return (int) Math.round(Mth.clamp((getEnergy(stack) / getEnergyCapacity(stack)) * 13D, 0D, 13D));
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return 0xFFFF0000;
	}

	@Override
	@SuppressWarnings("deprecation")
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
			Consumer<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, context, display, tooltip, flag);
		double energy = getEnergy(stack);
		double cap = getEnergyCapacity(stack);
		tooltip.accept(Component.translatable("item.ftbic.tooltip.energy",
						EnergyItemHandler.formatEnergy(energy), EnergyItemHandler.formatEnergy(cap))
				.withStyle(ChatFormatting.GRAY));
	}
}
