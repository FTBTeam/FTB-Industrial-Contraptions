package dev.ftb.mods.ftbic.block;

import dev.ftb.mods.ftbic.block.entity.ActiveNukeBlockEntity;
import dev.ftb.mods.ftbic.block.entity.FTBICBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ActiveNukeBlock extends Block implements EntityBlock {
	public ActiveNukeBlock(BlockBehaviour.Properties props) {
		super(props);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ActiveNukeBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (level.isClientSide()) return null;
		if (type != FTBICBlockEntities.ACTIVE_NUKE.get()) return null;
		return (lvl, pos, st, be) -> {
			if (be instanceof ActiveNukeBlockEntity anbe) anbe.serverTick();
		};
	}
}
