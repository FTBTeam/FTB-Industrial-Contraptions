package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.machine.AntimatterConstructorBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import org.jetbrains.annotations.Nullable;

public class AntimatterConstructorMenu extends ElectricBlockMenu<AntimatterConstructorBlockEntity> {
	public AntimatterConstructorMenu(int id, Inventory playerInv, AntimatterConstructorBlockEntity r, ContainerData d) {
		super(FTBICMenus.ANTIMATTER_CONSTRUCTOR.get(), id, playerInv, r, d, null);
	}

	public AntimatterConstructorMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
		this(id, playerInv, (AntimatterConstructorBlockEntity) playerInv.player.level.getBlockEntity(buf.readBlockPos()), new SimpleContainerData(2));
	}

	@Override
	public void addBlockSlots(@Nullable Object extra) {
		addSlot(new SimpleItemHandlerSlot(entity, 0, 53, 36));
		addSlot(new SimpleItemHandlerSlot(entity, 1, 107, 36));
	}

	public int getBoost() {
		return containerData.get(1);
	}
}
