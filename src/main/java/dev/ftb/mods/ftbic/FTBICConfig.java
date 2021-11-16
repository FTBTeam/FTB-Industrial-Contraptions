package dev.ftb.mods.ftbic;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.FluidAttributes;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FTBICConfig {
	public static Component ENERGY_FORMAT = new TextComponent("⚡").withStyle(ChatFormatting.BOLD);

	public static double LV_TRANSFER_RATE = 32;
	public static double MV_TRANSFER_RATE = 128;
	public static double HV_TRANSFER_RATE = 512;
	public static double EV_TRANSFER_RATE = 2_048;
	public static double IV_TRANSFER_RATE = 8_192;
	public static double ITEM_TRANSFER_EFFICIENCY = 0.75;

	public static double SINGLE_USE_BATTERY_CAPACITY = 2_400;
	public static double LV_BATTERY_CAPACITY = 4_000;
	public static double MV_BATTERY_CAPACITY = 40_000;
	public static double HV_BATTERY_CAPACITY = 400_000;
	public static double EV_BATTERY_CAPACITY = 10_000_000;

	public static double CARBON_ARMOR_CAPACITY = 1_000_000;
	public static double QUANTUM_ARMOR_CAPACITY = 15_000_000;
	public static double MECHANICAL_ELYTRA_CAPACITY = 50_000;
	public static double MECHANICAL_ELYTRA_RECHARGE = 1;
	public static double ARMOR_DAMAGE_ENERGY = 5_000;
	public static double ARMOR_FLIGHT_ENERGY = 5;
	public static double ARMOR_FLIGHT_BOOST = 50;
	public static double ARMOR_FLIGHT_STOP = 15;

	public static double LV_BATTERY_BOX_CAPACITY = 40_000;
	public static double MV_BATTERY_BOX_CAPACITY = 400_000;
	public static double HV_BATTERY_BOX_CAPACITY = 4_000_000;
	public static double EV_BATTERY_BOX_CAPACITY = 40_000_000;

	public static int STATE_UPDATE_TICKS = 6;
	public static int IRON_FURNACE_ITEMS_PER_COAL = 12;
	public static int MAX_CABLE_LENGTH = 300;

	public static double BASIC_GENERATOR_CAPACITY = 4000;
	public static double BASIC_GENERATOR_OUTPUT = 10;
	public static int GEOTHERMAL_GENERATOR_TANK_SIZE = 8_000;
	public static double GEOTHERMAL_GENERATOR_CAPACITY = 2_400;
	public static double GEOTHERMAL_GENERATOR_OUTPUT = 20;
	public static double WIND_MILL_MIN_OUTPUT = 0.3;
	public static double WIND_MILL_MAX_OUTPUT = 6.5;
	public static int WIND_MILL_MIN_Y = 64;
	public static int WIND_MILL_MAX_Y = 250;
	public static double WIND_MILL_RAIN_MODIFIER = 1.2;
	public static double WIND_MILL_THUNDER_MODIFIER = 1.5;
	public static double LV_SOLAR_PANEL_OUTPUT = 1;
	public static double MV_SOLAR_PANEL_OUTPUT = 8;
	public static double HV_SOLAR_PANEL_OUTPUT = 64;
	public static double EV_SOLAR_PANEL_OUTPUT = 256;

	public static double MACHINE_RECIPE_BASE_TICKS = 200;

	public static double POWERED_FURNACE_CAPACITY = 416;
	public static double POWERED_FURNACE_USE = 3;
	public static double MACERATOR_CAPACITY = 1200;
	public static double MACERATOR_USE = 2;
	public static double CENTRIFUGE_CAPACITY = 800;
	public static double CENTRIFUGE_USE = 2;
	public static double COMPRESSOR_CAPACITY = 800;
	public static double COMPRESSOR_USE = 2;
	public static double REPROCESSOR_CAPACITY = 4_000;
	public static double REPROCESSOR_USE = 8;
	public static double CANNING_MACHINE_CAPACITY = 800;
	public static double CANNING_MACHINE_USE = 1;
	public static double ROLLER_CAPACITY = 800;
	public static double ROLLER_USE = 2;
	public static double EXTRUDER_CAPACITY = 800;
	public static double EXTRUDER_USE = 2;
	public static double ANTIMATTER_CONSTRUCTOR_CAPACITY = 500_000;
	public static double ANTIMATTER_CONSTRUCTOR_USE = 1_000;
	public static double ADVANCED_POWERED_FURNACE_CAPACITY = 10_000;
	public static double ADVANCED_POWERED_FURNACE_USE = 16;
	public static double ADVANCED_MACERATOR_CAPACITY = 10_000;
	public static double ADVANCED_MACERATOR_USE = 16;
	public static double ADVANCED_CENTRIFUGE_CAPACITY = 10_000;
	public static double ADVANCED_CENTRIFUGE_USE = 16;
	public static double ADVANCED_COMPRESSOR_CAPACITY = 10_000;
	public static double ADVANCED_COMPRESSOR_USE = 16;
	public static double TELEPORTER_CAPACITY = 10_000_000;
	public static double TELEPORTER_USE = 10_000;
	public static double CHARGE_PAD_CAPACITY = 1_000_000;

	public static int UPGRADE_LIMIT_PER_SLOT = 4;
	public static double OVERCLOCKER_SPEED = 1.45;
	public static double OVERCLOCKER_ENERGY_USE = 1.6;
	public static double STORAGE_UPGRADE = 10_000;
	public static int FLUID_CELL_CAPACITY = FluidAttributes.BUCKET_VOLUME;
	public static boolean ADD_ALL_FLUID_CELLS = false;

	public static boolean ADD_DUST_FROM_ORE_RECIPES = true;
	public static boolean ADD_DUST_FROM_MATERIAL_RECIPES = true;
	public static boolean ADD_GEM_FROM_ORE_RECIPES = true;
	public static boolean ADD_ROD_RECIPES = true;
	public static boolean ADD_PLATE_RECIPES = true;
	public static boolean ADD_GEAR_RECIPES = true;
	public static boolean ADD_CANNED_FOOD_RECIPES = true;
	public static double SCRAP_CHANCE = 0.25;

	public static final List<String> MOD_MATERIAL_PRIORITY = new ArrayList<>(Arrays.asList(
			"minecraft",
			"emendatusenigmatica",
			"thermal",
			"mekanism",
			"immersiveengineering"
	));

	public static final List<String> AUTO_METALS = new ArrayList<>(Arrays.asList(
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
			"iridium",
			"cast_iron",
			"tungsten",
			"lithium"
	));

	public static final List<String> AUTO_GEMS = new ArrayList<>(Arrays.asList(
			"redstone",
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

	private static int getOrder(@Nullable ResourceLocation id) {
		int i = id == null ? -1 : MOD_MATERIAL_PRIORITY.indexOf(id.getNamespace());
		return i == -1 ? MOD_MATERIAL_PRIORITY.size() : i;
	}

	public static Item getItemFromTag(Tag<Item> tag) {
		List<Item> items = tag.getValues();

		if (items.isEmpty()) {
			return Items.AIR;
		} else if (items.size() == 1) {
			return items.get(0);
		}

		int order = Integer.MAX_VALUE;
		Item current = null;

		for (Item item : items) {
			int o = getOrder(item.getRegistryName());

			if (o == 0) {
				return item;
			} else if (current == null || o < order) {
				current = item;
				order = o;
			}
		}

		return current;
	}
}
