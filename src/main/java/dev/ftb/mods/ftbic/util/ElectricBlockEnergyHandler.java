package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class ElectricBlockEnergyHandler extends SnapshotJournal<Double> implements EnergyHandler {
    private final ElectricBlockEntity be;
    private final boolean canInsert;
    private final boolean canExtract;

    public ElectricBlockEnergyHandler(ElectricBlockEntity be) {
        this(be, true, true);
    }

    public ElectricBlockEnergyHandler(ElectricBlockEntity be, boolean canInsert, boolean canExtract) {
        this.be = be;
        this.canInsert = canInsert;
        this.canExtract = canExtract;
    }

    @Override
    public long getAmountAsLong() {
        double fe = be.getEnergy() * ZapFEConversion.rate();
        return fe >= (double) Long.MAX_VALUE ? Long.MAX_VALUE : (long) fe;
    }

    @Override
    public long getCapacityAsLong() {
        double fe = be.getEnergyCapacity() * ZapFEConversion.rate();
        return fe >= (double) Long.MAX_VALUE ? Long.MAX_VALUE : (long) fe;
    }

    @Override
    public int insert(int amount, TransactionContext transaction) {
        if (!canInsert || amount <= 0 || be.isBurnt()) return 0;
        double room = Math.min(be.getMaxInputEnergy(), be.getEnergyCapacity() - be.getEnergy());
        if (room <= 0D) return 0;
        int accepted = ZapFEConversion.feToZaps(amount) <= room ? amount : ZapFEConversion.zapsToFEFloor(room);
        if (accepted <= 0) return 0;
        updateSnapshots(transaction);
        be.setEnergyRaw(be.getEnergy() + ZapFEConversion.feToZaps(accepted));
        return accepted;
    }

    @Override
    public int extract(int amount, TransactionContext transaction) {
        if (!canExtract || amount <= 0 || be.getEnergy() <= 0D) return 0;
        double available = Math.min(be.getMaxOutputEnergy(), be.getEnergy());
        if (available <= 0D) return 0;
        int extracted =
                ZapFEConversion.feToZaps(amount) <= available ? amount : ZapFEConversion.zapsToFEFloor(available);
        if (extracted <= 0) return 0;
        updateSnapshots(transaction);
        be.setEnergyRaw(be.getEnergy() - ZapFEConversion.feToZaps(extracted));
        return extracted;
    }

    @Override
    protected Double createSnapshot() {
        return be.getEnergy();
    }

    @Override
    protected void revertToSnapshot(Double snap) {
        be.setEnergyRaw(snap);
    }

    @Override
    protected void onRootCommit(Double originalState) {
        be.setChanged();
    }
}
