package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class OreWasherBlockEntity extends FluidMachineBlockEntity {
    public OreWasherBlockEntity(BlockPos pos, BlockState state) {
        this(FTBICElectricBlocks.ORE_WASHER, pos, state);
    }

    protected OreWasherBlockEntity(ElectricBlockInstance type, BlockPos pos, BlockState state) {
        super(type, FTBICRecipes.WASHING, pos, state);
    }
}
