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
		double maxInput = be.getMaxInputEnergy();
		if (maxInput <= 0D) return 0;
		double asZaps = ZapFEConversion.feToZapsFloor(amount);
		if (asZaps > maxInput && be.canBurn()) {
			updateSnapshots(transaction);
			be.setBurnt(true);
			return amount;
		}
		double acceptable = Math.min(Math.min(asZaps, maxInput), be.getEnergyCapacity() - be.getEnergy());
		if (acceptable <= 0D) return 0;
		updateSnapshots(transaction);
		be.setEnergyRaw(be.getEnergy() + acceptable);
		return ZapFEConversion.zapsToFEFloor(acceptable);
	}

	@Override
	public int extract(int amount, TransactionContext transaction) {
		if (!canExtract || amount <= 0 || be.getEnergy() <= 0D) return 0;
		double maxOutput = be.getMaxOutputEnergy();
		double asZaps = ZapFEConversion.feToZapsFloor(amount);
		double extractable = Math.min(Math.min(asZaps, maxOutput), be.getEnergy());
		if (extractable <= 0D) return 0;
		updateSnapshots(transaction);
		be.setEnergyRaw(be.getEnergy() - extractable);
		return ZapFEConversion.zapsToFEFloor(extractable);
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
