package dev.ftb.mods.ftbic.net;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.screen.TeleporterMenu;
import dev.ftb.mods.ftbic.util.TeleporterEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record TeleporterListPayload(List<TeleporterEntry> entries) implements CustomPacketPayload {
	public static final Type<TeleporterListPayload> TYPE = new Type<>(FTBIC.id("teleporter_list"));

	public static final StreamCodec<RegistryFriendlyByteBuf, TeleporterListPayload> STREAM_CODEC =
			TeleporterEntry.STREAM_CODEC.apply(ByteBufCodecs.list(256))
					.map(TeleporterListPayload::new, TeleporterListPayload::entries);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handleOnClient(TeleporterListPayload payload, IPayloadContext context) {
		context.enqueueWork(() -> {
			LocalPlayer p = Minecraft.getInstance().player;
			if (p == null) return;
			if (p.containerMenu instanceof TeleporterMenu menu) {
				menu.peers = payload.entries;
			}
		});
	}
}
