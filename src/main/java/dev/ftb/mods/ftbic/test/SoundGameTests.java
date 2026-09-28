package dev.ftb.mods.ftbic.test;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.generator.NuclearReactorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.ChargePadBlockEntity;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.sound.FTBICSounds;
import dev.ftb.mods.ftbic.util.EnergyItemHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.PlayLevelSoundEvent;

final class SoundGameTests {
    private static final BlockPos POS = new BlockPos(1, 1, 1);

    static void chargingCompletion(GameTestHelper helper) {
        helper.setBlock(POS, FTBICElectricBlocks.CHARGE_PAD.block.get());
        var pad = helper.getBlockEntity(POS, ChargePadBlockEntity.class);
        ItemStack battery = new ItemStack(FTBICItems.LV_BATTERY.get());
        var item = (EnergyItemHandler) battery.getItem();
        item.setEnergy(battery, item.getEnergyCapacity(battery) - 100);
        pad.inputItems[0] = battery;
        try (Capture sounds = new Capture(helper, POS)) {
            pad.energy = 50;
            pad.tick();
            helper.assertTrue(
                    sounds.events.isEmpty(), "An exhausted charger must not announce a partial charge as complete");
            pad.energy = 50;
            pad.tick();
            helper.assertTrue(
                    sounds.events.equals(List.of(FTBICSounds.CHARGE_COMPLETE.get())),
                    "Finishing a charge plays exactly one completion cue, even when the pad empties");
            pad.energy = 100;
            pad.tick();
            helper.assertValueEqual(1, sounds.events.size(), "A full battery does not repeat the completion cue");
        }
        helper.succeed();
    }

    static void hotIdleReactor(GameTestHelper helper) {
        helper.setBlock(POS, FTBICElectricBlocks.NUCLEAR_REACTOR.block.get());
        var reactor = helper.getBlockEntity(POS, NuclearReactorBlockEntity.class);
        reactor.reactor.heat = 8000;
        reactor.reactor.maxHeat = 10000;
        reactor.reactor.energyOutput = 0;
        reactor.timeUntilNextCycle = 100;
        try (Capture sounds = new Capture(helper, POS)) {
            reactor.handleGeneration();
            reactor.handleGeneration();
            helper.assertTrue(
                    sounds.events.equals(List.of(FTBICSounds.REACTOR_WARNING.get())),
                    "A hot reactor warns while idle, and repeated updates do not overlap alarms");
        }
        helper.succeed();
    }

    static void criticalIdleReactor(GameTestHelper helper) {
        helper.setBlock(POS, FTBICElectricBlocks.NUCLEAR_REACTOR.block.get());
        var reactor = helper.getBlockEntity(POS, NuclearReactorBlockEntity.class);
        reactor.reactor.heat = 9500;
        reactor.reactor.maxHeat = 10000;
        reactor.reactor.energyOutput = 0;
        reactor.timeUntilNextCycle = 100;
        try (Capture sounds = new Capture(helper, POS)) {
            reactor.handleGeneration();
            helper.assertTrue(
                    sounds.events.equals(List.of(FTBICSounds.REACTOR_CRITICAL.get())),
                    "Critical heat uses the critical alarm instead of the ordinary warning");
        }
        helper.succeed();
    }

    private static final class Capture implements AutoCloseable {
        private final List<SoundEvent> events = new ArrayList<>();
        private final Consumer<PlayLevelSoundEvent.AtPosition> listener;

        private Capture(GameTestHelper helper, BlockPos pos) {
            BlockPos absolute = helper.absolutePos(pos);
            listener = event -> {
                if (event.getLevel() == helper.getLevel()
                        && BlockPos.containing(event.getPosition()).equals(absolute)
                        && event.getSound() != null) {
                    events.add(event.getSound().value());
                }
            };
            NeoForge.EVENT_BUS.addListener(PlayLevelSoundEvent.AtPosition.class, listener);
        }

        @Override
        public void close() {
            NeoForge.EVENT_BUS.unregister(listener);
        }
    }
}
