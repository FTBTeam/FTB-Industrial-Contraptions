package dev.ftb.mods.ftbic.recipe;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.recipe.machines.CompressingRecipeResults;
import dev.ftb.mods.ftbic.recipe.machines.CookingRecipeResults;
import dev.ftb.mods.ftbic.recipe.machines.ExtrudingRecipeResults;
import dev.ftb.mods.ftbic.recipe.machines.MaceratingRecipeResults;
import dev.ftb.mods.ftbic.recipe.machines.ReprocessingRecipeResults;
import dev.ftb.mods.ftbic.recipe.machines.RollingRecipeResults;
import dev.ftb.mods.ftbic.recipe.machines.SeparatingRecipeResults;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class RecipeCache implements Recipe<NoContainer> {
	public static final ResourceLocation ID = new ResourceLocation(FTBIC.MOD_ID, "recipe_cache");

	private final Map<Item, Integer> basicGeneratorFuel = new HashMap<>();
	private final Map<Item, Double> antimatterBoost = new HashMap<>();
	public final CookingRecipeResults smelting = new CookingRecipeResults(FTBICRecipes.SMELTING, "smelting", RecipeType.SMELTING);
	public final MaceratingRecipeResults macerating = new MaceratingRecipeResults();
	public final SeparatingRecipeResults separating = new SeparatingRecipeResults();
	public final CompressingRecipeResults compressing = new CompressingRecipeResults();
	public final RollingRecipeResults rolling = new RollingRecipeResults();
	public final ExtrudingRecipeResults extruding = new ExtrudingRecipeResults();
	public final ReprocessingRecipeResults reprocessing = new ReprocessingRecipeResults();
	public final CanningMachineRecipeResults canning = new CanningMachineRecipeResults();

	@Nullable
	public static RecipeCache get(Level level) {
		// Replace this with AT for speed or make some mod that optimizes byKey()
		Recipe<?> r = level.getRecipeManager().byKey(ID).orElse(null);
		return r instanceof RecipeCache ? (RecipeCache) r : null;
	}

	@Override
	public boolean matches(NoContainer container, Level level) {
		return false;
	}

	@Override
	public ItemStack assemble(NoContainer container) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return false;
	}

	@Override
	public ItemStack getResultItem() {
		return ItemStack.EMPTY;
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return FTBICRecipes.RECIPE_CACHE.get();
	}

	@Override
	public RecipeType<?> getType() {
		return FTBICRecipes.RECIPE_CACHE_TYPE.get();
	}

	public int getBasicGeneratorFuelTicks(Level level, ItemStack item) {
		Integer fuel = basicGeneratorFuel.get(item.getItem());

		if (fuel != null) {
			return fuel;
		}

		for (BasicGeneratorFuelRecipe recipe : level.getRecipeManager().getAllRecipesFor(FTBICRecipes.BASIC_GENERATOR_FUEL_TYPE.get())) {
			if (recipe.ingredient.test(item)) {
				basicGeneratorFuel.put(item.getItem(), recipe.ticks);
				return recipe.ticks;
			}
		}

		basicGeneratorFuel.put(item.getItem(), 0);
		return 0;
	}

	public double getAntimatterBoost(Level level, ItemStack item) {
		Double boost = antimatterBoost.get(item.getItem());

		if (boost != null) {
			return boost;
		}

		for (AntimatterBoostRecipe recipe : level.getRecipeManager().getAllRecipesFor(FTBICRecipes.ANTIMATTER_BOOST_TYPE.get())) {
			if (recipe.ingredient.test(item)) {
				antimatterBoost.put(item.getItem(), recipe.boost);
				return recipe.boost;
			}
		}

		antimatterBoost.put(item.getItem(), 0D);
		return 0;
	}
}
