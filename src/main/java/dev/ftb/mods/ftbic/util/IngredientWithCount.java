package dev.ftb.mods.ftbic.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Ingredient paired with a required count. Used by machine recipes which need "3 of X" inputs.
 * Ported to 26.1 Codec/StreamCodec from the 1.18.2 JSON/FriendlyByteBuf serialisation.
 */
public record IngredientWithCount(Ingredient ingredient, int count) {
	public static final IngredientWithCount[] EMPTY = new IngredientWithCount[0];

	public static final Codec<IngredientWithCount> CODEC = RecordCodecBuilder.create(i -> i.group(
			Ingredient.CODEC.fieldOf("ingredient").forGetter(IngredientWithCount::ingredient),
			Codec.INT.optionalFieldOf("count", 1).forGetter(IngredientWithCount::count)
	).apply(i, IngredientWithCount::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, IngredientWithCount> STREAM_CODEC = StreamCodec.composite(
			Ingredient.CONTENTS_STREAM_CODEC, IngredientWithCount::ingredient,
			ByteBufCodecs.VAR_INT, IngredientWithCount::count,
			IngredientWithCount::new
	);

	public boolean matches(ItemStack stack) {
		return stack.getCount() >= count && ingredient.test(stack);
	}
}
