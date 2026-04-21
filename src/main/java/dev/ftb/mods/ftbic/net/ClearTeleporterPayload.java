package dev.ftb.mods.ftbic.net;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.entity.machine.TeleporterBlockEntity;
import dev.ftb.mods.ftbic.screen.TeleporterMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClearTeleporterPayload(boolean clearItems, boolean clearFluids) implements CustomPacketPayload {
	public static final Type<ClearTeleporterPayload> TYPE = new Type<>(FTBIC.id("clear_teleporter"));

	public static final StreamCodec<FriendlyByteBuf, ClearTeleporterPayload> STREAM_CODEC = StreamCodec.of(
			(buf, pl) -> {
				buf.writeBoolean(pl.clearItems);
				buf.writeBoolean(pl.clearFluids);
			},
			buf -> new ClearTeleporterPayload(buf.readBoolean(), buf.readBoolean())
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handleOnServer(ClearTeleporterPayload payload, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (!(context.player() instanceof ServerPlayer sp)) return;
			if (!(sp.containerMenu instanceof TeleporterMenu menu)) return;
			if (!(menu.blockEntity instanceof TeleporterBlockEntity be)) return;
			if (payload.clearItems) be.clearStorage();
			if (payload.clearFluids) be.clearFluids();
		});
	}
}
