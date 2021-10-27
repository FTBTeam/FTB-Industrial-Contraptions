package dev.ftb.mods.ftbic.block.entity.storage;

import dev.ftb.mods.ftbic.block.entity.generator.GeneratorBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class BatteryBoxBlockEntity extends GeneratorBlockEntity {
	public BatteryBoxBlockEntity(BlockEntityType<?> type) {
		super(type);
	}

	@Override
	public boolean isValidLookupSide(Direction direction) {
		return direction == getBlockState().getValue(BlockStateProperties.FACING);
	}

	@Override
	public boolean canReceive() {
		return true;
	}
}
