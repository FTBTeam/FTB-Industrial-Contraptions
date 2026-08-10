package dev.ftb.mods.industrialcontraptions.integration.jei;

import dev.ftb.mods.industrialcontraptions.IC;
import dev.ftb.mods.industrialcontraptions.ICConfig;
import dev.ftb.mods.industrialcontraptions.block.ICElectricBlocks;
import dev.ftb.mods.industrialcontraptions.item.ICItems;
import dev.ftb.mods.industrialcontraptions.util.ICUtils;
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
			Identifier.fromNamespaceAndPath(IC.MOD_ID, "geothermal_fuel"), Entry.class);

	public GeothermalFuelCategory(IGuiHelper helper) {
		super(TYPE,
				Component.translatable("block.ic.geothermal_generator"),
				helper.createDrawableItemStack(new ItemStack(ICElectricBlocks.GEOTHERMAL_GENERATOR.item.get())),
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
		double zapsPerMb = ICItems.safeGet(ICConfig.MACHINES.GEOTHERMAL_GENERATOR_OUTPUT, 20D);
		int tankCap = ICConfig.MACHINES.GEOTHERMAL_GENERATOR_TANK_SIZE.get();
		long zapsPerBucket = Math.round(zapsPerMb * 1000D);
		long zapsPerTank = Math.round(zapsPerMb * tankCap);

		builder.addText(Component.translatable("ic.jei.zaps_per_mb",
						ICUtils.fmtDouble(zapsPerMb, 0), ICUtils.fmtInt(zapsPerBucket)), 120, 9)
				.setPosition(26, 3)
				.setColor(0xFF404040);
		builder.addText(Component.translatable("ic.jei.zaps_per_tank",
						ICUtils.fmtInt(zapsPerTank), String.valueOf(tankCap)), 120, 9)
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
