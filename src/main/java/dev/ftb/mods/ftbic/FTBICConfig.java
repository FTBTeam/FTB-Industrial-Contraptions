package dev.ftb.mods.ftbic;

import dev.ftb.mods.ftbic.util.CraftingMaterial;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FTBICConfig {
	public static Component ENERGY_FORMAT = new TextComponent("⚡").withStyle(ChatFormatting.BOLD);
	public static Component HEAT_FORMAT = new TextComponent("\uD83D\uDD25");

	public static double LV_TRANSFER_RATE = 32;
	public static double MV_TRANSFER_RATE = 128;
	public static double HV_TRANSFER_RATE = 512;
	public static double EV_TRANSFER_RATE = 2_048;
	public static double IV_TRANSFER_RATE = 8_192;
	public static double ITEM_TRANSFER_EFFICIENCY = 0.75;
	public static double ZAP_TO_FE_CONVERSION_RATE = 4.0;

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
	public static double WIND_MILL_CAPACITY = 100;
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
	public static double NUCLEAR_REACTOR_CAPACITY = 8000;

	public static double MACHINE_RECIPE_BASE_TICKS = 200;

	public static double POWERED_FURNACE_CAPACITY = 1_200;
	public static double POWERED_FURNACE_USE = 3;
	public static double MACERATOR_CAPACITY = 1_200;
	public static double MACERATOR_USE = 2;
	public static double CENTRIFUGE_CAPACITY = 1_200;
	public static double CENTRIFUGE_USE = 2;
	public static double COMPRESSOR_CAPACITY = 1_200;
	public static double COMPRESSOR_USE = 2;
	public static double REPROCESSOR_CAPACITY = 4_000;
	public static double REPROCESSOR_USE = 8;
	public static double CANNING_MACHINE_CAPACITY = 1_200;
	public static double CANNING_MACHINE_USE = 1;
	public static double ROLLER_CAPACITY = 1_200;
	public static double ROLLER_USE = 3;
	public static double EXTRUDER_CAPACITY = 1_200;
	public static double EXTRUDER_USE = 3;
	public static double ANTIMATTER_CONSTRUCTOR_CAPACITY = 1_000_000;
	public static double ANTIMATTER_CONSTRUCTOR_BOOST = 6;
	public static double ADVANCED_POWERED_FURNACE_CAPACITY = 10_000;
	public static double ADVANCED_POWERED_FURNACE_USE = 16;
	public static double ADVANCED_MACERATOR_CAPACITY = 10_000;
	public static double ADVANCED_MACERATOR_USE = 16;
	public static double ADVANCED_CENTRIFUGE_CAPACITY = 10_000;
	public static double ADVANCED_CENTRIFUGE_USE = 16;
	public static double ADVANCED_COMPRESSOR_CAPACITY = 10_000;
	public static double ADVANCED_COMPRESSOR_USE = 16;
	public static double TELEPORTER_CAPACITY = 1_000_000;
	public static double TELEPORTER_MIN_USE = 100;
	public static double TELEPORTER_MIN_DISTANCE = 16;
	public static double TELEPORTER_MAX_USE = 10_000;
	public static double TELEPORTER_MAX_DISTANCE = 1_200;
	public static double CHARGE_PAD_CAPACITY = 1_000_000;
	public static double POWERED_CRAFTING_TABLE_CAPACITY = 1_200;
	public static double POWERED_CRAFTING_TABLE_USE = 1;
	public static double QUARRY_CAPACITY = 10_000;
	public static double QUARRY_USE = 3;
	public static long QUARRY_MINE_TICKS = 40;
	public static long QUARRY_MOVE_TICKS = 10;
	public static double PUMP_CAPACITY = 10_000;
	public static double PUMP_USE = 3;
	public static long PUMP_MINE_TICKS = 40;
	public static long PUMP_MOVE_TICKS = 10;
	public static int PUMP_TANK_CAPACITY = FluidAttributes.BUCKET_VOLUME * 128;

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
	public static double SCRAP_CHANCE = 0.125;

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

	public static void init() {
	}
}
