package dev.ftb.mods.ftbic.net;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.screen.ElectricBlockMenu;
import dev.ftb.mods.ftbic.util.SideConfiguration;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SideConfigurationPayload(int containerId, int resource, int face, int mode)
        implements CustomPacketPayload {
    public static final Type<SideConfigurationPayload> TYPE = new Type<>(FTBIC.id("side_configuration"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SideConfigurationPayload> STREAM_CODEC = StreamCodec.of(
            (buf, p) -> {
                buf.writeVarInt(p.containerId);
                buf.writeVarInt(p.resource);
                buf.writeVarInt(p.face);
                buf.writeVarInt(p.mode);
            },
            buf -> new SideConfigurationPayload(
                    buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readVarInt()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleOnServer(SideConfigurationPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) apply(player, payload);
        });
    }

    public static boolean apply(ServerPlayer player, SideConfigurationPayload payload) {
        if (!(player.containerMenu instanceof ElectricBlockMenu menu)
                || menu.containerId != payload.containerId
                || !menu.stillValid(player)
                || menu.blockEntity == null) return false;
        var machine = menu.blockEntity;
        var pos = machine.getBlockPos();
        if (machine.getLevel() != player.level()
                || player.level().getBlockEntity(pos) != machine
                || player.distanceToSqr(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5) > 64) return false;
        if (payload.resource == -1 && payload.face == -1 && payload.mode == -1) {
            machine.setSideConfiguration(SideConfiguration.DEFAULT);
            return true;
        }
        if (payload.resource < 0
                || payload.resource >= SideConfiguration.Resource.values().length
                || payload.face < 0
                || payload.face >= SideConfiguration.Face.values().length
                || payload.mode < 0
                || payload.mode >= SideConfiguration.Mode.values().length) return false;
        var resource = SideConfiguration.Resource.values()[payload.resource];
        var face = SideConfiguration.Face.values()[payload.face];
        var mode = SideConfiguration.Mode.values()[payload.mode];
        if (!machine.supportsResource(resource) || !machine.supportsSideMode(resource, face, mode)) return false;
        machine.setSideConfiguration(machine.getSideConfiguration().with(resource, face, mode));
        return true;
    }
}
