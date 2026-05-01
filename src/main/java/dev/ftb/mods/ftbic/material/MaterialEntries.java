package dev.ftb.mods.ftbic.material;

import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.item.FTBICItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class MaterialEntries {
	private static final List<MaterialEntry> ENTRIES = new ArrayList<>();
	private static final Map<Material, Map<MaterialComponent, MaterialEntry>> INDEX = new EnumMap<>(Material.class);
	private static boolean initialized;

	private MaterialEntries() {}

	public static synchronized void register() {
		if (initialized) return;
		initialized = true;

		for (Material material : Material.values()) {
			Map<MaterialComponent, MaterialEntry> perComponent = new EnumMap<>(MaterialComponent.class);
			INDEX.put(material, perComponent);

			for (MaterialComponent component : material.components()) {
				String name = material.key() + "_" + component.suffix();

				if (component.isBlock()) {
					DeferredBlock<Block> block = FTBICBlocks.REGISTRY.register(name,
							id -> new Block(blockProps(component, material.tool())
									.setId(ResourceKey.create(Registries.BLOCK, id))));
					DeferredItem<Item> item = FTBICItems.REGISTRY.register(name,
							id -> new BlockItem(block.get(),
									new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))));
					MaterialEntry entry = new MaterialEntry(material, component, name, block, item);
					ENTRIES.add(entry);
					perComponent.put(component, entry);
				} else {
					DeferredItem<Item> item = FTBICItems.REGISTRY.register(name,
							id -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))));
					MaterialEntry entry = new MaterialEntry(material, component, name, null, item);
					ENTRIES.add(entry);
					perComponent.put(component, entry);
				}
			}
		}
	}

	public static List<MaterialEntry> all() {
		return Collections.unmodifiableList(ENTRIES);
	}

	public static MaterialEntry get(Material material, MaterialComponent component) {
		Map<MaterialComponent, MaterialEntry> perComponent = INDEX.get(material);
		return perComponent == null ? null : perComponent.get(component);
	}

	private static BlockBehaviour.Properties blockProps(MaterialComponent component, Material.Tool tool) {
		BlockBehaviour.Properties props = BlockBehaviour.Properties.of().requiresCorrectToolForDrops();
		return switch (component) {
			case DEEPSLATE_ORE -> props.strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE);
			case STONE_ORE -> props.strength(3.0F, 3.0F).sound(SoundType.STONE);
			case BLOCK -> props.strength(5.0F, 6.0F).sound(SoundType.METAL);
			case RAW_BLOCK -> props.strength(5.0F, 6.0F).sound(SoundType.STONE);
			default -> props.strength(3.0F, 3.0F).sound(SoundType.STONE);
		};
	}
}
