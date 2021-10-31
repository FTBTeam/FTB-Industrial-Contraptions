package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import dev.ftb.mods.ftbic.util.PowerTier;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.BlockHitResult;

public class MachineBlockEntity extends ElectricBlockEntity {
	public MachineBlockEntity(BlockEntityType<?> type) {
		super(type);
		inputPowerTier = PowerTier.LV;
	}

	@Override
	public void tick() {
		handleEnergyInput();
		handleChanges();
	}

	@Override
	public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide()) {
			player.displayClientMessage(new TextComponent("Energy: " + FTBICUtils.formatPower(energy, energyCapacity)), false);
		}

		return InteractionResult.SUCCESS;
	}
}