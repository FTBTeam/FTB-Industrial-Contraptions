package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.world.ResourceType;
import dev.ftb.mods.ftbic.world.ResourceElements;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.function.Consumer;

public class FTBICMaceratingRecipes extends FTBICRecipesGen{
	public FTBICMaceratingRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		resourceBreakDown(List.of(TIN_ORE, TIN_CHUNK), ResourceElements.TIN, ResourceType.DUST, 2, consumer);
		resourceBreakDown(List.of(LEAD_ORE, LEAD_CHUNK), ResourceElements.LEAD, ResourceType.DUST, 2, consumer);
		resourceBreakDown(List.of(IRIDIUM_ORE, IRIDIUM_CHUNK), ResourceElements.IRIDIUM, ResourceType.DUST, 2, consumer);
		resourceBreakDown(List.of(URANIUM_ORE, URANIUM_CHUNK), ResourceElements.URANIUM, ResourceType.DUST, 2, consumer);
		resourceBreakDown(List.of(ALUMINUM_ORE, ALUMINUM_CHUNK), ResourceElements.ALUMINUM, ResourceType.DUST, 2, consumer);
		resourceBreakDown(List.of(IRON_ORE, IRON_RAW), ResourceElements.IRON, ResourceType.DUST, 2, consumer);
		resourceBreakDown(List.of(GOLD_ORE, GOLD_RAW), ResourceElements.GOLD, ResourceType.DUST, 2, consumer);
		resourceBreakDown(List.of(COPPER_ORE, COPPER_RAW), ResourceElements.COPPER, ResourceType.DUST, 2, consumer);

		resourceBreakDown(List.of(TIN_INGOT), ResourceElements.TIN, ResourceType.DUST, 1, consumer);
		resourceBreakDown(List.of(LEAD_INGOT), ResourceElements.LEAD, ResourceType.DUST, 1, consumer);
		resourceBreakDown(List.of(IRIDIUM_INGOT), ResourceElements.IRIDIUM, ResourceType.DUST, 1, consumer);
		resourceBreakDown(List.of(URANIUM_INGOT), ResourceElements.URANIUM, ResourceType.DUST, 1, consumer);
		resourceBreakDown(List.of(ALUMINUM_INGOT), ResourceElements.ALUMINUM, ResourceType.DUST, 1, consumer);
		resourceBreakDown(List.of(ENDERIUM_INGOT), ResourceElements.ENDERIUM, ResourceType.DUST,1, consumer);
		resourceBreakDown(List.of(IRON_INGOT), ResourceElements.IRON, ResourceType.DUST,1, consumer);
		resourceBreakDown(List.of(GOLD_INGOT), ResourceElements.GOLD, ResourceType.DUST,1, consumer);
		resourceBreakDown(List.of(COPPER_INGOT), ResourceElements.COPPER, ResourceType.DUST,1, consumer);
		resourceBreakDown(List.of(BRONZE_INGOT), ResourceElements.BRONZE, ResourceType.DUST,1, consumer);

		resourceBreakDown(List.of(DIAMOND), ResourceElements.DIAMOND, ResourceType.DUST,1, consumer);
	}

	private static void resourceBreakDown(List<TagKey<Item>> input, ResourceElements element, ResourceType type, int count, Consumer<FinishedRecipe> consumer) {
		for (TagKey<Item> item : input) {
			MachineRecipeBuilder.macerating()
					.unlockedBy("has_item", has(item))
					.inputItem(Ingredient.of(item))
					.outputItem(new ItemStack(FTBICItems.getResourceFromType(element, type).orElseThrow().get(), count))
					.save(consumer, maceratingLoc(item.location().getPath() + "_to_" + type.name().toLowerCase()));
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
