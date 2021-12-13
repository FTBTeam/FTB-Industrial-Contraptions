package dev.ftb.mods.ftbic.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class NukeBlock extends Block {
	public NukeBlock() {
		super(Properties.of(Material.EXPLOSIVE).instabreak().sound(SoundType.GRASS));
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block1, BlockPos pos1, boolean bl) {
		if (level.hasNeighborSignal(pos)) {
			if (!level.isClientSide) {
				PrimedTnt primedTnt = new PrimedTnt(level, (double) pos.getX() + 0.5, pos.getY(), (double) pos.getZ() + 0.5, null);
				primedTnt.setFuse(0);
				level.addFreshEntity(primedTnt);
				level.playSound(null, primedTnt.getX(), primedTnt.getY(), primedTnt.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1F, 1F);
			}

			level.removeBlock(pos, false);
		}
	}
}
