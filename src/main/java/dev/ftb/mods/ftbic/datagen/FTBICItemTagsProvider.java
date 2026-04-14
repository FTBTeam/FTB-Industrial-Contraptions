package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.world.ResourceElements;
import dev.ftb.mods.ftbic.world.ResourceType;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class FTBICItemTagsProvider extends ItemTagsProvider {
	public FTBICItemTagsProvider(PackOutput out, CompletableFuture<HolderLookup.Provider> lookup) {
		super(out, lookup, FTBIC.MOD_ID);
	}

	@Override
	protected void addTags(HolderLookup.Provider lookup) {
		addBuckets(ResourceType.ORE, "ores", "_ore");
		addBuckets(ResourceType.INGOT, "ingots", "_ingot");
		addBuckets(ResourceType.DUST, "dusts", "_dust");
		addBuckets(ResourceType.NUGGET, "nuggets", "_nugget");
		addBuckets(ResourceType.PLATE, "plates", "_plate");
		addBuckets(ResourceType.GEAR, "gears", "_gear");
		addBuckets(ResourceType.ROD, "rods", "_rod");
		addBuckets(ResourceType.WIRE, "wires", "_wire");
		addBuckets(ResourceType.RAW, "raw_materials", "_raw");
		addBuckets(ResourceType.BLOCK, "storage_blocks", "_block");
	}

	private void addBuckets(ResourceType type, String umbrella, String suffix) {
		TagKey<Item> umbrellaTag = TagKey.create(Registries.ITEM,
				Identifier.fromNamespaceAndPath("c", umbrella));
		var umbrellaBuilder = tag(umbrellaTag);

		var perElement = FTBICItems.RESOURCE_TYPE_MAP.get(type);
		if (perElement == null) return;
		for (ResourceElements el : ResourceElements.RESOURCES_BY_REQUIREMENT.get(type)) {
			var sup = perElement.get(el);
			if (sup == null) continue;
			Item item = sup.get();
			if (item == null) continue;

			ResourceElements baseEl = type == ResourceType.ORE
					? ResourceElements.getNonDeepslateVersion(el).orElse(el)
					: el;
			TagKey<Item> elementTag = TagKey.create(Registries.ITEM,
					Identifier.fromNamespaceAndPath("c", umbrella + "/" + baseEl.getName().toLowerCase(Locale.ROOT)));
			tag(elementTag).add(item);
			umbrellaBuilder.addTag(elementTag);
		}
	}
}
