package dev.ftb.mods.ftbic.jei;


import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.recipe.MachineRecipe;
import dev.ftb.mods.ftbic.recipe.MachineRecipeSerializer;
import dev.ftb.mods.ftbic.screen.MachineScreen;
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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
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
	public final String titleKey;
	public final ElectricBlockInstance electricBlockInstance;
	private final IDrawable background;
	private final IDrawable icon;
	private final IDrawable arrowOff, arrowOn, powerOff, powerOn;
	private final IDrawable slot, largeSlot;

	public MachineRecipeCategory(Supplier<MachineRecipeSerializer> s, IGuiHelper guiHelper, ElectricBlockInstance item) {
		serializer = s.get();
		titleKey = "recipe." + FTBIC.MOD_ID + "." + serializer.getRegistryName().getPath();
		electricBlockInstance = item;
		background = guiHelper.createBlankDrawable(serializer.guiWidth, serializer.guiHeight);
		icon = guiHelper.createDrawableIngredient(new ItemStack(item.item.get()));
		arrowOff = guiHelper.drawableBuilder(MachineScreen.BASE_TEXTURE, 87, 167, 24, 17).setTextureSize(256, 256).build();
		arrowOn = guiHelper.drawableBuilder(MachineScreen.BASE_TEXTURE, 87, 185, 24, 17).setTextureSize(256, 256).buildAnimated(Mth.ceil(FTBICConfig.MACHINES.MACHINE_RECIPE_BASE_TICKS.get()), IDrawableAnimated.StartDirection.LEFT, false);
		powerOff = guiHelper.drawableBuilder(MachineScreen.BASE_TEXTURE, 1, 240, 14, 14).setTextureSize(256, 256).build();
		powerOn = guiHelper.drawableBuilder(MachineScreen.BASE_TEXTURE, 16, 240, 14, 14).setTextureSize(256, 256).buildAnimated(84, IDrawableAnimated.StartDirection.TOP, true);
		slot = guiHelper.drawableBuilder(MachineScreen.BASE_TEXTURE, 1, 167, 18, 18).setTextureSize(256, 256).build();
		largeSlot = (serializer.outputSlots > 1 ? guiHelper.drawableBuilder(MachineScreen.BASE_TEXTURE, 1, 213, 47, 26) : guiHelper.drawableBuilder(MachineScreen.BASE_TEXTURE, 1, 186, 26, 26)).setTextureSize(256, 256).build();
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
	public Component getTitle() {
		return new TranslatableComponent(titleKey);
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
		arrowOff.draw(matrixStack, serializer.progressX, serializer.progressY);
		arrowOn.draw(matrixStack, serializer.progressX, serializer.progressY);
		powerOff.draw(matrixStack, serializer.energyX, serializer.energyY);
		powerOn.draw(matrixStack, serializer.energyX, serializer.energyY);

		for (int i = 0; i < serializer.inputSlots; i++) {
			slot.draw(matrixStack, i * 18, 0);
		}

		slot.draw(matrixStack, serializer.batteryX, serializer.batteryY);
		largeSlot.draw(matrixStack, serializer.outputX, serializer.outputY);
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
			fluidStacks.init(i + recipe.inputFluids.size(), false, serializer.outputX + 5 + i * 25, serializer.outputY + 5);
		}

		for (int i = 0; i < recipe.outputItems.size(); i++) {
			itemStacks.init(i + recipe.inputItems.size(), false, serializer.outputX + 4 + (i + recipe.outputFluids.size()) * 25, serializer.outputY + 4);
		}

		itemStacks.set(ingredients);
		fluidStacks.set(ingredients);

		itemStacks.addTooltipCallback((idx, input, stack, tooltip) -> {
			if (!input) {
				double chance = recipe.outputItems.get(idx - recipe.inputItems.size()).chance;

				if (chance < 1D) {
					String s = String.valueOf(chance * 100D);
					tooltip.add(new TextComponent("Chance: ").append(new TextComponent((s.endsWith(".0") ? s.substring(0, s.length() - 2) : s) + "%").withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY));
				}
			}
		});
	}
}
