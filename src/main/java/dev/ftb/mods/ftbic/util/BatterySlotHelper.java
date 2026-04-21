package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import net.minecraft.world.item.ItemStack;

public final class BatterySlotHelper {
	private BatterySlotHelper() {}

	public static double drainBatteryToBuffer(ElectricBlockEntity be, ItemStack battery,
			double maxInputTransfer, double efficiency) {
		if (battery.isEmpty() || !(battery.getItem() instanceof EnergyItemHandler item)) return 0D;
		if (be.energy >= be.energyCapacity) return 0D;

		double transfer = item.isCreativeEnergyItem()
				? Double.POSITIVE_INFINITY
				: maxInputTransfer * efficiency;
		double drained = item.extractEnergy(battery, Math.min(be.energyCapacity - be.energy, transfer), false);
		if (drained > 0D) {
			be.energy += drained;
			be.setChanged();
		}
		return drained;
	}
}
