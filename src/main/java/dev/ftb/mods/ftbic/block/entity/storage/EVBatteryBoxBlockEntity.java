package dev.ftb.mods.ftbic.block.entity.storage;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class EVBatteryBoxBlockEntity extends BatteryBoxBlockEntity {
	public EVBatteryBoxBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICElectricBlocks.EV_BATTERY_BOX, pos, state);
	}
}
