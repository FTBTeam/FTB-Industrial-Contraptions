package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.FluidMachineBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.MachineBlockEntity;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.RecipeType;

public class MachineMenu extends ElectricBlockMenu {
    public MachineMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        super(FTBICMenus.MACHINE.get(), id, playerInv, buf);
    }

    public MachineMenu(int id, Inventory playerInv, ElectricBlockEntity be) {
        super(FTBICMenus.MACHINE.get(), id, playerInv, be);
    }

    @Override
    protected void addMachineSlots(Inventory playerInv) {
        if (blockEntity instanceof FluidMachineBlockEntity) {
            var container = new ElectricBlockEntityContainer(blockEntity);
            addSlot(new InputSlot(container, 0, 60, 35));
            int outputs = blockEntity.outputItems.length;
            for (int i = 0; i < outputs; i++) {
                addSlot(new OutputSlot(container, 1 + i, 108, centrifugeOutputY(outputs, i)));
            }
            machineSlotCount = 1 + outputs;
        } else {
            super.addMachineSlots(playerInv);
        }
        addBatterySlot(8, 53);
        addUpgradeSlots(152);
    }

    public static int centrifugeOutputY(int outputs, int slot) {
        return 35 - (outputs - 1) * 9 + slot * 18;
    }

    public RecipeType<?> getJeiRecipeType() {
        if (blockEntity instanceof MachineBlockEntity m) {
            return m.recipeType.TYPE.get();
        }
        return null;
    }

    public List<RecipeType<?>> getJeiRecipeTypes() {
        if (!(blockEntity instanceof MachineBlockEntity m)) return List.of();
        return List.of(m.recipeType.TYPE.get());
    }
}
