package dev.ftb.mods.ftbic.block.entity.machine;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.item.UpgradeItem;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class UpgradeInventory {
	public final ElectricBlockEntity entity;
	public final int limit;
	private final NonNullList<ItemStack> stacks;

	public UpgradeInventory(ElectricBlockEntity e, int slots, int stackLimit) {
		entity = e;
		limit = stackLimit;
		stacks = NonNullList.withSize(slots, ItemStack.EMPTY);
	}

	public int getSlots() {
		return stacks.size();
	}

	public ItemStack getStackInSlot(int slot) {
		return stacks.get(slot);
	}

	public void setStackInSlot(int slot, ItemStack stack) {
		stacks.set(slot, stack == null ? ItemStack.EMPTY : stack);
		onContentsChanged(slot);
	}

	public boolean isItemValid(int slot, ItemStack stack) {
		return stack.getItem() instanceof UpgradeItem;
	}

	public int getSlotLimit(int slot) {
		return limit;
	}

	protected void onContentsChanged(int slot) {
		if (entity.hasLevel() && !entity.getLevel().isClientSide()) {
			entity.initProperties();
			entity.upgradesChanged();
			entity.setChanged();
		}
	}

	public int countUpgrades(Item item) {
		int count = 0;
		for (ItemStack stack : stacks) {
			if (stack.getItem() == item) count += stack.getCount();
		}
		return count;
	}

	public void serialize(ValueOutput output) {
		ValueOutput.TypedOutputList<SlotEntry> list = output.list("Items", SlotEntry.CODEC);
		for (int i = 0; i < stacks.size(); i++) {
			ItemStack s = stacks.get(i);
			if (!s.isEmpty()) list.add(new SlotEntry(i, s));
		}
	}

	public void deserialize(ValueInput input) {
		for (int i = 0; i < stacks.size(); i++) stacks.set(i, ItemStack.EMPTY);
		input.listOrEmpty("Items", SlotEntry.CODEC).forEach(e -> {
			if (e.slot >= 0 && e.slot < stacks.size()) stacks.set(e.slot, e.stack);
		});
	}

	public record SlotEntry(int slot, ItemStack stack) {
		public static final Codec<SlotEntry> CODEC = RecordCodecBuilder.create(i -> i.group(
				Codec.INT.fieldOf("slot").forGetter(SlotEntry::slot),
				ItemStack.CODEC.fieldOf("stack").forGetter(SlotEntry::stack)
		).apply(i, SlotEntry::new));
	}
}
