package dev.ftb.mods.ftbic.datagen.recipes;

import dev.ftb.mods.ftbic.datagen.FTBICRecipesGen;
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
		ShapedRecipeBuilder.shaped(MECHANICAL_ELYTRA.get())
				.unlockedBy("has_item", has(CARBON_PLATE.get()))
				.group(MODID + ":mechanical_elytra")
				.pattern("CBC")
				.pattern("CSC")
				.pattern("VLV")
				.define('C', CARBON_PLATE.get())
				.define('B', MV_BATTERY.get())
				.define('L', Items.ELYTRA)
				.define('S', LV_SOLAR_PANEL.get())
				.define('V', HEAT_VENT.get())
				.save(consumer, shapedLoc("mechanical_elytra"));

		ShapedRecipeBuilder.shaped(CARBON_HELMET.get())
				.unlockedBy("has_item", has(CARBON_PLATE.get()))
				.group(MODID + ":carbon_helmet")
				.pattern("CEC")
				.pattern("CAC")
				.define('C', CARBON_PLATE.get())
				.define('E', ENERGY_CRYSTAL.get())
				.define('A', Items.NETHERITE_HELMET)
				.save(consumer, shapedLoc("carbon_helmet"));

		ShapedRecipeBuilder.shaped(CARBON_CHESTPLATE.get())
				.unlockedBy("has_item", has(CARBON_PLATE.get()))
				.group(MODID + ":carbon_chestplate")
				.pattern("CEC")
				.pattern("CAC")
				.pattern("CCC")
				.define('C', CARBON_PLATE.get())
				.define('E', HV_BATTERY.get())
				.define('A', Items.NETHERITE_CHESTPLATE)
				.save(consumer, shapedLoc("carbon_chestplate"));

		ShapedRecipeBuilder.shaped(CARBON_LEGGINGS.get())
				.unlockedBy("has_item", has(CARBON_PLATE.get()))
				.group(MODID + ":carbon_leggings")
				.pattern("CEC")
				.pattern("CAC")
				.pattern("C C")
				.define('C', CARBON_PLATE.get())
				.define('E', ENERGY_CRYSTAL.get())
				.define('A', Items.NETHERITE_LEGGINGS)
				.save(consumer, shapedLoc("carbon_leggings"));

		ShapedRecipeBuilder.shaped(CARBON_BOOTS.get())
				.unlockedBy("has_item", has(CARBON_PLATE.get()))
				.group(MODID + ":carbon_boots")
				.pattern("CEC")
				.pattern("CAC")
				.define('C', CARBON_PLATE.get())
				.define('E', ENERGY_CRYSTAL.get())
				.define('A', Items.NETHERITE_BOOTS)
				.save(consumer, shapedLoc("carbon_boots"));

		ShapedRecipeBuilder.shaped(QUANTUM_HELMET.get())
				.unlockedBy("has_item", has(EV_BATTERY.get()))
				.group(MODID + ":quantum_helmet")
				.pattern(" A ")
				.pattern("INI")
				.pattern("CGC")
				.define('A', CARBON_HELMET.get())
				.define('I', IRIDIUM_ALLOY.get())
				.define('N', GLOWSTONE) // Replace with night vision goggles
				.define('C', IRIDIUM_CIRCUIT.get())
				.define('G', REINFORCED_GLASS.get())
				.save(consumer, shapedLoc("quantum_helmet"));

		ShapedRecipeBuilder.shaped(QUANTUM_CHESTPLATE.get())
				.unlockedBy("has_item", has(EV_BATTERY.get()))
				.group(MODID + ":quantum_chestplate")
				.pattern("MBM")
				.pattern("NAN")
				.pattern("IEI")
				.define('A', CARBON_CHESTPLATE.get())
				.define('I', IRIDIUM_ALLOY.get())
				.define('B', EV_BATTERY.get())
				.define('M', ADVANCED_MACHINE_BLOCK.get())
				.define('E', MECHANICAL_ELYTRA.get())
				.define('N', ANTIMATTER.get())
				.save(consumer, shapedLoc("quantum_chestplate"));

		ShapedRecipeBuilder.shaped(QUANTUM_LEGGINGS.get())
				.unlockedBy("has_item", has(EV_BATTERY.get()))
				.group(MODID + ":quantum_leggings")
				.pattern("III")
				.pattern("MAM")
				.pattern("G G")
				.define('A', CARBON_LEGGINGS.get())
				.define('I', IRIDIUM_ALLOY.get())
				.define('M', ADVANCED_MACHINE_BLOCK.get())
				.define('G', GLOWSTONE)
				.save(consumer, shapedLoc("quantum_leggings"));

		ShapedRecipeBuilder.shaped(QUANTUM_BOOTS.get())
				.unlockedBy("has_item", has(EV_BATTERY.get()))
				.group(MODID + ":quantum_boots")
				.pattern("IAI")
				.pattern("SLS")
				.define('A', CARBON_BOOTS.get())
				.define('I', IRIDIUM_ALLOY.get())
				.define('S', REINFORCED_STONE.get())
				.define('L', Items.LEATHER_BOOTS)
				.save(consumer, shapedLoc("quantum_boots"));
	}
}
