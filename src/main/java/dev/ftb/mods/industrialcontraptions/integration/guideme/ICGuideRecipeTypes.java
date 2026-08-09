package dev.ftb.mods.industrialcontraptions.integration.guideme;

import dev.ftb.mods.industrialcontraptions.ICConfig;
import dev.ftb.mods.industrialcontraptions.block.ElectricBlockInstance;
import dev.ftb.mods.industrialcontraptions.block.ICElectricBlocks;
import dev.ftb.mods.industrialcontraptions.recipe.AntimatterBoostRecipe;
import dev.ftb.mods.industrialcontraptions.recipe.BasicGeneratorFuelRecipe;
import dev.ftb.mods.industrialcontraptions.recipe.ICRecipes;
import dev.ftb.mods.industrialcontraptions.recipe.MachineRecipe;
import dev.ftb.mods.industrialcontraptions.recipe.MachineRecipeType;
import dev.ftb.mods.industrialcontraptions.util.IngredientWithCount;
import dev.ftb.mods.industrialcontraptions.util.StackWithChance;
import guideme.compiler.tags.RecipeTypeMappingSupplier;
import guideme.document.block.LytParagraph;
import guideme.document.block.LytSlotGrid;
import guideme.document.block.recipes.LytStandardRecipeBox;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.List;

public class ICGuideRecipeTypes implements RecipeTypeMappingSupplier {
	@Override
	public void collect(RecipeTypeMappings mappings) {
		mappings.add(ICRecipes.SMELTING.TYPE.get(),
				h -> buildMachineBox(ICRecipes.SMELTING, ICElectricBlocks.POWERED_FURNACE, h));
		mappings.add(ICRecipes.MACERATING.TYPE.get(),
				h -> buildMachineBox(ICRecipes.MACERATING, ICElectricBlocks.MACERATOR, h));
		mappings.add(ICRecipes.SEPARATING.TYPE.get(),
				h -> buildMachineBox(ICRecipes.SEPARATING, ICElectricBlocks.CENTRIFUGE, h));
		mappings.add(ICRecipes.COMPRESSING.TYPE.get(),
				h -> buildMachineBox(ICRecipes.COMPRESSING, ICElectricBlocks.COMPRESSOR, h));
		mappings.add(ICRecipes.REPROCESSING.TYPE.get(),
				h -> buildMachineBox(ICRecipes.REPROCESSING, ICElectricBlocks.REPROCESSOR, h));
		mappings.add(ICRecipes.CANNING.TYPE.get(),
				h -> buildMachineBox(ICRecipes.CANNING, ICElectricBlocks.CANNING_MACHINE, h));
		mappings.add(ICRecipes.ROLLING.TYPE.get(),
				h -> buildMachineBox(ICRecipes.ROLLING, ICElectricBlocks.ROLLER, h));
		mappings.add(ICRecipes.EXTRUDING.TYPE.get(),
				h -> buildMachineBox(ICRecipes.EXTRUDING, ICElectricBlocks.EXTRUDER, h));
		mappings.add(ICRecipes.ALLOY_SMELTING.TYPE.get(),
				h -> buildMachineBox(ICRecipes.ALLOY_SMELTING, ICElectricBlocks.ALLOY_SMELTER, h));

		@SuppressWarnings("unchecked")
		RecipeType<BasicGeneratorFuelRecipe> fuelType =
				(RecipeType<BasicGeneratorFuelRecipe>)
						(RecipeType<?>) ICRecipes.BASIC_GENERATOR_FUEL.get();
		mappings.add(fuelType, this::buildFuelBox);

		@SuppressWarnings("unchecked")
		RecipeType<AntimatterBoostRecipe> boostType =
				(RecipeType<AntimatterBoostRecipe>)
						(RecipeType<?>) ICRecipes.ANTIMATTER_BOOST.get();
		mappings.add(boostType, this::buildBoostBox);
	}

