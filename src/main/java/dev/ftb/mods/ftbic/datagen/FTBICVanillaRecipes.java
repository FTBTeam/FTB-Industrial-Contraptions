package dev.ftb.mods.ftbic.datagen;

import com.ridanisaurus.emendatusenigmatica.registries.ItemHandler;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Consumer;

public class FTBICVanillaRecipes extends FTBICRecipesGen {
	public FTBICVanillaRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		ShapelessRecipeBuilder.shapeless(SULFUR_DUST_ITEM)
				.unlockedBy("has_item", has(SULFUR))
				.group(MODID + ":sulfur")
				.requires(SULFUR)
				.requires(ItemHandler.ENIGMATIC_HAMMER.get())
				.save(consumer, shapelessLoc("sulfur_from_hammer"));

		MachineRecipeBuilder.macerating()
				.unlockedBy("has_item", has(BLAZE_ROD))
				.inputItem(Ingredient.of(BLAZE_ROD))
				.outputItem(new ItemStack(Items.BLAZE_POWDER, 5))
				.save(consumer, maceratingLoc("blaze_powder"));

		MachineRecipeBuilder.macerating()
				.unlockedBy("has_item", has(Items.BONE))
				.inputItem(Ingredient.of(Items.BONE))
				.outputItem(new ItemStack(Items.BONE_MEAL, 5))
				.save(consumer, maceratingLoc("bone_meal"));

		MachineRecipeBuilder.macerating()
				.unlockedBy("has_item", has(COBBLESTONE))
				.inputItem(Ingredient.of(COBBLESTONE))
				.outputItem(new ItemStack(Items.GRAVEL))
				.save(consumer, maceratingLoc("gravel"));

		MachineRecipeBuilder.macerating()
				.unlockedBy("has_item", has(STONE))
				.inputItem(Ingredient.of(STONE))
				.outputItem(new ItemStack(Items.COBBLESTONE))
				.save(consumer, maceratingLoc("cobblestone"));

		MachineRecipeBuilder.macerating()
				.unlockedBy("has_item", has(GRAVEL))
				.inputItem(Ingredient.of(GRAVEL))
				.outputItem(new ItemStack(Items.SAND))
				.save(consumer, maceratingLoc("sand"));

		MachineRecipeBuilder.macerating()
				.unlockedBy("has_item", has(Items.SNOW_BLOCK))
				.inputItem(Ingredient.of(Items.SNOW_BLOCK))
				.outputItem(new ItemStack(Items.SNOWBALL))
				.save(consumer, maceratingLoc("snowball"));

		MachineRecipeBuilder.macerating()
				.unlockedBy("has_item", has(WOOL))
				.inputItem(Ingredient.of(WOOL))
				.outputItem(new ItemStack(Items.STRING, 4))
				.save(consumer, maceratingLoc("string"));

		MachineRecipeBuilder.macerating()
				.unlockedBy("has_item", has(OBSIDIAN))
				.inputItem(Ingredient.of(OBSIDIAN))
				.outputItem(new ItemStack(OBSIDIAN_DUST_ITEM))
				.save(consumer, maceratingLoc("obsidian_dust"));

		MachineRecipeBuilder.macerating()
				.unlockedBy("has_item", has(ENDER_PEARL))
				.inputItem(Ingredient.of(ENDER_PEARL))
				.outputItem(new ItemStack(ENDER_DUST_ITEM))
				.save(consumer, maceratingLoc("ender_dust"));

		MachineRecipeBuilder.macerating()
				.unlockedBy("has_item", has(PLANKS))
				.inputItem(Ingredient.of(PLANKS))
				.outputItem(new ItemStack(SAWDUST_ITEM))
				.save(consumer, maceratingLoc("sawdust"));

		MachineRecipeBuilder.macerating()
				.unlockedBy("has_item", has(Items.COAL))
				.inputItem(Ingredient.of(Items.COAL))
				.outputItem(new ItemStack(COAL_DUST_ITEM))
				.save(consumer, maceratingLoc("coal_dust"));

