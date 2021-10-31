package dev.ftb.mods.ftbic.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.Arrays;
import java.util.function.Consumer;

public class FTBICComponentRecipes extends FTBICRecipesGen {
	public FTBICComponentRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		SimpleCookingRecipeBuilder.cooking(Ingredient.of(IRON_INGOT), INDUSTRIAL_GRADE_METAL, 0F, 200, RecipeSerializer.BLASTING_RECIPE)
				.unlockedBy("has_item", has(IRON_INGOT))
				.save(consumer, blastingLoc("industrial_grade_metal"));

		SimpleCookingRecipeBuilder.cooking(Ingredient.of(SLIMEBALL), RUBBER, 0F, 150, RecipeSerializer.SMELTING_RECIPE)
				.unlockedBy("has_item", has(SLIMEBALL))
				.save(consumer, smeltingLoc("rubber"));

		SimpleCookingRecipeBuilder.cooking(Ingredient.of(SLIMEBALL), RUBBER, 0F, 300, RecipeSerializer.CAMPFIRE_COOKING_RECIPE)
				.unlockedBy("has_item", has(SLIMEBALL))
				.save(consumer, campfireCookingLoc("rubber"));

		ShapedRecipeBuilder.shaped(MIXED_METAL_INGOT, 3)
				.unlockedBy("has_item", has(INDUSTRIAL_GRADE_METAL))
				.group(MODID + ":mixed_metal_ingot")
				.pattern("MMM")
				.pattern("CCC")
				.pattern("TTT")
				.define('M', INDUSTRIAL_GRADE_METAL)
				.define('C', COPPER_INGOT)
				.define('T', TIN_INGOT)
				.save(consumer, shapedLoc("mixed_metal_ingot"));

		ShapedRecipeBuilder.shaped(RUBBER_SHEET)
				.unlockedBy("has_item", has(RUBBER))
				.group(MODID + ":rubber_sheet")
				.pattern("III")
				.define('I', RUBBER)
				.save(consumer, shapedLoc("rubber_sheet"));

		ShapedRecipeBuilder.shaped(SCRAP_BOX)
				.unlockedBy("has_item", has(SCRAP))
				.group(MODID + ":scrap_box")
				.pattern("SSS")
				.pattern("SSS")
				.pattern("SSS")
				.define('S', SCRAP)
				.save(consumer, shapedLoc("scrap_box"));

		ShapedRecipeBuilder.shaped(COAL_BALL)
				.unlockedBy("has_item", has(COAL))
				.group(MODID + ":coal_ball")
				.pattern("CCC")
				.pattern("CFC")
				.pattern("CCC")
				.define('C', COAL)
				.define('F', FLINT)
				.save(consumer, shapedLoc("coal_ball"));

		ShapedRecipeBuilder.shaped(GRAPHENE)
				.unlockedBy("has_item", has(COMPRESSED_COAL_BALL))
				.group(MODID + ":graphene")
				.pattern("CCC")
				.pattern("COC")
				.pattern("CCC")
				.define('C', COMPRESSED_COAL_BALL)
				.define('O', OBSIDIAN)
				.save(consumer, shapedLoc("graphene"));

		ShapedRecipeBuilder.shaped(ENERGY_CRYSTAL)
				.unlockedBy("has_item", has(DIAMOND))
				.group(MODID + ":energy_crystal")
				.pattern("RQR")
				.pattern("QDQ")
				.pattern("RQR")
				.define('R', REDSTONE)
				.define('D', DIAMOND)
				.define('Q', SILICON)
				.save(consumer, shapedLoc("energy_crystal"));

		ShapelessRecipeBuilder.shapeless(RUBBER, 3)
				.unlockedBy("has_item", has(RUBBER))
				.group(MODID + ":rubber")
				.requires(RUBBER_SHEET)
				.save(consumer, shapelessLoc("rubber"));

		ShapelessRecipeBuilder.shapeless(SCRAP, 9)
				.unlockedBy("has_item", has(SCRAP_BOX))
				.group(MODID + ":scrap")
				.requires(SCRAP_BOX)
				.save(consumer, shapelessLoc("scrap"));

		ShapedRecipeBuilder.shaped(FUSE, 24)
				.unlockedBy("has_item", has(GLASS))
				.group(MODID + ":fuse")
				.pattern("GGG")
				.pattern("MMM")
				.pattern("GGG")
				.define('G', GLASS)
				.define('M', Ingredient.merge(Arrays.asList(Ingredient.of(IRON_INGOT), Ingredient.of(COPPER_INGOT), Ingredient.of(TIN_INGOT))))
				.save(consumer, shapedLoc("fuse"));

