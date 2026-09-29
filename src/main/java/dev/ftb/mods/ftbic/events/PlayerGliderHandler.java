package dev.ftb.mods.ftbic.events;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.item.EnergyArmorItem;
import dev.ftb.mods.ftbic.util.EnergyArmorMaterial;
import dev.ftb.mods.ftbic.util.EnergyItemHandler;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = FTBIC.MOD_ID)
public final class PlayerGliderHandler {
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (!player.isFallFlying()) return;

        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!chest.has(DataComponents.GLIDER) || !(chest.getItem() instanceof EnergyItemHandler energy)) return;

        boolean server = !player.level().isClientSide();
        double flightCost = FTBICConfig.EQUIPMENT.ARMOR_FLIGHT_ENERGY.get();
        double available = energy.getEnergy(chest);
        if (available <= 0D || available < flightCost) {
            player.stopFallFlying();
            return;
        }
        if (server && flightCost > 0D) energy.extractEnergy(chest, flightCost, false);

        int flightTicks = player.getFallFlyingTicks();
        Vec3 velocity = player.getDeltaMovement();
        Vec3 look = player.getLookAngle();
        boolean changed = false;

        boolean quantum =
                chest.getItem() instanceof EnergyArmorItem armor && armor.getMaterial() == EnergyArmorMaterial.QUANTUM;
        double boostCost = FTBICConfig.EQUIPMENT.ARMOR_FLIGHT_BOOST.get();
        double boostRate = FTBICConfig.EQUIPMENT.ARMOR_FLIGHT_BOOST_RATE.get() / 100D;
        if (quantum
                && boostRate > 0D
                && flightTicks >= 5
                && player.isJumping()
                && energy.getEnergy(chest) > 0D
                && energy.getEnergy(chest) >= boostCost) {
            Vec3 boost = new Vec3(
                    look.x * 0.1D + (look.x * 1.5D - velocity.x) * 0.5D,
                    look.y * 0.1D + (look.y * 1.5D - velocity.y) * 0.5D,
                    look.z * 0.1D + (look.z * 1.5D - velocity.z) * 0.5D);
            velocity = velocity.add(boost.scale(boostRate));
            changed = true;
            if (server && boostCost > 0D) energy.extractEnergy(chest, boostCost, false);
        } else if (flightTicks >= 3 && player.isShiftKeyDown()) {
            double brakeCost = FTBICConfig.EQUIPMENT.ARMOR_FLIGHT_STOP.get();
            double brakeRate = FTBICConfig.EQUIPMENT.ARMOR_FLIGHT_STOP_RATE.get() / 100D;
            double forwardVelocity = velocity.dot(look);
            if (brakeRate > 0D
                    && forwardVelocity > 0D
                    && energy.getEnergy(chest) > 0D
                    && energy.getEnergy(chest) >= brakeCost) {
                velocity = velocity.subtract(look.scale(forwardVelocity * brakeRate));
                changed = true;
                if (server && brakeCost > 0D) energy.extractEnergy(chest, brakeCost, false);
            }
        }

        if (changed) {
            player.setDeltaMovement(velocity);
            if (server) player.hurtMarked = true;
        }
    }

    private PlayerGliderHandler() {}
}
