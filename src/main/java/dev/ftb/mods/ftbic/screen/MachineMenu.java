package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.MachineBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.RecipeType;

public class MachineMenu extends ElectricBlockMenu {
	public MachineMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
		super(FTBICMenus.MACHINE.get(), id, playerInv, buf);
	}

	public MachineMenu(int id, Inventory playerInv, ElectricBlockEntity be) {
		super(FTBICMenus.MACHINE.get(), id, playerInv, be);
	}

	public RecipeType<?> getJeiRecipeType() {
		if (blockEntity instanceof MachineBlockEntity m) {
			return m.recipeType.TYPE.get();
		}
		return null;
	}
}
