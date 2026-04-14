package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.util.EnergyItemHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

/**
 * Electric elytra. Fall-flight is enabled via the vanilla {@code minecraft:glider} data component.
 * While flying, drains energy each tick; sneaking engages a brake (decelerates); sprinting boosts
 * speed in the look direction. Passively recharges when the wearer is stood in daylight and not
 * flying. Runs out of energy mid-flight → forces {@code stopFallFlying()} so the player can't glide
 * indefinitely on a dead battery.
 */
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
	public void inventoryTick(ItemStack stack, net.minecraft.server.level.ServerLevel level, net.minecraft.world.entity.Entity entity, net.minecraft.world.entity.EquipmentSlot slot) {
		if (slot != net.minecraft.world.entity.EquipmentSlot.CHEST) return;
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

	/**
	 * Per-flight-tick drain + modifiers. Called only while {@code isFallFlying()} is true.
	 * Runs on both client and server; server is authoritative for energy + forced landings.
	 */
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

		if (!(le instanceof Player player)) return;

		Vec3 velocity = player.getDeltaMovement();
		if (player.isShiftKeyDown()) {
			double brake = FTBICConfig.EQUIPMENT.ARMOR_FLIGHT_STOP.get();
			double damp = Math.max(0D, 1D - brake / 100D);
			player.setDeltaMovement(velocity.x * damp, velocity.y * damp, velocity.z * damp);
		} else if (player.isSprinting()) {
			double boost = FTBICConfig.EQUIPMENT.ARMOR_FLIGHT_BOOST.get() / 2000D;
			Vec3 look = player.getLookAngle();
			player.setDeltaMovement(velocity.add(look.scale(boost)));
		}
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return stack.has(dev.ftb.mods.ftbic.registry.ModDataComponents.ENERGY.get());
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
