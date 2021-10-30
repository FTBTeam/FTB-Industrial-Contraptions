package dev.ftb.mods.ftbic.jei;


import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.recipe.MachineRecipe;
import dev.ftb.mods.ftbic.recipe.MachineRecipeSerializer;
import dev.ftb.mods.ftbic.util.IngredientWithCount;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author LatvianModder
 */
public class MachineRecipeCategory implements IRecipeCategory<MachineRecipe> {
	public final MachineRecipeSerializer serializer;
	public final ElectricBlockInstance electricBlockInstance;
	private final IDrawable background;
	private final IDrawable icon;

	public MachineRecipeCategory(Supplier<MachineRecipeSerializer> s, IGuiHelper guiHelper, ElectricBlockInstance item) {
		serializer = s.get();
		electricBlockInstance = item;
		background = guiHelper.drawableBuilder(new ResourceLocation(FTBIC.MOD_ID + ":textures/gui/machine_jei.png"), 0, (serializer.inputItems + serializer.inputFluids) > 1 ? 55 : 0, 82, 54).setTextureSize(128, 128).build();
		icon = guiHelper.createDrawableIngredient(new ItemStack(item.item.get()));
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

		// fluidStacks.addTooltipCallback((idx, input, stack, tooltip) -> tooltip.set(0, new TranslatableComponent("ftblibrary.mb", stack.getAmount(), stack.getDisplayName())));
	}

	@Override
	public List<Component> getTooltipStrings(MachineRecipe recipe, double mouseX, double mouseY) {
		if (mouseX >= 55D && mouseY >= 3D && mouseX < 73D && mouseY < 30D) {
			// return Collections.singletonList(new TranslatableComponent(FTBJarMod.MOD_ID + ".processing_time", recipe.time / 20));
		}

		return Collections.emptyList();
	}
}
