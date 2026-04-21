package dev.ftb.mods.ftbic.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.projectile.hurtingprojectile.WitherSkull;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ReinforcedGlassBlock extends HalfTransparentBlock {
	public ReinforcedGlassBlock(Properties props) {
		super(props);
	}

	@Override
	public boolean canEntityDestroy(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
		if (entity instanceof WitherBoss || entity instanceof WitherSkull) {
			return false;
		}
		return super.canEntityDestroy(state, level, pos, entity);
	}
}
