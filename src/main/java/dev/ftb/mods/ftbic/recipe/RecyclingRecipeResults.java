package dev.ftb.mods.ftbic.recipe;

import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.util.MachineProcessingResult;
import dev.ftb.mods.ftbic.util.StackWithChance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RecyclingRecipeResults extends MachineRecipeResults {
	@Override
	public MachineProcessingResult createResult(Level level, ItemStack[] inputs) {
		// TODO: Add support for custom scrap items or chances
		return new MachineProcessingResult(new StackWithChance(new ItemStack(FTBICItems.SCRAP.item.get()), 0.1D), 1D);
	}
}
