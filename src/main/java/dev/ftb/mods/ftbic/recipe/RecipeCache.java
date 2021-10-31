package dev.ftb.mods.ftbic.recipe;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.util.CachedItemProcessingResult;
import dev.ftb.mods.ftbic.util.IngredientWithCount;
import dev.ftb.mods.ftbic.util.ItemPair;
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
import java.util.function.Supplier;

public class RecipeCache implements Recipe<NoContainer> {
	public static final ResourceLocation ID = new ResourceLocation(FTBIC.MOD_ID, "recipe_cache");

	private final Map<Item, Integer> basicGeneratorFuel = new HashMap<>();
	private final Map<Item, CachedItemProcessingResult> furnaceRecipes = new HashMap<>();
	private final Map<Item, CachedItemProcessingResult> maceratingRecipes = new HashMap<>();
	private final Map<Item, CachedItemProcessingResult> extractingRecipes = new HashMap<>();
	private final Map<Item, CachedItemProcessingResult> compressingRecipes = new HashMap<>();
	private final Map<Item, CachedItemProcessingResult> recyclingRecipes = new HashMap<>();
	private final Map<ItemPair, CachedItemProcessingResult> canningRecipes = new HashMap<>();

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
		return FTBICRecipes.RECIPE_CACHE_TYPE;
	}

	public int getBasicGeneratorFuelTicks(Level level, Item item) {
		Integer fuel = basicGeneratorFuel.get(item);

		if (fuel != null) {
			return fuel;
		}

		ItemStack itemStack = new ItemStack(item);

		for (BasicGeneratorFuelRecipe recipe : level.getRecipeManager().getAllRecipesFor(FTBICRecipes.BASIC_GENERATOR_FUEL_TYPE)) {
			if (recipe.ingredient.test(itemStack)) {
				basicGeneratorFuel.put(item, recipe.ticks);
				return recipe.ticks;
			}
		}

		return 0;
	}

	public CachedItemProcessingResult getFurnaceResult(Level level, Item item) {
		return CachedItemProcessingResult.NONE;
	}

	private CachedItemProcessingResult getResult(Level level, Item item, Map<Item, CachedItemProcessingResult> cache, Supplier<MachineRecipeSerializer> type) {
		Item key = item.getItem();
		CachedItemProcessingResult result = cache.get(key);

		if (result == null) {
			result = CachedItemProcessingResult.NONE;
			ItemStack itemStack = new ItemStack(item.getItem());

			for (MachineRecipe recipe : level.getRecipeManager().getAllRecipesFor(type.get().recipeType)) {
				if (recipe.inputItems.size() == 1 && recipe.outputItems.size() >= 1) {
					IngredientWithCount c = recipe.inputItems.get(0);

					if (c.ingredient.test(itemStack)) {
						result = new CachedItemProcessingResult(recipe);
						result.consume[0] = c.count;
						break;
					}
				}
			}

			cache.put(key, result);
		}

		return result;
	}

	private CachedItemProcessingResult getResult(Level level, Item itemA, Item itemB, Map<ItemPair, CachedItemProcessingResult> cache, Supplier<MachineRecipeSerializer> type) {
		ItemPair key = new ItemPair(itemA, itemB);
		CachedItemProcessingResult result = cache.get(key);

		if (result == null) {
			result = CachedItemProcessingResult.NONE;
			ItemStack itemStackA = new ItemStack(itemA);
			ItemStack itemStackB = new ItemStack(itemB);

			for (MachineRecipe recipe : level.getRecipeManager().getAllRecipesFor(type.get().recipeType)) {
				if (recipe.inputItems.size() == 2 && recipe.outputItems.size() >= 1) {
					IngredientWithCount cA = recipe.inputItems.get(0);
					IngredientWithCount cB = recipe.inputItems.get(1);

					if (cA.ingredient.test(itemStackA) && cB.ingredient.test(itemStackB)) {
						result = new CachedItemProcessingResult(recipe);
						result.consume[0] = cA.count;
						result.consume[1] = cB.count;
						break;
					} else if (cA.ingredient.test(itemStackB) && cB.ingredient.test(itemStackA)) {
						result = new CachedItemProcessingResult(recipe);
						result.consume[0] = cB.count;
						result.consume[1] = cA.count;
						break;
					}
				}
			}

			cache.put(key, result);
		}

		return result;
	}

	public CachedItemProcessingResult getMaceratingResult(Level level, Item item) {
		return getResult(level, item, maceratingRecipes, FTBICRecipes.MACERATING);
	}

	public CachedItemProcessingResult getExtractingResult(Level level, Item item) {
		return getResult(level, item, extractingRecipes, FTBICRecipes.EXTRACTING);
	}

	public CachedItemProcessingResult getCompressingResult(Level level, Item item) {
		return getResult(level, item, compressingRecipes, FTBICRecipes.COMPRESSING);
	}

	public CachedItemProcessingResult getCanningResult(Level level, Item itemA, Item itemB) {
		return getResult(level, itemA, itemB, canningRecipes, FTBICRecipes.CANNING);
	}
}
