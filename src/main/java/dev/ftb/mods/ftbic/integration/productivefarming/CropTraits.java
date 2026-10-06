package dev.ftb.mods.ftbic.integration.productivefarming;

import dev.ftb.mods.ftbic.FTBICConfig;
import java.util.List;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

public interface CropTraits {
    String MOD_ID = "productivefarming";

    CropTraits NONE = new CropTraits() {};

    static CropTraits get() {
        if (!FTBICConfig.MACHINES.HYDROPONIC_CROP_TRAITS.get()) return NONE;
        return Holder.INSTANCE;
    }

    default int adjustGrowthTime(int ticks, ItemStack seed) {
        return ticks;
    }

    default void applyToHarvest(List<ItemStack> outputs, ItemStack seed) {}

    default void rollIncrease(ItemStack seed, RandomSource random) {}

    default boolean sameCrop(ItemStack a, ItemStack b) {
        return ItemStack.isSameItemSameComponents(a, b);
    }

    default double mutationChance(double chance, ItemStack parent) {
        return chance;
    }

    default int mutability(ItemStack seed) {
        return 0;
    }

    default void inheritTraits(ItemStack parent, ItemStack child) {}

    default Optional<Mutation> findMutation(ServerLevel level, ItemStack first, ItemStack second) {
        return Optional.empty();
    }

    record Mutation(String id, ItemStack result, double chance) {}

    final class Holder {
        static final CropTraits INSTANCE = ModList.get().isLoaded(MOD_ID) ? new ProductiveFarmingCropTraits() : NONE;

        private Holder() {}
    }
}
