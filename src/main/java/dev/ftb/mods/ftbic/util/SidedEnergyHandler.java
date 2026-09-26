package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.GeneratorBlockEntity;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

public record SidedEnergyHandler(
        ElectricBlockEntity machine, @Nullable Direction side, EnergyHandler delegate) implements EnergyHandler {
    @Override
    public long getAmountAsLong() {
        return delegate.getAmountAsLong();
    }

    @Override
    public long getCapacityAsLong() {
        return delegate.getCapacityAsLong();
    }

    @Override
    public int insert(int amount, TransactionContext tx) {
        if (side != null && delegate instanceof ElectricBlockEnergyHandler && !machine.isValidEnergyInputSide(side))
            return 0;
        return machine.allowsTransfer(SideConfiguration.Resource.ENERGY, side, true) ? delegate.insert(amount, tx) : 0;
    }

    @Override
    public int extract(int amount, TransactionContext tx) {
        if (side != null
                && machine instanceof GeneratorBlockEntity generator
                && !generator.isValidEnergyOutputSide(side)) return 0;
        return machine.allowsTransfer(SideConfiguration.Resource.ENERGY, side, false)
                ? delegate.extract(amount, tx)
                : 0;
    }
}
