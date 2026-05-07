package dev.ftb.mods.ftbic.material;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.Nullable;

public record MaterialEntry(
		Material material,
		MaterialComponent component,
		String name,
		@Nullable DeferredBlock<Block> block,
		DeferredItem<Item> item) {
}
