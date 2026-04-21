package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class ElectricBlockResourceHandler extends SnapshotJournal<Integer> implements ResourceHandler<ItemResource> {
	private record SlotBackup(int snapshotId, ItemStack prev) {}

	private final ElectricBlockEntity be;
	private final ArrayDeque<SlotBackup>[] slotHistory;
	private final Map<Integer, BitSet> touchedById = new HashMap<>();
	private final Map<Integer, Integer> parentById = new HashMap<>();
	private final ArrayDeque<Integer> activeStack = new ArrayDeque<>();
	private int nextSnapshotId = 0;
	private boolean lastCallWasRevert = false;

	@SuppressWarnings("unchecked")
	public ElectricBlockResourceHandler(ElectricBlockEntity be) {
		this.be = be;
		this.slotHistory = new ArrayDeque[be.getSlotCount()];
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
		if (index < 0 || index >= total()) return 0;
		if (!isInputSlot(index)) return 0;
		return resource.isEmpty() ? 64 : Math.min(resource.getMaxStackSize(), 64);
	}

	@Override
	public boolean isValid(int index, ItemResource resource) {
		if (!isInputSlot(index)) return false;
		return be.isItemValid(index, resource.toStack(1));
	}

	@Override
	public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
		if (!isInputSlot(index) || resource.isEmpty() || amount <= 0) return 0;
		if (!be.isItemValid(index, resource.toStack(1))) return 0;
		ItemStack existing = be.getStackInSlot(index);
		int limit = (int) getCapacityAsLong(index, resource);
		int room;
		if (existing.isEmpty()) {
			room = limit;
		} else {
			if (!resource.matches(existing)) return 0;
			room = limit - existing.getCount();
		}
		if (room <= 0) return 0;
		int toInsert = Math.min(amount, room);
		recordSlot(index, transaction);
		if (existing.isEmpty()) {
			be.setStackInSlot(index, resource.toStack(toInsert));
		} else {
			ItemStack grown = existing.copy();
			grown.grow(toInsert);
			be.setStackInSlot(index, grown);
		}
		return toInsert;
	}

	@Override
	public int insert(ItemResource resource, int amount, TransactionContext transaction) {
		if (resource.isEmpty() || amount <= 0) return 0;
		int inserted = 0;
		int inputs = inputs();
		for (int i = 0; i < inputs; i++) {
			int accepted = insert(i, resource, amount - inserted, transaction);
			if (accepted > 0) {
				inserted += accepted;
				if (inserted >= amount) break;
			}
		}
		return inserted;
	}

	@Override
	public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
		if (index < 0 || index >= total() || resource.isEmpty() || amount <= 0) return 0;
		if (!be.isSlotExtractable(index)) return 0;
		ItemStack existing = be.getStackInSlot(index);
		if (existing.isEmpty() || !resource.matches(existing)) return 0;
		int extracted = Math.min(amount, existing.getCount());
		recordSlot(index, transaction);
		existing.shrink(extracted);
		if (existing.getCount() <= 0) be.setStackInSlot(index, ItemStack.EMPTY);
		return extracted;
	}

	private void recordSlot(int slot, TransactionContext transaction) {
		updateSnapshots(transaction);
		int activeId = activeStack.peekFirst();
		BitSet touched = touchedById.get(activeId);
		if (touched.get(slot)) return;
		touched.set(slot);
		ArrayDeque<SlotBackup> hist = slotHistory[slot];
		if (hist == null) {
			hist = new ArrayDeque<>();
			slotHistory[slot] = hist;
		}
		hist.addFirst(new SlotBackup(activeId, be.getStackInSlot(slot).copy()));
	}

	@Override
	protected Integer createSnapshot() {
		int id = nextSnapshotId++;
		touchedById.put(id, new BitSet(total()));
		if (!activeStack.isEmpty()) {
			parentById.put(id, activeStack.peekFirst());
		}
		activeStack.push(id);
		return id;
	}

	@Override
	protected void revertToSnapshot(Integer snapshotId) {
		lastCallWasRevert = true;
		int id = snapshotId;
		BitSet touched = touchedById.get(id);
		if (touched == null) return;
		for (int slot = touched.nextSetBit(0); slot >= 0; slot = touched.nextSetBit(slot + 1)) {
			ArrayDeque<SlotBackup> hist = slotHistory[slot];
			if (hist == null) continue;
			SlotBackup top = hist.peekFirst();
			if (top != null && top.snapshotId == id) {
				hist.pollFirst();
				be.setStackInSlot(slot, top.prev);
			}
		}
	}

	@Override
	protected void releaseSnapshot(Integer snapshotId) {
		int id = snapshotId;
		if (lastCallWasRevert) {
			lastCallWasRevert = false;
			cleanup(id);
			return;
		}
		Integer parentObj = parentById.get(id);
		if (parentObj != null) {
			int parent = parentObj;
			BitSet currentTouched = touchedById.get(id);
			BitSet parentTouched = touchedById.get(parent);
			if (currentTouched != null && parentTouched != null) {
				for (int slot = currentTouched.nextSetBit(0); slot >= 0; slot = currentTouched.nextSetBit(slot + 1)) {
					ArrayDeque<SlotBackup> hist = slotHistory[slot];
					if (hist == null) continue;
					SlotBackup top = hist.peekFirst();
					if (top == null || top.snapshotId != id) continue;
					if (parentTouched.get(slot)) {
						hist.pollFirst();
					} else {
						hist.pollFirst();
						hist.addFirst(new SlotBackup(parent, top.prev));
						parentTouched.set(slot);
					}
				}
			}
		}
		cleanup(id);
	}

	@Override
	protected void onRootCommit(Integer originalState) {
		be.setChanged();
		for (ArrayDeque<SlotBackup> hist : slotHistory) {
			if (hist != null) hist.clear();
		}
		touchedById.clear();
		parentById.clear();
		activeStack.clear();
		nextSnapshotId = 0;
	}

	private void cleanup(int id) {
		touchedById.remove(id);
		parentById.remove(id);
		if (!activeStack.isEmpty() && activeStack.peekFirst() == id) {
			activeStack.pop();
		}
	}
}
