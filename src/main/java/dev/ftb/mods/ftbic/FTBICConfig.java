package dev.ftb.mods.ftbic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FTBICConfig {
	public static String ENERGY_FORMAT_1 = "%,d FE";
	public static String ENERGY_FORMAT_2 = "%,d / %,d FE";

	public static int LV_TRANSFER_RATE = 320;
	public static int MV_TRANSFER_RATE = 1280;
	public static int HV_TRANSFER_RATE = 40960;
	public static int EV_TRANSFER_RATE = 163840;
	public static double BATTERY_TRANSFER_EFFICIENCY = 0.1D;

	public static int SINGLE_USE_BATTERY_CAPACITY = 24_000;
	public static int LV_BATTERY_CAPACITY = 40_000;
	public static int MV_BATTERY_CAPACITY = 400_000;
	public static int HV_BATTERY_CAPACITY = 4_000_000;
	public static int EV_BATTERY_CAPACITY = 1_000_000_000;

	public static int LV_BATTERY_BOX_CAPACITY = 400_000;
	public static int MV_BATTERY_BOX_CAPACITY = 3_000_000;
	public static int HV_BATTERY_BOX_CAPACITY = 40_000_000;

	public static int IRON_FURNACE_ITEMS_PER_COAL = 12;
	public static int BASIC_GENERATOR_OUTPUT = 100;
	public static int GEOTHERMAL_GENERATOR_LAVA_TANK = 8000;
	public static int GEOTHERMAL_GENERATOR_OUTPUT = 100;

	public static int LV_SOLAR_PANEL_GENERATION = 4;
	public static int MV_SOLAR_PANEL_GENERATION = 32;
	public static int HV_SOLAR_PANEL_GENERATION = 256;

	public static final List<String> MOD_MATERIAL_PRIORITY = new ArrayList<>(Arrays.asList(
			"minecraft",
			"emendatusenigmatica",
			"thermal",
			"mekanism",
			"immersiveengineering"
	));

	public static final List<String> AUTO_INGOTS = new ArrayList<>(Arrays.asList(
			"iron",
			"gold",
			"netherite",
			"copper",
			"aluminum",
			"silver",
			"lead",
			"nickel",
			"uranium",
			"osmium",
			"tin",
			"zinc",
			"cobalt",
			"bronze",
			"brass",
			"constantan",
			"electrum",
			"steel",
			"invar",
			"signalum",
			"lumium",
			"enderium",
			"tungsten"
	));

	public static final List<String> AUTO_GEMS = new ArrayList<>(Arrays.asList(
			"lapis",
			"diamond",
			"emerald",
			"quartz",
			"prismarine",
			"certus_quartz",
			"charged_certus_quartz",
			"fluix",
			"fluorite",
			"bitumen",
			"cinnabar",
			"apatite",
			"sulfur",
			"potassium_nitrate",
			"mana",
			"dimensional",
			"silicon",
			"ruby",
			"sapphire",
			"peridot"
	));
}
