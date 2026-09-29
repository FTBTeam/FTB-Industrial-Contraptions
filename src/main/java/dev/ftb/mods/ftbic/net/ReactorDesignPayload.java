package dev.ftb.mods.ftbic.net;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.entity.generator.NuclearReactorBlockEntity;
import dev.ftb.mods.ftbic.screen.NuclearReactorMenu;
import dev.ftb.mods.ftbic.util.ReactorDesign;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ReactorDesignPayload(int containerId, String json) implements CustomPacketPayload {
    public static final Type<ReactorDesignPayload> TYPE = new Type<>(FTBIC.id("reactor_design"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ReactorDesignPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeVarInt(payload.containerId);
                buf.writeUtf(payload.json, ReactorDesign.MAX_JSON_LENGTH);
            },
            buf -> new ReactorDesignPayload(buf.readVarInt(), buf.readUtf(ReactorDesign.MAX_JSON_LENGTH)));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleOnServer(ReactorDesignPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)
                    || !(player.containerMenu instanceof NuclearReactorMenu menu)
                    || menu.containerId != payload.containerId
                    || !menu.stillValid(player)
                    || !(menu.blockEntity instanceof NuclearReactorBlockEntity reactor)) return;
            try {
                reactor.setPlannedDesign(ReactorDesign.fromJson(payload.json));
            } catch (IllegalArgumentException e) {
                player.sendOverlayMessage(Component.translatable("ftbic.reactor.design.invalid"));
            }
        });
    }
}
