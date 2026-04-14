package dev.ftb.mods.ftbic.block.entity.storage;

import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.block.entity.generator.GeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Accepts higher-tier input on its back face (facing) and emits lower-tier output on all other faces.
 * Per-tier subclasses configure capacity + max-input/output via the ElectricBlockInstance declaration.
 */
public class TransformerBlockEntity extends GeneratorBlockEntity {
	public TransformerBlockEntity(ElectricBlockInstance type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		maxEnergyOutputTransfer = maxEnergyOutput;
	}

	@Override
	public boolean isValidEnergyOutputSide(Direction direction) {
		return direction != getFacing(Direction.NORTH);
	}

	@Override
	public boolean isValidEnergyInputSide(Direction direction) {
		return direction == getFacing(Direction.NORTH);
	}
}
