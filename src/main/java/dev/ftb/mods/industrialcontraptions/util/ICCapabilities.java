package dev.ftb.mods.industrialcontraptions.util;

import dev.ftb.mods.industrialcontraptions.IC;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;

public final class ICCapabilities {
	public static final BlockCapability<ZapEnergyHandler, Direction> ZAP_ENERGY_BLOCK =
			BlockCapability.createSided(IC.id("zap_energy"), ZapEnergyHandler.class);

	private ICCapabilities() {}
}
