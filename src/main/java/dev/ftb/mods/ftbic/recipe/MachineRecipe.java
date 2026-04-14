package dev.ftb.mods.ftbic.recipe;

import dev.ftb.mods.ftbic.util.IngredientWithCount;
import dev.ftb.mods.ftbic.util.StackWithChance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.List;

/**
 * Data-carrier recipe type for all FTBIC machines (macerator, compressor, centrifuge, etc.).
 *
 * Machine BEs call `matches(NoInput, level)` symbolically; the actual input-slot check happens inside
 * the BE's process loop by scanning `inputs` against its internal ItemStack array. This matches the
 * 1.18.2 behaviour where MachineRecipe.matches always returned false (recipes are looked up by
 * scanning the BE's input buffer, not by Minecraft's recipe manager).
 */
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
}
