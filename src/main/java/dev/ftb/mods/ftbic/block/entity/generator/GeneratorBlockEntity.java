package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class GeneratorBlockEntity extends ElectricBlockEntity {
	public GeneratorBlockEntity(BlockEntityType<?> type) {
		super(type);
	}
}
