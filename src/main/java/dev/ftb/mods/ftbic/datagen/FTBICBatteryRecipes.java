package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.item.FTBICItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.UpgradeRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Consumer;

public class FTBICBatteryRecipes extends FTBICRecipes {
	public FTBICBatteryRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(FTBICItems.SINGLE_USE_BATTERY.get())
				.unlockedBy("has_item", has(COAL))
				.group(MODID + ":battery")
				.pattern("C")
				.pattern("R")
				.pattern("O")
				.define('C', FTBICItems.COPPER_CABLE.get())
				.define('O', COAL)
				.define('R', REDSTONE)
				.save(consumer, shapedLoc("single_use_battery"));

		ShapedRecipeBuilder.shaped(FTBICItems.BATTERY.get())
				.unlockedBy("has_item", has(TIN_INGOT))
				.group(MODID + ":battery")
				.pattern(" C ")
				.pattern("TRT")
				.pattern("TRT")
				.define('C', FTBICItems.COPPER_CABLE.get())
				.define('T', TIN_INGOT)
				.define('R', REDSTONE)
				.save(consumer, shapedLoc("battery"));

		UpgradeRecipeBuilder.smithing(Ingredient.of(FTBICItems.BATTERY.get()), FTBICItems.ENERGY_CRYSTAL.ingredient(), FTBICItems.CRYSTAL_BATTERY.get())
				.unlocks("has_item", has(FTBICItems.ENERGY_CRYSTAL.get()))
				.save(consumer, smithingLoc("crystal_battery"));

		UpgradeRecipeBuilder.smithing(Ingredient.of(FTBICItems.CRYSTAL_BATTERY.get()), FTBICItems.GRAPHENE.ingredient(), FTBICItems.GRAPHENE_BATTERY.get())
				.unlocks("has_item", has(FTBICItems.GRAPHENE.get()))
				.save(consumer, smithingLoc("graphene_battery"));

		UpgradeRecipeBuilder.smithing(Ingredient.of(FTBICItems.GRAPHENE_BATTERY.get()), FTBICItems.IRIDIUM_PLATE.ingredient(), FTBICItems.IRIDIUM_BATTERY.get())
				.unlocks("has_item", has(FTBICItems.IRIDIUM_PLATE.get()))
				.save(consumer, smithingLoc("iridium_battery"));
	}
}
