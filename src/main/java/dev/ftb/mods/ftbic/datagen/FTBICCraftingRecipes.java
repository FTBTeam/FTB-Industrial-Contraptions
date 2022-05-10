package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.item.FTBICItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

import static dev.ftb.mods.ftbic.world.ResourceType.*;
import static dev.ftb.mods.ftbic.world.ResourceElements.*;
import static net.minecraft.world.item.Items.FIRE_CHARGE;
import static net.minecraft.world.item.Items.IRON_NUGGET;

public class FTBICCraftingRecipes extends FTBICRecipesGen {
	public FTBICCraftingRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		ingotToNuggetAndBack(TIN_INGOT, FTBICItems.getResourceFromType(TIN, INGOT).orElseThrow().get(), TIN_NUGGET, FTBICItems.getResourceFromType(TIN, NUGGET).orElseThrow().get(), consumer);
		ingotToNuggetAndBack(LEAD_INGOT, FTBICItems.getResourceFromType(LEAD, INGOT).orElseThrow().get(), LEAD_NUGGET, FTBICItems.getResourceFromType(LEAD, NUGGET).orElseThrow().get(), consumer);
		ingotToNuggetAndBack(URANIUM_INGOT, FTBICItems.getResourceFromType(URANIUM, INGOT).orElseThrow().get(), URANIUM_NUGGET, FTBICItems.getResourceFromType(URANIUM, NUGGET).orElseThrow().get(), consumer);
		ingotToNuggetAndBack(IRIDIUM_INGOT, FTBICItems.getResourceFromType(IRIDIUM, INGOT).orElseThrow().get(), IRIDIUM_NUGGET, FTBICItems.getResourceFromType(IRIDIUM, NUGGET).orElseThrow().get(), consumer);
		ingotToNuggetAndBack(ALUMINUM_INGOT, FTBICItems.getResourceFromType(ALUMINUM, INGOT).orElseThrow().get(), ALUMINUM_NUGGET, FTBICItems.getResourceFromType(ALUMINUM, NUGGET).orElseThrow().get(), consumer);
		ingotToNuggetAndBack(ENDERIUM_INGOT, FTBICItems.getResourceFromType(ENDERIUM, INGOT).orElseThrow().get(), ENDERIUM_NUGGET, FTBICItems.getResourceFromType(ENDERIUM, NUGGET).orElseThrow().get(), consumer);
		ingotToNuggetAndBack(COPPER_INGOT, Items.COPPER_INGOT, COPPER_NUGGET, FTBICItems.getResourceFromType(COPPER, NUGGET).orElseThrow().get(), consumer);
		ingotToNuggetAndBack(BRONZE_INGOT, FTBICItems.getResourceFromType(BRONZE, INGOT).orElseThrow().get(), BRONZE_NUGGET, FTBICItems.getResourceFromType(BRONZE, NUGGET).orElseThrow().get(), consumer);


		ingotToBlockAndBack(TIN_INGOT, FTBICItems.getResourceFromType(TIN, INGOT).orElseThrow().get(), TIN_BLOCK, FTBICItems.getResourceFromType(TIN, BLOCK).orElseThrow().get(), consumer);
		ingotToBlockAndBack(LEAD_INGOT, FTBICItems.getResourceFromType(LEAD, INGOT).orElseThrow().get(), LEAD_BLOCK, FTBICItems.getResourceFromType(LEAD, BLOCK).orElseThrow().get(), consumer);
		ingotToBlockAndBack(URANIUM_INGOT, FTBICItems.getResourceFromType(URANIUM, INGOT).orElseThrow().get(), URANIUM_BLOCK, FTBICItems.getResourceFromType(URANIUM, BLOCK).orElseThrow().get(), consumer);
		ingotToBlockAndBack(IRIDIUM_INGOT, FTBICItems.getResourceFromType(IRIDIUM, INGOT).orElseThrow().get(), IRIDIUM_BLOCK, FTBICItems.getResourceFromType(IRIDIUM, BLOCK).orElseThrow().get(), consumer);
		ingotToBlockAndBack(ALUMINUM_INGOT, FTBICItems.getResourceFromType(ALUMINUM, INGOT).orElseThrow().get(), ALUMINUM_BLOCK, FTBICItems.getResourceFromType(ALUMINUM, BLOCK).orElseThrow().get(), consumer);
		ingotToBlockAndBack(ENDERIUM_INGOT, FTBICItems.getResourceFromType(ENDERIUM, INGOT).orElseThrow().get(), ENDERIUM_BLOCK, FTBICItems.getResourceFromType(ENDERIUM, BLOCK).orElseThrow().get(), consumer);
		ingotToBlockAndBack(BRONZE_INGOT, FTBICItems.getResourceFromType(BRONZE, INGOT).orElseThrow().get(), BRONZE_BLOCK, FTBICItems.getResourceFromType(BRONZE, BLOCK).orElseThrow().get(), consumer);


