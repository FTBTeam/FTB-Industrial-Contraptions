package dev.ftb.mods.ftbic.jei;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import dev.ftb.mods.ftbic.recipe.MachineRecipeSerializer;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
@JeiPlugin
public class FTBICJEIPlugin implements IModPlugin {
	public static final ResourceLocation ID = new ResourceLocation(FTBIC.MOD_ID, "jei");

	@Override
	public ResourceLocation getPluginUid() {
		return ID;
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration r) {
		r.addRecipeCatalyst(new ItemStack(FTBICItems.IRON_FURNACE.get()), VanillaRecipeCategoryUid.FURNACE);
		r.addRecipeCatalyst(new ItemStack(FTBICElectricBlocks.ELECTRIC_FURNACE.item.get()), VanillaRecipeCategoryUid.FURNACE);
		r.addRecipeCatalyst(new ItemStack(FTBICElectricBlocks.INDUCTION_FURNACE.item.get()), VanillaRecipeCategoryUid.FURNACE);
		r.addRecipeCatalyst(new ItemStack(FTBICElectricBlocks.MACERATOR.item.get()), FTBICRecipes.MACERATING.get().getRegistryName());
		r.addRecipeCatalyst(new ItemStack(FTBICElectricBlocks.EXTRACTOR.item.get()), FTBICRecipes.EXTRACTING.get().getRegistryName());
		r.addRecipeCatalyst(new ItemStack(FTBICElectricBlocks.COMPRESSOR.item.get()), FTBICRecipes.COMPRESSING.get().getRegistryName());
		r.addRecipeCatalyst(new ItemStack(FTBICElectricBlocks.ELECTROLYZER.item.get()), FTBICRecipes.ELECTROLYZING.get().getRegistryName());
		r.addRecipeCatalyst(new ItemStack(FTBICElectricBlocks.RECYCLER.item.get()), FTBICRecipes.RECYCLING.get().getRegistryName());
		r.addRecipeCatalyst(new ItemStack(FTBICElectricBlocks.CANNING_MACHINE.item.get()), FTBICRecipes.CANNING.get().getRegistryName());
		r.addRecipeCatalyst(new ItemStack(FTBICElectricBlocks.ROTARY_MACERATOR.item.get()), FTBICRecipes.MACERATING.get().getRegistryName());
		r.addRecipeCatalyst(new ItemStack(FTBICElectricBlocks.VACUUM_EXTRACTOR.item.get()), FTBICRecipes.EXTRACTING.get().getRegistryName());
		r.addRecipeCatalyst(new ItemStack(FTBICElectricBlocks.SINGULARITY_COMPRESSOR.item.get()), FTBICRecipes.COMPRESSING.get().getRegistryName());
	}

	private void addMachineRecipe(IRecipeRegistration r, Supplier<MachineRecipeSerializer> supplier) {
		Level level = Minecraft.getInstance().level;
		r.addRecipes(level.getRecipeManager().getAllRecipesFor(supplier.get().recipeType), supplier.get().getRegistryName());
	}

	@Override
	public void registerRecipes(IRecipeRegistration r) {
		addMachineRecipe(r, FTBICRecipes.MACERATING);
		addMachineRecipe(r, FTBICRecipes.EXTRACTING);
		addMachineRecipe(r, FTBICRecipes.COMPRESSING);
		addMachineRecipe(r, FTBICRecipes.ELECTROLYZING);
		addMachineRecipe(r, FTBICRecipes.RECYCLING);
		addMachineRecipe(r, FTBICRecipes.CANNING);
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration r) {
		r.addRecipeCategories(new MachineRecipeCategory(FTBICRecipes.MACERATING, r.getJeiHelpers().getGuiHelper(), FTBICElectricBlocks.MACERATOR));
		r.addRecipeCategories(new MachineRecipeCategory(FTBICRecipes.EXTRACTING, r.getJeiHelpers().getGuiHelper(), FTBICElectricBlocks.EXTRACTOR));
		r.addRecipeCategories(new MachineRecipeCategory(FTBICRecipes.COMPRESSING, r.getJeiHelpers().getGuiHelper(), FTBICElectricBlocks.COMPRESSOR));
		r.addRecipeCategories(new MachineRecipeCategory(FTBICRecipes.ELECTROLYZING, r.getJeiHelpers().getGuiHelper(), FTBICElectricBlocks.ELECTROLYZER));
		r.addRecipeCategories(new MachineRecipeCategory(FTBICRecipes.RECYCLING, r.getJeiHelpers().getGuiHelper(), FTBICElectricBlocks.RECYCLER));
		r.addRecipeCategories(new MachineRecipeCategory(FTBICRecipes.CANNING, r.getJeiHelpers().getGuiHelper(), FTBICElectricBlocks.CANNING_MACHINE));
	}
}