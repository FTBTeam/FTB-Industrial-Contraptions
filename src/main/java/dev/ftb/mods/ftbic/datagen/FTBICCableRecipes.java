package dev.ftb.mods.ftbic.datagen;

import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Consumer;
import java.util.function.Function;

public class FTBICCableRecipes extends FTBICRecipesGen {
	public FTBICCableRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		Function<TagKey<Item>, InventoryChangeTrigger.TriggerInstance> tagKeyHas = (e) -> RecipeProvider.inventoryTrigger(ItemPredicate.Builder.item().of(e).build());

		// Wire from metal

		ShapedRecipeBuilder.shaped(COPPER_WIRE, 6)
				.unlockedBy("has_item", tagKeyHas.apply(COPPER_INGOT))
				.group(MODID + ":copper_wire")
				.pattern("MMM")
				.define('M', COPPER_INGOT)
				.save(consumer, shapedLoc("copper_wire"));

		ShapedRecipeBuilder.shaped(ALUMINUM_WIRE, 6)
				.unlockedBy("has_item", tagKeyHas.apply(ALUMINUM_INGOT))
				.group(MODID + ":aluminum_wire")
				.pattern("MMM")
				.define('M', ALUMINUM_INGOT)
				.save(consumer, shapedLoc("aluminum_wire"));

		ShapedRecipeBuilder.shaped(GOLD_WIRE, 6)
				.unlockedBy("has_item", tagKeyHas.apply(GOLD_INGOT))
				.group(MODID + ":gold_wire")
				.pattern("MMM")
				.define('M', GOLD_INGOT)
				.save(consumer, shapedLoc("gold_wire"));

		ShapedRecipeBuilder.shaped(ENDERIUM_WIRE, 6)
				.unlockedBy("has_item", tagKeyHas.apply(ENDERIUM_INGOT))
				.group(MODID + ":enderium_wire")
				.pattern("MMM")
				.define('M', ENDERIUM_INGOT)
				.save(consumer, shapedLoc("enderium_wire"));

		// Cable from metal + rubber

		ShapedRecipeBuilder.shaped(LV_CABLE, 6)
				.unlockedBy("has_item", has(RUBBER))
				.group(MODID + ":lv_cable")
				.pattern("RRR")
				.pattern("MMM")
				.pattern("RRR")
				.define('R', RUBBER)
				.define('M', COPPER_INGOT)
				.save(consumer, shapedLoc("lv_cable"));

		ShapedRecipeBuilder.shaped(MV_CABLE, 6)
				.unlockedBy("has_item", has(RUBBER))
				.group(MODID + ":mv_cable")
				.pattern("RRR")
				.pattern("MMM")
				.pattern("RRR")
				.define('R', RUBBER)
				.define('M', ALUMINUM_INGOT)
				.save(consumer, shapedLoc("mv_cable"));

		ShapedRecipeBuilder.shaped(HV_CABLE, 6)
				.unlockedBy("has_item", has(RUBBER))
				.group(MODID + ":hv_cable")
				.pattern("RRR")
				.pattern("MMM")
				.pattern("RRR")
				.define('R', RUBBER)
				.define('M', GOLD_INGOT)
				.save(consumer, shapedLoc("hv_cable"));

		ShapedRecipeBuilder.shaped(EV_CABLE, 6)
				.unlockedBy("has_item", has(RUBBER))
				.group(MODID + ":ev_cable")
				.pattern("RRR")
				.pattern("MMM")
				.pattern("RRR")
				.define('R', RUBBER)
				.define('M', ENDERIUM_INGOT)
				.save(consumer, shapedLoc("ev_cable"));

		// Cable from wire + rubber

		ShapelessRecipeBuilder.shapeless(LV_CABLE)
				.unlockedBy("has_item", has(RUBBER))
				.group(MODID + ":lv_cable")
				.requires(COPPER_WIRE)
				.requires(RUBBER)
				.save(consumer, shapelessLoc("lv_cable"));

		ShapelessRecipeBuilder.shapeless(MV_CABLE)
				.unlockedBy("has_item", has(RUBBER))
				.group(MODID + ":mv_cable")
				.requires(ALUMINUM_WIRE)
				.requires(RUBBER)
				.save(consumer, shapelessLoc("mv_cable"));

		ShapelessRecipeBuilder.shapeless(HV_CABLE)
				.unlockedBy("has_item", has(RUBBER))
				.group(MODID + ":hv_cable")
				.requires(GOLD_WIRE)
				.requires(RUBBER)
				.save(consumer, shapelessLoc("hv_cable"));

		ShapelessRecipeBuilder.shapeless(EV_CABLE)
				.unlockedBy("has_item", has(RUBBER))
				.group(MODID + ":ev_cable")
				.requires(ENDERIUM_WIRE)
				.requires(RUBBER)
				.save(consumer, shapelessLoc("ev_cable"));

		// Glass cable

		ShapedRecipeBuilder.shaped(IV_CABLE, 6)
				.unlockedBy("has_item", has(ENERGY_CRYSTAL))
				.group(MODID + ":iv_cable")
				.pattern("GGG")
				.pattern(" C ")
				.pattern("GGG")
				.define('G', GLASS)
				.define('C', ENERGY_CRYSTAL)
				.save(consumer, shapedLoc("iv_cable"));

		// Burnt cable

		ShapelessRecipeBuilder.shapeless(SCRAP)
				.unlockedBy("has_item", has(BURNT_CABLE))
				.group(MODID + ":scrap")
				.requires(BURNT_CABLE)
				.save(consumer, shapelessLoc("scrap_from_burnt_cable"));
	}
}