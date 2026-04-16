package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.FTBIC;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class FTBICIngredientTypes {
	public static final DeferredRegister<IngredientType<?>> REGISTRY =
			DeferredRegister.create(NeoForgeRegistries.INGREDIENT_TYPES, FTBIC.MOD_ID);

	public static final DeferredHolder<IngredientType<?>, IngredientType<FluidCellIngredient>> FLUID_CELL =
			REGISTRY.register("fluid_cell",
					() -> new IngredientType<>(FluidCellIngredient.CODEC, FluidCellIngredient.STREAM_CODEC));

	private FTBICIngredientTypes() {}
}
