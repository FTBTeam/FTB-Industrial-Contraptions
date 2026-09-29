package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CentrifugeBlockEntity extends FluidMachineBlockEntity {
    public CentrifugeBlockEntity(BlockPos pos, BlockState state) {
        this(FTBICElectricBlocks.CENTRIFUGE, pos, state);
    }

    protected CentrifugeBlockEntity(ElectricBlockInstance type, BlockPos pos, BlockState state) {
        super(type, FTBICRecipes.SEPARATING, pos, state);
    }
}
