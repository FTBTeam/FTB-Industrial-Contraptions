package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.item.FluidCellItem;
import dev.ftb.mods.ftbic.registry.ModDataComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class FluidCellHandler implements ResourceHandler<FluidResource> {
	private final ItemAccess access;

	public FluidCellHandler(ItemAccess access) {
		this.access = access;
	}

	private ItemStack peekStack() {
		ItemResource res = access.getResource();
		return res.isEmpty() ? ItemStack.EMPTY : res.toStack(1);
	}

	private FluidStack currentStored() {
		ItemStack stack = peekStack();
		if (stack.isEmpty()) return FluidStack.EMPTY;
		SimpleFluidContent fs = stack.get(ModDataComponents.FLUID_CELL_CONTENT.get());
		return fs == null ? FluidStack.EMPTY : fs.copy();
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public FluidResource getResource(int index) {
		FluidStack stored = currentStored();
		return stored.isEmpty() ? FluidResource.EMPTY : FluidResource.of(stored.getFluid());
	}

	@Override
	public long getAmountAsLong(int index) {
		return currentStored().getAmount();
	}

	@Override
	public long getCapacityAsLong(int index, FluidResource resource) {
		return FluidCellItem.capacity();
	}

	@Override
	public boolean isValid(int index, FluidResource resource) {
		FluidStack stored = currentStored();
		return stored.isEmpty() || resource.isEmpty() || resource.getFluid() == stored.getFluid();
	}

	@Override
	public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
		if (index != 0 || resource.isEmpty() || amount <= 0) return 0;
		ItemResource held = access.getResource();
		if (held.isEmpty()) return 0;
		FluidStack stored = currentStored();
		int cap = FluidCellItem.capacity();
		if (!stored.isEmpty() && resource.getFluid() != stored.getFluid()) return 0;
		int current = stored.getAmount();
		int room = cap - current;
		int inserted = Math.min(amount, room);
		if (inserted <= 0) return 0;

		ItemStack newStack = held.toStack(1);
		FluidStack newFluid = new FluidStack(resource.getFluid(), current + inserted);
		FluidCellItem.setStored(newStack, newFluid);
		ItemResource newRes = ItemResource.of(newStack);

		int exchanged = access.exchange(newRes, 1, transaction);
		return exchanged > 0 ? inserted : 0;
	}

	@Override
	public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
		if (index != 0 || resource.isEmpty() || amount <= 0) return 0;
		ItemResource held = access.getResource();
		if (held.isEmpty()) return 0;
		FluidStack stored = currentStored();
		if (stored.isEmpty() || resource.getFluid() != stored.getFluid()) return 0;
		int extracted = Math.min(amount, stored.getAmount());
		if (extracted <= 0) return 0;

		ItemStack newStack = held.toStack(1);
		int remaining = stored.getAmount() - extracted;
		if (remaining > 0) {
			FluidCellItem.setStored(newStack, new FluidStack(stored.getFluid(), remaining));
		} else {
			FluidCellItem.setStored(newStack, FluidStack.EMPTY);
		}
		ItemResource newRes = ItemResource.of(newStack);

		int exchanged = access.exchange(newRes, 1, transaction);
		return exchanged > 0 ? extracted : 0;
	}
}
