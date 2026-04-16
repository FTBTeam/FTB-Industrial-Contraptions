package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.block.entity.machine.PumpBlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class PumpTankHandler extends SnapshotJournal<PumpTankHandler.Snapshot> implements ResourceHandler<FluidResource> {
	public record Snapshot(Fluid fluid, int amount) {}

	private final PumpBlockEntity be;

	public PumpTankHandler(PumpBlockEntity be) {
		this.be = be;
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public FluidResource getResource(int index) {
		return be.fluidAmount > 0 && be.storedFluid != Fluids.EMPTY ? FluidResource.of(be.storedFluid) : FluidResource.EMPTY;
	}

	@Override
	public long getAmountAsLong(int index) {
		return be.fluidAmount;
	}

	@Override
	public long getCapacityAsLong(int index, FluidResource resource) {
		return be.getTankCapacity();
	}

	@Override
	public boolean isValid(int index, FluidResource resource) {
		// The pump itself decides what goes in; external callers can't insert arbitrary fluids.
		return resource.isEmpty() || resource.getFluid() == be.storedFluid || be.storedFluid == Fluids.EMPTY;
	}

	@Override
	public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
		// Read-only from foreign callers.
		return 0;
	}

	@Override
	public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
		if (index != 0 || resource.isEmpty() || amount <= 0) return 0;
		if (be.storedFluid == Fluids.EMPTY || resource.getFluid() != be.storedFluid) return 0;
		int extracted = Math.min(amount, be.fluidAmount);
		if (extracted <= 0) return 0;
		updateSnapshots(transaction);
		be.fluidAmount -= extracted;
		if (be.fluidAmount == 0) be.storedFluid = Fluids.EMPTY;
		return extracted;
	}

	@Override
	protected Snapshot createSnapshot() {
		return new Snapshot(be.storedFluid, be.fluidAmount);
	}

	@Override
	protected void revertToSnapshot(Snapshot snap) {
		be.storedFluid = snap.fluid;
		be.fluidAmount = snap.amount;
	}

	@Override
	protected void onRootCommit(Snapshot originalState) {
		be.setChanged();
	}
}
