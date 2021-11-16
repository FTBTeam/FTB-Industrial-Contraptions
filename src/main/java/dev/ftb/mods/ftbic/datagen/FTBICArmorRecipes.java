package dev.ftb.mods.ftbic.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class FTBICArmorRecipes extends FTBICRecipesGen {
	public FTBICArmorRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(MECHANICAL_ELYTRA)
				.unlockedBy("has_item", has(CARBON_PLATE))
				.group(MODID + ":mechanical_elytra")
				.pattern("CBC")
				.pattern("CSC")
				.pattern("CLC")
				.define('C', CARBON_PLATE)
				.define('B', MV_BATTERY)
				.define('L', Items.ELYTRA)
				.define('S', LV_SOLAR_PANEL)
				.save(consumer, shapedLoc("mechanical_elytra"));

		ShapedRecipeBuilder.shaped(CARBON_HELMET)
				.unlockedBy("has_item", has(CARBON_PLATE))
				.group(MODID + ":carbon_helmet")
				.pattern("CEC")
				.pattern("CAC")
				.define('C', CARBON_PLATE)
				.define('E', ENERGY_CRYSTAL)
				.define('A', Items.NETHERITE_HELMET)
				.save(consumer, shapedLoc("carbon_helmet"));

		ShapedRecipeBuilder.shaped(CARBON_CHESTPLATE)
				.unlockedBy("has_item", has(CARBON_PLATE))
				.group(MODID + ":carbon_chestplate")
				.pattern("CEC")
				.pattern("CAC")
				.pattern("CCC")
				.define('C', CARBON_PLATE)
				.define('E', HV_BATTERY)
				.define('A', Items.NETHERITE_CHESTPLATE)
				.save(consumer, shapedLoc("carbon_chestplate"));

		ShapedRecipeBuilder.shaped(CARBON_LEGGINGS)
				.unlockedBy("has_item", has(CARBON_PLATE))
				.group(MODID + ":carbon_leggings")
				.pattern("CEC")
				.pattern("CAC")
				.pattern("C C")
				.define('C', CARBON_PLATE)
				.define('E', ENERGY_CRYSTAL)
				.define('A', Items.NETHERITE_LEGGINGS)
				.save(consumer, shapedLoc("carbon_leggings"));

		ShapedRecipeBuilder.shaped(CARBON_BOOTS)
				.unlockedBy("has_item", has(CARBON_PLATE))
				.group(MODID + ":carbon_boots")
				.pattern("CEC")
				.pattern("CAC")
				.define('C', CARBON_PLATE)
				.define('E', ENERGY_CRYSTAL)
				.define('A', Items.NETHERITE_BOOTS)
				.save(consumer, shapedLoc("carbon_boots"));

		ShapedRecipeBuilder.shaped(QUANTUM_HELMET)
				.unlockedBy("has_item", has(EV_BATTERY))
				.group(MODID + ":quantum_helmet")
				.pattern(" A ")
				.pattern("INI")
				.pattern("CGC")
				.define('A', CARBON_HELMET)
				.define('I', IRIDIUM_ALLOY)
				.define('N', GLOWSTONE) // Replace with night vision goggles
				.define('C', IRIDIUM_CIRCUIT)
				.define('G', REINFORCED_GLASS)
				.save(consumer, shapedLoc("quantum_helmet"));

		ShapedRecipeBuilder.shaped(QUANTUM_CHESTPLATE)
				.unlockedBy("has_item", has(EV_BATTERY))
				.group(MODID + ":quantum_chestplate")
				.pattern("MBM")
				.pattern("NAN")
				.pattern("IEI")
				.define('A', CARBON_CHESTPLATE)
				.define('I', IRIDIUM_ALLOY)
				.define('B', EV_BATTERY)
				.define('M', ADVANCED_MACHINE_BLOCK)
				.define('E', MECHANICAL_ELYTRA)
				.define('N', ANTIMATTER)
				.save(consumer, shapedLoc("quantum_chestplate"));

		ShapedRecipeBuilder.shaped(QUANTUM_LEGGINGS)
				.unlockedBy("has_item", has(EV_BATTERY))
				.group(MODID + ":quantum_leggings")
				.pattern("III")
				.pattern("MAM")
				.pattern("G G")
				.define('A', CARBON_LEGGINGS)
				.define('I', IRIDIUM_ALLOY)
				.define('M', ADVANCED_MACHINE_BLOCK)
				.define('G', GLOWSTONE)
				.save(consumer, shapedLoc("quantum_leggings"));

		ShapedRecipeBuilder.shaped(QUANTUM_BOOTS)
				.unlockedBy("has_item", has(EV_BATTERY))
				.group(MODID + ":quantum_boots")
				.pattern("IAI")
				.pattern("SLS")
				.define('A', CARBON_BOOTS)
				.define('I', IRIDIUM_ALLOY)
				.define('S', REINFORCED_STONE)
				.define('L', Items.LEATHER_BOOTS)
				.save(consumer, shapedLoc("quantum_boots"));
	}
}
