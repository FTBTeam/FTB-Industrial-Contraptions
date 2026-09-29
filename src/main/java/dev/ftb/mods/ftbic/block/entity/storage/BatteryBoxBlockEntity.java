package dev.ftb.mods.ftbic.block.entity.storage;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.block.entity.generator.GeneratorBlockEntity;
import dev.ftb.mods.ftbic.screen.BatteryBoxMenu;
import dev.ftb.mods.ftbic.util.BatterySlotHelper;
import dev.ftb.mods.ftbic.util.SideConfiguration.Face;
import dev.ftb.mods.ftbic.util.SideConfiguration.Mode;
import dev.ftb.mods.ftbic.util.SideConfiguration.Resource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class BatteryBoxBlockEntity extends GeneratorBlockEntity {
    public BatteryBoxBlockEntity(ElectricBlockInstance type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv) {
        return new BatteryBoxMenu(id, inv, this);
    }

    @Override
    public void initProperties() {
        super.initProperties();
        maxEnergyOutputTransfer = maxEnergyOutput;
    }

    @Override
    public boolean keepsEnergyWhenBroken() {
        return true;
    }

    @Override
    public boolean isValidEnergyOutputSide(Direction direction) {
        Mode mode = getSideConfiguration().mode(Resource.ENERGY, Face.relative(getFacing(Direction.NORTH), direction));
        return mode == Mode.DEFAULT ? direction == getFacing(Direction.NORTH) : mode.allows(false);
    }

    @Override
    public boolean isValidEnergyInputSide(Direction direction) {
        Mode mode = getSideConfiguration().mode(Resource.ENERGY, Face.relative(getFacing(Direction.NORTH), direction));
        return mode == Mode.DEFAULT ? direction != getFacing(Direction.NORTH) : mode.allows(true);
    }

    @Override
    public int supportedTransfers(Resource resource, Face face) {
        return resource == Resource.ENERGY ? 3 : super.supportedTransfers(resource, face);
    }

    @Override
    public void handleGeneration() {
        // Discharge a battery item in input slot 0 into our buffer.
        // (tick() already gated on level != null && !level.isClientSide())
        if (inputItems.length == 0) return;
        ItemStack battery = inputItems[0];
        double drained = BatterySlotHelper.drainBatteryToBuffer(
                this, battery, maxInputEnergy, FTBICConfig.MACHINES.ITEM_TRANSFER_EFFICIENCY.get());
        if (drained > 0D) {
            active = true;
            if (battery.isEmpty()) {
                inputItems[0] = ItemStack.EMPTY;
            }
        }
    }
}
