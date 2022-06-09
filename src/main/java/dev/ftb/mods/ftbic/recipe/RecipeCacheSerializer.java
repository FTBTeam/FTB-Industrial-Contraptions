package dev.ftb.mods.ftbic.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

public class RecipeCacheSerializer implements RecipeSerializer<RecipeCache> {
	@Override
	public RecipeCache fromJson(ResourceLocation id, JsonObject json) {
		return new RecipeCache();
	}

	@Nullable
	@Override
	public RecipeCache fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
		return new RecipeCache();
	}

	@Override
	public void toNetwork(FriendlyByteBuf buf, RecipeCache recipe) {
	}
}
