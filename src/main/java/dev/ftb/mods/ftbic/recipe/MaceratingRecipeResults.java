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

public class MaceratingRecipeResults extends SimpleMachineRecipeResults {
	public MaceratingRecipeResults() {
		super(FTBICRecipes.MACERATING);
	}

	@Override
	protected void addAdditionalRecipes(Level level, List<MachineRecipe> list) {
		for (String s : FTBICConfig.AUTO_METALS) {
			Tag<Item> oreTag = SerializationTags.getInstance().getItems().getTag(new ResourceLocation("forge:ores/" + s));
			Tag<Item> ingotTag = SerializationTags.getInstance().getItems().getTag(new ResourceLocation("forge:ingots/" + s));
			Tag<Item> dustTag = SerializationTags.getInstance().getItems().getTag(new ResourceLocation("forge:dusts/" + s));

			if (oreTag != null && dustTag != null && FTBICConfig.ADD_DUST_FROM_ORE_RECIPES) {
				Item item = FTBICConfig.getItemFromTag(dustTag);
				ResourceLocation id = item == Items.AIR ? null : item.getRegistryName();

				if (id != null) {
					MachineRecipe recipe = new MachineRecipe(recipeSerializer.get(), new ResourceLocation(FTBIC.MOD_ID, "macerating/generated/dust_from_metal_ore/" + id.getNamespace() + "/" + id.getPath()));
					recipe.inputItems.add(new IngredientWithCount(Ingredient.of(oreTag), 1));
					recipe.outputItems.add(new StackWithChance(new ItemStack(item, 2), 1D));
					list.add(recipe);
				}
			}

			if (ingotTag != null && dustTag != null && FTBICConfig.ADD_DUST_FROM_MATERIAL_RECIPES) {
				Item item = FTBICConfig.getItemFromTag(dustTag);
				ResourceLocation id = item == Items.AIR ? null : item.getRegistryName();

				if (id != null) {
					MachineRecipe recipe = new MachineRecipe(recipeSerializer.get(), new ResourceLocation(FTBIC.MOD_ID, "macerating/generated/dust_from_metal/" + id.getNamespace() + "/" + id.getPath()));
					recipe.inputItems.add(new IngredientWithCount(Ingredient.of(ingotTag), 1));
					recipe.outputItems.add(new StackWithChance(new ItemStack(item, 1), 1D));
					list.add(recipe);
				}
			}
		}

		for (String s : FTBICConfig.AUTO_GEMS) {
			Tag<Item> oreTag = SerializationTags.getInstance().getItems().getTag(new ResourceLocation("forge:ores/" + s));
			Tag<Item> gemTag = SerializationTags.getInstance().getItems().getTag(new ResourceLocation("forge:gems/" + s));
			Tag<Item> dustTag = SerializationTags.getInstance().getItems().getTag(new ResourceLocation("forge:dusts/" + s));

			if (oreTag != null && gemTag != null && FTBICConfig.ADD_DUST_FROM_ORE_RECIPES) {
				Item item = FTBICConfig.getItemFromTag(gemTag);
				ResourceLocation id = item == Items.AIR ? null : item.getRegistryName();

				if (id != null) {
					MachineRecipe recipe = new MachineRecipe(recipeSerializer.get(), new ResourceLocation(FTBIC.MOD_ID, "macerating/generated/gem_from_ore/" + id.getNamespace() + "/" + id.getPath()));
					recipe.inputItems.add(new IngredientWithCount(Ingredient.of(oreTag), 1));
					recipe.outputItems.add(new StackWithChance(new ItemStack(item, 2), 1D));
					list.add(recipe);
				}
			} else if (oreTag != null && dustTag != null && FTBICConfig.ADD_GEM_FROM_ORE_RECIPES) {
				Item item = FTBICConfig.getItemFromTag(dustTag);
				ResourceLocation id = item == Items.AIR ? null : item.getRegistryName();

				if (id != null) {
					MachineRecipe recipe = new MachineRecipe(recipeSerializer.get(), new ResourceLocation(FTBIC.MOD_ID, "macerating/generated/dust_from_gem_ore/" + id.getNamespace() + "/" + id.getPath()));
					recipe.inputItems.add(new IngredientWithCount(Ingredient.of(oreTag), 1));
					recipe.outputItems.add(new StackWithChance(new ItemStack(item, 2), 1D));
					list.add(recipe);
				}
			}

			if (gemTag != null && dustTag != null && FTBICConfig.ADD_DUST_FROM_MATERIAL_RECIPES) {
				Item item = FTBICConfig.getItemFromTag(dustTag);
				ResourceLocation id = item == Items.AIR ? null : item.getRegistryName();

				if (id != null) {
					MachineRecipe recipe = new MachineRecipe(recipeSerializer.get(), new ResourceLocation(FTBIC.MOD_ID, "macerating/generated/dust_from_gem/" + id.getNamespace() + "/" + id.getPath()));
					recipe.inputItems.add(new IngredientWithCount(Ingredient.of(gemTag), 1));
					recipe.outputItems.add(new StackWithChance(new ItemStack(item, 1), 1D));
					list.add(recipe);
				}
			}
		}
	}
}
