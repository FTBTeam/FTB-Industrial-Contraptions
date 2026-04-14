package dev.ftb.mods.ftbic.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

/**
 * An output ItemStack paired with a drop chance [0.0, 1.0]. Chance of 1.0 is a guaranteed output.
 */
public record StackWithChance(ItemStack stack, double chance) {
	public static final StackWithChance EMPTY = new StackWithChance(ItemStack.EMPTY, 0D);

	public static final Codec<StackWithChance> CODEC = RecordCodecBuilder.create(i -> i.group(
			ItemStack.CODEC.fieldOf("item").forGetter(StackWithChance::stack),
			Codec.DOUBLE.optionalFieldOf("chance", 1D).forGetter(StackWithChance::chance)
	).apply(i, StackWithChance::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, StackWithChance> STREAM_CODEC = StreamCodec.composite(
			ItemStack.STREAM_CODEC, StackWithChance::stack,
			ByteBufCodecs.DOUBLE, StackWithChance::chance,
			StackWithChance::new
	);
}
