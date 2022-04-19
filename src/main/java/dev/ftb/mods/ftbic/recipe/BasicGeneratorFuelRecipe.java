package dev.ftb.mods.ftbic.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class BasicGeneratorFuelRecipe implements Recipe<NoContainer> {
	public final ResourceLocation id;
	public Ingredient ingredient;
	public int ticks;

	public BasicGeneratorFuelRecipe(ResourceLocation id) {
		this.id = id;
	}

	@Override
	public boolean matches(NoContainer container, Level level) {
		return true;
	}

	@Override
	public ItemStack assemble(NoContainer container) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	public ItemStack getResultItem() {
		return ItemStack.EMPTY;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return FTBICRecipes.BASIC_GENERATOR_FUEL.get();
	}

	@Override
	public RecipeType<?> getType() {
		return FTBICRecipes.BASIC_GENERATOR_FUEL_TYPE.get();
	}
}
