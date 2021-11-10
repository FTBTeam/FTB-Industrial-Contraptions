package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.util.EnergyItemHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
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
		stacks.set(0, stack);
	}

	@Override
	protected void onContentsChanged(int slot) {
		entity.setChanged();
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return stack.getItem() instanceof EnergyItemHandler && (charge ? ((EnergyItemHandler) stack.getItem()).canInsertEnergy() : ((EnergyItemHandler) stack.getItem()).canExtractEnergy());
	}

	@Override
	public int getSlotLimit(int slot) {
		return 1;
	}
}
