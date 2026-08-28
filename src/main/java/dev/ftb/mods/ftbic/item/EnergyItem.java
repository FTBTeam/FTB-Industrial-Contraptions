package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.registry.ModDataComponents;
import dev.ftb.mods.ftbic.util.EnergyItemHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public abstract class EnergyItem extends Item implements EnergyItemHandler {

	public EnergyItem(Properties props) {
		super(props);
	}

	public void damageEnergyItem(ItemStack stack, double amount) {
		setEnergy(stack, Math.max(0D, getEnergy(stack) - amount));
	}

	@Override
	public abstract double getEnergyCapacity(ItemStack stack);

	@Override
	public boolean canExtractEnergy() {
		return true;
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return stack.has(ModDataComponents.ENERGY.get());
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return (int) Math.round(Mth.clamp((getEnergy(stack) / getEnergyCapacity(stack)) * 13D, 0D, 13D));
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return 0xFFFF0000;
	}

	@Override
	@SuppressWarnings("deprecation")
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
