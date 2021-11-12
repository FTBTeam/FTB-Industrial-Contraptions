package dev.ftb.mods.ftbic.recipe;

import dev.ftb.mods.ftbic.util.IngredientWithCount;
import dev.ftb.mods.ftbic.util.StackWithChance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class MachineRecipe implements Recipe<NoContainer> {
	public final MachineRecipeSerializer serializer;
	public final ResourceLocation id;
	public boolean realRecipe;
	public List<IngredientWithCount> inputItems;
	public List<FluidStack> inputFluids;
	public List<StackWithChance> outputItems;
	public List<FluidStack> outputFluids;
	public double processingTime;
	public boolean hideFromJEI;

	public MachineRecipe(MachineRecipeSerializer s, ResourceLocation i) {
		serializer = s;
		id = i;
		realRecipe = false;
		inputItems = new ArrayList<>(1);
		inputFluids = new ArrayList<>(0);
		outputItems = new ArrayList<>(1);
		outputFluids = new ArrayList<>(0);
		processingTime = 1D;
		hideFromJEI = false;
	}

	@Override
	public boolean matches(NoContainer container, Level level) {
		return false;
	}

	@Override
	public ItemStack assemble(NoContainer container) {
		return getResultItem().copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return false;
	}

	@Override
	public ItemStack getResultItem() {
		return outputItems.isEmpty() ? ItemStack.EMPTY : outputItems.get(0).stack;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return serializer;
	}

	@Override
	public RecipeType<?> getType() {
		return serializer.recipeType;
	}

	public boolean isVisibleJEI() {
		return !hideFromJEI;
	}

	public boolean isRealAndVisibleJEI() {
		return realRecipe && !hideFromJEI;
	}
}
