package dev.ftb.mods.ftbic.integration.jei;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.BasicGeneratorFuelRecipe;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Font;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

public class BasicGeneratorFuelCategory extends AbstractRecipeCategory<RecipeHolder<BasicGeneratorFuelRecipe>> {
	public static final int WIDTH = 110;
	public static final int HEIGHT = 36;

	public BasicGeneratorFuelCategory(IGuiHelper helper) {
		super(jeiType(),
				Component.translatable("block.ftbic.basic_generator"),
				helper.createDrawableItemStack(new ItemStack(FTBICElectricBlocks.BASIC_GENERATOR.item.get())),
				WIDTH, HEIGHT);
	}

	@SuppressWarnings("unchecked")
	private static IRecipeHolderType<BasicGeneratorFuelRecipe> jeiType() {
		return IRecipeType.create((RecipeType<BasicGeneratorFuelRecipe>) (RecipeType<?>) FTBICRecipes.BASIC_GENERATOR_FUEL.get());
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<BasicGeneratorFuelRecipe> holder, IFocusGroup focuses) {
		builder.addInputSlot(4, 9).add(holder.value().ingredient());
	}

	@Override
	public void draw(RecipeHolder<BasicGeneratorFuelRecipe> holder, mezz.jei.api.gui.ingredient.IRecipeSlotsView slots,
			GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
		Font font = Minecraft.getInstance().font;
		double seconds = holder.value().ticks() / 20.0;
		String text = String.format("Burns %.1fs", seconds);
		graphics.text(font, text, 30, 14, 0x404040, false);
	}
}
