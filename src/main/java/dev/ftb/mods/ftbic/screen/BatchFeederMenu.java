package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class BatchFeederMenu extends ElectricBlockMenu {
    public BatchFeederMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        super(FTBICMenus.BATCH_FEEDER.get(), id, inventory, buffer);
    }

    public BatchFeederMenu(int id, Inventory inventory, ElectricBlockEntity machine) {
        super(FTBICMenus.BATCH_FEEDER.get(), id, inventory, machine);
    }

    @Override
    protected void addMachineSlots(Inventory inventory) {
        if (blockEntity == null) return;
        var container = new ElectricBlockEntityContainer(blockEntity);
        for (int slot = 0; slot < 9; slot++) addSlot(new Slot(container, slot, 32 + slot % 3 * 18, 36 + slot / 3 * 18));
        machineSlotCount = 9;
    }

    @Override
    protected int getPlayerSlotOffset() {
        return 130;
    }
}
