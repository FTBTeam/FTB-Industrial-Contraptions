package dev.ftb.mods.ftbic.client;

import java.util.List;
import java.util.Map;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

public interface RecipeViewerBridge {
    void add(List<ItemStack> variants, Map<RecipeType<?>, List<RecipeHolder<?>>> recipes);

    void remove(List<ItemStack> variants);

    void showTypes(List<RecipeType<?>> types);

    void setSearchFilter(String text);
}
