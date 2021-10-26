package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Arrays;

public class SolarPanelBlockEntity extends GeneratorBlockEntity {
	public SolarPanelBlockEntity(BlockEntityType<?> type) {
		super(type);
	}

	public int getGeneration() {
		return 1;
	}

	@Override
	public void tick() {
		if (energy < energyCapacity && !level.isClientSide() && level.isDay() && level.canSeeSky(worldPosition.above())) {
			energy += Math.min(energyCapacity - energy, getGeneration());

			if (energy == energyCapacity) {
				setChanged();
			}
		}

		super.tick();
	}

	@Override
	public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide()) {
			player.displayClientMessage(new TextComponent("Energy: " + FTBICUtils.formatPower(energy, energyCapacity)), false);

			if (player.isCrouching()) {
				player.displayClientMessage(new TextComponent("Connected machines: " + Arrays.toString(getConnectedEnergyBlocks())), false);
			}
		}

		return InteractionResult.SUCCESS;
	}
}
