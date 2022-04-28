package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.item.FTBICItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Consumer;

import static dev.ftb.mods.ftbic.world.ResourceType.*;
import static dev.ftb.mods.ftbic.world.ResourceElements.*;

public class FTBICExtrudingRecipes extends FTBICRecipesGen {
	public FTBICExtrudingRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		MachineRecipeBuilder.extruding()
				.unlockedBy("has_item", has(TIN_INGOT))
				.io(Ingredient.of(TIN_INGOT), new ItemStack(FTBICItems.getResourceFromType(TIN, ROD).orElseThrow().get(), 2))
				.save(consumer, extrudingLoc(TIN_INGOT.location().getPath() + "_to_" + FTBICItems.getResourceFromType(TIN, ROD).orElseThrow().get()));

		MachineRecipeBuilder.extruding()
				.unlockedBy("has_item", has(LEAD_INGOT))
				.io(Ingredient.of(LEAD_INGOT), new ItemStack(FTBICItems.getResourceFromType(LEAD, ROD).orElseThrow().get(), 2))
				.save(consumer, extrudingLoc(LEAD_INGOT.location().getPath() + "_to_" + FTBICItems.getResourceFromType(LEAD, ROD).orElseThrow().get()));

		MachineRecipeBuilder.extruding()
				.unlockedBy("has_item", has(URANIUM_INGOT))
				.io(Ingredient.of(URANIUM_INGOT), new ItemStack(FTBICItems.getResourceFromType(URANIUM, ROD).orElseThrow().get(), 2))
				.save(consumer, extrudingLoc(URANIUM_INGOT.location().getPath() + "_to_" + FTBICItems.getResourceFromType(URANIUM, ROD).orElseThrow().get()));

		MachineRecipeBuilder.extruding()
				.unlockedBy("has_item", has(IRIDIUM_INGOT))
				.io(Ingredient.of(IRIDIUM_INGOT), new ItemStack(FTBICItems.getResourceFromType(IRIDIUM, ROD).orElseThrow().get(), 2))
				.save(consumer, extrudingLoc(IRIDIUM_INGOT.location().getPath() + "_to_" + FTBICItems.getResourceFromType(IRIDIUM, ROD).orElseThrow().get()));

		MachineRecipeBuilder.extruding()
				.unlockedBy("has_item", has(ALUMINUM_INGOT))
				.io(Ingredient.of(ALUMINUM_INGOT), new ItemStack(FTBICItems.getResourceFromType(ALUMINUM, ROD).orElseThrow().get(), 2))
				.save(consumer, extrudingLoc(ALUMINUM_INGOT.location().getPath() + "_to_" + FTBICItems.getResourceFromType(ALUMINUM, ROD).orElseThrow().get()));

		MachineRecipeBuilder.extruding()
				.unlockedBy("has_item", has(ENDERIUM_INGOT))
				.io(Ingredient.of(ENDERIUM_INGOT), new ItemStack(FTBICItems.getResourceFromType(ENDERIUM, ROD).orElseThrow().get(), 2))
				.save(consumer, extrudingLoc(ENDERIUM_INGOT.location().getPath() + "_to_" + FTBICItems.getResourceFromType(ENDERIUM, ROD).orElseThrow().get()));

		MachineRecipeBuilder.extruding()
				.unlockedBy("has_item", has(IRON_INGOT))
				.io(Ingredient.of(IRON_INGOT), new ItemStack(FTBICItems.getResourceFromType(IRON, ROD).orElseThrow().get(), 2))
				.save(consumer, extrudingLoc(IRON_INGOT.location().getPath() + "_to_" + FTBICItems.getResourceFromType(IRON, ROD).orElseThrow().get()));

		MachineRecipeBuilder.extruding()
				.unlockedBy("has_item", has(GOLD_INGOT))
				.io(Ingredient.of(GOLD_INGOT), new ItemStack(FTBICItems.getResourceFromType(GOLD, ROD).orElseThrow().get(), 2))
				.save(consumer, extrudingLoc(GOLD_INGOT.location().getPath() + "_to_" + FTBICItems.getResourceFromType(GOLD, ROD).orElseThrow().get()));

		MachineRecipeBuilder.extruding()
				.unlockedBy("has_item", has(COPPER_INGOT))
				.io(Ingredient.of(COPPER_INGOT), new ItemStack(FTBICItems.getResourceFromType(COPPER, ROD).orElseThrow().get(), 2))
				.save(consumer, extrudingLoc(COPPER_INGOT.location().getPath() + "_to_" + FTBICItems.getResourceFromType(COPPER, ROD).orElseThrow().get()));

		MachineRecipeBuilder.extruding()
				.unlockedBy("has_item", has(BRONZE_INGOT))
				.io(Ingredient.of(BRONZE_INGOT), new ItemStack(FTBICItems.getResourceFromType(BRONZE, ROD).orElseThrow().get(), 2))
				.save(consumer, extrudingLoc(BRONZE_INGOT.location().getPath() + "_to_" + FTBICItems.getResourceFromType(BRONZE, ROD).orElseThrow().get()));
	}
}
