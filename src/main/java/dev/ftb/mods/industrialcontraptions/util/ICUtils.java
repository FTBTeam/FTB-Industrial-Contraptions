package dev.ftb.mods.industrialcontraptions.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import dev.ftb.mods.industrialcontraptions.IC;
import dev.ftb.mods.industrialcontraptions.ICConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.Locale;

public final class ICUtils {
	public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setStrictness(Strictness.LENIENT).create();
	public static final TagKey<Block> REINFORCED = TagKey.create(Registries.BLOCK, IC.id("reinforced"));
	public static final TagKey<Item> UNCANNABLE_FOOD = TagKey.create(Registries.ITEM, IC.id("uncannable_food"));
	public static final TagKey<Item> NO_AUTO_RECIPE = TagKey.create(Registries.ITEM, IC.id("no_auto_recipe"));

	public static final Direction[] DIRECTIONS = Direction.values();
	public static final Direction[] HORIZONTAL_DIRECTIONS = {Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

	public static void init() {
	}

	public static String formatEnergyValue(double energy) {
		return String.format("%,d", (long) energy);
	}

	public static String fmtInt(long value) {
		return String.format(Locale.ROOT, "%,d", value);
	}

	public static String fmtDouble(double value, int decimals) {
		return String.format(Locale.ROOT, "%." + decimals + "f", value);
	}

	public static MutableComponent formatEnergy(double energy) {
		return Component.literal("").append(formatEnergyValue(energy) + " ").append(ICConfig.ENERGY_FORMAT);
	}

	public static Component energyTooltip(ItemStack stack, EnergyItemHandler itemHandler) {
		return Component.literal("")
				.append(formatEnergy(itemHandler.getEnergy(stack)).withStyle(ChatFormatting.GRAY))
				.append(" / ")
				.append(formatEnergy(itemHandler.getEnergyCapacity(stack)).withStyle(ChatFormatting.GRAY))
				.withStyle(ChatFormatting.DARK_GRAY);
	}

	public static MutableComponent formatHeat(int heat) {
		return Component.literal("").append(String.format("%,d ", heat)).append(ICConfig.HEAT_FORMAT);
	}

	public static int packInt(int value, int max) {
		if (value <= 30000) {
			return Math.max(value, 0);
		} else if (value >= max) {
			return 32767;
		}
		return Math.min(value, 32000);
	}

	public static int unpackInt(int value, int max) {
		if (value <= 30000) {
			return value;
		} else if (value == 32767) {
			return max;
		}
		return value;
	}

	private ICUtils() {}
}
