package dev.ftb.mods.ftbic.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

public record StackWithChance(ItemStack stack, double chance) {
	public static final StackWithChance EMPTY = new StackWithChance(ItemStack.EMPTY, 0D);

	public static final Codec<StackWithChance> CODEC = RecordCodecBuilder.create(i -> i.group(
			ItemStackTemplate.CODEC.fieldOf("item").forGetter(swc ->
					new ItemStackTemplate(swc.stack().typeHolder(), swc.stack().getCount(), swc.stack().getComponentsPatch())),
			Codec.DOUBLE.optionalFieldOf("chance", 1D).forGetter(StackWithChance::chance)
	).apply(i, (template, chance) -> new StackWithChance(template.create(), chance)));

	public static final StreamCodec<RegistryFriendlyByteBuf, StackWithChance> STREAM_CODEC = StreamCodec.composite(
			ItemStack.STREAM_CODEC, StackWithChance::stack,
			ByteBufCodecs.DOUBLE, StackWithChance::chance,
			StackWithChance::new
	);
}
