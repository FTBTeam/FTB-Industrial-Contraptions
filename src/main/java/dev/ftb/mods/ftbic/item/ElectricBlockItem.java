package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.registry.ModDataComponents;
import dev.ftb.mods.ftbic.util.EnergyDisplay;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

public class ElectricBlockItem extends BlockItem {
    public final ElectricBlockInstance instance;

    public ElectricBlockItem(ElectricBlockInstance instance, Properties props) {
        super(instance.block.get(), props);
        this.instance = instance;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            TooltipDisplay display,
            Consumer<Component> tooltip,
            TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltip, flag);

        double maxIn = instance.maxEnergyInput.get();
        if (maxIn > 0D) {
            tooltip.accept(Component.translatable("ftbic.max_input", EnergyDisplay.compactPerTick(maxIn))
                    .withStyle(ChatFormatting.GRAY));
        }

        double maxOut = instance.maxEnergyOutput.get();
        if (maxOut > 0D) {
            tooltip.accept(Component.translatable("ftbic.energy_output", EnergyDisplay.compactPerTick(maxOut))
                    .withStyle(ChatFormatting.GRAY));
        }

        double cap = instance.energyCapacity.get();
        if (cap > 0D) {
            tooltip.accept(Component.translatable("ftbic.energy_capacity", EnergyDisplay.compactAmount(cap))
                    .withStyle(ChatFormatting.GRAY));
        }

        Double stored = stack.get(ModDataComponents.ENERGY.get());
        if (stored != null) {
            tooltip.accept(Component.translatable("ftbic.energy_stored", EnergyDisplay.compactAmount(stored))
                    .withStyle(ChatFormatting.GRAY));
        }

        double use = instance.energyUsage.get();
        if (use > 0D) {
            tooltip.accept(Component.translatable("ftbic.energy_usage", EnergyDisplay.compactPerTick(use))
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
