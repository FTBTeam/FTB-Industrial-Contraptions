package dev.ftb.mods.ftbic.datagen.recipies;

import com.google.gson.JsonObject;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class AntimatterBoostRecipeBuilder {
	private RecipeSerializer<?> serializer;
	private Ingredient ingredient;
	private double boost;
	private final Advancement.Builder advancement = Advancement.Builder.advancement();

	public static AntimatterBoostRecipeBuilder make(Ingredient ingredient, double boost) {
		AntimatterBoostRecipeBuilder builder = new AntimatterBoostRecipeBuilder();
		builder.serializer = FTBICRecipes.ANTIMATTER_BOOST.get();
		builder.ingredient = ingredient;
		builder.boost = boost;
		return builder;
	}

	public AntimatterBoostRecipeBuilder unlockedBy(String string, CriterionTriggerInstance arg) {
		advancement.addCriterion(string, arg);
		return this;
	}

	public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
		advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(RequirementsStrategy.OR);
		consumer.accept(new Result(this, id, new ResourceLocation(id.getNamespace(), "recipes/ftbic_antimatter_boost/" + id.getPath())));
	}

	public static class Result implements FinishedRecipe {
		private final AntimatterBoostRecipeBuilder builder;
		private final ResourceLocation id;
		private final ResourceLocation advancementId;

		private Result(AntimatterBoostRecipeBuilder b, ResourceLocation i, ResourceLocation a) {
			builder = b;
			id = i;
			advancementId = a;
		}

		@Override
		public void serializeRecipeData(JsonObject json) {
			json.add("ingredient", builder.ingredient.toJson());
			json.addProperty("boost", builder.boost);
		}

		@Override
		public ResourceLocation getId() {
			return id;
		}

		@Override
		public RecipeSerializer<?> getType() {
			return builder.serializer;
		}

		@Nullable
		@Override
		public JsonObject serializeAdvancement() {
			return builder.advancement.serializeToJson();
		}

		@Nullable
		@Override
		public ResourceLocation getAdvancementId() {
			return advancementId;
		}
	}
}
