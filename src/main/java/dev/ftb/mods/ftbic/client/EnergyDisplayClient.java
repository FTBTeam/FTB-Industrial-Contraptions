package dev.ftb.mods.ftbic.client;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.util.EnergyDisplay;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

@EventBusSubscriber(modid = FTBIC.MOD_ID, value = Dist.CLIENT)
public final class EnergyDisplayClient {
    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        EnergyDisplay.clearSync();
    }

    private EnergyDisplayClient() {}
}
