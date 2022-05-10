package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.FTBICConfig;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public class EnergyArmorMaterial implements ArmorMaterial {
	public static final EnergyArmorMaterial ELYTRA = new EnergyArmorMaterial(FTBIC.MOD_ID + ":elytra", FTBICConfig.EQUIPMENT.MECHANICAL_ELYTRA_CAPACITY.get());
	public static final EnergyArmorMaterial CARBON = new EnergyArmorMaterial(FTBIC.MOD_ID + ":carbon", FTBICConfig.EQUIPMENT.CARBON_ARMOR_CAPACITY.get());
	public static final EnergyArmorMaterial QUANTUM = new EnergyArmorMaterial(FTBIC.MOD_ID + ":quantum", FTBICConfig.EQUIPMENT.QUANTUM_ARMOR_CAPACITY.get());

	private final String name;
	public final double capacity;

	public EnergyArmorMaterial(String n, double c) {
		name = n;
		capacity = c;
	}

	@Override
	public int getDurabilityForSlot(EquipmentSlot arg) {
		return 1;
	}

	@Override
	public int getDefenseForSlot(EquipmentSlot arg) {
		return 0;
	}

	@Override
	public int getEnchantmentValue() {
		return 0;
	}

	@Override
	public SoundEvent getEquipSound() {
		return SoundEvents.ARMOR_EQUIP_LEATHER;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return Ingredient.EMPTY;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public float getToughness() {
		return 0F;
	}

	@Override
	public float getKnockbackResistance() {
		return 0F;
	}
}
