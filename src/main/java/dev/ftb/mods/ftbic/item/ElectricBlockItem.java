package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.util.EnergyItemHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

/**
 * BlockItem wrapper for electric blocks that renders per-tier energy stats on hover.
 * Shows max input / max output / capacity / energy use when each is configured.
 */
public class ElectricBlockItem extends BlockItem {
	public final ElectricBlockInstance instance;

	public ElectricBlockItem(ElectricBlockInstance instance, Properties props) {
		super(instance.block.get(), props);
		this.instance = instance;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
			Consumer<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, context, display, tooltip, flag);

		double maxIn = instance.maxEnergyInput.get();
		if (maxIn > 0D) {
			tooltip.accept(Component.translatable("ftbic.max_input", EnergyItemHandler.formatEnergy(maxIn))
					.withStyle(ChatFormatting.GRAY));
		}

		double maxOut = instance.maxEnergyOutput.get();
		if (maxOut > 0D) {
			tooltip.accept(Component.translatable("ftbic.energy_output", EnergyItemHandler.formatEnergy(maxOut))
					.withStyle(ChatFormatting.GRAY));
		}

		double cap = instance.energyCapacity.get();
		if (cap > 0D) {
			tooltip.accept(Component.translatable("ftbic.energy_capacity", EnergyItemHandler.formatEnergy(cap))
					.withStyle(ChatFormatting.GRAY));
		}

		double use = instance.energyUsage.get();
		if (use > 0D) {
			tooltip.accept(Component.translatable("ftbic.energy_usage", EnergyItemHandler.formatEnergy(use))
					.withStyle(ChatFormatting.DARK_GRAY));
		}
	}
}
