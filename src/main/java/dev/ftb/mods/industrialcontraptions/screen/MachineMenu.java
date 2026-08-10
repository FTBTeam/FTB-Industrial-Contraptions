package dev.ftb.mods.industrialcontraptions.screen;

import dev.ftb.mods.industrialcontraptions.block.entity.ElectricBlockEntity;
import dev.ftb.mods.industrialcontraptions.block.entity.machine.MachineBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

public class MachineMenu extends ElectricBlockMenu {
	public MachineMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
		super(ICMenus.MACHINE.get(), id, playerInv, buf);
	}

	public MachineMenu(int id, Inventory playerInv, ElectricBlockEntity be) {
		super(ICMenus.MACHINE.get(), id, playerInv, be);
	}

	@Override
	protected void addMachineSlots(Inventory playerInv) {
		super.addMachineSlots(playerInv);
		addBatterySlot(8, 53);
		addUpgradeSlots(152);
	}

	public RecipeType<?> getJeiRecipeType() {
		if (blockEntity instanceof MachineBlockEntity m) {
			return m.recipeType.TYPE.get();
		}
		return null;
	}

	public List<RecipeType<?>> getJeiRecipeTypes() {
		if (!(blockEntity instanceof MachineBlockEntity m)) return List.of();
		return List.of(m.recipeType.TYPE.get());
	}
}
