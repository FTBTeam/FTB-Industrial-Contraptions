package dev.ftb.mods.ftbic.mixin.compat.mysticalagriculture;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

/** Allows MA's item index to enumerate custom ingredients without losing their stack predicates. */
@Pseudo
@Mixin(targets = "com.blakebr0.mysticalagriculture.util.RecipeIngredientCache", remap = false)
public abstract class RecipeIngredientCacheMixin {
    // Optional injection: an upstream fix may remove either getValues() call.
    @WrapOperation(
            method = {"cache", "cacheVesselItems"},
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/item/crafting/Ingredient;getValues()Lnet/minecraft/core/HolderSet;"),
            require = 0)
    private static HolderSet<Item> ftbic$enumerateCustomIngredient(
            Ingredient ingredient, Operation<HolderSet<Item>> original) {
        return ingredient.isCustom() ? HolderSet.direct(ingredient.items().toList()) : original.call(ingredient);
    }
}
