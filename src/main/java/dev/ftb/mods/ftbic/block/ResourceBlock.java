package dev.ftb.mods.ftbic.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

/**
 * Copper / nope
 * Tin
 * Uranium
 * Iridium
 * Lead
 * aluminum
 */
public class ResourceBlock extends Block {
    public ResourceBlock(boolean isDeepslate) {
        super(isDeepslate ? deepslateProps() : normalProps());
    }

	private static Properties deepslateProps() {
		return Properties.of(Material.STONE).strength(4.5f, 3.0f).sound(SoundType.DEEPSLATE);
	}

	private static Properties normalProps() {
		return Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.0f, 3.0f);
	}
}
