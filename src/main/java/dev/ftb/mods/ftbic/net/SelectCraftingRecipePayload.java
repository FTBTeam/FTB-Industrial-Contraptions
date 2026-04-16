package dev.ftb.mods.ftbic.net;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.screen.PoweredCraftingTableMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.Optional;

public record SelectCraftingRecipePayload(List<Optional<Ingredient>> ingredients) implements CustomPacketPayload {
	public static final Type<SelectCraftingRecipePayload> TYPE = new Type<>(FTBIC.id("select_crafting_recipe"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SelectCraftingRecipePayload> STREAM_CODEC =
			Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list(9))
					.map(SelectCraftingRecipePayload::new, SelectCraftingRecipePayload::ingredients);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handleOnServer(SelectCraftingRecipePayload payload, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.player() instanceof ServerPlayer player
					&& player.containerMenu instanceof PoweredCraftingTableMenu menu) {
				menu.setIngredients(player, payload.ingredients());
			}
		});
	}
}
