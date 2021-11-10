package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.screen.SolarPanelMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.BlockHitResult;

public class SolarPanelBlockEntity extends GeneratorBlockEntity {
	public double solarOutput;

	public SolarPanelBlockEntity(BlockEntityType<?> type) {
		super(type, 0, 0);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		solarOutput = 0D;
	}

	@Override
	public void handleGeneration() {
		if (energy < energyCapacity && level.isDay() && level.canSeeSky(worldPosition.above())) {
			energy += Math.min(energyCapacity - energy, solarOutput);

			if (energy >= energyCapacity) {
				setChanged();
			}
		}
	}

	@Override
	public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide()) {
			openMenu((ServerPlayer) player, (id, inventory) -> new SolarPanelMenu(id, inventory, this, this));
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public int getCount() {
		return 2;
	}

	@Override
	public int get(int id) {
		switch (id) {
			case 0:
				// getLightValue()
				return level.isDay() && level.canSeeSky(worldPosition.above()) ? 14 : 0;
			case 1:
				// getEnergyBar()
				return energy == 0 ? 0 : Mth.clamp(Mth.ceil(energy * 14D / energyCapacity), 0, 14);
			default:
				return 0;
		}
	}
}
