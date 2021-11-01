package dev.ftb.mods.ftbic.recipe;

import dev.ftb.mods.ftbic.util.MachineProcessingResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AntimatterFabricatorRecipeResults extends MachineRecipeResults {
	@Override
	public MachineProcessingResult createResult(Level level, ItemStack[] inputs) {
		return MachineProcessingResult.NONE;
	}
}
