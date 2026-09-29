package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

public final class BatterySlotHelper {
    private BatterySlotHelper() {}

    public static double drainBatteryToBuffer(
            ElectricBlockEntity be, ItemStack battery, double maxInputTransfer, double efficiency) {
        if (battery.isEmpty()) return 0D;
        if (!(battery.getItem() instanceof EnergyItemHandler item)) {
            EnergyHandler foreign = foreignEnergyHandler(battery);
            return foreign == null ? 0D : drainHandlerToBuffer(be, foreign, maxInputTransfer * efficiency);
        }
        if (be.energy >= be.energyCapacity) return 0D;

        double transfer = item.isCreativeEnergyItem() ? Double.POSITIVE_INFINITY : maxInputTransfer * efficiency;
        double drained = item.extractEnergy(battery, Math.min(be.energyCapacity - be.energy, transfer), false);
        if (drained > 0D) {
            be.energy += drained;
            be.setChanged();
        }
        return drained;
    }

    public static double drainHandlerToBuffer(ElectricBlockEntity be, EnergyHandler handler, double maxTransfer) {
        if (be.energy >= be.energyCapacity) return 0D;
        double drained = drainFE(handler, Math.min(be.energyCapacity - be.energy, maxTransfer));
        if (drained > 0D) {
            be.energy += drained;
            be.setChanged();
        }
        return drained;
    }

    @Nullable
    public static EnergyHandler foreignEnergyHandler(ItemStack stack) {
        if (stack.isEmpty() || stack.getItem() instanceof EnergyItemHandler || !FTBICConfig.ENERGY.FULL_FE_MODE.get()) {
            return null;
        }
        return ItemAccess.forStack(stack).getCapability(Capabilities.Energy.ITEM);
    }

    public static boolean needsCharge(ItemStack stack) {
        if (stack.getItem() instanceof EnergyItemHandler item) {
            return !item.isCreativeEnergyItem()
                    && item.canInsertEnergy()
                    && item.getEnergy(stack) < item.getEnergyCapacity(stack);
        }
        EnergyHandler handler = foreignEnergyHandler(stack);
        return handler != null && handler.getAmountAsLong() < handler.getCapacityAsLong();
    }

    public static boolean isForeignEnergyItem(ItemStack stack) {
        return foreignEnergyHandler(stack) != null;
    }

    public static double chargeForeignItem(ItemStack stack, double maxZaps) {
        EnergyHandler handler = foreignEnergyHandler(stack);
        return handler == null ? 0D : chargeFE(handler, maxZaps);
    }

    public static double drainFE(EnergyHandler handler, double maxZaps) {
        int request = ZapFEConversion.zapsToFEFloor(maxZaps);
        if (request <= 0) return 0D;
        try (Transaction tx = Transaction.openRoot()) {
            int extracted = handler.extract(request, tx);
            if (extracted <= 0) return 0D;
            tx.commit();
            return Math.min(ZapFEConversion.feToZaps(extracted), maxZaps);
        }
    }

    public static double chargeFE(EnergyHandler handler, double maxZaps) {
        int offer = ZapFEConversion.zapsToFEFloor(maxZaps);
        if (offer <= 0) return 0D;
        try (Transaction tx = Transaction.openRoot()) {
            int accepted = handler.insert(offer, tx);
            if (accepted <= 0) return 0D;
            tx.commit();
            return Math.min(ZapFEConversion.feToZaps(accepted), maxZaps);
        }
    }
}
