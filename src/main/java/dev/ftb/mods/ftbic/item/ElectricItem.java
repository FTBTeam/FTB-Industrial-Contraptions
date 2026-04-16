package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.registry.ModDataComponents;
import dev.ftb.mods.ftbic.util.EnergyItemHandler;
import dev.ftb.mods.ftbic.util.EnergyTier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class ElectricItem extends Item implements EnergyItemHandler {
	public final EnergyTier tier;
	public final double capacity;

	public ElectricItem(Properties props, EnergyTier tier, double capacity) {
		super(props.stacksTo(1));
		this.tier = tier;
		this.capacity = capacity;
	}

	@Override
	public double getEnergyCapacity(ItemStack stack) {
		return capacity;
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return !isCreativeEnergyItem() && stack.has(ModDataComponents.ENERGY.get());
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		double cap = getEnergyCapacity(stack);
		if (cap <= 0D) return 0;
		return (int) Math.round(Mth.clamp((getEnergy(stack) / cap) * 13D, 0D, 13D));
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return 0xFFEE3030;
	}

	@Override
	@SuppressWarnings("deprecation")
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
			Consumer<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, context, display, tooltip, flag);
		if (isCreativeEnergyItem()) {
			tooltip.accept(Component.translatable("item.ftbic.tooltip.creative_energy")
					.withStyle(ChatFormatting.LIGHT_PURPLE));
		} else {
			double energy = getEnergy(stack);
			double cap = getEnergyCapacity(stack);
			tooltip.accept(Component.translatable("item.ftbic.tooltip.energy",
							EnergyItemHandler.formatEnergy(energy), EnergyItemHandler.formatEnergy(cap))
					.withStyle(ChatFormatting.GRAY));
			tooltip.accept(Component.translatable("item.ftbic.tooltip.tier", tier.name)
					.withStyle(ChatFormatting.DARK_GRAY));
		}
	}
}
