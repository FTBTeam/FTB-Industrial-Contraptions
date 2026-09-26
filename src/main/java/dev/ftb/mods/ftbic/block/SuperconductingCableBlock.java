package dev.ftb.mods.ftbic.block;

import dev.ftb.mods.ftbic.block.entity.SuperconductingCableBlockEntity;
import dev.ftb.mods.ftbic.util.EnergyTier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SuperconductingCableBlock extends CableBlock implements EntityBlock {

    public SuperconductingCableBlock(Properties properties) {
        super(properties, EnergyTier.SUPERCONDUCTING, 5);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SuperconductingCableBlockEntity(pos, state);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getBlockEntity(pos) instanceof SuperconductingCableBlockEntity cable) {
            cable.checkIdle();
        }
    }

    @Override
    protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int parameter) {
        BlockEntity entity = level.getBlockEntity(pos);
        return entity != null && entity.triggerEvent(id, parameter);
    }
}
