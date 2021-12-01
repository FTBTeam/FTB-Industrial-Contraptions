package dev.ftb.mods.ftbic.datagen;

import com.ridanisaurus.emendatusenigmatica.registries.ItemHandler;
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

		ShapedRecipeBuilder.shaped(POWERED_FURNACE)
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

		ShapedRecipeBuilder.shaped(CENTRIFUGE)
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

		ShapedRecipeBuilder.shaped(REPROCESSOR)
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

		ShapedRecipeBuilder.shaped(ROLLER)
				.unlockedBy("has_item", has(MACHINE_BLOCK))
				.group(MODID + ":roller")
				.pattern("HCH")
				.pattern("PMP")
				.define('H', ItemHandler.ENIGMATIC_HAMMER.get())
				.define('P', PISTON)
				.define('M', MACHINE_BLOCK)
				.define('C', ELECTRONIC_CIRCUIT)
				.save(consumer, shapedLoc("roller"));

		ShapedRecipeBuilder.shaped(EXTRUDER)
				.unlockedBy("has_item", has(MACHINE_BLOCK))
				.group(MODID + ":extruder")
				.pattern("SCS")
				.pattern("SMS")
				.define('S', IRON_ROD)
				.define('M', MACHINE_BLOCK)
				.define('C', ELECTRONIC_CIRCUIT)
				.save(consumer, shapedLoc("extruder"));

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

		/*
		ShapedRecipeBuilder.shaped(ADVANCED_POWERED_FURNACE)
				.unlockedBy("has_item", has(COPPER_COIL))
				.group(MODID + ":advanced_powered_furnace")
				.pattern("CCC")
				.pattern("CFC")
				.pattern("WMW")
				.define('F', POWERED_FURNACE)
				.define('M', ADVANCED_MACHINE_BLOCK)
				.define('C', COPPER_INGOT)
				.define('W', COPPER_COIL)
				.save(consumer, shapedLoc("advanced_powered_furnace"));

		ShapedRecipeBuilder.shaped(ADVANCED_MACERATOR)
				.unlockedBy("has_item", has(COPPER_COIL))
				.group(MODID + ":advanced_macerator")
				.pattern("CCC")
				.pattern("CFC")
				.pattern("WMW")
				.define('F', MACERATOR)
				.define('M', ADVANCED_MACHINE_BLOCK)
				.define('C', INDUSTRIAL_GRADE_METAL)
				.define('W', COPPER_COIL)
				.save(consumer, shapedLoc("advanced_macerator"));

		ShapedRecipeBuilder.shaped(ADVANCED_CENTRIFUGE)
				.unlockedBy("has_item", has(COPPER_COIL))
				.group(MODID + ":advanced_centrifuge")
				.pattern("CCC")
				.pattern("CFC")
				.pattern("WMW")
				.define('F', CENTRIFUGE)
				.define('M', ADVANCED_MACHINE_BLOCK)
				.define('C', FLUID_CELL)
				.define('W', COPPER_COIL)
				.save(consumer, shapedLoc("advanced_centrifuge"));

		ShapedRecipeBuilder.shaped(ADVANCED_COMPRESSOR)
				.unlockedBy("has_item", has(COPPER_COIL))
				.group(MODID + ":advanced_compressor")
				.pattern("CCC")
				.pattern("CFC")
				.pattern("WMW")
				.define('F', COMPRESSOR)
				.define('M', ADVANCED_MACHINE_BLOCK)
				.define('C', OBSIDIAN)
				.define('W', COPPER_COIL)
				.save(consumer, shapedLoc("advanced_compressor"));
		*/

		ShapedRecipeBuilder.shaped(CHARGE_PAD)
				.unlockedBy("has_item", has(COPPER_COIL))
				.group(MODID + ":charge_pad")
				.pattern("WWW")
				.pattern("CMC")
				.define('M', MACHINE_BLOCK)
				.define('C', ADVANCED_CIRCUIT)
				.define('W', COPPER_COIL)
				.save(consumer, shapedLoc("charge_pad"));

		/*
		ShapedRecipeBuilder.shaped(TELEPORTER)
				.unlockedBy("has_item", has(ANTIMATTER_CRYSTAL))
				.group(MODID + ":teleporter")
				.pattern("CEC")
				.pattern("CSC")
				.pattern("WMW")
				.define('E', ENDER_PEARL)
				.define('S', ANTIMATTER_CRYSTAL)
				.define('M', ADVANCED_MACHINE_BLOCK)
				.define('C', IRIDIUM_CIRCUIT)
				.define('W', COPPER_COIL)
				.save(consumer, shapedLoc("teleporter"));
		 */

		/*
		ShapedRecipeBuilder.shaped(POWERED_CRAFTING_TABLE)
				.unlockedBy("has_item", has(MACHINE_BLOCK))
				.group(MODID + ":powered_crafting_table")
				.pattern(" T ")
				.pattern("CMC")
				.define('T', Items.CRAFTING_TABLE)
				.define('M', MACHINE_BLOCK)
				.define('C', ELECTRONIC_CIRCUIT)
				.save(consumer, shapedLoc("powered_crafting_table"));
		 */

		ShapedRecipeBuilder.shaped(QUARRY)
				.unlockedBy("has_item", has(ADVANCED_MACHINE_BLOCK))
				.group(MODID + ":quarry")
				.pattern("CMC")
				.pattern("ADA")
				.define('M', ADVANCED_MACHINE_BLOCK)
				.define('C', ADVANCED_CIRCUIT)
				.define('D', Items.DIAMOND_PICKAXE) // Replace with Diamond Drill
				.define('A', ADVANCED_ALLOY)
				.save(consumer, shapedLoc("quarry"));
	}
}
