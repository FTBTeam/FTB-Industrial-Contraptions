package dev.ftb.mods.ftbic.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public record SoilOption(Ingredient ingredient, double speed) {
    private static final Codec<SoilOption> BASE_CODEC = RecordCodecBuilder.create(i -> i.group(
                    Ingredient.CODEC.fieldOf("ingredient").forGetter(SoilOption::ingredient),
                    Codec.DOUBLE.fieldOf("speed").forGetter(SoilOption::speed))
            .apply(i, SoilOption::new));
    public static final Codec<SoilOption> CODEC =
            BASE_CODEC.validate(option -> option.speed() > 0 && Double.isFinite(option.speed())
                    ? DataResult.success(option)
                    : DataResult.error(() -> "Soil speed must be positive and finite"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SoilOption> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            SoilOption::ingredient,
            ByteBufCodecs.DOUBLE,
            SoilOption::speed,
            SoilOption::new);

    public boolean matches(ItemStack stack) {
        return !stack.isEmpty() && ingredient.test(stack);
    }
}
