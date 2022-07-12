package dev.ftb.mods.ftbic.datagen.recipes;

import dev.ftb.mods.ftbic.datagen.FTBICRecipesGen;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
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

		SimpleCookingRecipeBuilder.cooking(Ingredient.of(IRON_INGOT), INDUSTRIAL_GRADE_METAL.get(), 0F, 400, RecipeSerializer.SMELTING_RECIPE)
				.unlockedBy("has_item", tagKeyHas.apply(IRON_INGOT))
				.save(consumer, smeltingLoc("industrial_grade_metal"));

		SimpleCookingRecipeBuilder.cooking(Ingredient.of(IRON_INGOT), INDUSTRIAL_GRADE_METAL.get(), 0F, 200, RecipeSerializer.BLASTING_RECIPE)
				.unlockedBy("has_item", tagKeyHas.apply(IRON_INGOT))
				.save(consumer, blastingLoc("industrial_grade_metal"));

		SimpleCookingRecipeBuilder.cooking(Ingredient.of(SLIMEBALL), RUBBER.get(), 0F, 300, RecipeSerializer.SMELTING_RECIPE)
				.unlockedBy("has_item", tagKeyHas.apply(SLIMEBALL))
				.save(consumer, smeltingLoc("rubber"));

		SimpleCookingRecipeBuilder.cooking(Ingredient.of(SLIMEBALL), RUBBER.get(), 0F, 800, RecipeSerializer.CAMPFIRE_COOKING_RECIPE)
				.unlockedBy("has_item", tagKeyHas.apply(SLIMEBALL))
				.save(consumer, campfireCookingLoc("rubber"));

		SimpleCookingRecipeBuilder.cooking(Ingredient.of(SLIMEBALL), RUBBER.get(), 0F, 150, RecipeSerializer.SMOKING_RECIPE)
				.unlockedBy("has_item", tagKeyHas.apply(SLIMEBALL))
				.save(consumer, smokingLoc("rubber"));

		SimpleCookingRecipeBuilder.cooking(Ingredient.of(MIXED_METAL_BLEND.get()), ADVANCED_ALLOY.get(), 0F, 400, RecipeSerializer.SMELTING_RECIPE)
				.unlockedBy("has_item", has(MIXED_METAL_BLEND.get()))
				.save(consumer, smeltingLoc("advanced_alloy"));

		SimpleCookingRecipeBuilder.cooking(Ingredient.of(MIXED_METAL_BLEND.get()), ADVANCED_ALLOY.get(), 0F, 200, RecipeSerializer.BLASTING_RECIPE)
				.unlockedBy("has_item", has(MIXED_METAL_BLEND.get()))
				.save(consumer, blastingLoc("advanced_alloy"));

		ConditionalRecipe.builder()
				.addCondition(not(tagEmpty(BRONZE_DUST)))
				.addRecipe( finishedRecipeConsumer ->
						ShapedRecipeBuilder.shaped(MIXED_METAL_BLEND.get(), 3)
								.unlockedBy("has_item", has(INDUSTRIAL_GRADE_METAL.get()))
								.group(MODID + ":mixed_metal_blend")
								.pattern("III")
								.pattern("OOO")
								.pattern("PPP")
								.define('I', Ingredient.merge(Arrays.asList(Ingredient.of(IRON_DUST), Ingredient.of(LEAD_DUST))))
								.define('O', BRONZE_DUST)
								.define('P', Ingredient.merge(Arrays.asList(Ingredient.of(TIN_DUST), Ingredient.of(ALUMINUM_DUST))))
								.save(finishedRecipeConsumer, shapedLoc("mixed_metal_blend_1"))
				)
				.generateAdvancement()
				.build(consumer, shapedLoc("mixed_metal_blend_1"));

		ConditionalRecipe.builder()
				.addCondition(not(tagEmpty(ELECTRUM_DUST)))
				.addRecipe( finishedRecipeConsumer ->
						ShapedRecipeBuilder.shaped(MIXED_METAL_BLEND.get(), 3)
								.unlockedBy("has_item", has(INDUSTRIAL_GRADE_METAL.get()))
								.group(MODID + ":mixed_metal_blend")
								.pattern("III")
								.pattern("OOO")
								.pattern("PPP")
								.define('I', Ingredient.merge(Arrays.asList(Ingredient.of(IRON_DUST), Ingredient.of(LEAD_DUST))))
								.define('O', Ingredient.of(ELECTRUM_DUST))
								.define('P', Ingredient.merge(Arrays.asList(Ingredient.of(TIN_DUST), Ingredient.of(ALUMINUM_DUST))))
								.save(finishedRecipeConsumer, shapedLoc("mixed_metal_blend_2"))
				)
				.generateAdvancement()
				.build(consumer, shapedLoc("mixed_metal_blend_2"));

		ConditionalRecipe.builder()
				.addCondition(not(tagEmpty(CONSTANTAN_DUST)))
				.addRecipe( finishedRecipeConsumer ->
						ShapedRecipeBuilder.shaped(MIXED_METAL_BLEND.get(), 3)
								.unlockedBy("has_item", has(INDUSTRIAL_GRADE_METAL.get()))
								.group(MODID + ":mixed_metal_blend")
								.pattern("III")
								.pattern("OOO")
								.pattern("PPP")
								.define('I', Ingredient.merge(Arrays.asList(Ingredient.of(IRON_DUST), Ingredient.of(LEAD_DUST))))
								.define('O', Ingredient.of(CONSTANTAN_DUST))
								.define('P', Ingredient.merge(Arrays.asList(Ingredient.of(TIN_DUST), Ingredient.of(ALUMINUM_DUST))))
								.save(finishedRecipeConsumer, shapedLoc("mixed_metal_blend_3"))
				)
				.generateAdvancement()
				.build(consumer, shapedLoc("mixed_metal_blend_3"));

		ShapedRecipeBuilder.shaped(RUBBER_SHEET.get())
				.unlockedBy("has_item", has(RUBBER.get()))
				.group(MODID + ":rubber_sheet")
				.pattern("III")
				.define('I', RUBBER.get())
				.save(consumer, shapedLoc("rubber_sheet"));

		ShapedRecipeBuilder.shaped(SCRAP_BOX.get())
				.unlockedBy("has_item", has(SCRAP.get()))
				.group(MODID + ":scrap_box")
				.pattern("SSS")
				.pattern("SSS")
				.pattern("SSS")
				.define('S', SCRAP.get())
				.save(consumer, shapedLoc("scrap_box"));

		ShapedRecipeBuilder.shaped(COAL_BALL.get())
				.unlockedBy("has_item", tagKeyHas.apply(COAL))
				.group(MODID + ":coal_ball")
				.pattern("CCC")
				.pattern("CFC")
				.pattern("CCC")
				.define('C', COAL)
				.define('F', FLINT)
				.save(consumer, shapedLoc("coal_ball"));

		ShapedRecipeBuilder.shaped(GRAPHENE.get())
				.unlockedBy("has_item", has(COMPRESSED_COAL_BALL.get()))
				.group(MODID + ":graphene")
				.pattern("CCC")
				.pattern("COC")
				.pattern("CCC")
				.define('C', COMPRESSED_COAL_BALL.get())
				.define('O', OBSIDIAN)
				.save(consumer, shapedLoc("graphene"));

		ShapelessRecipeBuilder.shapeless(ENERGY_CRYSTAL.get())
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

		ShapelessRecipeBuilder.shapeless(RUBBER.get(), 3)
				.unlockedBy("has_item", has(RUBBER.get()))
				.group(MODID + ":rubber")
				.requires(RUBBER_SHEET.get())
				.save(consumer, shapelessLoc("rubber"));

		ShapelessRecipeBuilder.shapeless(SCRAP.get(), 9)
				.unlockedBy("has_item", has(SCRAP_BOX.get()))
				.group(MODID + ":scrap")
				.requires(SCRAP_BOX.get())
				.save(consumer, shapelessLoc("scrap"));

		ShapelessRecipeBuilder.shapeless(ELECTRONIC_CIRCUIT.get())
				.unlockedBy("has_item", has(INDUSTRIAL_GRADE_METAL.get()))
				.group(MODID + ":electronic_circuit")
				.requires(LV_CABLE.get())
				.requires(LV_CABLE.get())
				.requires(LV_CABLE.get())
				.requires(REDSTONE)
				.requires(INDUSTRIAL_GRADE_METAL.get())
				.requires(REDSTONE)
				.requires(LV_CABLE.get())
				.requires(LV_CABLE.get())
				.requires(LV_CABLE.get())
				.save(consumer, shapelessLoc("electronic_circuit"));

		ShapelessRecipeBuilder.shapeless(ADVANCED_CIRCUIT.get())
				.unlockedBy("has_item", has(ELECTRONIC_CIRCUIT.get()))
				.group(MODID + ":advanced_circuit")
				.requires(REDSTONE)
				.requires(GLOWSTONE)
				.requires(REDSTONE)
				.requires(SILICON)
				.requires(ELECTRONIC_CIRCUIT.get())
				.requires(SILICON)
				.requires(REDSTONE)
				.requires(GLOWSTONE)
				.requires(REDSTONE)
				.save(consumer, shapelessLoc("advanced_circuit"));

		ShapelessRecipeBuilder.shapeless(IRIDIUM_CIRCUIT.get(), 2)
				.unlockedBy("has_item", has(ADVANCED_CIRCUIT.get()))
				.group(MODID + ":iridium_circuit")
				.requires(ADVANCED_ALLOY.get())
				.requires(GRAPHENE.get())
				.requires(ADVANCED_ALLOY.get())
				.requires(ADVANCED_CIRCUIT.get())
				.requires(IRIDIUM_ALLOY.get())
				.requires(ADVANCED_CIRCUIT.get())
				.requires(ADVANCED_ALLOY.get())
				.requires(GRAPHENE.get())
				.requires(ADVANCED_ALLOY.get())
				.save(consumer, shapelessLoc("iridium_circuit"));

		ShapedRecipeBuilder.shaped(FUSE.get(), 24)
				.unlockedBy("has_item", tagKeyHas.apply(GLASS))
				.group(MODID + ":fuse")
				.pattern("GGG")
				.pattern("MMM")
				.pattern("GGG")
				.define('G', GLASS)
				.define('M', INDUSTRIAL_GRADE_METAL.get())
				.save(consumer, shapedLoc("fuse"));

		ShapedRecipeBuilder.shaped(MACHINE_BLOCK.get())
				.unlockedBy("has_item", has(INDUSTRIAL_GRADE_METAL.get()))
				.group(MODID + ":machine_block")
				.pattern("MMM")
				.pattern("MFM")
				.pattern("MMM")
				.define('M', INDUSTRIAL_GRADE_METAL.get())
				.define('F', FUSE.get())
				.save(consumer, shapedLoc("machine_block"));

		ShapelessRecipeBuilder.shapeless(ADVANCED_MACHINE_BLOCK.get())
				.unlockedBy("has_item", has(MACHINE_BLOCK.get()))
				.group(MODID + ":advanced_machine_block")
				.requires(COPPER_COIL.get())
				.requires(CARBON_PLATE.get())
				.requires(COPPER_COIL.get())
				.requires(ADVANCED_ALLOY.get())
				.requires(MACHINE_BLOCK.get())
				.requires(ADVANCED_ALLOY.get())
				.requires(COPPER_COIL.get())
				.requires(CARBON_PLATE.get())
				.requires(COPPER_COIL.get())
				.save(consumer, shapelessLoc("advanced_machine_block"));

		ShapedRecipeBuilder.shaped(FLUID_CELL.get(), 4)
				.unlockedBy("has_item", tagKeyHas.apply(TIN_INGOT))
				.group(MODID + ":fluid_cell")
				.pattern(" T ")
				.pattern("T T")
				.pattern(" T ")
				.define('T', TIN_INGOT)
				.save(consumer, shapedLoc("fluid_cell"));

		ShapedRecipeBuilder.shaped(ANTIMATTER_CRYSTAL.get())
				.unlockedBy("has_item", has(ANTIMATTER.get()))
				.group(MODID + ":antimatter_crystal")
				.pattern("AAC")
				.pattern("ANA")
				.pattern("CAA")
				.define('A', ANTIMATTER.get())
				.define('C', ENERGY_CRYSTAL.get())
				.define('N', Items.NETHER_STAR)
				.save(consumer, shapedLoc("antimatter_crystal"));

		ShapedRecipeBuilder.shaped(EMPTY_CAN.get(), 10)
				.unlockedBy("has_item", tagKeyHas.apply(TIN_INGOT))
				.group(MODID + ":empty_can")
				.pattern("T T")
				.pattern("TTT")
				.define('T', TIN_INGOT)
				.save(consumer, shapedLoc("empty_can"));

		ShapedRecipeBuilder.shaped(IRIDIUM_ALLOY.get())
				.unlockedBy("has_item", tagKeyHas.apply(IRIDIUM_INGOT))
				.group(MODID + ":iridium_alloy")
				.pattern("IAI")
				.pattern("ADA")
				.pattern("IAI")
				.define('I', IRIDIUM_INGOT)
				.define('A', ADVANCED_ALLOY.get())
				.define('D', DIAMOND)
				.save(consumer, shapedLoc("iridium_alloy"));

		ShapedRecipeBuilder.shaped(CARBON_FIBERS.get())
				.unlockedBy("has_item", tagKeyHas.apply(COAL_DUST))
				.group(MODID + ":carbon_fibers")
				.pattern("DD")
				.pattern("DD")
				.define('D', COAL_DUST)
				.save(consumer, shapedLoc("carbon_fibers"));

		ShapelessRecipeBuilder.shapeless(CARBON_FIBER_MESH.get())
				.unlockedBy("has_item", has(CARBON_FIBERS.get()))
				.group(MODID + ":carbon_fiber_mesh")
				.requires(CARBON_FIBERS.get(), 4)
				.save(consumer, shapelessLoc("carbon_fiber_mesh"));

		ShapedRecipeBuilder.shaped(COPPER_COIL.get())
				.unlockedBy("has_item", has(COPPER_WIRE.get()))
				.group(MODID + ":copper_coil")
				.pattern("WWW")
				.pattern("WRW")
				.pattern("WWW")
				.define('W', COPPER_WIRE.get())
				.define('R', IRON_ROD)
				.save(consumer, shapedLoc("copper_coil"));

		ShapelessRecipeBuilder.shapeless(LANDMARK.get())
				.unlockedBy("has_item", has(Items.REDSTONE_TORCH))
				.group(MODID + ":landmark")
				.requires(Items.REDSTONE_TORCH)
				.requires(LAPIS)
				.save(consumer, shapelessLoc("landmark"));

		MachineRecipeBuilder.separating()
				.unlockedBy("has_item", has(LATEX.get()))
				.inputItem(Ingredient.of(LATEX.get()))
				.outputItem(new ItemStack(RUBBER.get(), 3))
				.save(consumer, separatingLoc("rubber_from_latex"));

		MachineRecipeBuilder.compressing()
				.unlockedBy("has_item", tagKeyHas.apply(IRON_INGOT))
				.inputItem(Ingredient.of(IRON_INGOT), 3)
				.outputItem(new ItemStack(INDUSTRIAL_GRADE_METAL.get(), 3))
				.save(consumer, compressingLoc("industrial_grade_metal"));

		MachineRecipeBuilder.compressing()
				.unlockedBy("has_item", has(CARBON_FIBER_MESH.get()))
				.inputItem(Ingredient.of(CARBON_FIBER_MESH.get()))
				.outputItem(new ItemStack(CARBON_PLATE.get()))
				.save(consumer, compressingLoc("carbon_plate"));

		MachineRecipeBuilder.compressing()
				.unlockedBy("has_item", has(COAL_BALL.get()))
				.inputItem(Ingredient.of(COAL_BALL.get()))
				.outputItem(new ItemStack(COMPRESSED_COAL_BALL.get()))
				.save(consumer, compressingLoc("compressed_coal_ball"));

		MachineRecipeBuilder.compressing()
				.unlockedBy("has_item", tagKeyHas.apply(COPPER_PLATE))
				.inputItem(Ingredient.of(COPPER_PLATE), 8)
				.outputItem(new ItemStack(DENSE_COPPER_PLATE.get()))
				.save(consumer, compressingLoc("dense_copper_plate"));

		AntimatterBoostRecipeBuilder.make(Ingredient.of(SCRAP.get()), 5000)
				.unlockedBy("has_item", has(SCRAP.get()))
				.save(consumer, antimatterBoostLoc("scrap"));

		AntimatterBoostRecipeBuilder.make(Ingredient.of(SCRAP_BOX.get()), 45000)
				.unlockedBy("has_item", has(SCRAP_BOX.get()))
				.save(consumer, antimatterBoostLoc("scrap_box"));
	}
}
