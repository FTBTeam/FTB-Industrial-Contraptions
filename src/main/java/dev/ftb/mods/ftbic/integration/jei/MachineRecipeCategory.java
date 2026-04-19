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
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

public class MachineRecipeCategory extends AbstractRecipeCategory<RecipeHolder<MachineRecipe>> {
	public static final int WIDTH = 112;
	public static final int HEIGHT = 26;

	private static final int INPUT_X_1 = 22;
	private static final int ARROW_X = 44;
	private static final int ARROW_Y = 5;
	private static final int OUTPUT_X_0 = 72;
	private static final int SLOT_Y = 4;
	private static final int ARROW_HIT_X0 = INPUT_X_1 + 18;
	private static final int ARROW_HIT_X1 = OUTPUT_X_0;
	private static final int ARROW_HIT_Y0 = SLOT_Y;
	private static final int ARROW_HIT_Y1 = SLOT_Y + 18;

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
			var slot = builder.addInputSlot(x, SLOT_Y).setStandardSlotBackground();
			int cnt = in.count();
			if (cnt > 1) {
				List<ItemStack> stacks = new ArrayList<>();
				in.ingredient().items().forEach(h -> {
					ItemStack stack = new ItemStack(h);
					stack.setCount(cnt);
					stacks.add(stack);
				});
				slot.addItemStacks(stacks);
			} else {
				slot.add(in.ingredient());
			}
			idx++;
			if (idx >= 2) break;
		}

		int visibleOutputs = 0;
		for (StackWithChance out : recipe.outputs) {
			if (!out.stack().isEmpty()) visibleOutputs++;
		}
		boolean singleOutput = visibleOutputs == 1;
		int ox = OUTPUT_X_0;
		for (StackWithChance out : recipe.outputs) {
			ItemStack stack = out.stack();
			if (stack.isEmpty()) continue;
			var slot = builder.addOutputSlot(ox, SLOT_Y);
			if (singleOutput) slot.setOutputSlotBackground();
			else slot.setStandardSlotBackground();
			slot.add(stack);
			double chance = out.chance();
			if (chance < 1.0D) {
				slot.addRichTooltipCallback((slotView, tooltip) ->
						tooltip.add(Component.literal(String.format("Chance: %.1f%%", chance * 100D))
								.withStyle(ChatFormatting.GRAY)));
			}
			ox += 18;
		}
	}

	@Override
	public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<MachineRecipe> holder, IFocusGroup focuses) {
		builder.addAnimatedRecipeArrow(50).setPosition(ARROW_X, ARROW_Y);
	}

	@Override
	public void getTooltip(ITooltipBuilder tooltip, RecipeHolder<MachineRecipe> recipe, IRecipeSlotsView slots, double mouseX, double mouseY) {
		if (mouseX >= ARROW_HIT_X0 && mouseX < ARROW_HIT_X1 && mouseY >= ARROW_HIT_Y0 && mouseY < ARROW_HIT_Y1) {
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
