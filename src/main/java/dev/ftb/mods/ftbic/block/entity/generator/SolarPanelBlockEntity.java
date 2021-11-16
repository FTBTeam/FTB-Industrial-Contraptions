package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.screen.SolarPanelMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.BlockHitResult;

public class SolarPanelBlockEntity extends GeneratorBlockEntity {
	public SolarPanelBlockEntity(BlockEntityType<?> type) {
		super(type, 0, 0);
	}

	@Override
	public void initProperties() {
		super.initProperties();
	}

	@Override
	public void handleGeneration() {
		if (energy < energyCapacity && level.isDay() && level.canSeeSky(worldPosition.above())) {
			energy += Math.min(energyCapacity - energy, maxEnergyOutput);

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
		if (id == 1) {
			// getLightValue()
			return level.isDay() && level.canSeeSky(worldPosition.above()) ? 14 : 0;
		}

		return super.get(id);
	}
}
