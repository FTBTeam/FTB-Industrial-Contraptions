package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.item.ElectricItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.InfiniteEnergyHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class EnergyItemFEHandler implements EnergyHandler {
    private final ItemAccess access;
    private final EnergyItemHandler item;
    private final Item validItem;

    public EnergyItemFEHandler(ItemAccess access, EnergyItemHandler item) {
        this.access = access;
        this.item = item;
        this.validItem = access.getResource().getItem();
    }

    public static EnergyHandler create(ItemAccess access, EnergyItemHandler item) {
        return item.isCreativeEnergyItem() ? InfiniteEnergyHandler.INSTANCE : new EnergyItemFEHandler(access, item);
    }

    private ItemStack peekStack() {
        ItemResource resource = access.getResource();
        return resource.isEmpty() || !resource.is(validItem) ? ItemStack.EMPTY : resource.toStack(1);
    }

    private int transferLimit() {
        if (!(item instanceof ElectricItem electric)) {
            return Integer.MAX_VALUE;
        }
        return ZapFEConversion.zapsToFEFloor(
                electric.tier.transferRate() * FTBICConfig.MACHINES.ITEM_TRANSFER_EFFICIENCY.get());
    }

    private static long toFE(double zaps) {
        double fe = zaps * ZapFEConversion.rate();
        if (fe <= 0D) return 0L;
        return fe >= (double) Long.MAX_VALUE ? Long.MAX_VALUE : (long) fe;
    }

    @Override
    public long getAmountAsLong() {
        ItemStack stack = peekStack();
        return stack.isEmpty() ? 0L : toFE(item.getEnergy(stack) * access.getAmount());
    }

    @Override
    public long getCapacityAsLong() {
        ItemStack stack = peekStack();
        return stack.isEmpty() ? 0L : toFE(item.getEnergyCapacity(stack) * access.getAmount());
    }

    @Override
    public int insert(int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonNegative(amount);
        int count = access.getAmount();
        if (count == 0 || !item.canInsertEnergy()) return 0;
        ItemStack stack = peekStack();
        if (stack.isEmpty()) return 0;

        double energy = item.getEnergy(stack);
        int perItem = Math.min(
                Math.min(amount / count, transferLimit()),
                ZapFEConversion.zapsToFEFloor(item.getEnergyCapacity(stack) - energy));
        if (perItem <= 0) return 0;

        if (item.insertEnergy(stack, ZapFEConversion.feToZaps(perItem), false) <= 0D) return 0;
        return perItem * access.exchange(ItemResource.of(stack), count, transaction);
    }

    @Override
    public int extract(int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonNegative(amount);
        int count = access.getAmount();
        if (count == 0 || !item.canExtractEnergy()) return 0;
        ItemResource resource = access.getResource();
        ItemStack stack = peekStack();
        if (stack.isEmpty()) return 0;

        double energy = item.getEnergy(stack);
        int perItem = Math.min(Math.min(amount / count, transferLimit()), ZapFEConversion.zapsToFEFloor(energy));
        if (perItem <= 0) return 0;

        double zaps = ZapFEConversion.feToZaps(perItem);
        if (ZapFEConversion.zapsToFEFloor(energy - zaps) <= 0) {
            zaps = energy;
        }
        if (item.extractEnergy(stack, zaps, false) <= 0D) return 0;

        if (stack.isEmpty()) {
            return perItem * access.extract(resource, count, transaction);
        }
        return perItem * access.exchange(ItemResource.of(stack), count, transaction);
    }
}
