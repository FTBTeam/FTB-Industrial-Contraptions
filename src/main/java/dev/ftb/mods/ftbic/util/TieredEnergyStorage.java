package dev.ftb.mods.ftbic.util;

import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public interface TieredEnergyStorage extends IEnergyStorage {
	@Nullable
	default PowerTier getInputPowerTier() {
		return null;
	}

	@Nullable
	default PowerTier getOutputPowerTier() {
		return null;
	}
}
