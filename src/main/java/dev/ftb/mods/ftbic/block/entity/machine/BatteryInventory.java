package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.util.EnergyItemHandler;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class BatteryInventory extends ItemStackHandler {
	public final ElectricBlockEntity entity;
	public final boolean charge;

	public BatteryInventory(ElectricBlockEntity e, boolean c) {
		super(1);
		entity = e;
		charge = c;
	}

	public void loadItem(ItemStack stack) {
		setStackInSlot(0, stack);
	}

	@Override
	protected void onContentsChanged(int slot) {
		entity.setChanged();
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		if (!(stack.getItem() instanceof EnergyItemHandler handler)) {
			return false;
		}
		return charge ? handler.canInsertEnergy() : handler.canExtractEnergy();
	}

	@Override
	public int getSlotLimit(int slot) {
		return 1;
	}
}
