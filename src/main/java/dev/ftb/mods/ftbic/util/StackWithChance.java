package dev.ftb.mods.ftbic.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

public record StackWithChance(ItemStackTemplate template, double chance) {
	public static final Codec<StackWithChance> CODEC = RecordCodecBuilder.create(i -> i.group(
			ItemStackTemplate.CODEC.fieldOf("item").forGetter(StackWithChance::template),
			Codec.DOUBLE.optionalFieldOf("chance", 1D).forGetter(StackWithChance::chance)
	).apply(i, StackWithChance::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, StackWithChance> STREAM_CODEC = StreamCodec.composite(
			ItemStackTemplate.STREAM_CODEC, StackWithChance::template,
			ByteBufCodecs.DOUBLE, StackWithChance::chance,
			StackWithChance::new
	);

	public ItemStack stack() {
		return template.create();
	}
}
