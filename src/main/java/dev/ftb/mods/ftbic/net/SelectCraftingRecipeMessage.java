package dev.ftb.mods.ftbic.net;

import dev.ftb.mods.ftbic.screen.PoweredCraftingTableMenu;
import me.shedaniel.architectury.networking.NetworkManager;
import me.shedaniel.architectury.networking.simple.BaseC2SMessage;
import me.shedaniel.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Ingredient;

public class SelectCraftingRecipeMessage extends BaseC2SMessage {
	private final Ingredient[] ingredients;

	public SelectCraftingRecipeMessage(Ingredient[] in) {
		ingredients = in;
	}

	public SelectCraftingRecipeMessage(FriendlyByteBuf buf) {
		ingredients = new Ingredient[9];

		for (int i = 0; i < ingredients.length; i++) {
			ingredients[i] = Ingredient.fromNetwork(buf);
		}
	}

	@Override
	public MessageType getType() {
		return FTBICNet.SELECT_CRAFTING_RECIPE;
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		for (Ingredient ingredient : ingredients) {
			ingredient.toNetwork(buf);
		}
	}

	@Override
	public void handle(NetworkManager.PacketContext ctx) {
		ServerPlayer p = (ServerPlayer) ctx.getPlayer();

		if (p.containerMenu instanceof PoweredCraftingTableMenu) {
			((PoweredCraftingTableMenu) p.containerMenu).setIngredients(p, ingredients);
		}
	}
}
