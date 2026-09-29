package dev.ftb.mods.ftbic.block.entity;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.screen.IronFurnaceMenu;
import dev.ftb.mods.ftbic.util.GhostItem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class IronFurnaceBlockEntity extends AbstractFurnaceBlockEntity {
    private List<GhostItem> inputLocks = List.of();

    public ItemStack getInputLock(int slot) {
        for (GhostItem lock : inputLocks)
            if (lock.slot() == slot) return lock.item().create();
        return ItemStack.EMPTY;
    }

    public void setInputLock(int slot, ItemStack stack) {
        if (slot < 0 || slot > 1) return;
        var locks = new ArrayList<>(inputLocks);
        locks.removeIf(lock -> lock.slot() == slot);
        if (!stack.isEmpty()) locks.add(GhostItem.of(slot, stack, 1));
        inputLocks = List.copyOf(locks);
        setChanged();
        if (level != null && !level.isClientSide())
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        ItemStack lock = getInputLock(slot);
        return super.canPlaceItem(slot, stack) && (lock.isEmpty() || ItemStack.isSameItemSameComponents(lock, stack));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("InputLocks", GhostItem.LIST_CODEC, inputLocks);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        inputLocks = input.read("InputLocks", GhostItem.LIST_CODEC).orElse(List.of());
    }

    @Override
    public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(worldPosition);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveCustomOnly(registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public IronFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(FTBICBlockEntities.IRON_FURNACE.get(), pos, state, RecipeType.SMELTING);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.ftbic.iron_furnace");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inv) {
        return new IronFurnaceMenu(id, inv, this, this.dataAccess);
    }

    @Override
    protected int getBurnDuration(FuelValues fuelValues, ItemStack stack) {
        int base = super.getBurnDuration(fuelValues, stack);
        return Math.round(base * (float) FTBICConfig.MACHINES.IRON_FURNACE_ITEMS_PER_COAL.get() / 8F);
    }
}
