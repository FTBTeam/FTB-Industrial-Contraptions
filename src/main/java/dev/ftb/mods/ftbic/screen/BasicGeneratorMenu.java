package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.generator.BasicGeneratorBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;

public class BasicGeneratorMenu extends ElectricBlockMenu<BasicGeneratorBlockEntity> {
	public BasicGeneratorMenu(int id, Inventory playerInv, BasicGeneratorBlockEntity r) {
		super(FTBICMenus.BASIC_GENERATOR.get(), id, playerInv, r, null);
	}

	public BasicGeneratorMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
		this(id, playerInv, (BasicGeneratorBlockEntity) playerInv.player.level.getBlockEntity(buf.readBlockPos()));
	}

	@Override
	public void addBlockSlots(@Nullable Object extra) {
		addSlot(new SimpleItemHandlerSlot(entity.chargeBatteryInventory, 0, 98, 44));
		addSlot(new SimpleItemHandlerSlot(entity, 0, 62, 44));
	}
}
