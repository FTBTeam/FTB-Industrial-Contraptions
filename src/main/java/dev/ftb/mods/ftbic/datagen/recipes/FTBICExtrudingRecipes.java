package dev.ftb.mods.ftbic.datagen.recipes;

import dev.ftb.mods.ftbic.datagen.FTBICRecipesGen;
import dev.ftb.mods.ftbic.item.FTBICItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Consumer;

import static dev.ftb.mods.ftbic.world.ResourceElements.*;
import static dev.ftb.mods.ftbic.world.ResourceType.WIRE;
import static dev.ftb.mods.ftbic.world.ResourceType.GEAR;
import static dev.ftb.mods.ftbic.world.ResourceType.ROD;

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

		MachineRecipeBuilder.extruding()
				.unlockedBy("has_item", has(TIN_PLATE))
				.io(Ingredient.of(TIN_PLATE), 4, new ItemStack(FTBICItems.getResourceFromType(TIN, GEAR).orElseThrow().get(), 1))
				.save(consumer, extrudingLoc(TIN_PLATE.location().getPath() + "_to_" + FTBICItems.getResourceFromType(TIN, GEAR).orElseThrow().get()));

		MachineRecipeBuilder.extruding()
				.unlockedBy("has_item", has(LEAD_PLATE))
				.io(Ingredient.of(LEAD_PLATE), 4, new ItemStack(FTBICItems.getResourceFromType(LEAD, GEAR).orElseThrow().get(), 1))
				.save(consumer, extrudingLoc(LEAD_PLATE.location().getPath() + "_to_" + FTBICItems.getResourceFromType(LEAD, GEAR).orElseThrow().get()));

		MachineRecipeBuilder.extruding()
				.unlockedBy("has_item", has(URANIUM_PLATE))
				.io(Ingredient.of(URANIUM_PLATE), 4, new ItemStack(FTBICItems.getResourceFromType(URANIUM, GEAR).orElseThrow().get(), 1))
				.save(consumer, extrudingLoc(URANIUM_PLATE.location().getPath() + "_to_" + FTBICItems.getResourceFromType(URANIUM, GEAR).orElseThrow().get()));

		MachineRecipeBuilder.extruding()
				.unlockedBy("has_item", has(IRIDIUM_PLATE))
				.io(Ingredient.of(IRIDIUM_PLATE), 4, new ItemStack(FTBICItems.getResourceFromType(IRIDIUM, GEAR).orElseThrow().get(), 1))
				.save(consumer, extrudingLoc(IRIDIUM_PLATE.location().getPath() + "_to_" + FTBICItems.getResourceFromType(IRIDIUM, GEAR).orElseThrow().get()));

		MachineRecipeBuilder.extruding()
				.unlockedBy("has_item", has(ALUMINUM_PLATE))
				.io(Ingredient.of(ALUMINUM_PLATE), 4, new ItemStack(FTBICItems.getResourceFromType(ALUMINUM, GEAR).orElseThrow().get(), 1))
				.save(consumer, extrudingLoc(ALUMINUM_PLATE.location().getPath() + "_to_" + FTBICItems.getResourceFromType(ALUMINUM, GEAR).orElseThrow().get()));

		MachineRecipeBuilder.extruding()
				.unlockedBy("has_item", has(ENDERIUM_PLATE))
				.io(Ingredient.of(ENDERIUM_PLATE), 4, new ItemStack(FTBICItems.getResourceFromType(ENDERIUM, GEAR).orElseThrow().get(), 1))
				.save(consumer, extrudingLoc(ENDERIUM_PLATE.location().getPath() + "_to_" + FTBICItems.getResourceFromType(ENDERIUM, GEAR).orElseThrow().get()));

		MachineRecipeBuilder.extruding()
				.unlockedBy("has_item", has(GOLD_PLATE))
				.io(Ingredient.of(GOLD_PLATE), 4, new ItemStack(FTBICItems.getResourceFromType(GOLD, GEAR).orElseThrow().get(), 1))
				.save(consumer, extrudingLoc(GOLD_PLATE.location().getPath() + "_to_" + FTBICItems.getResourceFromType(GOLD, GEAR).orElseThrow().get()));

		MachineRecipeBuilder.extruding()
				.unlockedBy("has_item", has(IRON_PLATE))
				.io(Ingredient.of(IRON_PLATE), 4, new ItemStack(FTBICItems.getResourceFromType(IRON, GEAR).orElseThrow().get(), 1))
				.save(consumer, extrudingLoc(IRON_PLATE.location().getPath() + "_to_" + FTBICItems.getResourceFromType(IRON, GEAR).orElseThrow().get()));

		MachineRecipeBuilder.extruding()
				.unlockedBy("has_item", has(COPPER_PLATE))
				.io(Ingredient.of(COPPER_PLATE), 4, new ItemStack(FTBICItems.getResourceFromType(COPPER, GEAR).orElseThrow().get(), 1))
				.save(consumer, extrudingLoc(COPPER_PLATE.location().getPath() + "_to_" + FTBICItems.getResourceFromType(COPPER, GEAR).orElseThrow().get()));

		MachineRecipeBuilder.extruding()
				.unlockedBy("has_item", has(BRONZE_PLATE))
				.io(Ingredient.of(BRONZE_PLATE), 4, new ItemStack(FTBICItems.getResourceFromType(BRONZE, GEAR).orElseThrow().get(), 1))
				.save(consumer, extrudingLoc(BRONZE_PLATE.location().getPath() + "_to_" + FTBICItems.getResourceFromType(BRONZE, GEAR).orElseThrow().get()));


		MachineRecipeBuilder.extruding()
				.unlockedBy("has_item", has(COPPER_ROD))
				.io(Ingredient.of(COPPER_ROD), 1, new ItemStack(FTBICItems.getResourceFromType(COPPER, WIRE).orElseThrow().get(), 2))
				.save(consumer, extrudingLoc(COPPER_ROD.location().getPath() + "_to_" + FTBICItems.getResourceFromType(COPPER, WIRE).orElseThrow().get()));

		MachineRecipeBuilder.extruding()
				.unlockedBy("has_item", has(GOLD_ROD))
				.io(Ingredient.of(GOLD_ROD), 1, new ItemStack(FTBICItems.getResourceFromType(GOLD, WIRE).orElseThrow().get(), 2))
				.save(consumer, extrudingLoc(GOLD_ROD.location().getPath() + "_to_" + FTBICItems.getResourceFromType(GOLD, WIRE).orElseThrow().get()));

		MachineRecipeBuilder.extruding()
				.unlockedBy("has_item", has(ALUMINUM_ROD))
				.io(Ingredient.of(ALUMINUM_ROD), 1, new ItemStack(FTBICItems.getResourceFromType(ALUMINUM, WIRE).orElseThrow().get(), 2))
				.save(consumer, extrudingLoc(ALUMINUM_ROD.location().getPath() + "_to_" + FTBICItems.getResourceFromType(ALUMINUM, WIRE).orElseThrow().get()));

		MachineRecipeBuilder.extruding()
				.unlockedBy("has_item", has(ENDERIUM_ROD))
				.io(Ingredient.of(ENDERIUM_ROD), 1, new ItemStack(FTBICItems.getResourceFromType(ENDERIUM, WIRE).orElseThrow().get(), 2))
				.save(consumer, extrudingLoc(ENDERIUM_ROD.location().getPath() + "_to_" + FTBICItems.getResourceFromType(ENDERIUM, WIRE).orElseThrow().get()));
	}
}
