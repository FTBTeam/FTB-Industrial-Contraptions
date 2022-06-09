package dev.ftb.mods.ftbic.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import org.jetbrains.annotations.Nullable;

public class BasicGeneratorFuelRecipeSerializer implements RecipeSerializer<BasicGeneratorFuelRecipe> {
	@Override
	public BasicGeneratorFuelRecipe fromJson(ResourceLocation id, JsonObject json) {
		BasicGeneratorFuelRecipe recipe = new BasicGeneratorFuelRecipe(id);
		recipe.ingredient = CraftingHelper.getIngredient(json.get("ingredient"));
		recipe.ticks = json.get("ticks").getAsInt();
		return recipe;
	}

	@Nullable
	@Override
	public BasicGeneratorFuelRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
		BasicGeneratorFuelRecipe recipe = new BasicGeneratorFuelRecipe(id);
		recipe.ingredient = Ingredient.fromNetwork(buf);
		recipe.ticks = buf.readVarInt();
		return recipe;
	}

	@Override
	public void toNetwork(FriendlyByteBuf buf, BasicGeneratorFuelRecipe recipe) {
		recipe.ingredient.toNetwork(buf);
		buf.writeVarInt(recipe.ticks);
	}
}
