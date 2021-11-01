package dev.ftb.mods.ftbic.recipe;

import dev.ftb.mods.ftbic.util.MachineProcessingResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public abstract class MachineRecipeResults {
	private final Map<Object, MachineProcessingResult> cache = new HashMap<>();

	public MachineProcessingResult getResult(Level level, ItemStack[] inputs) {
		if (inputs.length != getRequiredItems()) {
			return MachineProcessingResult.NONE;
		}

		Object key = createKey(inputs);
		MachineProcessingResult result = cache.get(key);

		if (result == null) {
			result = createResult(level, inputs);
			cache.put(key, result);
		}

		return result;
	}

	public int getRequiredItems() {
		return 1;
	}

	public Object createKey(ItemStack[] inputs) {
		return inputs[0].getItem();
	}

	public abstract MachineProcessingResult createResult(Level level, ItemStack[] inputs);

	public boolean canInsert(Level level, int slot, ItemStack item) {
		return slot == 0 && getResult(level, new ItemStack[]{item}).exists();
	}
}
