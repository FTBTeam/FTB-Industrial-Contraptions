package dev.ftb.mods.ftbic.recipe;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.util.CraftingMaterial;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import dev.ftb.mods.ftbic.util.IngredientWithCount;
import dev.ftb.mods.ftbic.util.StackWithChance;
import net.minecraft.resources.ResourceLocation;
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
		for (CraftingMaterial m : FTBICConfig.MATERIALS.values()) {
			Item rod = FTBICConfig.getItemFromTag(m.rod);

			if (FTBICConfig.ADD_ROD_RECIPES && rod != Items.AIR) {
				ResourceLocation id = rod.getRegistryName();

				if (id != null && !FTBICUtils.NO_AUTO_RECIPE.contains(rod)) {
					if (!m.ingot.getValues().isEmpty()) {
						MachineRecipe recipe = new MachineRecipe(recipeSerializer.get(), new ResourceLocation(FTBIC.MOD_ID, "extracting/generated/rod_from_metal/" + id.getNamespace() + "/" + id.getPath()));
						recipe.inputItems.add(new IngredientWithCount(Ingredient.of(m.ingot), 1));
						recipe.outputItems.add(new StackWithChance(new ItemStack(rod, 2), 1D));
						list.add(recipe);
					}

					if (!m.gem.getValues().isEmpty()) {
						MachineRecipe recipe = new MachineRecipe(recipeSerializer.get(), new ResourceLocation(FTBIC.MOD_ID, "extracting/generated/rod_from_gem/" + id.getNamespace() + "/" + id.getPath()));
						recipe.inputItems.add(new IngredientWithCount(Ingredient.of(m.gem), 1));
						recipe.outputItems.add(new StackWithChance(new ItemStack(rod, 2), 1D));
						list.add(recipe);
					}
				}
			}
		}
	}
}
