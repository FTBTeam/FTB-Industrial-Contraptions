package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.item.FTBICItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;

import java.util.function.Consumer;

public class FTBICGeneratorRecipes extends FTBICRecipes {
	public FTBICGeneratorRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(FTBICElectricBlocks.LV_SOLAR_PANEL.item.get())
				.unlockedBy("has_item", has(COAL_DUST))
				.group(MODID + ":solar_panel")
				.pattern("LLL")
				.pattern("DDD")
				.pattern("CGC")
				.define('L', GLASS)
				.define('D', COAL_DUST)
				.define('G', FTBICElectricBlocks.BASIC_GENERATOR.item.get())
				.define('C', FTBICItems.ELECTRONIC_CIRCUIT.get())
				.save(consumer, shapedLoc("lv_solar_panel"));

		ShapedRecipeBuilder.shaped(FTBICElectricBlocks.MV_SOLAR_PANEL.item.get())
				.unlockedBy("has_item", has(FTBICElectricBlocks.LV_SOLAR_PANEL.item.get()))
				.group(MODID + ":solar_panel")
				.pattern("SES")
				.pattern("SAS")
				.pattern("SCS")
				.define('S', FTBICElectricBlocks.LV_SOLAR_PANEL.item.get())
				.define('E', FTBICItems.ENERGY_CRYSTAL.get())
				.define('A', FTBICItems.ADVANCED_ALLOY.get())
				.define('C', FTBICItems.ADVANCED_CIRCUIT.get())
				.save(consumer, shapedLoc("mv_solar_panel"));

		ShapedRecipeBuilder.shaped(FTBICElectricBlocks.HV_SOLAR_PANEL.item.get())
				.unlockedBy("has_item", has(FTBICElectricBlocks.MV_SOLAR_PANEL.item.get()))
				.group(MODID + ":solar_panel")
				.pattern("SGS")
				.pattern("SAS")
				.pattern("SCS")
				.define('S', FTBICElectricBlocks.MV_SOLAR_PANEL.item.get())
				.define('G', FTBICItems.GRAPHENE.get())
				.define('A', FTBICItems.ADVANCED_MACHINE_BLOCK.get())
				.define('C', FTBICItems.IRIDIUM_PLATE.get())
				.save(consumer, shapedLoc("hv_solar_panel"));
	}
}
