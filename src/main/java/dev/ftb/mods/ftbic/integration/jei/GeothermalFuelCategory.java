package dev.ftb.mods.ftbic.integration.jei;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.item.FTBICItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

public class GeothermalFuelCategory extends AbstractRecipeCategory<GeothermalFuelCategory.Entry> {
	public static final IRecipeType<Entry> TYPE = IRecipeType.create(
			Identifier.fromNamespaceAndPath(FTBIC.MOD_ID, "geothermal_fuel"), Entry.class);

	public GeothermalFuelCategory(IGuiHelper helper) {
		super(TYPE,
				Component.translatable("block.ftbic.geothermal_generator"),
				helper.createDrawableItemStack(new ItemStack(FTBICElectricBlocks.GEOTHERMAL_GENERATOR.item.get())),
				160, 40);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, Entry entry, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 4, 9)
				.addFluidStack(Fluids.LAVA, entry.mbPerTick);
	}

	@Override
	public void draw(Entry entry, mezz.jei.api.gui.ingredient.IRecipeSlotsView slots,
			GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
		Font font = Minecraft.getInstance().font;
		double zapsPerTick = FTBICItems.safeGet(FTBICConfig.MACHINES.GEOTHERMAL_GENERATOR_OUTPUT, 20D);
		int tankCap = FTBICConfig.MACHINES.GEOTHERMAL_GENERATOR_TANK_SIZE.get();
		long zapsPerBucket = Math.round(zapsPerTick * 1000);
		graphics.text(font, String.format("%.0f z/mB", zapsPerTick), 30, 2, 0x404040, false);
		graphics.text(font, String.format("%d z per bucket", zapsPerBucket), 30, 13, 0x404040, false);
		graphics.text(font, String.format("Tank %d mB", tankCap), 30, 24, 0x404040, false);
	}

	public static Entry defaultEntry() {
		return new Entry(1);
	}

	public record Entry(int mbPerTick) {
		public static final FluidStack LAVA = new FluidStack(Fluids.LAVA, 1000);
	}
}
