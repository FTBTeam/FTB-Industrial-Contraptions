package dev.ftb.mods.ftbic.net;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import dev.ftb.mods.ftbic.block.entity.machine.TeleporterBlockEntity;
import dev.ftb.mods.ftbic.screen.TeleporterMenu;

public record ConfigureTeleporterPayload(String name, boolean isPublic, boolean unlink) implements CustomPacketPayload {
	public static final Type<ConfigureTeleporterPayload> TYPE = new Type<>(FTBIC.id("configure_teleporter"));

	public static final StreamCodec<FriendlyByteBuf, ConfigureTeleporterPayload> STREAM_CODEC = StreamCodec.of(
			(buf, pl) -> {
				buf.writeUtf(pl.name, 32);
				buf.writeBoolean(pl.isPublic);
				buf.writeBoolean(pl.unlink);
			},
			buf -> new ConfigureTeleporterPayload(buf.readUtf(32), buf.readBoolean(), buf.readBoolean())
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handleOnServer(ConfigureTeleporterPayload payload, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (!(context.player() instanceof net.minecraft.server.level.ServerPlayer sp)) return;
			if (!(sp.containerMenu instanceof TeleporterMenu menu)) return;
			if (!(menu.blockEntity instanceof TeleporterBlockEntity be)) return;
			be.configure(sp, payload.name, payload.isPublic);
			if (payload.unlink) be.unlink(sp);
		});
	}
}
