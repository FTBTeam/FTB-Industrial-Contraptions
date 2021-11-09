package dev.ftb.mods.ftbic.recipe;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.FTBICConfig;
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

public class RollingRecipeResults extends SimpleMachineRecipeResults {
	public RollingRecipeResults() {
		super(FTBICRecipes.ROLLING);
	}

	@Override
	protected void addAdditionalRecipes(Level level, List<MachineRecipe> list) {
		for (String s : FTBICConfig.AUTO_METALS) {
			Tag<Item> ingotTag = SerializationTags.getInstance().getItems().getTag(new ResourceLocation("forge:ingots/" + s));
			Tag<Item> plateTag = SerializationTags.getInstance().getItems().getTag(new ResourceLocation("forge:plates/" + s));

			if (ingotTag != null && plateTag != null && FTBICConfig.ADD_PLATE_RECIPES) {
				Item item = FTBICConfig.getItemFromTag(plateTag);
				ResourceLocation id = item == Items.AIR ? null : item.getRegistryName();

				if (id != null) {
					MachineRecipe recipe = new MachineRecipe(recipeSerializer.get(), new ResourceLocation(FTBIC.MOD_ID, "rolling/generated/plate_from_ingot/" + id.getNamespace() + "/" + id.getPath()));
					recipe.inputItems.add(new IngredientWithCount(Ingredient.of(ingotTag), 1));
					recipe.outputItems.add(new StackWithChance(new ItemStack(item, 2), 1D));
					list.add(recipe);
				}
			}
		}

		for (String s : FTBICConfig.AUTO_GEMS) {
			Tag<Item> gemTag = SerializationTags.getInstance().getItems().getTag(new ResourceLocation("forge:gems/" + s));
			Tag<Item> plateTag = SerializationTags.getInstance().getItems().getTag(new ResourceLocation("forge:plates/" + s));

			if (gemTag != null && plateTag != null && FTBICConfig.ADD_PLATE_RECIPES) {
				Item item = FTBICConfig.getItemFromTag(plateTag);
				ResourceLocation id = item == Items.AIR ? null : item.getRegistryName();

				if (id != null) {
					MachineRecipe recipe = new MachineRecipe(recipeSerializer.get(), new ResourceLocation(FTBIC.MOD_ID, "rolling/generated/plate_from_gem/" + id.getNamespace() + "/" + id.getPath()));
					recipe.inputItems.add(new IngredientWithCount(Ingredient.of(gemTag), 1));
					recipe.outputItems.add(new StackWithChance(new ItemStack(item, 2), 1D));
					list.add(recipe);
				}
			}
		}

		for (String s : FTBICConfig.AUTO_METALS) {
			Tag<Item> plateTag = SerializationTags.getInstance().getItems().getTag(new ResourceLocation("forge:plates/" + s));
			Tag<Item> gearTag = SerializationTags.getInstance().getItems().getTag(new ResourceLocation("forge:gears/" + s));

			if (gearTag != null && plateTag != null && FTBICConfig.ADD_GEAR_RECIPES) {
				Item item = FTBICConfig.getItemFromTag(gearTag);
				ResourceLocation id = item == Items.AIR ? null : item.getRegistryName();

				if (id != null) {
					MachineRecipe recipe = new MachineRecipe(recipeSerializer.get(), new ResourceLocation(FTBIC.MOD_ID, "rolling/generated/gear_from_ingots/" + id.getNamespace() + "/" + id.getPath()));
					recipe.inputItems.add(new IngredientWithCount(Ingredient.of(plateTag), 4));
					recipe.outputItems.add(new StackWithChance(new ItemStack(item), 1D));
					list.add(recipe);
				}
			}
		}

		for (String s : FTBICConfig.AUTO_GEMS) {
			Tag<Item> gemTag = SerializationTags.getInstance().getItems().getTag(new ResourceLocation("forge:gems/" + s));
			Tag<Item> gearTag = SerializationTags.getInstance().getItems().getTag(new ResourceLocation("forge:gears/" + s));
			Tag<Item> plateTag = SerializationTags.getInstance().getItems().getTag(new ResourceLocation("forge:plates/" + s));

			if (gearTag != null && plateTag != null && FTBICConfig.ADD_GEAR_RECIPES) {
				Item item = FTBICConfig.getItemFromTag(gearTag);
				ResourceLocation id = item == Items.AIR ? null : item.getRegistryName();

				if (id != null) {
					MachineRecipe recipe = new MachineRecipe(recipeSerializer.get(), new ResourceLocation(FTBIC.MOD_ID, "rolling/generated/gear_from_gems/" + id.getNamespace() + "/" + id.getPath()));
					recipe.inputItems.add(new IngredientWithCount(Ingredient.of(plateTag), 4));
					recipe.outputItems.add(new StackWithChance(new ItemStack(item), 1D));
					list.add(recipe);
				}
			} else if (gearTag != null && gemTag != null && FTBICConfig.ADD_GEAR_RECIPES) {
				Item item = FTBICConfig.getItemFromTag(gearTag);
				ResourceLocation id = item == Items.AIR ? null : item.getRegistryName();

				if (id != null) {
					MachineRecipe recipe = new MachineRecipe(recipeSerializer.get(), new ResourceLocation(FTBIC.MOD_ID, "rolling/generated/gear_from_gems/" + id.getNamespace() + "/" + id.getPath()));
					recipe.inputItems.add(new IngredientWithCount(Ingredient.of(gemTag), 2));
					recipe.outputItems.add(new StackWithChance(new ItemStack(item), 1D));
					list.add(recipe);
				}
			}
		}
	}
}
