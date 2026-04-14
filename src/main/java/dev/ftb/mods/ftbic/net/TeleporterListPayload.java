package dev.ftb.mods.ftbic.net;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.util.TeleporterEntry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

/**
 * S2C: ships the list of valid teleporter destinations to the client when its source teleporter
 * menu opens. Client populates the destination-picker screen from this payload.
 */
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
			dev.ftb.mods.ftbic.client.gui.TeleporterScreen.setEntries(payload.entries);
		});
	}
}
