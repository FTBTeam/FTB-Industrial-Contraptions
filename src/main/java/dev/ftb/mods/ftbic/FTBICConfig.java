package dev.ftb.mods.ftbic;

import dev.ftb.mods.ftbic.util.CraftingMaterial;
import dev.ftb.mods.ftblibrary.snbt.config.BooleanValue;
import dev.ftb.mods.ftblibrary.snbt.config.DoubleValue;
import dev.ftb.mods.ftblibrary.snbt.config.IntValue;
import dev.ftb.mods.ftblibrary.snbt.config.LongValue;
import dev.ftb.mods.ftblibrary.snbt.config.SNBTConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FTBICConfig {
	public static SNBTConfig CONFIG = SNBTConfig.create(FTBIC.MOD_ID).comment("FTB Industrial Contraptions config file");

	public static Component ENERGY_FORMAT = Component.literal("⚡").withStyle(ChatFormatting.BOLD);
	public static Component HEAT_FORMAT = Component.literal("\uD83D\uDD25");

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
	}


	public static final class Equipment {
		private static SNBTConfig EQUIPMENT = CONFIG.getGroup("Equipment");

		public final DoubleValue CARBON_ARMOR_CAPACITY;
		public final DoubleValue QUANTUM_ARMOR_CAPACITY;
		public final DoubleValue MECHANICAL_ELYTRA_CAPACITY;
		public final DoubleValue MECHANICAL_ELYTRA_RECHARGE;
		public final DoubleValue ARMOR_DAMAGE_ENERGY;
		public final DoubleValue ARMOR_FLIGHT_ENERGY;
		public final DoubleValue ARMOR_FLIGHT_BOOST;
		public final DoubleValue ARMOR_FLIGHT_STOP;

		public Equipment() {
			CARBON_ARMOR_CAPACITY = EQUIPMENT.getDouble("Carbon armor capacity", 1_000_000D, 0D, Double.POSITIVE_INFINITY).comment("Max energy the Carbon armor can contain");
			QUANTUM_ARMOR_CAPACITY = EQUIPMENT.getDouble("Quantum armor capacity", 15_000_000D, 0D, Double.POSITIVE_INFINITY).comment("Max energy the Quantum armor can contain");
			MECHANICAL_ELYTRA_CAPACITY = EQUIPMENT.getDouble("Mechanical elytra capacity", 50_000D, 0D, Double.POSITIVE_INFINITY).comment("Max energy that the elytra can contain");
			MECHANICAL_ELYTRA_RECHARGE = EQUIPMENT.getDouble("Mechanical elytra recharge", 1D, 0D, 100_000D).comment("How much power is recharged passively");
			ARMOR_DAMAGE_ENERGY = EQUIPMENT.getDouble("Armor damage energy", 5_000D, 0D, Double.POSITIVE_INFINITY).comment("Armor damage energy");
			ARMOR_FLIGHT_ENERGY = EQUIPMENT.getDouble("Armor flight energy", 5D, 0D, Double.POSITIVE_INFINITY).comment("Armor flight engery");
			ARMOR_FLIGHT_BOOST = EQUIPMENT.getDouble("Armor flight boost", 50D, 0D, 100_000D).comment("Boost gained from wearing the armor during flight");
			ARMOR_FLIGHT_STOP = EQUIPMENT.getDouble("Armor flight stop", 15D, 0D, 100_000D).comment("Force used to stop flight");
		}
	}

	public static final class Energy {
		private static final SNBTConfig ENERGY = CONFIG.getGroup("Energy");

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
			LV_TRANSFER_RATE = ENERGY.getDouble("Lv Transfer Rate", 32D, 1D, Double.POSITIVE_INFINITY).comment("Lv Transfer Rate (how much energy is transferred per tick)");
			MV_TRANSFER_RATE = ENERGY.getDouble("Mv Transfer Rate", 128D, 1D, Double.POSITIVE_INFINITY).comment("Mv Transfer Rate (how much energy is transferred per tick)");
			HV_TRANSFER_RATE = ENERGY.getDouble("Hv Transfer Rate", 512D, 1D, Double.POSITIVE_INFINITY).comment("Hv Transfer Rate (how much energy is transferred per tick)");
			EV_TRANSFER_RATE = ENERGY.getDouble("Ev Transfer Rate", 2_048D, 1D, Double.POSITIVE_INFINITY).comment("Ev Transfer Rate (how much energy is transferred per tick)");
			IV_TRANSFER_RATE = ENERGY.getDouble("Iv Transfer Rate", 8_192D, 1D, Double.POSITIVE_INFINITY).comment("Iv Transfer Rate (how much energy is transferred per tick)");
			ZAP_TO_FE_CONVERSION_RATE = ENERGY.getDouble("Zap To Fe Conversion Rate", 4.0D, 0D, Double.POSITIVE_INFINITY).comment("The amount of Zaps to FE");
			SINGLE_USE_BATTERY_CAPACITY = ENERGY.getDouble("Single Use Battery Capacity", 2_400D, 1D, Double.POSITIVE_INFINITY).comment("The amount of energy a single use battery can contain");
			LV_BATTERY_CAPACITY = ENERGY.getDouble("Lv Battery Capacity", 4_000D, 1D, Double.POSITIVE_INFINITY).comment("Energy a Lv Battery can contain");
			MV_BATTERY_CAPACITY = ENERGY.getDouble("Mv Battery Capacity", 40_000D, 1D, Double.POSITIVE_INFINITY).comment("Energy a Mv Battery can contain");
			HV_BATTERY_CAPACITY = ENERGY.getDouble("Hv Battery Capacity", 400_000D, 1D, Double.POSITIVE_INFINITY).comment("Energy a Hv Battery can contain");
			EV_BATTERY_CAPACITY = ENERGY.getDouble("Ev Battery Capacity", 10_000_000D, 1D, Double.POSITIVE_INFINITY).comment("Energy a Ev Battery can contain");
			LV_BATTERY_BOX_CAPACITY = ENERGY.getDouble("Lv Battery Box Capacity", 40_000D, 1D, Double.POSITIVE_INFINITY).comment("Energy a Lv Battery Box can contain");
			MV_BATTERY_BOX_CAPACITY = ENERGY.getDouble("Mv Battery Box Capacity", 400_000D, 1D, Double.POSITIVE_INFINITY).comment("Energy a Mv Battery Box can contain");
			HV_BATTERY_BOX_CAPACITY = ENERGY.getDouble("Hv Battery Box Capacity", 4_000_000D, 1D, Double.POSITIVE_INFINITY).comment("Energy a Hv Battery Box can contain");
			EV_BATTERY_BOX_CAPACITY = ENERGY.getDouble("Ev Battery Box Capacity", 40_000_000D, 1D, Double.POSITIVE_INFINITY).comment("Energy a Ev Battery Box can contain");
			MAX_CABLE_LENGTH = ENERGY.getInt("Max Cable Length", 300, 1, 100_000).comment("Max length of a cable chain", "The higher the number, the more tick lag will likely present.");
		}
	}

	public static final class Machines {
		private static final SNBTConfig MACHINES = CONFIG.getGroup("Machines");

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
		public final BooleanValue QUARRY_REPLACE_FLUID_EXFLUID;
		public final DoubleValue PUMP_CAPACITY;
		public final DoubleValue PUMP_USE;
		public final LongValue PUMP_MINE_TICKS;
		public final LongValue PUMP_MOVE_TICKS;
		public final BooleanValue PUMP_REPLACE_FLUID_EXFLUID;
		public final IntValue PUMP_TANK_CAPACITY;
		public final DoubleValue ITEM_TRANSFER_EFFICIENCY;
		public final IntValue STATE_UPDATE_TICKS;
		public final IntValue UPGRADE_LIMIT_PER_SLOT;
		public final DoubleValue OVERCLOCKER_SPEED;
		public final DoubleValue OVERCLOCKER_ENERGY_USE;
		public final DoubleValue STORAGE_UPGRADE;
		public final DoubleValue SCRAP_CHANCE;

		public Machines() {
			IRON_FURNACE_ITEMS_PER_COAL = MACHINES.getInt("Items processed per fuel", 12, 1, 1_000).comment("How many items are processed per fuel in the Iron Furnace");
			BASIC_GENERATOR_CAPACITY = MACHINES.getDouble("Basic generator capacity", 4_000D, 1D, 100_000D).comment("Amount of energy is stored in the Basic generator");
			BASIC_GENERATOR_OUTPUT = MACHINES.getDouble("Basic generator output", 10D, 1D, 100_000D).comment("Energy created from the Basic generator");
			GEOTHERMAL_GENERATOR_TANK_SIZE = MACHINES.getInt("Geothermal generator tank size", 8_000, 1_000, 10_000).comment("The internal size of the Geothermal generator");
			GEOTHERMAL_GENERATOR_CAPACITY = MACHINES.getDouble("Geothermal generator capacity", 2_400D, 1D, 100_000D).comment("Amount of energy stored in the Geothermal Generator");
			GEOTHERMAL_GENERATOR_OUTPUT = MACHINES.getDouble("Geothermal generator output", 20D, 1D, 100_000D).comment("Energy created from the Geothermal generator");
			WIND_MILL_CAPACITY = MACHINES.getDouble("Wind mill capacity", 100D, 1D, 100_000D).comment("Amount of energy stored in the Wind Mill");
			WIND_MILL_MIN_OUTPUT = MACHINES.getDouble("Wind mill min output", 0.3D, 0.001D, 100_000D).comment("The min amount of energy the wind mill can output");
			WIND_MILL_MAX_OUTPUT = MACHINES.getDouble("Wind mill max output", 6.5D, 0.001D, 1000_000D).comment("The max amount of energy the wind mill can output");
			WIND_MILL_MIN_Y = MACHINES.getInt("Wind mill min y", 64, 0, 250).comment("Minimum height that the windmill is effective from");
			WIND_MILL_MAX_Y = MACHINES.getInt("Wind mill max y", 320, 0, 320).comment("Maximum height that the windmill is effective from ");
			WIND_MILL_RAIN_MODIFIER = MACHINES.getDouble("Wind mill rain modifier", 1.2D, 0.001D, 5_000D).comment("How much rain effects the energy production");
			WIND_MILL_THUNDER_MODIFIER = MACHINES.getDouble("Wind mill thunder modifier", 1.5D, 0.001D, 5_000D).comment("How much thunder effects the energy production");
			LV_SOLAR_PANEL_OUTPUT = MACHINES.getDouble("Lv solar panel output", 1D, 1D, 100_000D).comment("Energy created from the Lv solar panel");
			MV_SOLAR_PANEL_OUTPUT = MACHINES.getDouble("Mv solar panel output", 8D, 1D, 100_000D).comment("Energy created from the Mv solar panel");
			HV_SOLAR_PANEL_OUTPUT = MACHINES.getDouble("Hv solar panel output", 64D, 1D, 100_000D).comment("Energy created from the Hv solar panel");
			EV_SOLAR_PANEL_OUTPUT = MACHINES.getDouble("Ev solar panel output", 512D, 1D, 100_000D).comment("Energy created from the Ev solar panel");
			LV_SOLAR_PANEL_CAPACITY = MACHINES.getDouble("Lv solar panel capacity", 60D, 1D, 100_000D).comment("Amount of energy stored in the Lv solar panel");
			MV_SOLAR_PANEL_CAPACITY = MACHINES.getDouble("Mv solar panel capacity", 480D, 1D, 100_000D).comment("Amount of energy stored in the Mv solar panel");
			HV_SOLAR_PANEL_CAPACITY = MACHINES.getDouble("Hv solar panel capacity", 3840D, 1D, 100_000D).comment("Amount of energy stored inm the Hv solar panel");
			EV_SOLAR_PANEL_CAPACITY = MACHINES.getDouble("Ev solar panel capacity", 30720D, 1D, 100_000D).comment("Amount of energy stored in the Ev solar panel");
			NUCLEAR_REACTOR_CAPACITY = MACHINES.getDouble("Nuclear reactor capacity", 50_000D, 1D, 100_000D).comment("Amount of energy stored in the Nuclear Reactor");
			MACHINE_RECIPE_BASE_TICKS = MACHINES.getDouble("Machine recipe base ticks", 200D, 1D, 100_000D).comment("Base lengths in ticks a machine takes to process a recipe");
			POWERED_FURNACE_CAPACITY = MACHINES.getDouble("Powered furnace capacity", 1_200D, 1D, 100_000D).comment("Amount of energy stored in the Powered Furnace");
			POWERED_FURNACE_USE = MACHINES.getDouble("Powered furnace use", 3D, 0D, 100_000D).comment("Energy usage per operation of the powered furnace");
			MACERATOR_CAPACITY = MACHINES.getDouble("Macerator capacity", 1_200D, 1D, 100_000D).comment("Amount of energy stored in the Macerator");
			MACERATOR_USE = MACHINES.getDouble("Macerator use", 2D, 0D, 100_000D).comment("Energy usage of the Macerator");
			CENTRIFUGE_CAPACITY = MACHINES.getDouble("Centrifuge capacity", 1_200D, 1D, 100_000D).comment("Amount of energy stored in the Centrifuge");
			CENTRIFUGE_USE = MACHINES.getDouble("Centrifuge use", 2D, 0D, 100_000D).comment("Energy usage of the Centrifuge");
			COMPRESSOR_CAPACITY = MACHINES.getDouble("Compressor capacity", 1_200D, 1D, 100_000D).comment("Amount of energy stored in the Compressor");
			COMPRESSOR_USE = MACHINES.getDouble("Compressor use", 2D, 0D, 100_000D).comment("Energy usage of the Compressor");
			REPROCESSOR_CAPACITY = MACHINES.getDouble("Reprocessor capacity", 4_000D, 1D, 100_000D).comment("Amount of energy stored in the Reprocessor");
			REPROCESSOR_USE = MACHINES.getDouble("Reprocessor use", 8D, 0D, 100_000D).comment("Energy usage of the Reprocessor");
			CANNING_MACHINE_CAPACITY = MACHINES.getDouble("Canning machine capacity", 1_200D, 1D, 100_000D).comment("Amount of energy stored in the Canning Machine");
			CANNING_MACHINE_USE = MACHINES.getDouble("Canning machine use", 1D, 0D, 100_000D).comment("Energy usage of the Canning machine");
			ROLLER_CAPACITY = MACHINES.getDouble("Roller capacity", 1_200D, 1D, 100_000D).comment("Amount of energy stored in the Roller");
			ROLLER_USE = MACHINES.getDouble("Roller use", 3D, 0D, 100_000D).comment("Energy usage of the Roller");
			EXTRUDER_CAPACITY = MACHINES.getDouble("Extruder capacity", 1_200D, 1D, 100_000D).comment("Amount of energy stored in the Extruder");
			EXTRUDER_USE = MACHINES.getDouble("Extruder use", 3D, 0D, 100_000D).comment("Energy usage of the Extruder");
			ANTIMATTER_CONSTRUCTOR_CAPACITY = MACHINES.getDouble("Antimatter constructor capacity", 1_000_000D, 1D, 100_000D).comment("Amount of energy stored in the Antimatter Constructor");
			ANTIMATTER_CONSTRUCTOR_BOOST = MACHINES.getDouble("Antimatter constructor boost", 6D, 1D, 100_000D).comment("Construction boost multiplier");
			ADVANCED_POWERED_FURNACE_CAPACITY = MACHINES.getDouble("Advanced powered furnace capacity", 10_000D, 1D, 100_000D).comment("Amount of energy stored in the Advanced Powered Furnace");
			ADVANCED_POWERED_FURNACE_USE = MACHINES.getDouble("Advanced powered furnace use", 16D, 0D, 100_000D).comment("Energy usage of the Advanced powered furnace");
			ADVANCED_MACERATOR_CAPACITY = MACHINES.getDouble("Advanced macerator capacity", 10_000D, 1D, 100_000D).comment("Amount of energy stored in the Advanced Macerator");
			ADVANCED_MACERATOR_USE = MACHINES.getDouble("Advanced macerator use", 16D, 0D, 100_000D).comment("Energy usage of the Advanced macerator");
			ADVANCED_CENTRIFUGE_CAPACITY = MACHINES.getDouble("Advanced centrifuge capacity", 10_000D, 1D, 100_000D).comment("Amount of energy stored in the Advanced Centrifuge");
			ADVANCED_CENTRIFUGE_USE = MACHINES.getDouble("Advanced centrifuge use", 16D, 0D, 100_000D).comment("Energy usage of the Advanced centrifuge");
			ADVANCED_COMPRESSOR_CAPACITY = MACHINES.getDouble("Advanced compressor capacity", 10_000D, 1D, 100_000D).comment("Amount of energy stored in the Advanced Compressor");
			ADVANCED_COMPRESSOR_USE = MACHINES.getDouble("Advanced compressor use", 16D, 0D, 100_000D).comment("Energy usage of the Advanced compressor");
			TELEPORTER_CAPACITY = MACHINES.getDouble("Teleporter capacity", 1_000_000D, 1D, 100_000D).comment("Amount of energy stored in the Teleporter");
			TELEPORTER_MIN_USE = MACHINES.getDouble("Teleporter min use", 100D, 0D, 100_000D).comment("Energy usage of the Teleporter min");
			TELEPORTER_MIN_DISTANCE = MACHINES.getDouble("Teleporter min distance", 16D, 1D, 100_000D).comment("Min distance a teleporter can work over");
			TELEPORTER_MAX_USE = MACHINES.getDouble("Teleporter max use", 10_000D, 0D, 100_000D).comment("Energy usage of the Teleporter max");
			TELEPORTER_MAX_DISTANCE = MACHINES.getDouble("Teleporter max distance", 1_200D, 1D, 100_000D).comment("Max distance a teleporter can work over");
			CHARGE_PAD_CAPACITY = MACHINES.getDouble("Charge pad capacity", 1_000_000D, 1D, 100_000D).comment("Amount of energy stored in the Charge Pad");
			POWERED_CRAFTING_TABLE_CAPACITY = MACHINES.getDouble("Powered crafting table capacity", 1_200D, 1D, 100_000D).comment("Amount of energy stored in the Powered Crafting Table");
			POWERED_CRAFTING_TABLE_USE = MACHINES.getDouble("Powered crafting table use", 1D, 0D, 100_000D).comment("Energy usage of the Powered crafting table");
			QUARRY_CAPACITY = MACHINES.getDouble("Quarry capacity", 10_000D, 1D, 100_000D).comment("Amount of energy stored in the Quarry");
			QUARRY_USE = MACHINES.getDouble("Quarry use", 3D, 0D, 100_000D).comment("Energy usage of the Quarry");
			QUARRY_MINE_TICKS = MACHINES.getLong("Quarry mine ticks", 40L, 0L, 100_000L).comment("Duration taken to mine a block using the quarry");
			QUARRY_MOVE_TICKS = MACHINES.getLong("Quarry move ticks", 10L, 0L, 100_000L).comment("Duration taken to move the quarry to a new location");
			QUARRY_REPLACE_FLUID_EXFLUID = MACHINES.getBoolean("Quarry replace fluid with exfluid", true).comment("Whether fluid sources are replaced with exfluid on dig");
			PUMP_CAPACITY = MACHINES.getDouble("Pump capacity", 10_000D, 1D, 100_000D).comment("Amount of energy stored in the Pump");
			PUMP_USE = MACHINES.getDouble("Pump use", 3D, 0D, 100_000D).comment("Energy usage of the Pump");
			PUMP_MINE_TICKS = MACHINES.getLong("Pump mine ticks", 40L, 0L, 100_000L).comment("Duration taken to mine a fluid using the pump");
			PUMP_MOVE_TICKS = MACHINES.getLong("Pump move ticks", 10L, 0L, 100_000L).comment("Duration taken to move the pump to a new location");
			PUMP_REPLACE_FLUID_EXFLUID = MACHINES.getBoolean("Pump replace fluid with exfluid", true).comment("Whether fluid sources are replaced with exfluid on pump");
			PUMP_TANK_CAPACITY = MACHINES.getInt("Pump tank capacity", FluidType.BUCKET_VOLUME * 128, 1, 100_000).comment("Amount of energy stored in the Pump Tank");
			ITEM_TRANSFER_EFFICIENCY = MACHINES.getDouble("Item transfer efficiency", 20.0D, 0D, Double.POSITIVE_INFINITY).comment("Determines how effective an item can transport energy to something else");
			STATE_UPDATE_TICKS = MACHINES.getInt("State update ticks", 6, 1, 1_000).comment("To reduce lag, we only update a state of a block every X ticks. This controls how many ticks we wait to update the blocks state.");
			UPGRADE_LIMIT_PER_SLOT = MACHINES.getInt("Upgrade limit per slot", 4, 1, 64).comment("Amount of upgrades allowed in each slot of a machine");
			OVERCLOCKER_SPEED = MACHINES.getDouble("Overclocker speed", 1.45D, 0D, 100_000D).comment("Speed increase from a single overclocking upgrade");
			OVERCLOCKER_ENERGY_USE = MACHINES.getDouble("Overclocker energy use", 1.6D, 0D, 100_000D).comment("Energy usage of the Overclocker");
			STORAGE_UPGRADE = MACHINES.getDouble("Storage upgrade", 10_000D, 0D, 100_000D).comment("Energy cost per storage");
			SCRAP_CHANCE = MACHINES.getDouble("Scrap chance", 0.125D, 0D, 1D).comment("Chance of producing scrap");
		}
	}

	public static final class Nuclear {
		SNBTConfig NUCLEAR = CONFIG.getGroup("Nuclear");

		public final DoubleValue NUKE_RADIUS;
		public final DoubleValue NUCLEAR_REACTOR_EXPLOSION_BASE_RADIUS;
		public final DoubleValue NUCLEAR_REACTOR_EXPLOSION_MULTIPLIER;
		public final DoubleValue NUCLEAR_REACTOR_EXPLOSION_LIMIT;
		public final IntValue FLUID_CELL_CAPACITY;
		public final BooleanValue ADD_ALL_FLUID_CELLS;
		public final BooleanValue NUCLEAR_EXPLOSION_DAEMON_THREAD;

		public Nuclear() {
			NUKE_RADIUS = NUCLEAR.getDouble("Nuke Radius", 36D, 1D, 1_000D).comment("How many blocks in a circular radius the nuke will effect");
			NUCLEAR_REACTOR_EXPLOSION_BASE_RADIUS = NUCLEAR.getDouble("Base Nuclear blast radius", 10D, 1D, 1_000D).comment("The starting radius for a nuclear reactor explosion");
			NUCLEAR_REACTOR_EXPLOSION_MULTIPLIER = NUCLEAR.getDouble("Nuclear blast radius multiplier", 0.5D, 0.001D, 100D).comment("How much to multiple the above base radius of the explosion");
			NUCLEAR_REACTOR_EXPLOSION_LIMIT = NUCLEAR.getDouble("Nuclear blast limit", 80D, 1D, 10_000D).comment("The limit of how large an explosion can be");
			FLUID_CELL_CAPACITY = NUCLEAR.getInt("Fluid cell capacity", FluidType.BUCKET_VOLUME, 1, 100_000).comment("How much fluid a fluid cell can contain");
			ADD_ALL_FLUID_CELLS = NUCLEAR.getBoolean("Add all fluid cells", false).comment("Add all fluid cells to creative tab");
			NUCLEAR_EXPLOSION_DAEMON_THREAD = NUCLEAR.getBoolean("Explosion uses deamon thread", false).comment("Spawn a deamon thread to handle the explosion calculation (experimental)");
		}
	}

	public static final class Recipes {
		SNBTConfig RECIPES = CONFIG.getGroup("Recipes");

		public final BooleanValue ADD_DUST_FROM_ORE_RECIPES;
		public final BooleanValue ADD_DUST_FROM_MATERIAL_RECIPES;
		public final BooleanValue ADD_GEM_FROM_ORE_RECIPES;
		public final BooleanValue ADD_ROD_RECIPES;
		public final BooleanValue ADD_PLATE_RECIPES;
		public final BooleanValue ADD_GEAR_RECIPES;
		public final BooleanValue ADD_CANNED_FOOD_RECIPES;

		public Recipes() {
			ADD_DUST_FROM_ORE_RECIPES = RECIPES.getBoolean("Create dust from ore recipes", true).comment("Automatically generate dust from ore recipes");
			ADD_DUST_FROM_MATERIAL_RECIPES = RECIPES.getBoolean("Create dust from material recipes", true).comment("Automatically generate dust from material recipes");
			ADD_GEM_FROM_ORE_RECIPES = RECIPES.getBoolean("Create gem from ore recipes", true).comment("Automatically generate gem from ore recipes");
			ADD_ROD_RECIPES = RECIPES.getBoolean("Create rod recipes", true).comment("Automatically generate rod recipes");
			ADD_PLATE_RECIPES = RECIPES.getBoolean("Create plate recipes", true).comment("Automatically generate plate recipes");
			ADD_GEAR_RECIPES = RECIPES.getBoolean("Create gear recipes", true).comment("Automatically generate gear recipes");
			ADD_CANNED_FOOD_RECIPES = RECIPES.getBoolean("Create canned food recipes", true).comment("Automatically canned food recipes");
		}
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
