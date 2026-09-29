package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

/** Remains present on disabled faces so native energy cannot fall back to FE conversion. */
public record SidedZapHandler(
        ElectricBlockEntity machine, @Nullable Direction side) implements ZapEnergyHandler {
    private boolean accepts() {
        if (machine.isRemoved()) return false;
        if (side != null) return machine.isValidEnergyInputSide(side);
        for (Direction face : Direction.values()) if (machine.isValidEnergyInputSide(face)) return true;
        return false;
    }

    @Override
    public double getMaxInputEnergy() {
        return accepts() ? machine.getMaxInputEnergy() : 0;
    }

    @Override
    public double getMaxOutputEnergy() {
        return machine.getMaxOutputEnergy();
    }

    @Override
    public double getEnergyCapacity() {
        return machine.getEnergyCapacity();
    }

    @Override
    public double getEnergy() {
        return machine.getEnergy();
    }

    @Override
    public void setEnergyRaw(double energy) {
        machine.setEnergyRaw(energy);
    }

    @Override
    public void energyChanged(double previous) {
        machine.energyChanged(previous);
    }

    @Override
    public double insertEnergy(double amount, boolean simulate) {
        return accepts() ? machine.insertEnergy(amount, simulate) : 0;
    }

    @Override
    public boolean isValidEnergyInputSide(Direction direction) {
        return accepts();
    }

    @Override
    public boolean canBurn() {
        return machine.canBurn();
    }

    @Override
    public boolean isBurnt() {
        return machine.isBurnt();
    }

    @Override
    public void setBurnt(boolean burnt) {
        machine.setBurnt(burnt);
    }

    @Override
    public boolean isEnergyHandlerInvalid() {
        return machine.isRemoved();
    }
}
