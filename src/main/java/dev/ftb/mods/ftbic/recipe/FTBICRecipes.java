package dev.ftb.mods.ftbic.recipe;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public interface FTBICRecipes {
	DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, FTBIC.MOD_ID);

	static Supplier<MachineRecipeSerializer> machine(String id, int inItems, int inFluids, int outItems, int outFluids) {
		return REGISTRY.register(id, () -> new MachineRecipeSerializer(id, inItems, inFluids, outItems, outFluids));
	}

	Supplier<RecipeCacheSerializer> RECIPE_CACHE = REGISTRY.register("recipe_cache", RecipeCacheSerializer::new);
	RecipeType<RecipeCache> RECIPE_CACHE_TYPE = RecipeType.register(FTBIC.MOD_ID + ":recipe_cache");

	Supplier<BasicGeneratorFuelRecipeSerializer> BASIC_GENERATOR_FUEL = REGISTRY.register("basic_generator_fuel", BasicGeneratorFuelRecipeSerializer::new);
	RecipeType<BasicGeneratorFuelRecipe> BASIC_GENERATOR_FUEL_TYPE = RecipeType.register(FTBIC.MOD_ID + ":basic_generator_fuel");

	Supplier<MachineRecipeSerializer> MACERATING = machine("macerating", 1, 0, 1, 0);
	Supplier<MachineRecipeSerializer> EXTRACTING = machine("extracting", 1, 0, 1, 0);
	Supplier<MachineRecipeSerializer> COMPRESSING = machine("compressing", 1, 0, 1, 0);
	Supplier<MachineRecipeSerializer> ELECTROLYZING = machine("electrolyzing", 1, 0, 1, 0);
	Supplier<MachineRecipeSerializer> RECYCLING = machine("recycling", 1, 0, 1, 0);
	Supplier<MachineRecipeSerializer> CANNING = machine("canning", 2, 0, 1, 0);
}
