package dev.ftb.mods.ftbic.datagen.recipes;

import dev.ftb.mods.ftbic.datagen.FTBICRecipesGen;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Consumer;
import java.util.function.Function;

public class FTBICNuclearRecipes extends FTBICRecipesGen {
	public FTBICNuclearRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		Function<TagKey<Item>, InventoryChangeTrigger.TriggerInstance> tagKeyHas = (e) -> RecipeProvider.inventoryTrigger(ItemPredicate.Builder.item().of(e).build());

		ShapedRecipeBuilder.shaped(REINFORCED_STONE.get(), 4)
				.unlockedBy("has_item", has(ADVANCED_ALLOY.get()))
				.group(MODID + ":reinforced_stone")
				.pattern("SSS")
				.pattern("SAS")
				.pattern("SSS")
				.define('S', SMOOTH_STONE)
				.define('A', ADVANCED_ALLOY.get())
				.save(consumer, shapedLoc("reinforced_stone"));

		ShapedRecipeBuilder.shaped(REINFORCED_GLASS.get(), 4)
				.unlockedBy("has_item", has(REINFORCED_STONE.get()))
				.group(MODID + ":reinforced_glass")
				.pattern("RGR")
				.pattern("G G")
				.pattern("RGR")
				.define('R', REINFORCED_STONE.get())
				.define('G', GLASS)
				.save(consumer, shapedLoc("reinforced_glass"));

		ShapedRecipeBuilder.shaped(NUCLEAR_REACTOR_CHAMBER.get(), 3)
				.unlockedBy("has_item", has(ADVANCED_MACHINE_BLOCK.get()))
				.group(MODID + ":nuclear_reactor_chamber")
				.pattern("HPH")
				.pattern("PMP")
				.pattern("HPH")
				.define('M', ADVANCED_MACHINE_BLOCK.get())
				.define('P', DENSE_COPPER_PLATE.get())
				.define('H', REACTOR_PLATING.get())
				.save(consumer, shapedLoc("nuclear_reactor_chamber"));

		ShapedRecipeBuilder.shaped(NUCLEAR_REACTOR.get())
				.unlockedBy("has_item", has(BASIC_GENERATOR.get()))
				.group(MODID + ":nuclear_reactor")
				.pattern("HCH")
				.pattern("HHH")
				.pattern("HGH")
				.define('H', NUCLEAR_REACTOR_CHAMBER.get())
				.define('C', IRIDIUM_CIRCUIT.get())
				.define('G', BASIC_GENERATOR.get())
				.save(consumer, shapedLoc("nuclear_reactor"));

		ShapedRecipeBuilder.shaped(NUKE.get())
				.unlockedBy("has_item", has(COPPER_WIRE.get()))
				.group(MODID + ":nuke")
				.pattern("UCU")
				.pattern("TMT")
				.pattern("UCU")
				.define('T', Items.TNT)
				.define('U', QUAD_URANIUM_FUEL_ROD.get())
				.define('M', ADVANCED_MACHINE_BLOCK.get())
				.define('C', ADVANCED_CIRCUIT.get())
				.save(consumer, shapedLoc("nuke"));

		ShapedRecipeBuilder.shaped(SMALL_COOLANT_CELL.get())
				.unlockedBy("has_item", has(FLUID_CELL.get()))
				.group(MODID + ":small_coolant_cell")
				.pattern(" T ")
				.pattern("TCT")
				.pattern(" T ")
				.define('T', TIN_INGOT)
				.define('C', waterCell())
				.save(consumer, shapedLoc("small_coolant_cell"));

		ShapedRecipeBuilder.shaped(MEDIUM_COOLANT_CELL.get())
				.unlockedBy("has_item", has(SMALL_COOLANT_CELL.get()))
				.group(MODID + ":medium_coolant_cell")
				.pattern("TTT")
				.pattern("CCC")
				.pattern("TTT")
				.define('T', TIN_INGOT)
				.define('C', unbroken(SMALL_COOLANT_CELL.get()))
				.save(consumer, shapedLoc("medium_coolant_cell"));

		ShapedRecipeBuilder.shaped(LARGE_COOLANT_CELL.get())
				.unlockedBy("has_item", has(MEDIUM_COOLANT_CELL.get()))
				.group(MODID + ":large_coolant_cell")
				.pattern("TCT")
				.pattern("TAT")
				.pattern("TCT")
				.define('T', TIN_INGOT)
				.define('C', unbroken(MEDIUM_COOLANT_CELL.get()))
				.define('A', DENSE_COPPER_PLATE.get())
				.save(consumer, shapedLoc("large_coolant_cell"));

