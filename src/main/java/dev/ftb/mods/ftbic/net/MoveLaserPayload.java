package dev.ftb.mods.ftbic.net;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.entity.machine.DiggingBaseBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
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
            buf -> new MoveLaserPayload(buf.readBlockPos(), buf.readFloat(), buf.readVarInt(), buf.readFloat()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleOnClient(MoveLaserPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getBlockEntity(payload.pos) instanceof DiggingBaseBlockEntity digging) {
                digging.laserX = payload.x;
                digging.laserY = payload.y;
                digging.laserZ = payload.z;
            }
        });
    }
}
