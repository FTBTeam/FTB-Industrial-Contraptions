package dev.ftb.mods.ftbic.recipe;

import dev.ftb.mods.ftbic.util.IngredientWithCount;
import dev.ftb.mods.ftbic.util.StackWithChance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.ArrayList;
import java.util.List;

public class MachineRecipe implements Recipe<NoInput> {
	public final MachineRecipeType machineType;
	public final List<IngredientWithCount> inputs;
	public final List<SizedFluidIngredient> inputFluids;
	public final List<StackWithChance> outputs;
	public final List<FluidStack> outputFluids;
	public final double processingTime;
	public final boolean hideFromJEI;

	public MachineRecipe(MachineRecipeType machineType,
			List<IngredientWithCount> inputs,
			List<SizedFluidIngredient> inputFluids,
			List<StackWithChance> outputs,
			List<FluidStack> outputFluids,
			double processingTime,
			boolean hideFromJEI) {
		this.machineType = machineType;
		this.inputs = inputs;
		this.inputFluids = inputFluids;
		this.outputs = outputs;
		this.outputFluids = outputFluids;
		this.processingTime = processingTime;
		this.hideFromJEI = hideFromJEI;
	}

	@Override
	public boolean matches(NoInput input, Level level) {
		return false;
	}

	@Override
	public ItemStack assemble(NoInput input) {
		return outputs.isEmpty() ? ItemStack.EMPTY : outputs.get(0).stack().copy();
	}

	@Override
	public String group() {
		return "";
	}

	@Override
	public boolean showNotification() {
		return false;
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public RecipeSerializer<? extends Recipe<NoInput>> getSerializer() {
		return machineType.SERIALIZER.get();
	}

	@Override
	public RecipeType<? extends Recipe<NoInput>> getType() {
		return machineType.TYPE.get();
	}

	@Override
	public PlacementInfo placementInfo() {
		return PlacementInfo.NOT_PLACEABLE;
	}

	@Override
	public RecipeBookCategory recipeBookCategory() {
		return RecipeBookCategories.CRAFTING_MISC;
	}

	public boolean isVisibleJEI() {
		return !hideFromJEI;
	}

	@Override
	public List<RecipeDisplay> display() {
		if (hideFromJEI) {
			return List.of();
		}
		List<SlotDisplay> ingredientDisplays = new ArrayList<>();
		for (IngredientWithCount in : inputs) {
			ingredientDisplays.add(in.ingredient().display());
		}
		SlotDisplay craftingStation = new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE.builtInRegistryHolder());
		List<RecipeDisplay> displays = new ArrayList<>();
		for (StackWithChance out : outputs) {
			if (out.stack().isEmpty()) {
				continue;
			}
			displays.add(new ShapelessCraftingRecipeDisplay(
					ingredientDisplays,
					new SlotDisplay.ItemStackSlotDisplay(out.template()),
					craftingStation
			));
		}
		return displays;
	}
}
