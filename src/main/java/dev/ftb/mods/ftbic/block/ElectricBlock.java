package dev.ftb.mods.ftbic.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.ToolType;
import org.jetbrains.annotations.Nullable;

public class ElectricBlock extends Block {
	public static final BooleanProperty DARK = BooleanProperty.create("dark");

	public final ElectricBlockInstance electricBlockInstance;
	public final DirectionProperty facingProperty;

	public ElectricBlock(ElectricBlockInstance m) {
		super(Properties.of(Material.METAL).strength(3.5F).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops());
		electricBlockInstance = m;
		facingProperty = electricBlockInstance.horizontal ? BlockStateProperties.HORIZONTAL_FACING : BlockStateProperties.FACING;
		registerDefaultState(getStateDefinition().any().setValue(facingProperty, Direction.SOUTH).setValue(BlockStateProperties.LIT, false).setValue(DARK, false));
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public BlockEntity createTileEntity(BlockState state, BlockGetter level) {
		return electricBlockInstance.blockEntity.get().create();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(ElectricBlockInstance.current.horizontal ? BlockStateProperties.HORIZONTAL_FACING : BlockStateProperties.FACING, BlockStateProperties.LIT, DARK);
	}

	@Override
	@Deprecated
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(facingProperty, rotation.rotate(state.getValue(facingProperty)));
	}

	@Override
	@Deprecated
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(facingProperty)));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext arg) {
		if (electricBlockInstance.horizontal) {
			return defaultBlockState().setValue(facingProperty, arg.getHorizontalDirection().getOpposite());
		} else {
			return defaultBlockState().setValue(facingProperty, arg.getNearestLookingDirection().getOpposite());
		}
	}
}
