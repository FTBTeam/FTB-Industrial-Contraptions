package dev.ftb.mods.ftbic.datagen.recipes;

import dev.ftb.mods.ftbic.datagen.FTBICRecipesGen;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.world.ResourceElements;
import dev.ftb.mods.ftbic.world.ResourceType;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class FTBICMaceratingRecipes extends FTBICRecipesGen {
	public FTBICMaceratingRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		oreProcessing(TIN_ORE, TIN_CHUNK, ResourceElements.TIN, consumer);
		oreProcessing(LEAD_ORE, LEAD_CHUNK, ResourceElements.LEAD, consumer);
		oreProcessing(IRIDIUM_ORE, IRIDIUM_CHUNK, ResourceElements.IRIDIUM, consumer);
		oreProcessing(URANIUM_ORE, URANIUM_CHUNK, ResourceElements.URANIUM, consumer);
		oreProcessing(ALUMINUM_ORE, ALUMINUM_CHUNK, ResourceElements.ALUMINUM, consumer);
		oreProcessing(IRON_ORE, IRON_RAW, ResourceElements.IRON, consumer);
		oreProcessing(GOLD_ORE, GOLD_RAW, ResourceElements.GOLD, consumer);
		oreProcessing(COPPER_ORE, COPPER_RAW, ResourceElements.COPPER, consumer);

		resourceBreakDown(List.of(TIN_INGOT), ResourceElements.TIN, ResourceType.DUST, 1, consumer);
		resourceBreakDown(List.of(LEAD_INGOT), ResourceElements.LEAD, ResourceType.DUST, 1, consumer);
		resourceBreakDown(List.of(IRIDIUM_INGOT), ResourceElements.IRIDIUM, ResourceType.DUST, 1, consumer);
		resourceBreakDown(List.of(URANIUM_INGOT), ResourceElements.URANIUM, ResourceType.DUST, 1, consumer);
		resourceBreakDown(List.of(ALUMINUM_INGOT), ResourceElements.ALUMINUM, ResourceType.DUST, 1, consumer);
		resourceBreakDown(List.of(ENDERIUM_INGOT), ResourceElements.ENDERIUM, ResourceType.DUST, 1, consumer);
		resourceBreakDown(List.of(IRON_INGOT), ResourceElements.IRON, ResourceType.DUST, 1, consumer);
		resourceBreakDown(List.of(GOLD_INGOT), ResourceElements.GOLD, ResourceType.DUST, 1, consumer);
		resourceBreakDown(List.of(COPPER_INGOT), ResourceElements.COPPER, ResourceType.DUST, 1, consumer);
		resourceBreakDown(List.of(BRONZE_INGOT), ResourceElements.BRONZE, ResourceType.DUST, 1, consumer);

		resourceBreakDown(List.of(DIAMOND), ResourceElements.DIAMOND, ResourceType.DUST, 1, consumer);
	}

	// ore processing recipes: raw chunks => 1.35x yield of dust, ore block => 2x yield of dust
	private static void oreProcessing(TagKey<Item> oreTag, TagKey<Item> chunkTag, ResourceElements element, Consumer<FinishedRecipe> consumer) {
		var dust = FTBICItems.getResourceFromType(element, ResourceType.DUST).orElseThrow().get();
		MachineRecipeBuilder.macerating()
				.unlockedBy("has_item", has(oreTag))
				.inputItem(Ingredient.of(oreTag))
				.outputItem(new ItemStack(dust, 2))
				.save(consumer, maceratingLoc("%s_to_dust".formatted(oreTag.location().getPath())));

		MachineRecipeBuilder.macerating()
				.unlockedBy("has_item", has(chunkTag))
				.inputItem(Ingredient.of(chunkTag))
				.outputItem(new ItemStack(dust))
				.outputItem(new ItemStack(dust), 0.35)
				.save(consumer, maceratingLoc("%s_to_dust".formatted(chunkTag.location().getPath())));
	}

	private static void resourceBreakDown(List<TagKey<Item>> input, ResourceElements element, ResourceType type, int count, Consumer<FinishedRecipe> consumer) {
		for (TagKey<Item> item : input) {
			MachineRecipeBuilder.macerating()
					.unlockedBy("has_item", has(item))
					.inputItem(Ingredient.of(item))
					.outputItem(new ItemStack(FTBICItems.getResourceFromType(element, type).orElseThrow().get(), count))
					.save(consumer, maceratingLoc(item.location().getPath() + "_to_" + type.name().toLowerCase(Locale.ENGLISH)));
		}
	}

	private static void resourceBreakDown(List<TagKey<Item>> input, Item output, int count, Consumer<FinishedRecipe> consumer) {
		for (TagKey<Item> item : input) {
			MachineRecipeBuilder.macerating()
					.unlockedBy("has_item", has(item))
					.inputItem(Ingredient.of(item))
					.outputItem(new ItemStack(output, count))
					.save(consumer, maceratingLoc(item.location().getPath() + "_to_" + output));
		}
	}
}
