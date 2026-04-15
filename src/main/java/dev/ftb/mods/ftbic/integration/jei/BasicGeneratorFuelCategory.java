package dev.ftb.mods.ftbic.integration.jei;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.BasicGeneratorFuelRecipe;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

public class BasicGeneratorFuelCategory extends AbstractRecipeCategory<RecipeHolder<BasicGeneratorFuelRecipe>> {
	public static final int WIDTH = 148;
	public static final int HEIGHT = 26;

	private final IDrawableAnimated flame;

	public BasicGeneratorFuelCategory(IGuiHelper helper) {
		super(jeiType(),
				Component.translatable("block.ftbic.basic_generator"),
				helper.createDrawableItemStack(new ItemStack(FTBICElectricBlocks.BASIC_GENERATOR.item.get())),
				WIDTH, HEIGHT);
		this.flame = helper.createAnimatedRecipeFlame(300);
	}

	@SuppressWarnings("unchecked")
	private static IRecipeHolderType<BasicGeneratorFuelRecipe> jeiType() {
		return IRecipeType.create((RecipeType<BasicGeneratorFuelRecipe>) (RecipeType<?>) FTBICRecipes.BASIC_GENERATOR_FUEL.get());
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<BasicGeneratorFuelRecipe> holder, IFocusGroup focuses) {
		builder.addInputSlot(4, 4)
				.setStandardSlotBackground()
				.add(holder.value().ingredient());
	}

	@Override
	public void draw(RecipeHolder<BasicGeneratorFuelRecipe> holder, mezz.jei.api.gui.ingredient.IRecipeSlotsView slots,
			GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
		flame.draw(graphics, 26, 5);

		Font font = Minecraft.getInstance().font;
		int ticks = holder.value().ticks();
		double seconds = ticks / 20.0D;
		double zapsPerTick = dev.ftb.mods.ftbic.item.FTBICItems.safeGet(
				dev.ftb.mods.ftbic.FTBICConfig.MACHINES.BASIC_GENERATOR_OUTPUT, 10D);
		long totalZaps = Math.round(zapsPerTick * ticks);
		graphics.text(font, String.format("%.1fs · %.0f z/t", seconds, zapsPerTick), 44, 4, 0x404040, false);
		graphics.text(font, String.format("%d zaps total", totalZaps), 44, 14, 0x404040, false);
	}
}
