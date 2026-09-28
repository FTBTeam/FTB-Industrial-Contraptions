package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.screen.BatchFeederMenu;
import dev.ftb.mods.ftbic.util.BatchFeederTankHandler;
import dev.ftb.mods.ftbic.util.GhostItem;
import dev.ftb.mods.ftbic.util.SideConfiguration.Face;
import dev.ftb.mods.ftbic.util.SideConfiguration.Resource;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

public class BatchFeederBlockEntity extends ElectricBlockEntity {
    public static final int PATTERN_SIZE = 3;
    public static final int TANK_CAPACITY = 16_000;
    private List<GhostItem> batch = List.of();
    private FluidStack bufferFluid = FluidStack.EMPTY;
    private FluidStack batchFluid = FluidStack.EMPTY;
    private boolean fluidsDirty;
    public final BatchFeederTankHandler fluidHandler = new BatchFeederTankHandler(this);

    public FluidStack getBufferFluid() {
        return bufferFluid.copy();
    }

    public FluidStack getBatchFluid() {
        return batchFluid.copy();
    }

    /** Used by the tank transaction journal, including rollback. */
    public void setBufferFluid(FluidStack stack) {
        bufferFluid = stack.copy();
    }

    public void fluidsChanged() {
        fluidsDirty = true;
        setChanged();
    }

    public void setBatchFluid(FluidStack stack) {
        batchFluid =
                stack.isEmpty() ? FluidStack.EMPTY : stack.copyWithAmount(Math.min(TANK_CAPACITY, stack.getAmount()));
        setChanged();
        if (level != null && !level.isClientSide())
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public BatchFeederBlockEntity(BlockPos pos, BlockState state) {
        super(FTBICElectricBlocks.BATCH_FEEDER, pos, state);
    }

    public List<GhostItem> getBatch() {
        return batch;
    }

    public ItemStack getBatchItem(int slot) {
        for (GhostItem entry : batch)
            if (entry.slot() == slot) return entry.item().create();
        return ItemStack.EMPTY;
    }

    public void setBatchItem(int slot, ItemStack stack) {
        if (slot < 0 || slot >= PATTERN_SIZE) return;
        var entries = new ArrayList<>(batch);
        entries.removeIf(entry -> entry.slot() == slot);
        if (!stack.isEmpty())
            entries.add(GhostItem.of(slot, stack, Math.min(stack.getCount(), Math.min(64, stack.getMaxStackSize()))));
        setBatch(entries);
    }

    public void setBatch(List<GhostItem> entries) {
        var normalized = new ArrayList<GhostItem>();
        for (GhostItem entry : entries) {
            if (entry.slot() < 0 || entry.slot() >= PATTERN_SIZE) continue;
            normalized.removeIf(existing -> existing.slot() == entry.slot());
            ItemStack stack = entry.item().create();
            normalized.add(GhostItem.of(
                    entry.slot(), stack, Math.min(stack.getCount(), Math.min(64, stack.getMaxStackSize()))));
        }
        batch = List.copyOf(normalized);
        setChanged();
        if (level != null && !level.isClientSide())
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    @Override
    public int supportedTransfers(Resource resource, Face face) {
        return resource == Resource.ITEMS || resource == Resource.FLUIDS
                ? (face == Face.FRONT ? 2 : 1)
                : super.supportedTransfers(resource, face);
    }

    @Override
    public boolean allowsTransfer(Resource resource, @Nullable Direction side, boolean input) {
        if ((resource == Resource.ITEMS || resource == Resource.FLUIDS)
                && side != null
                && (input == (side == getFacing(Direction.NORTH)))) return false;
        return super.allowsTransfer(resource, side, input);
    }

    @Override
    public void tick() {
        super.tick();
        if (level != null && !level.isClientSide()) {
            if (level.getGameTime() % 8 == 0) trySendBatch();
            if (fluidsDirty) {
                fluidsDirty = false;
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    }

    /** Item and fluid destinations share one transaction; rejected batches leave both buffers intact. */
    public boolean trySendBatch() {
        if (level == null
                || level.isClientSide()
                || (batch.isEmpty() && batchFluid.isEmpty())
                || level.hasNeighborSignal(worldPosition)) return false;
        Direction front = getFacing(Direction.NORTH);
        if (!batch.isEmpty() && !allowsTransfer(Resource.ITEMS, front, false)) return false;
        if (!batchFluid.isEmpty()
                && (!allowsTransfer(Resource.FLUIDS, front, false)
                        || !FluidStack.isSameFluidSameComponents(bufferFluid, batchFluid)
                        || bufferFluid.getAmount() < batchFluid.getAmount())) return false;
        var destination = batch.isEmpty()
                ? null
                : level.getCapability(Capabilities.Item.BLOCK, worldPosition.relative(front), front.getOpposite());
        var fluidDestination = batchFluid.isEmpty()
                ? null
                : level.getCapability(Capabilities.Fluid.BLOCK, worldPosition.relative(front), front.getOpposite());
        if ((!batch.isEmpty() && destination == null) || (!batchFluid.isEmpty() && fluidDestination == null))
            return false;
        int[] consumed = new int[inputItems.length];
        for (GhostItem entry : batch) {
            ItemStack ingredient = entry.item().create();
            int remaining = ingredient.getCount();
            for (int slot = 0; slot < inputItems.length && remaining > 0; slot++) {
                if (!ItemStack.isSameItemSameComponents(inputItems[slot], ingredient)) continue;
                int taken = Math.min(remaining, inputItems[slot].getCount() - consumed[slot]);
                consumed[slot] += taken;
                remaining -= taken;
            }
            if (remaining > 0) return false;
        }
        try (var transaction = Transaction.openRoot()) {
            for (GhostItem entry : batch) {
                ItemStack ingredient = entry.item().create();
                if (destination.insert(ItemResource.of(ingredient), ingredient.getCount(), transaction)
                        != ingredient.getCount()) return false;
            }
            if (fluidDestination != null) {
                FluidResource fluid = FluidResource.of(batchFluid);
                int amount = batchFluid.getAmount();
                if (fluidDestination.insert(fluid, amount, transaction) != amount
                        || fluidHandler.manualAccess.extract(fluid, amount, transaction) != amount) return false;
            }
            transaction.commit();
        }
        for (int slot = 0; slot < inputItems.length; slot++) {
            if (consumed[slot] == 0) continue;
            inputItems[slot].shrink(consumed[slot]);
            if (inputItems[slot].isEmpty()) inputItems[slot] = ItemStack.EMPTY;
        }
        setChanged();
        return true;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("Batch", GhostItem.LIST_CODEC, batch);
        output.store("BufferFluid", FluidStack.OPTIONAL_CODEC, bufferFluid);
        output.store("BatchFluid", FluidStack.OPTIONAL_CODEC, batchFluid);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        batch = input.read("Batch", GhostItem.LIST_CODEC).orElse(List.of());
        bufferFluid = input.read("BufferFluid", FluidStack.OPTIONAL_CODEC).orElse(FluidStack.EMPTY);
        batchFluid = input.read("BatchFluid", FluidStack.OPTIONAL_CODEC).orElse(FluidStack.EMPTY);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new BatchFeederMenu(id, inventory, this);
    }
}
