package dev.ftb.mods.ftbic.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.CraftingHelper;

public class StackWithChance {
	public static final StackWithChance EMPTY = new StackWithChance(ItemStack.EMPTY, 0D);

	public final ItemStack stack;
	public final double chance;

	public StackWithChance(ItemStack s, double c) {
		stack = s;
		chance = c;
	}

	public StackWithChance(JsonElement element) {
		JsonObject json = element.getAsJsonObject();
		stack = CraftingHelper.getItemStack(json, true);
		chance = json.has("chance") ? json.get("chance").getAsDouble() : 1D;
	}

	public StackWithChance(FriendlyByteBuf buf) {
		stack = buf.readItem();
		chance = buf.readDouble();
	}

	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("item", Registry.ITEM.getKey(stack.getItem()).toString());

		if (stack.getCount() > 1) {
			json.addProperty("count", stack.getCount());
		}

		if (stack.hasTag()) {
			CompoundTag tag = stack.getTag().copy();
			tag.remove("Damage");

			if (!tag.isEmpty()) {
				json.addProperty("nbt", tag.toString());
			}
		}

		if (chance != 1D) {
			json.addProperty("chance", chance);
		}

		return json;
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeItem(stack);
		buf.writeDouble(chance);
	}
}
