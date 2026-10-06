package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.registry.ModDataComponents;
import dev.ftb.mods.ftbic.util.EnergyDisplay;
import dev.ftb.mods.ftbic.util.EnergyItemHandler;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

public class MechanicalElytraItem extends Item implements EnergyItemHandler {
    public MechanicalElytraItem(Properties props) {
        super(props.stacksTo(1));
    }

    public void damageEnergyItem(ItemStack stack, double amount) {
        double energy = getEnergy(stack);
        setEnergy(stack, Math.max(0D, energy - Math.min(energy, amount)));
    }

    @Override
    public double getEnergyCapacity(ItemStack stack) {
        return FTBICConfig.EQUIPMENT.MECHANICAL_ELYTRA_CAPACITY.get();
    }

    @Override
    public boolean canExtractEnergy() {
        return true;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        if (slot != EquipmentSlot.CHEST) return;
        if (!(entity instanceof LivingEntity le)) return;

        if (le.isFallFlying()) return;

        double rechargeRate = FTBICConfig.EQUIPMENT.MECHANICAL_ELYTRA_RECHARGE.get();
        if (rechargeRate <= 0D) return;
        if (!FTBICUtils.isDaylight(level)) return;
        if (!level.canSeeSky(le.blockPosition())) return;
        insertEnergy(stack, rechargeRate, false);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || !oldStack.is(newStack.getItem());
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
