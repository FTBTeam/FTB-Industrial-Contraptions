package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AdvancedExtruderBlockEntity extends MachineBlockEntity {
    public AdvancedExtruderBlockEntity(BlockPos pos, BlockState state) {
        super(FTBICElectricBlocks.ADVANCED_EXTRUDER, FTBICRecipes.EXTRUDING, pos, state);
    }
}
