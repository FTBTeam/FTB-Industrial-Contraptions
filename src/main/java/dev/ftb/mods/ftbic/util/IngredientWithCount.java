package dev.ftb.mods.ftbic.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.item.crafting.Ingredient;

public class IngredientWithCount {
	public final Ingredient ingredient;
	public final int count;

	public IngredientWithCount(Ingredient in, int c) {
		ingredient = in;
		count = c;
	}

	public IngredientWithCount(JsonElement json) {
		JsonObject o = json.getAsJsonObject();
		ingredient = Ingredient.fromJson(o.get("ingredient"));
		count = o.has("count") ? o.get("count").getAsInt() : 1;
	}

	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.add("ingredient", ingredient.toJson());
		json.addProperty("count", count);
		return json;
	}
}
