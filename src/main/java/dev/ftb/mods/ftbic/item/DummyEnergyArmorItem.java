package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.util.EnergyArmorMaterial;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;

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
