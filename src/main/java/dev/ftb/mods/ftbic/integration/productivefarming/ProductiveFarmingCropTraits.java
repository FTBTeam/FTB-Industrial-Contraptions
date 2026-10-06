package dev.ftb.mods.ftbic.integration.productivefarming;

import cy.jdkdigital.productivefarming.common.attachment.CropTraitState;
import cy.jdkdigital.productivefarming.common.item.CropBlockItem;
import cy.jdkdigital.productivefarming.integrations.agritech.AgriTechStatsHelper;
import cy.jdkdigital.productivefarming.recipe.CropMutationRecipe;
import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivefarming.registry.ModTags;
import cy.jdkdigital.productivefarming.util.ExternalCropStats;
import cy.jdkdigital.productivefarming.util.TraitsHelper;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

final class ProductiveFarmingCropTraits implements CropTraits {
    @Override
    public int adjustGrowthTime(int ticks, ItemStack seed) {
        return AgriTechStatsHelper.adjustGrowthTime(ticks, seed);
    }

    @Override
    public void applyToHarvest(List<ItemStack> outputs, ItemStack seed) {
        AgriTechStatsHelper.applyStats(outputs, seed);
    }

    @Override
    public void rollIncrease(ItemStack seed, RandomSource random) {
        if (seed.isEmpty() || !isStatSeed(seed)) return;
        String stat = TraitsHelper.rollIncreasedStat(random, seed.typeHolder());
        if (stat == null) return;
        CropTraitState raised = ExternalCropStats.fromSeedStack(seed).increase(stat);
        TraitsHelper.applyTraits(seed, raised.growth(), raised.yield(), raised.resistance(), raised.mutability());
    }

    @Override
    public boolean sameCrop(ItemStack a, ItemStack b) {
        return ItemStack.isSameItemSameComponents(withoutTraits(a), withoutTraits(b));
    }

    private static ItemStack withoutTraits(ItemStack stack) {
        ItemStack copy = stack.copy();
        copy.remove(FarmingDataComponents.GROWTH);
        copy.remove(FarmingDataComponents.YIELD);
        copy.remove(FarmingDataComponents.RESISTANCE);
        copy.remove(FarmingDataComponents.MUTABILITY);
        return copy;
    }

    private static boolean isStatSeed(ItemStack seed) {
        return seed.getItem() instanceof CropBlockItem
                || ExternalCropStats.isEnabled() && seed.is(ModTags.Items.EXTERNAL_SEEDS);
    }

    @Override
    public int mutability(ItemStack seed) {
        return seed.isEmpty() ? 0 : ExternalCropStats.fromSeedStack(seed).mutability();
    }

    @Override
    public double mutationChance(double chance, ItemStack parent) {
        return Math.min(1D, TraitsHelper.mutationChance((float) chance, mutability(parent)));
    }

    @Override
    public void inheritTraits(ItemStack parent, ItemStack child) {
        if (child.isEmpty() || !hasTraits(parent)) return;
        CropTraitState traits = ExternalCropStats.fromSeedStack(parent);
        TraitsHelper.applyTraits(child, traits.growth(), traits.yield(), traits.resistance(), traits.mutability());
    }

    @Override
    public Optional<Mutation> findMutation(ServerLevel level, ItemStack first, ItemStack second) {
        Identifier a = cropId(first);
        Identifier b = cropId(second);
        if (a == null || b == null) return Optional.empty();
        for (RecipeHolder<CropMutationRecipe> holder :
                level.recipeAccess().recipeMap().byType(FarmingRegistrator.CROP_MUTATION_TYPE.get())) {
            CropMutationRecipe recipe = holder.value();
            if (!recipe.matches(a, b) && !recipe.matches(b, a)) continue;
            ItemStack result = TraitsHelper.mutatedSeed(recipe.mutation());
            if (result.isEmpty()) continue;
            return Optional.of(new Mutation(holder.id().identifier().toString(), result, recipe.chance()));
        }
        return Optional.empty();
    }

    private static Identifier cropId(ItemStack seed) {
        return seed.getItem() instanceof BlockItem block ? BuiltInRegistries.BLOCK.getKey(block.getBlock()) : null;
    }

    private static boolean hasTraits(ItemStack seed) {
        return seed.has(FarmingDataComponents.GROWTH)
                || seed.has(FarmingDataComponents.YIELD)
                || seed.has(FarmingDataComponents.RESISTANCE)
                || seed.has(FarmingDataComponents.MUTABILITY);
    }
}
