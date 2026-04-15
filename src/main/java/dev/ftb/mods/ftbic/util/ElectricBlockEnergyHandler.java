package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/**
 * Bridges an `ElectricBlockEntity`'s zap-denominated energy buffer to NeoForge's transfer-API
 * `EnergyHandler` using the config-controlled `ZAP_TO_FE_CONVERSION_RATE` (default 4 FE per zap).
 *
 * Foreign mods insert FE amounts; this handler divides by the conversion rate to add zaps and
 * snapshots the previous energy value for transaction rollback.
 */
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
		return (long) (be.getEnergy() * FTBICConfig.ENERGY.ZAP_TO_FE_CONVERSION_RATE.get());
	}

	@Override
	public long getCapacityAsLong() {
		return (long) (be.getEnergyCapacity() * FTBICConfig.ENERGY.ZAP_TO_FE_CONVERSION_RATE.get());
	}

	@Override
	public int insert(int amount, TransactionContext transaction) {
		if (!canInsert || amount <= 0 || be.isBurnt() || be.getMaxInputEnergy() <= 0D) return 0;
		double rate = FTBICConfig.ENERGY.ZAP_TO_FE_CONVERSION_RATE.get();
		double asZaps = amount / rate;
		double acceptable = Math.min(asZaps, be.getEnergyCapacity() - be.getEnergy());
		if (acceptable <= 0D) return 0;
		updateSnapshots(transaction);
		be.setEnergyRaw(be.getEnergy() + acceptable);
		// Return the FE equivalent of what we actually accepted.
		return (int) Math.round(acceptable * rate);
	}

	@Override
	public int extract(int amount, TransactionContext transaction) {
		if (!canExtract || amount <= 0 || be.getEnergy() <= 0D) return 0;
		double rate = FTBICConfig.ENERGY.ZAP_TO_FE_CONVERSION_RATE.get();
		double asZaps = amount / rate;
		double extractable = Math.min(asZaps, be.getEnergy());
		if (extractable <= 0D) return 0;
		updateSnapshots(transaction);
		be.setEnergyRaw(be.getEnergy() - extractable);
		return (int) Math.round(extractable * rate);
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
