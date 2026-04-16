package dev.ftb.mods.ftbic.integration.jei;

import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.recipe.MachineRecipe;
import dev.ftb.mods.ftbic.recipe.MachineRecipeType;
import dev.ftb.mods.ftbic.util.IngredientWithCount;
import dev.ftb.mods.ftbic.util.StackWithChance;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class MachineRecipeCategory extends AbstractRecipeCategory<RecipeHolder<MachineRecipe>> {
	public static final int WIDTH = 112;
	public static final int HEIGHT = 26;

	private static final int INPUT_X_1 = 22;
	private static final int ARROW_X = 44;
	private static final int ARROW_Y = 5;
	private static final int ARROW_W = 24;
	private static final int ARROW_H = 17;
	private static final int OUTPUT_X_0 = 72;
	private static final int SLOT_Y = 4;

	private final ElectricBlockInstance machine;

	public MachineRecipeCategory(MachineRecipeType type, ElectricBlockInstance machine, IGuiHelper helper) {
		super(jeiRecipeType(type),
				Component.literal(machine.name),
				helper.createDrawableItemStack(new ItemStack(machine.item.get())),
				WIDTH,
				HEIGHT);
		this.machine = machine;
	}

	@SuppressWarnings("unchecked")
	private static IRecipeHolderType<MachineRecipe> jeiRecipeType(MachineRecipeType type) {
		return IRecipeType.create((net.minecraft.world.item.crafting.RecipeType<MachineRecipe>) (net.minecraft.world.item.crafting.RecipeType<?>) type.TYPE.get());
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<MachineRecipe> holder, IFocusGroup focuses) {
		MachineRecipe recipe = holder.value();

		int inputCount = Math.min(2, recipe.inputs.size());
		int idx = 0;
		for (IngredientWithCount in : recipe.inputs) {
			int x = INPUT_X_1 - (inputCount - 1 - idx) * 18;
			builder.addInputSlot(x, SLOT_Y)
					.setStandardSlotBackground()
					.add(in.ingredient());
			idx++;
			if (idx >= 2) break;
		}

		int ox = OUTPUT_X_0;
		for (StackWithChance out : recipe.outputs) {
			ItemStack stack = out.stack();
			if (stack.isEmpty()) continue;
			builder.addOutputSlot(ox, SLOT_Y)
					.setOutputSlotBackground()
					.add(stack);
			ox += 18;
		}
	}

	@Override
	public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<MachineRecipe> holder, IFocusGroup focuses) {
		builder.addAnimatedRecipeArrow(50).setPosition(ARROW_X, ARROW_Y);
	}

	@Override
	public void getTooltip(ITooltipBuilder tooltip, RecipeHolder<MachineRecipe> recipe, IRecipeSlotsView slots, double mouseX, double mouseY) {
		if (mouseX >= ARROW_X && mouseX < ARROW_X + ARROW_W && mouseY >= ARROW_Y && mouseY < ARROW_Y + ARROW_H) {
			double baseTicks = dev.ftb.mods.ftbic.FTBICConfig.MACHINES.MACHINE_RECIPE_BASE_TICKS.get();
			double ticks = recipe.value().processingTime * baseTicks;
			double energyPerTick = machine.energyUsage.get();
			long zaps = Math.round(ticks * energyPerTick);
			double seconds = ticks / 20.0D;
			tooltip.add(Component.literal(String.format("%.1fs · %,d zaps", seconds, zaps)));
			tooltip.add(Component.literal(String.format("%.0f z/t", energyPerTick))
					.withStyle(net.minecraft.ChatFormatting.DARK_GRAY));
		}
	}
}
