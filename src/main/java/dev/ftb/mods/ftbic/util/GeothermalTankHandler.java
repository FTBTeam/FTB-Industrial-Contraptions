package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.entity.generator.GeothermalGeneratorBlockEntity;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class GeothermalTankHandler extends SnapshotJournal<Integer> implements ResourceHandler<FluidResource> {
	private final GeothermalGeneratorBlockEntity be;

	public GeothermalTankHandler(GeothermalGeneratorBlockEntity be) {
		this.be = be;
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public FluidResource getResource(int index) {
		return be.fluidAmount > 0 ? FluidResource.of(Fluids.LAVA) : FluidResource.EMPTY;
	}

	@Override
	public long getAmountAsLong(int index) {
		return be.fluidAmount;
	}

	@Override
	public long getCapacityAsLong(int index, FluidResource resource) {
		return FTBICConfig.MACHINES.GEOTHERMAL_GENERATOR_TANK_SIZE.get();
	}

	@Override
	public boolean isValid(int index, FluidResource resource) {
		return resource.isEmpty() || resource.getFluid() == Fluids.LAVA;
	}

	@Override
	public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
		if (index != 0 || resource.isEmpty() || resource.getFluid() != Fluids.LAVA || amount <= 0) return 0;
		int cap = FTBICConfig.MACHINES.GEOTHERMAL_GENERATOR_TANK_SIZE.get();
		int room = cap - be.fluidAmount;
		int inserted = Math.min(amount, room);
		if (inserted <= 0) return 0;
		updateSnapshots(transaction);
		be.fluidAmount += inserted;
		return inserted;
	}

	@Override
	public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
		return 0;
	}

	@Override
	protected Integer createSnapshot() {
		return be.fluidAmount;
	}

	@Override
	protected void revertToSnapshot(Integer snap) {
		be.fluidAmount = snap;
	}

	@Override
	protected void onRootCommit(Integer originalState) {
		be.setChanged();
	}
}
