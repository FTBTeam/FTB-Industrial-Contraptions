package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Optional;

public class PoweredCraftingTableMenu extends ElectricBlockMenu {
	public PoweredCraftingTableMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
		super(FTBICMenus.POWERED_CRAFTING_TABLE.get(), id, playerInv, buf);
	}

	public PoweredCraftingTableMenu(int id, Inventory playerInv, ElectricBlockEntity be) {
		super(FTBICMenus.POWERED_CRAFTING_TABLE.get(), id, playerInv, be);
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

		for (int i = 0; i < Math.min(9, inputs); i++) {
			int row = i / 3, col = i % 3;
			addSlot(new Slot(container, i, 30 + col * 18, 17 + row * 18));
		}
		for (int i = 9; i < inputs; i++) {
			addSlot(new Slot(container, i, -1000, -1000));
		}

		for (int i = 0; i < outputs; i++) {
			addSlot(new OutputSlot(container, inputs + i, 124, 35));
		}

		machineSlotCount = inputs + outputs;
	}

	public void setIngredients(ServerPlayer player, List<Optional<Ingredient>> ingredients) {
		if (blockEntity == null || blockEntity.inputItems.length < 9) return;
		Inventory inv = player.getInventory();

		for (int i = 0; i < 9 && i < ingredients.size(); i++) {
			Optional<Ingredient> ingOpt = ingredients.get(i);
			ItemStack current = blockEntity.inputItems[i];

			boolean matches = !current.isEmpty() && ingOpt.isPresent() && ingOpt.get().test(current);
			if (!current.isEmpty() && !matches) {
				if (!inv.add(current.copy())) player.drop(current.copy(), false);
				blockEntity.inputItems[i] = ItemStack.EMPTY;
				current = ItemStack.EMPTY;
			}

			if (!current.isEmpty()) continue;
			if (ingOpt.isEmpty()) continue;
			Ingredient ing = ingOpt.get();

			for (int j = 0; j < inv.getContainerSize(); j++) {
				ItemStack candidate = inv.getItem(j);
				if (candidate.isEmpty() || !ing.test(candidate)) continue;
				ItemStack one = candidate.copyWithCount(1);
				candidate.shrink(1);
				if (candidate.isEmpty()) inv.setItem(j, ItemStack.EMPTY);
				blockEntity.inputItems[i] = one;
				break;
			}
		}

		blockEntity.setChanged();
		player.containerMenu.broadcastChanges();
	}
}
