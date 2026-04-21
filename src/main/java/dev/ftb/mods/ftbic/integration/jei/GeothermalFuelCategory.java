package dev.ftb.mods.ftbic.integration.jei;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.item.FTBICItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
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
	public void createRecipeExtras(IRecipeExtrasBuilder builder, Entry entry, IFocusGroup focuses) {
		double zapsPerMb = FTBICItems.safeGet(FTBICConfig.MACHINES.GEOTHERMAL_GENERATOR_OUTPUT, 20D);
		int tankCap = FTBICConfig.MACHINES.GEOTHERMAL_GENERATOR_TANK_SIZE.get();
		long zapsPerBucket = Math.round(zapsPerMb * 1000D);
		long zapsPerTank = Math.round(zapsPerMb * tankCap);

		builder.addText(Component.literal(String.format("%.0f z/mB @ %,d z/bucket", zapsPerMb, zapsPerBucket)), 120, 9)
				.setPosition(26, 3)
				.setColor(0xFF404040);
		builder.addText(Component.literal(String.format("= %,d zaps / full tank (%d mB)", zapsPerTank, tankCap)), 120, 9)
				.setPosition(26, 14)
				.setColor(0xFF0A7F0A);
	}

	public static Entry defaultEntry() {
		return new Entry(1);
	}

	public record Entry(int mbPerTick) {
		public static final FluidStack LAVA = new FluidStack(Fluids.LAVA, 1000);
	}
}
