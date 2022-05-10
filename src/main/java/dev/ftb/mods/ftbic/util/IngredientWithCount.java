package dev.ftb.mods.ftbic.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;

public class IngredientWithCount {
	public static final IngredientWithCount[] EMPTY = new IngredientWithCount[0];

	public final Ingredient ingredient;
	public final int count;

	public IngredientWithCount(Ingredient in, int c) {
		ingredient = in;
		count = c;
	}

	public IngredientWithCount(FriendlyByteBuf buf) {
		ingredient = Ingredient.fromNetwork(buf);
		count = buf.readVarInt();
	}

	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.add("ingredient", ingredient.toJson());
		json.addProperty("count", count);
		return json;
	}

	public void write(FriendlyByteBuf buf) {
		ingredient.toNetwork(buf);
		buf.writeVarInt(count);
	}

	public static IngredientWithCount deserialize(@Nullable JsonElement json) {
		if (json != null && !json.isJsonNull()) {
			if (json.isJsonObject()) {
				JsonObject obj = json.getAsJsonObject();

				Ingredient ingredient = obj.has("ingredient") ? Ingredient.fromJson(obj.get("ingredient")) : Ingredient.fromJson(obj);
				int count = GsonHelper.getAsInt(obj, "count", 1);
				return new IngredientWithCount(ingredient, count);
			} else {
				Ingredient ingredient = Ingredient.fromJson(json);
				return new IngredientWithCount(ingredient, 1);
			}
		} else {
			throw new JsonSyntaxException("Ingredient stack cannot be null!");
		}
	}
}
