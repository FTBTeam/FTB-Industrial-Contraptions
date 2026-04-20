package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.block.entity.machine.TeleporterBlockEntity;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class TeleporterFluidPassthroughHandler implements ResourceHandler<FluidResource> {
	public static final int SEND_TANK = 0;
	public static final int RECEIVE_TANK = 1;

	private final TeleporterBlockEntity teleporter;

	public TeleporterFluidPassthroughHandler(TeleporterBlockEntity teleporter) {
		this.teleporter = teleporter;
	}

	@Override
	public int size() {
		return 2;
	}

	@Override
	public FluidResource getResource(int index) {
		if (index == SEND_TANK) {
			return teleporter.sendFluid == Fluids.EMPTY || teleporter.sendFluidAmount <= 0
					? FluidResource.EMPTY : FluidResource.of(teleporter.sendFluid);
		}
		if (index == RECEIVE_TANK) {
			return teleporter.receiveFluid == Fluids.EMPTY || teleporter.receiveFluidAmount <= 0
					? FluidResource.EMPTY : FluidResource.of(teleporter.receiveFluid);
		}
		return FluidResource.EMPTY;
	}

	@Override
	public long getAmountAsLong(int index) {
		if (index == SEND_TANK) return teleporter.sendFluidAmount;
		if (index == RECEIVE_TANK) return teleporter.receiveFluidAmount;
		return 0;
	}

	@Override
	public long getCapacityAsLong(int index, FluidResource resource) {
		return index == SEND_TANK || index == RECEIVE_TANK ? TeleporterBlockEntity.TANK_CAPACITY : 0;
	}

	@Override
	public boolean isValid(int index, FluidResource resource) {
		return index == SEND_TANK;
	}

	@Override
	public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
		if (index != SEND_TANK) return 0;
		return teleporter.tryInsertFluid(resource, amount, transaction);
	}

	@Override
	public int insert(FluidResource resource, int amount, TransactionContext transaction) {
		return teleporter.tryInsertFluid(resource, amount, transaction);
	}

	@Override
	public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
		if (index != RECEIVE_TANK) return 0;
		return teleporter.tryExtractFluid(resource, amount, transaction);
	}

	@Override
	public int extract(FluidResource resource, int amount, TransactionContext transaction) {
		return teleporter.tryExtractFluid(resource, amount, transaction);
	}
}
