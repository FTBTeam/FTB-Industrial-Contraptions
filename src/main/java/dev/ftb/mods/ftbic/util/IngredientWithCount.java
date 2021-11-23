package dev.ftb.mods.ftbic.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;

public class IngredientWithCount {
	public static final IngredientWithCount[] EMPTY = new IngredientWithCount[0];

	public final Ingredient ingredient;
	public final int count;

	public IngredientWithCount(Ingredient in, int c) {
		ingredient = in;
		count = c;
	}

	public IngredientWithCount(JsonElement element) {
		JsonObject json = element.getAsJsonObject();
		ingredient = Ingredient.fromJson(json.get("ingredient"));
		count = json.has("count") ? json.get("count").getAsInt() : 1;
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
}
