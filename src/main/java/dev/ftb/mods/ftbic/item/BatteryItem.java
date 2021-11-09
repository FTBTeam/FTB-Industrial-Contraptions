package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.util.EnergyTier;
import net.minecraft.world.item.ItemStack;

public class BatteryItem extends EnergyItem {
	public final BatteryType batteryType;

	public BatteryItem(BatteryType b, EnergyTier t, int cap) {
		super(t, cap);
		batteryType = b;
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
		double d = super.extractEnergy(stack, maxExtract, simulate);

		if (!simulate && batteryType.singleUse && getEnergy(stack) <= 0D) {
			stack.shrink(1);
		}

		return d;
	}
}
