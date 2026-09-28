package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.block.entity.machine.BatchFeederBlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.DelegatingResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/** Automation can fill the buffer; only a player or a complete batch may drain it. */
public class BatchFeederTankHandler extends SnapshotJournal<FluidStack> implements ResourceHandler<FluidResource> {
    private final BatchFeederBlockEntity feeder;
    public final ResourceHandler<FluidResource> manualAccess = new DelegatingResourceHandler<>(this) {
        @Override
        public int extract(int index, FluidResource resource, int amount, TransactionContext tx) {
            return index == 0 ? drainBuffer(resource, amount, tx) : 0;
        }

        @Override
        public int extract(FluidResource resource, int amount, TransactionContext tx) {
            return drainBuffer(resource, amount, tx);
        }
    };

    public BatchFeederTankHandler(BatchFeederBlockEntity feeder) {
        this.feeder = feeder;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public FluidResource getResource(int index) {
        return FluidResource.of(feeder.getBufferFluid());
    }

    @Override
    public long getAmountAsLong(int index) {
        return feeder.getBufferFluid().getAmount();
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        return index == 0 ? BatchFeederBlockEntity.TANK_CAPACITY : 0;
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        return index == 0;
    }

    @Override
    public int insert(int index, FluidResource resource, int amount, TransactionContext tx) {
        if (index != 0 || resource.isEmpty() || amount <= 0) return 0;
        FluidStack stored = feeder.getBufferFluid();
        if (!stored.isEmpty() && !FluidResource.of(stored).equals(resource)) return 0;
        int inserted = Math.min(amount, BatchFeederBlockEntity.TANK_CAPACITY - stored.getAmount());
        if (inserted <= 0) return 0;
        updateSnapshots(tx);
        feeder.setBufferFluid(resource.toStack(stored.getAmount() + inserted));
        return inserted;
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext tx) {
        return 0;
    }

    private int drainBuffer(FluidResource resource, int amount, TransactionContext tx) {
        FluidStack stored = feeder.getBufferFluid();
        if (resource.isEmpty() || amount <= 0 || !FluidResource.of(stored).equals(resource)) return 0;
        int extracted = Math.min(amount, stored.getAmount());
        if (extracted <= 0) return 0;
        updateSnapshots(tx);
        stored.shrink(extracted);
        feeder.setBufferFluid(stored);
        return extracted;
    }

    @Override
    protected FluidStack createSnapshot() {
        return feeder.getBufferFluid();
    }

    @Override
    protected void revertToSnapshot(FluidStack snapshot) {
        feeder.setBufferFluid(snapshot);
    }

    @Override
    protected void onRootCommit(FluidStack original) {
        feeder.fluidsChanged();
    }
}
