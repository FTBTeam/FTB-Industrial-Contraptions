package dev.ftb.mods.ftbic.block;

import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.ToolType;

import java.util.Random;

public class ExFluidBlock extends Block {
	public ExFluidBlock() {
		super(Properties.of(Material.STONE).strength(0.9F).harvestTool(ToolType.PICKAXE).sound(SoundType.STONE).noDrops().isValidSpawn((arg, arg2, arg3, object) -> false).randomTicks());
	}

	@Override
	@Deprecated
	public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
		for (Direction direction : FTBICUtils.DIRECTIONS) {
			if (direction != Direction.DOWN && level.getFluidState(pos.relative(direction)).getType() != Fluids.EMPTY) {
				return;
			}
		}

		level.removeBlock(pos, false);
	}

	@Override
	@Deprecated
	public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}
}
