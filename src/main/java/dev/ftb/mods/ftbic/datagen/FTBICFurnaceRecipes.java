package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.world.ResourceElementTypes;
import dev.ftb.mods.ftbic.world.ResourceElements;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.List;
import java.util.function.Consumer;

public class FTBICFurnaceRecipes extends FTBICRecipesGen {
	public FTBICFurnaceRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		blastAndSmelt(List.of(TIN_ORE, TIN_CHUNK, TIN_DUST), ResourceElements.TIN, ResourceElementTypes.INGOT, consumer);
		blastAndSmelt(List.of(LEAD_ORE, LEAD_CHUNK, LEAD_DUST), ResourceElements.LEAD, ResourceElementTypes.INGOT, consumer);
		blastAndSmelt(List.of(IRIDIUM_ORE, IRIDIUM_CHUNK, IRIDIUM_DUST), ResourceElements.IRIDIUM, ResourceElementTypes.INGOT, consumer);
		blastAndSmelt(List.of(URANIUM_ORE, URANIUM_CHUNK, URANIUM_DUST), ResourceElements.URANIUM, ResourceElementTypes.INGOT, consumer);
		blastAndSmelt(List.of(ALUMINUM_ORE, ALUMINUM_CHUNK, ALUMINUM_DUST), ResourceElements.ALUMINUM, ResourceElementTypes.INGOT, consumer);

		blastAndSmelt(List.of(ENDERIUM_DUST), ResourceElements.ENDERIUM, ResourceElementTypes.INGOT, consumer);
	}

	private static void blastAndSmelt(List<TagKey<Item>> input, ResourceElements element, ResourceElementTypes type, Consumer<FinishedRecipe> consumer) {
		for (TagKey<Item> item : input) {
			SimpleCookingRecipeBuilder.cooking(Ingredient.of(item), FTBICItems.getResourceFromType(element, type).orElseThrow().get(), 0.7F, 200, RecipeSerializer.SMELTING_RECIPE)
					.unlockedBy("has_item", has(item))
					.save(consumer, smeltingLoc(item.location().getPath() + "_to_" + type.name().toLowerCase()));
			SimpleCookingRecipeBuilder.cooking(Ingredient.of(item), FTBICItems.getResourceFromType(element, type).orElseThrow().get(), 0.7F, 200, RecipeSerializer.BLASTING_RECIPE)
					.unlockedBy("has_item", has(item))
					.save(consumer, blastingLoc(item.location().getPath() + "_to_" + type.name().toLowerCase()));
		}
	}
}
