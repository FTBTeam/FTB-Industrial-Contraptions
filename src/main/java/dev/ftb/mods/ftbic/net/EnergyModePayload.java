package dev.ftb.mods.ftbic.net;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.util.EnergyDisplay;
import dev.ftb.mods.ftbic.util.ZapFEConversion;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@EventBusSubscriber(modid = FTBIC.MOD_ID)
public record EnergyModePayload(boolean fullFE, double rate) implements CustomPacketPayload {
    public static final Type<EnergyModePayload> TYPE = new Type<>(FTBIC.id("energy_mode"));

    public static final StreamCodec<FriendlyByteBuf, EnergyModePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            EnergyModePayload::fullFE,
            ByteBufCodecs.DOUBLE,
            EnergyModePayload::rate,
            EnergyModePayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleOnClient(EnergyModePayload payload, IPayloadContext context) {
        double rate = Double.isFinite(payload.rate) && payload.rate > 0D ? payload.rate : 1D;
        context.enqueueWork(() -> EnergyDisplay.sync(payload.fullFE, rate));
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        var payload = new EnergyModePayload(FTBICConfig.ENERGY.FULL_FE_MODE.get(), ZapFEConversion.rate());
        if (event.getPlayer() != null) {
            FTBICNet.sendToPlayer(event.getPlayer(), payload);
        } else {
            for (ServerPlayer player : event.getPlayerList().getPlayers()) {
                FTBICNet.sendToPlayer(player, payload);
            }
        }
    }
}
