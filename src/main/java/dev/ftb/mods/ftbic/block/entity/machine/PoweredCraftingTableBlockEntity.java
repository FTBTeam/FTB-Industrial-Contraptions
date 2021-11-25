package dev.ftb.mods.ftbic.block.entity.machine;

import com.google.gson.JsonElement;
import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.screen.PoweredCraftingTableMenu;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class PoweredCraftingTableBlockEntity extends BasicMachineBlockEntity {
	public final Ingredient[] ingredients;
	private ResourceLocation matchedRecipe;

	public PoweredCraftingTableBlockEntity() {
		super(FTBICElectricBlocks.POWERED_CRAFTING_TABLE);
		ingredients = new Ingredient[9];
		Arrays.fill(ingredients, Ingredient.EMPTY);
	}

	@Override
	public void writeData(CompoundTag tag) {
		super.writeData(tag);

		for (int i = 0; i < 9; i++) {
			if (ingredients[i] != Ingredient.EMPTY) {
				tag.putString("Ingredient" + (i + 1), FTBICUtils.GSON.toJson(ingredients[i].toJson()));
			}
		}
	}

	@Override
	public void readData(CompoundTag tag) {
		super.readData(tag);
		Arrays.fill(ingredients, Ingredient.EMPTY);

		for (int i = 0; i < 9; i++) {
			String s = tag.getString("Ingredient" + (i + 1));
			ingredients[i] = s.isEmpty() ? Ingredient.EMPTY : Ingredient.fromJson(FTBICUtils.GSON.fromJson(s, JsonElement.class));
		}

		matchedRecipe = null;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return slot >= 0 && slot < 9 && ingredients[slot] != Ingredient.EMPTY && ingredients[slot].test(stack);
	}

	@Override
	public int getSlotLimit(int slot) {
		return slot < 9 ? 1 : 64;
	}

	@Override
	public void writeMenu(ServerPlayer player, FriendlyByteBuf buf) {
		super.writeMenu(player, buf);

		for (int i = 0; i < 9; i++) {
			ingredients[i].toNetwork(buf);
		}
	}

	@Override
	public void handleProcessing() {
		if (energy < energyUse) {
			return;
		}

		boolean hasRecipe = false;

		for (int i = 0; i < ingredients.length; i++) {
			if (ingredients[i] == Ingredient.EMPTY) {
				if (!inputItems[i].isEmpty()) {
					return;
				}
			} else {
				hasRecipe = true;

				if (inputItems[i].isEmpty() || !ingredients[i].test(inputItems[i])) {
					return;
				}
			}
		}

		if (!hasRecipe) {
			return;
		}

		FTBIC.LOGGER.info("Success!");
		energy -= energyUse;
		setChanged();
	}

	@Override
	public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide()) {
			openMenu((ServerPlayer) player, (id, inventory) -> new PoweredCraftingTableMenu(id, inventory, this, this));
		}

		return InteractionResult.SUCCESS;
	}

	public void updateRecipe(ServerPlayer player, Ingredient[] newIngredients) {
		for (int i = 0; i < 9; i++) {
			ingredients[i] = newIngredients[i];

			if (!inputItems[i].isEmpty() && !ingredients[i].test(inputItems[i])) {
				ItemHandlerHelper.giveItemToPlayer(player, inputItems[i].copy());
				inputItems[i] = ItemStack.EMPTY;
			}
		}

		setChanged();
	}
}