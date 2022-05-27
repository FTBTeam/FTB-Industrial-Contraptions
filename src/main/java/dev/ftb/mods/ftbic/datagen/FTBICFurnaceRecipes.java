package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.world.ResourceElements;
import dev.ftb.mods.ftbic.world.ResourceType;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class FTBICFurnaceRecipes extends FTBICRecipesGen {
	public FTBICFurnaceRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		blastAndSmelt(List.of(TIN_ORE, TIN_CHUNK, TIN_DUST), ResourceElements.TIN, ResourceType.INGOT, consumer);
		blastAndSmelt(List.of(LEAD_ORE, LEAD_CHUNK, LEAD_DUST), ResourceElements.LEAD, ResourceType.INGOT, consumer);
		blastAndSmelt(List.of(IRIDIUM_ORE, IRIDIUM_CHUNK, IRIDIUM_DUST), ResourceElements.IRIDIUM, ResourceType.INGOT, consumer);
		blastAndSmelt(List.of(URANIUM_ORE, URANIUM_CHUNK, URANIUM_DUST), ResourceElements.URANIUM, ResourceType.INGOT, consumer);
		blastAndSmelt(List.of(ALUMINUM_ORE, ALUMINUM_CHUNK, ALUMINUM_DUST), ResourceElements.ALUMINUM, ResourceType.INGOT, consumer);

		blastAndSmelt(List.of(ENDERIUM_DUST), ResourceElements.ENDERIUM, ResourceType.INGOT, consumer);
		blastAndSmelt(List.of(BRONZE_DUST), ResourceElements.BRONZE, ResourceType.INGOT, consumer);

		blastAndSmelt(List.of(IRON_DUST), Items.IRON_INGOT, consumer);
		blastAndSmelt(List.of(COPPER_DUST), Items.COPPER_INGOT, consumer);
		blastAndSmelt(List.of(GOLD_DUST), Items.GOLD_INGOT, consumer);
	}

	private static void blastAndSmelt(List<TagKey<Item>> input, ResourceElements element, ResourceType type, Consumer<FinishedRecipe> consumer) {
		for (TagKey<Item> item : input) {
			SimpleCookingRecipeBuilder.cooking(Ingredient.of(item), FTBICItems.getResourceFromType(element, type).orElseThrow().get(), 0.7F, 200, RecipeSerializer.SMELTING_RECIPE)
					.unlockedBy("has_item", has(item))
					.save(consumer, smeltingLoc(item.location().getPath() + "_to_" + type.name().toLowerCase(Locale.ENGLISH)));
			SimpleCookingRecipeBuilder.cooking(Ingredient.of(item), FTBICItems.getResourceFromType(element, type).orElseThrow().get(), 0.7F, 200, RecipeSerializer.BLASTING_RECIPE)
					.unlockedBy("has_item", has(item))
					.save(consumer, blastingLoc(item.location().getPath() + "_to_" + type.name().toLowerCase(Locale.ENGLISH)));
		}
	}

	private static void blastAndSmelt(List<TagKey<Item>> input, ItemLike output, Consumer<FinishedRecipe> consumer) {
		for (TagKey<Item> item : input) {
			SimpleCookingRecipeBuilder.cooking(Ingredient.of(item), output, 0.7F, 200, RecipeSerializer.SMELTING_RECIPE)
					.unlockedBy("has_item", has(item))
					.save(consumer, smeltingLoc(item.location().getPath() + "_to_" + output));
			SimpleCookingRecipeBuilder.cooking(Ingredient.of(item), output, 0.7F, 200, RecipeSerializer.BLASTING_RECIPE)
					.unlockedBy("has_item", has(item))
					.save(consumer, blastingLoc(item.location().getPath() + "_to_" + output));
		}
	}
}
