package dev.ftb.mods.ftbic.net;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import dev.ftb.mods.ftbic.recipe.MachineRecipeType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@EventBusSubscriber(modid = FTBIC.MOD_ID)
public final class FTBICRecipeSyncHandler {

	@SubscribeEvent
	public static void onDatapackSync(OnDatapackSyncEvent event) {
		event.sendRecipes(
				FTBICRecipes.SMELTING.TYPE.get(),
				FTBICRecipes.MACERATING.TYPE.get(),
				FTBICRecipes.SEPARATING.TYPE.get(),
				FTBICRecipes.COMPRESSING.TYPE.get(),
				FTBICRecipes.REPROCESSING.TYPE.get(),
				FTBICRecipes.CANNING.TYPE.get(),
				FTBICRecipes.ROLLING.TYPE.get(),
				FTBICRecipes.EXTRUDING.TYPE.get(),
				FTBICRecipes.BASIC_GENERATOR_FUEL.get(),
				FTBICRecipes.ANTIMATTER_BOOST.get());

		MinecraftServer server = event.getPlayerList().getServer();
		List<RecipeHolder<?>> all = collect(server);
		if (event.getPlayer() != null) {
			FTBICNet.sendToPlayer(event.getPlayer(), new FTBICRecipeSyncPayload(all));
		} else {
			for (ServerPlayer sp : event.getPlayerList().getPlayers()) {
				FTBICNet.sendToPlayer(sp, new FTBICRecipeSyncPayload(all));
			}
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static List<RecipeHolder<?>> collect(MinecraftServer server) {
		var map = server.getRecipeManager().recipeMap();
		List<RecipeHolder<?>> out = new ArrayList<>();
		RecipeType<?>[] types = new RecipeType<?>[] {
				FTBICRecipes.SMELTING.TYPE.get(), FTBICRecipes.MACERATING.TYPE.get(), FTBICRecipes.SEPARATING.TYPE.get(),
				FTBICRecipes.COMPRESSING.TYPE.get(), FTBICRecipes.REPROCESSING.TYPE.get(), FTBICRecipes.CANNING.TYPE.get(),
				FTBICRecipes.ROLLING.TYPE.get(), FTBICRecipes.EXTRUDING.TYPE.get(),
				FTBICRecipes.BASIC_GENERATOR_FUEL.get(), FTBICRecipes.ANTIMATTER_BOOST.get(),
		};
		for (RecipeType<?> t : types) {
			out.addAll((Collection<? extends RecipeHolder<?>>) map.byType((RecipeType) t));
		}
		out.addAll((Collection<? extends RecipeHolder<?>>) map.byType((RecipeType) RecipeType.SMELTING));
		return out;
	}

	private FTBICRecipeSyncHandler() {}
}