	private LytStandardRecipeBox<MachineRecipe> buildMachineBox(MachineRecipeType type,
			ElectricBlockInstance machine, RecipeHolder<MachineRecipe> holder) {
		MachineRecipe recipe = holder.value();

		List<Ingredient> inputIngredients = new ArrayList<>();
		for (IngredientWithCount in : recipe.inputs) {
			inputIngredients.add(in.ingredient());
		}
		LytSlotGrid inputGrid = inputIngredients.isEmpty()
				? new LytSlotGrid(1, 1)
				: LytSlotGrid.rowFromIngredients(inputIngredients, true);

		List<ItemStackTemplate> outputTemplates = new ArrayList<>();
		for (StackWithChance out : recipe.outputs) {
			if (!out.stack().isEmpty()) {
				outputTemplates.add(out.template());
			}
		}
		LytSlotGrid outputGrid = outputTemplates.isEmpty()
				? new LytSlotGrid(1, 1)
				: LytSlotGrid.rowFromStacks(outputTemplates, true);

		LytStandardRecipeBox.Builder builder = LytStandardRecipeBox.<MachineRecipe>builder()
				.icon(machine.item.get())
				.title(machine.name)
				.input(inputGrid)
				.output(outputGrid);

		double baseTicks = ICConfig.MACHINES.MACHINE_RECIPE_BASE_TICKS.get();
		double ticks = recipe.processingTime * baseTicks;
		double energyPerTick = machine.energyUsage.get();
		if (ticks > 0D) {
			double seconds = ticks / 20.0D;
			if (energyPerTick > 0D) {
				long totalZaps = Math.round(ticks * energyPerTick);
				builder.addBottom(paragraph(String.format("%.1fs · %,d zaps · %.0f z/t", seconds, totalZaps, energyPerTick)));
			} else {
				builder.addBottom(paragraph(String.format("%.1fs", seconds)));
			}
		}

		for (StackWithChance out : recipe.outputs) {
			ItemStack stack = out.stack();
			if (stack.isEmpty() || out.chance() >= 1.0D) {
				continue;
			}
			builder.addBottom(paragraph(String.format("%.0f%% chance: %s",
					out.chance() * 100D, stack.getHoverName().getString())));
		}

		return builder.build(holder);
	}

	private static LytParagraph paragraph(String text) {
		LytParagraph p = new LytParagraph();
		p.appendText(text);
		return p;
	}

	private LytStandardRecipeBox<BasicGeneratorFuelRecipe> buildFuelBox(RecipeHolder<BasicGeneratorFuelRecipe> holder) {
		BasicGeneratorFuelRecipe recipe = holder.value();
		LytSlotGrid inputGrid = LytSlotGrid.rowFromIngredients(List.of(recipe.ingredient()), true);
		double seconds = recipe.ticks() / 20.0D;
		double zapsPerTick = ICElectricBlocks.BASIC_GENERATOR.maxEnergyOutput.get();
		long totalZaps = Math.round(zapsPerTick * recipe.ticks());
		return LytStandardRecipeBox.<BasicGeneratorFuelRecipe>builder()
				.icon(ICElectricBlocks.BASIC_GENERATOR.item.get())
				.title(ICElectricBlocks.BASIC_GENERATOR.name)
				.input(inputGrid)
				.output(new LytSlotGrid(1, 1))
				.addBottom(paragraph(String.format("%.1fs @ %.0f z/t", seconds, zapsPerTick)))
				.addBottom(paragraph(String.format("= %,d zaps", totalZaps)))
				.build(holder);
	}

	private LytStandardRecipeBox<AntimatterBoostRecipe> buildBoostBox(RecipeHolder<AntimatterBoostRecipe> holder) {
		AntimatterBoostRecipe recipe = holder.value();
		LytSlotGrid inputGrid = LytSlotGrid.rowFromIngredients(List.of(recipe.ingredient()), true);
		return LytStandardRecipeBox.<AntimatterBoostRecipe>builder()
				.icon(ICElectricBlocks.ANTIMATTER_CONSTRUCTOR.item.get())
				.title(ICElectricBlocks.ANTIMATTER_CONSTRUCTOR.name)
				.input(inputGrid)
				.output(new LytSlotGrid(1, 1))
				.addBottom(paragraph(String.format("+%.0f boost", recipe.boost())))
				.build(holder);
	}
}
