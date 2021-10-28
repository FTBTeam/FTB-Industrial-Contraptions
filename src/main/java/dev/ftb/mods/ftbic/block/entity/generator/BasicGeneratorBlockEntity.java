package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public class BasicGeneratorBlockEntity extends GeneratorBlockEntity {
	public BasicGeneratorBlockEntity() {
		super(FTBICElectricBlocks.BASIC_GENERATOR.blockEntity.get());
		energyCapacity = 16000;
	}

	@Override
	public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
		return super.rightClick(player, hand, hit);
	}
}
