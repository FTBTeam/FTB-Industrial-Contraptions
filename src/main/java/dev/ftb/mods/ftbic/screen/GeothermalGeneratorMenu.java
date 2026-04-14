package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.GeothermalGeneratorBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;

public class GeothermalGeneratorMenu extends ElectricBlockMenu {
	public final DataSlot fluidScaled = DataSlot.standalone();

	public GeothermalGeneratorMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
		super(FTBICMenus.GEOTHERMAL_GENERATOR.get(), id, playerInv, buf);
		addDataSlot(fluidScaled);
	}

	public GeothermalGeneratorMenu(int id, Inventory playerInv, ElectricBlockEntity be) {
		super(FTBICMenus.GEOTHERMAL_GENERATOR.get(), id, playerInv, be);
		addDataSlot(fluidScaled);
	}

	@Override
	protected void addMachineSlots(Inventory playerInv) {
		if (blockEntity == null || blockEntity.getSlotCount() == 0) {
			machineSlotCount = 0;
			return;
		}
		ElectricBlockEntityContainer container = new ElectricBlockEntityContainer(blockEntity);
		int inputs = blockEntity.inputItems.length;
		int outputs = blockEntity.outputItems.length;
		if (inputs > 0) addSlot(new Slot(container, 0, 62, 17));
		if (outputs > 0) addSlot(new OutputSlot(container, inputs, 62, 53));
		machineSlotCount = inputs + outputs;
	}

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();
		if (blockEntity instanceof GeothermalGeneratorBlockEntity geo) {
			int cap = geo.getTankCapacity();
			fluidScaled.set(cap <= 0 ? 0 : (int) Math.round(1000D * geo.fluidAmount / cap));
		}
	}

	/** Client-side: fluid fill fraction 0-1 for bar rendering. */
	public float getFluidFraction() {
		return fluidScaled.get() / 1000F;
	}

	@Override
	public boolean stillValid(Player player) {
		return super.stillValid(player);
	}
}
