package dev.ftb.mods.ftbic.util;

import net.minecraft.resources.Identifier;

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
		ore = ItemTagKeyWrapper.create(Identifier.fromNamespaceAndPath("c", "ores/" + id));
		ingot = ItemTagKeyWrapper.create(Identifier.fromNamespaceAndPath("c", "ingots/" + id));
		gem = ItemTagKeyWrapper.create(Identifier.fromNamespaceAndPath("c", "gems/" + id));
		dust = ItemTagKeyWrapper.create(Identifier.fromNamespaceAndPath("c", "dusts/" + id));
		plate = ItemTagKeyWrapper.create(Identifier.fromNamespaceAndPath("c", "plates/" + id));
		gear = ItemTagKeyWrapper.create(Identifier.fromNamespaceAndPath("c", "gears/" + id));
		rod = ItemTagKeyWrapper.create(Identifier.fromNamespaceAndPath("c", "rods/" + id));
	}
}
