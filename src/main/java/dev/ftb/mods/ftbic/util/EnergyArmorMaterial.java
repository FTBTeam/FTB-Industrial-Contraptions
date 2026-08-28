package dev.ftb.mods.ftbic.util;

import java.util.Map;

import com.google.common.collect.Maps;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

public enum EnergyArmorMaterial {
	CARBON(new ArmorMaterial(33, defense(3, 6, 8, 3, 11), 10, SoundEvents.ARMOR_EQUIP_DIAMOND, 2.0F, 0.0F, tag("repairs_carbon_armor"), asset("carbon"))),
	QUANTUM(new ArmorMaterial(37, defense(3, 6, 8, 3, 19), 15, SoundEvents.ARMOR_EQUIP_NETHERITE, 3.0F, 0.1F, tag("repairs_quantum_armor"), asset("quantum")));

	public final ArmorMaterial material;

	EnergyArmorMaterial(ArmorMaterial material) {
		this.material = material;
	}

	private static ResourceKey<EquipmentAsset> asset(String name) {
		return ResourceKey.create(EquipmentAssets.ROOT_ID, FTBIC.id(name));
	}

	private static Map<ArmorType, Integer> defense(int boots, int legs, int chest, int helm, int body) {
        return Maps.newEnumMap(Map.of(ArmorType.BOOTS, boots, ArmorType.LEGGINGS, legs, ArmorType.CHESTPLATE, chest, ArmorType.HELMET, helm, ArmorType.BODY, body));
    }

    private static TagKey<Item> tag(String name) {
	    return TagKey.create(Registries.ITEM, FTBIC.id(name));
    }
}
