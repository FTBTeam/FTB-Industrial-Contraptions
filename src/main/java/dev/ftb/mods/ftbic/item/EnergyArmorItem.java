package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.registry.ModDataComponents;
import dev.ftb.mods.ftbic.util.EnergyArmorMaterial;
import dev.ftb.mods.ftbic.util.EnergyDisplay;
import dev.ftb.mods.ftbic.util.EnergyItemHandler;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

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
    public boolean isBarVisible(ItemStack stack) {
        return stack.has(ModDataComponents.ENERGY.get());
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        double capacity = getEnergyCapacity(stack);
        if (capacity <= 0D) return 0;
        return (int) Math.round(Mth.clamp(getEnergy(stack) / capacity * 13D, 0D, 13D));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0xFFEE3030;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || !oldStack.is(newStack.getItem());
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
        double energy = getEnergy(stack);
        double cap = getEnergyCapacity(stack);
        tooltip.accept(Component.translatable(
                        "item.ftbic.tooltip.energy",
                        EnergyDisplay.compactNumber(energy),
                        EnergyDisplay.compactAmount(cap))
                .withStyle(ChatFormatting.GRAY));
    }
}
