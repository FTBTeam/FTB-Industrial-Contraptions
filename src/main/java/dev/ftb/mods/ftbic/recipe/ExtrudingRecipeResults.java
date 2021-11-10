package dev.ftb.mods.ftbic.recipe;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import dev.ftb.mods.ftbic.util.IngredientWithCount;
import dev.ftb.mods.ftbic.util.StackWithChance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.SerializationTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import java.util.List;

public class ExtrudingRecipeResults extends SimpleMachineRecipeResults {
	public ExtrudingRecipeResults() {
		super(FTBICRecipes.EXTRUDING);
	}

	@Override
	protected void addAdditionalRecipes(Level level, List<MachineRecipe> list) {
		for (String s : FTBICConfig.AUTO_METALS) {
			Tag<Item> ingotTag = SerializationTags.getInstance().getItems().getTag(new ResourceLocation("forge:ingots/" + s));
			Tag<Item> rodTag = SerializationTags.getInstance().getItems().getTag(new ResourceLocation("forge:rods/" + s));

			if (ingotTag != null && rodTag != null && FTBICConfig.ADD_ROD_RECIPES) {
				Item item = FTBICConfig.getItemFromTag(rodTag);
				ResourceLocation id = item == Items.AIR ? null : item.getRegistryName();

				if (id != null && !FTBICUtils.NO_AUTO_RECIPE.contains(item)) {
					MachineRecipe recipe = new MachineRecipe(recipeSerializer.get(), new ResourceLocation(FTBIC.MOD_ID, "extracting/generated/rod_from_metal/" + id.getNamespace() + "/" + id.getPath()));
					recipe.inputItems.add(new IngredientWithCount(Ingredient.of(ingotTag), 1));
					recipe.outputItems.add(new StackWithChance(new ItemStack(item, 2), 1D));
					list.add(recipe);
				}
			}
		}

		for (String s : FTBICConfig.AUTO_GEMS) {
			Tag<Item> gemTag = SerializationTags.getInstance().getItems().getTag(new ResourceLocation("forge:gems/" + s));
			Tag<Item> rodTag = SerializationTags.getInstance().getItems().getTag(new ResourceLocation("forge:rods/" + s));

			if (gemTag != null && rodTag != null && FTBICConfig.ADD_ROD_RECIPES) {
				Item item = FTBICConfig.getItemFromTag(rodTag);
				ResourceLocation id = item == Items.AIR ? null : item.getRegistryName();

				if (id != null && !FTBICUtils.NO_AUTO_RECIPE.contains(item)) {
					MachineRecipe recipe = new MachineRecipe(recipeSerializer.get(), new ResourceLocation(FTBIC.MOD_ID, "extracting/generated/rod_from_gem/" + id.getNamespace() + "/" + id.getPath()));
					recipe.inputItems.add(new IngredientWithCount(Ingredient.of(gemTag), 1));
					recipe.outputItems.add(new StackWithChance(new ItemStack(item, 2), 1D));
					list.add(recipe);
				}
			}
		}
	}
}
