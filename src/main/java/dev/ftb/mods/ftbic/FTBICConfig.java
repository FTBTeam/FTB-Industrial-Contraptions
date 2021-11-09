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

	public static double LV_TRANSFER_RATE = 320;
	public static double MV_TRANSFER_RATE = 1280;
	public static double HV_TRANSFER_RATE = 40960;
	public static double EV_TRANSFER_RATE = 163840;
	public static double ITEM_TRANSFER_EFFICIENCY = 0.1D;

	public static double SINGLE_USE_BATTERY_CAPACITY = 24_000;
	public static double LV_BATTERY_CAPACITY = 40_000;
	public static double MV_BATTERY_CAPACITY = 400_000;
	public static double HV_BATTERY_CAPACITY = 4_000_000;
	public static double EV_BATTERY_CAPACITY = 1_000_000_000;

	public static double LV_BATTERY_BOX_CAPACITY = 400_000;
	public static double MV_BATTERY_BOX_CAPACITY = 3_000_000;
	public static double HV_BATTERY_BOX_CAPACITY = 40_000_000;

	public static int IRON_FURNACE_ITEMS_PER_COAL = 12;
	public static double BASIC_GENERATOR_OUTPUT = 100;
	public static int GEOTHERMAL_GENERATOR_LAVA_TANK = 8000;
	public static double GEOTHERMAL_GENERATOR_OUTPUT = 100;

	public static double LV_SOLAR_PANEL_GENERATION = 4;
	public static double MV_SOLAR_PANEL_GENERATION = 32;
	public static double HV_SOLAR_PANEL_GENERATION = 256;

	public static double MACHINE_RECIPE_BASE_TICKS = 200D;

	public static int UPGRADE_LIMIT_PER_SLOT = 4;
	public static double OVERCLOCKER_SPEED = 1.45D;
	public static double OVERCLOCKER_ENERGY_USE = 1.6D;
	public static double STORAGE_UPGRADE = 10000D;

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
