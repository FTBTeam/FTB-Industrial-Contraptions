package dev.ftb.mods.ftbic.datagen.recipies;

import dev.ftb.mods.ftbic.datagen.FTBICRecipesGen;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;

import java.util.function.Consumer;

public class FTBICEnergyStorageRecipes extends FTBICRecipesGen {
	public FTBICEnergyStorageRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(LV_BATTERY_BOX)
				.unlockedBy("has_item", has(LV_BATTERY))
				.group(MODID + ":lv_battery_box")
				.pattern("PWP")
				.pattern("BBB")
				.pattern("PPP")
				.define('W', LV_CABLE)
				.define('B', LV_BATTERY)
				.define('P', PLANKS)
				.save(consumer, shapedLoc("lv_battery_box"));

		ShapedRecipeBuilder.shaped(MV_BATTERY_BOX)
				.unlockedBy("has_item", has(ENERGY_CRYSTAL))
				.group(MODID + ":mv_battery_box")
				.pattern("WBW")
				.pattern("BMB")
				.pattern("WBW")
				.define('W', MV_CABLE)
				.define('B', ENERGY_CRYSTAL)
				.define('M', MACHINE_BLOCK)
				.save(consumer, shapedLoc("mv_battery_box"));

		ShapedRecipeBuilder.shaped(HV_BATTERY_BOX)
				.unlockedBy("has_item", has(ENERGY_CRYSTAL))
				.group(MODID + ":hv_battery_box")
				.pattern("GCG")
				.pattern("EXE")
				.pattern("GMG")
				.define('C', ADVANCED_CIRCUIT)
				.define('G', GRAPHENE)
				.define('E', ENERGY_CRYSTAL)
				.define('X', MV_BATTERY_BOX)
				.define('M', ADVANCED_MACHINE_BLOCK)
				.save(consumer, shapedLoc("hv_battery_box"));

		ShapedRecipeBuilder.shaped(EV_BATTERY_BOX)
				.unlockedBy("has_item", has(ANTIMATTER_CRYSTAL))
				.group(MODID + ":ev_battery_box")
				.pattern("GCG")
				.pattern("EXE")
				.pattern("GMG")
				.define('C', IRIDIUM_CIRCUIT)
				.define('G', ADVANCED_ALLOY)
				.define('E', ANTIMATTER_CRYSTAL)
				.define('X', HV_BATTERY_BOX)
				.define('M', ADVANCED_MACHINE_BLOCK)
				.save(consumer, shapedLoc("ev_battery_box"));

		ShapedRecipeBuilder.shaped(LV_TRANSFORMER)
				.unlockedBy("has_item", has(LV_CABLE))
				.group(MODID + ":lv_transformer")
				.pattern("PWP")
				.pattern("CCC")
				.pattern("PWP")
				.define('W', LV_CABLE)
				.define('C', COPPER_INGOT)
				.define('P', PLANKS)
				.save(consumer, shapedLoc("lv_transformer"));

		ShapedRecipeBuilder.shaped(MV_TRANSFORMER)
				.unlockedBy("has_item", has(MV_CABLE))
				.group(MODID + ":mv_transformer")
				.pattern("W")
				.pattern("M")
				.pattern("W")
				.define('W', MV_CABLE)
				.define('M', MACHINE_BLOCK)
				.save(consumer, shapedLoc("mv_transformer"));

		ShapedRecipeBuilder.shaped(HV_TRANSFORMER)
				.unlockedBy("has_item", has(MV_TRANSFORMER))
				.group(MODID + ":hv_transformer")
				.pattern(" W ")
				.pattern("CTE")
				.pattern(" W ")
				.define('W', HV_CABLE)
				.define('C', ELECTRONIC_CIRCUIT)
				.define('E', ENERGY_CRYSTAL)
				.define('T', MV_TRANSFORMER)
				.save(consumer, shapedLoc("hv_transformer"));

		ShapedRecipeBuilder.shaped(EV_TRANSFORMER)
				.unlockedBy("has_item", has(HV_TRANSFORMER))
				.group(MODID + ":ev_transformer")
				.pattern(" W ")
				.pattern("CTE")
				.pattern(" W ")
				.define('W', EV_CABLE)
				.define('C', ADVANCED_CIRCUIT)
				.define('E', ADVANCED_ALLOY)
				.define('T', HV_TRANSFORMER)
				.save(consumer, shapedLoc("ev_transformer"));
	}
}
