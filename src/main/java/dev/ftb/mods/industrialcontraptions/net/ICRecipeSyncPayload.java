package dev.ftb.mods.industrialcontraptions.net;

import dev.ftb.mods.industrialcontraptions.IC;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import dev.ftb.mods.industrialcontraptions.integration.jei.ClientRecipeCache;

public record ICRecipeSyncPayload(List<RecipeHolder<?>> recipes) implements CustomPacketPayload {
	public static final Type<ICRecipeSyncPayload> TYPE = new Type<>(IC.id("recipe_sync"));

	public static final StreamCodec<RegistryFriendlyByteBuf, ICRecipeSyncPayload> STREAM_CODEC =
			StreamCodec.composite(
					RecipeHolder.STREAM_CODEC.apply(ByteBufCodecs.list()),
					ICRecipeSyncPayload::recipes,
					ICRecipeSyncPayload::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handleOnClient(ICRecipeSyncPayload p, IPayloadContext ctx) {
		ctx.enqueueWork(() -> ClientRecipeCache.applySyncedRecipes(p.recipes()));
	}
}
