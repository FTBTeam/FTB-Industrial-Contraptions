package dev.ftb.mods.ftbic.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public interface SprayPaintable {
	BooleanProperty DARK = BooleanProperty.create("dark");

	default boolean paint(BlockState state, Level level, BlockPos pos, boolean dark) {
		if (state.getValue(DARK) != dark) {
			level.setBlock(pos, state.setValue(DARK, dark), 3);
			return true;
		}

		return false;
	}
}
