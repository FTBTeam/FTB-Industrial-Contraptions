package dev.ftb.mods.ftbic.recipe;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Function;
import java.util.function.Supplier;

public abstract class FTBICRecipes {
	public static final DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, FTBIC.MOD_ID);

	public static Supplier<MachineRecipeSerializer> machine(String id, Function<MachineRecipeSerializer, MachineRecipeSerializer> properties) {
		return REGISTRY.register(id, () -> properties.apply(new MachineRecipeSerializer(id)));
	}

	public static final Supplier<RecipeCacheSerializer> RECIPE_CACHE = REGISTRY.register("recipe_cache", RecipeCacheSerializer::new);
	public static RecipeType<RecipeCache> RECIPE_CACHE_TYPE;

	public static final Supplier<BasicGeneratorFuelRecipeSerializer> BASIC_GENERATOR_FUEL = REGISTRY.register("basic_generator_fuel", BasicGeneratorFuelRecipeSerializer::new);
	public static RecipeType<BasicGeneratorFuelRecipe> BASIC_GENERATOR_FUEL_TYPE;

	public static final Supplier<AntimatterBoostRecipeSerializer> ANTIMATTER_BOOST = REGISTRY.register("antimatter_boost", AntimatterBoostRecipeSerializer::new);
	public static RecipeType<AntimatterBoostRecipe> ANTIMATTER_BOOST_TYPE;

	public static final Supplier<MachineRecipeSerializer> SMELTING = machine("smelting", Function.identity());
	public static final Supplier<MachineRecipeSerializer> MACERATING = machine("macerating", MachineRecipeSerializer::extraOutput);
	public static final Supplier<MachineRecipeSerializer> SEPARATING = machine("separating", MachineRecipeSerializer::extraOutput);
	public static final Supplier<MachineRecipeSerializer> COMPRESSING = machine("compressing", Function.identity());
	public static final Supplier<MachineRecipeSerializer> REPROCESSING = machine("reprocessing", Function.identity());
	public static final Supplier<MachineRecipeSerializer> CANNING = machine("canning", MachineRecipeSerializer::twoInputs);
	public static final Supplier<MachineRecipeSerializer> ROLLING = machine("rolling", Function.identity());
	public static final Supplier<MachineRecipeSerializer> EXTRUDING = machine("extruding", Function.identity());

	public static void onItemRegistry(RegistryEvent.Register<Item> event) {
		RECIPE_CACHE_TYPE = RecipeType.register(FTBIC.MOD_ID + ":recipe_cache");
		BASIC_GENERATOR_FUEL_TYPE = RecipeType.register(FTBIC.MOD_ID + ":basic_generator_fuel");
		ANTIMATTER_BOOST_TYPE = RecipeType.register(FTBIC.MOD_ID + ":antimatter_boost");
	}
}