		ShapedRecipeBuilder.shaped(MACHINE_BLOCK)
				.unlockedBy("has_item", has(INDUSTRIAL_GRADE_METAL))
				.group(MODID + ":machine_block")
				.pattern("MMM")
				.pattern("M M")
				.pattern("MMM")
				.define('M', INDUSTRIAL_GRADE_METAL)
				.save(consumer, shapedLoc("machine_block"));

		ShapedRecipeBuilder.shaped(ADVANCED_MACHINE_BLOCK)
				.unlockedBy("has_item", has(MACHINE_BLOCK))
				.group(MODID + ":advanced_machine_block")
				.pattern(" C ")
				.pattern("AMA")
				.pattern(" C ")
				.define('M', MACHINE_BLOCK)
				.define('C', CARBON_PLATE)
				.define('A', ADVANCED_ALLOY)
				.save(consumer, shapedLoc("advanced_machine_block_h"));

		ShapedRecipeBuilder.shaped(ADVANCED_MACHINE_BLOCK)
				.unlockedBy("has_item", has(MACHINE_BLOCK))
				.group(MODID + ":advanced_machine_block")
				.pattern(" A ")
				.pattern("CMC")
				.pattern(" A ")
				.define('M', MACHINE_BLOCK)
				.define('C', CARBON_PLATE)
				.define('A', ADVANCED_ALLOY)
				.save(consumer, shapedLoc("advanced_machine_block_v"));

		ShapedRecipeBuilder.shaped(EMPTY_CELL, 4)
				.unlockedBy("has_item", has(TIN_INGOT))
				.group(MODID + ":empty_cell")
				.pattern(" T ")
				.pattern("T T")
				.pattern(" T ")
				.define('T', TIN_INGOT)
				.save(consumer, shapedLoc("empty_cell"));

		ShapedRecipeBuilder.shaped(EMPTY_CAN, 5)
				.unlockedBy("has_item", has(TIN_INGOT))
				.group(MODID + ":empty_can")
				.pattern("T T")
				.pattern("TTT")
				.define('T', TIN_INGOT)
				.save(consumer, shapedLoc("empty_can"));

		MachineRecipeBuilder.macerating()
				.unlockedBy("has_item", has(Items.NETHER_STAR))
				.inputItem(Ingredient.of(Items.NETHER_STAR))
				.outputItem(new ItemStack(IRIDIUM_DUST, 4))
				.save(consumer, maceratingLoc("iridium_dust"));

		MachineRecipeBuilder.extracting()
				.unlockedBy("has_item", has(LATEX))
				.inputItem(Ingredient.of(LATEX))
				.outputItem(new ItemStack(RUBBER, 3))
				.save(consumer, extractingLoc("rubber_from_latex"));

		MachineRecipeBuilder.extracting()
				.unlockedBy("has_item", has(RUBBERWOOD_LOG))
				.inputItem(Ingredient.of(RUBBERWOOD_LOG))
				.outputItem(new ItemStack(LATEX))
				.save(consumer, extractingLoc("latex_from_log"));

		MachineRecipeBuilder.extracting()
				.unlockedBy("has_item", has(RUBBERWOOD_SAPLING))
				.inputItem(Ingredient.of(RUBBERWOOD_SAPLING))
				.outputItem(new ItemStack(LATEX))
				.save(consumer, extractingLoc("latex_from_sapling"));

		MachineRecipeBuilder.extracting()
				.unlockedBy("has_item", has(RUBBERWOOD_LEAVES))
				.inputItem(Ingredient.of(RUBBERWOOD_LEAVES))
				.outputItem(new ItemStack(LATEX))
				.save(consumer, extractingLoc("latex_from_leaves"));

		MachineRecipeBuilder.compressing()
				.unlockedBy("has_item", has(IRON_INGOT))
				.inputItem(Ingredient.of(IRON_INGOT), 3)
				.outputItem(new ItemStack(INDUSTRIAL_GRADE_METAL, 3))
				.save(consumer, compressingLoc("industrial_grade_metal"));

		MachineRecipeBuilder.compressing()
				.unlockedBy("has_item", has(MIXED_METAL_INGOT))
				.inputItem(Ingredient.of(MIXED_METAL_INGOT))
				.outputItem(new ItemStack(ADVANCED_ALLOY))
				.save(consumer, compressingLoc("advanced_alloy"));

		MachineRecipeBuilder.compressing()
				.unlockedBy("has_item", has(RAW_CARBON_MESH))
				.inputItem(Ingredient.of(RAW_CARBON_MESH))
				.outputItem(new ItemStack(CARBON_PLATE))
				.save(consumer, compressingLoc("carbon_plate"));

		MachineRecipeBuilder.compressing()
				.unlockedBy("has_item", has(COAL_BALL))
				.inputItem(Ingredient.of(COAL_BALL))
				.outputItem(new ItemStack(COMPRESSED_COAL_BALL))
				.save(consumer, compressingLoc("compressed_coal_ball"));
	}
}
