package dev.ftb.mods.ftbic.net;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import dev.ftb.mods.ftbic.integration.jei.ClientRecipeCache;

public record FTBICRecipeSyncPayload(List<RecipeHolder<?>> recipes) implements CustomPacketPayload {
	public static final Type<FTBICRecipeSyncPayload> TYPE = new Type<>(FTBIC.id("recipe_sync"));

	public static final StreamCodec<RegistryFriendlyByteBuf, FTBICRecipeSyncPayload> STREAM_CODEC =
			StreamCodec.composite(
					RecipeHolder.STREAM_CODEC.apply(ByteBufCodecs.list()),
					FTBICRecipeSyncPayload::recipes,
					FTBICRecipeSyncPayload::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handleOnClient(FTBICRecipeSyncPayload p, IPayloadContext ctx) {
		ctx.enqueueWork(() -> ClientRecipeCache.applySyncedRecipes(p.recipes()));
	}
}
