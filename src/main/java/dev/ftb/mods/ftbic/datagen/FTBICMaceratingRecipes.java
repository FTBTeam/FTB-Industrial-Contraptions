package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.world.ResourceElementTypes;
import dev.ftb.mods.ftbic.world.ResourceElements;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class FTBICMaceratingRecipes extends FTBICRecipesGen{
	public FTBICMaceratingRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		resourceBreakDown(List.of(TIN_ORE, TIN_CHUNK), ResourceElements.TIN, ResourceElementTypes.DUST, 2, consumer);
		resourceBreakDown(List.of(LEAD_ORE, LEAD_CHUNK), ResourceElements.LEAD, ResourceElementTypes.DUST, 2, consumer);
		resourceBreakDown(List.of(IRIDIUM_ORE, IRIDIUM_CHUNK), ResourceElements.IRIDIUM, ResourceElementTypes.DUST, 2, consumer);
		resourceBreakDown(List.of(URANIUM_ORE, URANIUM_CHUNK), ResourceElements.URANIUM, ResourceElementTypes.DUST, 2, consumer);
		resourceBreakDown(List.of(ALUMINUM_ORE, ALUMINUM_CHUNK), ResourceElements.ALUMINUM, ResourceElementTypes.DUST, 2, consumer);

		resourceBreakDown(List.of(TIN_INGOT), ResourceElements.TIN, ResourceElementTypes.DUST, 1, consumer);
		resourceBreakDown(List.of(LEAD_INGOT), ResourceElements.LEAD, ResourceElementTypes.DUST, 1, consumer);
		resourceBreakDown(List.of(IRIDIUM_INGOT), ResourceElements.IRIDIUM, ResourceElementTypes.DUST, 1, consumer);
		resourceBreakDown(List.of(URANIUM_INGOT), ResourceElements.URANIUM, ResourceElementTypes.DUST, 1, consumer);
		resourceBreakDown(List.of(ALUMINUM_INGOT), ResourceElements.ALUMINUM, ResourceElementTypes.DUST, 1, consumer);
		resourceBreakDown(List.of(ENDERIUM_INGOT), ResourceElements.ENDERIUM, ResourceElementTypes.DUST,1, consumer);

		resourceBreakDown(List.of(DIAMOND), FTBICItems.DIAMOND_DUST.get(),1, consumer);
	}

	private static void resourceBreakDown(List<TagKey<Item>> input, ResourceElements element, ResourceElementTypes type, int count, Consumer<FinishedRecipe> consumer) {
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
