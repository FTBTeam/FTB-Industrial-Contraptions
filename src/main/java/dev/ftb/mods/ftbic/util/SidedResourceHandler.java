package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

/** Keeps the underlying slot/tank rules and transactions, checking live side settings on every transfer. */
public record SidedResourceHandler<T extends Resource>(
        ElectricBlockEntity machine,
        @Nullable Direction side,
        SideConfiguration.Resource type,
        ResourceHandler<T> delegate)
        implements ResourceHandler<T> {
    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public T getResource(int index) {
        return delegate.getResource(index);
    }

    @Override
    public long getAmountAsLong(int index) {
        return delegate.getAmountAsLong(index);
    }

    @Override
    public long getCapacityAsLong(int index, T resource) {
        return delegate.getCapacityAsLong(index, resource);
    }

    @Override
    public boolean isValid(int index, T resource) {
        return machine.allowsTransfer(type, side, true) && delegate.isValid(index, resource);
    }

    @Override
    public int insert(int index, T resource, int amount, TransactionContext tx) {
        return machine.allowsTransfer(type, side, true) ? delegate.insert(index, resource, amount, tx) : 0;
    }

    @Override
    public int extract(int index, T resource, int amount, TransactionContext tx) {
        return machine.allowsTransfer(type, side, false) ? delegate.extract(index, resource, amount, tx) : 0;
    }

    @Override
    public int insert(T resource, int amount, TransactionContext tx) {
        return machine.allowsTransfer(type, side, true) ? delegate.insert(resource, amount, tx) : 0;
    }

    @Override
    public int extract(T resource, int amount, TransactionContext tx) {
        return machine.allowsTransfer(type, side, false) ? delegate.extract(resource, amount, tx) : 0;
    }
}
