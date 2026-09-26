package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

public final class FTBICArmorMaterials {
    public static final ArmorMaterial CARBON = withAsset(ArmorMaterials.DIAMOND, "carbon");
    public static final ArmorMaterial QUANTUM = withAsset(ArmorMaterials.NETHERITE, "quantum");

    private static ArmorMaterial withAsset(ArmorMaterial base, String name) {
        ResourceKey<EquipmentAsset> asset =
                ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath(FTBIC.MOD_ID, name));
        return new ArmorMaterial(
                base.durability(),
                base.defense(),
                base.enchantmentValue(),
                base.equipSound(),
                base.toughness(),
                base.knockbackResistance(),
                base.repairIngredient(),
                asset);
    }

    private FTBICArmorMaterials() {}
}
