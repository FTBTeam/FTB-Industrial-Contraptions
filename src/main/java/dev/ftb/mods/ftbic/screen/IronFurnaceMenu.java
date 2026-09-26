package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.IronFurnaceBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.item.crafting.RecipeType;

public class IronFurnaceMenu extends AbstractFurnaceMenu {
    public final IronFurnaceBlockEntity blockEntity;

    public IronFurnaceMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        this(
                id,
                playerInv,
                playerInv.player.level().getBlockEntity(buf.readBlockPos()) instanceof IronFurnaceBlockEntity furnace
                        ? furnace
                        : new SimpleContainer(3),
                new SimpleContainerData(4));
    }

    public IronFurnaceMenu(int id, Inventory playerInv, Container container, ContainerData data) {
        super(
                FTBICMenus.IRON_FURNACE.get(),
                RecipeType.SMELTING,
                RecipePropertySet.FURNACE_INPUT,
                RecipeBookType.FURNACE,
                id,
                playerInv,
                container,
                data);
        blockEntity = container instanceof IronFurnaceBlockEntity furnace ? furnace : null;
        for (int slot = 0; slot < 2; slot++) {
            Slot original = slots.get(slot);
            Slot filtered = new Slot(container, slot, original.x, original.y) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return original.mayPlace(stack) && container.canPlaceItem(getContainerSlot(), stack);
                }

                @Override
                public int getMaxStackSize(ItemStack stack) {
                    return mayPlace(stack) ? original.getMaxStackSize(stack) : 0;
                }
            };
            filtered.index = slot;
            slots.set(slot, filtered);
        }
    }
}
