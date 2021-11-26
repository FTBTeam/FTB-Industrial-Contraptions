package dev.ftb.mods.ftbic.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;

public class CraftingMaterial {
	public final String id;
	public final Tags.IOptionalNamedTag<Item> ore;
	public final Tags.IOptionalNamedTag<Item> ingot;
	public final Tags.IOptionalNamedTag<Item> gem;
	public final Tags.IOptionalNamedTag<Item> dust;
	public final Tags.IOptionalNamedTag<Item> plate;
	public final Tags.IOptionalNamedTag<Item> gear;
	public final Tags.IOptionalNamedTag<Item> rod;

	public CraftingMaterial(String s) {
		id = s;
		ore = ItemTags.createOptional(new ResourceLocation("forge:ores/" + id));
		ingot = ItemTags.createOptional(new ResourceLocation("forge:ingots/" + id));
		gem = ItemTags.createOptional(new ResourceLocation("forge:gems/" + id));
		dust = ItemTags.createOptional(new ResourceLocation("forge:dusts/" + id));
		plate = ItemTags.createOptional(new ResourceLocation("forge:plates/" + id));
		gear = ItemTags.createOptional(new ResourceLocation("forge:gears/" + id));
		rod = ItemTags.createOptional(new ResourceLocation("forge:rods/" + id));
	}

	public boolean isGem() {
		return !gem.getValues().isEmpty();
	}
}
