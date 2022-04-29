package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.item.FTBICItems;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.ConditionalRecipe;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

public class FTBICComponentRecipes extends FTBICRecipesGen {
	public FTBICComponentRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		Function<TagKey<Item>, InventoryChangeTrigger.TriggerInstance> tagKeyHas = (e) -> RecipeProvider.inventoryTrigger(ItemPredicate.Builder.item().of(e).build());

		SimpleCookingRecipeBuilder.cooking(Ingredient.of(IRON_INGOT), INDUSTRIAL_GRADE_METAL, 0F, 400, RecipeSerializer.SMELTING_RECIPE)
				.unlockedBy("has_item", tagKeyHas.apply(IRON_INGOT))
				.save(consumer, smeltingLoc("industrial_grade_metal"));

		SimpleCookingRecipeBuilder.cooking(Ingredient.of(IRON_INGOT), INDUSTRIAL_GRADE_METAL, 0F, 200, RecipeSerializer.BLASTING_RECIPE)
				.unlockedBy("has_item", tagKeyHas.apply(IRON_INGOT))
				.save(consumer, blastingLoc("industrial_grade_metal"));

		SimpleCookingRecipeBuilder.cooking(Ingredient.of(SLIMEBALL), RUBBER, 0F, 300, RecipeSerializer.SMELTING_RECIPE)
				.unlockedBy("has_item", tagKeyHas.apply(SLIMEBALL))
				.save(consumer, smeltingLoc("rubber"));

		SimpleCookingRecipeBuilder.cooking(Ingredient.of(SLIMEBALL), RUBBER, 0F, 800, RecipeSerializer.CAMPFIRE_COOKING_RECIPE)
				.unlockedBy("has_item", tagKeyHas.apply(SLIMEBALL))
				.save(consumer, campfireCookingLoc("rubber"));

		SimpleCookingRecipeBuilder.cooking(Ingredient.of(SLIMEBALL), RUBBER, 0F, 150, RecipeSerializer.SMOKING_RECIPE)
				.unlockedBy("has_item", tagKeyHas.apply(SLIMEBALL))
				.save(consumer, smokingLoc("rubber"));

		SimpleCookingRecipeBuilder.cooking(Ingredient.of(MIXED_METAL_BLEND), ADVANCED_ALLOY, 0F, 400, RecipeSerializer.SMELTING_RECIPE)
				.unlockedBy("has_item", has(MIXED_METAL_BLEND))
				.save(consumer, smeltingLoc("advanced_alloy"));

		SimpleCookingRecipeBuilder.cooking(Ingredient.of(MIXED_METAL_BLEND), ADVANCED_ALLOY, 0F, 200, RecipeSerializer.BLASTING_RECIPE)
				.unlockedBy("has_item", has(MIXED_METAL_BLEND))
				.save(consumer, blastingLoc("advanced_alloy"));

		ConditionalRecipe.builder()
				.addCondition(not(tagEmpty(BRONZE_DUST)))
				.addRecipe( finishedRecipeConsumer ->
						ShapelessRecipeBuilder.shapeless(MIXED_METAL_BLEND, 3)
								.unlockedBy("has_item", has(INDUSTRIAL_GRADE_METAL))
								.group(MODID + ":mixed_metal_blend")
								.requires(Ingredient.merge(Arrays.asList(Ingredient.of(IRON_DUST), Ingredient.of(LEAD_DUST))), 3)
								.requires(Ingredient.of(BRONZE_DUST), 3)
								.requires(Ingredient.merge(Arrays.asList(Ingredient.of(TIN_DUST), Ingredient.of(ALUMINUM_DUST))), 3)
								.save(finishedRecipeConsumer, shapelessLoc("mixed_metal_blend_1"))
				)
				.generateAdvancement()
				.build(consumer, shapelessLoc("mixed_metal_blend_1"));

		ConditionalRecipe.builder()
				.addCondition(not(tagEmpty(ELECTRUM_DUST)))
				.addRecipe( finishedRecipeConsumer ->
						ShapelessRecipeBuilder.shapeless(MIXED_METAL_BLEND, 3)
								.unlockedBy("has_item", has(INDUSTRIAL_GRADE_METAL))
								.group(MODID + ":mixed_metal_blend")
								.requires(Ingredient.merge(Arrays.asList(Ingredient.of(IRON_DUST), Ingredient.of(LEAD_DUST))), 3)
								.requires(Ingredient.of(ELECTRUM_DUST), 3)
								.requires(Ingredient.merge(Arrays.asList(Ingredient.of(TIN_DUST), Ingredient.of(ALUMINUM_DUST))), 3)
								.save(finishedRecipeConsumer, shapelessLoc("mixed_metal_blend_2"))
				)
				.generateAdvancement()
				.build(consumer, shapelessLoc("mixed_metal_blend_2"));

