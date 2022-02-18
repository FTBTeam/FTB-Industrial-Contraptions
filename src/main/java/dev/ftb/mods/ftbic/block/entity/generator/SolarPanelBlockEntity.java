package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.screen.SolarPanelMenu;
import dev.ftb.mods.ftbic.screen.sync.SyncedData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class SolarPanelBlockEntity extends GeneratorBlockEntity {
	public SolarPanelBlockEntity(ElectricBlockInstance type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		maxEnergyOutputTransfer = Math.max(FTBICConfig.LV_TRANSFER_RATE, maxEnergyOutput);
	}

	@Override
	public void handleGeneration() {
		if (energy < energyCapacity && level.isDay() && level.canSeeSky(worldPosition.above())) {
			energy += Math.min(energyCapacity - energy, maxEnergyOutput);
		}
	}

	@Override
	public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide()) {
			openMenu((ServerPlayer) player, (id, inventory) -> new SolarPanelMenu(id, inventory, this));
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public void addSyncData(SyncedData data) {
		super.addSyncData(data);
		data.addShort(SyncedData.BAR, () -> level.isDay() && level.canSeeSky(worldPosition.above()) ? 14 : 0);
	}
}
