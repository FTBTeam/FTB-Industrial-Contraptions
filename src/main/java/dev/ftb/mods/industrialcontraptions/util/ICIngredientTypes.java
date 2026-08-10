package dev.ftb.mods.industrialcontraptions.util;

import dev.ftb.mods.industrialcontraptions.IC;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ICIngredientTypes {
	public static final DeferredRegister<IngredientType<?>> REGISTRY =
			DeferredRegister.create(NeoForgeRegistries.INGREDIENT_TYPES, IC.MOD_ID);

	public static final DeferredHolder<IngredientType<?>, IngredientType<FluidCellIngredient>> FLUID_CELL =
			REGISTRY.register("fluid_cell",
					() -> new IngredientType<>(FluidCellIngredient.CODEC, FluidCellIngredient.STREAM_CODEC));

	private ICIngredientTypes() {}
}
