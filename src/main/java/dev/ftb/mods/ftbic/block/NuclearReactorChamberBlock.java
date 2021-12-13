package dev.ftb.mods.ftbic.block;

import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ToolType;
import org.jetbrains.annotations.Nullable;

public class NuclearReactorChamberBlock extends Block {
	public NuclearReactorChamberBlock() {
		super(Properties.of(Material.METAL).strength(3.5F).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops());
		registerDefaultState(getStateDefinition().any().setValue(BlockStateProperties.FACING, Direction.DOWN));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.FACING);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		BlockPos pos = ctx.getClickedPos();

		for (Direction dir : FTBICUtils.DIRECTIONS) {
			if (ctx.getLevel().getBlockState(pos.relative(dir)).getBlock() == FTBICElectricBlocks.NUCLEAR_REACTOR.block.get()) {
				return defaultBlockState().setValue(BlockStateProperties.FACING, dir.getOpposite());
			}
		}

		return defaultBlockState();
	}

	@Override
	@Deprecated
	public BlockState updateShape(BlockState state, Direction dir, BlockState state1, LevelAccessor level, BlockPos pos, BlockPos pos1) {
		if (state1.getBlock() == FTBICElectricBlocks.NUCLEAR_REACTOR.block.get()) {
			return state.setValue(BlockStateProperties.FACING, dir.getOpposite());
		}

		return state;
	}

	@Override
	@Deprecated
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		BlockPos pos1 = pos.relative(state.getValue(BlockStateProperties.FACING).getOpposite());
		return level.getBlockState(pos1).use(level, player, hand, hit.withPosition(pos1));
	}
}
