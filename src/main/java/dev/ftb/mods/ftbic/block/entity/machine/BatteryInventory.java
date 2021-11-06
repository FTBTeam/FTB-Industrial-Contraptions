package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.item.BatteryItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class BatteryInventory extends ItemStackHandler {
	public final ElectricBlockEntity entity;

	public BatteryInventory(ElectricBlockEntity e) {
		super(1);
		entity = e;
	}

	@Override
	protected void onContentsChanged(int slot) {
		entity.setChanged();
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return stack.getItem() instanceof BatteryItem;
	}

	@Override
	public int getSlotLimit(int slot) {
		return 1;
	}
}
