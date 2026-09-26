package dev.ftb.mods.ftbic.events;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.item.EnergyArmorItem;
import dev.ftb.mods.ftbic.item.FTBICItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = FTBIC.MOD_ID)
public final class QuantumFlightHandler {
    private static final String FLIGHT_GRANTED = "ftbic_quantum_flight_granted";

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        boolean granted = player.getPersistentData().getBooleanOr(FLIGHT_GRANTED, false);
        if (player.isCreative() || player.isSpectator()) {
            if (granted) player.getPersistentData().remove(FLIGHT_GRANTED);
            return;
        }

        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        EnergyArmorItem armor =
                chest.is(FTBICItems.QUANTUM_CHESTPLATE.get()) && chest.getItem() instanceof EnergyArmorItem item
                        ? item
                        : null;
        double flightCost = FTBICConfig.EQUIPMENT.ARMOR_FLIGHT_ENERGY.get();
        double available = armor == null ? 0D : armor.getEnergy(chest);
        boolean powered = available > 0D && available >= flightCost;

        if (!powered) {
            if (granted) revokeFlight(player);
            return;
        }

        if (!player.getAbilities().mayfly) {
            if (!granted) player.getPersistentData().putBoolean(FLIGHT_GRANTED, true);
            player.getAbilities().mayfly = true;
            player.onUpdateAbilities();
            granted = true;
        }

        if (granted && player.getAbilities().flying && flightCost > 0D) {
            armor.extractEnergy(chest, flightCost, false);
            if (armor.getEnergy(chest) < flightCost) revokeFlight(player);
        }
    }

    private static void revokeFlight(ServerPlayer player) {
        player.getPersistentData().remove(FLIGHT_GRANTED);
        player.getAbilities().mayfly = false;
        player.getAbilities().flying = false;
        player.onUpdateAbilities();
    }

    private QuantumFlightHandler() {}
}
