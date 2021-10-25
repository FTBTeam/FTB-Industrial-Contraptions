package dev.ftb.mods.ftbic.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class ElectricBlock extends Block {
	public static final EnumProperty<ElectricBlockState> STATE = EnumProperty.create("state", ElectricBlockState.class);
	public static final BooleanProperty DARK = BooleanProperty.create("dark");

	public final ElectricBlockInstance electricBlockInstance;
	public final DirectionProperty facingProperty;

	public ElectricBlock(ElectricBlockInstance m) {
		super(Properties.of(Material.METAL).strength(3.5F).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops());
		electricBlockInstance = m;
		facingProperty = electricBlockInstance.horizontal ? BlockStateProperties.HORIZONTAL_FACING : BlockStateProperties.FACING;
		registerDefaultState(getStateDefinition().any().setValue(facingProperty, Direction.SOUTH).setValue(STATE, ElectricBlockState.OFF).setValue(DARK, false));
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
		builder.add(ElectricBlockInstance.current.horizontal ? BlockStateProperties.HORIZONTAL_FACING : BlockStateProperties.FACING, STATE, DARK);
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

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
		if (state.getValue(STATE) == ElectricBlockState.BURNT) {
			for (int i = 0; i < 5; i++) {
				level.addParticle(ParticleTypes.SMOKE, pos.getX() + random.nextFloat(), pos.getY() + 1D, pos.getZ() + random.nextFloat(), 0D, 0D, 0D);
			}

			level.addParticle(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5D, pos.getY() + 1D, pos.getZ() + 0.5D, 0D, 0D, 0D);
		}
	}
}
