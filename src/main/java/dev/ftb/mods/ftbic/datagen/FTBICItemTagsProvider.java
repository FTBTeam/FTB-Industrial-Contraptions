package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class FTBICItemTagsProvider extends IntrinsicHolderTagsProvider<Item> {
	private static final TagKey<Item> SCRAPPABLE = TagKey.create(Registries.ITEM, FTBIC.id("scrappable"));
	private static final TagKey<Item> REACTOR_COMPONENT = TagKey.create(Registries.ITEM, FTBIC.id("reactor_component"));
	private static final TagKey<Item> REINFORCED = TagKey.create(Registries.ITEM, FTBIC.id("reinforced"));

	public FTBICItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
		super(output, Registries.ITEM, lookup, item -> item.builtInRegistryHolder().key(), FTBIC.MOD_ID);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(ItemTags.ARROWS).add(FTBICItems.NUKE_ARROW.get());

		tag(REINFORCED)
				.add(FTBICItems.REINFORCED_STONE.get())
				.add(FTBICItems.REINFORCED_GLASS.get())
				.add(FTBICItems.LV_REINFORCED_CABLE.get())
				.add(FTBICItems.MV_REINFORCED_CABLE.get())
				.add(FTBICItems.HV_REINFORCED_CABLE.get())
				.add(FTBICItems.EV_REINFORCED_CABLE.get())
				.add(FTBICItems.IV_REINFORCED_CABLE.get())
				.add(FTBICItems.BURNT_REINFORCED_CABLE.get());

		tag(FTBICUtils.UNCANNABLE_FOOD)
				.add(FTBICItems.CANNED_FOOD.get())
				.add(FTBICItems.PROTEIN_BAR.get());

		tag(REACTOR_COMPONENT)
				.add(FTBICItems.URANIUM_FUEL_ROD.get())
				.add(FTBICItems.DUAL_URANIUM_FUEL_ROD.get())
				.add(FTBICItems.QUAD_URANIUM_FUEL_ROD.get())
				.add(FTBICItems.HEAT_VENT.get())
				.add(FTBICItems.ADVANCED_HEAT_VENT.get())
				.add(FTBICItems.REACTOR_HEAT_VENT.get())
				.add(FTBICItems.COMPONENT_HEAT_VENT.get())
				.add(FTBICItems.OVERCLOCKED_HEAT_VENT.get())
				.add(FTBICItems.HEAT_EXCHANGER.get())
				.add(FTBICItems.ADVANCED_HEAT_EXCHANGER.get())
				.add(FTBICItems.REACTOR_HEAT_EXCHANGER.get())
				.add(FTBICItems.COMPONENT_HEAT_EXCHANGER.get())
				.add(FTBICItems.SMALL_COOLANT_CELL.get())
				.add(FTBICItems.MEDIUM_COOLANT_CELL.get())
				.add(FTBICItems.LARGE_COOLANT_CELL.get())
				.add(FTBICItems.NEUTRON_REFLECTOR.get())
				.add(FTBICItems.THICK_NEUTRON_REFLECTOR.get())
				.add(FTBICItems.IRIDIUM_NEUTRON_REFLECTOR.get())
				.add(FTBICItems.REACTOR_PLATING.get())
				.add(FTBICItems.CONTAINMENT_REACTOR_PLATING.get())
				.add(FTBICItems.HEAT_CAPACITY_REACTOR_PLATING.get());

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
}
