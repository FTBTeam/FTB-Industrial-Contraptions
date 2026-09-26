package dev.ftb.mods.ftbic.block.entity.storage;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.screen.BankMenu;
import dev.ftb.mods.ftbic.util.SideConfiguration.Face;
import dev.ftb.mods.ftbic.util.SideConfiguration.Resource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public class BankCellBlockEntity extends ElectricBlockEntity {
    public BankCellBlockEntity(BlockPos pos, BlockState state) {
        super(FTBICElectricBlocks.INDUSTRIAL_BANK_CELL, pos, state);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new BankMenu(id, inventory, this);
    }

    @Override
    public int supportedTransfers(Resource resource, Face face) {
        return 0;
    }

    @Override
    public boolean isValidEnergyInputSide(Direction direction) {
        return false;
    }

    @Override
    public boolean keepsEnergyWhenBroken() {
        return true;
    }
}
