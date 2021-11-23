package dev.ftb.mods.ftbic.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

public class AntimatterBoostRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<AntimatterBoostRecipe> {
	@Override
	public AntimatterBoostRecipe fromJson(ResourceLocation id, JsonObject json) {
		AntimatterBoostRecipe recipe = new AntimatterBoostRecipe(id);
		recipe.ingredient = CraftingHelper.getIngredient(json.get("ingredient"));
		recipe.boost = json.get("boost").getAsDouble();
		return recipe;
	}

	@Nullable
	@Override
	public AntimatterBoostRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
		AntimatterBoostRecipe recipe = new AntimatterBoostRecipe(id);
		recipe.ingredient = Ingredient.fromNetwork(buf);
		recipe.boost = buf.readDouble();
		return recipe;
	}

	@Override
	public void toNetwork(FriendlyByteBuf buf, AntimatterBoostRecipe recipe) {
		recipe.ingredient.toNetwork(buf);
		buf.writeDouble(recipe.boost);
	}
}
