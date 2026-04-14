package dev.ftb.mods.ftbic.integration.jei;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import dev.ftb.mods.ftbic.recipe.MachineRecipe;
import dev.ftb.mods.ftbic.recipe.MachineRecipeType;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

/**
/**
 * JEI 29.4.x plugin. Registers one recipe category per FTBIC machine type, ships recipes from the
 * live server recipe map into JEI via {@link ClientRecipeCache}, wires the powered-crafting-table
 * recipe-transfer handler, and tells JEI to differentiate {@code FluidCellItem} stacks by their
 * stored fluid type so each filled cell shows up as its own JEI entry.
 */
@JeiPlugin
public class FTBICJEIPlugin implements IModPlugin {
	public static final Identifier ID = FTBIC.id("jei");

	@Override
	public Identifier getPluginUid() {
		return ID;
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration r) {
		r.registerSubtypeInterpreter(dev.ftb.mods.ftbic.item.FTBICItems.FLUID_CELL.get(), (stack, ctx) -> {
			net.neoforged.neoforge.fluids.FluidStack fs = dev.ftb.mods.ftbic.item.FluidCellItem.getStored(stack);
			if (fs.isEmpty()) return "empty";
			var id = BuiltInRegistries.FLUID.getKey(fs.getFluid());
			return id == null ? "unknown" : id.toString();
		});
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration r) {
		var helper = r.getJeiHelpers().getGuiHelper();
		r.addRecipeCategories(new MachineRecipeCategory(FTBICRecipes.SMELTING, FTBICElectricBlocks.POWERED_FURNACE, helper));
		r.addRecipeCategories(new MachineRecipeCategory(FTBICRecipes.MACERATING, FTBICElectricBlocks.MACERATOR, helper));
		r.addRecipeCategories(new MachineRecipeCategory(FTBICRecipes.SEPARATING, FTBICElectricBlocks.CENTRIFUGE, helper));
		r.addRecipeCategories(new MachineRecipeCategory(FTBICRecipes.COMPRESSING, FTBICElectricBlocks.COMPRESSOR, helper));
		r.addRecipeCategories(new MachineRecipeCategory(FTBICRecipes.REPROCESSING, FTBICElectricBlocks.REPROCESSOR, helper));
		r.addRecipeCategories(new MachineRecipeCategory(FTBICRecipes.CANNING, FTBICElectricBlocks.CANNING_MACHINE, helper));
		r.addRecipeCategories(new MachineRecipeCategory(FTBICRecipes.ROLLING, FTBICElectricBlocks.ROLLER, helper));
		r.addRecipeCategories(new MachineRecipeCategory(FTBICRecipes.EXTRUDING, FTBICElectricBlocks.EXTRUDER, helper));
		r.addRecipeCategories(new BasicGeneratorFuelCategory(helper));
		r.addRecipeCategories(new GeothermalFuelCategory(helper));
		r.addRecipeCategories(new AntimatterBoostCategory(helper));
	}

	@Override
	public void registerRecipes(IRecipeRegistration r) {
		r.addRecipes(GeothermalFuelCategory.TYPE, java.util.List.of(GeothermalFuelCategory.defaultEntry()));
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		ClientRecipeCache.setRuntime(jeiRuntime);
	}

	@Override
	public void onRuntimeUnavailable() {
		ClientRecipeCache.clearRuntime();
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration r) {
		r.addRecipeTransferHandler(
				new PoweredCraftingTableTransferHandler(r.getTransferHelper()),
				RecipeTypes.CRAFTING);
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration r) {
		r.addCraftingStation(catalystType(FTBICRecipes.SMELTING), FTBICElectricBlocks.POWERED_FURNACE.block.get(), FTBICElectricBlocks.ADVANCED_POWERED_FURNACE.block.get());
		r.addCraftingStation(catalystType(FTBICRecipes.MACERATING), FTBICElectricBlocks.MACERATOR.block.get(), FTBICElectricBlocks.ADVANCED_MACERATOR.block.get());
		r.addCraftingStation(catalystType(FTBICRecipes.SEPARATING), FTBICElectricBlocks.CENTRIFUGE.block.get(), FTBICElectricBlocks.ADVANCED_CENTRIFUGE.block.get());
		r.addCraftingStation(catalystType(FTBICRecipes.COMPRESSING), FTBICElectricBlocks.COMPRESSOR.block.get(), FTBICElectricBlocks.ADVANCED_COMPRESSOR.block.get());
		r.addCraftingStation(catalystType(FTBICRecipes.REPROCESSING), FTBICElectricBlocks.REPROCESSOR.block.get());
		r.addCraftingStation(catalystType(FTBICRecipes.CANNING), FTBICElectricBlocks.CANNING_MACHINE.block.get());
		r.addCraftingStation(catalystType(FTBICRecipes.ROLLING), FTBICElectricBlocks.ROLLER.block.get());
		r.addCraftingStation(catalystType(FTBICRecipes.EXTRUDING), FTBICElectricBlocks.EXTRUDER.block.get());
		r.addCraftingStation(RecipeTypes.CRAFTING, FTBICElectricBlocks.POWERED_CRAFTING_TABLE.block.get());
		r.addCraftingStation(basicGeneratorFuelType(), FTBICElectricBlocks.BASIC_GENERATOR.block.get());
		r.addCraftingStation(GeothermalFuelCategory.TYPE, FTBICElectricBlocks.GEOTHERMAL_GENERATOR.block.get());
		r.addCraftingStation(antimatterBoostType(), FTBICElectricBlocks.ANTIMATTER_CONSTRUCTOR.block.get());
	}

	private static IRecipeHolderType<MachineRecipe> catalystType(MachineRecipeType machine) {
		return IRecipeType.create(machine.TYPE.get());
	}

	@SuppressWarnings("unchecked")
	private static IRecipeHolderType<dev.ftb.mods.ftbic.recipe.BasicGeneratorFuelRecipe> basicGeneratorFuelType() {
		return IRecipeType.create((net.minecraft.world.item.crafting.RecipeType<dev.ftb.mods.ftbic.recipe.BasicGeneratorFuelRecipe>)
				(net.minecraft.world.item.crafting.RecipeType<?>) FTBICRecipes.BASIC_GENERATOR_FUEL.get());
	}

	@SuppressWarnings("unchecked")
	private static IRecipeHolderType<dev.ftb.mods.ftbic.recipe.AntimatterBoostRecipe> antimatterBoostType() {
		return IRecipeType.create((net.minecraft.world.item.crafting.RecipeType<dev.ftb.mods.ftbic.recipe.AntimatterBoostRecipe>)
				(net.minecraft.world.item.crafting.RecipeType<?>) FTBICRecipes.ANTIMATTER_BOOST.get());
	}
}
