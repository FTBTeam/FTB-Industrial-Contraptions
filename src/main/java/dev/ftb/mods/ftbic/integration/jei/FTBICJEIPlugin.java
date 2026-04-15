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

		// Info entry for the antimatter item — explains it's produced over time by the Antimatter Constructor.
		long zapsPer = Math.round(dev.ftb.mods.ftbic.block.entity.machine.AntimatterConstructorBlockEntity.PRODUCTION_THRESHOLD);
		r.addItemStackInfo(
				new net.minecraft.world.item.ItemStack(dev.ftb.mods.ftbic.item.FTBICItems.ANTIMATTER.item.get()),
				net.minecraft.network.chat.Component.literal("Produced by the Antimatter Constructor."),
				net.minecraft.network.chat.Component.literal("Each antimatter requires " + zapsPer + " zaps of progress."),
				net.minecraft.network.chat.Component.literal("Boost items consumed in the input slot accelerate progress."),
				net.minecraft.network.chat.Component.literal("See \"Antimatter Constructor\" recipes for boost values."));

		registerReactorComponentInfo(r);
	}

	/**
	 * Adds JEI "info" cards for every nuclear-reactor component item, listing its cooling/heat/pulse
	 * stats so players can read the numbers without digging through tooltips or source.
	 */
	private static void registerReactorComponentInfo(IRecipeRegistration r) {
		var items = dev.ftb.mods.ftbic.item.FTBICItems.class;

		// Fuel rods
		rodInfo(r, dev.ftb.mods.ftbic.item.FTBICItems.URANIUM_FUEL_ROD.get(), 1, 5, 2, 20000);
		rodInfo(r, dev.ftb.mods.ftbic.item.FTBICItems.DUAL_URANIUM_FUEL_ROD.get(), 2, 10, 4, 20000);
		rodInfo(r, dev.ftb.mods.ftbic.item.FTBICItems.QUAD_URANIUM_FUEL_ROD.get(), 4, 20, 8, 20000);

		// Coolant cells — passive heat buffer
		coolantInfo(r, dev.ftb.mods.ftbic.item.FTBICItems.SMALL_COOLANT_CELL.get(), 10_000);
		coolantInfo(r, dev.ftb.mods.ftbic.item.FTBICItems.MEDIUM_COOLANT_CELL.get(), 30_000);
		coolantInfo(r, dev.ftb.mods.ftbic.item.FTBICItems.LARGE_COOLANT_CELL.get(), 60_000);

		// Heat vents (maxHeat, selfCool, reactorCool, componentCool)
		ventInfo(r, dev.ftb.mods.ftbic.item.FTBICItems.HEAT_VENT.get(), 1000, 6, 0, 0);
		ventInfo(r, dev.ftb.mods.ftbic.item.FTBICItems.ADVANCED_HEAT_VENT.get(), 1000, 12, 0, 0);
		ventInfo(r, dev.ftb.mods.ftbic.item.FTBICItems.REACTOR_HEAT_VENT.get(), 1000, 5, 5, 0);
		ventInfo(r, dev.ftb.mods.ftbic.item.FTBICItems.COMPONENT_HEAT_VENT.get(), 0, 0, 0, 4);
		ventInfo(r, dev.ftb.mods.ftbic.item.FTBICItems.OVERCLOCKED_HEAT_VENT.get(), 1000, 20, 36, 0);

		// Heat exchangers (maxHeat, toAdjacent, toCore)
		exchangerInfo(r, dev.ftb.mods.ftbic.item.FTBICItems.HEAT_EXCHANGER.get(), 2500, 12, 4);
		exchangerInfo(r, dev.ftb.mods.ftbic.item.FTBICItems.ADVANCED_HEAT_EXCHANGER.get(), 10_000, 24, 8);
		exchangerInfo(r, dev.ftb.mods.ftbic.item.FTBICItems.REACTOR_HEAT_EXCHANGER.get(), 5000, 0, 72);
		exchangerInfo(r, dev.ftb.mods.ftbic.item.FTBICItems.COMPONENT_HEAT_EXCHANGER.get(), 5000, 36, 0);

		// Plating (heat capacity bonus, explosion multiplier)
		platingInfo(r, dev.ftb.mods.ftbic.item.FTBICItems.REACTOR_PLATING.get(), 1000, 0.95);
		platingInfo(r, dev.ftb.mods.ftbic.item.FTBICItems.CONTAINMENT_REACTOR_PLATING.get(), 500, 0.90);
		platingInfo(r, dev.ftb.mods.ftbic.item.FTBICItems.HEAT_CAPACITY_REACTOR_PLATING.get(), 1700, 0.99);

		// Reflectors (durability; 0 = infinite)
		reflectorInfo(r, dev.ftb.mods.ftbic.item.FTBICItems.NEUTRON_REFLECTOR.get(), 30_000);
		reflectorInfo(r, dev.ftb.mods.ftbic.item.FTBICItems.THICK_NEUTRON_REFLECTOR.get(), 120_000);
		reflectorInfo(r, dev.ftb.mods.ftbic.item.FTBICItems.IRIDIUM_NEUTRON_REFLECTOR.get(), 0);
	}

	private static void rodInfo(IRecipeRegistration r, net.minecraft.world.item.Item item, int rods, double energyMult, double heatMult, int durability) {
		int pulses = rods == 1 ? 1 : rods == 2 ? 2 : 3;
		double baseEnergy = pulses * energyMult;
		double baseHeat = heatMult * pulses * (pulses + 1);
		r.addItemStackInfo(new net.minecraft.world.item.ItemStack(item),
				net.minecraft.network.chat.Component.literal("Nuclear fuel rod. " + rods + "-rod pack emits " + pulses + " pulse" + (pulses == 1 ? "" : "s") + "/cycle."),
				net.minecraft.network.chat.Component.literal("Energy: " + baseEnergy + " zap/t base (×(pulses+reflectors))."),
				net.minecraft.network.chat.Component.literal("Heat: " + baseHeat + "/cycle base — distributed into neighboring heat acceptors."),
				net.minecraft.network.chat.Component.literal("Durability: " + durability + " cycles before the rod is spent."));
	}

	private static void coolantInfo(IRecipeRegistration r, net.minecraft.world.item.Item item, int capacity) {
		r.addItemStackInfo(new net.minecraft.world.item.ItemStack(item),
				net.minecraft.network.chat.Component.literal("Passive heat buffer. Absorbs heat distributed by adjacent fuel rods."),
				net.minecraft.network.chat.Component.literal("Capacity: " + String.format("%,d", capacity) + " heat."),
				net.minecraft.network.chat.Component.literal("Pair with a Component Heat Vent to replenish durability each cycle."));
	}

	private static void ventInfo(IRecipeRegistration r, net.minecraft.world.item.Item item, int maxHeat, int selfCool, int reactorCool, int componentCool) {
		java.util.List<net.minecraft.network.chat.Component> lines = new java.util.ArrayList<>();
		lines.add(net.minecraft.network.chat.Component.literal("Heat vent — removes heat each reactor cycle."));
		if (maxHeat > 0) lines.add(net.minecraft.network.chat.Component.literal("Durability: " + maxHeat + " heat absorption."));
		if (selfCool > 0) lines.add(net.minecraft.network.chat.Component.literal("Self cooling: " + selfCool + "/cycle (heals own durability)."));
		if (reactorCool > 0) lines.add(net.minecraft.network.chat.Component.literal("Reactor cooling: " + reactorCool + "/cycle removed from reactor heat pool."));
		if (componentCool > 0) lines.add(net.minecraft.network.chat.Component.literal("Component cooling: " + componentCool + "/cycle to each adjacent coolant cell."));
		r.addItemStackInfo(new net.minecraft.world.item.ItemStack(item), lines.toArray(net.minecraft.network.chat.Component[]::new));
	}

	private static void exchangerInfo(IRecipeRegistration r, net.minecraft.world.item.Item item, int maxHeat, int toAdjacent, int toCore) {
		java.util.List<net.minecraft.network.chat.Component> lines = new java.util.ArrayList<>();
		lines.add(net.minecraft.network.chat.Component.literal("Heat exchanger — balances heat between neighbors and the reactor core."));
		lines.add(net.minecraft.network.chat.Component.literal("Durability: " + String.format("%,d", maxHeat) + " heat buffer."));
		if (toAdjacent > 0) lines.add(net.minecraft.network.chat.Component.literal("Adjacent transfer: up to " + toAdjacent + "/cycle per neighbor."));
		if (toCore > 0) lines.add(net.minecraft.network.chat.Component.literal("Core transfer: up to " + toCore + "/cycle to/from the reactor heat pool."));
	    r.addItemStackInfo(new net.minecraft.world.item.ItemStack(item), lines.toArray(net.minecraft.network.chat.Component[]::new));
	}

	private static void platingInfo(IRecipeRegistration r, net.minecraft.world.item.Item item, int heatCapacity, double explosionMod) {
		int pct = (int) Math.round((1.0 - explosionMod) * 100.0);
		r.addItemStackInfo(new net.minecraft.world.item.ItemStack(item),
				net.minecraft.network.chat.Component.literal("Reactor plating — modifies the reactor hull itself."),
				net.minecraft.network.chat.Component.literal("Max heat bonus: +" + String.format("%,d", heatCapacity) + " (stacks with other plating)."),
				net.minecraft.network.chat.Component.literal("Explosion dampening: ×" + explosionMod + " (-" + pct + "% radius per plating)."));
	}

	private static void reflectorInfo(IRecipeRegistration r, net.minecraft.world.item.Item item, int durability) {
		r.addItemStackInfo(new net.minecraft.world.item.ItemStack(item),
				net.minecraft.network.chat.Component.literal("Neutron reflector — bounces pulses back into adjacent fuel rods."),
				net.minecraft.network.chat.Component.literal("Each reflector adjacent to a rod adds +1 pulse (more energy AND more heat)."),
				net.minecraft.network.chat.Component.literal(durability == 0
						? "Durability: infinite (iridium-reinforced)."
						: "Durability: " + String.format("%,d", durability) + " pulses before the reflector burns out."));
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