		ConditionalRecipe.builder()
				.addCondition(not(tagEmpty(CONSTANTAN_DUST)))
				.addRecipe( finishedRecipeConsumer ->
						ShapelessRecipeBuilder.shapeless(MIXED_METAL_BLEND, 3)
								.unlockedBy("has_item", has(INDUSTRIAL_GRADE_METAL))
								.group(MODID + ":mixed_metal_blend")
								.requires(Ingredient.merge(Arrays.asList(Ingredient.of(IRON_DUST), Ingredient.of(LEAD_DUST))), 3)
								.requires(Ingredient.of(CONSTANTAN_DUST), 3)
								.requires(Ingredient.merge(Arrays.asList(Ingredient.of(TIN_DUST), Ingredient.of(ALUMINUM_DUST))), 3)
								.save(finishedRecipeConsumer, shapelessLoc("mixed_metal_blend_3"))
				)
				.generateAdvancement()
				.build(consumer, shapelessLoc("mixed_metal_blend_3"));

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
				.unlockedBy("has_item", tagKeyHas.apply(COAL))
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

		ShapelessRecipeBuilder.shapeless(ENERGY_CRYSTAL)
				.unlockedBy("has_item", tagKeyHas.apply(DIAMOND))
				.group(MODID + ":energy_crystal")
				.requires(REDSTONE)
				.requires(GLOWSTONE)
				.requires(REDSTONE)
				.requires(SILICON)
				.requires(DIAMOND)
				.requires(SILICON)
				.requires(REDSTONE)
				.requires(GLOWSTONE)
				.requires(REDSTONE)
				.save(consumer, shapelessLoc("energy_crystal"));

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

		ShapelessRecipeBuilder.shapeless(ELECTRONIC_CIRCUIT)
				.unlockedBy("has_item", has(INDUSTRIAL_GRADE_METAL))
				.group(MODID + ":electronic_circuit")
				.requires(LV_CABLE)
				.requires(LV_CABLE)
				.requires(LV_CABLE)
				.requires(REDSTONE)
				.requires(INDUSTRIAL_GRADE_METAL)
				.requires(REDSTONE)
				.requires(LV_CABLE)
				.requires(LV_CABLE)
				.requires(LV_CABLE)
				.save(consumer, shapelessLoc("electronic_circuit"));

		ShapelessRecipeBuilder.shapeless(ADVANCED_CIRCUIT)
				.unlockedBy("has_item", has(ELECTRONIC_CIRCUIT))
				.group(MODID + ":advanced_circuit")
				.requires(REDSTONE)
				.requires(GLOWSTONE)
				.requires(REDSTONE)
				.requires(SILICON)
				.requires(ELECTRONIC_CIRCUIT)
				.requires(SILICON)
				.requires(REDSTONE)
				.requires(GLOWSTONE)
				.requires(REDSTONE)
				.save(consumer, shapelessLoc("advanced_circuit"));

		ShapelessRecipeBuilder.shapeless(IRIDIUM_CIRCUIT, 2)
				.unlockedBy("has_item", has(ADVANCED_CIRCUIT))
				.group(MODID + ":iridium_circuit")
				.requires(ADVANCED_ALLOY)
				.requires(GRAPHENE)
				.requires(ADVANCED_ALLOY)
				.requires(ADVANCED_CIRCUIT)
				.requires(IRIDIUM_ALLOY)
				.requires(ADVANCED_CIRCUIT)
				.requires(ADVANCED_ALLOY)
				.requires(GRAPHENE)
				.requires(ADVANCED_ALLOY)
				.save(consumer, shapelessLoc("iridium_circuit"));

		ShapedRecipeBuilder.shaped(FUSE, 24)
				.unlockedBy("has_item", tagKeyHas.apply(GLASS))
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

		ShapelessRecipeBuilder.shapeless(ADVANCED_MACHINE_BLOCK)
				.unlockedBy("has_item", has(MACHINE_BLOCK))
				.group(MODID + ":advanced_machine_block")
				.requires(COPPER_COIL)
				.requires(CARBON_PLATE)
				.requires(COPPER_COIL)
				.requires(ADVANCED_ALLOY)
				.requires(MACHINE_BLOCK)
				.requires(ADVANCED_ALLOY)
				.requires(COPPER_COIL)
				.requires(CARBON_PLATE)
				.requires(COPPER_COIL)
				.save(consumer, shapelessLoc("advanced_machine_block"));

