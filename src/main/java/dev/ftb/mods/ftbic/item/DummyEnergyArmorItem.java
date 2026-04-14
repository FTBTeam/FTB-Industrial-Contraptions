package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.util.EnergyArmorMaterial;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;

/**
 * Helmet/leggings/boots companion to {@link EnergyArmorItem}. Acts as a vanilla armour piece (slot
 * configured in {@link FTBICItems} via {@code Item.Properties.humanoidArmor(...)}), but doesn't
 * carry energy itself — damage absorption + zap drain run through the chestplate's
 * {@link dev.ftb.mods.ftbic.events.EnergyArmorDamageHandler}, which checks for full-set bonuses.
 */
public class DummyEnergyArmorItem extends Item {
	public final EnergyArmorMaterial material;
	public final EquipmentSlot slot;

	public DummyEnergyArmorItem(Properties props, EnergyArmorMaterial material, EquipmentSlot slot) {
		super(props.stacksTo(1));
		this.material = material;
		this.slot = slot;
	}

	public EnergyArmorMaterial getMaterial() {
		return material;
	}
}
