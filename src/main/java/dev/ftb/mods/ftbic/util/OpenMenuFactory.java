package dev.ftb.mods.ftbic.util;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

@FunctionalInterface
public interface OpenMenuFactory {
	AbstractContainerMenu create(int id, Inventory inventory);
}
