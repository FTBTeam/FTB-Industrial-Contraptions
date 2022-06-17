package dev.ftb.mods.ftbic.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.FTBICConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class FTBICUtils {
	public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setLenient().create();
	public static final TagKey<Block> REINFORCED = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(FTBIC.MOD_ID, "reinforced"));
	public static final TagKey<Item> UNCANNABLE_FOOD = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(FTBIC.MOD_ID, "uncannable_food"));
	public static final TagKey<Item> NO_AUTO_RECIPE = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(FTBIC.MOD_ID, "no_auto_recipe"));

	public static final DeferredRegister<LootItemConditionType> LOOT_REGISTRY = DeferredRegister.create(Registry.LOOT_ITEM_REGISTRY, FTBIC.MOD_ID);
	public static RegistryObject<LootItemConditionType> BURNT_BLOCK = LOOT_REGISTRY.register("burnt_block", () -> new LootItemConditionType(new BurntBlockCondition.Serializer()));

	public static final Direction[] DIRECTIONS = Direction.values();
	public static final Direction[] HORIZONTAL_DIRECTIONS = {Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

	public static void init() {
		if (ModList.get().isLoaded("ftbchunks")) {
			initFTBChunks();
		}
	}

	private static void initFTBChunks() {
		FTBChunksIntegration.instance = new FTBChunksIntegrationImpl();
	}

	public static String formatEnergyValue(double energy) {
		return String.format("%,d", (long) energy);
	}

	public static MutableComponent formatEnergy(double energy) {
		return Component.literal("").append(formatEnergyValue(energy) + " ").append(FTBICConfig.ENERGY_FORMAT);
	}

	public static Component energyTooltip(ItemStack stack, EnergyItemHandler itemHandler) {
		return Component.literal("").append(formatEnergy(itemHandler.getEnergy(stack)).withStyle(ChatFormatting.GRAY)).append(" / ").append(formatEnergy(itemHandler.getEnergyCapacity(stack)).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY);
	}

	public static Component formatHeat(int heat) {
		return Component.literal("").append(String.format("%,d ", heat)).append(FTBICConfig.HEAT_FORMAT);
	}

	public static <T> List<T> listFromJson(JsonObject json, String key, Function<JsonElement, T> function) {
		if (json.has(key)) {
			JsonArray array = json.get(key).getAsJsonArray();

			if (array.size() == 0) {
				return Collections.emptyList();
			} else if (array.size() == 1) {
				return Collections.singletonList(function.apply(array.get(0)));
			}

			List<T> list = new ArrayList<>(array.size());

			for (JsonElement e : array) {
				list.add(function.apply(e));
			}

			return list;
		}

		return Collections.emptyList();
	}

	public static <T> void listToJson(List<T> list, JsonObject json, String key, Function<T, JsonElement> function) {
		JsonArray array = new JsonArray();

		for (T obj : list) {
			array.add(function.apply(obj));
		}

		if (array.size() > 0) {
			json.add(key, array);
		}
	}

	public static <T> List<T> listFromNet(FriendlyByteBuf buf, Function<FriendlyByteBuf, T> function) {
		int size = buf.readVarInt();

		if (size == 0) {
			return Collections.emptyList();
		} else if (size == 1) {
			return Collections.singletonList(function.apply(buf));
		}

		List<T> list = new ArrayList<>(size);

		for (int i = 0; i < size; i++) {
			list.add(function.apply(buf));
		}

		return list;
	}

	public static <T> void listToNet(List<T> list, FriendlyByteBuf buf, BiConsumer<T, FriendlyByteBuf> function) {
		buf.writeVarInt(list.size());

		for (T obj : list) {
			function.accept(obj, buf);
		}
	}

	public static FluidStack fluidFromJson(JsonElement element) {
		if (element == null || element.isJsonNull()) {
			return FluidStack.EMPTY;
		} else if (element.isJsonObject()) {
			JsonObject o = element.getAsJsonObject();
			Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(o.get("fluid").getAsString()));
			int amount = o.has("amount") ? o.get("amount").getAsInt() : FluidType.BUCKET_VOLUME;
			return new FluidStack(fluid, amount);
		}

		Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(element.getAsString()));
		return new FluidStack(fluid, FluidType.BUCKET_VOLUME);
	}

	public static JsonElement fluidToJson(FluidStack stack) {
		JsonObject json = new JsonObject();
		json.addProperty("fluid", Registry.FLUID.getKey(stack.getFluid()).toString());
		json.addProperty("amount", stack.getAmount());
		return json;
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
}
