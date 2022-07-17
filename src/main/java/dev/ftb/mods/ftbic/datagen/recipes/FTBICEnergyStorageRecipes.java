package dev.ftb.mods.ftbic.datagen.recipes;

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
		ShapedRecipeBuilder.shaped(LV_BATTERY_BOX.get())
				.unlockedBy("has_item", has(LV_BATTERY.get()))
				.group(MODID + ":lv_battery_box")
				.pattern("PWP")
				.pattern("BBB")
				.pattern("PPP")
				.define('W', LV_CABLE.get())
				.define('B', LV_BATTERY.get())
				.define('P', PLANKS)
				.save(consumer, shapedLoc("lv_battery_box"));

		ShapedRecipeBuilder.shaped(MV_BATTERY_BOX.get())
				.unlockedBy("has_item", has(ENERGY_CRYSTAL.get()))
				.group(MODID + ":mv_battery_box")
				.pattern("WBW")
				.pattern("BMB")
				.pattern("WBW")
				.define('W', MV_CABLE.get())
				.define('B', ENERGY_CRYSTAL.get())
				.define('M', MACHINE_BLOCK.get())
				.save(consumer, shapedLoc("mv_battery_box"));

		ShapedRecipeBuilder.shaped(HV_BATTERY_BOX.get())
				.unlockedBy("has_item", has(ENERGY_CRYSTAL.get()))
				.group(MODID + ":hv_battery_box")
				.pattern("GCG")
				.pattern("EXE")
				.pattern("GMG")
				.define('C', ADVANCED_CIRCUIT.get())
				.define('G', GRAPHENE.get())
				.define('E', ENERGY_CRYSTAL.get())
				.define('X', MV_BATTERY_BOX.get())
				.define('M', ADVANCED_MACHINE_BLOCK.get())
				.save(consumer, shapedLoc("hv_battery_box"));

		ShapedRecipeBuilder.shaped(EV_BATTERY_BOX.get())
				.unlockedBy("has_item", has(ANTIMATTER_CRYSTAL.get()))
				.group(MODID + ":ev_battery_box")
				.pattern("GCG")
				.pattern("EXE")
				.pattern("GMG")
				.define('C', IRIDIUM_CIRCUIT.get())
				.define('G', ADVANCED_ALLOY.get())
				.define('E', ANTIMATTER_CRYSTAL.get())
				.define('X', HV_BATTERY_BOX.get())
				.define('M', ADVANCED_MACHINE_BLOCK.get())
				.save(consumer, shapedLoc("ev_battery_box"));

		ShapedRecipeBuilder.shaped(LV_TRANSFORMER.get())
				.unlockedBy("has_item", has(LV_CABLE.get()))
				.group(MODID + ":lv_transformer")
				.pattern("PWP")
				.pattern("CCC")
				.pattern("PWP")
				.define('W', LV_CABLE.get())
				.define('C', COPPER_INGOT)
				.define('P', PLANKS)
				.save(consumer, shapedLoc("lv_transformer"));

		ShapedRecipeBuilder.shaped(MV_TRANSFORMER.get())
				.unlockedBy("has_item", has(MV_CABLE.get()))
				.group(MODID + ":mv_transformer")
				.pattern("W")
				.pattern("M")
				.pattern("W")
				.define('W', MV_CABLE.get())
				.define('M', MACHINE_BLOCK.get())
				.save(consumer, shapedLoc("mv_transformer"));

		ShapedRecipeBuilder.shaped(HV_TRANSFORMER.get())
				.unlockedBy("has_item", has(MV_TRANSFORMER.get()))
				.group(MODID + ":hv_transformer")
				.pattern(" W ")
				.pattern("CTE")
				.pattern(" W ")
				.define('W', HV_CABLE.get())
				.define('C', ELECTRONIC_CIRCUIT.get())
				.define('E', ENERGY_CRYSTAL.get())
				.define('T', MV_TRANSFORMER.get())
				.save(consumer, shapedLoc("hv_transformer"));

		ShapedRecipeBuilder.shaped(EV_TRANSFORMER.get())
				.unlockedBy("has_item", has(HV_TRANSFORMER.get()))
				.group(MODID + ":ev_transformer")
				.pattern(" W ")
				.pattern("CTE")
				.pattern(" W ")
				.define('W', EV_CABLE.get())
				.define('C', ADVANCED_CIRCUIT.get())
				.define('E', ADVANCED_ALLOY.get())
				.define('T', HV_TRANSFORMER.get())
				.save(consumer, shapedLoc("ev_transformer"));
	}
}
