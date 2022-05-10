package dev.ftb.mods.ftbic.block.entity.storage;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class LVBatteryBoxBlockEntity extends BatteryBoxBlockEntity {
	public LVBatteryBoxBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICElectricBlocks.LV_BATTERY_BOX, pos, state);
	}
}
