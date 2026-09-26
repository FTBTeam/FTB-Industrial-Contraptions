package dev.ftb.mods.ftbic.block.entity.machine;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.item.UpgradeItem;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class UpgradeInventory {
    public static final int MAX_PER_TYPE_UPGRADES = 4;
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
        return slot >= 0
                && slot < stacks.size()
                && !stack.isEmpty()
                && stack.getItem() instanceof UpgradeItem
                && (!stack.is(FTBICItems.QUARRY_FILTER_UPGRADE.get()) || entity instanceof QuarryBlockEntity)
                && (!stack.is(FTBICItems.PARALLEL_PROCESSING_UPGRADE.get())
                        || entity instanceof MachineBlockEntity machine && machine.supportsParallelProcessing());
    }

    public int getSlotLimit(int slot) {
        return limit;
    }

    public int getSlotLimit(int slot, ItemStack stack) {
        if (!isItemValid(slot, stack)) return 0;
        int sameTypeElsewhere = 0;
        int parallelElsewhere = 0;
        for (int i = 0; i < stacks.size(); i++) {
            if (i == slot) continue;
            ItemStack existing = stacks.get(i);
            if (existing.is(stack.getItem())) sameTypeElsewhere += existing.getCount();
            if (existing.is(FTBICItems.PARALLEL_PROCESSING_UPGRADE.get())) parallelElsewhere += existing.getCount();
        }
        int available = Math.max(0, Math.min(limit, MAX_PER_TYPE_UPGRADES - sameTypeElsewhere));
        if (stack.is(FTBICItems.QUARRY_FILTER_UPGRADE.get())) {
            for (int i = 0; i < stacks.size(); i++) {
                if (i != slot && stacks.get(i).is(stack.getItem())) return 0;
            }
            return Math.min(available, 1);
        }
        return stack.is(FTBICItems.PARALLEL_PROCESSING_UPGRADE.get())
                ? Math.max(0, Math.min(available, 3 - parallelElsewhere))
                : available;
    }

    /** Inserts as much of a held stack as this machine can accept. The caller consumes that amount. */
    public int insert(ItemStack held) {
        if (held.isEmpty()) return 0;
        int remaining = held.getCount();
        for (int pass = 0; pass < 2 && remaining > 0; pass++) {
            for (int slot = 0; slot < stacks.size() && remaining > 0; slot++) {
                ItemStack current = stacks.get(slot);
                if (pass == 0 && (current.isEmpty() || !ItemStack.isSameItemSameComponents(current, held))) continue;
                if (pass == 1 && !current.isEmpty()) continue;
                int room = getSlotLimit(slot, held) - current.getCount();
                if (room <= 0) continue;
                int moved = Math.min(remaining, room);
                ItemStack result = current.isEmpty()
                        ? held.copyWithCount(moved)
                        : current.copyWithCount(current.getCount() + moved);
                setStackInSlot(slot, result);
                remaining -= moved;
            }
        }
        return held.getCount() - remaining;
    }

    public void onContentsChanged(int slot) {
        if (entity.hasLevel() && !entity.getLevel().isClientSide()) {
            entity.initProperties();
            entity.upgradesChanged();
            entity.setChanged();
        }
    }

    /** Sets one upgrade type's total without consuming items; rejects invalid changes atomically. */
    public boolean setUpgradeCount(Item item, int count) {
        if (!(item instanceof UpgradeItem) || count < 0 || count > MAX_PER_TYPE_UPGRADES) return false;
        UpgradeInventory planned = new UpgradeInventory(entity, getSlots(), limit);
        for (int slot = 0; slot < getSlots(); slot++) {
            ItemStack current = stacks.get(slot);
            planned.stacks.set(slot, current.is(item) ? ItemStack.EMPTY : current.copy());
        }
        ItemStack upgrade = new ItemStack(item);
        int remaining = count;
        for (int slot = 0; slot < getSlots() && remaining > 0; slot++) {
            if (!planned.stacks.get(slot).isEmpty()) continue;
            int moved = Math.min(remaining, planned.getSlotLimit(slot, upgrade));
            if (moved > 0) {
                planned.stacks.set(slot, upgrade.copyWithCount(moved));
                remaining -= moved;
            }
        }
        if (remaining > 0) return false;
        for (int slot = 0; slot < getSlots(); slot++) stacks.set(slot, planned.stacks.get(slot));
        onContentsChanged(0);
        return true;
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
                        ItemStack.CODEC.fieldOf("stack").forGetter(SlotEntry::stack))
                .apply(i, SlotEntry::new));
    }
}
