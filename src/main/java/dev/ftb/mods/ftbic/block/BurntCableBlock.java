package dev.ftb.mods.ftbic.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class BurntCableBlock extends BaseCableBlock {
	public BurntCableBlock() {
		super(6, SoundType.METAL);
	}

	public static BlockState getBurntCable(BlockState state) {
		if (!(state.getBlock() instanceof BaseCableBlock)) {
			return FTBICBlocks.BURNT_CABLE.get().defaultBlockState();
		}

		return FTBICBlocks.BURNT_CABLE.get().defaultBlockState()
				.setValue(CONNECTION[0], state.getValue(CONNECTION[0]))
				.setValue(CONNECTION[1], state.getValue(CONNECTION[1]))
				.setValue(CONNECTION[2], state.getValue(CONNECTION[2]))
				.setValue(CONNECTION[3], state.getValue(CONNECTION[3]))
				.setValue(CONNECTION[4], state.getValue(CONNECTION[4]))
				.setValue(CONNECTION[5], state.getValue(CONNECTION[5]))
				.setValue(BlockStateProperties.WATERLOGGED, state.getValue(BlockStateProperties.WATERLOGGED));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
		for (int i = 0; i < 3; i++) {
			double x = pos.getX() + 0.3 + random.nextDouble() * 0.4;
			double y = pos.getY() + 0.3 + random.nextDouble() * 0.4;
			double z = pos.getZ() + 0.3 + random.nextDouble() * 0.4;
			level.addParticle(random.nextInt(5) == 0 ? ParticleTypes.LARGE_SMOKE : ParticleTypes.SMOKE, x, y, z, 0D, 0D, 0D);
		}
	}
}