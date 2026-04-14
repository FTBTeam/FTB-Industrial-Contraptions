package dev.ftb.mods.ftbic.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * RecipeInput with no slots. FTBIC machines call `matches(recipe, level)` with this sentinel — the
 * actual input matching against the machine's internal input buffer happens separately in the BE.
 */
public final class NoInput implements RecipeInput {
	public static final NoInput INSTANCE = new NoInput();

	private NoInput() {}

	@Override
	public ItemStack getItem(int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}
}
