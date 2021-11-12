package dev.ftb.mods.ftbic.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class CachedEnergyStorageOrigin {
	public Direction direction;
	public BlockPos cablePos = null;
	public EnergyTier cableTier = null;
	public boolean cableBurnt = false;
}
