package dev.ftb.mods.ftbic.jei;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.item.FluidCellItem;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import dev.ftb.mods.ftbic.recipe.MachineRecipe;
import dev.ftb.mods.ftbic.recipe.MachineRecipeSerializer;
import dev.ftb.mods.ftbic.recipe.RecipeCache;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
@JeiPlugin
public class FTBICJEIPlugin implements IModPlugin {
	public static final ResourceLocation ID = new ResourceLocation(FTBIC.MOD_ID, "jei");

	private static final Map<MachineRecipeSerializer, RecipeType<MachineRecipe>> RECIPE_TYPES = new HashMap<>();

	public static RecipeType<MachineRecipe> getMachineRecipeType(MachineRecipeSerializer serializer) {
		return RECIPE_TYPES.computeIfAbsent(serializer, key -> new RecipeType<>(key.getId(), MachineRecipe.class));
	}

	@Override
	public ResourceLocation getPluginUid() {
		return ID;
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration r) {
		r.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, FTBICItems.FLUID_CELL.get(), FTBICJEIPlugin::getSubtype);

		r.useNbtForSubtypes(
				FTBICItems.MECHANICAL_ELYTRA.get(),
				FTBICItems.CARBON_CHESTPLATE.get(),
				FTBICItems.QUANTUM_CHESTPLATE.get()
		);
	}

	public static String getSubtype(ItemStack stack, UidContext context) {
		CompoundTag nbt = stack.getTag();

		if (nbt != null) {
			return nbt.getString(FluidCellItem.TAG_FLUID);
		}

		return "";
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration r) {
		r.addRecipeTransferHandler(new PoweredCraftingTableTransferHandler(r.getTransferHelper()), RecipeTypes.CRAFTING);
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration r) {
		r.addRecipeCatalyst(new ItemStack(FTBICItems.IRON_FURNACE.get()), RecipeTypes.SMELTING, RecipeTypes.FUELING);
		r.addRecipeCatalyst(new ItemStack(FTBICElectricBlocks.POWERED_FURNACE.item.get()), RecipeTypes.SMELTING, RecipeTypes.FUELING);
		r.addRecipeCatalyst(new ItemStack(FTBICElectricBlocks.POWERED_FURNACE.item.get()), getMachineRecipeType(FTBICRecipes.SMELTING.get()));
		r.addRecipeCatalyst(new ItemStack(FTBICElectricBlocks.MACERATOR.item.get()), getMachineRecipeType(FTBICRecipes.MACERATING.get()));
		r.addRecipeCatalyst(new ItemStack(FTBICElectricBlocks.CENTRIFUGE.item.get()), getMachineRecipeType(FTBICRecipes.SEPARATING.get()));
		r.addRecipeCatalyst(new ItemStack(FTBICElectricBlocks.COMPRESSOR.item.get()), getMachineRecipeType(FTBICRecipes.COMPRESSING.get()));
		r.addRecipeCatalyst(new ItemStack(FTBICElectricBlocks.REPROCESSOR.item.get()), getMachineRecipeType(FTBICRecipes.REPROCESSING.get()));
		r.addRecipeCatalyst(new ItemStack(FTBICElectricBlocks.CANNING_MACHINE.item.get()), getMachineRecipeType(FTBICRecipes.CANNING.get()));
		r.addRecipeCatalyst(new ItemStack(FTBICElectricBlocks.ROLLER.item.get()), getMachineRecipeType(FTBICRecipes.ROLLING.get()));
		r.addRecipeCatalyst(new ItemStack(FTBICElectricBlocks.EXTRUDER.item.get()), getMachineRecipeType(FTBICRecipes.EXTRUDING.get()));
		r.addRecipeCatalyst(new ItemStack(FTBICElectricBlocks.ADVANCED_POWERED_FURNACE.item.get()), getMachineRecipeType(FTBICRecipes.SMELTING.get()));
		r.addRecipeCatalyst(new ItemStack(FTBICElectricBlocks.ADVANCED_POWERED_FURNACE.item.get()), RecipeTypes.SMELTING, RecipeTypes.FUELING);
		r.addRecipeCatalyst(new ItemStack(FTBICElectricBlocks.ADVANCED_MACERATOR.item.get()), getMachineRecipeType(FTBICRecipes.MACERATING.get()));
		r.addRecipeCatalyst(new ItemStack(FTBICElectricBlocks.ADVANCED_CENTRIFUGE.item.get()), getMachineRecipeType(FTBICRecipes.SEPARATING.get()));
		r.addRecipeCatalyst(new ItemStack(FTBICElectricBlocks.ADVANCED_COMPRESSOR.item.get()), getMachineRecipeType(FTBICRecipes.COMPRESSING.get()));
	}

	@Override
	public void registerRecipes(IRecipeRegistration r) {
		Level level = Minecraft.getInstance().level;
		RecipeCache cache = level == null ? null : RecipeCache.get(Minecraft.getInstance().level);

		if (cache == null) {
			FTBIC.LOGGER.error("Recipe cache not found for JEI plugin!");
			return;
		}

		r.addRecipes(getMachineRecipeType(FTBICRecipes.SMELTING.get()), cache.smelting.getAllRealAndVisibleRecipes(level));
		r.addRecipes(getMachineRecipeType(FTBICRecipes.MACERATING.get()), cache.macerating.getAllVisibleRecipes(level));
		r.addRecipes(getMachineRecipeType(FTBICRecipes.COMPRESSING.get()), cache.compressing.getAllVisibleRecipes(level));
		r.addRecipes(getMachineRecipeType(FTBICRecipes.SEPARATING.get()), cache.separating.getAllVisibleRecipes(level));
		r.addRecipes(getMachineRecipeType(FTBICRecipes.REPROCESSING.get()), cache.reprocessing.getAllVisibleRecipes(level));
		r.addRecipes(getMachineRecipeType(FTBICRecipes.CANNING.get()), cache.canning.getAllVisibleRecipes(level));
		r.addRecipes(getMachineRecipeType(FTBICRecipes.ROLLING.get()), cache.rolling.getAllVisibleRecipes(level));
		r.addRecipes(getMachineRecipeType(FTBICRecipes.EXTRUDING.get()), cache.extruding.getAllVisibleRecipes(level));
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration r) {
		r.addRecipeCategories(new MachineRecipeCategory(FTBICRecipes.SMELTING, r.getJeiHelpers().getGuiHelper(), FTBICElectricBlocks.POWERED_FURNACE));
		r.addRecipeCategories(new MachineRecipeCategory(FTBICRecipes.MACERATING, r.getJeiHelpers().getGuiHelper(), FTBICElectricBlocks.MACERATOR));
		r.addRecipeCategories(new MachineRecipeCategory(FTBICRecipes.SEPARATING, r.getJeiHelpers().getGuiHelper(), FTBICElectricBlocks.CENTRIFUGE));
		r.addRecipeCategories(new MachineRecipeCategory(FTBICRecipes.COMPRESSING, r.getJeiHelpers().getGuiHelper(), FTBICElectricBlocks.COMPRESSOR));
		r.addRecipeCategories(new MachineRecipeCategory(FTBICRecipes.REPROCESSING, r.getJeiHelpers().getGuiHelper(), FTBICElectricBlocks.REPROCESSOR));
		r.addRecipeCategories(new MachineRecipeCategory(FTBICRecipes.CANNING, r.getJeiHelpers().getGuiHelper(), FTBICElectricBlocks.CANNING_MACHINE));
		r.addRecipeCategories(new MachineRecipeCategory(FTBICRecipes.ROLLING, r.getJeiHelpers().getGuiHelper(), FTBICElectricBlocks.ROLLER));
		r.addRecipeCategories(new MachineRecipeCategory(FTBICRecipes.EXTRUDING, r.getJeiHelpers().getGuiHelper(), FTBICElectricBlocks.EXTRUDER));
	}
}