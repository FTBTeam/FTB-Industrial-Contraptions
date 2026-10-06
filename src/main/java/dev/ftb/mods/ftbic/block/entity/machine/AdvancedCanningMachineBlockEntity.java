package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AdvancedCanningMachineBlockEntity extends MachineBlockEntity {
    public AdvancedCanningMachineBlockEntity(BlockPos pos, BlockState state) {
        super(FTBICElectricBlocks.ADVANCED_CANNING_MACHINE, FTBICRecipes.CANNING, pos, state);
    }
}
