package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.block.entity.storage.EnergyRectifierBlockEntity;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/**
 * FE-side handler for an {@link EnergyRectifierBlockEntity}. Insert-only; pushes FE into the
 * rectifier's small FE buffer where the BE's tick will convert it into internal zaps.
 */
public class EnergyRectifierFEHandler extends SnapshotJournal<Long> implements EnergyHandler {
	private final EnergyRectifierBlockEntity be;

	public EnergyRectifierFEHandler(EnergyRectifierBlockEntity be) {
		this.be = be;
	}

	@Override
	public long getAmountAsLong() {
		return be.feBuffer;
	}

	@Override
	public long getCapacityAsLong() {
		return be.getFeBufferCapacity();
	}

	@Override
	public int insert(int amount, TransactionContext transaction) {
		if (amount <= 0 || be.isBurnt()) return 0;
		long accepted = be.insertFE(amount, true);
		if (accepted <= 0L) return 0;
		updateSnapshots(transaction);
		be.feBuffer += accepted;
		return (int) accepted;
	}

	@Override
	public int extract(int amount, TransactionContext transaction) {
		return 0;
	}

	@Override
	protected Long createSnapshot() {
		return be.feBuffer;
	}

	@Override
	protected void revertToSnapshot(Long snap) {
		be.feBuffer = snap;
	}

	@Override
	protected void onRootCommit(Long originalState) {
		be.setChanged();
	}
}
