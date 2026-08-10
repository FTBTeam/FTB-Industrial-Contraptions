package dev.ftb.mods.industrialcontraptions.integration.jei;

import dev.ftb.mods.industrialcontraptions.ICConfig;
import dev.ftb.mods.industrialcontraptions.block.ICElectricBlocks;
import dev.ftb.mods.industrialcontraptions.item.ICItems;
import dev.ftb.mods.industrialcontraptions.recipe.BasicGeneratorFuelRecipe;
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

public class BasicGeneratorFuelCategory extends AbstractRecipeCategory<RecipeHolder<BasicGeneratorFuelRecipe>> {
	public static final int WIDTH = 148;
	public static final int HEIGHT = 26;

	public BasicGeneratorFuelCategory(IGuiHelper helper) {
		super(jeiType(),
				Component.translatable("block.ic.basic_generator"),
				helper.createDrawableItemStack(new ItemStack(ICElectricBlocks.BASIC_GENERATOR.item.get())),
				WIDTH, HEIGHT);
	}

	@SuppressWarnings("unchecked")
	private static IRecipeHolderType<BasicGeneratorFuelRecipe> jeiType() {
		return IRecipeType.create((RecipeType<BasicGeneratorFuelRecipe>) (RecipeType<?>) ICRecipes.BASIC_GENERATOR_FUEL.get());
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<BasicGeneratorFuelRecipe> holder, IFocusGroup focuses) {
		builder.addInputSlot(4, 4)
				.setStandardSlotBackground()
				.add(holder.value().ingredient());
	}

	@Override
	public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<BasicGeneratorFuelRecipe> holder, IFocusGroup focuses) {
		int ticks = holder.value().ticks();
		double seconds = ticks / 20.0D;
		double zapsPerTick = ICItems.safeGet(
				ICConfig.MACHINES.BASIC_GENERATOR_OUTPUT, 10D);
		long totalZaps = Math.round(zapsPerTick * ticks);

		builder.addAnimatedRecipeFlame(300).setPosition(26, 5);

		builder.addText(Component.translatable("ic.jei.burn_time",
						ICUtils.fmtDouble(seconds, 1), ICUtils.fmtDouble(zapsPerTick, 0)), 100, 9)
				.setPosition(44, 3)
				.setColor(0xFF404040);
		builder.addText(Component.translatable("ic.jei.total_zaps", ICUtils.fmtInt(totalZaps)), 100, 9)
				.setPosition(44, 14)
				.setColor(0xFF0A7F0A);
	}
}