		ShapedRecipeBuilder.shaped(FTBICItems.getResourceFromType(ENDERIUM, DUST).orElseThrow().get(), 2)
				.unlockedBy("has_item", has(DIAMOND_DUST))
				.group(MODID + ":" + ENDER_DUST_ITEM)
				.pattern("LLL")
				.pattern("DEE")
				.define('L', LEAD_DUST)
				.define('D', DIAMOND_DUST)
				.define('E', ENDER_DUST)
				.save(consumer, shapedLoc("enderium_dust"));

		ShapedRecipeBuilder.shaped(FTBICItems.getResourceFromType(ENDERIUM, DUST).orElseThrow().get(), 2)
				.unlockedBy("has_item", has(DIAMOND_DUST))
				.group(MODID + ":" + ENDER_DUST_ITEM)
				.pattern("LLL")
				.pattern("DEE")
				.define('L', LEAD_DUST)
				.define('D', DIAMOND_DUST)
				.define('E', ENDER_PEARL)
				.save(consumer, shapedLoc("enderium_dust_2"));

		ShapedRecipeBuilder.shaped(FTBICItems.getResourceFromType(ENDERIUM, INGOT).orElseThrow().get(), 2)
				.unlockedBy("has_item", has(DIAMOND_DUST))
				.group(MODID + ":" + ENDER_DUST_ITEM)
				.pattern("LLL")
				.pattern("DEE")
				.pattern("F  ")
				.define('L', Ingredient.merge(Arrays.asList(Ingredient.of(LEAD_DUST), Ingredient.of(LEAD_INGOT))))
				.define('D', DIAMOND_DUST)
				.define('E', ENDER_DUST)
				.define('F', FIRE_CHARGE)
				.save(consumer, shapedLoc("enderium_ingot"));

		ShapedRecipeBuilder.shaped(FTBICItems.getResourceFromType(BRONZE, DUST).orElseThrow().get(), 4)
				.unlockedBy("has_item", has(COPPER_DUST))
				.group(MODID + ":" + FTBICItems.getResourceFromType(COPPER, DUST).orElseThrow().get())
				.pattern("CC ")
				.pattern("CT ")
				.define('C', COPPER_DUST)
				.define('T', TIN_DUST)
				.save(consumer, shapedLoc("bronze_dust"));

		ShapedRecipeBuilder.shaped(FTBICItems.getResourceFromType(BRONZE, INGOT).orElseThrow().get(), 4)
				.unlockedBy("has_item", has(COPPER_INGOT))
				.group(MODID + ":" + Items.COPPER_INGOT)
				.pattern("CCC")
				.pattern("TF ")
				.define('C', Ingredient.merge(Arrays.asList(Ingredient.of(COPPER_DUST), Ingredient.of(COPPER_INGOT))))
				.define('T', Ingredient.merge(Arrays.asList(Ingredient.of(TIN_DUST), Ingredient.of(TIN_INGOT))))
				.define('F', FIRE_CHARGE)
				.save(consumer, shapedLoc("bronze_ingot"));

		gearFromTag(TIN_INGOT, FTBICItems.getResourceFromType(TIN, GEAR).orElseThrow().get(), consumer);
		gearFromTag(LEAD_INGOT, FTBICItems.getResourceFromType(LEAD, GEAR).orElseThrow().get(), consumer);
		gearFromTag(URANIUM_INGOT, FTBICItems.getResourceFromType(URANIUM, GEAR).orElseThrow().get(), consumer);
		gearFromTag(IRIDIUM_INGOT, FTBICItems.getResourceFromType(IRIDIUM, GEAR).orElseThrow().get(), consumer);
		gearFromTag(ALUMINUM_INGOT, FTBICItems.getResourceFromType(ALUMINUM, GEAR).orElseThrow().get(), consumer);
		gearFromTag(ENDERIUM_INGOT, FTBICItems.getResourceFromType(ENDERIUM, GEAR).orElseThrow().get(), consumer);
		gearFromTag(IRON_INGOT, FTBICItems.getResourceFromType(IRON, GEAR).orElseThrow().get(), consumer);
		gearFromTag(COPPER_INGOT, FTBICItems.getResourceFromType(COPPER, GEAR).orElseThrow().get(), consumer);
		gearFromTag(GOLD_INGOT, FTBICItems.getResourceFromType(GOLD, GEAR).orElseThrow().get(), consumer);
		gearFromTag(BRONZE_INGOT, FTBICItems.getResourceFromType(BRONZE, GEAR).orElseThrow().get(), consumer);

