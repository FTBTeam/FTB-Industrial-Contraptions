package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.item.FTBICItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Consumer;

public class FTBICComponentRecipes extends FTBICRecipes {
	public FTBICComponentRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		SimpleCookingRecipeBuilder.cooking(Ingredient.of(RESIN), RUBBER, 0F, 150, RecipeSerializer.SMELTING_RECIPE)
				.unlockedBy("has_item", has(RESIN))
				.save(consumer, smeltingLoc("rubber"));

		SimpleCookingRecipeBuilder.cooking(Ingredient.of(RESIN), RUBBER, 0F, 300, RecipeSerializer.CAMPFIRE_COOKING_RECIPE)
				.unlockedBy("has_item", has(RESIN))
				.save(consumer, campfireCookingLoc("rubber"));

		ShapedRecipeBuilder.shaped(FTBICItems.RUBBER_SHEET.get())
				.unlockedBy("has_item", has(FTBICItems.RUBBER_SHEET.get()))
				.group(MODID + ":rubber")
				.pattern("III")
				.define('I', RUBBER)
				.save(consumer, shapedLoc("rubber_sheet"));

		ShapedRecipeBuilder.shaped(FTBICItems.SCRAP_BOX.get())
				.unlockedBy("has_item", has(FTBICItems.SCRAP.get()))
				.group(MODID + ":scrap")
				.pattern("SSS")
				.pattern("SSS")
				.pattern("SSS")
				.define('S', FTBICItems.SCRAP.ingredient())
				.save(consumer, shapedLoc("scrap_box"));

		ShapedRecipeBuilder.shaped(FTBICItems.COAL_BALL.get())
				.unlockedBy("has_item", has(Items.COAL))
				.group(MODID + ":graphene")
				.pattern("CCC")
				.pattern("CFC")
				.pattern("CCC")
				.define('C', Ingredient.of(COAL))
				.define('F', Items.FLINT)
				.save(consumer, shapedLoc("coal_ball"));

		ShapedRecipeBuilder.shaped(FTBICItems.GRAPHENE.get())
				.unlockedBy("has_item", has(FTBICItems.COMPRESSED_COAL_BALL.get()))
				.group(MODID + ":graphene")
				.pattern("CCC")
				.pattern("COC")
				.pattern("CCC")
				.define('C', FTBICItems.COMPRESSED_COAL_BALL.ingredient())
				.define('O', Items.OBSIDIAN)
				.save(consumer, shapedLoc("graphene"));

		ShapedRecipeBuilder.shaped(FTBICItems.ENERGY_CRYSTAL.get())
				.unlockedBy("has_item", has(Items.DIAMOND))
				.group(MODID + ":energy_crystal")
				.pattern("RQR")
				.pattern("QDQ")
				.pattern("RQR")
				.define('R', REDSTONE)
				.define('D', DIAMOND)
				.define('Q', QUARTZ)
				.save(consumer, shapedLoc("energy_crystal"));

		ShapelessRecipeBuilder.shapeless(RUBBER, 3)
				.unlockedBy("has_item", has(RUBBER))
				.group(MODID + ":rubber")
				.requires(FTBICItems.RUBBER_SHEET.get())
				.save(consumer, shapelessLoc("rubber"));

		ShapelessRecipeBuilder.shapeless(FTBICItems.SCRAP.get(), 9)
				.unlockedBy("has_item", has(FTBICItems.SCRAP_BOX.get()))
				.group(MODID + ":scrap")
				.requires(FTBICItems.SCRAP_BOX.ingredient())
				.save(consumer, shapelessLoc("scrap"));
	}
}
