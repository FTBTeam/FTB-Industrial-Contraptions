package dev.ftb.mods.ftbic.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class MaceratingRecipe extends MachineRecipe {
	public MaceratingRecipe(ResourceLocation id) {
		super(id);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return FTBICRecipes.MACERATING.get();
	}

	@Override
	public RecipeType<?> getType() {
		return FTBICRecipes.MACERATING.get().recipeType;
	}
}
