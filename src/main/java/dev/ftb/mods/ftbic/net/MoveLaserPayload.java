package dev.ftb.mods.ftbic.net;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MoveLaserPayload(BlockPos pos, float x, int y, float z) implements CustomPacketPayload {
	public static final Type<MoveLaserPayload> TYPE = new Type<>(FTBIC.id("move_laser"));

	public static final StreamCodec<FriendlyByteBuf, MoveLaserPayload> STREAM_CODEC = StreamCodec.of(
			(buf, pl) -> {
				buf.writeBlockPos(pl.pos);
				buf.writeFloat(pl.x);
				buf.writeVarInt(pl.y);
				buf.writeFloat(pl.z);
			},
			buf -> new MoveLaserPayload(buf.readBlockPos(), buf.readFloat(), buf.readVarInt(), buf.readFloat())
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handleOnClient(MoveLaserPayload payload, IPayloadContext context) {
		context.enqueueWork(() -> {
			// Currently no-op — DiggingBeamRenderer derives its beam from the BE's own state +
			// game time without per-tick position sync. This payload is plumbed end-to-end and
			// available for a future fine-grained laser-cursor renderer if we want it.
		});
	}
}
