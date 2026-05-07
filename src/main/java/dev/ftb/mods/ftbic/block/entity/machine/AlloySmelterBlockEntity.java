package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class AlloySmelterBlockEntity extends MachineBlockEntity {
	public AlloySmelterBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICElectricBlocks.ALLOY_SMELTER, FTBICRecipes.ALLOY_SMELTING, pos, state);
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		if (!super.isItemValid(slot, stack)) return false;
		if (stack.isEmpty()) return true;
		for (int i = 0; i < inputItems.length; i++) {
			if (i == slot) continue;
			ItemStack other = inputItems[i];
			if (!other.isEmpty() && ItemStack.isSameItemSameComponents(other, stack)) {
				return false;
			}
		}
		return true;
	}
}
