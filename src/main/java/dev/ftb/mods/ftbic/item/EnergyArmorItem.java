package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.util.EnergyArmorMaterial;
import net.minecraft.world.item.ItemStack;

public class EnergyArmorItem extends EnergyItem {
	public final EnergyArmorMaterial material;

	public EnergyArmorItem(Properties props, EnergyArmorMaterial material) {
		super(props.stacksTo(1));
		this.material = material;
	}

	public EnergyArmorMaterial getMaterial() {
		return material;
	}

	@Override
	public double getEnergyCapacity(ItemStack stack) {
		return switch (material) {
			case CARBON -> FTBICConfig.EQUIPMENT.CARBON_ARMOR_CAPACITY.get();
			case QUANTUM -> FTBICConfig.EQUIPMENT.QUANTUM_ARMOR_CAPACITY.get();
		};
	}
}
