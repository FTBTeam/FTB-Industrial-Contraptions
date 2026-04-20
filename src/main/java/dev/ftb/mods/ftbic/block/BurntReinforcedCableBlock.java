package dev.ftb.mods.ftbic.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BurntReinforcedCableBlock extends Block {
	public BurntReinforcedCableBlock(BlockBehaviour.Properties props) {
		super(props);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		for (int i = 0; i < 3; i++) {
			double x = pos.getX() + 0.3 + random.nextDouble() * 0.4;
			double y = pos.getY() + 0.3 + random.nextDouble() * 0.4;
			double z = pos.getZ() + 0.3 + random.nextDouble() * 0.4;
			level.addParticle(random.nextInt(5) == 0 ? ParticleTypes.LARGE_SMOKE : ParticleTypes.SMOKE, x, y, z, 0D, 0D, 0D);
		}
	}
}
