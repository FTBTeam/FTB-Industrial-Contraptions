package dev.ftb.mods.ftbic.net;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.entity.machine.TeleporterBlockEntity;
import dev.ftb.mods.ftbic.screen.TeleporterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SelectTeleporterPayload(ResourceKey<Level> dimension, BlockPos pos) implements CustomPacketPayload {
	public static final Type<SelectTeleporterPayload> TYPE = new Type<>(FTBIC.id("select_teleporter"));

	public static final StreamCodec<FriendlyByteBuf, SelectTeleporterPayload> STREAM_CODEC = StreamCodec.of(
			(buf, pl) -> {
				buf.writeIdentifier(pl.dimension.identifier());
				buf.writeBlockPos(pl.pos);
			},
			buf -> new SelectTeleporterPayload(
					ResourceKey.create(Registries.DIMENSION, buf.readIdentifier()),
					buf.readBlockPos())
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handleOnServer(SelectTeleporterPayload payload, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (!(context.player() instanceof ServerPlayer sp)) return;
			if (!(sp.containerMenu instanceof TeleporterMenu menu)) return;
			if (!(menu.blockEntity instanceof TeleporterBlockEntity source)) return;
			source.select(sp, payload.dimension, payload.pos);
		});
	}

}
