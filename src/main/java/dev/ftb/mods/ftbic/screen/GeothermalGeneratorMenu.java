package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.generator.GeothermalGeneratorBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;

public class GeothermalGeneratorMenu extends ElectricBlockMenu<GeothermalGeneratorBlockEntity> {
	public GeothermalGeneratorMenu(int id, Inventory playerInv, GeothermalGeneratorBlockEntity r) {
		super(FTBICMenus.GEOTHERMAL_GENERATOR.get(), id, playerInv, r, null);
	}

	public GeothermalGeneratorMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
		this(id, playerInv, (GeothermalGeneratorBlockEntity) playerInv.player.level.getBlockEntity(buf.readBlockPos()));
	}

	@Override
	public void addBlockSlots(@Nullable Object extra) {
		addSlot(new SimpleItemHandlerSlot(entity.chargeBatteryInventory, 0, 62, 17));
		addSlot(new SimpleItemHandlerSlot(entity, 0, 62, 53));
	}
}
