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

public class MachineFuelRecipeBuilder {
	private RecipeSerializer<?> serializer;
	private Ingredient ingredient;
	private int ticks;
	private final Advancement.Builder advancement = Advancement.Builder.advancement();

	public static MachineFuelRecipeBuilder basicGenerator(Ingredient ingredient, int ticks) {
		MachineFuelRecipeBuilder builder = new MachineFuelRecipeBuilder();
		builder.serializer = FTBICRecipes.BASIC_GENERATOR_FUEL.get();
		builder.ingredient = ingredient;
		builder.ticks = ticks;
		return builder;
	}

	public MachineFuelRecipeBuilder unlockedBy(String string, CriterionTriggerInstance arg) {
		advancement.addCriterion(string, arg);
		return this;
	}

	public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
		advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(RequirementsStrategy.OR);
		consumer.accept(new Result(this, id, new ResourceLocation(id.getNamespace(), "recipes/ftbic_fuel/" + id.getPath())));
	}

	public static class Result implements FinishedRecipe {
		private final MachineFuelRecipeBuilder builder;
		private final ResourceLocation id;
		private final ResourceLocation advancementId;

		private Result(MachineFuelRecipeBuilder b, ResourceLocation i, ResourceLocation a) {
			builder = b;
			id = i;
			advancementId = a;
		}

		@Override
		public void serializeRecipeData(JsonObject json) {
			json.add("ingredient", builder.ingredient.toJson());
			json.addProperty("ticks", builder.ticks);
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
