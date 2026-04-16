package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ElectricBlockEntityRef extends ElectricBlockEntity {
	public ElectricBlockEntityRef(ElectricBlockInstance type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
}
