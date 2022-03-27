package dev.ftb.mods.ftbic.recipe.machines;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import dev.ftb.mods.ftbic.recipe.MachineRecipe;
import dev.ftb.mods.ftbic.recipe.SimpleMachineRecipeResults;
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

public class MaceratingRecipeResults extends SimpleMachineRecipeResults {
	public MaceratingRecipeResults() {
		super(FTBICRecipes.MACERATING);
	}

	@Override
	protected void addAdditionalRecipes(Level level, List<MachineRecipe> list) {
		for (CraftingMaterial material : FTBICConfig.MATERIALS.values()) {
			Item dust = FTBICConfig.getItemFromTag(material.dust.getTag());

			boolean gemFromOre = false;

			if (FTBICConfig.RECIPES.ADD_GEM_FROM_ORE_RECIPES.get() && !material.gem.getValues().isEmpty() && !material.ore.getValues().isEmpty()) {
				Item gem = FTBICConfig.getItemFromTag(material.gem.getTag());
				ResourceLocation id = gem == Items.AIR ? null : gem.getRegistryName();

				if (id != null && !gem.builtInRegistryHolder().is(FTBICUtils.NO_AUTO_RECIPE)) {
					MachineRecipe recipe = new MachineRecipe(recipeSerializer.get(), new ResourceLocation(FTBIC.MOD_ID, "macerating/generated/gem_from_ore/" + id.getNamespace() + "/" + id.getPath()));
					recipe.inputItems.add(new IngredientWithCount(Ingredient.of(material.ore.getTag()), 1));
					recipe.outputItems.add(new StackWithChance(new ItemStack(gem, 2), 1D));
					list.add(recipe);
					gemFromOre = true;
				}
			}

			if (!gemFromOre && FTBICConfig.RECIPES.ADD_DUST_FROM_ORE_RECIPES.get() && dust != Items.AIR && !material.ore.getValues().isEmpty()) {
				ResourceLocation id = dust.getRegistryName();

				if (id != null && !dust.builtInRegistryHolder().is(FTBICUtils.NO_AUTO_RECIPE)) {
					MachineRecipe recipe = new MachineRecipe(recipeSerializer.get(), new ResourceLocation(FTBIC.MOD_ID, "macerating/generated/dust_from_ore/" + id.getNamespace() + "/" + id.getPath()));
					recipe.inputItems.add(new IngredientWithCount(Ingredient.of(material.ore.getTag()), 1));
					recipe.outputItems.add(new StackWithChance(new ItemStack(dust, 2), 1D));
					list.add(recipe);
				}
			}

			if (FTBICConfig.RECIPES.ADD_DUST_FROM_MATERIAL_RECIPES.get() && dust != Items.AIR) {
				ResourceLocation id = dust.getRegistryName();

				if (id != null && !dust.builtInRegistryHolder().is(FTBICUtils.NO_AUTO_RECIPE)) {
					if (!material.ingot.getValues().isEmpty()) {
						MachineRecipe recipe = new MachineRecipe(recipeSerializer.get(), new ResourceLocation(FTBIC.MOD_ID, "macerating/generated/dust_from_metal/" + id.getNamespace() + "/" + id.getPath()));
						recipe.inputItems.add(new IngredientWithCount(Ingredient.of(material.ingot.getTag()), 1));
						recipe.outputItems.add(new StackWithChance(new ItemStack(dust, 1), 1D));
						list.add(recipe);
					}

					if (!material.gem.getValues().isEmpty()) {
						MachineRecipe recipe = new MachineRecipe(recipeSerializer.get(), new ResourceLocation(FTBIC.MOD_ID, "macerating/generated/dust_from_gem/" + id.getNamespace() + "/" + id.getPath()));
						recipe.inputItems.add(new IngredientWithCount(Ingredient.of(material.gem.getTag()), 1));
						recipe.outputItems.add(new StackWithChance(new ItemStack(dust, 1), 1D));
						list.add(recipe);
					}
				}
			}
		}
	}
}
