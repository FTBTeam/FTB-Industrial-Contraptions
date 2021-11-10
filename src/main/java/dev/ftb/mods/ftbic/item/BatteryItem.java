package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.util.EnergyTier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

public class BatteryItem extends EnergyItem {
	public final BatteryType batteryType;

	public BatteryItem(BatteryType b, EnergyTier t, double cap) {
		super(t, cap);
		batteryType = b;
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		if (batteryType.singleUse) {
			CompoundTag t = stack.getOrCreateTag();

			if (!t.contains("Energy")) {
				t.putDouble("Energy", capacity);
			}
		}

		return null;
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

		if (!simulate && getEnergy(stack) <= 0D) {
			if (batteryType.singleUse) {
				stack.shrink(1);
			} else {
				stack.removeTagKey("Energy");
			}
		}

		return d;
	}
}
