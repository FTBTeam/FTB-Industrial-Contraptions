package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Shared helpers for the "drain an {@link EnergyItemHandler} in an input slot into the BE's buffer"
 * pattern used by machines, battery boxes, and generators.
 */
public final class BatterySlotHelper {
	private BatterySlotHelper() {}

	/**
	 * Drain the given battery stack into the block entity's energy buffer.
	 *
	 * @param be              target BE (energy += drained; setChanged() called if anything drained)
	 * @param battery         battery slot contents — may be empty
	 * @param maxInputTransfer per-tick input cap (typically {@code maxInputEnergy} or {@code maxEnergyOutputTransfer})
	 * @param efficiency      {@link FTBICConfig.Machines#ITEM_TRANSFER_EFFICIENCY}
	 * @return amount of energy drained this tick (0 if nothing happened)
	 */
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
