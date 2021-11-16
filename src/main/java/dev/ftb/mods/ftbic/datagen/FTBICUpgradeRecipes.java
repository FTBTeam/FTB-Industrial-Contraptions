package dev.ftb.mods.ftbic.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;

import java.util.function.Consumer;

public class FTBICUpgradeRecipes extends FTBICRecipesGen {
	public FTBICUpgradeRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(OVERCLOCKER_UPGRADE)
				.unlockedBy("has_item", has(COOLANT_10K))
				.group(MODID + ":overclocker_upgrade")
				.pattern("UUU")
				.pattern("WCW")
				.define('U', COOLANT_10K)
				.define('W', LV_CABLE)
				.define('C', ELECTRONIC_CIRCUIT)
				.save(consumer, shapedLoc("overclocker_upgrade"));

		ShapedRecipeBuilder.shaped(ENERGY_STORAGE_UPGRADE)
				.unlockedBy("has_item", has(ELECTRONIC_CIRCUIT))
				.group(MODID + ":energy_storage_upgrade")
				.pattern("PPP")
				.pattern("WBW")
				.pattern("PCP")
				.define('P', PLANKS)
				.define('W', LV_CABLE)
				.define('B', LV_BATTERY)
				.define('C', ELECTRONIC_CIRCUIT)
				.save(consumer, shapedLoc("energy_storage_upgrade"));

		ShapedRecipeBuilder.shaped(TRANSFORMER_UPGRADE)
				.unlockedBy("has_item", has(MV_TRANSFORMER))
				.group(MODID + ":transformer_upgrade")
				.pattern("GGG")
				.pattern("WTW")
				.pattern("GCG")
				.define('G', GLASS)
				.define('W', MV_CABLE)
				.define('T', MV_TRANSFORMER)
				.define('C', ELECTRONIC_CIRCUIT)
				.save(consumer, shapedLoc("transformer_upgrade"));

		ShapedRecipeBuilder.shaped(EJECTOR_UPGRADE)
				.unlockedBy("has_item", has(PISTON))
				.group(MODID + ":ejector_upgrade")
				.pattern("T T")
				.pattern(" P ")
				.pattern("T T")
				.define('P', PISTON)
				.define('T', TIN_INGOT)
				.save(consumer, shapedLoc("ejector_upgrade"));
	}
}
