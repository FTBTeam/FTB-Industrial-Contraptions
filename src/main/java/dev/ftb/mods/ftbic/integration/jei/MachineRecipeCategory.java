package dev.ftb.mods.ftbic.integration.jei;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.recipe.MachineRecipe;
import dev.ftb.mods.ftbic.recipe.MachineRecipeType;
import dev.ftb.mods.ftbic.util.IngredientWithCount;
import dev.ftb.mods.ftbic.util.StackWithChance;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * JEI recipe category for FTBIC machine recipes. One instance per {@link MachineRecipeType}; renders
 * up to 2 input slots + up to 4 output slots, an animated progress arrow blitted from the shared
 * {@code textures/gui/base.png} atlas (the same arrow the in-world machine GUIs use), and a static
 * machine icon to the right of the arrow as a visual hint of which machine produces the recipe.
 */
public class MachineRecipeCategory extends AbstractRecipeCategory<RecipeHolder<MachineRecipe>> {
	public static final int WIDTH = 164;
	public static final int HEIGHT = 60;
	private static final Identifier BASE_TEXTURE = FTBIC.id("textures/gui/base.png");

	private final IDrawableAnimated arrow;
	private final IDrawable machineIcon;

	public MachineRecipeCategory(MachineRecipeType type, ElectricBlockInstance machine, IGuiHelper helper) {
		super(jeiRecipeType(type),
				Component.literal(machine.name),
				buildIcon(machine, helper),
				WIDTH,
				HEIGHT);
		// Animated 24×17 arrow filling left-to-right over 50 ticks, reading from the in-world GUI atlas.
		IDrawable staticArrow = helper.drawableBuilder(BASE_TEXTURE, 87, 185, 24, 17)
				.setTextureSize(256, 256).build();
		this.arrow = helper.drawableBuilder(BASE_TEXTURE, 87, 185, 24, 17)
				.setTextureSize(256, 256)
				.buildAnimated(50, IDrawableAnimated.StartDirection.LEFT, false);
		// Decorative machine icon next to the arrow.
		this.machineIcon = helper.createDrawableItemStack(new ItemStack(machine.item.get()));
	}

	@SuppressWarnings("unchecked")
	private static IRecipeHolderType<MachineRecipe> jeiRecipeType(MachineRecipeType type) {
		return IRecipeType.create((net.minecraft.world.item.crafting.RecipeType<MachineRecipe>) (net.minecraft.world.item.crafting.RecipeType<?>) type.TYPE.get());
	}

	private static IDrawable buildIcon(ElectricBlockInstance machine, IGuiHelper helper) {
		return helper.createDrawableItemStack(new ItemStack(machine.item.get()));
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<MachineRecipe> holder, IFocusGroup focuses) {
		MachineRecipe recipe = holder.value();

		int x = 4;
		for (IngredientWithCount in : recipe.inputs) {
			builder.addInputSlot(x, 14).add(in.ingredient());
			x += 20;
		}

		int ox = WIDTH - 4 - 16;
		for (StackWithChance out : recipe.outputs) {
			ItemStack stack = out.stack();
			if (stack.isEmpty()) continue;
			builder.addOutputSlot(ox, 14).add(stack);
			ox -= 20;
		}
	}

	@Override
	public void draw(RecipeHolder<MachineRecipe> recipe, mezz.jei.api.gui.ingredient.IRecipeSlotsView slots,
			GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
		int arrowX = WIDTH / 2 - 12;
		arrow.draw(graphics, arrowX, 14);
		machineIcon.draw(graphics, arrowX - 22, 14);
	}
}
