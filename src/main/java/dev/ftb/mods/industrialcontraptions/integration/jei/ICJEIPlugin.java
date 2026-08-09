package dev.ftb.mods.industrialcontraptions.integration.jei;

import dev.ftb.mods.industrialcontraptions.IC;
import dev.ftb.mods.industrialcontraptions.block.ICBlocks;
import dev.ftb.mods.industrialcontraptions.block.ICElectricBlocks;
import dev.ftb.mods.industrialcontraptions.block.entity.machine.AntimatterConstructorBlockEntity;
import dev.ftb.mods.industrialcontraptions.client.gui.ReactorSimulatorScreen;
import dev.ftb.mods.industrialcontraptions.item.ICItems;
import dev.ftb.mods.industrialcontraptions.item.FluidCellItem;
import dev.ftb.mods.industrialcontraptions.recipe.AntimatterBoostRecipe;
import dev.ftb.mods.industrialcontraptions.recipe.BasicGeneratorFuelRecipe;
import dev.ftb.mods.industrialcontraptions.recipe.ICRecipes;
import dev.ftb.mods.industrialcontraptions.recipe.MachineRecipe;
import dev.ftb.mods.industrialcontraptions.recipe.MachineRecipeType;
import dev.ftb.mods.industrialcontraptions.util.IngredientWithCount;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class ICJEIPlugin implements IModPlugin {
	public static final Identifier ID = IC.id("jei");

	@Override
	public Identifier getPluginUid() {
		return ID;
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration r) {
		r.registerSubtypeInterpreter(ICItems.FLUID_CELL.get(), (stack, ctx) -> {
			FluidStack fs = FluidCellItem.getStored(stack);
			if (fs.isEmpty()) return "empty";
			var id = BuiltInRegistries.FLUID.getKey(fs.getFluid());
			return id == null ? "unknown" : id.toString();
		});
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration r) {
		var helper = r.getJeiHelpers().getGuiHelper();
		r.addRecipeCategories(new MachineRecipeCategory(ICRecipes.SMELTING, ICElectricBlocks.POWERED_FURNACE, helper));
		r.addRecipeCategories(new MachineRecipeCategory(ICRecipes.MACERATING, ICElectricBlocks.MACERATOR, helper));
		r.addRecipeCategories(new MachineRecipeCategory(ICRecipes.SEPARATING, ICElectricBlocks.CENTRIFUGE, helper));
		r.addRecipeCategories(new MachineRecipeCategory(ICRecipes.COMPRESSING, ICElectricBlocks.COMPRESSOR, helper));
		r.addRecipeCategories(new MachineRecipeCategory(ICRecipes.REPROCESSING, ICElectricBlocks.REPROCESSOR, helper));
		r.addRecipeCategories(new MachineRecipeCategory(ICRecipes.CANNING, ICElectricBlocks.CANNING_MACHINE, helper));
		r.addRecipeCategories(new MachineRecipeCategory(ICRecipes.ROLLING, ICElectricBlocks.ROLLER, helper));
		r.addRecipeCategories(new MachineRecipeCategory(ICRecipes.EXTRUDING, ICElectricBlocks.EXTRUDER, helper));
		r.addRecipeCategories(new MachineRecipeCategory(ICRecipes.ALLOY_SMELTING, ICElectricBlocks.ALLOY_SMELTER, helper, 3));
		r.addRecipeCategories(new BasicGeneratorFuelCategory(helper));
		r.addRecipeCategories(new GeothermalFuelCategory(helper));
		r.addRecipeCategories(new AntimatterBoostCategory(helper));
	}

	@Override
	public void registerRecipes(IRecipeRegistration r) {
		r.addRecipes(GeothermalFuelCategory.TYPE, List.of(GeothermalFuelCategory.defaultEntry()));

		long zapsPer = Math.round(AntimatterConstructorBlockEntity.PRODUCTION_THRESHOLD);
		r.addItemStackInfo(
				new ItemStack(ICItems.ANTIMATTER.item.get()),
				Component.literal("Produced by the Antimatter Constructor."),
				Component.literal("Each antimatter requires " + zapsPer + " zaps of progress."),
				Component.literal("Boost items consumed in the input slot accelerate progress."),
				Component.literal("See \"Antimatter Constructor\" recipes for boost values."));

		registerReactorComponentInfo(r);
	}

	private static void registerReactorComponentInfo(IRecipeRegistration r) {
		rodInfo(r, ICItems.URANIUM_FUEL_ROD.get(), 1, 5, 2, 20000);
		rodInfo(r, ICItems.DUAL_URANIUM_FUEL_ROD.get(), 2, 10, 4, 20000);
		rodInfo(r, ICItems.QUAD_URANIUM_FUEL_ROD.get(), 4, 20, 8, 20000);

		coolantInfo(r, ICItems.SMALL_COOLANT_CELL.get(), 10_000);
		coolantInfo(r, ICItems.MEDIUM_COOLANT_CELL.get(), 30_000);
		coolantInfo(r, ICItems.LARGE_COOLANT_CELL.get(), 60_000);

		ventInfo(r, ICItems.HEAT_VENT.get(), 1000, 6, 0, 0);
		ventInfo(r, ICItems.ADVANCED_HEAT_VENT.get(), 1000, 12, 0, 0);
		ventInfo(r, ICItems.REACTOR_HEAT_VENT.get(), 1000, 5, 5, 0);
		ventInfo(r, ICItems.COMPONENT_HEAT_VENT.get(), 0, 0, 0, 4);
		ventInfo(r, ICItems.OVERCLOCKED_HEAT_VENT.get(), 1000, 20, 36, 0);

		exchangerInfo(r, ICItems.HEAT_EXCHANGER.get(), 2500, 12, 4);
		exchangerInfo(r, ICItems.ADVANCED_HEAT_EXCHANGER.get(), 10_000, 24, 8);
		exchangerInfo(r, ICItems.REACTOR_HEAT_EXCHANGER.get(), 5000, 0, 72);
		exchangerInfo(r, ICItems.COMPONENT_HEAT_EXCHANGER.get(), 5000, 36, 0);

		platingInfo(r, ICItems.REACTOR_PLATING.get(), 1000, 0.95);
		platingInfo(r, ICItems.CONTAINMENT_REACTOR_PLATING.get(), 500, 0.90);
		platingInfo(r, ICItems.HEAT_CAPACITY_REACTOR_PLATING.get(), 1700, 0.99);

		reflectorInfo(r, ICItems.NEUTRON_REFLECTOR.get(), 30_000);
		reflectorInfo(r, ICItems.THICK_NEUTRON_REFLECTOR.get(), 120_000);
		reflectorInfo(r, ICItems.IRIDIUM_NEUTRON_REFLECTOR.get(), 0);
	}

	private static void rodInfo(IRecipeRegistration r, Item item, int rods, double energyMult, double heatMult, int durability) {
		int pulses = rods == 1 ? 1 : rods == 2 ? 2 : 3;
		double baseEnergy = pulses * energyMult;
		double baseHeat = heatMult * pulses * (pulses + 1);
		r.addItemStackInfo(new ItemStack(item),
				Component.literal("Nuclear fuel rod. " + rods + "-rod pack emits " + pulses + " pulse" + (pulses == 1 ? "" : "s") + "/cycle."),
				Component.literal("Energy: " + baseEnergy + " zap/t base (×(pulses+reflectors))."),
				Component.literal("Heat: " + baseHeat + "/cycle base. Distributed into neighboring heat acceptors."),
				Component.literal("Durability: " + durability + " cycles before the rod is spent."));
	}

	private static void coolantInfo(IRecipeRegistration r, Item item, int capacity) {
		r.addItemStackInfo(new ItemStack(item),
				Component.literal("Passive heat buffer. Absorbs heat distributed by adjacent fuel rods."),
				Component.literal("Capacity: " + String.format("%,d", capacity) + " heat."),
				Component.literal("Pair with a Component Heat Vent to replenish durability each cycle."));
	}

	private static void ventInfo(IRecipeRegistration r, Item item, int maxHeat, int selfCool, int reactorCool, int componentCool) {
		List<Component> lines = new ArrayList<>();
		lines.add(Component.literal("Heat vent. Removes heat each reactor cycle."));
		if (maxHeat > 0) lines.add(Component.literal("Durability: " + maxHeat + " heat absorption."));
		if (selfCool > 0) lines.add(Component.literal("Self cooling: " + selfCool + "/cycle (heals own durability)."));
		if (reactorCool > 0) lines.add(Component.literal("Reactor cooling: " + reactorCool + "/cycle removed from reactor heat pool."));
		if (componentCool > 0) lines.add(Component.literal("Component cooling: " + componentCool + "/cycle to each adjacent coolant cell."));
		r.addItemStackInfo(new ItemStack(item), lines.toArray(Component[]::new));
	}

	private static void exchangerInfo(IRecipeRegistration r, Item item, int maxHeat, int toAdjacent, int toCore) {
		List<Component> lines = new ArrayList<>();
		lines.add(Component.literal("Heat exchanger. Balances heat between neighbors and the reactor core."));
		lines.add(Component.literal("Durability: " + String.format("%,d", maxHeat) + " heat buffer."));
		if (toAdjacent > 0) lines.add(Component.literal("Adjacent transfer: up to " + toAdjacent + "/cycle per neighbor."));
		if (toCore > 0) lines.add(Component.literal("Core transfer: up to " + toCore + "/cycle to/from the reactor heat pool."));
	    r.addItemStackInfo(new ItemStack(item), lines.toArray(Component[]::new));
	}

	private static void platingInfo(IRecipeRegistration r, Item item, int heatCapacity, double explosionMod) {
		int pct = (int) Math.round((1.0 - explosionMod) * 100.0);
		r.addItemStackInfo(new ItemStack(item),
				Component.literal("Reactor plating. Modifies the reactor hull itself."),
				Component.literal("Max heat bonus: +" + String.format("%,d", heatCapacity) + " (stacks with other plating)."),
				Component.literal("Explosion dampening: ×" + explosionMod + " (-" + pct + "% radius per plating)."));
	}

	private static void reflectorInfo(IRecipeRegistration r, Item item, int durability) {
		r.addItemStackInfo(new ItemStack(item),
				Component.literal("Neutron reflector. Bounces pulses back into adjacent fuel rods."),
				Component.literal("Each reflector adjacent to a rod adds +1 pulse (more energy AND more heat)."),
				Component.literal(durability == 0
						? "Durability: infinite (iridium-reinforced)."
						: "Durability: " + String.format("%,d", durability) + " pulses before the reflector burns out."));
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration r) {
		r.addGuiContainerHandler(ReactorSimulatorScreen.class,
				new IGuiContainerHandler<>() {
					@Override
					public List<Rect2i> getGuiExtraAreas(ReactorSimulatorScreen screen) {
						return List.of(new Rect2i(0, 0, screen.width, screen.height));
					}
				});
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		ClientRecipeCache.setRuntime(jeiRuntime);
		hideEmptyInputRecipes(jeiRuntime);
	}

	private static void hideEmptyInputRecipes(IJeiRuntime runtime) {
		IRecipeManager jeiRm = runtime.getRecipeManager();
		for (MachineRecipeType type : MachineRecipeType.ALL) {
			hideEmptyFor(jeiRm, type);
		}
	}

	private static void hideEmptyFor(IRecipeManager jeiRm, MachineRecipeType type) {
		IRecipeHolderType<MachineRecipe> holderType = catalystType(type);
		List<RecipeHolder<MachineRecipe>> toHide = jeiRm.createRecipeLookup(holderType).get()
				.filter(h -> !hasResolvableInputs(h.value()))
				.toList();
		if (!toHide.isEmpty()) {
			jeiRm.hideRecipes(holderType, toHide);
		}
	}

	private static boolean hasResolvableInputs(MachineRecipe recipe) {
		for (IngredientWithCount in : recipe.inputs) {
			var ing = in.ingredient();
			if (ing.getCustomIngredient() != null) continue;
			var values = ing.getValues();
			if (values == null || values.size() == 0) return false;
		}
		return true;
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
		r.addCraftingStation(catalystType(ICRecipes.SMELTING), ICElectricBlocks.POWERED_FURNACE.block.get(), ICElectricBlocks.ADVANCED_POWERED_FURNACE.block.get());
		r.addCraftingStation(catalystType(ICRecipes.MACERATING), ICElectricBlocks.MACERATOR.block.get(), ICElectricBlocks.ADVANCED_MACERATOR.block.get());
		r.addCraftingStation(catalystType(ICRecipes.SEPARATING), ICElectricBlocks.CENTRIFUGE.block.get(), ICElectricBlocks.ADVANCED_CENTRIFUGE.block.get());
		r.addCraftingStation(catalystType(ICRecipes.COMPRESSING), ICElectricBlocks.COMPRESSOR.block.get(), ICElectricBlocks.ADVANCED_COMPRESSOR.block.get());
		r.addCraftingStation(catalystType(ICRecipes.REPROCESSING), ICElectricBlocks.REPROCESSOR.block.get());
		r.addCraftingStation(catalystType(ICRecipes.CANNING), ICElectricBlocks.CANNING_MACHINE.block.get());
		r.addCraftingStation(catalystType(ICRecipes.ROLLING), ICElectricBlocks.ROLLER.block.get());
		r.addCraftingStation(catalystType(ICRecipes.EXTRUDING), ICElectricBlocks.EXTRUDER.block.get());
		r.addCraftingStation(catalystType(ICRecipes.ALLOY_SMELTING), ICElectricBlocks.ALLOY_SMELTER.block.get());
		r.addCraftingStation(RecipeTypes.CRAFTING, ICElectricBlocks.POWERED_CRAFTING_TABLE.block.get());
		r.addCraftingStation(RecipeTypes.SMELTING, ICBlocks.IRON_FURNACE.get());
		r.addCraftingStation(basicGeneratorFuelType(), ICElectricBlocks.BASIC_GENERATOR.block.get());
		r.addCraftingStation(GeothermalFuelCategory.TYPE, ICElectricBlocks.GEOTHERMAL_GENERATOR.block.get());
		r.addCraftingStation(antimatterBoostType(), ICElectricBlocks.ANTIMATTER_CONSTRUCTOR.block.get());
	}

	private static IRecipeHolderType<MachineRecipe> catalystType(MachineRecipeType machine) {
		return IRecipeType.create(machine.TYPE.get());
	}

	@SuppressWarnings("unchecked")
	private static IRecipeHolderType<BasicGeneratorFuelRecipe> basicGeneratorFuelType() {
		return IRecipeType.create((RecipeType<BasicGeneratorFuelRecipe>)
				(RecipeType<?>) ICRecipes.BASIC_GENERATOR_FUEL.get());
	}

	@SuppressWarnings("unchecked")
	private static IRecipeHolderType<AntimatterBoostRecipe> antimatterBoostType() {
		return IRecipeType.create((RecipeType<AntimatterBoostRecipe>)
				(RecipeType<?>) ICRecipes.ANTIMATTER_BOOST.get());
	}
}
