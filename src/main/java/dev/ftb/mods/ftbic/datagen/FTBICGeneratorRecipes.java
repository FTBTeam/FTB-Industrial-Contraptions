package dev.ftb.mods.ftbic.datagen;

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
		ShapedRecipeBuilder.shaped(BASIC_GENERATOR)
				.unlockedBy("has_item", has(INDUSTRIAL_GRADE_METAL))
				.group(MODID + ":basic_generator")
				.pattern(" B ")
				.pattern("MMM")
				.pattern(" F ")
				.define('B', LV_BATTERY)
				.define('M', INDUSTRIAL_GRADE_METAL)
				.define('F', IRON_FURNACE)
				.save(consumer, shapedLoc("basic_generator"));

		ShapedRecipeBuilder.shaped(GEOTHERMAL_GENERATOR)
				.unlockedBy("has_item", has(BASIC_GENERATOR))
				.group(MODID + ":geothermal_generator")
				.pattern("LCL")
				.pattern("LCL")
				.pattern("MGM")
				.define('L', GLASS)
				.define('C', FLUID_CELL)
				.define('M', INDUSTRIAL_GRADE_METAL)
				.define('G', BASIC_GENERATOR)
				.save(consumer, shapedLoc("geothermal_generator"));

		ShapedRecipeBuilder.shaped(WIND_MILL)
				.unlockedBy("has_item", has(BASIC_GENERATOR))
				.group(MODID + ":wind_mill")
				.pattern(" P ")
				.pattern("CGC")
				.pattern(" P ")
				.define('P', CARBON_PLATE)
				.define('G', BASIC_GENERATOR)
				.define('C', ELECTRONIC_CIRCUIT)
				.save(consumer, shapedLoc("wind_mill"));

		ShapedRecipeBuilder.shaped(LV_SOLAR_PANEL)
				.unlockedBy("has_item", has(BASIC_GENERATOR))
				.group(MODID + ":lv_solar_panel")
				.pattern("LLL")
				.pattern("DSD")
				.pattern("CGC")
				.define('L', GLASS)
				.define('D', COAL_DUST)
				.define('S', SILICON)
				.define('G', BASIC_GENERATOR)
				.define('C', ELECTRONIC_CIRCUIT)
				.save(consumer, shapedLoc("lv_solar_panel"));

		ShapedRecipeBuilder.shaped(MV_SOLAR_PANEL)
				.unlockedBy("has_item", has(LV_SOLAR_PANEL))
				.group(MODID + ":mv_solar_panel")
				.pattern("SES")
				.pattern("SAS")
				.pattern("SCS")
				.define('S', LV_SOLAR_PANEL)
				.define('E', ENERGY_CRYSTAL)
				.define('A', ADVANCED_ALLOY)
				.define('C', ADVANCED_CIRCUIT)
				.save(consumer, shapedLoc("mv_solar_panel"));

		ShapedRecipeBuilder.shaped(HV_SOLAR_PANEL)
				.unlockedBy("has_item", has(MV_SOLAR_PANEL))
				.group(MODID + ":hv_solar_panel")
				.pattern("SGS")
				.pattern("SAS")
				.pattern("SCS")
				.define('S', MV_SOLAR_PANEL)
				.define('G', GRAPHENE)
				.define('A', ADVANCED_MACHINE_BLOCK)
				.define('C', IRIDIUM_CIRCUIT)
				.save(consumer, shapedLoc("hv_solar_panel"));

		ShapedRecipeBuilder.shaped(EV_SOLAR_PANEL)
				.unlockedBy("has_item", has(HV_SOLAR_PANEL))
				.group(MODID + ":ev_solar_panel")
				.pattern("SAS")
				.pattern("SCS")
				.pattern("SNS")
				.define('S', HV_SOLAR_PANEL)
				.define('A', ANTIMATTER)
				.define('C', LARGE_COOLANT_CELL)
				.define('N', Items.NETHERITE_BLOCK)
				.save(consumer, shapedLoc("ev_solar_panel"));
	}
}
