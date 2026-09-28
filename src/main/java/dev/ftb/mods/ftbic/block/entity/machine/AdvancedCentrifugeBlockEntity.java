package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AdvancedCentrifugeBlockEntity extends CentrifugeBlockEntity {
    public AdvancedCentrifugeBlockEntity(BlockPos pos, BlockState state) {
        super(FTBICElectricBlocks.ADVANCED_CENTRIFUGE, pos, state);
    }
}
