package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/**
 * ResourceHandler<ItemResource> adapter exposing an `ElectricBlockEntity`'s inputItems + outputItems
 * arrays as a single handler so hoppers and pipes from other mods can interact via the new NeoForge
 * transfer API (`Capabilities.Item.BLOCK`).
 *
 * Slot layout: 0..inputs-1 = input buffer (insert-only from outside), inputs..end = output buffer
 * (extract-only). Transactions are supported via per-slot SnapshotJournal.
 */
public class ElectricBlockResourceHandler extends SnapshotJournal<ItemStack[]> implements ResourceHandler<ItemResource> {
	private final ElectricBlockEntity be;

	public ElectricBlockResourceHandler(ElectricBlockEntity be) {
		this.be = be;
	}

	private int inputs() { return be.inputItems.length; }
	private int total()  { return be.getSlotCount(); }

	private boolean isInputSlot(int slot)  { return slot < inputs(); }
	private boolean isOutputSlot(int slot) { return slot >= inputs() && slot < total(); }

	@Override
	public int size() {
		return total();
	}

	@Override
	public ItemResource getResource(int index) {
		return index < 0 || index >= total() ? ItemResource.EMPTY : ItemResource.of(be.getStackInSlot(index));
	}

	@Override
	public long getAmountAsLong(int index) {
		return index < 0 || index >= total() ? 0L : be.getStackInSlot(index).getCount();
	}

	@Override
	public long getCapacityAsLong(int index, ItemResource resource) {
		return resource.isEmpty() ? 64 : Math.min(resource.getMaxStackSize(), 64);
	}

	@Override
	public boolean isValid(int index, ItemResource resource) {
		// Outside code can insert into input slots only; extraction handles output slots.
		if (!isInputSlot(index)) return false;
		return be.isItemValid(index, resource.toStack(1));
	}

	@Override
	public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
		if (!isInputSlot(index) || resource.isEmpty() || amount <= 0) return 0;
		if (!be.isItemValid(index, resource.toStack(1))) return 0;
		ItemStack existing = be.getStackInSlot(index);
		int limit = (int) getCapacityAsLong(index, resource);
		if (existing.isEmpty()) {
			int inserted = Math.min(amount, limit);
			snapshot(transaction);
			be.setStackInSlot(index, resource.toStack(inserted));
			return inserted;
		}
		if (!resource.matches(existing)) return 0;
		int room = limit - existing.getCount();
		int inserted = Math.min(amount, room);
		if (inserted <= 0) return 0;
		snapshot(transaction);
		existing.grow(inserted);
		return inserted;
	}

	@Override
	public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
		if (index < 0 || index >= total() || resource.isEmpty() || amount <= 0) return 0;
		// Default policy: only output slots are extractable. BE subclasses (e.g. the nuclear reactor)
		// can override isSlotExtractable to open input slots to automation too.
		if (!be.isSlotExtractable(index)) return 0;
		ItemStack existing = be.getStackInSlot(index);
		if (existing.isEmpty() || !resource.matches(existing)) return 0;
		int extracted = Math.min(amount, existing.getCount());
		snapshot(transaction);
		existing.shrink(extracted);
		if (existing.getCount() <= 0) be.setStackInSlot(index, ItemStack.EMPTY);
		return extracted;
	}

	private void snapshot(TransactionContext transaction) {
		updateSnapshots(transaction);
	}

	@Override
	protected ItemStack[] createSnapshot() {
		ItemStack[] snap = new ItemStack[total()];
		for (int i = 0; i < snap.length; i++) snap[i] = be.getStackInSlot(i).copy();
		return snap;
	}

	@Override
	protected void revertToSnapshot(ItemStack[] snap) {
		for (int i = 0; i < snap.length; i++) be.setStackInSlot(i, snap[i]);
	}

	@Override
	protected void onRootCommit(ItemStack[] originalState) {
		be.setChanged();
	}
}
