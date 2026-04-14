package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.registry.ModDataComponents;
import dev.ftb.mods.ftbic.util.EnergyItemHandler;
import dev.ftb.mods.ftbic.util.EnergyTier;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Hand-holdable battery. Single-use variants ship pre-charged to capacity and shrink when drained.
 * Right-clicking toggles the {@code BATTERY_ACTIVE} data component; while active, the battery's
 * {@code inventoryTick} drains itself into every other equipped {@link EnergyItemHandler} item the
 * carrier owns (armour, hand items, hotbar gear) so players can carry a battery to top up their
 * tools/armor on the move.
 */
public class BatteryItem extends ElectricItem {
	public final BatteryType batteryType;

	public BatteryItem(Properties props, BatteryType batteryType, EnergyTier tier, double capacity) {
		super(props, tier, capacity);
		this.batteryType = batteryType;
		// Single-use batteries ship pre-charged via the ENERGY data component default.
		if (batteryType.singleUse) {
			props.component(ModDataComponents.ENERGY.get(), capacity);
		}
	}

	@Override
	public boolean canInsertEnergy() {
		return !batteryType.singleUse;
	}

	@Override
	public boolean canExtractEnergy() {
		return true;
	}

	@Override
	public boolean isCreativeEnergyItem() {
		return batteryType.creative;
	}

	@Override
	public double extractEnergy(ItemStack stack, double maxExtract, boolean simulate) {
		double drained = super.extractEnergy(stack, maxExtract, simulate);
		if (!simulate && getEnergy(stack) <= 0D) {
			if (batteryType.singleUse) {
				stack.shrink(1);
			} else {
				stack.remove(ModDataComponents.ENERGY.get());
			}
		}
		return drained;
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return stack.has(ModDataComponents.BATTERY_ACTIVE.get());
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (batteryType.singleUse) return InteractionResult.PASS;
		ItemStack stack = player.getItemInHand(hand);
		if (stack.has(ModDataComponents.BATTERY_ACTIVE.get())) {
			stack.remove(ModDataComponents.BATTERY_ACTIVE.get());
		} else {
			stack.set(ModDataComponents.BATTERY_ACTIVE.get(), Unit.INSTANCE);
		}
		return InteractionResult.SUCCESS;
	}

	/** Slots a player can hold an EnergyItemHandler in (armour + hands). Mob-only slots skipped. */
	private static final EquipmentSlot[] CHARGE_SLOTS = {
			EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET,
			EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND
	};

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
		if (!(entity instanceof LivingEntity le)) return;
		if (!stack.has(ModDataComponents.BATTERY_ACTIVE.get())) return;
		if (getEnergy(stack) <= 0D) return;

		// Charge every other EnergyItemHandler stack the carrier wears or holds.
		for (EquipmentSlot eq : CHARGE_SLOTS) {
			ItemStack equipped = le.getItemBySlot(eq);
			if (equipped == stack || equipped.isEmpty()) continue;
			if (!(equipped.getItem() instanceof EnergyItemHandler other)) continue;
			double available = getEnergy(stack);
			if (available <= 0D) break;
			double inserted = other.insertEnergy(equipped, available, false);
			if (inserted > 0D) extractEnergy(stack, inserted, false);
		}
	}
}
