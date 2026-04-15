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
	public static final int WIDTH = 148;
	public static final int HEIGHT = 26;

	public static final IRecipeType<Entry> TYPE = IRecipeType.create(
			Identifier.fromNamespaceAndPath(FTBIC.MOD_ID, "geothermal_fuel"), Entry.class);

	public GeothermalFuelCategory(IGuiHelper helper) {
		super(TYPE,
				Component.translatable("block.ftbic.geothermal_generator"),
				helper.createDrawableItemStack(new ItemStack(FTBICElectricBlocks.GEOTHERMAL_GENERATOR.item.get())),
				WIDTH, HEIGHT);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, Entry entry, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 4, 4)
				.setStandardSlotBackground()
				.add(Fluids.LAVA, 1000);
	}

	@Override
	public void draw(Entry entry, mezz.jei.api.gui.ingredient.IRecipeSlotsView slots,
			GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
		Font font = Minecraft.getInstance().font;
		double zapsPerMb = FTBICItems.safeGet(FTBICConfig.MACHINES.GEOTHERMAL_GENERATOR_OUTPUT, 20D);
		int tankCap = FTBICConfig.MACHINES.GEOTHERMAL_GENERATOR_TANK_SIZE.get();
		long zapsPerBucket = Math.round(zapsPerMb * 1000D);
		graphics.text(font, String.format("%.0f z/mB · %d z/bucket", zapsPerMb, zapsPerBucket), 26, 4, 0x404040, false);
		graphics.text(font, String.format("Tank %d mB", tankCap), 26, 14, 0x404040, false);
	}

	public static Entry defaultEntry() {
		return new Entry(1);
	}

	public record Entry(int mbPerTick) {
		public static final FluidStack LAVA = new FluidStack(Fluids.LAVA, 1000);
	}
}
