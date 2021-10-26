package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class MachineBlockEntity extends ElectricBlockEntity {
	public MachineBlockEntity(BlockEntityType<?> type) {
		super(type);
	}

	@Override
	public boolean canReceive() {
		return true;
	}
}
