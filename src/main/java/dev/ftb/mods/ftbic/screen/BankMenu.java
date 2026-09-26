package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.BankPortBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.BankTopology;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.DataSlot;

public class BankMenu extends ElectricBlockMenu {
    private static final int SNAPSHOT_INTERVAL = 5;

    @Override
    protected int getPlayerSlotOffset() {
        return 134;
    }

    @Override
    protected void addMachineSlots(Inventory playerInv) {
        machineSlotCount = 0;
        if (blockEntity instanceof BankPortBlockEntity port) {
            for (int i = 0; i < port.chargeSlots.size(); i++) {
                addChargeSlot(port.chargeSlots.get(i), 53 + i * 18, 99);
            }
        }
    }

    public boolean hasChargeSlots() {
        return blockEntity instanceof BankPortBlockEntity;
    }

    public final DataSlot cells = DataSlot.standalone();
    public final DataSlot ports = DataSlot.standalone();
    private final DataSlot[] stored = DataSlotPacking.slots(4);
    private final DataSlot[] capacity = DataSlotPacking.slots(4);
    private long lastSnapshotTime = Long.MIN_VALUE;

    public BankMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        super(FTBICMenus.BANK.get(), id, inventory, buffer);
        addBankSlots();
    }

    public BankMenu(int id, Inventory inventory, ElectricBlockEntity entity) {
        super(FTBICMenus.BANK.get(), id, inventory, entity);
        addBankSlots();
    }

    private void addBankSlots() {
        addDataSlot(cells);
        addDataSlot(ports);
        for (DataSlot slot : stored) addDataSlot(slot);
        for (DataSlot slot : capacity) addDataSlot(slot);
    }

    @Override
    public void broadcastChanges() {
        if (blockEntity != null && blockEntity.getLevel() != null) {
            long time = blockEntity.getLevel().getGameTime();
            if (lastSnapshotTime == Long.MIN_VALUE || time - lastSnapshotTime >= SNAPSHOT_INTERVAL) {
                lastSnapshotTime = time;
                BankTopology.Snapshot snapshot =
                        BankTopology.snapshot(blockEntity.getLevel(), blockEntity.getBlockPos());
                cells.set(snapshot.cells());
                ports.set(snapshot.ports());
                DataSlotPacking.pack(Double.doubleToRawLongBits(snapshot.stored()), stored);
                DataSlotPacking.pack(Double.doubleToRawLongBits(snapshot.capacity()), capacity);
            }
        }
        super.broadcastChanges();
    }

    public double stored() {
        return Double.longBitsToDouble(DataSlotPacking.unpack(stored));
    }

    public double capacity() {
        return Double.longBitsToDouble(DataSlotPacking.unpack(capacity));
    }
}
