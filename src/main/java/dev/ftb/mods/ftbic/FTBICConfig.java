package dev.ftb.mods.ftbic;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FTBICConfig {
	public static Component ENERGY_FORMAT = new TextComponent("⚡").withStyle(ChatFormatting.BOLD);

	public static double LV_TRANSFER_RATE = 32;
	public static double MV_TRANSFER_RATE = 128;
	public static double HV_TRANSFER_RATE = 4096;
	public static double EV_TRANSFER_RATE = 16384;
	public static double ITEM_TRANSFER_EFFICIENCY = 0.1;

	public static double SINGLE_USE_BATTERY_CAPACITY = 2_400;
	public static double LV_BATTERY_CAPACITY = 4_000;
	public static double MV_BATTERY_CAPACITY = 40_000;
	public static double HV_BATTERY_CAPACITY = 400_000;
	public static double EV_BATTERY_CAPACITY = 100_000_000;

	public static double LV_BATTERY_BOX_CAPACITY = 40_000;
	public static double MV_BATTERY_BOX_CAPACITY = 300_000;
	public static double HV_BATTERY_BOX_CAPACITY = 4_000_000;

	public static int IRON_FURNACE_ITEMS_PER_COAL = 12;

	public static double BASIC_GENERATOR_CAPACITY = 1600;
	public static double BASIC_GENERATOR_OUTPUT = 1;
	public static int GEOTHERMAL_GENERATOR_LAVA_TANK = 8000;
	public static double GEOTHERMAL_GENERATOR_CAPACITY = 2400;
	public static double GEOTHERMAL_GENERATOR_OUTPUT = 1;
	public static double WIND_MILL_MIN_OUTPUT = 4;
	public static double WIND_MILL_MAX_OUTPUT = 20;
	public static double LV_SOLAR_PANEL_OUTPUT = 2;
	public static double MV_SOLAR_PANEL_OUTPUT = 16;
	public static double HV_SOLAR_PANEL_OUTPUT = 128;

	public static double MACHINE_RECIPE_BASE_TICKS = 200;

	public static double POWERED_FURNACE_CAPACITY = 416;
	public static double POWERED_FURNACE_USE = 3;
	public static double MACERATOR_CAPACITY = 1200;
	public static double MACERATOR_USE = 2;
	public static double CENTRIFUGE_CAPACITY = 800;
	public static double CENTRIFUGE_USE = 2;
	public static double COMPRESSOR_CAPACITY = 800;
	public static double COMPRESSOR_USE = 2;
	public static double REPROCESSOR_CAPACITY = 4000;
	public static double REPROCESSOR_USE = 8;
	public static double CANNING_MACHINE_CAPACITY = 800;
	public static double CANNING_MACHINE_USE = 1;
	public static double ROLLER_CAPACITY = 800;
	public static double ROLLER_USE = 2;
	public static double EXTRUDER_CAPACITY = 800;
	public static double EXTRUDER_USE = 2;
	public static double ANTIMATTER_CONSTRUCTOR_CAPACITY = 12000;
	public static double ANTIMATTER_CONSTRUCTOR_USE = 20;
	public static double ADVANCED_POWERED_FURNACE_CAPACITY = 416;
	public static double ADVANCED_POWERED_FURNACE_USE = 3;
	public static double ADVANCED_MACERATOR_CAPACITY = 1200;
	public static double ADVANCED_MACERATOR_USE = 2;
	public static double ADVANCED_CENTRIFUGE_CAPACITY = 800;
	public static double ADVANCED_CENTRIFUGE_USE = 2;
	public static double ADVANCED_COMPRESSOR_CAPACITY = 800;
	public static double ADVANCED_COMPRESSOR_USE = 2;

	public static int UPGRADE_LIMIT_PER_SLOT = 4;
	public static double OVERCLOCKER_SPEED = 1.45;
	public static double OVERCLOCKER_ENERGY_USE = 1.6;
	public static double STORAGE_UPGRADE = 1000;

	public static boolean ADD_DUST_FROM_ORE_RECIPES = true;
	public static boolean ADD_DUST_FROM_MATERIAL_RECIPES = true;
	public static boolean ADD_GEM_FROM_ORE_RECIPES = true;
	public static boolean ADD_ROD_RECIPES = true;
	public static boolean ADD_PLATE_RECIPES = true;
	public static boolean ADD_GEAR_RECIPES = true;
	public static boolean ADD_CANNED_FOOD_RECIPES = true;

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
