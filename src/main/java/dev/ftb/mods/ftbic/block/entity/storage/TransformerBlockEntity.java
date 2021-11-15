package dev.ftb.mods.ftbic.block.entity.storage;

import dev.ftb.mods.ftbic.block.entity.generator.GeneratorBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;

public class TransformerBlockEntity extends GeneratorBlockEntity {
	public TransformerBlockEntity(BlockEntityType<?> type) {
		super(type, 0, 0);
	}

	@Override
	public boolean isValidEnergyOutputSide(Direction direction) {
		return direction != getBlockState().getValue(BlockStateProperties.FACING);
	}

	@Override
	public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
		return InteractionResult.SUCCESS;
	}
}
