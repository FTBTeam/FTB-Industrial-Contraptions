package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AdvancedOreWasherBlockEntity extends OreWasherBlockEntity {
    public AdvancedOreWasherBlockEntity(BlockPos pos, BlockState state) {
        super(FTBICElectricBlocks.ADVANCED_ORE_WASHER, pos, state);
    }
}
