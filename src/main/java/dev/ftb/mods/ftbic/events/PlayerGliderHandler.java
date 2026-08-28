package dev.ftb.mods.ftbic.events;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.item.EnergyArmorItem;
import dev.ftb.mods.ftbic.item.EnergyItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = FTBIC.MOD_ID)
public class PlayerGliderHandler {

    // This is doing almost everything on both the client and server side to try to prevent "flying too fast" errors
    // and rubber-banding. The main exception is consuming energy in the items is only done on the server
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        final Player player = event.getEntity();
        final boolean isServer = !player.level().isClientSide();

        if (!player.isFallFlying()) return;

        // Look for an EnergyItem with a GLIDER component
        ItemStack chestItem = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chestItem.isEmpty() || !chestItem.has(DataComponents.GLIDER) || !(chestItem.getItem() instanceof EnergyItem glider)) return;

        // Stop early if there's no available energy
        double flightCost = FTBICConfig.EQUIPMENT.ARMOR_FLIGHT_ENERGY.get();
        if (glider.getEnergy(chestItem) < flightCost) {
            if (isServer) {
                glider.setEnergy(chestItem, 0D);
            }
            player.stopFallFlying();
            return;
        }

        final double boostEnergy = FTBICConfig.EQUIPMENT.ARMOR_FLIGHT_BOOST_ENERGY.get();
        final double boostRate = FTBICConfig.EQUIPMENT.ARMOR_FLIGHT_BOOST_RATE.get() / 100D;
        final double brakeEnergy = FTBICConfig.EQUIPMENT.ARMOR_FLIGHT_STOP_ENERGY.get();
        final double brakeRate = FTBICConfig.EQUIPMENT.ARMOR_FLIGHT_STOP_RATE.get() / 100D;

        final int flightTicks = player.getFallFlyingTicks();
        final boolean canBoost = glider instanceof EnergyArmorItem;
        final boolean isBraking = flightTicks >= 3 && player.isShiftKeyDown();
        final boolean isBoosting = flightTicks >= 5 && canBoost && player.isJumping();

        Vec3 velocity = player.getDeltaMovement();
        Vec3 look = player.getLookAngle();
        boolean movementModified = false;

        // Charge standard flight cost
        if (isServer) {
            glider.damageEnergyItem(chestItem, flightCost);
        }

        // Accelerate player, and charge boostEnergy
        if (isBoosting && (glider.isCreativeEnergyItem() || glider.getEnergy(chestItem) >= boostEnergy)) {
            Vec3 boost = new Vec3(
                look.x * 0.1D + (look.x * 1.5D - velocity.x) * 0.5D,
                look.y * 0.1D + (look.y * 1.5D - velocity.y) * 0.5D,
                look.z * 0.1D + (look.z * 1.5D - velocity.z) * 0.5D);
            velocity = velocity.add(boost.scale(boostRate));
            movementModified = true;
            if (isServer) {
                glider.damageEnergyItem(chestItem, boostEnergy);
            }
        // Decelerate player, and charge brakeEnergy
        } else if (isBraking && (glider.isCreativeEnergyItem() || glider.getEnergy(chestItem) >= brakeEnergy)) {
            double forwardVelocity = velocity.dot(look);
            if (forwardVelocity > 0) {
                Vec3 brake = look.scale(forwardVelocity * brakeRate);
                velocity = velocity.subtract(brake);
                movementModified = true;
                if (isServer) {
                    glider.damageEnergyItem(chestItem, brakeEnergy);
                }
            }
        }

        // Update player's movement speed
        if (movementModified) {
            player.setDeltaMovement(velocity);
            // marks player movement as "dirty" broadcast position updates to clients
            if (isServer) {
                player.hurtMarked = true;
            }
        }
    }
}
