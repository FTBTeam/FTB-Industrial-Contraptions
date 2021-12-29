package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.machine.PoweredCraftingTableBlockEntity;
import dev.ftb.mods.ftbic.net.SelectCraftingRecipeMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

public class PoweredCraftingTableMenu extends ElectricBlockMenu<PoweredCraftingTableBlockEntity> {
	public PoweredCraftingTableMenu(int id, Inventory playerInv, PoweredCraftingTableBlockEntity r) {
		super(FTBICMenus.POWERED_CRAFTING_TABLE.get(), id, playerInv, r, null);
	}

	public PoweredCraftingTableMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
		this(id, playerInv, (PoweredCraftingTableBlockEntity) playerInv.player.level.getBlockEntity(buf.readBlockPos()));

		for (int i = 0; i < entity.ingredients.length; i++) {
			entity.ingredients[i] = Ingredient.fromNetwork(buf);
		}
	}

	@Override
	public void addBlockSlots(@Nullable Object extra) {
		for (int y = 0; y < entity.upgradeInventory.getSlots(); y++) {
			addSlot(new SimpleItemHandlerSlot(entity.upgradeInventory, y, 152, 8 + y * 18));
		}

		addSlot(new SimpleItemHandlerSlot(entity.batteryInventory, 0, 15, 44));

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				addSlot(new SimpleItemHandlerSlot(entity, y * 3 + x, 44 + x * 18, 17 + y * 18));
			}
		}

		addSlot(new SimpleItemHandlerSlot(entity, 9, 115, 35));
	}

	public void setIngredients(Player player, Ingredient[] in) {
		for (int i = 0; i < 9; i++) {
			entity.ingredients[i] = in[i];

			if (player instanceof ServerPlayer && !entity.inputItems[i].isEmpty() && !in[i].test(entity.inputItems[i])) {
				ItemHandlerHelper.giveItemToPlayer(player, entity.inputItems[i].copy());
				entity.inputItems[i] = ItemStack.EMPTY;
			}
		}

		entity.setChanged();

		if (!(player instanceof ServerPlayer)) {
			new SelectCraftingRecipeMessage(in).sendToServer();
		}
	}
}
