package dev.ftb.mods.ftbic.util;

import com.google.common.base.Preconditions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

public class RegistryUtil {
	public static ResourceLocation getKey(final Supplier<Item> itemSupplier) {
		return getKey(itemSupplier.get());
	}

	public static ResourceLocation getKey(final Item item) {
		return getKey(ForgeRegistries.ITEMS, item);
	}

	public static <T> ResourceLocation getKey(final IForgeRegistry<T> registry, final T entry) {
		return Preconditions.checkNotNull(registry.getKey(entry), "%s has no registry key", entry);
	}
}
