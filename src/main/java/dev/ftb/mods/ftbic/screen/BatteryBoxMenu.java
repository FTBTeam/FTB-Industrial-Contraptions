package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class BatteryBoxMenu extends ElectricBlockMenu {
    public BatteryBoxMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        super(FTBICMenus.BATTERY_BOX.get(), id, playerInv, buf);
    }

    public BatteryBoxMenu(int id, Inventory playerInv, ElectricBlockEntity be) {
        super(FTBICMenus.BATTERY_BOX.get(), id, playerInv, be);
    }

    @Override
    protected void addMachineSlots(Inventory playerInv) {
        if (blockEntity == null || blockEntity.getSlotCount() == 0) {
            machineSlotCount = 0;
            return;
        }
        ElectricBlockEntityContainer container = new ElectricBlockEntityContainer(blockEntity);
        int inputs = blockEntity.inputItems.length;
        if (inputs > 0) addSlot(new FilteredInputSlot(container, 0, 53, 35));
        machineSlotCount = inputs;
        addChargeSlot(109, 35);
    }
}
