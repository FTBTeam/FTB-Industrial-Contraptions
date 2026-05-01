package dev.ftb.mods.ftbic.net;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@EventBusSubscriber(modid = FTBIC.MOD_ID)
public final class FTBICRecipeSyncHandler {

	@SubscribeEvent
	public static void onDatapackSync(OnDatapackSyncEvent event) {
		RecipeType<?>[] types = FTBICRecipes.TYPES.getEntries().stream()
				.map(DeferredHolder::get)
				.toArray(RecipeType<?>[]::new);

		event.sendRecipes(types);

		MinecraftServer server = event.getPlayerList().getServer();
		List<RecipeHolder<?>> all = collect(server, types);
		if (event.getPlayer() != null) {
			FTBICNet.sendToPlayer(event.getPlayer(), new FTBICRecipeSyncPayload(all));
		} else {
			for (ServerPlayer sp : event.getPlayerList().getPlayers()) {
				FTBICNet.sendToPlayer(sp, new FTBICRecipeSyncPayload(all));
			}
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static List<RecipeHolder<?>> collect(MinecraftServer server, RecipeType<?>[] types) {
		var map = server.getRecipeManager().recipeMap();
		List<RecipeHolder<?>> out = new ArrayList<>();
		for (RecipeType<?> t : types) {
			out.addAll((Collection<? extends RecipeHolder<?>>) map.byType((RecipeType) t));
		}
		out.addAll((Collection<? extends RecipeHolder<?>>) map.byType((RecipeType) RecipeType.SMELTING));
		return out;
	}

	private FTBICRecipeSyncHandler() {}
}
