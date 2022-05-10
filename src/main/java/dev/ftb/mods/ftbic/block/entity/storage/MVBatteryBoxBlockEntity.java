package dev.ftb.mods.ftbic.block.entity.storage;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class MVBatteryBoxBlockEntity extends BatteryBoxBlockEntity {
	public MVBatteryBoxBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICElectricBlocks.MV_BATTERY_BOX, pos, state);
	}
}
