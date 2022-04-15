package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.item.FTBICItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

import static dev.ftb.mods.ftbic.world.ResourceElementTypes.*;
import static dev.ftb.mods.ftbic.world.ResourceElements.*;

public class FTBICCraftingRecipes extends FTBICRecipesGen {
	public FTBICCraftingRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		ingotToNuggetAndBack(TIN_INGOT, FTBICItems.getResourceFromType(TIN, INGOT).orElseThrow().get(), TIN_NUGGET, FTBICItems.getResourceFromType(TIN, NUGGET).orElseThrow().get(), consumer);
		ingotToNuggetAndBack(LEAD_INGOT, FTBICItems.getResourceFromType(LEAD, INGOT).orElseThrow().get(), LEAD_NUGGET, FTBICItems.getResourceFromType(LEAD, NUGGET).orElseThrow().get(), consumer);
		ingotToNuggetAndBack(URANIUM_INGOT, FTBICItems.getResourceFromType(URANIUM, INGOT).orElseThrow().get(), URANIUM_NUGGET, FTBICItems.getResourceFromType(URANIUM, NUGGET).orElseThrow().get(), consumer);
		ingotToNuggetAndBack(IRIDIUM_INGOT, FTBICItems.getResourceFromType(IRIDIUM, INGOT).orElseThrow().get(), IRIDIUM_NUGGET, FTBICItems.getResourceFromType(IRIDIUM, NUGGET).orElseThrow().get(), consumer);
		ingotToNuggetAndBack(ALUMINUM_INGOT, FTBICItems.getResourceFromType(ALUMINUM, INGOT).orElseThrow().get(), ALUMINUM_NUGGET, FTBICItems.getResourceFromType(ALUMINUM, NUGGET).orElseThrow().get(), consumer);
		ingotToNuggetAndBack(ENDERIUM_INGOT, FTBICItems.getResourceFromType(ENDERIUM, INGOT).orElseThrow().get(), ENDERIUM_NUGGET, FTBICItems.getResourceFromType(ENDERIUM, NUGGET).orElseThrow().get(), consumer);
	}

	private static void ingotToNuggetAndBack(TagKey<Item> ingotTag, Item ingotItem, TagKey<Item> nuggetTag, Item nuggetItem, Consumer<FinishedRecipe> consumer) {
		ShapelessRecipeBuilder.shapeless(nuggetItem, 9)
			.unlockedBy("has_item", has(ingotTag))
			.group(MODID + ":" + nuggetItem)
			.requires(ingotTag)
			.save(consumer, shapelessLoc(ingotItem + "_to_" + nuggetItem));

		ShapedRecipeBuilder.shaped(ingotItem)
			.unlockedBy("has_item", has(nuggetTag))
			.group(MODID + ":" + ingotItem)
			.pattern("XXX")
			.pattern("XXX")
			.pattern("XXX")
			.define('X', nuggetTag)
			.save(consumer, shapedLoc(nuggetItem + "_to_" + ingotItem));
	}
}
