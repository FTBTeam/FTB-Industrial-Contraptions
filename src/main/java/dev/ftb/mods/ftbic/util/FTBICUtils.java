package dev.ftb.mods.ftbic.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.FTBICConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public final class FTBICUtils {
	public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setStrictness(com.google.gson.Strictness.LENIENT).create();
	public static final TagKey<Block> REINFORCED = TagKey.create(Registries.BLOCK, FTBIC.id("reinforced"));
	public static final TagKey<Item> UNCANNABLE_FOOD = TagKey.create(Registries.ITEM, FTBIC.id("uncannable_food"));
	public static final TagKey<Item> NO_AUTO_RECIPE = TagKey.create(Registries.ITEM, FTBIC.id("no_auto_recipe"));

	public static final Direction[] DIRECTIONS = Direction.values();
	public static final Direction[] HORIZONTAL_DIRECTIONS = {Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

	public static void init() {
	}

	public static String formatEnergyValue(double energy) {
		return String.format("%,d", (long) energy);
	}

	public static MutableComponent formatEnergy(double energy) {
		return Component.literal("").append(formatEnergyValue(energy) + " ").append(FTBICConfig.ENERGY_FORMAT);
	}

	public static Component energyTooltip(ItemStack stack, EnergyItemHandler itemHandler) {
		return Component.literal("")
				.append(formatEnergy(itemHandler.getEnergy(stack)).withStyle(ChatFormatting.GRAY))
				.append(" / ")
				.append(formatEnergy(itemHandler.getEnergyCapacity(stack)).withStyle(ChatFormatting.GRAY))
				.withStyle(ChatFormatting.DARK_GRAY);
	}

	public static MutableComponent formatHeat(int heat) {
		return Component.literal("").append(String.format("%,d ", heat)).append(FTBICConfig.HEAT_FORMAT);
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

	private FTBICUtils() {}
}
