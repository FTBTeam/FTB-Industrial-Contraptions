package dev.ftb.mods.ftbic.block;

import net.minecraft.world.level.block.Block;
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
    public ResourceBlock() {
        super(Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.0f, 3.0f));
    }
}
