package dev.ftb.mods.ftbic.util;

/**
 * Tier label for {@link dev.ftb.mods.ftbic.item.EnergyArmorItem} / {@code DummyEnergyArmorItem}.
 * The vanilla {@code ArmorMaterial} chosen at registration in {@link dev.ftb.mods.ftbic.item.FTBICItems}
 * (DIAMOND for Carbon, NETHERITE for Quantum) governs base defense + durability; this enum is
 * consulted for the energy capacity + tier-specific bonus damage absorption.
 */
public enum EnergyArmorMaterial {
	CARBON("carbon"),
	QUANTUM("quantum");

	public final String name;

	EnergyArmorMaterial(String n) {
		name = n;
	}
}
