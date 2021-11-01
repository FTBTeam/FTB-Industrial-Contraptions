package dev.ftb.mods.ftbic.jei;


import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.recipe.MachineRecipe;
import dev.ftb.mods.ftbic.recipe.MachineRecipeSerializer;
import dev.ftb.mods.ftbic.util.IngredientWithCount;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author LatvianModder
 */
public class MachineRecipeCategory implements IRecipeCategory<MachineRecipe> {
	public final MachineRecipeSerializer serializer;
	public final ElectricBlockInstance electricBlockInstance;
	public final ResourceLocation texture;
	private final IDrawable background;
	private final IDrawable icon;
	private final IDrawableAnimated animatedArrow;
	private final IDrawableAnimated animatedPower;

	public MachineRecipeCategory(Supplier<MachineRecipeSerializer> s, IGuiHelper guiHelper, ElectricBlockInstance item) {
		serializer = s.get();
		electricBlockInstance = item;
		texture = new ResourceLocation(FTBIC.MOD_ID + ":textures/gui/" + serializer.getRegistryName().getPath() + "_jei.png");
		background = guiHelper.drawableBuilder(texture, 0, 0, serializer.jeiWidth, serializer.jeiHeight).setTextureSize(128, 64).build();
		icon = guiHelper.createDrawableIngredient(new ItemStack(item.item.get()));
		animatedArrow = guiHelper.drawableBuilder(texture, 104, 0, 24, 17).setTextureSize(128, 64).buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
		animatedPower = guiHelper.createAnimatedDrawable(guiHelper.drawableBuilder(texture, 114, 18, 14, 14).setTextureSize(128, 64).build(), 84, IDrawableAnimated.StartDirection.TOP, true);
	}

	@Override
	public ResourceLocation getUid() {
		return serializer.getRegistryName();
	}

	@Override
	public Class<? extends MachineRecipe> getRecipeClass() {
		return MachineRecipe.class;
	}

	@Override
	public String getTitle() {
		return I18n.get("recipe." + FTBIC.MOD_ID + "." + serializer.getRegistryName().getPath());
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(MachineRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
		animatedPower.draw(matrixStack, serializer.jeiPowerX, serializer.jeiPowerY);
		animatedArrow.draw(matrixStack, serializer.jeiArrowX, serializer.jeiArrowY);
	}

	@Override
	public void setIngredients(MachineRecipe recipe, IIngredients ingredients) {
		List<List<ItemStack>> inputItems = new ArrayList<>();

		for (IngredientWithCount i : recipe.inputItems) {
			List<ItemStack> stackList = new ArrayList<>();

			for (ItemStack is : i.ingredient.getItems()) {
				ItemStack is1 = is.copy();
				is1.setCount(i.count);
				stackList.add(is1);
			}

			inputItems.add(stackList);
		}

		ingredients.setInputLists(VanillaTypes.ITEM, inputItems);
		ingredients.setInputs(VanillaTypes.FLUID, recipe.inputFluids);
		ingredients.setOutputs(VanillaTypes.ITEM, recipe.outputItems.stream().map(i -> i.stack).collect(Collectors.toList()));
		ingredients.setOutputs(VanillaTypes.FLUID, recipe.outputFluids);
	}

	@Override
	public void setRecipe(IRecipeLayout layout, MachineRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup itemStacks = layout.getItemStacks();
		IGuiFluidStackGroup fluidStacks = layout.getFluidStacks();

		for (int i = 0; i < recipe.inputFluids.size(); i++) {
			fluidStacks.init(i, true, 1 + i * 18, 1);
		}

		for (int i = 0; i < recipe.inputItems.size(); i++) {
			itemStacks.init(i, true, (i + recipe.inputFluids.size()) * 18, 0);
		}

		for (int i = 0; i < recipe.outputFluids.size(); i++) {
			fluidStacks.init(i + recipe.inputFluids.size(), false, 61 + i * 18, 19);
		}

		for (int i = 0; i < recipe.outputItems.size(); i++) {
			itemStacks.init(i + recipe.inputItems.size(), false, 60 + (i + recipe.outputFluids.size()) * 18, 18);
		}

		itemStacks.set(ingredients);
		fluidStacks.set(ingredients);

		itemStacks.addTooltipCallback((idx, input, stack, tooltip) -> {
			if (!input) {
				double chance = recipe.outputItems.get(idx - recipe.inputItems.size()).chance;

				if (chance < 1D) {
					String s = String.valueOf(chance * 100D);
					tooltip.add(new TextComponent("Chance: " + (s.endsWith(".0") ? s.substring(0, s.length() - 2) : s) + "%").withStyle(ChatFormatting.GRAY));
				}
			}
		});
	}
}
