package dev.ftb.mods.ftbic.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.ItemStack;
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
		SimpleCookingRecipeBuilder.cooking(Ingredient.of(IRON_INGOT), INDUSTRIAL_GRADE_METAL, 0F, 400, RecipeSerializer.SMELTING_RECIPE)
				.unlockedBy("has_item", has(IRON_INGOT))
				.save(consumer, smeltingLoc("industrial_grade_metal"));

		SimpleCookingRecipeBuilder.cooking(Ingredient.of(IRON_INGOT), INDUSTRIAL_GRADE_METAL, 0F, 200, RecipeSerializer.BLASTING_RECIPE)
				.unlockedBy("has_item", has(IRON_INGOT))
				.save(consumer, blastingLoc("industrial_grade_metal"));

		SimpleCookingRecipeBuilder.cooking(Ingredient.of(SLIMEBALL), RUBBER, 0F, 300, RecipeSerializer.SMELTING_RECIPE)
				.unlockedBy("has_item", has(SLIMEBALL))
				.save(consumer, smeltingLoc("rubber"));

		SimpleCookingRecipeBuilder.cooking(Ingredient.of(SLIMEBALL), RUBBER, 0F, 800, RecipeSerializer.CAMPFIRE_COOKING_RECIPE)
				.unlockedBy("has_item", has(SLIMEBALL))
				.save(consumer, campfireCookingLoc("rubber"));

		SimpleCookingRecipeBuilder.cooking(Ingredient.of(SLIMEBALL), RUBBER, 0F, 150, RecipeSerializer.SMOKING_RECIPE)
				.unlockedBy("has_item", has(SLIMEBALL))
				.save(consumer, smokingLoc("rubber"));

		SimpleCookingRecipeBuilder.cooking(Ingredient.of(MIXED_METAL_BLEND), ADVANCED_ALLOY, 0F, 400, RecipeSerializer.SMELTING_RECIPE)
				.unlockedBy("has_item", has(MIXED_METAL_BLEND))
				.save(consumer, smeltingLoc("advanced_alloy"));

		SimpleCookingRecipeBuilder.cooking(Ingredient.of(MIXED_METAL_BLEND), ADVANCED_ALLOY, 0F, 200, RecipeSerializer.BLASTING_RECIPE)
				.unlockedBy("has_item", has(MIXED_METAL_BLEND))
				.save(consumer, blastingLoc("advanced_alloy"));

		ShapelessRecipeBuilder.shapeless(MIXED_METAL_BLEND, 3)
				.unlockedBy("has_item", has(INDUSTRIAL_GRADE_METAL))
				.group(MODID + ":mixed_metal_ingot")
				.requires(Ingredient.merge(Arrays.asList(Ingredient.of(IRON_DUST), Ingredient.of(LEAD_DUST))), 3)
				.requires(Ingredient.merge(Arrays.asList(Ingredient.of(BRONZE_DUST))), 3)
				.requires(Ingredient.merge(Arrays.asList(Ingredient.of(TIN_DUST), Ingredient.of(ALUMINUM_DUST))), 3)
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
				.define('M', INDUSTRIAL_GRADE_METAL)
				.save(consumer, shapedLoc("fuse"));

		ShapedRecipeBuilder.shaped(MACHINE_BLOCK)
				.unlockedBy("has_item", has(INDUSTRIAL_GRADE_METAL))
				.group(MODID + ":machine_block")
				.pattern("MMM")
				.pattern("MFM")
				.pattern("MMM")
				.define('M', INDUSTRIAL_GRADE_METAL)
				.define('F', FUSE)
				.save(consumer, shapedLoc("machine_block"));

		ShapedRecipeBuilder.shaped(ADVANCED_MACHINE_BLOCK)
				.unlockedBy("has_item", has(MACHINE_BLOCK))
				.group(MODID + ":advanced_machine_block")
				.pattern("WCW")
				.pattern("AMA")
				.pattern("WCW")
				.define('M', MACHINE_BLOCK)
				.define('C', CARBON_PLATE)
				.define('A', ADVANCED_ALLOY)
				.define('W', COPPER_COIL)
				.save(consumer, shapedLoc("advanced_machine_block_h"));

		ShapedRecipeBuilder.shaped(ADVANCED_MACHINE_BLOCK)
				.unlockedBy("has_item", has(MACHINE_BLOCK))
				.group(MODID + ":advanced_machine_block")
				.pattern("WAW")
				.pattern("CMC")
				.pattern("WAW")
				.define('M', MACHINE_BLOCK)
				.define('C', CARBON_PLATE)
				.define('A', ADVANCED_ALLOY)
				.define('W', COPPER_COIL)
				.save(consumer, shapedLoc("advanced_machine_block_v"));

		ShapedRecipeBuilder.shaped(EMPTY_CELL, 4)
				.unlockedBy("has_item", has(TIN_INGOT))
				.group(MODID + ":empty_cell")
				.pattern(" T ")
				.pattern("T T")
				.pattern(" T ")
				.define('T', TIN_INGOT)
				.save(consumer, shapedLoc("empty_cell"));

		ShapedRecipeBuilder.shaped(EMPTY_CAN, 10)
				.unlockedBy("has_item", has(TIN_INGOT))
				.group(MODID + ":empty_can")
				.pattern("T T")
				.pattern("TTT")
				.define('T', TIN_INGOT)
				.save(consumer, shapedLoc("empty_can"));

		ShapedRecipeBuilder.shaped(IRIDIUM_ALLOY)
				.unlockedBy("has_item", has(IRIDIUM_INGOT))
				.group(MODID + ":iridium_alloy")
				.pattern("IAI")
				.pattern("ADA")
				.pattern("IAI")
				.define('I', IRIDIUM_INGOT)
				.define('A', ADVANCED_ALLOY)
				.define('D', DIAMOND)
				.save(consumer, shapedLoc("iridium_alloy"));

		ShapedRecipeBuilder.shaped(CARBON_FIBERS)
				.unlockedBy("has_item", has(COAL_DUST))
				.group(MODID + ":carbon_fibers")
				.pattern("DD")
				.pattern("DD")
				.define('D', COAL_DUST)
				.save(consumer, shapedLoc("carbon_fibers"));

		ShapelessRecipeBuilder.shapeless(CARBON_FIBER_MESH)
				.unlockedBy("has_item", has(CARBON_FIBERS))
				.group(MODID + ":carbon_fiber_mesh")
				.requires(CARBON_FIBERS, 2)
				.save(consumer, shapedLoc("carbon_fiber_mesh"));

		ShapedRecipeBuilder.shaped(COPPER_COIL)
				.unlockedBy("has_item", has(COPPER_WIRE))
				.group(MODID + ":copper_coil")
				.pattern("WWW")
				.pattern("WRW")
				.pattern("WWW")
				.define('W', COPPER_WIRE)
				.define('R', IRON_ROD)
				.save(consumer, shapedLoc("copper_coil"));

		MachineRecipeBuilder.separating()
				.unlockedBy("has_item", has(LATEX))
				.inputItem(Ingredient.of(LATEX))
				.outputItem(new ItemStack(RUBBER, 3))
				.save(consumer, separatingLoc("rubber_from_latex"));

		MachineRecipeBuilder.separating()
				.unlockedBy("has_item", has(RUBBERWOOD_LOG))
				.inputItem(Ingredient.of(RUBBERWOOD_LOG))
				.outputItem(new ItemStack(LATEX), 0.2D)
				.save(consumer, separatingLoc("latex_from_log"));

		MachineRecipeBuilder.separating()
				.unlockedBy("has_item", has(RUBBERWOOD_SAPLING))
				.inputItem(Ingredient.of(RUBBERWOOD_SAPLING))
				.outputItem(new ItemStack(LATEX), 0.1D)
				.save(consumer, separatingLoc("latex_from_sapling"));

		MachineRecipeBuilder.separating()
				.unlockedBy("has_item", has(RUBBERWOOD_LEAVES))
				.inputItem(Ingredient.of(RUBBERWOOD_LEAVES))
				.outputItem(new ItemStack(LATEX), 0.1D)
				.save(consumer, separatingLoc("latex_from_leaves"));

		MachineRecipeBuilder.compressing()
				.unlockedBy("has_item", has(IRON_INGOT))
				.inputItem(Ingredient.of(IRON_INGOT), 3)
				.outputItem(new ItemStack(INDUSTRIAL_GRADE_METAL, 3))
				.save(consumer, compressingLoc("industrial_grade_metal"));

		MachineRecipeBuilder.compressing()
				.unlockedBy("has_item", has(CARBON_FIBER_MESH))
				.inputItem(Ingredient.of(CARBON_FIBER_MESH))
				.outputItem(new ItemStack(CARBON_PLATE))
				.save(consumer, compressingLoc("carbon_plate"));

		MachineRecipeBuilder.compressing()
				.unlockedBy("has_item", has(COAL_BALL))
				.inputItem(Ingredient.of(COAL_BALL))
				.outputItem(new ItemStack(COMPRESSED_COAL_BALL))
				.save(consumer, compressingLoc("compressed_coal_ball"));
	}
}