		rodFromTag(TIN_INGOT, FTBICItems.getResourceFromType(TIN, ROD).orElseThrow().get(), consumer);
		rodFromTag(LEAD_INGOT, FTBICItems.getResourceFromType(LEAD, ROD).orElseThrow().get(), consumer);
		rodFromTag(URANIUM_INGOT, FTBICItems.getResourceFromType(URANIUM, ROD).orElseThrow().get(), consumer);
		rodFromTag(IRIDIUM_INGOT, FTBICItems.getResourceFromType(IRIDIUM, ROD).orElseThrow().get(), consumer);
		rodFromTag(ALUMINUM_INGOT, FTBICItems.getResourceFromType(ALUMINUM, ROD).orElseThrow().get(), consumer);
		rodFromTag(ENDERIUM_INGOT, FTBICItems.getResourceFromType(ENDERIUM, ROD).orElseThrow().get(), consumer);
		rodFromTag(IRON_INGOT, FTBICItems.getResourceFromType(IRON, ROD).orElseThrow().get(), consumer);
		rodFromTag(COPPER_INGOT, FTBICItems.getResourceFromType(COPPER, ROD).orElseThrow().get(), consumer);
		rodFromTag(GOLD_INGOT, FTBICItems.getResourceFromType(GOLD, ROD).orElseThrow().get(), consumer);
		rodFromTag(BRONZE_INGOT, FTBICItems.getResourceFromType(BRONZE, ROD).orElseThrow().get(), consumer);
	}

	private static void gearFromTag(TagKey<Item> inputTag, Item output, Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(output)
				.unlockedBy("has_item", has(inputTag))
				.group(MODID + ":" + output)
				.pattern(" X ")
				.pattern("XIX")
				.pattern(" X ")
				.define('X', inputTag)
				.define('I', IRON_NUGGET)
				.save(consumer, shapedLoc(inputTag.location().getPath() + "_to_" + output));
	}

	private static void rodFromTag(TagKey<Item> inputTag, Item output, Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(output)
				.unlockedBy("has_item", has(inputTag))
				.group(MODID + ":" + output)
				.pattern(" X ")
				.pattern(" X ")
				.define('X', inputTag)
				.save(consumer, shapedLoc(inputTag.location().getPath() + "_to_" + output));
	}

	private static void ingotToNuggetAndBack(TagKey<Item> ingotTag, Item ingotItem, TagKey<Item> nuggetTag, Item nuggetItem, Consumer<FinishedRecipe> consumer) {
		ShapelessRecipeBuilder.shapeless(nuggetItem, 9)
			.unlockedBy("has_item", has(ingotTag))
			.group(MODID + ":" + nuggetItem)
			.requires(ingotTag)
			.save(consumer, shapelessLoc(ingotItem + "_to_" + nuggetItem));

		ShapedRecipeBuilder.shaped(ingotItem)
				.unlockedBy("has_item", has(nuggetTag))
				.group(MODID + ":" + ingotItem)
				.pattern("XXX")
				.pattern("XXX")
				.pattern("XXX")
				.define('X', nuggetTag)
				.save(consumer, shapedLoc(nuggetTag.location().getPath() + "_to_" + ingotItem));
	}

	private static void ingotToBlockAndBack(TagKey<Item> ingotTag, Item ingotItem, TagKey<Item> blockTag, Item blockItem, Consumer<FinishedRecipe> consumer) {
		ShapelessRecipeBuilder.shapeless(ingotItem, 9)
				.unlockedBy("has_item", has(blockTag))
				.group(MODID + ":" + ingotItem)
				.requires(blockTag)
				.save(consumer, shapelessLoc(blockItem + "_to_" + ingotItem));

		ShapedRecipeBuilder.shaped(blockItem)
				.unlockedBy("has_item", has(ingotTag))
				.group(MODID + ":" + blockItem)
				.pattern("XXX")
				.pattern("XXX")
				.pattern("XXX")
				.define('X', ingotTag)
				.save(consumer, shapedLoc(ingotItem + "_to_" + blockItem));
	}
}
