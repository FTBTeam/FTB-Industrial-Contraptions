package dev.ftb.mods.industrialcontraptions.screen;

import dev.ftb.mods.industrialcontraptions.block.entity.ElectricBlockEntity;
import dev.ftb.mods.industrialcontraptions.block.entity.generator.BasicGeneratorBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;

public class BasicGeneratorMenu extends ElectricBlockMenu {
	public final DataSlot fuelBar = DataSlot.standalone();
	public final DataSlot fuelTicksRemaining = DataSlot.standalone();

	public BasicGeneratorMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
		super(ICMenus.BASIC_GENERATOR.get(), id, playerInv, buf);
		addDataSlot(fuelBar);
		addDataSlot(fuelTicksRemaining);
	}

	public BasicGeneratorMenu(int id, Inventory playerInv, ElectricBlockEntity be) {
		super(ICMenus.BASIC_GENERATOR.get(), id, playerInv, be);
		addDataSlot(fuelBar);
		addDataSlot(fuelTicksRemaining);
	}

	@Override
	protected void addMachineSlots(Inventory playerInv) {
		if (blockEntity == null || blockEntity.getSlotCount() == 0) {
			machineSlotCount = 0;
			return;
		}
		ElectricBlockEntityContainer container = new ElectricBlockEntityContainer(blockEntity);
		int inputs = blockEntity.inputItems.length;
		if (inputs > 0) addSlot(new Slot(container, 0, 62, 44));
		machineSlotCount = inputs;
	}

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();
		if (blockEntity instanceof BasicGeneratorBlockEntity gen) {
			int max = gen.maxFuelTicks;
			fuelBar.set(max <= 0 ? 0 : (int) Math.round(14D * gen.fuelTicks / max));
			fuelTicksRemaining.set(Math.min(Short.MAX_VALUE, gen.fuelTicks));
		}
	}

	public int getFuelBar() {
		return fuelBar.get();
	}

	public int getFuelTicksRemaining() {
		return fuelTicksRemaining.get();
	}
}
