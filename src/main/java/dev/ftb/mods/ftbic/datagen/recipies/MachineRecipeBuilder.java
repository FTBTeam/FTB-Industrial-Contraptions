package dev.ftb.mods.ftbic.datagen.recipies;

import com.google.gson.JsonObject;
import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import dev.ftb.mods.ftbic.recipe.MachineRecipe;
import dev.ftb.mods.ftbic.recipe.MachineRecipeSerializer;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import dev.ftb.mods.ftbic.util.IngredientWithCount;
import dev.ftb.mods.ftbic.util.StackWithChance;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MachineRecipeBuilder {
	private static final ResourceLocation DUMMY_ID = new ResourceLocation(FTBIC.MOD_ID, "dummy");

	private MachineRecipe recipe;
	private final Advancement.Builder advancement = Advancement.Builder.advancement();

	public static MachineRecipeBuilder machine(Supplier<MachineRecipeSerializer> type) {
		MachineRecipeBuilder builder = new MachineRecipeBuilder();
		builder.recipe = new MachineRecipe(type.get(), DUMMY_ID);
		builder.recipe.realRecipe = true;
		builder.recipe.inputItems = new ArrayList<>(1);
		builder.recipe.inputFluids = new ArrayList<>(0);
		builder.recipe.outputItems = new ArrayList<>(1);
		builder.recipe.outputFluids = new ArrayList<>(0);
		return builder;
	}

	public static MachineRecipeBuilder macerating() {
		return machine(FTBICRecipes.MACERATING);
	}

	public static MachineRecipeBuilder separating() {
		return machine(FTBICRecipes.SEPARATING);
	}

	public static MachineRecipeBuilder compressing() {
		return machine(FTBICRecipes.COMPRESSING);
	}

	public static MachineRecipeBuilder canning() {
		return machine(FTBICRecipes.CANNING);
	}

	public static MachineRecipeBuilder rolling() {
		return machine(FTBICRecipes.ROLLING);
	}

	public static MachineRecipeBuilder extruding() {
		return machine(FTBICRecipes.EXTRUDING);
	}

	public MachineRecipeBuilder unlockedBy(String string, CriterionTriggerInstance arg) {
		advancement.addCriterion(string, arg);
		return this;
	}

	public MachineRecipeBuilder inputItem(Ingredient in, int count) {
		recipe.inputItems.add(new IngredientWithCount(in, count));
		return this;
	}

	public MachineRecipeBuilder inputItem(Ingredient in) {
		return inputItem(in, 1);
	}

	public MachineRecipeBuilder io(Ingredient in,  ItemStack out) {
		return io(in, 1, out, 1D);
	}

	public MachineRecipeBuilder io(Ingredient in, int count, ItemStack out) {
		recipe.inputItems.add(new IngredientWithCount(in, count));
		recipe.outputItems.add(new StackWithChance(out, 1D));
		return this;
	}
	public MachineRecipeBuilder io(Ingredient in, int count, ItemStack out, double chance) {
		recipe.inputItems.add(new IngredientWithCount(in, count));
		recipe.outputItems.add(new StackWithChance(out, chance));
		return this;
	}

	public MachineRecipeBuilder inputFluid(FluidStack stack) {
		recipe.inputFluids.add(stack);
		return this;
	}

	public MachineRecipeBuilder outputItem(ItemStack stack, double chance) {
		recipe.outputItems.add(new StackWithChance(stack, chance));
		return this;
	}

	public MachineRecipeBuilder outputItem(ItemStack stack) {
		return outputItem(stack, 1D);
	}

	public MachineRecipeBuilder outputFluid(FluidStack stack) {
		recipe.outputFluids.add(stack);
		return this;
	}

	public MachineRecipeBuilder processingTime(double t) {
		recipe.processingTime = t;
		return this;
	}

	public MachineRecipeBuilder hideFromJEI() {
		recipe.hideFromJEI = true;
		return this;
	}

	public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
		advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(RequirementsStrategy.OR);
		ItemStack result = recipe.getResultItem();

		if (result.isEmpty()) {
			consumer.accept(new Result(this, id, new ResourceLocation(id.getNamespace(), "recipes/ftbic_machine/" + id.getPath())));
		} else {
			consumer.accept(new Result(this, id, new ResourceLocation(id.getNamespace(), "recipes/" + result.getItem().getItemCategory().getRecipeFolderName() + "/" + id.getPath())));
		}
	}

	public static class Result implements FinishedRecipe {
		private final MachineRecipeBuilder builder;
		private final ResourceLocation id;
		private final ResourceLocation advancementId;

		private Result(MachineRecipeBuilder b, ResourceLocation i, ResourceLocation a) {
			builder = b;
			id = i;
			advancementId = a;
		}

		@Override
		public void serializeRecipeData(JsonObject json) {
			FTBICUtils.listToJson(builder.recipe.inputItems, json, "inputItems", IngredientWithCount::toJson);
			FTBICUtils.listToJson(builder.recipe.inputFluids, json, "inputFluids", FTBICUtils::fluidToJson);
			FTBICUtils.listToJson(builder.recipe.outputItems, json, "outputItems", StackWithChance::toJson);
			FTBICUtils.listToJson(builder.recipe.outputFluids, json, "outputFluids", FTBICUtils::fluidToJson);

			if (builder.recipe.processingTime != 1D) {
				json.addProperty("processingTime", builder.recipe.processingTime);
			}
		}

		@Override
		public ResourceLocation getId() {
			return id;
		}

		@Override
		public RecipeSerializer<?> getType() {
			return builder.recipe.serializer;
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
