package dev.ftb.mods.ftbic.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ResourceBlock extends Block {
	public ResourceBlock(BlockBehaviour.Properties props, boolean isDeepslate) {
		super(isDeepslate
				? props.strength(4.5f, 3.0f).sound(SoundType.DEEPSLATE).requiresCorrectToolForDrops()
				: props.strength(3.0f, 3.0f).requiresCorrectToolForDrops());
	}
}
