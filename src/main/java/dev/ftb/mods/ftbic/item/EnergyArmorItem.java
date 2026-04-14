package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.util.EnergyArmorMaterial;
import dev.ftb.mods.ftbic.util.EnergyItemHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

/**
 * Energised chestplate. Item-side properties are configured via {@code Item.Properties.humanoidArmor(...)}
 * in {@link FTBICItems} (DIAMOND base for Carbon, NETHERITE for Quantum); per-tick damage absorption
 * + zap drain happens in {@link dev.ftb.mods.ftbic.events.EnergyArmorDamageHandler}.
 */
public class EnergyArmorItem extends Item implements EnergyItemHandler {
	public final EnergyArmorMaterial material;

	public EnergyArmorItem(Properties props, EnergyArmorMaterial material) {
		super(props.stacksTo(1));
		this.material = material;
	}

	public EnergyArmorMaterial getMaterial() {
		return material;
	}

	public void damageEnergyItem(ItemStack stack, double amount) {
		setEnergy(stack, Math.max(0D, getEnergy(stack) - amount));
	}

	@Override
	public double getEnergyCapacity(ItemStack stack) {
		return switch (material) {
			case CARBON -> FTBICConfig.EQUIPMENT.CARBON_ARMOR_CAPACITY.get();
			case QUANTUM -> FTBICConfig.EQUIPMENT.QUANTUM_ARMOR_CAPACITY.get();
		};
	}

	@Override
	public boolean canExtractEnergy() {
		return true;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
			Consumer<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, context, display, tooltip, flag);
		double energy = getEnergy(stack);
		double cap = getEnergyCapacity(stack);
		tooltip.accept(Component.translatable("item.ftbic.tooltip.energy",
						EnergyItemHandler.formatEnergy(energy), EnergyItemHandler.formatEnergy(cap))
				.withStyle(ChatFormatting.GRAY));
	}
}