		MachineRecipeBuilder.macerating()
				.unlockedBy("has_item", has(Items.CHARCOAL))
				.inputItem(Ingredient.of(Items.CHARCOAL))
				.outputItem(new ItemStack(CHARCOAL_DUST_ITEM))
				.save(consumer, maceratingLoc("charcoal_dust"));

		MachineRecipeBuilder.macerating()
				.unlockedBy("has_item", has(COAL_ORE))
				.inputItem(Ingredient.of(COAL_ORE))
				.outputItem(new ItemStack(Items.COAL, 2))
				.save(consumer, maceratingLoc("coal_from_ore"));

		MachineRecipeBuilder.separating()
				.unlockedBy("has_item", has(GRAVEL))
				.inputItem(Ingredient.of(GRAVEL))
				.outputItem(new ItemStack(Items.FLINT))
				.save(consumer, separatingLoc("flint"));

		MachineRecipeBuilder.separating()
				.unlockedBy("has_item", has(QUARTZ))
				.inputItem(Ingredient.of(QUARTZ))
				.outputItem(new ItemStack(SILICON_ITEM, 3))
				.save(consumer, separatingLoc("silicon_from_quartz"));

		MachineRecipeBuilder.separating()
				.unlockedBy("has_item", has(SAND))
				.inputItem(Ingredient.of(SAND))
				.outputItem(new ItemStack(SILICON_ITEM), 0.1)
				.save(consumer, separatingLoc("silicon_from_sand"));

		MachineRecipeBuilder.separating()
				.unlockedBy("has_item", has(Items.SEA_PICKLE))
				.inputItem(Ingredient.of(Items.SEA_PICKLE))
				.outputItem(new ItemStack(Items.SEAGRASS), 0.5)
				.outputItem(new ItemStack(Items.GLOWSTONE_DUST), 0.03)
				.save(consumer, separatingLoc("glowstone_dust"));

		MachineRecipeBuilder.separating()
				.unlockedBy("has_item", has(Items.MAGMA_CREAM))
				.inputItem(Ingredient.of(Items.MAGMA_CREAM))
				.outputItem(new ItemStack(Items.SLIME_BALL))
				.outputItem(new ItemStack(Items.BLAZE_POWDER), 0.25D)
				.save(consumer, separatingLoc("slime_ball"));

		MachineRecipeBuilder.separating()
				.unlockedBy("has_item", has(Items.SUGAR_CANE))
				.inputItem(Ingredient.of(Items.SUGAR_CANE))
				.outputItem(new ItemStack(Items.SUGAR, 2))
				.outputItem(new ItemStack(Items.PAPER), 0.5D)
				.save(consumer, separatingLoc("sugar"));

		MachineRecipeBuilder.compressing()
				.unlockedBy("has_item", has(WATER_CELL))
				.inputItem(Ingredient.of(WATER_CELL))
				.outputItem(new ItemStack(Items.SNOWBALL))
				.save(consumer, compressingLoc("snowball"));

		MachineRecipeBuilder.compressing()
				.unlockedBy("has_item", has(Items.SNOWBALL))
				.inputItem(Ingredient.of(Items.SNOWBALL))
				.outputItem(new ItemStack(Items.ICE))
				.save(consumer, compressingLoc("ice"));

		MachineRecipeBuilder.compressing()
				.unlockedBy("has_item", has(GRAPHENE))
				.inputItem(Ingredient.of(GRAPHENE))
				.outputItem(new ItemStack(Items.DIAMOND))
				.save(consumer, compressingLoc("diamond"));

		MachineRecipeBuilder.compressing()
				.unlockedBy("has_item", has(Items.SUGAR_CANE))
				.inputItem(Ingredient.of(Items.SUGAR_CANE), 3)
				.outputItem(new ItemStack(Items.PAPER, 5))
				.save(consumer, compressingLoc("paper"));
	}
}
