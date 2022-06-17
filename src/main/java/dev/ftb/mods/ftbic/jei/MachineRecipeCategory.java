package dev.ftb.mods.ftbic.jei;


import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.recipe.MachineRecipe;
import dev.ftb.mods.ftbic.recipe.MachineRecipeSerializer;
import dev.ftb.mods.ftbic.screen.MachineScreen;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class MachineRecipeCategory implements IRecipeCategory<MachineRecipe> {
	public final MachineRecipeSerializer serializer;
	public final ElectricBlockInstance electricBlockInstance;
	private final IDrawable background;
	private final IDrawable icon;
	private final IDrawable arrowOff, arrowOn, powerOff, powerOn;
	private final IDrawable slot, largeSlot;

	public MachineRecipeCategory(Supplier<MachineRecipeSerializer> s, IGuiHelper guiHelper, ElectricBlockInstance item) {
		serializer = s.get();
		electricBlockInstance = item;
		background = guiHelper.createBlankDrawable(serializer.guiWidth, serializer.guiHeight);
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(item.item.get()));
		arrowOff = guiHelper.drawableBuilder(MachineScreen.BASE_TEXTURE, 87, 167, 24, 17).setTextureSize(256, 256).build();
		arrowOn = guiHelper.drawableBuilder(MachineScreen.BASE_TEXTURE, 87, 185, 24, 17).setTextureSize(256, 256).buildAnimated(Mth.ceil(FTBICConfig.MACHINES.MACHINE_RECIPE_BASE_TICKS.get()), IDrawableAnimated.StartDirection.LEFT, false);
		powerOff = guiHelper.drawableBuilder(MachineScreen.BASE_TEXTURE, 1, 240, 14, 14).setTextureSize(256, 256).build();
		powerOn = guiHelper.drawableBuilder(MachineScreen.BASE_TEXTURE, 16, 240, 14, 14).setTextureSize(256, 256).buildAnimated(84, IDrawableAnimated.StartDirection.TOP, true);
		slot = guiHelper.drawableBuilder(MachineScreen.BASE_TEXTURE, 1, 167, 18, 18).setTextureSize(256, 256).build();
		largeSlot = (serializer.outputSlots > 1 ? guiHelper.drawableBuilder(MachineScreen.BASE_TEXTURE, 1, 213, 47, 26) : guiHelper.drawableBuilder(MachineScreen.BASE_TEXTURE, 1, 186, 26, 26)).setTextureSize(256, 256).build();
	}

	@Override
	public RecipeType<MachineRecipe> getRecipeType() {
		return FTBICJEIPlugin.getMachineRecipeType(serializer);
	}

	@SuppressWarnings("removal")
	public ResourceLocation getUid() {
		return getRecipeType().getUid();
	}

	@SuppressWarnings("removal")
	public Class<? extends MachineRecipe> getRecipeClass() {
		return getRecipeType().getRecipeClass();
	}

	@Override
	public Component getTitle() {
		return new TranslatableComponent(Util.makeDescriptionId("recipe", getRecipeType().getUid()));
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
	public void draw(MachineRecipe recipe, IRecipeSlotsView slots, PoseStack poseStack, double mouseX, double mouseY) {
		arrowOff.draw(poseStack, serializer.progressX, serializer.progressY);
		arrowOn.draw(poseStack, serializer.progressX, serializer.progressY);
		powerOff.draw(poseStack, serializer.energyX, serializer.energyY);
		powerOn.draw(poseStack, serializer.energyX, serializer.energyY);

		for (int i = 0; i < serializer.inputSlots; i++) {
			slot.draw(poseStack, i * 18, 0);
		}

		slot.draw(poseStack, serializer.batteryX, serializer.batteryY);
		largeSlot.draw(poseStack, serializer.outputX, serializer.outputY);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {
		for (int i = 0; i < recipe.inputFluids.size(); i++) {
			builder.addSlot(RecipeIngredientRole.INPUT, 2 + i * 18, 2)
					.addIngredient(ForgeTypes.FLUID_STACK, recipe.inputFluids.get(i));
		}

		for (int i = 0; i < recipe.inputItems.size(); i++) {
			var input = recipe.inputItems.get(i);
			var slot = builder.addSlot(RecipeIngredientRole.INPUT, 1 + (i + recipe.inputFluids.size()) * 18, 1);
			for (var stack : input.ingredient.getItems()) {
				slot.addIngredient(VanillaTypes.ITEM_STACK, Util.make(stack.copy(), s -> s.setCount(input.count)));
			}
		}

		for (int i = 0; i < recipe.outputFluids.size(); i++) {
			builder.addSlot(RecipeIngredientRole.OUTPUT, serializer.outputX + 6 + i * 25, serializer.outputY + 6)
					.addIngredient(ForgeTypes.FLUID_STACK, recipe.outputFluids.get(i));
		}

		for (int i = 0; i < recipe.outputItems.size(); i++) {
			var output = recipe.outputItems.get(i);
			builder.addSlot(RecipeIngredientRole.OUTPUT, serializer.outputX + 5 + (i + recipe.outputFluids.size()) * 25, serializer.outputY + 5)
					.addItemStack(output.stack)
					.addTooltipCallback((slot, tooltip) -> {
						var chance = output.chance;
						if (chance < 1D) {
							String s = String.valueOf(chance * 100D);
							tooltip.add(new TextComponent("Chance: ").append(new TextComponent((s.endsWith(".0") ? s.substring(0, s.length() - 2) : s) + "%")
									.withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY));
						}
					});
		}
	}
}
