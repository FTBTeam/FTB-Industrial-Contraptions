package dev.ftb.mods.ftbic.datagen;

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

		ShapedRecipeBuilder.shaped(REINFORCED_STONE, 4)
				.unlockedBy("has_item", has(ADVANCED_ALLOY))
				.group(MODID + ":reinforced_stone")
				.pattern("SSS")
				.pattern("SAS")
				.pattern("SSS")
				.define('S', SMOOTH_STONE)
				.define('A', ADVANCED_ALLOY)
				.save(consumer, shapedLoc("reinforced_stone"));

		ShapedRecipeBuilder.shaped(REINFORCED_GLASS, 4)
				.unlockedBy("has_item", has(REINFORCED_STONE))
				.group(MODID + ":reinforced_glass")
				.pattern("RGR")
				.pattern("G G")
				.pattern("RGR")
				.define('R', REINFORCED_STONE)
				.define('G', GLASS)
				.save(consumer, shapedLoc("reinforced_glass"));

		ShapedRecipeBuilder.shaped(NUCLEAR_REACTOR_CHAMBER, 3)
				.unlockedBy("has_item", has(ADVANCED_MACHINE_BLOCK))
				.group(MODID + ":nuclear_reactor_chamber")
				.pattern("HPH")
				.pattern("PMP")
				.pattern("HPH")
				.define('M', ADVANCED_MACHINE_BLOCK)
				.define('P', DENSE_COPPER_PLATE)
				.define('H', REACTOR_PLATING)
				.save(consumer, shapedLoc("nuclear_reactor_chamber"));

		ShapedRecipeBuilder.shaped(NUCLEAR_REACTOR)
				.unlockedBy("has_item", has(BASIC_GENERATOR))
				.group(MODID + ":nuclear_reactor")
				.pattern("HCH")
				.pattern("HHH")
				.pattern("HGH")
				.define('H', NUCLEAR_REACTOR_CHAMBER)
				.define('C', IRIDIUM_CIRCUIT)
				.define('G', BASIC_GENERATOR)
				.save(consumer, shapedLoc("nuclear_reactor"));

		ShapedRecipeBuilder.shaped(NUKE)
				.unlockedBy("has_item", has(COPPER_WIRE))
				.group(MODID + ":nuke")
				.pattern("UCU")
				.pattern("TMT")
				.pattern("UCU")
				.define('T', Items.TNT)
				.define('U', QUAD_URANIUM_FUEL_ROD)
				.define('M', ADVANCED_MACHINE_BLOCK)
				.define('C', ADVANCED_CIRCUIT)
				.save(consumer, shapedLoc("nuke"));

		ShapedRecipeBuilder.shaped(SMALL_COOLANT_CELL)
				.unlockedBy("has_item", has(FLUID_CELL))
				.group(MODID + ":small_coolant_cell")
				.pattern(" T ")
				.pattern("TCT")
				.pattern(" T ")
				.define('T', TIN_INGOT)
				.define('C', waterCell())
				.save(consumer, shapedLoc("small_coolant_cell"));

		ShapedRecipeBuilder.shaped(MEDIUM_COOLANT_CELL)
				.unlockedBy("has_item", has(SMALL_COOLANT_CELL))
				.group(MODID + ":medium_coolant_cell")
				.pattern("TTT")
				.pattern("CCC")
				.pattern("TTT")
				.define('T', TIN_INGOT)
				.define('C', unbroken(SMALL_COOLANT_CELL))
				.save(consumer, shapedLoc("medium_coolant_cell"));

		ShapedRecipeBuilder.shaped(LARGE_COOLANT_CELL)
				.unlockedBy("has_item", has(MEDIUM_COOLANT_CELL))
				.group(MODID + ":large_coolant_cell")
				.pattern("TCT")
				.pattern("TAT")
				.pattern("TCT")
				.define('T', TIN_INGOT)
				.define('C', unbroken(MEDIUM_COOLANT_CELL))
				.define('A', DENSE_COPPER_PLATE)
				.save(consumer, shapedLoc("large_coolant_cell"));

		ShapedRecipeBuilder.shaped(DUAL_URANIUM_FUEL_ROD)
				.unlockedBy("has_item", has(URANIUM_FUEL_ROD))
				.group(MODID + ":dual_uranium_fuel_rod")
				.pattern("RMR")
				.define('R', unbroken(URANIUM_FUEL_ROD))
				.define('M', DENSE_COPPER_PLATE)
				.save(consumer, shapedLoc("dual_uranium_fuel_rod"));

		ShapedRecipeBuilder.shaped(QUAD_URANIUM_FUEL_ROD)
				.unlockedBy("has_item", has(DUAL_URANIUM_FUEL_ROD))
				.group(MODID + ":quad_uranium_fuel_rod")
				.pattern(" R ")
				.pattern("MMM")
				.pattern(" R ")
				.define('R', unbroken(DUAL_URANIUM_FUEL_ROD))
				.define('M', DENSE_COPPER_PLATE)
				.save(consumer, shapedLoc("quad_uranium_fuel_rod"));

		ShapedRecipeBuilder.shaped(HEAT_VENT)
				.unlockedBy("has_item", has(COPPER_COIL))
				.group(MODID + ":heat_vent")
				.pattern("MIM")
				.pattern("ICI")
				.pattern("MIM")
				.define('I', Items.IRON_BARS)
				.define('C', COPPER_COIL)
				.define('M', INDUSTRIAL_GRADE_METAL)
				.save(consumer, shapedLoc("heat_vent"));

		ShapedRecipeBuilder.shaped(ADVANCED_HEAT_VENT)
				.unlockedBy("has_item", has(HEAT_VENT))
				.group(MODID + ":advanced_heat_vent")
				.pattern("IVI")
				.pattern("IDI")
				.pattern("IVI")
				.define('I', Items.IRON_BARS)
				.define('D', DIAMOND)
				.define('V', HEAT_VENT)
				.save(consumer, shapedLoc("advanced_heat_vent"));

		ShapedRecipeBuilder.shaped(REACTOR_HEAT_VENT)
				.unlockedBy("has_item", has(HEAT_VENT))
				.group(MODID + ":reactor_heat_vent")
				.pattern("P")
				.pattern("V")
				.pattern("P")
				.define('P', LEAD_PLATE)
				.define('V', HEAT_VENT)
				.save(consumer, shapedLoc("reactor_heat_vent"));

		ShapedRecipeBuilder.shaped(COMPONENT_HEAT_VENT)
				.unlockedBy("has_item", has(HEAT_VENT))
				.group(MODID + ":component_heat_vent")
				.pattern("IPI")
				.pattern("PVP")
				.pattern("IPI")
				.define('I', Items.IRON_BARS)
				.define('P', TIN_PLATE)
				.define('V', HEAT_VENT)
				.save(consumer, shapedLoc("component_heat_vent"));

		ShapedRecipeBuilder.shaped(OVERCLOCKED_HEAT_VENT)
				.unlockedBy("has_item", has(REACTOR_HEAT_VENT))
				.group(MODID + ":overclocked_heat_vent")
				.pattern("P")
				.pattern("V")
				.pattern("P")
				.define('P', ENDERIUM_PLATE)
				.define('V', REACTOR_HEAT_VENT)
				.save(consumer, shapedLoc("overclocked_heat_vent"));

		ShapedRecipeBuilder.shaped(HEAT_EXCHANGER)
				.unlockedBy("has_item", tagKeyHas.apply(IRON_PLATE))
				.group(MODID + ":heat_exchanger")
				.pattern(" C ")
				.pattern("PBP")
				.pattern(" P ")
				.define('C', ELECTRONIC_CIRCUIT)
				.define('B', DENSE_COPPER_PLATE)
				.define('P', IRON_PLATE)
				.save(consumer, shapedLoc("heat_exchanger"));

		ShapedRecipeBuilder.shaped(ADVANCED_HEAT_EXCHANGER)
				.unlockedBy("has_item", has(HEAT_EXCHANGER))
				.group(MODID + ":advanced_heat_exchanger")
				.pattern("WCW")
				.pattern("VBV")
				.pattern("WCW")
				.define('C', ELECTRONIC_CIRCUIT)
				.define('B', DENSE_COPPER_PLATE)
				.define('V', HEAT_EXCHANGER)
				.define('W', IV_CABLE)
				.save(consumer, shapedLoc("advanced_heat_exchanger"));

		ShapedRecipeBuilder.shaped(REACTOR_HEAT_EXCHANGER)
				.unlockedBy("has_item", has(HEAT_EXCHANGER))
				.group(MODID + ":reactor_heat_exchanger")
				.pattern("P")
				.pattern("V")
				.pattern("P")
				.define('P', LEAD_PLATE)
				.define('V', HEAT_EXCHANGER)
				.save(consumer, shapedLoc("reactor_heat_exchanger"));

		ShapedRecipeBuilder.shaped(COMPONENT_HEAT_EXCHANGER)
				.unlockedBy("has_item", has(HEAT_EXCHANGER))
				.group(MODID + ":component_heat_exchanger")
				.pattern(" P ")
				.pattern("PVP")
				.pattern(" P ")
				.define('P', TIN_PLATE)
				.define('V', HEAT_EXCHANGER)
				.save(consumer, shapedLoc("component_heat_exchanger"));

		ShapelessRecipeBuilder.shapeless(REACTOR_PLATING)
				.unlockedBy("", has(ADVANCED_ALLOY))
				.group(MODID + ":reactor_plating")
				.requires(ADVANCED_ALLOY)
				.requires(LEAD_PLATE)
				.requires(LEAD_PLATE)
				.save(consumer, shapelessLoc("reactor_plating"));

		ShapelessRecipeBuilder.shapeless(CONTAINMENT_REACTOR_PLATING)
				.unlockedBy("", has(REACTOR_PLATING))
				.group(MODID + ":containment_reactor_plating")
				.requires(REACTOR_PLATING)
				.requires(ADVANCED_ALLOY)
				.requires(ADVANCED_ALLOY)
				.save(consumer, shapelessLoc("containment_reactor_plating"));

		ShapelessRecipeBuilder.shapeless(HEAT_CAPACITY_REACTOR_PLATING)
				.unlockedBy("", has(REACTOR_PLATING))
				.group(MODID + ":heat_capacity_reactor_plating")
				.requires(REACTOR_PLATING)
				.requires(DENSE_COPPER_PLATE)
				.requires(DENSE_COPPER_PLATE)
				.save(consumer, shapelessLoc("heat_capacity_reactor_plating"));

		ShapedRecipeBuilder.shaped(NEUTRON_REFLECTOR)
				.unlockedBy("has_item", tagKeyHas.apply(TIN_DUST))
				.group(MODID + ":neutron_reflector")
				.pattern("TCT")
				.pattern("TPT")
				.pattern("TCT")
				.define('T', TIN_DUST)
				.define('C', COAL_DUST)
				.define('P', DENSE_COPPER_PLATE)
				.save(consumer, shapedLoc("neutron_reflector"));

		ShapedRecipeBuilder.shaped(THICK_NEUTRON_REFLECTOR)
				.unlockedBy("has_item", has(NEUTRON_REFLECTOR))
				.group(MODID + ":thick_neutron_reflector")
				.pattern("PNP")
				.pattern("NPN")
				.pattern("PNP")
				.define('N', NEUTRON_REFLECTOR)
				.define('P', COPPER_PLATE)
				.save(consumer, shapedLoc("thick_neutron_reflector"));

		ShapedRecipeBuilder.shaped(IRIDIUM_NEUTRON_REFLECTOR)
				.unlockedBy("has_item", has(THICK_NEUTRON_REFLECTOR))
				.group(MODID + ":iridium_neutron_reflector")
				.pattern("NNN")
				.pattern("NPN")
				.pattern("NNN")
				.define('N', THICK_NEUTRON_REFLECTOR)
				.define('P', IRIDIUM_ALLOY)
				.save(consumer, shapedLoc("iridium_neutron_reflector"));

		ShapelessRecipeBuilder.shapeless(NUKE_ARROW)
				.unlockedBy("", has(NUKE))
				.group(MODID + ":nuke_arrow")
				.requires(NUKE)
				.requires(Items.ARROW)
				.save(consumer, shapelessLoc("nuke_arrow"));

		MachineRecipeBuilder.separating()
				.unlockedBy("has_item", has(FLUID_CELL))
				.inputItem(waterCell())
				.outputItem(new ItemStack(SMALL_COOLANT_CELL))
				.save(consumer, separatingLoc("small_coolant_cell"));

		MachineRecipeBuilder.canning()
				.unlockedBy("has_item", tagKeyHas.apply(URANIUM_DUST))
				.inputItem(waterCell())
				.inputItem(Ingredient.of(URANIUM_DUST))
				.outputItem(new ItemStack(URANIUM_FUEL_ROD))
				.save(consumer, canningLoc("uranium_fuel_rod"));
	}
}
