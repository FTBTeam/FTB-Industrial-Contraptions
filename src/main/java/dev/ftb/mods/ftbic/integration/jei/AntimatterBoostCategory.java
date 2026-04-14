package dev.ftb.mods.ftbic.integration.jei;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.AntimatterBoostRecipe;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
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

public class AntimatterBoostCategory extends AbstractRecipeCategory<RecipeHolder<AntimatterBoostRecipe>> {
	public static final int WIDTH = 110;
	public static final int HEIGHT = 36;

	public AntimatterBoostCategory(IGuiHelper helper) {
		super(jeiType(),
				Component.translatable("block.ftbic.antimatter_constructor"),
				helper.createDrawableItemStack(new ItemStack(FTBICElectricBlocks.ANTIMATTER_CONSTRUCTOR.item.get())),
				WIDTH, HEIGHT);
	}

	@SuppressWarnings("unchecked")
	private static IRecipeHolderType<AntimatterBoostRecipe> jeiType() {
		return IRecipeType.create((RecipeType<AntimatterBoostRecipe>) (RecipeType<?>) FTBICRecipes.ANTIMATTER_BOOST.get());
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<AntimatterBoostRecipe> holder, IFocusGroup focuses) {
		builder.addInputSlot(4, 9).add(holder.value().ingredient());
	}

	@Override
	public void draw(RecipeHolder<AntimatterBoostRecipe> holder, mezz.jei.api.gui.ingredient.IRecipeSlotsView slots,
			GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
		Font font = Minecraft.getInstance().font;
		String text = String.format("+%.0f zaps", holder.value().boost());
		graphics.text(font, text, 30, 14, 0x404040, false);
	}
}
