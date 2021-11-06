package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.machine.MachineBlockEntity;
import dev.ftb.mods.ftbic.recipe.MachineRecipeSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class MachineMenu extends AbstractContainerMenu {
	public final MachineBlockEntity entity;
	public final Player player;
	public final ContainerData containerData;
	public final MachineRecipeSerializer serializer;

	public MachineMenu(int id, Inventory playerInv, MachineBlockEntity r, ContainerData d, MachineRecipeSerializer s) {
		super(FTBICMenus.MACHINE.get(), id);
		entity = r;
		player = playerInv.player;
		containerData = d;
		serializer = s;

		int wx = 78 - (serializer.guiWidth / 2);
		int wy = 16;

		for (int x = 0; x < entity.inputItems.length; x++) {
			addSlot(new SimpleItemHandlerSlot(entity, x, wx + 1 + x * 18, wy + 1));
		}

		for (int x = 0; x < entity.outputItems.length; x++) {
			addSlot(new SimpleItemHandlerSlot(entity, entity.inputItems.length + x, wx + serializer.outputX + 5 + x * 25, wy + serializer.outputY + 5));
		}

		for (int y = 0; y < 4; y++) {
			addSlot(new SimpleItemHandlerSlot(entity.upgradeInventory, y, 152, 8 + y * 18));
		}

		addSlot(new SimpleItemHandlerSlot(entity.batteryInventory, 0, wx + 1 + serializer.batteryX, wy + 1 + serializer.batteryY));

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				addSlot(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
			}
		}

		for (int x = 0; x < 9; x++) {
			addSlot(new Slot(playerInv, x, 8 + x * 18, 142));
		}

		addDataSlots(containerData);
	}

	public MachineMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
		this(id, playerInv, (MachineBlockEntity) playerInv.player.level.getBlockEntity(buf.readBlockPos()), new SimpleContainerData(6), (MachineRecipeSerializer) ForgeRegistries.RECIPE_SERIALIZERS.getValue(buf.readResourceLocation()));
	}

	@Override
	public ItemStack quickMoveStack(Player player, int slotId) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(Player player) {
		return !entity.isRemoved();
	}

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();
	}

	@Override
	public boolean clickMenuButton(Player player, int button) {
		return false;
	}

	public int getProgress() {
		return containerData.get(0);
	}

	public int getMaxProgress() {
		return containerData.get(1);
	}

	public int getEnergy() {
		return containerData.get(2);
	}

	public int getEnergyCapacity() {
		return containerData.get(3);
	}

	public int getEnergyUse() {
		return containerData.get(4);
	}

	public int getAcceleration() {
		return containerData.get(5);
	}
}