		ShapedRecipeBuilder.shaped(DUAL_URANIUM_FUEL_ROD.get())
				.unlockedBy("has_item", has(URANIUM_FUEL_ROD.get()))
				.group(MODID + ":dual_uranium_fuel_rod")
				.pattern("RMR")
				.define('R', unbroken(URANIUM_FUEL_ROD.get()))
				.define('M', DENSE_COPPER_PLATE.get())
				.save(consumer, shapedLoc("dual_uranium_fuel_rod"));

		ShapedRecipeBuilder.shaped(QUAD_URANIUM_FUEL_ROD.get())
				.unlockedBy("has_item", has(DUAL_URANIUM_FUEL_ROD.get()))
				.group(MODID + ":quad_uranium_fuel_rod")
				.pattern(" R ")
				.pattern("MMM")
				.pattern(" R ")
				.define('R', unbroken(DUAL_URANIUM_FUEL_ROD.get()))
				.define('M', DENSE_COPPER_PLATE.get())
				.save(consumer, shapedLoc("quad_uranium_fuel_rod"));

		ShapedRecipeBuilder.shaped(HEAT_VENT.get())
				.unlockedBy("has_item", has(COPPER_COIL.get()))
				.group(MODID + ":heat_vent")
				.pattern("MIM")
				.pattern("ICI")
				.pattern("MIM")
				.define('I', Items.IRON_BARS)
				.define('C', COPPER_COIL.get())
				.define('M', INDUSTRIAL_GRADE_METAL.get())
				.save(consumer, shapedLoc("heat_vent"));

		ShapedRecipeBuilder.shaped(ADVANCED_HEAT_VENT.get())
				.unlockedBy("has_item", has(HEAT_VENT.get()))
				.group(MODID + ":advanced_heat_vent")
				.pattern("IVI")
				.pattern("IDI")
				.pattern("IVI")
				.define('I', Items.IRON_BARS)
				.define('D', DIAMOND)
				.define('V', HEAT_VENT.get())
				.save(consumer, shapedLoc("advanced_heat_vent"));

		ShapedRecipeBuilder.shaped(REACTOR_HEAT_VENT.get())
				.unlockedBy("has_item", has(HEAT_VENT.get()))
				.group(MODID + ":reactor_heat_vent")
				.pattern("P")
				.pattern("V")
				.pattern("P")
				.define('P', LEAD_PLATE)
				.define('V', HEAT_VENT.get())
				.save(consumer, shapedLoc("reactor_heat_vent"));

		ShapedRecipeBuilder.shaped(COMPONENT_HEAT_VENT.get())
				.unlockedBy("has_item", has(HEAT_VENT.get()))
				.group(MODID + ":component_heat_vent")
				.pattern("IPI")
				.pattern("PVP")
				.pattern("IPI")
				.define('I', Items.IRON_BARS)
				.define('P', TIN_PLATE)
				.define('V', HEAT_VENT.get())
				.save(consumer, shapedLoc("component_heat_vent"));

		ShapedRecipeBuilder.shaped(OVERCLOCKED_HEAT_VENT.get())
				.unlockedBy("has_item", has(REACTOR_HEAT_VENT.get()))
				.group(MODID + ":overclocked_heat_vent")
				.pattern("P")
				.pattern("V")
				.pattern("P")
				.define('P', ENDERIUM_PLATE)
				.define('V', REACTOR_HEAT_VENT.get())
				.save(consumer, shapedLoc("overclocked_heat_vent"));

		ShapedRecipeBuilder.shaped(HEAT_EXCHANGER.get())
				.unlockedBy("has_item", tagKeyHas.apply(IRON_PLATE))
				.group(MODID + ":heat_exchanger")
				.pattern(" C ")
				.pattern("PBP")
				.pattern(" P ")
				.define('C', ELECTRONIC_CIRCUIT.get())
				.define('B', DENSE_COPPER_PLATE.get())
				.define('P', IRON_PLATE)
				.save(consumer, shapedLoc("heat_exchanger"));

		ShapedRecipeBuilder.shaped(ADVANCED_HEAT_EXCHANGER.get())
				.unlockedBy("has_item", has(HEAT_EXCHANGER.get()))
				.group(MODID + ":advanced_heat_exchanger")
				.pattern("WCW")
				.pattern("VBV")
				.pattern("WCW")
				.define('C', ELECTRONIC_CIRCUIT.get())
				.define('B', DENSE_COPPER_PLATE.get())
				.define('V', HEAT_EXCHANGER.get())
				.define('W', IV_CABLE.get())
				.save(consumer, shapedLoc("advanced_heat_exchanger"));

