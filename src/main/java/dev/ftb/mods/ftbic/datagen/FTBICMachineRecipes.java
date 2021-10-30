package dev.ftb.mods.ftbic.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class FTBICMachineRecipes extends FTBICRecipesGen {
	public FTBICMachineRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(IRON_FURNACE)
				.unlockedBy("has_item", has(IRON_INGOT))
				.group(MODID + ":iron_furnace")
				.pattern(" I ")
				.pattern("I I")
				.pattern("IFI")
				.define('I', IRON_INGOT)
				.define('F', Items.FURNACE)
				.save(consumer, shapedLoc("iron_furnace"));

		ShapedRecipeBuilder.shaped(ELECTRIC_FURNACE)
				.unlockedBy("has_item", has(IRON_FURNACE))
				.group(MODID + ":electric_furnace")
				.pattern(" C ")
				.pattern("RFR")
				.define('C', ELECTRONIC_CIRCUIT)
				.define('R', REDSTONE)
				.define('F', IRON_FURNACE)
				.save(consumer, shapedLoc("electric_furnace"));

		ShapedRecipeBuilder.shaped(MACERATOR)
				.unlockedBy("has_item", has(MACHINE_BLOCK))
				.group(MODID + ":macerator")
				.pattern("FFF")
				.pattern("SMS")
				.pattern(" C ")
				.define('F', FLINT)
				.define('S', COBBLESTONE)
				.define('M', MACHINE_BLOCK)
				.define('C', ELECTRONIC_CIRCUIT)
				.save(consumer, shapedLoc("macerator"));

		ShapedRecipeBuilder.shaped(EXTRACTOR)
				.unlockedBy("has_item", has(MACHINE_BLOCK))
				.group(MODID + ":extractor")
				.pattern("TMT")
				.pattern("TCT")
				.define('T', TREE_TAP)
				.define('M', MACHINE_BLOCK)
				.define('C', ELECTRONIC_CIRCUIT)
				.save(consumer, shapedLoc("extractor"));

		ShapedRecipeBuilder.shaped(COMPRESSOR)
				.unlockedBy("has_item", has(MACHINE_BLOCK))
				.group(MODID + ":compressor")
				.pattern("S S")
				.pattern("SMS")
				.pattern("SCS")
				.define('S', STONE)
				.define('M', MACHINE_BLOCK)
				.define('C', ELECTRONIC_CIRCUIT)
				.save(consumer, shapedLoc("compressor"));

		ShapedRecipeBuilder.shaped(ELECTROLYZER)
				.unlockedBy("has_item", has(MACHINE_BLOCK))
				.group(MODID + ":electrolyzer")
				.pattern("W W")
				.pattern("WCW")
				.pattern("EME")
				.define('W', COPPER_CABLE)
				.define('E', EMPTY_CELL)
				.define('M', MACHINE_BLOCK)
				.define('C', ELECTRONIC_CIRCUIT)
				.save(consumer, shapedLoc("electrolyzer"));

		ShapedRecipeBuilder.shaped(RECYCLER)
				.unlockedBy("has_item", has(COMPRESSOR))
				.group(MODID + ":recycler")
				.pattern("MDM")
				.pattern("MCM")
				.define('D', Items.COMPOSTER)
				.define('C', COMPRESSOR)
				.define('M', INDUSTRIAL_GRADE_METAL)
				.save(consumer, shapedLoc("recycler"));

		ShapedRecipeBuilder.shaped(CANNING_MACHINE)
				.unlockedBy("has_item", has(MACHINE_BLOCK))
				.group(MODID + ":canning_machine")
				.pattern("TCT")
				.pattern("TMT")
				.pattern("TTT")
				.define('T', TIN_INGOT)
				.define('M', MACHINE_BLOCK)
				.define('C', ELECTRONIC_CIRCUIT)
				.save(consumer, shapedLoc("canning_machine"));

		ShapedRecipeBuilder.shaped(INDUCTION_FURNACE)
				.unlockedBy("has_item", has(ADVANCED_MACHINE_BLOCK))
				.group(MODID + ":induction_furnace")
				.pattern("CCC")
				.pattern("CFC")
				.pattern("CMC")
				.define('F', ELECTRIC_FURNACE)
				.define('M', ADVANCED_MACHINE_BLOCK)
				.define('C', COPPER_INGOT)
				.save(consumer, shapedLoc("induction_furnace"));

		ShapedRecipeBuilder.shaped(ROTARY_MACERATOR)
				.unlockedBy("has_item", has(ADVANCED_MACHINE_BLOCK))
				.group(MODID + ":rotary_macerator")
				.pattern("CCC")
				.pattern("CFC")
				.pattern("CMC")
				.define('F', MACERATOR)
				.define('M', ADVANCED_MACHINE_BLOCK)
				.define('C', INDUSTRIAL_GRADE_METAL)
				.save(consumer, shapedLoc("rotary_macerator"));

		ShapedRecipeBuilder.shaped(VACUUM_EXTRACTOR)
				.unlockedBy("has_item", has(ADVANCED_MACHINE_BLOCK))
				.group(MODID + ":vacuum_extractor")
				.pattern("CCC")
				.pattern("CFC")
				.pattern("CMC")
				.define('F', EXTRACTOR)
				.define('M', ADVANCED_MACHINE_BLOCK)
				.define('C', ALUMINUM_INGOT)
				.save(consumer, shapedLoc("vacuum_extractor"));

		ShapedRecipeBuilder.shaped(SINGULARITY_COMPRESSOR)
				.unlockedBy("has_item", has(ADVANCED_MACHINE_BLOCK))
				.group(MODID + ":singularity_compressor")
				.pattern("CCC")
				.pattern("CFC")
				.pattern("CMC")
				.define('F', COMPRESSOR)
				.define('M', ADVANCED_MACHINE_BLOCK)
				.define('C', OBSIDIAN)
				.save(consumer, shapedLoc("singularity_compressor"));

		ShapedRecipeBuilder.shaped(ANTIMATTER_FABRICATOR)
				.unlockedBy("has_item", has(ADVANCED_MACHINE_BLOCK))
				.group(MODID + ":antimatter_fabricator")
				.pattern("GCG")
				.pattern("MEM")
				.pattern("GCG")
				.define('G', GLOWSTONE)
				.define('M', ADVANCED_MACHINE_BLOCK)
				.define('C', IRIDIUM_CIRCUIT)
				.define('E', Items.NETHER_STAR)
				.save(consumer, shapedLoc("antimatter_fabricator"));
	}
}
