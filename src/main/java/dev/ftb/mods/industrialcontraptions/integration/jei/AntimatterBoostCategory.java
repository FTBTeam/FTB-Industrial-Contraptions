package dev.ftb.mods.industrialcontraptions.integration.jei;

import dev.ftb.mods.industrialcontraptions.block.ICElectricBlocks;
import dev.ftb.mods.industrialcontraptions.recipe.AntimatterBoostRecipe;
import dev.ftb.mods.industrialcontraptions.recipe.ICRecipes;
import dev.ftb.mods.industrialcontraptions.util.ICUtils;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

public class AntimatterBoostCategory extends AbstractRecipeCategory<RecipeHolder<AntimatterBoostRecipe>> {
	public static final int WIDTH = 110;
	public static final int HEIGHT = 26;

	public AntimatterBoostCategory(IGuiHelper helper) {
		super(jeiType(),
				Component.translatable("block.ic.antimatter_constructor"),
				helper.createDrawableItemStack(new ItemStack(ICElectricBlocks.ANTIMATTER_CONSTRUCTOR.item.get())),
				WIDTH, HEIGHT);
	}

	@SuppressWarnings("unchecked")
	private static IRecipeHolderType<AntimatterBoostRecipe> jeiType() {
		return IRecipeType.create((RecipeType<AntimatterBoostRecipe>) (RecipeType<?>) ICRecipes.ANTIMATTER_BOOST.get());
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<AntimatterBoostRecipe> holder, IFocusGroup focuses) {
		builder.addInputSlot(4, 4)
				.setStandardSlotBackground()
				.add(holder.value().ingredient());
	}

	@Override
	public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<AntimatterBoostRecipe> holder, IFocusGroup focuses) {
		builder.addText(Component.translatable("ic.jei.boost", ICUtils.fmtInt(Math.round(holder.value().boost()))), 80, 9)
				.setPosition(26, 9)
				.setColor(0xFF0A7F0A);
	}
}