		ShapedRecipeBuilder.shaped(FLUID_CELL, 4)
				.unlockedBy("has_item", tagKeyHas.apply(TIN_INGOT))
				.group(MODID + ":fluid_cell")
				.pattern(" T ")
				.pattern("T T")
				.pattern(" T ")
				.define('T', TIN_INGOT)
				.save(consumer, shapedLoc("fluid_cell"));

		ShapedRecipeBuilder.shaped(ANTIMATTER_CRYSTAL)
				.unlockedBy("has_item", has(ANTIMATTER))
				.group(MODID + ":antimatter_crystal")
				.pattern("AAC")
				.pattern("ANA")
				.pattern("CAA")
				.define('A', ANTIMATTER)
				.define('C', ENERGY_CRYSTAL)
				.define('N', Items.NETHER_STAR)
				.save(consumer, shapedLoc("antimatter_crystal"));

		ShapedRecipeBuilder.shaped(EMPTY_CAN, 10)
				.unlockedBy("has_item", tagKeyHas.apply(TIN_INGOT))
				.group(MODID + ":empty_can")
				.pattern("T T")
				.pattern("TTT")
				.define('T', TIN_INGOT)
				.save(consumer, shapedLoc("empty_can"));

		ShapedRecipeBuilder.shaped(IRIDIUM_ALLOY)
				.unlockedBy("has_item", tagKeyHas.apply(IRIDIUM_INGOT))
				.group(MODID + ":iridium_alloy")
				.pattern("IAI")
				.pattern("ADA")
				.pattern("IAI")
				.define('I', IRIDIUM_INGOT)
				.define('A', ADVANCED_ALLOY)
				.define('D', DIAMOND)
				.save(consumer, shapedLoc("iridium_alloy"));

		ShapedRecipeBuilder.shaped(CARBON_FIBERS)
				.unlockedBy("has_item", tagKeyHas.apply(COAL_DUST))
				.group(MODID + ":carbon_fibers")
				.pattern("DD")
				.pattern("DD")
				.define('D', COAL_DUST)
				.save(consumer, shapedLoc("carbon_fibers"));

		ShapelessRecipeBuilder.shapeless(CARBON_FIBER_MESH)
				.unlockedBy("has_item", has(CARBON_FIBERS))
				.group(MODID + ":carbon_fiber_mesh")
				.requires(CARBON_FIBERS, 4)
				.save(consumer, shapelessLoc("carbon_fiber_mesh"));

		ShapedRecipeBuilder.shaped(COPPER_COIL)
				.unlockedBy("has_item", has(COPPER_WIRE))
				.group(MODID + ":copper_coil")
				.pattern("WWW")
				.pattern("WRW")
				.pattern("WWW")
				.define('W', COPPER_WIRE)
				.define('R', IRON_ROD)
				.save(consumer, shapedLoc("copper_coil"));

		ShapelessRecipeBuilder.shapeless(LANDMARK)
				.unlockedBy("has_item", has(Items.REDSTONE_TORCH))
				.group(MODID + ":landmark")
				.requires(Items.REDSTONE_TORCH)
				.requires(LAPIS)
				.save(consumer, shapelessLoc("landmark"));

		MachineRecipeBuilder.separating()
				.unlockedBy("has_item", has(LATEX))
				.inputItem(Ingredient.of(LATEX))
				.outputItem(new ItemStack(RUBBER, 3))
				.save(consumer, separatingLoc("rubber_from_latex"));

		MachineRecipeBuilder.compressing()
				.unlockedBy("has_item", tagKeyHas.apply(IRON_INGOT))
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

		MachineRecipeBuilder.compressing()
				.unlockedBy("has_item", tagKeyHas.apply(COPPER_PLATE))
				.inputItem(Ingredient.of(COPPER_PLATE), 8)
				.outputItem(new ItemStack(DENSE_COPPER_PLATE))
				.save(consumer, compressingLoc("dense_copper_plate"));

		AntimatterBoostRecipeBuilder.make(Ingredient.of(SCRAP), 5000)
				.unlockedBy("has_item", has(SCRAP))
				.save(consumer, antimatterBoostLoc("scrap"));

		AntimatterBoostRecipeBuilder.make(Ingredient.of(SCRAP_BOX), 45000)
				.unlockedBy("has_item", has(SCRAP_BOX))
				.save(consumer, antimatterBoostLoc("scrap_box"));
	}
}
