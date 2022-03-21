package dev.ftb.mods.ftbic.datagen;

import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.UpgradeRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Consumer;
import java.util.function.Function;

public class FTBICBatteryRecipes extends FTBICRecipesGen {
	public FTBICBatteryRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		Function<TagKey<Item>, InventoryChangeTrigger.TriggerInstance> tagKeyHas = (e) -> RecipeProvider.inventoryTrigger(ItemPredicate.Builder.item().of(e).build());

		ShapedRecipeBuilder.shaped(SINGLE_USE_BATTERY)
				.unlockedBy("has_item", tagKeyHas.apply(COAL))
				.group(MODID + ":single_use_battery")
				.pattern("C")
				.pattern("R")
				.pattern("O")
				.define('C', LV_CABLE)
				.define('O', COAL)
				.define('R', REDSTONE)
				.save(consumer, shapedLoc("single_use_battery"));

		ShapedRecipeBuilder.shaped(LV_BATTERY)
				.unlockedBy("has_item", tagKeyHas.apply(TIN_INGOT))
				.group(MODID + ":battery")
				.pattern(" C ")
				.pattern("TRT")
				.pattern("TRT")
				.define('C', LV_CABLE)
				.define('T', TIN_INGOT)
				.define('R', REDSTONE)
				.save(consumer, shapedLoc("battery"));

		UpgradeRecipeBuilder.smithing(Ingredient.of(LV_BATTERY), Ingredient.of(ENERGY_CRYSTAL), MV_BATTERY)
				.unlocks("has_item", has(ENERGY_CRYSTAL))
				.save(consumer, smithingLoc("crystal_battery"));

		UpgradeRecipeBuilder.smithing(Ingredient.of(MV_BATTERY), Ingredient.of(GRAPHENE), HV_BATTERY)
				.unlocks("has_item", has(GRAPHENE))
				.save(consumer, smithingLoc("graphene_battery"));

		UpgradeRecipeBuilder.smithing(Ingredient.of(HV_BATTERY), Ingredient.of(IRIDIUM_ALLOY), EV_BATTERY)
				.unlocks("has_item", has(IRIDIUM_ALLOY))
				.save(consumer, smithingLoc("iridium_battery"));
	}
}
