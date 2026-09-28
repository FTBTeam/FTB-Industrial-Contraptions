package dev.ftb.mods.ftbic.screen;

import net.minecraft.world.inventory.DataSlot;

final class DataSlotPacking {
    static DataSlot[] slots(int count) {
        DataSlot[] slots = new DataSlot[count];
        for (int i = 0; i < count; i++) {
            slots[i] = DataSlot.standalone();
        }
        return slots;
    }

    static void pack(long value, DataSlot... parts) {
        for (int i = 0; i < parts.length; i++) {
            parts[i].set((int) ((value >>> (16 * i)) & 0xFFFF));
        }
    }

    static long unpack(DataSlot... parts) {
        long value = 0L;
        for (int i = 0; i < parts.length; i++) {
            value |= (parts[i].get() & 0xFFFFL) << (16 * i);
        }
        return value;
    }

    private DataSlotPacking() {}
}
