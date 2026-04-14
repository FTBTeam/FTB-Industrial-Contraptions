package dev.ftb.mods.ftbic.integration.jei;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import dev.ftb.mods.ftbic.recipe.MachineRecipe;
import dev.ftb.mods.ftbic.recipe.MachineRecipeType;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Client-side recipe cache populated from the {@link RecipesReceivedEvent} (fired when the server
 * ships us {@link net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket} at login or
 * after a datapack reload). JEI's plugin reads this cache in {@code onRuntimeAvailable} and again
 * each time the event fires, so category recipe listings stay in sync with the server.
 *
 * Why this exists: in 26.1 {@code ClientLevel.recipeAccess()} only exposes the vanilla property
 * sets + stonecutter recipes — not a general recipe-map iteration — so we intercept the raw
 * RecipeMap as it arrives and hand our recipes to JEI's runtime manager directly.
 */
@EventBusSubscriber(modid = FTBIC.MOD_ID, value = net.neoforged.api.distmarker.Dist.CLIENT)
public final class ClientRecipeCache {
	private static IJeiRuntime runtime;
	private static final Map<RecipeType<?>, List<RecipeHolder<?>>> CACHE = new HashMap<>();

	public static synchronized void setRuntime(IJeiRuntime jeiRuntime) {
		runtime = jeiRuntime;
		pushToJei();
	}

	public static synchronized void clearRuntime() {
		runtime = null;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static synchronized void showRecipesForType(RecipeType<?> vanillaType) {
		if (runtime == null) return;
		mezz.jei.api.recipe.types.IRecipeHolderType<?> jeiType = mezz.jei.api.recipe.types.IRecipeHolderType.create((RecipeType) vanillaType);
		runtime.getRecipesGui().showTypes(List.of((mezz.jei.api.recipe.types.IRecipeType) jeiType));
	}

	/** Fires when the server updates recipes (login or /reload). Snapshot our recipe types. */
	@SubscribeEvent
	public static void onRecipesReceived(RecipesReceivedEvent event) {
		RecipeMap map = event.getRecipeMap();
		CACHE.clear();

		for (MachineRecipeType mrt : new MachineRecipeType[] {
				FTBICRecipes.SMELTING, FTBICRecipes.MACERATING, FTBICRecipes.SEPARATING,
				FTBICRecipes.COMPRESSING, FTBICRecipes.REPROCESSING, FTBICRecipes.CANNING,
				FTBICRecipes.ROLLING, FTBICRecipes.EXTRUDING,
		}) {
			RecipeType<MachineRecipe> rt = mrt.TYPE.get();
			List<RecipeHolder<?>> list = new ArrayList<>(map.byType(rt));
			CACHE.put(rt, list);
		}

		@SuppressWarnings({"unchecked", "rawtypes"})
		RecipeType<dev.ftb.mods.ftbic.recipe.BasicGeneratorFuelRecipe> fuelType =
				(RecipeType<dev.ftb.mods.ftbic.recipe.BasicGeneratorFuelRecipe>) (RecipeType) FTBICRecipes.BASIC_GENERATOR_FUEL.get();
		CACHE.put(fuelType, new ArrayList<>(map.byType(fuelType)));
		@SuppressWarnings({"unchecked", "rawtypes"})
		RecipeType<dev.ftb.mods.ftbic.recipe.AntimatterBoostRecipe> boostType =
				(RecipeType<dev.ftb.mods.ftbic.recipe.AntimatterBoostRecipe>) (RecipeType) FTBICRecipes.ANTIMATTER_BOOST.get();
		CACHE.put(boostType, new ArrayList<>(map.byType(boostType)));

		pushToJei();
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static synchronized void pushToJei() {
		if (runtime == null) return;

		for (Map.Entry<RecipeType<?>, List<RecipeHolder<?>>> entry : CACHE.entrySet()) {
			RecipeType<?> vanillaType = entry.getKey();
			List<RecipeHolder<?>> holders = entry.getValue();
			if (holders.isEmpty()) continue;

			IRecipeHolderType<?> jeiType = IRecipeHolderType.create((RecipeType) vanillaType);
			try {
				runtime.getRecipeManager().addRecipes((IRecipeType) jeiType, (List) holders);
			} catch (IllegalStateException ignored) {
				// Category not registered (unexpected) — skip.
			}
		}
	}

	private ClientRecipeCache() {}
}