		ShapedRecipeBuilder.shaped(REACTOR_HEAT_EXCHANGER.get())
				.unlockedBy("has_item", has(HEAT_EXCHANGER.get()))
				.group(MODID + ":reactor_heat_exchanger")
				.pattern("P")
				.pattern("V")
				.pattern("P")
				.define('P', LEAD_PLATE)
				.define('V', HEAT_EXCHANGER.get())
				.save(consumer, shapedLoc("reactor_heat_exchanger"));

		ShapedRecipeBuilder.shaped(COMPONENT_HEAT_EXCHANGER.get())
				.unlockedBy("has_item", has(HEAT_EXCHANGER.get()))
				.group(MODID + ":component_heat_exchanger")
				.pattern(" P ")
				.pattern("PVP")
				.pattern(" P ")
				.define('P', TIN_PLATE)
				.define('V', HEAT_EXCHANGER.get())
				.save(consumer, shapedLoc("component_heat_exchanger"));

		ShapelessRecipeBuilder.shapeless(REACTOR_PLATING.get())
				.unlockedBy("", has(ADVANCED_ALLOY.get()))
				.group(MODID + ":reactor_plating")
				.requires(ADVANCED_ALLOY.get())
				.requires(LEAD_PLATE)
				.requires(LEAD_PLATE)
				.save(consumer, shapelessLoc("reactor_plating"));

		ShapelessRecipeBuilder.shapeless(CONTAINMENT_REACTOR_PLATING.get())
				.unlockedBy("", has(REACTOR_PLATING.get()))
				.group(MODID + ":containment_reactor_plating")
				.requires(REACTOR_PLATING.get())
				.requires(ADVANCED_ALLOY.get())
				.requires(ADVANCED_ALLOY.get())
				.save(consumer, shapelessLoc("containment_reactor_plating"));

		ShapelessRecipeBuilder.shapeless(HEAT_CAPACITY_REACTOR_PLATING.get())
				.unlockedBy("", has(REACTOR_PLATING.get()))
				.group(MODID + ":heat_capacity_reactor_plating")
				.requires(REACTOR_PLATING.get())
				.requires(DENSE_COPPER_PLATE.get())
				.requires(DENSE_COPPER_PLATE.get())
				.save(consumer, shapelessLoc("heat_capacity_reactor_plating"));

		ShapedRecipeBuilder.shaped(NEUTRON_REFLECTOR.get())
				.unlockedBy("has_item", tagKeyHas.apply(TIN_DUST))
				.group(MODID + ":neutron_reflector")
				.pattern("TCT")
				.pattern("TPT")
				.pattern("TCT")
				.define('T', TIN_DUST)
				.define('C', COAL_DUST)
				.define('P', DENSE_COPPER_PLATE.get())
				.save(consumer, shapedLoc("neutron_reflector"));

		ShapedRecipeBuilder.shaped(THICK_NEUTRON_REFLECTOR.get())
				.unlockedBy("has_item", has(NEUTRON_REFLECTOR.get()))
				.group(MODID + ":thick_neutron_reflector")
				.pattern("PNP")
				.pattern("NPN")
				.pattern("PNP")
				.define('N', NEUTRON_REFLECTOR.get())
				.define('P', COPPER_PLATE)
				.save(consumer, shapedLoc("thick_neutron_reflector"));

		ShapedRecipeBuilder.shaped(IRIDIUM_NEUTRON_REFLECTOR.get())
				.unlockedBy("has_item", has(THICK_NEUTRON_REFLECTOR.get()))
				.group(MODID + ":iridium_neutron_reflector")
				.pattern("NNN")
				.pattern("NPN")
				.pattern("NNN")
				.define('N', THICK_NEUTRON_REFLECTOR.get())
				.define('P', IRIDIUM_ALLOY.get())
				.save(consumer, shapedLoc("iridium_neutron_reflector"));

		ShapelessRecipeBuilder.shapeless(NUKE_ARROW.get())
				.unlockedBy("", has(NUKE.get()))
				.group(MODID + ":nuke_arrow")
				.requires(NUKE.get())
				.requires(Items.ARROW)
				.save(consumer, shapelessLoc("nuke_arrow"));

		MachineRecipeBuilder.separating()
				.unlockedBy("has_item", has(FLUID_CELL.get()))
				.inputItem(waterCell())
				.outputItem(new ItemStack(SMALL_COOLANT_CELL.get()))
				.save(consumer, separatingLoc("small_coolant_cell"));

		MachineRecipeBuilder.canning()
				.unlockedBy("has_item", tagKeyHas.apply(URANIUM_DUST))
				.inputItem(waterCell())
				.inputItem(Ingredient.of(URANIUM_DUST))
				.outputItem(new ItemStack(URANIUM_FUEL_ROD.get()))
				.save(consumer, canningLoc("uranium_fuel_rod"));
	}
}
