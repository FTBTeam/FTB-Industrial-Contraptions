package dev.ftb.mods.ftbic.datagen;

import com.ridanisaurus.emendatusenigmatica.registries.ItemHandler;
import com.ridanisaurus.emendatusenigmatica.util.Materials;
import com.ridanisaurus.emendatusenigmatica.util.ProcessedMaterials;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Consumer;

public class FTBICVanillaRecipes extends FTBICRecipesGen {
	public FTBICVanillaRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		MachineRecipeBuilder.macerating()
				.unlockedBy("has_item", has(BLAZE_ROD))
				.inputItem(Ingredient.of(BLAZE_ROD))
				.outputItem(new ItemStack(Items.BLAZE_POWDER, 5))
				.save(consumer, maceratingLoc("blaze_powder"));

		MachineRecipeBuilder.macerating()
				.unlockedBy("has_item", has(Items.BONE))
				.inputItem(Ingredient.of(Items.BONE))
				.outputItem(new ItemStack(Items.BONE_MEAL, 5))
				.save(consumer, maceratingLoc("bone_meal"));

		MachineRecipeBuilder.macerating()
				.unlockedBy("has_item", has(COAL))
				.inputItem(Ingredient.of(COAL))
				.outputItem(new ItemStack(ItemHandler.backingItemTable.get(ProcessedMaterials.DUST, Materials.COAL).get()))
				.save(consumer, maceratingLoc("coal_dust"));

		MachineRecipeBuilder.macerating()
				.unlockedBy("has_item", has(COBBLESTONE))
				.inputItem(Ingredient.of(COBBLESTONE))
				.outputItem(new ItemStack(Items.SAND))
				.save(consumer, maceratingLoc("sand"));

		MachineRecipeBuilder.macerating()
				.unlockedBy("has_item", has(STONE))
				.inputItem(Ingredient.of(STONE))
				.outputItem(new ItemStack(Items.COBBLESTONE))
				.save(consumer, maceratingLoc("cobblestone"));

		MachineRecipeBuilder.macerating()
				.unlockedBy("has_item", has(GRAVEL))
				.inputItem(Ingredient.of(GRAVEL))
				.outputItem(new ItemStack(Items.FLINT))
				.save(consumer, maceratingLoc("flint"));

		MachineRecipeBuilder.macerating()
				.unlockedBy("has_item", has(Items.SNOW_BLOCK))
				.inputItem(Ingredient.of(Items.SNOW_BLOCK))
				.outputItem(new ItemStack(Items.SNOWBALL))
				.save(consumer, maceratingLoc("snowball"));

		MachineRecipeBuilder.macerating()
				.unlockedBy("has_item", has(WOOL))
				.inputItem(Ingredient.of(WOOL))
				.outputItem(new ItemStack(Items.STRING, 4))
				.save(consumer, maceratingLoc("string"));

		MachineRecipeBuilder.extracting()
				.unlockedBy("has_item", has(QUARTZ))
				.inputItem(Ingredient.of(QUARTZ))
				.outputItem(new ItemStack(ItemHandler.backingItemTable.get(ProcessedMaterials.GEM, Materials.SILICON).get(), 3))
				.save(consumer, extractingLoc("silicon"));

		MachineRecipeBuilder.compressing()
				.unlockedBy("has_item", has(WATER_CELL))
				.inputItem(Ingredient.of(WATER_CELL))
				.outputItem(new ItemStack(Items.SNOWBALL))
				.save(consumer, compressingLoc("snowball"));

		MachineRecipeBuilder.compressing()
				.unlockedBy("has_item", has(Items.SNOWBALL))
				.inputItem(Ingredient.of(Items.SNOWBALL))
				.outputItem(new ItemStack(Items.ICE))
				.save(consumer, compressingLoc("ice"));

		MachineRecipeBuilder.compressing()
				.unlockedBy("has_item", has(GRAPHENE))
				.inputItem(Ingredient.of(GRAPHENE))
				.outputItem(new ItemStack(Items.DIAMOND))
				.save(consumer, compressingLoc("diamond"));
	}
}
