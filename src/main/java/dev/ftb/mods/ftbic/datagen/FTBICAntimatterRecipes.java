package dev.ftb.mods.ftbic.datagen;

import com.ridanisaurus.emendatusenigmatica.registries.ItemHandler;
import com.ridanisaurus.emendatusenigmatica.util.Materials;
import com.ridanisaurus.emendatusenigmatica.util.ProcessedMaterials;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class FTBICAntimatterRecipes extends FTBICRecipesGen {
	public FTBICAntimatterRecipes(DataGenerator generator) {
		super(generator);
	}

	private static void add(Consumer<FinishedRecipe> consumer, ItemStack out, String name, String... pattern) {
		ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(out.getItem(), out.getCount()).group(MODID + ":antimatter/" + name);
		builder.unlockedBy("has_item", has(ANTIMATTER));

		for (String s : pattern) {
			builder.pattern(s);
		}

		builder.define('A', ANTIMATTER);
		builder.save(consumer, shapedLoc("antimatter/" + name));
	}

	private static void addChunk(Consumer<FinishedRecipe> consumer, Materials material, int count, String name, String... pattern) {
		add(consumer, new ItemStack(ItemHandler.backingItemTable.get(ProcessedMaterials.CHUNK, material).get(), count), name, pattern);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		addChunk(consumer, Materials.COAL, 20, "coal", "  A", "A  ", "  A");
		addChunk(consumer, Materials.COPPER, 5, "copper", "  A", "A A");
		addChunk(consumer, Materials.TIN, 5, "tin", "A A", "  A");
		addChunk(consumer, Materials.IRON, 2, "iron", "A A", " A ", "A A");
		addChunk(consumer, Materials.GOLD, 2, "gold", " A ", "AAA", " A ");
		addChunk(consumer, Materials.LAPIS, 9, "lapis", "A ", "A ", "AA");
		addChunk(consumer, Materials.REDSTONE, 24, "redstone", " A ", "AAA");
		addChunk(consumer, Materials.DIAMOND, 1, "diamond", "AAA", "AAA", "AAA");
		addChunk(consumer, Materials.EMERALD, 1, "emerald", "AAA", "A A", "AAA");
		add(consumer, new ItemStack(IRIDIUM_DUST), "iridium", "AAA", " A ", "AAA");
		add(consumer, new ItemStack(Items.CLAY_BALL, 48), "clay", "AA", "A ", "AA");
		add(consumer, new ItemStack(Items.GLOWSTONE, 8), "glowstone", " A ", "A A", "AAA");
		add(consumer, new ItemStack(Items.GRASS_BLOCK, 16), "grass", "A", "A");
		add(consumer, new ItemStack(Items.MYCELIUM, 24), "mycelium", "A A", "AAA");
		add(consumer, new ItemStack(Items.NETHERRACK, 16), "netherrack", "  A", " A ", "A  ");
		add(consumer, new ItemStack(Items.OBSIDIAN, 12), "obsidian", "A A", "A A");
		add(consumer, new ItemStack(Items.WHITE_WOOL, 12), "wool", "A A", "   ", " A ");
		add(consumer, new ItemStack(LATEX, 21), "latex", "A A", "   ", "A A");
	}
}
