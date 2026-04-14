package dev.ftb.mods.ftbic.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RubberSheetBlock extends SlimeBlock {
	public static final VoxelShape SHAPE = box(0D, 0D, 0D, 16D, 3D, 16D);

	public RubberSheetBlock(BlockBehaviour.Properties props) {
		super(props.strength(1F).friction(0.8F).sound(SoundType.SLIME_BLOCK));
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return SHAPE;
	}
}
