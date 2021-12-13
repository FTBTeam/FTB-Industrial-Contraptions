package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.machine.PumpBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class PumpMenu extends ElectricBlockMenu<PumpBlockEntity> {
	public PumpMenu(int id, Inventory playerInv, PumpBlockEntity r, ContainerData d) {
		super(FTBICMenus.PUMP.get(), id, playerInv, r, d, null);
	}

	public PumpMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
		this(id, playerInv, (PumpBlockEntity) playerInv.player.level.getBlockEntity(buf.readBlockPos()), new SimpleContainerData(2));
		entity.filter = ForgeRegistries.FLUIDS.getValue(buf.readResourceLocation());
		entity.fluidStack = FluidStack.readFromPacket(buf);
	}

	@Override
	public void addBlockSlots(@Nullable Object extra) {
		for (int y = 0; y < entity.upgradeInventory.getSlots(); y++) {
			addSlot(new SimpleItemHandlerSlot(entity.upgradeInventory, y, 152, 8 + y * 18));
		}

		addSlot(new SimpleItemHandlerSlot(entity.batteryInventory, 0, 107, 53));

		addSlot(new SimpleItemHandlerSlot(entity, 0, 53, 17));
		addSlot(new SimpleItemHandlerSlot(entity, 1, 53, 53));
	}

	@Override
	public boolean clickMenuButton(Player player, int button) {
		if (button == 0) {
			entity.paused = !entity.paused;
			entity.syncBlock();
			return true;
		}

		return false;
	}

	public boolean isPaused() {
		return containerData.get(1) != 0;
	}
}
