package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public class WindMillBlockEntity extends GeneratorBlockEntity {
	private int blocksInRadius = -1;
	public double output = 0D;

	public WindMillBlockEntity() {
		super(FTBICElectricBlocks.WIND_MILL.blockEntity.get(), 0, 0);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		energyCapacity = 100;
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

			if (height < FTBICConfig.WIND_MILL_MIN_Y) {
				return;
			} else if (height > FTBICConfig.WIND_MILL_MAX_Y) {
				height = FTBICConfig.WIND_MILL_MAX_Y;
			}

			output = Mth.lerp(height / (double) (FTBICConfig.WIND_MILL_MAX_Y - FTBICConfig.WIND_MILL_MIN_Y), FTBICConfig.WIND_MILL_MIN_OUTPUT, FTBICConfig.WIND_MILL_MAX_OUTPUT);

			if (output <= 0D) {
				return;
			}

			if (level.isThundering()) {
				output *= FTBICConfig.WIND_MILL_THUNDER_MODIFIER;
			} else if (level.isRaining()) {
				output *= FTBICConfig.WIND_MILL_RAIN_MODIFIER;
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
			player.displayClientMessage(new TranslatableComponent("ftbic.output", FTBICUtils.formatEnergy(output)), false);
		}

		return InteractionResult.SUCCESS;
	}
}
