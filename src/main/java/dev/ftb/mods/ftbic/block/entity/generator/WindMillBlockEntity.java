package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class WindMillBlockEntity extends GeneratorBlockEntity {
	private int blocksInRadius = -1;
	public double output = 0D;

	public WindMillBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICElectricBlocks.WIND_MILL, pos, state);
	}

	@Override
	public void handleGeneration() {
		output = 0D;

		if (energy < energyCapacity && level.canSeeSky(worldPosition.above())) {
			if (level.getGameTime() % 1200L == 0L) {
				blocksInRadius = -1;
			}

			if (blocksInRadius == -1) {
				blocksInRadius = 0;

				for (int x = -5; x <= 5; x++) {
					for (int z = -5; z <= 5; z++) {
						for (int y = -2; y <= 5; y++) {
							if (y <= 0 && x == 0 && z == 0) {
								continue;
							}

							if (!level.isEmptyBlock(worldPosition.offset(x, y, z))) {
								blocksInRadius++;
							}
						}
					}
				}
			}

			int height = worldPosition.getY() - blocksInRadius;

			if (height < FTBICConfig.MACHINES.WIND_MILL_MIN_Y.get()) {
				return;
			} else if (height > FTBICConfig.MACHINES.WIND_MILL_MAX_Y.get()) {
				height = FTBICConfig.MACHINES.WIND_MILL_MAX_Y.get();
			}

			output = Mth.lerp(height / (double) (FTBICConfig.MACHINES.WIND_MILL_MAX_Y.get() - FTBICConfig.MACHINES.WIND_MILL_MIN_Y.get()), FTBICConfig.MACHINES.WIND_MILL_MIN_OUTPUT.get(), FTBICConfig.MACHINES.WIND_MILL_MAX_OUTPUT.get());

			if (output <= 0D) {
				return;
			}

			if (level.isThundering()) {
				output *= FTBICConfig.MACHINES.WIND_MILL_THUNDER_MODIFIER.get();
			} else if (level.isRaining()) {
				output *= FTBICConfig.MACHINES.WIND_MILL_RAIN_MODIFIER.get();
			}

			energy += Math.min(energyCapacity - energy, output);

			if (energy >= energyCapacity) {
				setChanged();
			}
		}
	}

	@Override
	public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide()) {
			player.displayClientMessage(Component.translatable("ftbic.energy_output", FTBICUtils.formatEnergy(output)), false);
		}

		return InteractionResult.SUCCESS;
	}
}
