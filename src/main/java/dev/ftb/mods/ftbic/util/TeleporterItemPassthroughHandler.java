package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.block.entity.machine.TeleporterBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class TeleporterItemPassthroughHandler implements ResourceHandler<ItemResource> {
	private final TeleporterBlockEntity teleporter;

	public TeleporterItemPassthroughHandler(TeleporterBlockEntity teleporter) {
		this.teleporter = teleporter;
	}

	@Override
	public int size() {
		return teleporter.getItemSlotCount();
	}

	@Override
	public ItemResource getResource(int index) {
		ItemStack s = teleporter.getSlot(index);
		return s.isEmpty() ? ItemResource.EMPTY : ItemResource.of(s);
	}

	@Override
	public long getAmountAsLong(int index) {
		return teleporter.getSlot(index).getCount();
	}

	@Override
	public long getCapacityAsLong(int index, ItemResource resource) {
		if (teleporter.isSendSlot(index)) return resource.isEmpty() ? 64 : Math.min(resource.getMaxStackSize(), 64);
		return 0;
	}

	@Override
	public boolean isValid(int index, ItemResource resource) {
		return teleporter.isSendSlot(index);
	}

	@Override
	public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
		if (!teleporter.isSendSlot(index)) return 0;
		return teleporter.insertItemAt(index, resource, amount, transaction);
	}

	@Override
	public int insert(ItemResource resource, int amount, TransactionContext transaction) {
		return teleporter.tryInsertItem(resource, amount, transaction);
	}

	@Override
	public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
		if (!teleporter.isReceiveSlot(index)) return 0;
		return teleporter.extractItemAt(index - TeleporterBlockEntity.SEND_SLOTS, resource, amount, transaction);
	}

	@Override
	public int extract(ItemResource resource, int amount, TransactionContext transaction) {
		return teleporter.tryExtractItem(resource, amount, transaction);
	}
}
