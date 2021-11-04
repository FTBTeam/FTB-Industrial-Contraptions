package dev.ftb.mods.ftbic.recipe;

import dev.ftb.mods.ftbic.FTBIC;
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
	public final CookingRecipeResults smelting = new CookingRecipeResults(RecipeType.SMELTING);
	public final MaceratingRecipeResults macerating = new MaceratingRecipeResults();
	public final ExtractingRecipeResults extracting = new ExtractingRecipeResults();
	public final CompressingRecipeResults compressing = new CompressingRecipeResults();
	public final RollingRecipeResults rolling = new RollingRecipeResults();
	public final ExtrudingRecipeResults extruding = new ExtrudingRecipeResults();
	public final CanningMachineRecipeResults canning = new CanningMachineRecipeResults();
	public final RecyclingRecipeResults recycling = new RecyclingRecipeResults();
	public final AntimatterFabricatorRecipeResults antimatterFabricator = new AntimatterFabricatorRecipeResults();

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

		basicGeneratorFuel.put(item, 0);
		return 0;
	}
}
