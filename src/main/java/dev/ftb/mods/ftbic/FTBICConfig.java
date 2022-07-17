package dev.ftb.mods.ftbic;

import dev.ftb.mods.ftbic.util.CraftingMaterial;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.common.ForgeConfigSpec.LongValue;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FTBICConfig {
	public static Component ENERGY_FORMAT = Component.literal("⚡").withStyle(ChatFormatting.BOLD);
	public static Component HEAT_FORMAT = Component.literal("\uD83D\uDD25");

	static final ForgeConfigSpec COMMON_CONFIG;
	private static final Builder COMMON = new Builder();

	public static final Energy ENERGY;
	public static final Machines MACHINES;
	public static final Nuclear NUCLEAR;
	public static final Equipment EQUIPMENT;
	public static final Recipes RECIPES;

	static {
		ENERGY = new Energy();
		MACHINES = new Machines();
		NUCLEAR = new Nuclear();
		EQUIPMENT = new Equipment();
		RECIPES = new Recipes();

		COMMON_CONFIG = COMMON.build();
	}


	public static final class Equipment {
		public final DoubleValue CARBON_ARMOR_CAPACITY;
		public final DoubleValue QUANTUM_ARMOR_CAPACITY;
		public final DoubleValue MECHANICAL_ELYTRA_CAPACITY;
		public final DoubleValue MECHANICAL_ELYTRA_RECHARGE;
		public final DoubleValue ARMOR_DAMAGE_ENERGY;
		public final DoubleValue ARMOR_FLIGHT_ENERGY;
		public final DoubleValue ARMOR_FLIGHT_BOOST;
		public final DoubleValue ARMOR_FLIGHT_STOP;

		public Equipment() {
			b().push("Equipment");

			CARBON_ARMOR_CAPACITY = b().comment("Max energy the Carbon armor can contain").defineInRange("Carbon armor capacity", 1_000_000D, 0D, Double.POSITIVE_INFINITY);
			QUANTUM_ARMOR_CAPACITY = b().comment("Max energy the Quantum armor can contain").defineInRange("Quantum armor capacity", 15_000_000D, 0D, Double.POSITIVE_INFINITY);
			MECHANICAL_ELYTRA_CAPACITY = b().comment("Max energy that the elytra can contain").defineInRange("Mechanical elytra capacity", 50_000D, 0D, Double.POSITIVE_INFINITY);
			MECHANICAL_ELYTRA_RECHARGE = b().comment("How much power is recharged passively").defineInRange("Mechanical elytra recharge", 1D, 0D, 100_000D);
			ARMOR_DAMAGE_ENERGY = b().comment("Armor damage energy").defineInRange("Armor damage energy", 5_000D, 0D, Double.POSITIVE_INFINITY);
			ARMOR_FLIGHT_ENERGY = b().comment("Armor flight engery").defineInRange("Armor flight energy", 5D, 0D, Double.POSITIVE_INFINITY);
			ARMOR_FLIGHT_BOOST = b().comment("Boost gained from wearing the armor during flight").defineInRange("Armor flight boost", 50D, 0D, 100_000D);
			ARMOR_FLIGHT_STOP = b().comment("Force used to stop flight").defineInRange("Armor flight stop", 15D, 0D, 100_000D);

			b().pop();
		}
	}

	public static final class Energy {
		public final DoubleValue LV_TRANSFER_RATE;
		public final DoubleValue MV_TRANSFER_RATE;
		public final DoubleValue HV_TRANSFER_RATE;
		public final DoubleValue EV_TRANSFER_RATE;
		public final DoubleValue IV_TRANSFER_RATE;
		public final DoubleValue ZAP_TO_FE_CONVERSION_RATE;
		public final DoubleValue SINGLE_USE_BATTERY_CAPACITY;
		public final DoubleValue LV_BATTERY_CAPACITY;
		public final DoubleValue MV_BATTERY_CAPACITY;
		public final DoubleValue HV_BATTERY_CAPACITY;
		public final DoubleValue EV_BATTERY_CAPACITY;
		public final DoubleValue LV_BATTERY_BOX_CAPACITY;
		public final DoubleValue MV_BATTERY_BOX_CAPACITY;
		public final DoubleValue HV_BATTERY_BOX_CAPACITY;
		public final DoubleValue EV_BATTERY_BOX_CAPACITY;
		public final IntValue MAX_CABLE_LENGTH;

		public Energy() {
			b().push("Energy");

			LV_TRANSFER_RATE = b().comment("Lv Transfer Rate (how much energy is transferred per tick)").defineInRange("Lv Transfer Rate", 32D, 1D, Double.POSITIVE_INFINITY);
			MV_TRANSFER_RATE = b().comment("Mv Transfer Rate (how much energy is transferred per tick)").defineInRange("Mv Transfer Rate", 128D, 1D, Double.POSITIVE_INFINITY);
			HV_TRANSFER_RATE = b().comment("Hv Transfer Rate (how much energy is transferred per tick)").defineInRange("Hv Transfer Rate", 512D, 1D, Double.POSITIVE_INFINITY);
			EV_TRANSFER_RATE = b().comment("Ev Transfer Rate (how much energy is transferred per tick)").defineInRange("Ev Transfer Rate", 2_048D, 1D, Double.POSITIVE_INFINITY);
			IV_TRANSFER_RATE = b().comment("Iv Transfer Rate (how much energy is transferred per tick)").defineInRange("Iv Transfer Rate", 8_192D, 1D, Double.POSITIVE_INFINITY);
			ZAP_TO_FE_CONVERSION_RATE = b().comment("The amount of Zaps to FE").defineInRange("Zap To Fe Conversion Rate", 4.0D, 0D, Double.POSITIVE_INFINITY);
			SINGLE_USE_BATTERY_CAPACITY = b().comment("The amount of energy a single use battery can contain").defineInRange("Single Use Battery Capacity", 2_400D, 1D, Double.POSITIVE_INFINITY);
			LV_BATTERY_CAPACITY = b().comment("Energy a Lv Battery can contain").defineInRange("Lv Battery Capacity", 4_000D, 1D, Double.POSITIVE_INFINITY);
			MV_BATTERY_CAPACITY = b().comment("Energy a Mv Battery can contain").defineInRange("Mv Battery Capacity", 40_000D, 1D, Double.POSITIVE_INFINITY);
			HV_BATTERY_CAPACITY = b().comment("Energy a Hv Battery can contain").defineInRange("Hv Battery Capacity", 400_000D, 1D, Double.POSITIVE_INFINITY);
			EV_BATTERY_CAPACITY = b().comment("Energy a Ev Battery can contain").defineInRange("Ev Battery Capacity", 10_000_000D, 1D, Double.POSITIVE_INFINITY);
			LV_BATTERY_BOX_CAPACITY = b().comment("Energy a Lv Battery Box can contain").defineInRange("Lv Battery Box Capacity", 40_000D, 1D, Double.POSITIVE_INFINITY);
			MV_BATTERY_BOX_CAPACITY = b().comment("Energy a Mv Battery Box can contain").defineInRange("Mv Battery Box Capacity", 400_000D, 1D, Double.POSITIVE_INFINITY);
			HV_BATTERY_BOX_CAPACITY = b().comment("Energy a Hv Battery Box can contain").defineInRange("Hv Battery Box Capacity", 4_000_000D, 1D, Double.POSITIVE_INFINITY);
			EV_BATTERY_BOX_CAPACITY = b().comment("Energy a Ev Battery Box can contain").defineInRange("Ev Battery Box Capacity", 40_000_000D, 1D, Double.POSITIVE_INFINITY);
			MAX_CABLE_LENGTH = b().comment("Max length of a cable chain", "The higher the number, the more tick lag will likely present.").defineInRange("Max Cable Length", 300, 1, 100_000);

			b().pop();
		}
	}

	public static final class Machines {
		public final IntValue IRON_FURNACE_ITEMS_PER_COAL;
		public final DoubleValue BASIC_GENERATOR_CAPACITY;
		public final DoubleValue BASIC_GENERATOR_OUTPUT;
		public final IntValue GEOTHERMAL_GENERATOR_TANK_SIZE;
		public final DoubleValue GEOTHERMAL_GENERATOR_CAPACITY;
		public final DoubleValue GEOTHERMAL_GENERATOR_OUTPUT;
		public final DoubleValue WIND_MILL_CAPACITY;
		public final DoubleValue WIND_MILL_MIN_OUTPUT;
		public final DoubleValue WIND_MILL_MAX_OUTPUT;
		public final IntValue WIND_MILL_MIN_Y;
		public final IntValue WIND_MILL_MAX_Y;
		public final DoubleValue WIND_MILL_RAIN_MODIFIER;
		public final DoubleValue WIND_MILL_THUNDER_MODIFIER;
		public final DoubleValue LV_SOLAR_PANEL_OUTPUT;
		public final DoubleValue MV_SOLAR_PANEL_OUTPUT;
		public final DoubleValue HV_SOLAR_PANEL_OUTPUT;
		public final DoubleValue EV_SOLAR_PANEL_OUTPUT;
		public final DoubleValue LV_SOLAR_PANEL_CAPACITY;
		public final DoubleValue MV_SOLAR_PANEL_CAPACITY;
		public final DoubleValue HV_SOLAR_PANEL_CAPACITY;
		public final DoubleValue EV_SOLAR_PANEL_CAPACITY;
		public final DoubleValue NUCLEAR_REACTOR_CAPACITY;
		public final DoubleValue MACHINE_RECIPE_BASE_TICKS;
		public final DoubleValue POWERED_FURNACE_CAPACITY;
		public final DoubleValue POWERED_FURNACE_USE;
		public final DoubleValue MACERATOR_CAPACITY;
		public final DoubleValue MACERATOR_USE;
		public final DoubleValue CENTRIFUGE_CAPACITY;
		public final DoubleValue CENTRIFUGE_USE;
		public final DoubleValue COMPRESSOR_CAPACITY;
		public final DoubleValue COMPRESSOR_USE;
		public final DoubleValue REPROCESSOR_CAPACITY;
		public final DoubleValue REPROCESSOR_USE;
		public final DoubleValue CANNING_MACHINE_CAPACITY;
		public final DoubleValue CANNING_MACHINE_USE;
		public final DoubleValue ROLLER_CAPACITY;
		public final DoubleValue ROLLER_USE;
		public final DoubleValue EXTRUDER_CAPACITY;
		public final DoubleValue EXTRUDER_USE;
		public final DoubleValue ANTIMATTER_CONSTRUCTOR_CAPACITY;
		public final DoubleValue ANTIMATTER_CONSTRUCTOR_BOOST;
		public final DoubleValue ADVANCED_POWERED_FURNACE_CAPACITY;
		public final DoubleValue ADVANCED_POWERED_FURNACE_USE;
		public final DoubleValue ADVANCED_MACERATOR_CAPACITY;
		public final DoubleValue ADVANCED_MACERATOR_USE;
		public final DoubleValue ADVANCED_CENTRIFUGE_CAPACITY;
		public final DoubleValue ADVANCED_CENTRIFUGE_USE;
		public final DoubleValue ADVANCED_COMPRESSOR_CAPACITY;
		public final DoubleValue ADVANCED_COMPRESSOR_USE;
		public final DoubleValue TELEPORTER_CAPACITY;
		public final DoubleValue TELEPORTER_MIN_USE;
		public final DoubleValue TELEPORTER_MIN_DISTANCE;
		public final DoubleValue TELEPORTER_MAX_USE;
		public final DoubleValue TELEPORTER_MAX_DISTANCE;
		public final DoubleValue CHARGE_PAD_CAPACITY;
		public final DoubleValue POWERED_CRAFTING_TABLE_CAPACITY;
		public final DoubleValue POWERED_CRAFTING_TABLE_USE;
		public final DoubleValue QUARRY_CAPACITY;
		public final DoubleValue QUARRY_USE;
		public final LongValue QUARRY_MINE_TICKS;
		public final LongValue QUARRY_MOVE_TICKS;
		public final DoubleValue PUMP_CAPACITY;
		public final DoubleValue PUMP_USE;
		public final LongValue PUMP_MINE_TICKS;
		public final LongValue PUMP_MOVE_TICKS;
		public final IntValue PUMP_TANK_CAPACITY;
		public final DoubleValue ITEM_TRANSFER_EFFICIENCY;
		public final IntValue STATE_UPDATE_TICKS;
		public final IntValue UPGRADE_LIMIT_PER_SLOT;
		public final DoubleValue OVERCLOCKER_SPEED;
		public final DoubleValue OVERCLOCKER_ENERGY_USE;
		public final DoubleValue STORAGE_UPGRADE;
		public final DoubleValue SCRAP_CHANCE;

		public Machines() {
			b().push("Machines");

			IRON_FURNACE_ITEMS_PER_COAL = b().comment("How many items are processed per fuel in the Iron Furnace").defineInRange("Items processed per fuel", 12, 1, 1_000);
			BASIC_GENERATOR_CAPACITY = b().comment("Amount of energy is stored in the Basic generator").defineInRange("Basic generator capacity", 4_000D, 1D, 100_000D);
			BASIC_GENERATOR_OUTPUT = b().comment("Energy created from the Basic generator").defineInRange("Basic generator output", 10D, 1D, 100_000D);
			GEOTHERMAL_GENERATOR_TANK_SIZE = b().comment("The internal size of the Geothermal generator").defineInRange("Geothermal generator tank size", 8_000, 1_000, 10_000);
			GEOTHERMAL_GENERATOR_CAPACITY = b().comment("Amount of energy stored in the Geothermal Generator").defineInRange("Geothermal generator capacity", 2_400D, 1D, 100_000D);
			GEOTHERMAL_GENERATOR_OUTPUT = b().comment("Energy created from the Geothermal generator").defineInRange("Geothermal generator output", 20D, 1D, 100_000D);
			WIND_MILL_CAPACITY = b().comment("Amount of energy stored in the Wind Mill").defineInRange("Wind mill capacity", 100D, 1D, 100_000D);
			WIND_MILL_MIN_OUTPUT = b().comment("The min amount of energy the wind mill can output").defineInRange("Wind mill min output", 0.3D, 0.001D, 100_000D);
			WIND_MILL_MAX_OUTPUT = b().comment("The max amount of energy the wind mill can output").defineInRange("Wind mill max output", 6.5D, 0.001D, 1000_000D);
			WIND_MILL_MIN_Y = b().comment("Minimum height that the windmill is effective from").defineInRange("Wind mill min y", 64, 0, 250);
			WIND_MILL_MAX_Y = b().comment("Maximum height that the windmill is effective from ").defineInRange("Wind mill max y", 320, 0, 320);
			WIND_MILL_RAIN_MODIFIER = b().comment("How much rain effects the energy production").defineInRange("Wind mill rain modifier", 1.2D, 0.001D, 5_000D);
			WIND_MILL_THUNDER_MODIFIER = b().comment("How much thunder effects the energy production").defineInRange("Wind mill thunder modifier", 1.5D, 0.001D, 5_000D);
			LV_SOLAR_PANEL_OUTPUT = b().comment("Energy created from the Lv solar panel").defineInRange("Lv solar panel output", 1D, 1D, 100_000D);
			MV_SOLAR_PANEL_OUTPUT = b().comment("Energy created from the Mv solar panel").defineInRange("Mv solar panel output", 8D, 1D, 100_000D);
			HV_SOLAR_PANEL_OUTPUT = b().comment("Energy created from the Hv solar panel").defineInRange("Hv solar panel output", 64D, 1D, 100_000D);
			EV_SOLAR_PANEL_OUTPUT = b().comment("Energy created from the Ev solar panel").defineInRange("Ev solar panel output", 512D, 1D, 100_000D);
			LV_SOLAR_PANEL_CAPACITY = b().comment("Amount of energy stored in the Lv solar panel").defineInRange("Lv solar panel capacity", 60D, 1D, 100_000D);
			MV_SOLAR_PANEL_CAPACITY = b().comment("Amount of energy stored in the Mv solar panel").defineInRange("Mv solar panel capacity", 480D, 1D, 100_000D);
			HV_SOLAR_PANEL_CAPACITY = b().comment("Amount of energy stored inm the Hv solar panel").defineInRange("Hv solar panel capacity", 3840D, 1D, 100_000D);
			EV_SOLAR_PANEL_CAPACITY = b().comment("Amount of energy stored in the Ev solar panel").defineInRange("Ev solar panel capacity", 30720D, 1D, 100_000D);
			NUCLEAR_REACTOR_CAPACITY = b().comment("Amount of energy stored in the Nuclear Reactor").defineInRange("Nuclear reactor capacity", 50_000D, 1D, 100_000D);
			MACHINE_RECIPE_BASE_TICKS = b().comment("Base lengths in ticks a machine takes to process a recipe").defineInRange("Machine recipe base ticks", 200D, 1D, 100_000D);
			POWERED_FURNACE_CAPACITY = b().comment("Amount of energy stored in the Powered Furnace").defineInRange("Powered furnace capacity", 1_200D, 1D, 100_000D);
			POWERED_FURNACE_USE = b().comment("Energy usage per operation of the powered furnace").defineInRange("Powered furnace use", 3D, 0D, 100_000D);
			MACERATOR_CAPACITY = b().comment("Amount of energy stored in the Macerator").defineInRange("Macerator capacity", 1_200D, 1D, 100_000D);
			MACERATOR_USE = b().comment("Energy usage of the Macerator").defineInRange("Macerator use", 2D, 0D, 100_000D);
			CENTRIFUGE_CAPACITY = b().comment("Amount of energy stored in the Centrifuge").defineInRange("Centrifuge capacity", 1_200D, 1D, 100_000D);
			CENTRIFUGE_USE = b().comment("Energy usage of the Centrifuge").defineInRange("Centrifuge use", 2D, 0D, 100_000D);
			COMPRESSOR_CAPACITY = b().comment("Amount of energy stored in the Compressor").defineInRange("Compressor capacity", 1_200D, 1D, 100_000D);
			COMPRESSOR_USE = b().comment("Energy usage of the Compressor").defineInRange("Compressor use", 2D, 0D, 100_000D);
			REPROCESSOR_CAPACITY = b().comment("Amount of energy stored in the Reprocessor").defineInRange("Reprocessor capacity", 4_000D, 1D, 100_000D);
			REPROCESSOR_USE = b().comment("Energy usage of the Reprocessor").defineInRange("Reprocessor use", 8D, 0D, 100_000D);
			CANNING_MACHINE_CAPACITY = b().comment("Amount of energy stored in the Canning Machine").defineInRange("Canning machine capacity", 1_200D, 1D, 100_000D);
			CANNING_MACHINE_USE = b().comment("Energy usage of the Canning machine").defineInRange("Canning machine use", 1D, 0D, 100_000D);
			ROLLER_CAPACITY = b().comment("Amount of energy stored in the Roller").defineInRange("Roller capacity", 1_200D, 1D, 100_000D);
			ROLLER_USE = b().comment("Energy usage of the Roller").defineInRange("Roller use", 3D, 0D, 100_000D);
			EXTRUDER_CAPACITY = b().comment("Amount of energy stored in the Extruder").defineInRange("Extruder capacity", 1_200D, 1D, 100_000D);
			EXTRUDER_USE = b().comment("Energy usage of the Extruder").defineInRange("Extruder use", 3D, 0D, 100_000D);
			ANTIMATTER_CONSTRUCTOR_CAPACITY = b().comment("Amount of energy stored in the Antimatter Constructor").defineInRange("Antimatter constructor capacity", 1_000_000D, 1D, 100_000D);
			ANTIMATTER_CONSTRUCTOR_BOOST = b().comment("Construction boost multiplier").defineInRange("Antimatter constructor boost", 6D, 1D, 100_000D);
			ADVANCED_POWERED_FURNACE_CAPACITY = b().comment("Amount of energy stored in the Advanced Powered Furnace").defineInRange("Advanced powered furnace capacity", 10_000D, 1D, 100_000D);
			ADVANCED_POWERED_FURNACE_USE = b().comment("Energy usage of the Advanced powered furnace").defineInRange("Advanced powered furnace use", 16D, 0D, 100_000D);
			ADVANCED_MACERATOR_CAPACITY = b().comment("Amount of energy stored in the Advanced Macerator").defineInRange("Advanced macerator capacity", 10_000D, 1D, 100_000D);
			ADVANCED_MACERATOR_USE = b().comment("Energy usage of the Advanced macerator").defineInRange("Advanced macerator use", 16D, 0D, 100_000D);
			ADVANCED_CENTRIFUGE_CAPACITY = b().comment("Amount of energy stored in the Advanced Centrifuge").defineInRange("Advanced centrifuge capacity", 10_000D, 1D, 100_000D);
			ADVANCED_CENTRIFUGE_USE = b().comment("Energy usage of the Advanced centrifuge").defineInRange("Advanced centrifuge use", 16D, 0D, 100_000D);
			ADVANCED_COMPRESSOR_CAPACITY = b().comment("Amount of energy stored in the Advanced Compressor").defineInRange("Advanced compressor capacity", 10_000D, 1D, 100_000D);
			ADVANCED_COMPRESSOR_USE = b().comment("Energy usage of the Advanced compressor").defineInRange("Advanced compressor use", 16D, 0D, 100_000D);
			TELEPORTER_CAPACITY = b().comment("Amount of energy stored in the Teleporter").defineInRange("Teleporter capacity", 1_000_000D, 1D, 100_000D);
			TELEPORTER_MIN_USE = b().comment("Energy usage of the Teleporter min").defineInRange("Teleporter min use", 100D, 0D, 100_000D);
			TELEPORTER_MIN_DISTANCE = b().comment("Min distance a teleporter can work over").defineInRange("Teleporter min distance", 16D, 1D, 100_000D);
			TELEPORTER_MAX_USE = b().comment("Energy usage of the Teleporter max").defineInRange("Teleporter max use", 10_000D, 0D, 100_000D);
			TELEPORTER_MAX_DISTANCE = b().comment("Max distance a teleporter can work over").defineInRange("Teleporter max distance", 1_200D, 1D, 100_000D);
			CHARGE_PAD_CAPACITY = b().comment("Amount of energy stored in the Charge Pad").defineInRange("Charge pad capacity", 1_000_000D, 1D, 100_000D);
			POWERED_CRAFTING_TABLE_CAPACITY = b().comment("Amount of energy stored in the Powered Crafting Table").defineInRange("Powered crafting table capacity", 1_200D, 1D, 100_000D);
			POWERED_CRAFTING_TABLE_USE = b().comment("Energy usage of the Powered crafting table").defineInRange("Powered crafting table use", 1D, 0D, 100_000D);
			QUARRY_CAPACITY = b().comment("Amount of energy stored in the Quarry").defineInRange("Quarry capacity", 10_000D, 1D, 100_000D);
			QUARRY_USE = b().comment("Energy usage of the Quarry").defineInRange("Quarry use", 3D, 0D, 100_000D);
			QUARRY_MINE_TICKS = b().comment("Duration taken to mine a block using the quarry").defineInRange("Quarry mine ticks", 40L, 0L, 100_000L);
			QUARRY_MOVE_TICKS = b().comment("Duration taken to move the quarry to a new location").defineInRange("Quarry move ticks", 10L, 0L, 100_000L);
			PUMP_CAPACITY = b().comment("Amount of energy stored in the Pump").defineInRange("Pump capacity", 10_000D, 1D, 100_000D);
			PUMP_USE = b().comment("Energy usage of the Pump").defineInRange("Pump use", 3D, 0D, 100_000D);
			PUMP_MINE_TICKS = b().comment("Duration taken to mine a fluid using the pump").defineInRange("Pump mine ticks", 40L, 0L, 100_000L);
			PUMP_MOVE_TICKS = b().comment("Duration taken to move the pump to a new location").defineInRange("Pump move ticks", 10L, 0L, 100_000L);
			PUMP_TANK_CAPACITY = b().comment("Amount of energy stored in the Pump Tank").defineInRange("Pump tank capacity", FluidType.BUCKET_VOLUME * 128, 1, 100_000);
			ITEM_TRANSFER_EFFICIENCY = b().comment("Determines how effective an item can transport energy to something else").defineInRange("Item transfer efficiency", 20.0D, 0D, Double.POSITIVE_INFINITY);
			STATE_UPDATE_TICKS = b().comment("To reduce lag, we only update a state of a block every X ticks. This controls how many ticks we wait to update the blocks state.").defineInRange("State update ticks", 6, 1, 1_000);
			UPGRADE_LIMIT_PER_SLOT = b().comment("Amount of upgrades allowed in each slot of a machine").defineInRange("Upgrade limit per slot", 4, 1, 64);
			OVERCLOCKER_SPEED = b().comment("Speed increase from a single overclocking upgrade").defineInRange("Overclocker speed", 1.45D, 0D, 100_000D);
			OVERCLOCKER_ENERGY_USE = b().comment("Energy usage of the Overclocker").defineInRange("Overclocker energy use", 1.6D, 0D, 100_000D);
			STORAGE_UPGRADE = b().comment("Energy cost per storage").defineInRange("Storage upgrade", 10_000D, 0D, 100_000D);
			SCRAP_CHANCE = b().comment("Chance of producing scrap").defineInRange("Scrap chance", 0.125D, 0D, 1D);

			b().pop();
		}
	}

	public static final class Nuclear {
		public final DoubleValue NUKE_RADIUS;
		public final DoubleValue NUCLEAR_REACTOR_EXPLOSION_BASE_RADIUS;
		public final DoubleValue NUCLEAR_REACTOR_EXPLOSION_MULTIPLIER;
		public final DoubleValue NUCLEAR_REACTOR_EXPLOSION_LIMIT;
		public final IntValue FLUID_CELL_CAPACITY;
		public final BooleanValue ADD_ALL_FLUID_CELLS;
		public final BooleanValue NUCLEAR_EXPLOSION_DAEMON_THREAD;

		public Nuclear() {
			b().push("Nuclear");

			NUKE_RADIUS = b().comment("How many blocks in a circular radius the nuke will effect").defineInRange("Nuke Radius", 36D, 1D, 1_000D);
			NUCLEAR_REACTOR_EXPLOSION_BASE_RADIUS = b().comment("The starting radius for a nuclear reactor explosion").defineInRange("Base Nuclear blast radius", 10D, 1D, 1_000D);
			NUCLEAR_REACTOR_EXPLOSION_MULTIPLIER = b().comment("How much to multiple the above base radius of the explosion").defineInRange("Nuclear blast radius multiplier", 0.5D, 0.001D, 100D);
			NUCLEAR_REACTOR_EXPLOSION_LIMIT = b().comment("The limit of how large an explosion can be").defineInRange("Nuclear blast limit", 80D, 1D, 10_000D);
			FLUID_CELL_CAPACITY = b().comment("How much fluid a fluid cell can contain").defineInRange("Fluid cell capacity", FluidType.BUCKET_VOLUME, 1, 100_000);
			ADD_ALL_FLUID_CELLS = b().comment("Add all fluid cells to creative tab").define("Add all fluid cells", false);
			NUCLEAR_EXPLOSION_DAEMON_THREAD = b().comment("Spawn a deamon thread to handle the explosion calculation (experimental)").define("Explosion uses deamon thread", false);

			b().pop();
		}
	}

	public static final class Recipes {
		public final BooleanValue ADD_DUST_FROM_ORE_RECIPES;
		public final BooleanValue ADD_DUST_FROM_MATERIAL_RECIPES;
		public final BooleanValue ADD_GEM_FROM_ORE_RECIPES;
		public final BooleanValue ADD_ROD_RECIPES;
		public final BooleanValue ADD_PLATE_RECIPES;
		public final BooleanValue ADD_GEAR_RECIPES;
		public final BooleanValue ADD_CANNED_FOOD_RECIPES;

		public Recipes() {
			b().push("Recipe Generation");

			ADD_DUST_FROM_ORE_RECIPES = b().comment("Automatically generate dust from ore recipes").define("Create dust from ore recipes", true);
			ADD_DUST_FROM_MATERIAL_RECIPES = b().comment("Automatically generate dust from material recipes").define("Create dust from material recipes", true);
			ADD_GEM_FROM_ORE_RECIPES = b().comment("Automatically generate gem from ore recipes").define("Create gem from ore recipes", true);
			ADD_ROD_RECIPES = b().comment("Automatically generate rod recipes").define("Create rod recipes", true);
			ADD_PLATE_RECIPES = b().comment("Automatically generate plate recipes").define("Create plate recipes", true);
			ADD_GEAR_RECIPES = b().comment("Automatically generate gear recipes").define("Create gear recipes", true);
			ADD_CANNED_FOOD_RECIPES = b().comment("Automatically canned food recipes").define("Create canned food recipes", true);

			b().pop();
		}
	}

	private static Builder b() {
		return COMMON;
	}

	public static final List<String> MOD_MATERIAL_PRIORITY = new ArrayList<>(Arrays.asList(
			"minecraft",
			"emendatusenigmatica",
			"thermal",
			"mekanism",
			"immersiveengineering"
	));

	// Replace with ItemTags
	public static final Map<String, CraftingMaterial> MATERIALS = new LinkedHashMap<>();

	public static void addMaterial(String m) {
		MATERIALS.put(m, new CraftingMaterial(m));
	}

	public static void removeMaterial(String m) {
		MATERIALS.remove(m);
	}

	static {
		// Metals
		addMaterial("iron");
		addMaterial("gold");
		addMaterial("netherite");
		addMaterial("copper");
		addMaterial("aluminum");
		addMaterial("silver");
		addMaterial("lead");
		addMaterial("nickel");
		addMaterial("uranium");
		addMaterial("osmium");
		addMaterial("tin");
		addMaterial("zinc");
		addMaterial("cobalt");
		addMaterial("bronze");
		addMaterial("brass");
		addMaterial("constantan");
		addMaterial("electrum");
		addMaterial("steel");
		addMaterial("invar");
		addMaterial("signalum");
		addMaterial("lumium");
		addMaterial("enderium");
		addMaterial("iridium");
		addMaterial("cast_iron");
		addMaterial("tungsten");
		addMaterial("lithium");
		addMaterial("titanium");
		// Gems
		addMaterial("lapis");
		addMaterial("diamond");
		addMaterial("emerald");
		addMaterial("quartz");
		addMaterial("prismarine");
		addMaterial("certus_quartz");
		addMaterial("charged_certus_quartz");
		addMaterial("fluix");
		addMaterial("fluorite");
		addMaterial("bitumen");
		addMaterial("cinnabar");
		addMaterial("apatite");
		addMaterial("sulfur");
		addMaterial("potassium_nitrate");
		addMaterial("mana");
		addMaterial("dimensional");
		addMaterial("silicon");
		addMaterial("ruby");
		addMaterial("sapphire");
		addMaterial("peridot");
		// Other
		addMaterial("redstone");
	}

	private static int getOrder(@Nullable ResourceLocation id) {
		int i = id == null ? -1 : MOD_MATERIAL_PRIORITY.indexOf(id.getNamespace());
		return i == -1 ? MOD_MATERIAL_PRIORITY.size() : i;
	}

	public static Item getItemFromTag(TagKey<Item> tag) {
		ITag<Item> items = ForgeRegistries.ITEMS.tags().getTag(tag);

		if (items.isEmpty()) {
			return Items.AIR;
		} else if (items.size() == 1) {
			return items.iterator().next();
		}

		int order = Integer.MAX_VALUE;
		Item current = null;

		for (Item item : items) {
			int o = getOrder(Registry.ITEM.getKey(item));

			if (o == 0) {
				return item;
			} else if (current == null || o < order) {
				current = item;
				order = o;
			}
		}

		return current;
	}

	public static void init() {
	}
}
