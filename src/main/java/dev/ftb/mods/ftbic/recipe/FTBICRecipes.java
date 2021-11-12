package dev.ftb.mods.ftbic.recipe;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Function;
import java.util.function.Supplier;

public interface FTBICRecipes {
	DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, FTBIC.MOD_ID);

	static Supplier<MachineRecipeSerializer> machine(String id, Function<MachineRecipeSerializer, MachineRecipeSerializer> properties) {
		return REGISTRY.register(id, () -> properties.apply(new MachineRecipeSerializer(id)));
	}

	Supplier<RecipeCacheSerializer> RECIPE_CACHE = REGISTRY.register("recipe_cache", RecipeCacheSerializer::new);
	RecipeType<RecipeCache> RECIPE_CACHE_TYPE = RecipeType.register(FTBIC.MOD_ID + ":recipe_cache");

	Supplier<BasicGeneratorFuelRecipeSerializer> BASIC_GENERATOR_FUEL = REGISTRY.register("basic_generator_fuel", BasicGeneratorFuelRecipeSerializer::new);
	RecipeType<BasicGeneratorFuelRecipe> BASIC_GENERATOR_FUEL_TYPE = RecipeType.register(FTBIC.MOD_ID + ":basic_generator_fuel");

	Supplier<MachineRecipeSerializer> SMELTING = machine("smelting", Function.identity());
	Supplier<MachineRecipeSerializer> MACERATING = machine("macerating", MachineRecipeSerializer::extraOutput);
	Supplier<MachineRecipeSerializer> SEPARATING = machine("separating", MachineRecipeSerializer::extraOutput);
	Supplier<MachineRecipeSerializer> COMPRESSING = machine("compressing", Function.identity());
	Supplier<MachineRecipeSerializer> CANNING = machine("canning", MachineRecipeSerializer::twoInputs);
	Supplier<MachineRecipeSerializer> ROLLING = machine("rolling", Function.identity());
	Supplier<MachineRecipeSerializer> EXTRUDING = machine("extruding", Function.identity());
	Supplier<MachineRecipeSerializer> RECONSTRUCTING = machine("reconstructing", Function.identity());
	Supplier<MachineRecipeSerializer> REPROCESSING = machine("reprocessing", Function.identity());
}
