package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;

public final class FTBICCapabilities {
	public static final BlockCapability<ZapEnergyHandler, Direction> ZAP_ENERGY_BLOCK =
			BlockCapability.createSided(FTBIC.id("zap_energy"), ZapEnergyHandler.class);

	private FTBICCapabilities() {}
}
