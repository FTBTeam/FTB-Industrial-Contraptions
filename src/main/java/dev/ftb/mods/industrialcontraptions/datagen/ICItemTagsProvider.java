package dev.ftb.mods.industrialcontraptions.datagen;

import dev.ftb.mods.industrialcontraptions.IC;
import dev.ftb.mods.industrialcontraptions.item.ICItems;
import dev.ftb.mods.industrialcontraptions.material.Material;
import dev.ftb.mods.industrialcontraptions.material.MaterialComponent;
import dev.ftb.mods.industrialcontraptions.material.MaterialEntries;
import dev.ftb.mods.industrialcontraptions.util.ICUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class ICItemTagsProvider extends IntrinsicHolderTagsProvider<Item> {
	private static final TagKey<Item> SCRAPPABLE = TagKey.create(Registries.ITEM, IC.id("scrappable"));
	private static final TagKey<Item> REACTOR_COMPONENT = TagKey.create(Registries.ITEM, IC.id("reactor_component"));
	private static final TagKey<Item> REINFORCED = TagKey.create(Registries.ITEM, IC.id("reinforced"));
	private static final TagKey<Item> COMMON_SILICON = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "silicon"));

	public ICItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
		super(output, Registries.ITEM, lookup, item -> item.builtInRegistryHolder().key(), IC.MOD_ID);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		for (var entry : MaterialEntries.all()) {
			Item item = entry.item().get();
			Material mat = entry.material();
			MaterialComponent comp = entry.component();
			for (String container : comp.containerTags()) {
				tag(commonItemTag(container)).add(item);
			}
			tag(commonItemTag(comp.perMaterialTag(mat.tagName()))).add(item);
			if (mat == Material.SILICON && comp == MaterialComponent.GEM) {
				tag(COMMON_SILICON).add(item);
			}
		}

		tag(ItemTags.ARROWS).add(ICItems.NUKE_ARROW.get());

		tag(REINFORCED)
				.add(ICItems.REINFORCED_STONE.get())
				.add(ICItems.REINFORCED_GLASS.get())
				.add(ICItems.LV_REINFORCED_CABLE.get())
				.add(ICItems.MV_REINFORCED_CABLE.get())
				.add(ICItems.HV_REINFORCED_CABLE.get())
				.add(ICItems.EV_REINFORCED_CABLE.get())
				.add(ICItems.IV_REINFORCED_CABLE.get())
				.add(ICItems.BURNT_REINFORCED_CABLE.get());

		tag(ICUtils.UNCANNABLE_FOOD)
				.add(ICItems.CANNED_FOOD.get())
				.add(ICItems.PROTEIN_BAR.get());

		tag(REACTOR_COMPONENT)
				.add(ICItems.URANIUM_FUEL_ROD.get())
				.add(ICItems.DUAL_URANIUM_FUEL_ROD.get())
				.add(ICItems.QUAD_URANIUM_FUEL_ROD.get())
				.add(ICItems.HEAT_VENT.get())
				.add(ICItems.ADVANCED_HEAT_VENT.get())
				.add(ICItems.REACTOR_HEAT_VENT.get())
				.add(ICItems.COMPONENT_HEAT_VENT.get())
				.add(ICItems.OVERCLOCKED_HEAT_VENT.get())
				.add(ICItems.HEAT_EXCHANGER.get())
				.add(ICItems.ADVANCED_HEAT_EXCHANGER.get())
				.add(ICItems.REACTOR_HEAT_EXCHANGER.get())
				.add(ICItems.COMPONENT_HEAT_EXCHANGER.get())
				.add(ICItems.SMALL_COOLANT_CELL.get())
				.add(ICItems.MEDIUM_COOLANT_CELL.get())
				.add(ICItems.LARGE_COOLANT_CELL.get())
				.add(ICItems.NEUTRON_REFLECTOR.get())
				.add(ICItems.THICK_NEUTRON_REFLECTOR.get())
				.add(ICItems.IRIDIUM_NEUTRON_REFLECTOR.get())
				.add(ICItems.REACTOR_PLATING.get())
				.add(ICItems.CONTAINMENT_REACTOR_PLATING.get())
				.add(ICItems.HEAT_CAPACITY_REACTOR_PLATING.get());

		tag(SCRAPPABLE)
				.add(Items.DIRT)
				.add(Items.COBBLESTONE)
				.add(Items.COBBLED_DEEPSLATE)
				.add(Items.GRAVEL)
				.add(Items.SAND)
				.add(Items.RED_SAND)
				.add(Items.NETHERRACK)
				.add(Items.BASALT)
				.add(Items.ANDESITE)
				.add(Items.DIORITE)
				.add(Items.GRANITE)
				.add(Items.TUFF)
				.add(Items.END_STONE)
				.add(Items.ROTTEN_FLESH)
				.addOptionalTag(ItemTags.LEAVES)
				.addOptionalTag(ItemTags.SAPLINGS)
				.addOptionalTag(ItemTags.DIRT);
	}

	private static TagKey<Item> commonItemTag(String fullPath) {
		Identifier id;
		if (fullPath.contains(":")) {
			String[] parts = fullPath.split(":", 2);
			id = Identifier.fromNamespaceAndPath(parts[0], parts[1]);
		} else {
			id = Identifier.fromNamespaceAndPath("c", fullPath);
		}
		return TagKey.create(Registries.ITEM, id);
	}
}
