package dev.ftb.mods.ftbic.datagen.recipes;

import dev.ftb.mods.ftbic.datagen.FTBICRecipesGen;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class FTBICGeneratorRecipes extends FTBICRecipesGen {
	public FTBICGeneratorRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(BASIC_GENERATOR.get())
				.unlockedBy("has_item", has(INDUSTRIAL_GRADE_METAL.get()))
				.group(MODID + ":basic_generator")
				.pattern(" B ")
				.pattern("MMM")
				.pattern(" F ")
				.define('B', LV_BATTERY.get())
				.define('M', INDUSTRIAL_GRADE_METAL.get())
				.define('F', IRON_FURNACE.get())
				.save(consumer, shapedLoc("basic_generator"));

		ShapedRecipeBuilder.shaped(GEOTHERMAL_GENERATOR.get())
				.unlockedBy("has_item", has(BASIC_GENERATOR.get()))
				.group(MODID + ":geothermal_generator")
				.pattern("LCL")
				.pattern("LCL")
				.pattern("MGM")
				.define('L', GLASS)
				.define('C', FLUID_CELL.get())
				.define('M', INDUSTRIAL_GRADE_METAL.get())
				.define('G', BASIC_GENERATOR.get())
				.save(consumer, shapedLoc("geothermal_generator"));

		ShapedRecipeBuilder.shaped(WIND_MILL.get())
				.unlockedBy("has_item", has(BASIC_GENERATOR.get()))
				.group(MODID + ":wind_mill")
				.pattern(" P ")
				.pattern("CGC")
				.pattern(" P ")
				.define('P', CARBON_PLATE.get())
				.define('G', BASIC_GENERATOR.get())
				.define('C', ELECTRONIC_CIRCUIT.get())
				.save(consumer, shapedLoc("wind_mill"));

		ShapedRecipeBuilder.shaped(LV_SOLAR_PANEL.get())
				.unlockedBy("has_item", has(BASIC_GENERATOR.get()))
				.group(MODID + ":lv_solar_panel")
				.pattern("LLL")
				.pattern("DSD")
				.pattern("CGC")
				.define('L', GLASS)
				.define('D', COAL_DUST)
				.define('S', SILICON)
				.define('G', BASIC_GENERATOR.get())
				.define('C', ELECTRONIC_CIRCUIT.get())
				.save(consumer, shapedLoc("lv_solar_panel"));

		ShapedRecipeBuilder.shaped(MV_SOLAR_PANEL.get())
				.unlockedBy("has_item", has(LV_SOLAR_PANEL.get()))
				.group(MODID + ":mv_solar_panel")
				.pattern("SES")
				.pattern("SAS")
				.pattern("SCS")
				.define('S', LV_SOLAR_PANEL.get())
				.define('E', ENERGY_CRYSTAL.get())
				.define('A', ADVANCED_ALLOY.get())
				.define('C', ADVANCED_CIRCUIT.get())
				.save(consumer, shapedLoc("mv_solar_panel"));

		ShapedRecipeBuilder.shaped(HV_SOLAR_PANEL.get())
				.unlockedBy("has_item", has(MV_SOLAR_PANEL.get()))
				.group(MODID + ":hv_solar_panel")
				.pattern("SGS")
				.pattern("SAS")
				.pattern("SCS")
				.define('S', MV_SOLAR_PANEL.get())
				.define('G', GRAPHENE.get())
				.define('A', ADVANCED_MACHINE_BLOCK.get())
				.define('C', IRIDIUM_CIRCUIT.get())
				.save(consumer, shapedLoc("hv_solar_panel"));

		ShapedRecipeBuilder.shaped(EV_SOLAR_PANEL.get())
				.unlockedBy("has_item", has(HV_SOLAR_PANEL.get()))
				.group(MODID + ":ev_solar_panel")
				.pattern("SAS")
				.pattern("SCS")
				.pattern("SNS")
				.define('S', HV_SOLAR_PANEL.get())
				.define('A', ANTIMATTER.get())
				.define('C', LARGE_COOLANT_CELL.get())
				.define('N', Items.NETHERITE_BLOCK)
				.save(consumer, shapedLoc("ev_solar_panel"));
	}
}
