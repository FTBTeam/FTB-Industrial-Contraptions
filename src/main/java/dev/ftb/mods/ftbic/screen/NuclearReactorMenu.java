package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.NuclearReactorBlockEntity;
import dev.ftb.mods.ftbic.item.ReactorBlueprintItem;
import dev.ftb.mods.ftbic.item.reactor.NuclearReactor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class NuclearReactorMenu extends ElectricBlockMenu {
    public final DataSlot pausedSlot = DataSlot.standalone();
    public final DataSlot allowRedstoneSlot = DataSlot.standalone();
    public final DataSlot heatScaled = DataSlot.standalone();
    public final DataSlot maxHeatScaled = DataSlot.standalone();
    public final DataSlot energyOutShort = DataSlot.standalone();
    public final DataSlot energyOutHigh = DataSlot.standalone();
    public final DataSlot runningFlag = DataSlot.standalone();
    public final DataSlot activeColumnsSlot = DataSlot.standalone();
    public final DataSlot coolingThousandths = DataSlot.standalone();

    public NuclearReactorMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        super(FTBICMenus.NUCLEAR_REACTOR.get(), id, playerInv, buf);
        registerSlots();
    }

    public NuclearReactorMenu(int id, Inventory playerInv, ElectricBlockEntity be) {
        super(FTBICMenus.NUCLEAR_REACTOR.get(), id, playerInv, be);
        registerSlots();
    }

    private void registerSlots() {
        addDataSlot(pausedSlot);
        addDataSlot(allowRedstoneSlot);
        addDataSlot(heatScaled);
        addDataSlot(maxHeatScaled);
        addDataSlot(energyOutShort);
        addDataSlot(energyOutHigh);
        addDataSlot(runningFlag);
        addDataSlot(activeColumnsSlot);
        addDataSlot(coolingThousandths);
    }

    @Override
    protected int getPlayerSlotOffset() {
        return 144;
    }

    public static final int HOTBAR_OFFSET_FROM_INV = 54;

    @Override
    protected void addPlayerInventorySlots(Inventory inv) {
        int off = getPlayerSlotOffset();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, off + row * 18));
            }
        }
        int hotbar = off + HOTBAR_OFFSET_FROM_INV;
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inv, col, 8 + col * 18, hotbar));
        }
    }

    public static int slotScreenY(int row) {
        return 18 + row * 18;
    }

    @Override
    protected void addMachineSlots(Inventory playerInv) {
        if (blockEntity == null || blockEntity.getSlotCount() == 0) {
            machineSlotCount = 0;
            return;
        }
        ElectricBlockEntityContainer container = new ElectricBlockEntityContainer(blockEntity);

        int activeColumns = NuclearReactor.MAX_COLUMNS;
        if (blockEntity instanceof NuclearReactorBlockEntity reactor) {
            activeColumns = Math.max(3, Math.min(NuclearReactor.MAX_COLUMNS, reactor.reactor.activeColumns));
        }
        // NOTE: do NOT touch activeColumnsSlot here, this method runs from super() before subclass
        // field initializers execute, so the DataSlot is still null. broadcastChanges() syncs it.

        for (int row = 0; row < NuclearReactor.ROWS; row++) {
            for (int col = 0; col < activeColumns; col++) {
                int idx = NuclearReactor.slotIndex(col, row);
                addSlot(new NuclearReactorSlot(container, blockEntity, idx, 8 + col * 18, slotScreenY(row)));
            }
        }

        machineSlotCount = NuclearReactor.ROWS * activeColumns;
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (blockEntity instanceof NuclearReactorBlockEntity reactor) {
            pausedSlot.set(reactor.reactor.paused ? 1 : 0);
            allowRedstoneSlot.set(reactor.reactor.allowRedstoneControl ? 1 : 0);
            int max = Math.max(1, reactor.reactor.maxHeat);
            heatScaled.set((int) Math.min(1000L, Math.round(1000D * reactor.reactor.heat / max)));
            maxHeatScaled.set(Math.min(Short.MAX_VALUE, max));
            double output = reactor.reactor.energyOutput * FTBICConfig.MACHINES.NUCLEAR_GENERATOR_OUTPUT.get();
            DataSlotPacking.pack(Math.clamp(Math.round(output), 0L, Integer.MAX_VALUE), energyOutShort, energyOutHigh);
            runningFlag.set(reactor.reactor.energyOutput > 0D ? 1 : 0);
            activeColumnsSlot.set(Math.max(3, Math.min(NuclearReactor.MAX_COLUMNS, reactor.reactor.activeColumns)));
            double extraCooling = FTBICConfig.NUCLEAR.WATER_COOLING_MULTIPLIER.get() - 1D;
            coolingThousandths.set(
                    extraCooling <= 0D
                            ? 0
                            : (int) Math.round(1000D * (reactor.computeEnvCooling() - 1D) / extraCooling));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return super.stillValid(player)
                && player.level() == blockEntity.getLevel()
                && player.distanceToSqr(blockEntity.getBlockPos().getCenter()) <= 64D;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        // Only active reactor columns have menu slots; the backing inventory always has 54.
        boolean moved = index < machineSlotCount
                ? moveItemStackTo(stack, machineSlotCount, slots.size(), true)
                : moveItemStackTo(stack, 0, machineSlotCount, false);
        if (!moved) return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        return original;
    }

    public boolean isPaused() {
        return pausedSlot.get() == 1;
    }

    public boolean allowRedstone() {
        return allowRedstoneSlot.get() == 1;
    }

    public boolean isRunning() {
        return runningFlag.get() == 1;
    }

    public float getHeatFraction() {
        return heatScaled.get() / 1000F;
    }

    public int getEnergyOutput() {
        return (int) DataSlotPacking.unpack(energyOutShort, energyOutHigh);
    }

    public int getActiveColumns() {
        return Math.max(3, Math.min(NuclearReactor.MAX_COLUMNS, activeColumnsSlot.get()));
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (!stillValid(player) || !(blockEntity instanceof NuclearReactorBlockEntity reactor)) return false;
        switch (id) {
            case 0 -> {
                reactor.reactor.paused = !reactor.reactor.paused;
                reactor.setChanged();
                return true;
            }
            case 1 -> {
                reactor.reactor.allowRedstoneControl = !reactor.reactor.allowRedstoneControl;
                reactor.setChanged();
                return true;
            }
            case 2 -> {
                int placed = reactor.buildPlannedDesign(player.getInventory());
                player.sendOverlayMessage(
                        placed == -2
                                ? Component.translatable("ftbic.reactor.design.chambers_missing")
                                : placed < 0
                                        ? Component.translatable("ftbic.reactor.design.pause_required")
                                        : Component.translatable("ftbic.reactor.design.built", placed));
                broadcastChanges();
                return true;
            }
            case 3 -> {
                reactor.setPlannedDesign(null);
                return true;
            }
            case 4 -> {
                boolean written = reactor.getPlannedDesign() != null
                        && ReactorBlueprintItem.writeBlank(player.getInventory(), reactor.getPlannedDesign());
                player.sendOverlayMessage(Component.translatable(
                        written ? "item.ftbic.reactor_blueprint.written" : "item.ftbic.reactor_blueprint.need_blank"));
                broadcastChanges();
                return true;
            }
        }
        return super.clickMenuButton(player, id);
    }
}
