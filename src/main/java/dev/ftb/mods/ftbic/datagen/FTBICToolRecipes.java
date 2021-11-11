package dev.ftb.mods.ftbic.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class FTBICToolRecipes extends FTBICRecipesGen {
	public FTBICToolRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		MachineRecipeBuilder.canning()
				.unlockedBy("has_item", has(EMPTY_CELL))
				.inputItem(Ingredient.of(EMPTY_CELL))
				.inputItem(Ingredient.of(Tags.Items.DYES_WHITE))
				.outputItem(new ItemStack(LIGHT_SPRAY_CAN))
				.save(consumer, canningLoc("light_spray_can"));

		MachineRecipeBuilder.canning()
				.unlockedBy("has_item", has(EMPTY_CELL))
				.inputItem(Ingredient.of(EMPTY_CELL))
				.inputItem(Ingredient.of(Tags.Items.DYES_BLACK))
				.outputItem(new ItemStack(DARK_SPRAY_CAN))
				.save(consumer, canningLoc("dark_spray_can"));

		MachineRecipeBuilder.compressing()
				.unlockedBy("has_item", has(CANNED_FOOD))
				.inputItem(Ingredient.of(CANNED_FOOD))
				.outputItem(new ItemStack(PROTEIN_BAR))
				.save(consumer, compressingLoc("protein_bar"));

		ShapedRecipeBuilder.shaped(CARBON_HELMET)
				.unlockedBy("has_item", has(CARBON_PLATE))
				.group(MODID + ":carbon_helmet")
				.pattern("CEC")
				.pattern("CGC")
				.define('C', CARBON_PLATE)
				.define('E', ENERGY_CRYSTAL)
				.define('G', GLASS)
				.save(consumer, shapedLoc("carbon_helmet"));

		ShapedRecipeBuilder.shaped(CARBON_CHESTPLATE)
				.unlockedBy("has_item", has(CARBON_PLATE))
				.group(MODID + ":carbon_chestplate")
				.pattern("C C")
				.pattern("CEC")
				.pattern("CCC")
				.define('C', CARBON_PLATE)
				.define('E', ENERGY_CRYSTAL)
				.save(consumer, shapedLoc("carbon_chestplate"));

		ShapedRecipeBuilder.shaped(CARBON_LEGGINGS)
				.unlockedBy("has_item", has(CARBON_PLATE))
				.group(MODID + ":carbon_leggings")
				.pattern("CEC")
				.pattern("C C")
				.pattern("C C")
				.define('C', CARBON_PLATE)
				.define('E', ENERGY_CRYSTAL)
				.save(consumer, shapedLoc("carbon_leggings"));

		ShapedRecipeBuilder.shaped(CARBON_BOOTS)
				.unlockedBy("has_item", has(CARBON_PLATE))
				.group(MODID + ":carbon_boots")
				.pattern("C C")
				.pattern("CEC")
				.define('C', CARBON_PLATE)
				.define('E', ENERGY_CRYSTAL)
				.save(consumer, shapedLoc("carbon_boots"));

		ShapedRecipeBuilder.shaped(QUANTUM_HELMET)
				.unlockedBy("has_item", has(IRIDIUM_BATTERY))
				.group(MODID + ":quantum_helmet")
				.pattern(" A ")
				.pattern("IBI")
				.pattern("CGC")
				.define('A', CARBON_HELMET)
				.define('I', IRIDIUM_ALLOY)
				.define('B', IRIDIUM_BATTERY)
				.define('C', IRIDIUM_CIRCUIT)
				.define('G', REINFORCED_GLASS)
				.save(consumer, shapedLoc("quantum_helmet"));

		ShapedRecipeBuilder.shaped(QUANTUM_CHESTPLATE)
				.unlockedBy("has_item", has(IRIDIUM_BATTERY))
				.group(MODID + ":quantum_chestplate")
				.pattern("MAM")
				.pattern("IBI")
				.pattern("IEI")
				.define('A', CARBON_CHESTPLATE)
				.define('I', IRIDIUM_ALLOY)
				.define('B', IRIDIUM_BATTERY)
				.define('M', ADVANCED_MACHINE_BLOCK)
				.define('E', Items.ELYTRA)
				.save(consumer, shapedLoc("quantum_chestplate"));

		ShapedRecipeBuilder.shaped(QUANTUM_LEGGINGS)
				.unlockedBy("has_item", has(IRIDIUM_BATTERY))
				.group(MODID + ":quantum_leggings")
				.pattern("MBM")
				.pattern("IAI")
				.pattern("G G")
				.define('A', CARBON_LEGGINGS)
				.define('I', IRIDIUM_ALLOY)
				.define('B', IRIDIUM_BATTERY)
				.define('M', ADVANCED_MACHINE_BLOCK)
				.define('G', GLOWSTONE)
				.save(consumer, shapedLoc("quantum_leggings"));

		ShapedRecipeBuilder.shaped(QUANTUM_BOOTS)
				.unlockedBy("has_item", has(IRIDIUM_BATTERY))
				.group(MODID + ":quantum_boots")
				.pattern("IAI")
				.pattern("LBL")
				.define('A', CARBON_BOOTS)
				.define('I', IRIDIUM_ALLOY)
				.define('B', IRIDIUM_BATTERY)
				.define('L', Items.LEATHER_BOOTS)
				.save(consumer, shapedLoc("quantum_boots"));
	}
}
