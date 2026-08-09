package dev.ftb.mods.industrialcontraptions.block.entity.generator;

import dev.ftb.mods.industrialcontraptions.ICConfig;
import dev.ftb.mods.industrialcontraptions.block.ICElectricBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class WindMillBlockEntity extends GeneratorBlockEntity {
	private int blocksInRadius = -1;
	public double output = 0D;

	public WindMillBlockEntity(BlockPos pos, BlockState state) {
		super(ICElectricBlocks.WIND_MILL, pos, state);
	}

	@Override
	public void handleGeneration() {
		output = 0D;
		if (level == null || energy >= energyCapacity || !level.canSeeSky(worldPosition.above())) {
			return;
		}

		if (level.getGameTime() % 1200L == 0L) {
			blocksInRadius = -1;
		}

		if (blocksInRadius == -1) {
			blocksInRadius = 0;
			for (int x = -5; x <= 5; x++) {
				for (int z = -5; z <= 5; z++) {
					for (int y = -2; y <= 5; y++) {
						if (y <= 0 && x == 0 && z == 0) continue;
						if (!level.isEmptyBlock(worldPosition.offset(x, y, z))) {
							blocksInRadius++;
						}
					}
				}
			}
		}

		int height = worldPosition.getY() - blocksInRadius;
		int minY = ICConfig.MACHINES.WIND_MILL_MIN_Y.get();
		int maxY = ICConfig.MACHINES.WIND_MILL_MAX_Y.get();
		if (height < minY) return;
		if (height > maxY) height = maxY;

		output = Mth.lerp(height / (double) (maxY - minY),
				ICConfig.MACHINES.WIND_MILL_MIN_OUTPUT.get(),
				ICConfig.MACHINES.WIND_MILL_MAX_OUTPUT.get());

		if (output <= 0D) return;

		if (level.isThundering()) {
			output *= ICConfig.MACHINES.WIND_MILL_THUNDER_MODIFIER.get();
		} else if (level.isRaining()) {
			output *= ICConfig.MACHINES.WIND_MILL_RAIN_MODIFIER.get();
		}

		energy += Math.min(energyCapacity - energy, output);
		setChanged();
	}
}
