package dev.ftb.mods.ftbic.recipe;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;
import java.util.function.Supplier;

public abstract class FTBICRecipes {
	public static final DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, FTBIC.MOD_ID);
	public static final DeferredRegister<RecipeType<?>> REGISTRY_TYPE = DeferredRegister.create(Registry.RECIPE_TYPE_REGISTRY, FTBIC.MOD_ID);

	public static Supplier<MachineRecipeSerializer> machine(String id, Function<MachineRecipeSerializer, MachineRecipeSerializer> properties) {
		RegistryObject<RecipeType<Recipe<?>>> type = registerRecipeType(id);
		return REGISTRY.register(id, () -> properties.apply(new MachineRecipeSerializer(type)));
	}

	public static <T extends Recipe<?>> RegistryObject<RecipeType<T>> registerRecipeType(final String string) {
		return REGISTRY_TYPE.register(string, () -> new RecipeType<T>() {
			public String toString() {
				return string;
			}
		});
	}

	public static final Supplier<RecipeCacheSerializer> RECIPE_CACHE = REGISTRY.register("recipe_cache", RecipeCacheSerializer::new);
	public static RegistryObject<RecipeType<RecipeCache>> RECIPE_CACHE_TYPE = REGISTRY_TYPE.register("recipe_cache", () -> new RecipeType<>() {});

	public static final Supplier<BasicGeneratorFuelRecipeSerializer> BASIC_GENERATOR_FUEL = REGISTRY.register("basic_generator_fuel", BasicGeneratorFuelRecipeSerializer::new);
	public static RegistryObject<RecipeType<BasicGeneratorFuelRecipe>> BASIC_GENERATOR_FUEL_TYPE = REGISTRY_TYPE.register("basic_generator_fuel", () -> new RecipeType<>() {});

	public static final Supplier<AntimatterBoostRecipeSerializer> ANTIMATTER_BOOST = REGISTRY.register("antimatter_boost", AntimatterBoostRecipeSerializer::new);
	public static RegistryObject<RecipeType<AntimatterBoostRecipe>> ANTIMATTER_BOOST_TYPE = REGISTRY_TYPE.register("antimatter_boost", () -> new RecipeType<>() {});

	public static final Supplier<MachineRecipeSerializer> SMELTING = machine("smelting", Function.identity());
	public static final Supplier<MachineRecipeSerializer> MACERATING = machine("macerating", MachineRecipeSerializer::extraOutput);
	public static final Supplier<MachineRecipeSerializer> SEPARATING = machine("separating", MachineRecipeSerializer::extraOutput);
	public static final Supplier<MachineRecipeSerializer> COMPRESSING = machine("compressing", Function.identity());
	public static final Supplier<MachineRecipeSerializer> REPROCESSING = machine("reprocessing", Function.identity());
	public static final Supplier<MachineRecipeSerializer> CANNING = machine("canning", MachineRecipeSerializer::twoInputs);
	public static final Supplier<MachineRecipeSerializer> ROLLING = machine("rolling", Function.identity());
	public static final Supplier<MachineRecipeSerializer> EXTRUDING = machine("extruding", Function.identity());
}
