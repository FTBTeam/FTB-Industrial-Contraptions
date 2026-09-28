package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.util.BatterySlotHelper;
import dev.ftb.mods.ftbic.util.EnergyItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ChargePadBlockEntity extends ElectricBlockEntityRef {
    public ChargePadBlockEntity(BlockPos pos, BlockState state) {
        super(FTBICElectricBlocks.CHARGE_PAD, pos, state);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (!super.isItemValid(slot, stack)) return false;
        if (stack.getItem() instanceof EnergyItemHandler handler) {
            return handler.canInsertEnergy() && !handler.isCreativeEnergyItem();
        }
        return BatterySlotHelper.isForeignEnergyItem(stack);
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide() || energy <= 0D) return;
        boolean charged = false;
        boolean needsCharge = false;
        for (ItemStack stack : inputItems) {
            charged |= chargeStack(stack);
            needsCharge |= BatterySlotHelper.needsCharge(stack);
        }
        if (charged && !needsCharge) playChargeCompleteSound();
    }

    @Override
    public void stepOn(ServerPlayer player) {
        if (energy <= 0D) return;
        Inventory inv = player.getInventory();
        boolean charged = false;
        boolean needsCharge = false;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            charged |= chargeStack(stack);
            needsCharge |= BatterySlotHelper.needsCharge(stack);
        }
        if (charged && !needsCharge) playChargeCompleteSound();
    }

    private boolean chargeStack(ItemStack stack) {
        if (energy <= 0D) return false;
        double accepted;
        if (stack.getItem() instanceof EnergyItemHandler handler) {
            if (handler.isCreativeEnergyItem()) return false;
            accepted = handler.insertEnergy(stack, energy, false);
        } else {
            accepted = BatterySlotHelper.chargeForeignItem(stack, energy);
        }
        if (accepted <= 0D) return false;
        energy -= accepted;
        active = true;
        setChanged();
        return true;
    }

    @Override
    public void spawnActiveParticles(Level level, double x, double y, double z, BlockState state, RandomSource r) {
        for (int i = 0; i < 5; i++) {
            level.addParticle(
                    DustParticleOptions.REDSTONE,
                    x + r.nextFloat(),
                    y + 1F + r.nextFloat() * 2F,
                    z + r.nextFloat(),
                    0D,
                    0D,
                    0D);
        }
    }
}
