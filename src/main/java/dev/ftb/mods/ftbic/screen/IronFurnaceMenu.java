package dev.ftb.mods.ftbic.screen;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.item.crafting.RecipeType;

public class IronFurnaceMenu extends AbstractFurnaceMenu {
	public IronFurnaceMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
		super(FTBICMenus.IRON_FURNACE.get(), RecipeType.SMELTING, RecipePropertySet.FURNACE_INPUT, RecipeBookType.FURNACE, id, playerInv);
	}

	public IronFurnaceMenu(int id, Inventory playerInv, Container container, ContainerData data) {
		super(FTBICMenus.IRON_FURNACE.get(), RecipeType.SMELTING, RecipePropertySet.FURNACE_INPUT, RecipeBookType.FURNACE, id, playerInv, container, data);
	}
}
