package dev.ftb.mods.ftbic.recipe;

import dev.ftb.mods.ftbic.util.MachineProcessingResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class MachineRecipeResults {
	private final Map<Object, MachineProcessingResult> cache = new HashMap<>();

	public MachineProcessingResult getResult(Level level, ItemStack[] inputs, boolean checkCount) {
		if (inputs.length < getRequiredItems()) {
			return MachineProcessingResult.NONE;
		}

		Object key = createKey(inputs);
		MachineProcessingResult result = cache.get(key);

		if (result == null) {
			result = createResult(level, inputs);
			cache.put(key, result);
		}

		if (inputs.length < result.consume.length) {
			return MachineProcessingResult.NONE;
		}

		if (checkCount) {
			for (int i = 0; i < result.consume.length; i++) {
				if (inputs[i].getCount() < result.consume[i]) {
					return MachineProcessingResult.NONE;
				}
			}
		}

		return result;
	}

	public int getRequiredItems() {
		return 1;
	}

	public Object createKey(ItemStack[] inputs) {
		return inputs[0].getItem();
	}

	public abstract List<MachineRecipe> getAllRecipes(Level level);

	public abstract MachineProcessingResult createResult(Level level, ItemStack[] inputs);

	public boolean canInsert(Level level, int slot, ItemStack item) {
		return slot == 0 && getResult(level, new ItemStack[]{item}, false).exists();
	}

	public final List<MachineRecipe> getAllVisibleRecipes(Level level) {
		return getAllRecipes(level).stream().filter(MachineRecipe::isVisibleJEI).collect(Collectors.toList());
	}

	public final List<MachineRecipe> getAllRealAndVisibleRecipes(Level level) {
		return getAllRecipes(level).stream().filter(MachineRecipe::isRealAndVisibleJEI).collect(Collectors.toList());
	}
}
