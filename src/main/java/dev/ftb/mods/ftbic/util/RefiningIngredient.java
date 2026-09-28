package dev.ftb.mods.ftbic.util;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.ftb.mods.ftbic.item.RefiningItem;
import dev.ftb.mods.ftbic.registry.ModDataComponents;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

public record RefiningIngredient(Item item, Identifier material) implements ICustomIngredient {
    public static final MapCodec<RefiningIngredient> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(RefiningIngredient::item),
                    Identifier.CODEC.fieldOf("material").forGetter(RefiningIngredient::material))
            .apply(i, RefiningIngredient::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, RefiningIngredient> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(Registries.ITEM),
            RefiningIngredient::item,
            Identifier.STREAM_CODEC,
            RefiningIngredient::material,
            RefiningIngredient::new);

    public ItemStack stack(int count) {
        return RefiningItem.stack(item, material, count);
    }

    @Override
    public boolean test(ItemStack stack) {
        return stack.is(item) && material.equals(stack.get(ModDataComponents.REFINING_MATERIAL.get()));
    }

    @Override
    public Stream<Holder<Item>> items() {
        return Stream.of(item.builtInRegistryHolder());
    }

    @Override
    public SlotDisplay display() {
        return new SlotDisplay.ItemStackSlotDisplay(ItemStackTemplate.fromNonEmptyStack(stack(1)));
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IngredientType<?> getType() {
        return FTBICIngredientTypes.REFINING.get();
    }
}
