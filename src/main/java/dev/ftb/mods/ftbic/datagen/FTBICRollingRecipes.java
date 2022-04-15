package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.item.FTBICItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Consumer;

import static dev.ftb.mods.ftbic.world.ResourceElementTypes.*;
import static dev.ftb.mods.ftbic.world.ResourceElements.*;

public class FTBICRollingRecipes extends FTBICRecipesGen{
	public FTBICRollingRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		MachineRecipeBuilder.rolling()
				.unlockedBy("has_item", has(TIN_INGOT))
				.io(Ingredient.of(TIN_INGOT), new ItemStack(FTBICItems.getResourceFromType(TIN, PLATE).orElseThrow().get(), 2))
				.save(consumer, rollingLoc(TIN_INGOT.location().getPath() + "_to_" + FTBICItems.getResourceFromType(TIN, PLATE).orElseThrow().get()));

		MachineRecipeBuilder.rolling()
				.unlockedBy("has_item", has(LEAD_INGOT))
				.io(Ingredient.of(LEAD_INGOT), new ItemStack(FTBICItems.getResourceFromType(LEAD, PLATE).orElseThrow().get(), 2))
				.save(consumer, rollingLoc(LEAD_INGOT.location().getPath() + "_to_" + FTBICItems.getResourceFromType(LEAD, PLATE).orElseThrow().get()));

		MachineRecipeBuilder.rolling()
				.unlockedBy("has_item", has(URANIUM_INGOT))
				.io(Ingredient.of(URANIUM_INGOT), new ItemStack(FTBICItems.getResourceFromType(URANIUM, PLATE).orElseThrow().get(), 2))
				.save(consumer, rollingLoc(URANIUM_INGOT.location().getPath() + "_to_" + FTBICItems.getResourceFromType(URANIUM, PLATE).orElseThrow().get()));

		MachineRecipeBuilder.rolling()
				.unlockedBy("has_item", has(IRIDIUM_INGOT))
				.io(Ingredient.of(IRIDIUM_INGOT), new ItemStack(FTBICItems.getResourceFromType(IRIDIUM, PLATE).orElseThrow().get(), 2))
				.save(consumer, rollingLoc(IRIDIUM_INGOT.location().getPath() + "_to_" + FTBICItems.getResourceFromType(IRIDIUM, PLATE).orElseThrow().get()));

		MachineRecipeBuilder.rolling()
				.unlockedBy("has_item", has(ALUMINUM_INGOT))
				.io(Ingredient.of(ALUMINUM_INGOT), new ItemStack(FTBICItems.getResourceFromType(ALUMINUM, PLATE).orElseThrow().get(), 2))
				.save(consumer, rollingLoc(ALUMINUM_INGOT.location().getPath() + "_to_" + FTBICItems.getResourceFromType(ALUMINUM, PLATE).orElseThrow().get()));

		MachineRecipeBuilder.rolling()
				.unlockedBy("has_item", has(ENDERIUM_INGOT))
				.io(Ingredient.of(ENDERIUM_INGOT), new ItemStack(FTBICItems.getResourceFromType(ENDERIUM, PLATE).orElseThrow().get(), 2))
				.save(consumer, rollingLoc(ENDERIUM_INGOT.location().getPath() + "_to_" + FTBICItems.getResourceFromType(ENDERIUM, PLATE).orElseThrow().get()));


		MachineRecipeBuilder.rolling()
				.unlockedBy("has_item", has(TIN_PLATE))
				.io(Ingredient.of(TIN_PLATE), 4, new ItemStack(FTBICItems.getResourceFromType(TIN, GEAR).orElseThrow().get(), 1))
				.save(consumer, rollingLoc(TIN_PLATE.location().getPath() + "_to_" + FTBICItems.getResourceFromType(TIN, GEAR).orElseThrow().get()));

		MachineRecipeBuilder.rolling()
				.unlockedBy("has_item", has(LEAD_PLATE))
				.io(Ingredient.of(LEAD_PLATE), 4, new ItemStack(FTBICItems.getResourceFromType(LEAD, GEAR).orElseThrow().get(), 1))
				.save(consumer, rollingLoc(LEAD_PLATE.location().getPath() + "_to_" + FTBICItems.getResourceFromType(LEAD, GEAR).orElseThrow().get()));

		MachineRecipeBuilder.rolling()
				.unlockedBy("has_item", has(URANIUM_PLATE))
				.io(Ingredient.of(URANIUM_PLATE), 4, new ItemStack(FTBICItems.getResourceFromType(URANIUM, GEAR).orElseThrow().get(), 1))
				.save(consumer, rollingLoc(URANIUM_PLATE.location().getPath() + "_to_" + FTBICItems.getResourceFromType(URANIUM, GEAR).orElseThrow().get()));

		MachineRecipeBuilder.rolling()
				.unlockedBy("has_item", has(IRIDIUM_PLATE))
				.io(Ingredient.of(IRIDIUM_PLATE), 4, new ItemStack(FTBICItems.getResourceFromType(IRIDIUM, GEAR).orElseThrow().get(), 1))
				.save(consumer, rollingLoc(IRIDIUM_PLATE.location().getPath() + "_to_" + FTBICItems.getResourceFromType(IRIDIUM, GEAR).orElseThrow().get()));

		MachineRecipeBuilder.rolling()
				.unlockedBy("has_item", has(ALUMINUM_PLATE))
				.io(Ingredient.of(ALUMINUM_PLATE), 4, new ItemStack(FTBICItems.getResourceFromType(ALUMINUM, GEAR).orElseThrow().get(), 1))
				.save(consumer, rollingLoc(ALUMINUM_PLATE.location().getPath() + "_to_" + FTBICItems.getResourceFromType(ALUMINUM, GEAR).orElseThrow().get()));

		MachineRecipeBuilder.rolling()
				.unlockedBy("has_item", has(ENDERIUM_PLATE))
				.io(Ingredient.of(ENDERIUM_PLATE), 4, new ItemStack(FTBICItems.getResourceFromType(ENDERIUM, GEAR).orElseThrow().get(), 1))
				.save(consumer, rollingLoc(ENDERIUM_PLATE.location().getPath() + "_to_" + FTBICItems.getResourceFromType(ENDERIUM, GEAR).orElseThrow().get()));
	}
}
