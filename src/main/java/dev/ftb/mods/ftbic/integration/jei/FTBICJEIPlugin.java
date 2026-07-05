package dev.ftb.mods.ftbic.integration.jei;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.machine.AntimatterConstructorBlockEntity;
import dev.ftb.mods.ftbic.client.gui.ReactorSimulatorScreen;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.item.FluidCellItem;
import dev.ftb.mods.ftbic.recipe.AntimatterBoostRecipe;
import dev.ftb.mods.ftbic.recipe.BasicGeneratorFuelRecipe;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import dev.ftb.mods.ftbic.recipe.MachineRecipe;
import dev.ftb.mods.ftbic.recipe.MachineRecipeType;
import dev.ftb.mods.ftbic.util.IngredientWithCount;
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
import java.util.Locale;

@JeiPlugin
public class FTBICJEIPlugin implements IModPlugin {
	public static final Identifier ID = FTBIC.id("jei");

	@Override
	public Identifier getPluginUid() {
		return ID;
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration r) {
		r.registerSubtypeInterpreter(FTBICItems.FLUID_CELL.get(), (stack, ctx) -> {
			FluidStack fs = FluidCellItem.getStored(stack);
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
		r.addRecipes(GeothermalFuelCategory.TYPE, List.of(GeothermalFuelCategory.defaultEntry()));

		long zapsPer = Math.round(AntimatterConstructorBlockEntity.PRODUCTION_THRESHOLD);
		r.addItemStackInfo(
				new ItemStack(FTBICItems.ANTIMATTER.item.get()),
				Component.translatable("ftbic.jei.antimatter.line1"),
				Component.translatable("ftbic.jei.antimatter.line2", fmtInt(zapsPer)),
				Component.translatable("ftbic.jei.antimatter.line3"),
				Component.translatable("ftbic.jei.antimatter.line4"));

		registerReactorComponentInfo(r);
	}

	private static void registerReactorComponentInfo(IRecipeRegistration r) {
		rodInfo(r, FTBICItems.URANIUM_FUEL_ROD.get(), 1, 5, 2, 20000);
		rodInfo(r, FTBICItems.DUAL_URANIUM_FUEL_ROD.get(), 2, 10, 4, 20000);
		rodInfo(r, FTBICItems.QUAD_URANIUM_FUEL_ROD.get(), 4, 20, 8, 20000);

		coolantInfo(r, FTBICItems.SMALL_COOLANT_CELL.get(), 10_000);
		coolantInfo(r, FTBICItems.MEDIUM_COOLANT_CELL.get(), 30_000);
		coolantInfo(r, FTBICItems.LARGE_COOLANT_CELL.get(), 60_000);

		ventInfo(r, FTBICItems.HEAT_VENT.get(), 1000, 6, 0, 0);
		ventInfo(r, FTBICItems.ADVANCED_HEAT_VENT.get(), 1000, 12, 0, 0);
		ventInfo(r, FTBICItems.REACTOR_HEAT_VENT.get(), 1000, 5, 5, 0);
		ventInfo(r, FTBICItems.COMPONENT_HEAT_VENT.get(), 0, 0, 0, 4);
		ventInfo(r, FTBICItems.OVERCLOCKED_HEAT_VENT.get(), 1000, 20, 36, 0);

		exchangerInfo(r, FTBICItems.HEAT_EXCHANGER.get(), 2500, 12, 4);
		exchangerInfo(r, FTBICItems.ADVANCED_HEAT_EXCHANGER.get(), 10_000, 24, 8);
		exchangerInfo(r, FTBICItems.REACTOR_HEAT_EXCHANGER.get(), 5000, 0, 72);
		exchangerInfo(r, FTBICItems.COMPONENT_HEAT_EXCHANGER.get(), 5000, 36, 0);

		platingInfo(r, FTBICItems.REACTOR_PLATING.get(), 1000, 0.95);
		platingInfo(r, FTBICItems.CONTAINMENT_REACTOR_PLATING.get(), 500, 0.90);
		platingInfo(r, FTBICItems.HEAT_CAPACITY_REACTOR_PLATING.get(), 1700, 0.99);

		reflectorInfo(r, FTBICItems.NEUTRON_REFLECTOR.get(), 30_000);
		reflectorInfo(r, FTBICItems.THICK_NEUTRON_REFLECTOR.get(), 120_000);
		reflectorInfo(r, FTBICItems.IRIDIUM_NEUTRON_REFLECTOR.get(), 0);
	}

	private static void rodInfo(IRecipeRegistration r, Item item, int rods, double energyMult, double heatMult, int durability) {
		int pulses = rods == 1 ? 1 : rods == 2 ? 2 : 3;
		double baseEnergy = pulses * energyMult;
		double baseHeat = heatMult * pulses * (pulses + 1);
		r.addItemStackInfo(new ItemStack(item),
				Component.translatable("ftbic.jei.rod.title"),
				Component.translatable("ftbic.jei.rod.desc", fmtInt(rods), fmtInt(pulses)),
				Component.translatable("ftbic.jei.rod.energy", fmt(baseEnergy)),
				Component.translatable("ftbic.jei.rod.heat", fmt(baseHeat)),
				Component.translatable("ftbic.jei.rod.durability", fmtInt(durability)));
	}

	private static void coolantInfo(IRecipeRegistration r, Item item, int capacity) {
		r.addItemStackInfo(new ItemStack(item),
				Component.translatable("ftbic.jei.coolant.title"),
				Component.translatable("ftbic.jei.coolant.desc"),
				Component.translatable("ftbic.jei.coolant.capacity", fmtInt(capacity)),
				Component.translatable("ftbic.jei.coolant.vent_pair"));
	}

	private static void ventInfo(IRecipeRegistration r, Item item, int maxHeat, int selfCool, int reactorCool, int componentCool) {
		List<Component> lines = new ArrayList<>();
		lines.add(Component.translatable("ftbic.jei.vent.title"));
		lines.add(Component.translatable("ftbic.jei.vent.desc"));
		if (maxHeat > 0) lines.add(Component.translatable("ftbic.jei.vent.durability", fmtInt(maxHeat)));
		if (selfCool > 0) lines.add(Component.translatable("ftbic.jei.vent.self_cool", fmtInt(selfCool)));
		if (reactorCool > 0) lines.add(Component.translatable("ftbic.jei.vent.reactor_cool", fmtInt(reactorCool)));
		if (componentCool > 0) lines.add(Component.translatable("ftbic.jei.vent.component_cool", fmtInt(componentCool)));
		r.addItemStackInfo(new ItemStack(item), lines.toArray(Component[]::new));
	}

	private static void exchangerInfo(IRecipeRegistration r, Item item, int maxHeat, int toAdjacent, int toCore) {
		List<Component> lines = new ArrayList<>();
		lines.add(Component.translatable("ftbic.jei.exchanger.title"));
		lines.add(Component.translatable("ftbic.jei.exchanger.desc"));
		lines.add(Component.translatable("ftbic.jei.exchanger.durability", fmtInt(maxHeat)));
		if (toAdjacent > 0) lines.add(Component.translatable("ftbic.jei.exchanger.adjacent", fmtInt(toAdjacent)));
		if (toCore > 0) lines.add(Component.translatable("ftbic.jei.exchanger.core", fmtInt(toCore)));
		r.addItemStackInfo(new ItemStack(item), lines.toArray(Component[]::new));
	}

	private static void platingInfo(IRecipeRegistration r, Item item, int heatCapacity, double explosionMod) {
		int pct = (int) Math.round((1.0 - explosionMod) * 100.0);
		r.addItemStackInfo(new ItemStack(item),
				Component.translatable("ftbic.jei.plating.title"),
				Component.translatable("ftbic.jei.plating.desc"),
				Component.translatable("ftbic.jei.plating.heat_bonus", fmtInt(heatCapacity)),
				Component.translatable("ftbic.jei.plating.explosion", fmt(explosionMod), fmtInt(pct)));
	}

	private static void reflectorInfo(IRecipeRegistration r, Item item, int durability) {
		r.addItemStackInfo(new ItemStack(item),
				Component.translatable("ftbic.jei.reflector.title"),
				Component.translatable("ftbic.jei.reflector.desc"),
				Component.translatable("ftbic.jei.reflector.pulse_effect"),
				durability == 0
						? Component.translatable("ftbic.jei.reflector.durability_infinite")
						: Component.translatable("ftbic.jei.reflector.durability", fmtInt(durability)));
	}

	private static String fmt(double v) {
		if (v == Math.floor(v) && !Double.isInfinite(v)) return String.valueOf((long) v);
		return String.format(Locale.ROOT, "%.2f", v);
	}

	public static String fmtInt(long v) {
		return String.format(Locale.ROOT, "%,d", v);
	}

	public static String fmtDouble(double v, int decimals) {
		return String.format(Locale.ROOT, "%." + decimals + "f", v);
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
		hideEmptyFor(jeiRm, FTBICRecipes.SMELTING);
		hideEmptyFor(jeiRm, FTBICRecipes.MACERATING);
		hideEmptyFor(jeiRm, FTBICRecipes.SEPARATING);
		hideEmptyFor(jeiRm, FTBICRecipes.COMPRESSING);
		hideEmptyFor(jeiRm, FTBICRecipes.REPROCESSING);
		hideEmptyFor(jeiRm, FTBICRecipes.CANNING);
		hideEmptyFor(jeiRm, FTBICRecipes.ROLLING);
		hideEmptyFor(jeiRm, FTBICRecipes.EXTRUDING);
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
		r.addCraftingStation(catalystType(FTBICRecipes.SMELTING), FTBICElectricBlocks.POWERED_FURNACE.block.get(), FTBICElectricBlocks.ADVANCED_POWERED_FURNACE.block.get());
		r.addCraftingStation(catalystType(FTBICRecipes.MACERATING), FTBICElectricBlocks.MACERATOR.block.get(), FTBICElectricBlocks.ADVANCED_MACERATOR.block.get());
		r.addCraftingStation(catalystType(FTBICRecipes.SEPARATING), FTBICElectricBlocks.CENTRIFUGE.block.get(), FTBICElectricBlocks.ADVANCED_CENTRIFUGE.block.get());
		r.addCraftingStation(catalystType(FTBICRecipes.COMPRESSING), FTBICElectricBlocks.COMPRESSOR.block.get(), FTBICElectricBlocks.ADVANCED_COMPRESSOR.block.get());
		r.addCraftingStation(catalystType(FTBICRecipes.REPROCESSING), FTBICElectricBlocks.REPROCESSOR.block.get());
		r.addCraftingStation(catalystType(FTBICRecipes.CANNING), FTBICElectricBlocks.CANNING_MACHINE.block.get());
		r.addCraftingStation(catalystType(FTBICRecipes.ROLLING), FTBICElectricBlocks.ROLLER.block.get());
		r.addCraftingStation(catalystType(FTBICRecipes.EXTRUDING), FTBICElectricBlocks.EXTRUDER.block.get());
		r.addCraftingStation(RecipeTypes.CRAFTING, FTBICElectricBlocks.POWERED_CRAFTING_TABLE.block.get());
		r.addCraftingStation(RecipeTypes.SMELTING, FTBICBlocks.IRON_FURNACE.get());
		r.addCraftingStation(basicGeneratorFuelType(), FTBICElectricBlocks.BASIC_GENERATOR.block.get());
		r.addCraftingStation(GeothermalFuelCategory.TYPE, FTBICElectricBlocks.GEOTHERMAL_GENERATOR.block.get());
		r.addCraftingStation(antimatterBoostType(), FTBICElectricBlocks.ANTIMATTER_CONSTRUCTOR.block.get());
	}

	private static IRecipeHolderType<MachineRecipe> catalystType(MachineRecipeType machine) {
		return IRecipeType.create(machine.TYPE.get());
	}

	@SuppressWarnings("unchecked")
	private static IRecipeHolderType<BasicGeneratorFuelRecipe> basicGeneratorFuelType() {
		return IRecipeType.create((RecipeType<BasicGeneratorFuelRecipe>)
				(RecipeType<?>) FTBICRecipes.BASIC_GENERATOR_FUEL.get());
	}

	@SuppressWarnings("unchecked")
	private static IRecipeHolderType<AntimatterBoostRecipe> antimatterBoostType() {
		return IRecipeType.create((RecipeType<AntimatterBoostRecipe>)
				(RecipeType<?>) FTBICRecipes.ANTIMATTER_BOOST.get());
	}
}
