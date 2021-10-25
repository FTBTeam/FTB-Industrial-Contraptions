package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.item.FTBICItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public abstract class FTBICRecipes extends RecipeProvider {
	public static final String MODID = FTBIC.MOD_ID;
	public final Tag<Item> REDSTONE = Tags.Items.DUSTS_REDSTONE;
	public final Tag<Item> GLASS = Tags.Items.GLASS_COLORLESS;
	public final Tag<Item> COAL = ItemTags.COALS;
	public final Tag<Item> DIAMOND = Tags.Items.GEMS_DIAMOND;
	public final Tag<Item> QUARTZ = Tags.Items.GEMS_QUARTZ;
	public final Tag<Item> IRON_INGOT = Tags.Items.INGOTS_IRON;
	public final Tag<Item> GOLD_INGOT = Tags.Items.INGOTS_GOLD;
	public final Tag<Item> COPPER_INGOT = ItemTags.bind("forge:ingots/copper");
	public final Tag<Item> TIN_INGOT = ItemTags.bind("forge:ingots/tin");
	public final Tag<Item> ALUMINUM_INGOT = ItemTags.bind("forge:ingots/aluminum");
	public final Item RESIN = FTBICItems.RESIN.get();
	public final Item RUBBER = FTBICItems.RUBBER.get();

	public FTBICRecipes(DataGenerator generator) {
		super(generator);
	}

	public static ResourceLocation modLoc(String s) {
		return new ResourceLocation(MODID, s);
	}

	public static ResourceLocation smeltingLoc(String s) {
		return modLoc("smelting/" + s);
	}

	public static ResourceLocation campfireCookingLoc(String s) {
		return modLoc("campfire_cooking/" + s);
	}

	public static ResourceLocation shapedLoc(String s) {
		return modLoc("shaped/" + s);
	}

	public static ResourceLocation shapelessLoc(String s) {
		return modLoc("shapeless/" + s);
	}

	public static ResourceLocation smithingLoc(String s) {
		return modLoc("smithing/" + s);
	}

	@Override
	protected final void buildShapelessRecipes(Consumer<FinishedRecipe> consumer) {
		add(consumer);
	}

	public abstract void add(Consumer<FinishedRecipe> consumer);
}
