package dev.ftb.mods.ftbic.block.entity.storage;

import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.block.entity.generator.GeneratorBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public class TransformerBlockEntity extends GeneratorBlockEntity {
	public TransformerBlockEntity(ElectricBlockInstance type) {
		super(type);
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

	@Override
	public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
		return InteractionResult.SUCCESS;
	}
}
