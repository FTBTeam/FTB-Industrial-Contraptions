package dev.ftb.mods.ftbic.datagen.recipes;

import dev.ftb.mods.ftbic.datagen.FTBICRecipesGen;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.world.ResourceElements;
import dev.ftb.mods.ftbic.world.ResourceType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;
import java.util.function.Function;

public class FTBICMachineRecipes extends FTBICRecipesGen {
	public FTBICMachineRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		Function<TagKey<Item>, InventoryChangeTrigger.TriggerInstance> tagKeyHas = (e) -> RecipeProvider.inventoryTrigger(ItemPredicate.Builder.item().of(e).build());

		ShapedRecipeBuilder.shaped(IRON_FURNACE.get())
				.unlockedBy("has_item", tagKeyHas.apply(IRON_INGOT))
				.group(MODID + ":iron_furnace")
				.pattern(" I ")
				.pattern("I I")
				.pattern("IFI")
				.define('I', IRON_INGOT)
				.define('F', Items.FURNACE)
				.save(consumer, shapedLoc("iron_furnace"));

		ShapedRecipeBuilder.shaped(POWERED_FURNACE.get())
				.unlockedBy("has_item", has(IRON_FURNACE.get()))
				.group(MODID + ":electric_furnace")
				.pattern(" C ")
				.pattern("RFR")
				.define('C', ELECTRONIC_CIRCUIT.get())
				.define('R', REDSTONE)
				.define('F', IRON_FURNACE.get())
				.save(consumer, shapedLoc("electric_furnace"));

		ShapedRecipeBuilder.shaped(MACERATOR.get())
				.unlockedBy("has_item", has(MACHINE_BLOCK.get()))
				.group(MODID + ":macerator")
				.pattern("FFF")
				.pattern("SMS")
				.pattern(" C ")
				.define('F', FLINT)
				.define('S', COBBLESTONE)
				.define('M', MACHINE_BLOCK.get())
				.define('C', ELECTRONIC_CIRCUIT.get())
				.save(consumer, shapedLoc("macerator"));

		ShapedRecipeBuilder.shaped(CENTRIFUGE.get())
				.unlockedBy("has_item", has(MACHINE_BLOCK.get()))
				.group(MODID + ":extractor")
				.pattern("TMT")
				.pattern("TCT")
				.define('T', TREE_TAP.get())
				.define('M', MACHINE_BLOCK.get())
				.define('C', ELECTRONIC_CIRCUIT.get())
				.save(consumer, shapedLoc("extractor"));

		ShapedRecipeBuilder.shaped(COMPRESSOR.get())
				.unlockedBy("has_item", has(MACHINE_BLOCK.get()))
				.group(MODID + ":compressor")
				.pattern("S S")
				.pattern("SMS")
				.pattern("SCS")
				.define('S', STONE)
				.define('M', MACHINE_BLOCK.get())
				.define('C', ELECTRONIC_CIRCUIT.get())
				.save(consumer, shapedLoc("compressor"));

		ShapedRecipeBuilder.shaped(REPROCESSOR.get())
				.unlockedBy("has_item", has(COMPRESSOR.get()))
				.group(MODID + ":recycler")
				.pattern("MDM")
				.pattern("MCM")
				.define('D', Items.COMPOSTER)
				.define('C', COMPRESSOR.get())
				.define('M', INDUSTRIAL_GRADE_METAL.get())
				.save(consumer, shapedLoc("recycler"));

		ShapedRecipeBuilder.shaped(CANNING_MACHINE.get())
				.unlockedBy("has_item", has(MACHINE_BLOCK.get()))
				.group(MODID + ":canning_machine")
				.pattern("TCT")
				.pattern("TMT")
				.pattern("TTT")
				.define('T', TIN_INGOT)
				.define('M', MACHINE_BLOCK.get())
				.define('C', ELECTRONIC_CIRCUIT.get())
				.save(consumer, shapedLoc("canning_machine"));

		ShapedRecipeBuilder.shaped(ROLLER.get())
				.unlockedBy("has_item", has(MACHINE_BLOCK.get()))
				.group(MODID + ":roller")
				.pattern("HCH")
				.pattern("PMP")
				.define('H', FTBICItems.getResourceFromType(ResourceElements.ALUMINUM, ResourceType.GEAR).orElseThrow().get())
				.define('P', PISTON)
				.define('M', MACHINE_BLOCK.get())
				.define('C', ELECTRONIC_CIRCUIT.get())
				.save(consumer, shapedLoc("roller"));

		ShapedRecipeBuilder.shaped(EXTRUDER.get())
				.unlockedBy("has_item", has(MACHINE_BLOCK.get()))
				.group(MODID + ":extruder")
				.pattern("SCS")
				.pattern("SMS")
				.define('S', IRON_ROD)
				.define('M', MACHINE_BLOCK.get())
				.define('C', ELECTRONIC_CIRCUIT.get())
				.save(consumer, shapedLoc("extruder"));

		ShapedRecipeBuilder.shaped(ANTIMATTER_FABRICATOR.get())
				.unlockedBy("has_item", has(ADVANCED_MACHINE_BLOCK.get()))
				.group(MODID + ":antimatter_fabricator")
				.pattern("GCG")
				.pattern("MEM")
				.pattern("GCG")
				.define('G', GLOWSTONE)
				.define('M', ADVANCED_MACHINE_BLOCK.get())
				.define('C', IRIDIUM_CIRCUIT.get())
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

		ShapedRecipeBuilder.shaped(CHARGE_PAD.get())
				.unlockedBy("has_item", has(COPPER_COIL.get()))
				.group(MODID + ":charge_pad")
				.pattern("WWW")
				.pattern("CMC")
				.define('M', MACHINE_BLOCK.get())
				.define('C', ADVANCED_CIRCUIT.get())
				.define('W', COPPER_COIL.get())
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

		ShapedRecipeBuilder.shaped(QUARRY.get())
				.unlockedBy("has_item", has(ADVANCED_MACHINE_BLOCK.get()))
				.group(MODID + ":quarry")
				.pattern("CMC")
				.pattern("ADA")
				.define('M', ADVANCED_MACHINE_BLOCK.get())
				.define('C', ADVANCED_CIRCUIT.get())
				.define('D', Items.DIAMOND_PICKAXE) // Replace with Diamond Drill
				.define('A', ADVANCED_ALLOY.get())
				.save(consumer, shapedLoc("quarry"));

		ShapedRecipeBuilder.shaped(PUMP.get())
				.unlockedBy("has_item", has(ADVANCED_MACHINE_BLOCK.get()))
				.group(MODID + ":pump")
				.pattern("FFF")
				.pattern("CMC")
				.pattern("ADA")
				.define('M', ADVANCED_MACHINE_BLOCK.get())
				.define('C', ADVANCED_CIRCUIT.get())
				.define('D', Items.BUCKET)
				.define('F', FLUID_CELL.get())
				.define('A', ADVANCED_ALLOY.get())
				.save(consumer, shapedLoc("pump"));
	}
}
