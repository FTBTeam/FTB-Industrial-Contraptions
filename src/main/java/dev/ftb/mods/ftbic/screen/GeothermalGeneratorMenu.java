package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.generator.GeothermalGeneratorBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import org.jetbrains.annotations.Nullable;

public class GeothermalGeneratorMenu extends ElectricBlockMenu<GeothermalGeneratorBlockEntity> {
	public GeothermalGeneratorMenu(int id, Inventory playerInv, GeothermalGeneratorBlockEntity r, ContainerData d) {
		super(FTBICMenus.GEOTHERMAL_GENERATOR.get(), id, playerInv, r, d, null);
	}

	public GeothermalGeneratorMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
		this(id, playerInv, (GeothermalGeneratorBlockEntity) playerInv.player.level.getBlockEntity(buf.readBlockPos()), new SimpleContainerData(2));
	}

	@Override
	public void addBlockSlots(@Nullable Object extra) {
		addSlot(new SimpleItemHandlerSlot(entity.chargeBatteryInventory, 0, 62, 17));
		addSlot(new SimpleItemHandlerSlot(entity, 0, 62, 53));
	}

	public int getFluidAmount() {
		return containerData.get(0);
	}

	public int getEnergyBar() {
		return containerData.get(1);
	}
}
