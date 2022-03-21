package dev.ftb.mods.ftbic.util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;

public class CraftingMaterial {
	public final String id;
	public final ItemTagKeyWrapper ore;
	public final ItemTagKeyWrapper ingot;
	public final ItemTagKeyWrapper gem;
	public final ItemTagKeyWrapper dust;
	public final ItemTagKeyWrapper plate;
	public final ItemTagKeyWrapper gear;
	public final ItemTagKeyWrapper rod;

	public CraftingMaterial(String s) {
		id = s;
		ore = ItemTagKeyWrapper.create(new ResourceLocation("forge", "ores/" + id));
		ingot = ItemTagKeyWrapper.create(new ResourceLocation("forge", "ingots/" + id));
		gem = ItemTagKeyWrapper.create(new ResourceLocation("forge", "gems/" + id));
		dust = ItemTagKeyWrapper.create(new ResourceLocation("forge", "dusts/" + id));
		plate = ItemTagKeyWrapper.create(new ResourceLocation("forge", "plates/" + id));
		gear = ItemTagKeyWrapper.create(new ResourceLocation("forge", "gears/" + id));
		rod = ItemTagKeyWrapper.create(new ResourceLocation("forge", "rods/" + id));
	}
//
//	public boolean isGem() {
//		return !gem.getValues().isEmpty();
//	}
}
