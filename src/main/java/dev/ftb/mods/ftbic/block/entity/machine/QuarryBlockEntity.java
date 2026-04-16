package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import dev.ftb.mods.ftbic.screen.QuarryMenu;

public class QuarryBlockEntity extends DiggingBaseBlockEntity {
	public QuarryBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICElectricBlocks.QUARRY, pos, state);
	}

	@Override
	public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int id, net.minecraft.world.entity.player.Inventory inv) {
		return new QuarryMenu(id, inv, this);
	}
}
