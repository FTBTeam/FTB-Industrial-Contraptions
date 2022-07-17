package dev.ftb.mods.ftbic.datagen.recipes;

import dev.ftb.mods.ftbic.datagen.FTBICRecipesGen;
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
		ShapedRecipeBuilder.shaped(OVERCLOCKER_UPGRADE.get())
				.unlockedBy("has_item", has(SMALL_COOLANT_CELL.get()))
				.group(MODID + ":overclocker_upgrade")
				.pattern("UUU")
				.pattern("WCW")
				.define('U', unbroken(SMALL_COOLANT_CELL.get()))
				.define('W', LV_CABLE.get())
				.define('C', ELECTRONIC_CIRCUIT.get())
				.save(consumer, shapedLoc("overclocker_upgrade"));

		ShapedRecipeBuilder.shaped(ENERGY_STORAGE_UPGRADE.get())
				.unlockedBy("has_item", has(ELECTRONIC_CIRCUIT.get()))
				.group(MODID + ":energy_storage_upgrade")
				.pattern("PPP")
				.pattern("WBW")
				.pattern("PCP")
				.define('P', PLANKS)
				.define('W', LV_CABLE.get())
				.define('B', LV_BATTERY.get())
				.define('C', ELECTRONIC_CIRCUIT.get())
				.save(consumer, shapedLoc("energy_storage_upgrade"));

		ShapedRecipeBuilder.shaped(TRANSFORMER_UPGRADE.get())
				.unlockedBy("has_item", has(MV_TRANSFORMER.get()))
				.group(MODID + ":transformer_upgrade")
				.pattern("GGG")
				.pattern("WTW")
				.pattern("GCG")
				.define('G', GLASS)
				.define('W', MV_CABLE.get())
				.define('T', MV_TRANSFORMER.get())
				.define('C', ELECTRONIC_CIRCUIT.get())
				.save(consumer, shapedLoc("transformer_upgrade"));

		ShapedRecipeBuilder.shaped(EJECTOR_UPGRADE.get())
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
