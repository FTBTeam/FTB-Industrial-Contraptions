package dev.ftb.mods.ftbic;

import dev.ftb.mods.ftbic.util.CraftingMaterial;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class FTBICConfig {
	public static final int FLUID_BUCKET_VOLUME = 1000;

	public static final Component ENERGY_FORMAT = Component.literal("\u26A1").withStyle(ChatFormatting.BOLD);
	public static final Component HEAT_FORMAT = Component.literal("\uD83D\uDD25");

	public static final Energy ENERGY;
	public static final Machines MACHINES;
	public static final Nuclear NUCLEAR;
	public static final Equipment EQUIPMENT;
	public static final Recipes RECIPES;

	public static final ModConfigSpec COMMON_SPEC;

	static {
		ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
		ENERGY = new Energy(builder);
		MACHINES = new Machines(builder);
		NUCLEAR = new Nuclear(builder);
		EQUIPMENT = new Equipment(builder);
		RECIPES = new Recipes(builder);
		COMMON_SPEC = builder.build();
	}

	public static final class Equipment {
		public final ModConfigSpec.DoubleValue CARBON_ARMOR_CAPACITY;
		public final ModConfigSpec.DoubleValue QUANTUM_ARMOR_CAPACITY;
		public final ModConfigSpec.DoubleValue MECHANICAL_ELYTRA_CAPACITY;
		public final ModConfigSpec.DoubleValue MECHANICAL_ELYTRA_RECHARGE;
		public final ModConfigSpec.DoubleValue ARMOR_DAMAGE_ENERGY;
		public final ModConfigSpec.DoubleValue ARMOR_FLIGHT_ENERGY;
		public final ModConfigSpec.DoubleValue ARMOR_FLIGHT_BOOST;
		public final ModConfigSpec.DoubleValue ARMOR_FLIGHT_STOP;

		Equipment(ModConfigSpec.Builder b) {
			b.push("equipment");
			CARBON_ARMOR_CAPACITY = b.comment("Max energy the Carbon armor can contain").defineInRange("carbon_armor_capacity", 1_000_000D, 0D, Double.POSITIVE_INFINITY);
			QUANTUM_ARMOR_CAPACITY = b.comment("Max energy the Quantum armor can contain").defineInRange("quantum_armor_capacity", 15_000_000D, 0D, Double.POSITIVE_INFINITY);
			MECHANICAL_ELYTRA_CAPACITY = b.comment("Max energy that the elytra can contain").defineInRange("mechanical_elytra_capacity", 50_000D, 0D, Double.POSITIVE_INFINITY);
			MECHANICAL_ELYTRA_RECHARGE = b.comment("How much power is recharged passively").defineInRange("mechanical_elytra_recharge", 1D, 0D, 100_000D);
			ARMOR_DAMAGE_ENERGY = b.comment("Armor damage energy").defineInRange("armor_damage_energy", 5_000D, 0D, Double.POSITIVE_INFINITY);
			ARMOR_FLIGHT_ENERGY = b.comment("Armor flight energy").defineInRange("armor_flight_energy", 5D, 0D, Double.POSITIVE_INFINITY);
			ARMOR_FLIGHT_BOOST = b.comment("Boost gained from wearing the armor during flight").defineInRange("armor_flight_boost", 50D, 0D, 100_000D);
			ARMOR_FLIGHT_STOP = b.comment("Force used to stop flight").defineInRange("armor_flight_stop", 15D, 0D, 100_000D);
			b.pop();
		}
	}

	public static final class Energy {
		public final ModConfigSpec.DoubleValue LV_TRANSFER_RATE;
		public final ModConfigSpec.DoubleValue MV_TRANSFER_RATE;
		public final ModConfigSpec.DoubleValue HV_TRANSFER_RATE;
		public final ModConfigSpec.DoubleValue EV_TRANSFER_RATE;
		public final ModConfigSpec.DoubleValue IV_TRANSFER_RATE;
		public final ModConfigSpec.DoubleValue ZAP_TO_FE_CONVERSION_RATE;
		public final ModConfigSpec.DoubleValue SINGLE_USE_BATTERY_CAPACITY;
		public final ModConfigSpec.DoubleValue LV_BATTERY_CAPACITY;
		public final ModConfigSpec.DoubleValue MV_BATTERY_CAPACITY;
		public final ModConfigSpec.DoubleValue HV_BATTERY_CAPACITY;
		public final ModConfigSpec.DoubleValue EV_BATTERY_CAPACITY;
		public final ModConfigSpec.DoubleValue LV_BATTERY_BOX_CAPACITY;
		public final ModConfigSpec.DoubleValue MV_BATTERY_BOX_CAPACITY;
		public final ModConfigSpec.DoubleValue HV_BATTERY_BOX_CAPACITY;
		public final ModConfigSpec.DoubleValue EV_BATTERY_BOX_CAPACITY;
		public final ModConfigSpec.IntValue MAX_CABLE_LENGTH;
		public final ModConfigSpec.BooleanValue FULL_FE_MODE;

		Energy(ModConfigSpec.Builder b) {
			b.push("energy");
			LV_TRANSFER_RATE = b.comment("LV transfer rate (energy per tick)").defineInRange("lv_transfer_rate", 32D, 1D, Double.POSITIVE_INFINITY);
			MV_TRANSFER_RATE = b.comment("MV transfer rate (energy per tick)").defineInRange("mv_transfer_rate", 128D, 1D, Double.POSITIVE_INFINITY);
			HV_TRANSFER_RATE = b.comment("HV transfer rate (energy per tick)").defineInRange("hv_transfer_rate", 512D, 1D, Double.POSITIVE_INFINITY);
			EV_TRANSFER_RATE = b.comment("EV transfer rate (energy per tick)").defineInRange("ev_transfer_rate", 2_048D, 1D, Double.POSITIVE_INFINITY);
			IV_TRANSFER_RATE = b.comment("IV transfer rate (energy per tick)").defineInRange("iv_transfer_rate", 8_192D, 1D, Double.POSITIVE_INFINITY);
			ZAP_TO_FE_CONVERSION_RATE = b.comment("Zap-to-FE conversion rate").defineInRange("zap_to_fe_conversion_rate", 8.0D, 0D, Double.POSITIVE_INFINITY);
			SINGLE_USE_BATTERY_CAPACITY = b.comment("Single-use battery capacity").defineInRange("single_use_battery_capacity", 2_400D, 1D, Double.POSITIVE_INFINITY);
			LV_BATTERY_CAPACITY = b.comment("LV battery capacity").defineInRange("lv_battery_capacity", 4_000D, 1D, Double.POSITIVE_INFINITY);
			MV_BATTERY_CAPACITY = b.comment("MV battery capacity").defineInRange("mv_battery_capacity", 40_000D, 1D, Double.POSITIVE_INFINITY);
			HV_BATTERY_CAPACITY = b.comment("HV battery capacity").defineInRange("hv_battery_capacity", 400_000D, 1D, Double.POSITIVE_INFINITY);
			EV_BATTERY_CAPACITY = b.comment("EV battery capacity").defineInRange("ev_battery_capacity", 10_000_000D, 1D, Double.POSITIVE_INFINITY);
			LV_BATTERY_BOX_CAPACITY = b.comment("LV battery box capacity").defineInRange("lv_battery_box_capacity", 40_000D, 1D, Double.POSITIVE_INFINITY);
			MV_BATTERY_BOX_CAPACITY = b.comment("MV battery box capacity").defineInRange("mv_battery_box_capacity", 400_000D, 1D, Double.POSITIVE_INFINITY);
			HV_BATTERY_BOX_CAPACITY = b.comment("HV battery box capacity").defineInRange("hv_battery_box_capacity", 4_000_000D, 1D, Double.POSITIVE_INFINITY);
			EV_BATTERY_BOX_CAPACITY = b.comment("EV battery box capacity").defineInRange("ev_battery_box_capacity", 40_000_000D, 1D, Double.POSITIVE_INFINITY);
			MAX_CABLE_LENGTH = b.comment("Max cable chain length. Higher values increase tick cost.").defineInRange("max_cable_length", 300, 1, 100_000);
			FULL_FE_MODE = b.comment("If true, FTBIC machines accept and emit FE directly on every side and rectifiers are no longer required. Takes effect on game start.")
					.define("full_fe_mode", false);
			b.pop();
		}
	}

	public static final class Machines {
		public final ModConfigSpec.IntValue IRON_FURNACE_ITEMS_PER_COAL;
		public final ModConfigSpec.DoubleValue BASIC_GENERATOR_CAPACITY;
		public final ModConfigSpec.DoubleValue BASIC_GENERATOR_OUTPUT;
		public final ModConfigSpec.IntValue GEOTHERMAL_GENERATOR_TANK_SIZE;
		public final ModConfigSpec.DoubleValue GEOTHERMAL_GENERATOR_CAPACITY;
		public final ModConfigSpec.DoubleValue GEOTHERMAL_GENERATOR_OUTPUT;
		public final ModConfigSpec.DoubleValue WIND_MILL_CAPACITY;
		public final ModConfigSpec.DoubleValue WIND_MILL_MIN_OUTPUT;
		public final ModConfigSpec.DoubleValue WIND_MILL_MAX_OUTPUT;
		public final ModConfigSpec.DoubleValue NUCLEAR_GENERATOR_OUTPUT;
		public final ModConfigSpec.IntValue WIND_MILL_MIN_Y;
		public final ModConfigSpec.IntValue WIND_MILL_MAX_Y;
		public final ModConfigSpec.DoubleValue WIND_MILL_RAIN_MODIFIER;
		public final ModConfigSpec.DoubleValue WIND_MILL_THUNDER_MODIFIER;
		public final ModConfigSpec.DoubleValue LV_SOLAR_PANEL_OUTPUT;
		public final ModConfigSpec.DoubleValue MV_SOLAR_PANEL_OUTPUT;
		public final ModConfigSpec.DoubleValue HV_SOLAR_PANEL_OUTPUT;
		public final ModConfigSpec.DoubleValue EV_SOLAR_PANEL_OUTPUT;
		public final ModConfigSpec.DoubleValue LV_SOLAR_PANEL_CAPACITY;
		public final ModConfigSpec.DoubleValue MV_SOLAR_PANEL_CAPACITY;
		public final ModConfigSpec.DoubleValue HV_SOLAR_PANEL_CAPACITY;
		public final ModConfigSpec.DoubleValue EV_SOLAR_PANEL_CAPACITY;
		public final ModConfigSpec.DoubleValue NUCLEAR_REACTOR_CAPACITY;
		public final ModConfigSpec.DoubleValue MACHINE_RECIPE_BASE_TICKS;
		public final ModConfigSpec.DoubleValue POWERED_FURNACE_CAPACITY;
		public final ModConfigSpec.DoubleValue POWERED_FURNACE_USE;
		public final ModConfigSpec.DoubleValue MACERATOR_CAPACITY;
		public final ModConfigSpec.DoubleValue MACERATOR_USE;
		public final ModConfigSpec.DoubleValue CENTRIFUGE_CAPACITY;
		public final ModConfigSpec.DoubleValue CENTRIFUGE_USE;
		public final ModConfigSpec.DoubleValue COMPRESSOR_CAPACITY;
		public final ModConfigSpec.DoubleValue COMPRESSOR_USE;
		public final ModConfigSpec.DoubleValue REPROCESSOR_CAPACITY;
		public final ModConfigSpec.DoubleValue REPROCESSOR_USE;
		public final ModConfigSpec.DoubleValue CANNING_MACHINE_CAPACITY;
		public final ModConfigSpec.DoubleValue CANNING_MACHINE_USE;
		public final ModConfigSpec.DoubleValue ROLLER_CAPACITY;
		public final ModConfigSpec.DoubleValue ROLLER_USE;
		public final ModConfigSpec.DoubleValue EXTRUDER_CAPACITY;
		public final ModConfigSpec.DoubleValue EXTRUDER_USE;
		public final ModConfigSpec.DoubleValue ANTIMATTER_CONSTRUCTOR_CAPACITY;
		public final ModConfigSpec.DoubleValue ANTIMATTER_CONSTRUCTOR_BOOST;
		public final ModConfigSpec.DoubleValue ADVANCED_POWERED_FURNACE_CAPACITY;
		public final ModConfigSpec.DoubleValue ADVANCED_POWERED_FURNACE_USE;
		public final ModConfigSpec.DoubleValue ADVANCED_MACERATOR_CAPACITY;
		public final ModConfigSpec.DoubleValue ADVANCED_MACERATOR_USE;
		public final ModConfigSpec.DoubleValue ADVANCED_CENTRIFUGE_CAPACITY;
		public final ModConfigSpec.DoubleValue ADVANCED_CENTRIFUGE_USE;
		public final ModConfigSpec.DoubleValue ADVANCED_COMPRESSOR_CAPACITY;
		public final ModConfigSpec.DoubleValue ADVANCED_COMPRESSOR_USE;
		public final ModConfigSpec.DoubleValue TELEPORTER_CAPACITY;
		public final ModConfigSpec.DoubleValue TELEPORTER_MIN_USE;
		public final ModConfigSpec.DoubleValue TELEPORTER_MIN_DISTANCE;
		public final ModConfigSpec.DoubleValue TELEPORTER_MAX_USE;
		public final ModConfigSpec.DoubleValue TELEPORTER_MAX_DISTANCE;
		public final ModConfigSpec.DoubleValue TELEPORTER_TRANSPORT_DRAIN;
		public final ModConfigSpec.IntValue TELEPORTER_ACTIVE_WINDOW_TICKS;
		public final ModConfigSpec.IntValue TELEPORTER_CHUNK_LOAD_IDLE_TICKS;
		public final ModConfigSpec.DoubleValue TELEPORTER_BALANCE_RATE;
		public final ModConfigSpec.DoubleValue CHARGE_PAD_CAPACITY;
		public final ModConfigSpec.DoubleValue POWERED_CRAFTING_TABLE_CAPACITY;
		public final ModConfigSpec.DoubleValue POWERED_CRAFTING_TABLE_USE;
		public final ModConfigSpec.DoubleValue QUARRY_CAPACITY;
		public final ModConfigSpec.DoubleValue QUARRY_USE;
		public final ModConfigSpec.LongValue QUARRY_MINE_TICKS;
		public final ModConfigSpec.LongValue QUARRY_MOVE_TICKS;
		public final ModConfigSpec.BooleanValue QUARRY_REPLACE_FLUID_EXFLUID;
		public final ModConfigSpec.BooleanValue QUARRY_PICKAXE_TAKES_DAMAGE;
		public final ModConfigSpec.DoubleValue PUMP_CAPACITY;
		public final ModConfigSpec.DoubleValue PUMP_USE;
		public final ModConfigSpec.LongValue PUMP_MINE_TICKS;
		public final ModConfigSpec.LongValue PUMP_MOVE_TICKS;
		public final ModConfigSpec.BooleanValue PUMP_REPLACE_FLUID_EXFLUID;
		public final ModConfigSpec.IntValue PUMP_TANK_CAPACITY;
		public final ModConfigSpec.DoubleValue ITEM_TRANSFER_EFFICIENCY;
		public final ModConfigSpec.IntValue STATE_UPDATE_TICKS;
		public final ModConfigSpec.IntValue UPGRADE_LIMIT_PER_SLOT;
		public final ModConfigSpec.DoubleValue OVERCLOCKER_SPEED;
		public final ModConfigSpec.DoubleValue OVERCLOCKER_ENERGY_USE;
		public final ModConfigSpec.DoubleValue STORAGE_UPGRADE;
		public final ModConfigSpec.DoubleValue SCRAP_CHANCE;

		Machines(ModConfigSpec.Builder b) {
			b.push("machines");
			IRON_FURNACE_ITEMS_PER_COAL = b.comment("Iron Furnace items per fuel").defineInRange("iron_furnace_items_per_fuel", 12, 1, 1_000);
			BASIC_GENERATOR_CAPACITY = b.defineInRange("basic_generator_capacity", 4_000D, 1D, 100_000D);
			BASIC_GENERATOR_OUTPUT = b.defineInRange("basic_generator_output", 10D, 1D, 100_000D);
			GEOTHERMAL_GENERATOR_TANK_SIZE = b.defineInRange("geothermal_generator_tank_size", 8_000, 1_000, 10_000);
			GEOTHERMAL_GENERATOR_CAPACITY = b.defineInRange("geothermal_generator_capacity", 2_400D, 1D, 100_000D);
			GEOTHERMAL_GENERATOR_OUTPUT = b.defineInRange("geothermal_generator_output", 20D, 1D, 100_000D);
			WIND_MILL_CAPACITY = b.defineInRange("wind_mill_capacity", 100D, 1D, 100_000D);
			WIND_MILL_MIN_OUTPUT = b.defineInRange("wind_mill_min_output", 0.3D, 0.001D, 100_000D);
			WIND_MILL_MAX_OUTPUT = b.defineInRange("wind_mill_max_output", 6.5D, 0.001D, 1_000_000D);
			WIND_MILL_MIN_Y = b.defineInRange("wind_mill_min_y", 64, 0, 250);
			WIND_MILL_MAX_Y = b.defineInRange("wind_mill_max_y", 320, 0, 320);
			WIND_MILL_RAIN_MODIFIER = b.defineInRange("wind_mill_rain_modifier", 1.2D, 0.001D, 5_000D);
			WIND_MILL_THUNDER_MODIFIER = b.defineInRange("wind_mill_thunder_modifier", 1.5D, 0.001D, 5_000D);
			LV_SOLAR_PANEL_OUTPUT = b.defineInRange("lv_solar_panel_output", 1D, 1D, 100_000D);
			MV_SOLAR_PANEL_OUTPUT = b.defineInRange("mv_solar_panel_output", 8D, 1D, 100_000D);
			HV_SOLAR_PANEL_OUTPUT = b.defineInRange("hv_solar_panel_output", 64D, 1D, 100_000D);
			EV_SOLAR_PANEL_OUTPUT = b.defineInRange("ev_solar_panel_output", 512D, 1D, 100_000D);
			LV_SOLAR_PANEL_CAPACITY = b.defineInRange("lv_solar_panel_capacity", 60D, 1D, 100_000D);
			MV_SOLAR_PANEL_CAPACITY = b.defineInRange("mv_solar_panel_capacity", 480D, 1D, 100_000D);
			HV_SOLAR_PANEL_CAPACITY = b.defineInRange("hv_solar_panel_capacity", 3840D, 1D, 100_000D);
			EV_SOLAR_PANEL_CAPACITY = b.defineInRange("ev_solar_panel_capacity", 30720D, 1D, 100_000D);
			NUCLEAR_REACTOR_CAPACITY = b.defineInRange("nuclear_reactor_capacity", 50_000D, 1D, 100_000D);
			NUCLEAR_GENERATOR_OUTPUT = b.comment("Nuclear Reactor output multiplier").defineInRange("nuclear_reactor_output_multiplier", 1D, 0.1D, 100D);
			MACHINE_RECIPE_BASE_TICKS = b.defineInRange("machine_recipe_base_ticks", 200D, 1D, 100_000D);
			POWERED_FURNACE_CAPACITY = b.defineInRange("powered_furnace_capacity", 1_200D, 1D, 100_000D);
			POWERED_FURNACE_USE = b.defineInRange("powered_furnace_use", 3D, 0D, 100_000D);
			MACERATOR_CAPACITY = b.defineInRange("macerator_capacity", 1_200D, 1D, 100_000D);
			MACERATOR_USE = b.defineInRange("macerator_use", 2D, 0D, 100_000D);
			CENTRIFUGE_CAPACITY = b.defineInRange("centrifuge_capacity", 1_200D, 1D, 100_000D);
			CENTRIFUGE_USE = b.defineInRange("centrifuge_use", 2D, 0D, 100_000D);
			COMPRESSOR_CAPACITY = b.defineInRange("compressor_capacity", 1_200D, 1D, 100_000D);
			COMPRESSOR_USE = b.defineInRange("compressor_use", 2D, 0D, 100_000D);
			REPROCESSOR_CAPACITY = b.defineInRange("reprocessor_capacity", 4_000D, 1D, 100_000D);
			REPROCESSOR_USE = b.defineInRange("reprocessor_use", 8D, 0D, 100_000D);
			CANNING_MACHINE_CAPACITY = b.defineInRange("canning_machine_capacity", 1_200D, 1D, 100_000D);
			CANNING_MACHINE_USE = b.defineInRange("canning_machine_use", 1D, 0D, 100_000D);
			ROLLER_CAPACITY = b.defineInRange("roller_capacity", 1_200D, 1D, 100_000D);
			ROLLER_USE = b.defineInRange("roller_use", 3D, 0D, 100_000D);
			EXTRUDER_CAPACITY = b.defineInRange("extruder_capacity", 1_200D, 1D, 100_000D);
			EXTRUDER_USE = b.defineInRange("extruder_use", 3D, 0D, 100_000D);
			ANTIMATTER_CONSTRUCTOR_CAPACITY = b.defineInRange("antimatter_constructor_capacity", 1_000_000D, 1D, 1_000_000D);
			ANTIMATTER_CONSTRUCTOR_BOOST = b.defineInRange("antimatter_constructor_boost", 6D, 1D, 100_000D);
			ADVANCED_POWERED_FURNACE_CAPACITY = b.defineInRange("advanced_powered_furnace_capacity", 10_000D, 1D, 100_000D);
			ADVANCED_POWERED_FURNACE_USE = b.defineInRange("advanced_powered_furnace_use", 16D, 0D, 100_000D);
			ADVANCED_MACERATOR_CAPACITY = b.defineInRange("advanced_macerator_capacity", 10_000D, 1D, 100_000D);
			ADVANCED_MACERATOR_USE = b.defineInRange("advanced_macerator_use", 16D, 0D, 100_000D);
			ADVANCED_CENTRIFUGE_CAPACITY = b.defineInRange("advanced_centrifuge_capacity", 10_000D, 1D, 100_000D);
			ADVANCED_CENTRIFUGE_USE = b.defineInRange("advanced_centrifuge_use", 16D, 0D, 100_000D);
			ADVANCED_COMPRESSOR_CAPACITY = b.defineInRange("advanced_compressor_capacity", 10_000D, 1D, 100_000D);
			ADVANCED_COMPRESSOR_USE = b.defineInRange("advanced_compressor_use", 16D, 0D, 100_000D);
			TELEPORTER_CAPACITY = b.defineInRange("teleporter_capacity", 1_000_000D, 1D, 10_000_000D);
			TELEPORTER_MIN_USE = b.defineInRange("teleporter_min_use", 100D, 0D, 100_000D);
			TELEPORTER_MIN_DISTANCE = b.defineInRange("teleporter_min_distance", 16D, 1D, 100_000D);
			TELEPORTER_MAX_USE = b.defineInRange("teleporter_max_use", 10_000D, 0D, 100_000D);
			TELEPORTER_MAX_DISTANCE = b.defineInRange("teleporter_max_distance", 1_200D, 1D, 100_000D);
			TELEPORTER_TRANSPORT_DRAIN = b.comment("Zaps drained once per active second from the sending teleporter while items or fluids are flowing through a linked pair.").defineInRange("teleporter_transport_drain", 32D, 0D, 1_000_000D);
			TELEPORTER_ACTIVE_WINDOW_TICKS = b.comment("How recent the last successful transfer must be (in ticks) for the pair to be considered active for drain and chunk-loading.").defineInRange("teleporter_active_window_ticks", 20, 1, 12_000);
			TELEPORTER_CHUNK_LOAD_IDLE_TICKS = b.comment("Release the peer chunk ticket after this many ticks of inactivity. Should be >= teleporter_active_window_ticks.").defineInRange("teleporter_chunk_load_idle_ticks", 1_200, 1, 72_000);
			TELEPORTER_BALANCE_RATE = b.comment("Maximum zaps per tick shuffled between a linked pair to keep their energy buffers in balance.").defineInRange("teleporter_balance_rate", 128D, 0D, 1_000_000D);
			CHARGE_PAD_CAPACITY = b.defineInRange("charge_pad_capacity", 1_000_000D, 1D, 10_000_000D);
			POWERED_CRAFTING_TABLE_CAPACITY = b.defineInRange("powered_crafting_table_capacity", 1_200D, 1D, 100_000D);
			POWERED_CRAFTING_TABLE_USE = b.defineInRange("powered_crafting_table_use", 1D, 0D, 100_000D);
			QUARRY_CAPACITY = b.defineInRange("quarry_capacity", 10_000D, 1D, 100_000D);
			QUARRY_USE = b.defineInRange("quarry_use", 3D, 0D, 100_000D);
			QUARRY_MINE_TICKS = b.defineInRange("quarry_mine_ticks", 8L, 0L, 100_000L);
			QUARRY_MOVE_TICKS = b.defineInRange("quarry_move_ticks", 2L, 0L, 100_000L);
			QUARRY_REPLACE_FLUID_EXFLUID = b.define("quarry_replace_fluid_with_exfluid", true);
			QUARRY_PICKAXE_TAKES_DAMAGE = b.comment("If true, a pickaxe in the quarry's pickaxe slot takes durability damage each block mined.")
					.define("quarry_pickaxe_takes_damage", false);
			PUMP_CAPACITY = b.defineInRange("pump_capacity", 10_000D, 1D, 100_000D);
			PUMP_USE = b.defineInRange("pump_use", 3D, 0D, 100_000D);
			PUMP_MINE_TICKS = b.defineInRange("pump_mine_ticks", 40L, 0L, 100_000L);
			PUMP_MOVE_TICKS = b.defineInRange("pump_move_ticks", 10L, 0L, 100_000L);
			PUMP_REPLACE_FLUID_EXFLUID = b.define("pump_replace_fluid_with_exfluid", true);
			PUMP_TANK_CAPACITY = b.defineInRange("pump_tank_capacity", FLUID_BUCKET_VOLUME * 128, 1, 100_000);
			ITEM_TRANSFER_EFFICIENCY = b.defineInRange("item_transfer_efficiency", 20.0D, 0D, Double.POSITIVE_INFINITY);
			STATE_UPDATE_TICKS = b.comment("To reduce lag, block states only update every X ticks.").defineInRange("state_update_ticks", 6, 1, 1_000);
			UPGRADE_LIMIT_PER_SLOT = b.defineInRange("upgrade_limit_per_slot", 4, 1, 64);
			OVERCLOCKER_SPEED = b.defineInRange("overclocker_speed", 1.45D, 0D, 100_000D);
			OVERCLOCKER_ENERGY_USE = b.defineInRange("overclocker_energy_use", 1.6D, 0D, 100_000D);
			STORAGE_UPGRADE = b.defineInRange("storage_upgrade", 10_000D, 0D, 100_000D);
			SCRAP_CHANCE = b.defineInRange("scrap_chance", 0.125D, 0D, 1D);
			b.pop();
		}
	}

	public static final class Nuclear {
		public final ModConfigSpec.DoubleValue NUKE_RADIUS;
		public final ModConfigSpec.DoubleValue NUCLEAR_REACTOR_EXPLOSION_BASE_RADIUS;
		public final ModConfigSpec.DoubleValue NUCLEAR_REACTOR_EXPLOSION_MULTIPLIER;
		public final ModConfigSpec.DoubleValue NUCLEAR_REACTOR_EXPLOSION_LIMIT;
		public final ModConfigSpec.IntValue FLUID_CELL_CAPACITY;
		public final ModConfigSpec.BooleanValue ADD_ALL_FLUID_CELLS;
		public final ModConfigSpec.BooleanValue NUCLEAR_EXPLOSION_DAEMON_THREAD;
		public final ModConfigSpec.DoubleValue WATER_COOLING_MULTIPLIER;
		public final ModConfigSpec.IntValue NUKE_FUSE_TICKS;
		public final ModConfigSpec.BooleanValue REACTOR_MELTDOWN_RESPECTS_CLAIMS;

		Nuclear(ModConfigSpec.Builder b) {
			b.push("nuclear");
			NUKE_RADIUS = b.defineInRange("nuke_radius", 36D, 1D, 1_000D);
			NUCLEAR_REACTOR_EXPLOSION_BASE_RADIUS = b.defineInRange("nuclear_reactor_explosion_base_radius", 10D, 1D, 1_000D);
			NUCLEAR_REACTOR_EXPLOSION_MULTIPLIER = b.defineInRange("nuclear_reactor_explosion_multiplier", 0.5D, 0.001D, 100D);
			NUCLEAR_REACTOR_EXPLOSION_LIMIT = b.defineInRange("nuclear_reactor_explosion_limit", 80D, 1D, 10_000D);
			FLUID_CELL_CAPACITY = b.defineInRange("fluid_cell_capacity", FLUID_BUCKET_VOLUME, 1, 100_000);
			ADD_ALL_FLUID_CELLS = b.define("add_all_fluid_cells", false);
			NUCLEAR_EXPLOSION_DAEMON_THREAD = b.comment("Spawn a daemon thread for explosion calc (experimental).").define("nuclear_explosion_daemon_thread", false);
			WATER_COOLING_MULTIPLIER = b.comment("Max reactor-hull cooling multiplier when every outward face is water-clad. 1.0 disables the bonus; 2.0 doubles cooling; scales linearly with water-adjacent faces.")
					.defineInRange("water_cooling_multiplier", 2.0D, 1.0D, 10.0D);
			NUKE_FUSE_TICKS = b.comment("Fuse length for the placed Nuke block in ticks (20 ticks per second).")
					.defineInRange("nuke_fuse_ticks", 200, 0, 20 * 60 * 10);
			REACTOR_MELTDOWN_RESPECTS_CLAIMS = b.comment("If true, nuclear reactor meltdown uses a vanilla explosion which chunk-protection mods (FTB Chunks etc.) can cancel. If false (default), reactor meltdown bypasses claim protection so the reactor itself and surrounding terrain always suffer the consequences.")
					.define("reactor_meltdown_respects_claims", false);
			b.pop();
		}
	}

	public static final class Recipes {
		public final ModConfigSpec.BooleanValue ADD_DUST_FROM_ORE_RECIPES;
		public final ModConfigSpec.BooleanValue ADD_DUST_FROM_MATERIAL_RECIPES;
		public final ModConfigSpec.BooleanValue ADD_GEM_FROM_ORE_RECIPES;
		public final ModConfigSpec.BooleanValue ADD_ROD_RECIPES;
		public final ModConfigSpec.BooleanValue ADD_PLATE_RECIPES;
		public final ModConfigSpec.BooleanValue ADD_GEAR_RECIPES;
		public final ModConfigSpec.BooleanValue ADD_CANNED_FOOD_RECIPES;

		Recipes(ModConfigSpec.Builder b) {
			b.push("recipes");
			ADD_DUST_FROM_ORE_RECIPES = b.define("add_dust_from_ore_recipes", true);
			ADD_DUST_FROM_MATERIAL_RECIPES = b.define("add_dust_from_material_recipes", true);
			ADD_GEM_FROM_ORE_RECIPES = b.define("add_gem_from_ore_recipes", true);
			ADD_ROD_RECIPES = b.define("add_rod_recipes", true);
			ADD_PLATE_RECIPES = b.define("add_plate_recipes", true);
			ADD_GEAR_RECIPES = b.define("add_gear_recipes", true);
			ADD_CANNED_FOOD_RECIPES = b.define("add_canned_food_recipes", true);
			b.pop();
		}
	}

	public static final List<String> MOD_MATERIAL_PRIORITY = new ArrayList<>(Arrays.asList(
			"minecraft",
			"emendatusenigmatica",
			"thermal",
			"mekanism",
			"immersiveengineering"
	));

	public static final Map<String, CraftingMaterial> MATERIALS = new LinkedHashMap<>();

	public static void addMaterial(String m) {
		MATERIALS.put(m, new CraftingMaterial(m));
	}

	public static void removeMaterial(String m) {
		MATERIALS.remove(m);
	}

	static {
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
		addMaterial("ruby");
		addMaterial("sapphire");
		addMaterial("peridot");
		addMaterial("redstone");
	}

	private static int getOrder(@Nullable Identifier id) {
		int i = id == null ? -1 : MOD_MATERIAL_PRIORITY.indexOf(id.getNamespace());
		return i == -1 ? MOD_MATERIAL_PRIORITY.size() : i;
	}

	public static Item getItemFromTag(TagKey<Item> tag) {
		List<Item> items = new ArrayList<>();
		BuiltInRegistries.ITEM.getTagOrEmpty(tag).forEach(h -> items.add(h.value()));

		if (items.isEmpty()) {
			return Items.AIR;
		} else if (items.size() == 1) {
			return items.get(0);
		}

		int order = Integer.MAX_VALUE;
		Item current = null;

		for (Item item : items) {
			int o = getOrder(BuiltInRegistries.ITEM.getKey(item));

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

	private FTBICConfig() {}
}
