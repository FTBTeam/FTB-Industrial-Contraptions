package net.intothevoid.ftbenergyqa;

import com.google.gson.GsonBuilder;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.util.*;

import net.minecraft.commands.Commands;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.transfer.energy.*;
import net.neoforged.neoforge.transfer.transaction.*;

import java.nio.file.*;
import java.util.*;

@Mod("itv_ftb_energy_qa")
public class FtbEnergyQA {
    private static volatile int benchmarkAmount = 50;

    static class Machine extends ElectricBlockEntity {
        long sideChecks;

        Machine() {
            super(
                    FTBICElectricBlocks.MACERATOR,
                    BlockPos.ZERO,
                    FTBICElectricBlocks.MACERATOR.block.get().defaultBlockState());
            energyCapacity = 10000;
            maxInputEnergy = 10000;
        }

        @Override
        public boolean allowsTransfer(SideConfiguration.Resource r, Direction side, boolean input) {
            sideChecks++;
            return super.allowsTransfer(r, side, input);
        }
    }

    static void check(boolean value, String name, List<String> checks) {
        if (!value) throw new AssertionError(name);
        checks.add(name);
    }

    static long insert(EnergyHandler h, int amount, boolean commit) {
        try (var tx = Transaction.openRoot()) {
            int n = h.insert(amount, tx);
            if (commit) tx.commit();
            return n;
        }
    }

    static long poll(EnergyHandler[] pollers, int repetitions) {
        long moved = 0;
        try (var tx = Transaction.openRoot()) {
            for (int i = 0; i < repetitions; i++)
                moved += pollers[i & 255].insert(benchmarkAmount, tx);
            tx.commit();
        }
        if (moved != 0) throw new AssertionError("Full polling accepted energy");
        return moved;
    }

    public FtbEnergyQA() {
        if (!Boolean.getBoolean("itv.ftbenergy.fixture"))
            throw new IllegalStateException("Local fixture flag required");
        NeoForge.EVENT_BUS.addListener(this::commands);
    }

    void commands(RegisterCommandsEvent event) {
        event.getDispatcher()
                .register(
                        Commands.literal("ftbenergyqa")
                                .requires(
                                        s ->
                                                s.getEntity() == null
                                                        && s.getServer()
                                                                .getLocalIp()
                                                                .equals("127.0.0.1"))
                                .executes(
                                        ctx -> {
                                            var out = new LinkedHashMap<String, Object>();
                                            var checks = new ArrayList<String>();
                                            try {
                                                if (!ctx.getSource()
                                                        .getServer()
                                                        .getPlayerList()
                                                        .getPlayers()
                                                        .isEmpty())
                                                    throw new IllegalStateException(
                                                            "No players permitted");
                                                for (Direction front : Direction.values())
                                                    for (Direction side : Direction.values()) {
                                                        SideConfiguration.Face expected = null;
                                                        for (var face :
                                                                SideConfiguration.Face.values())
                                                            if (face.direction(front) == side)
                                                                expected = face;
                                                        check(
                                                                SideConfiguration.Face.relative(
                                                                                front, side)
                                                                        == expected,
                                                                "face_" + front + "_" + side,
                                                                checks);
                                                    }
                                                var machine = new Machine();
                                                var storage =
                                                        new ElectricBlockEnergyHandler(machine);
                                                var sided =
                                                        new SidedEnergyHandler(
                                                                machine, Direction.NORTH, storage);
                                                machine.setEnergyRaw(machine.getEnergyCapacity());
                                                check(
                                                        insert(sided, 50, true) == 0
                                                                && machine.getEnergy() == 10000,
                                                        "full_rejects_without_energy_change",
                                                        checks);
                                                out.put("full_side_checks", machine.sideChecks);
                                                check(
                                                        !Boolean.getBoolean(
                                                                        "itv.ftbenergy.expectFast")
                                                                || machine.sideChecks == 0,
                                                        "fast_full_skips_side_queries",
                                                        checks);
                                                machine.setEnergyRaw(0);
                                                check(
                                                        insert(sided, 50, false) == 50
                                                                && machine.getEnergy() == 0,
                                                        "aborted_insert_restores_energy",
                                                        checks);
                                                check(
                                                        insert(sided, 50, true) == 50
                                                                && storage.getAmountAsLong() == 50,
                                                        "committed_insert_exact",
                                                        checks);
                                                machine.setEnergyRaw(0);
                                                try (var root = Transaction.openRoot()) {
                                                    try (var nested = Transaction.open(root)) {
                                                        check(
                                                                sided.insert(80, nested) == 80,
                                                                "nested_insert",
                                                                checks);
                                                        nested.commit();
                                                    }
                                                }
                                                check(
                                                        machine.getEnergy() == 0,
                                                        "parent_abort_restores_committed_child",
                                                        checks);
                                                machine.setEnergyRaw(10000);
                                                check(
                                                        insert(sided, 50, true) == 0,
                                                        "full_before_drain",
                                                        checks);
                                                machine.setEnergyRaw(0);
                                                check(
                                                        insert(sided, 50, true) == 50,
                                                        "fresh_query_accepts_immediately_after_drain",
                                                        checks);
                                                machine.setEnergyRaw(0);
                                                machine.setSideConfiguration(
                                                        new SideConfiguration(0, 0, 0x9249));
                                                check(
                                                        insert(sided, 50, true) == 0
                                                                && machine.getEnergy() == 0,
                                                        "disabled_sides_still_reject_nonfull",
                                                        checks);
                                                machine.setSideConfiguration(
                                                        SideConfiguration.DEFAULT);
                                                machine.maxInputEnergy = 0;
                                                check(
                                                        insert(sided, 50, true) == 0,
                                                        "zero_input_limit_preserved",
                                                        checks);
                                                machine.maxInputEnergy = 10000;
                                                machine.setEnergyRaw(10000);
                                                var forwarding = new SimpleEnergyHandler(1000);
                                                check(
                                                        insert(
                                                                                new SidedEnergyHandler(
                                                                                        machine,
                                                                                        Direction
                                                                                                .NORTH,
                                                                                        forwarding),
                                                                                30,
                                                                                true)
                                                                        == 30
                                                                && forwarding.getAmountAsLong()
                                                                        == 30,
                                                        "other_forwarding_handler_not_capacity_shortcut",
                                                        checks);
                                                var extended =
                                                        new ElectricBlockEnergyHandler(machine) {
                                                            @Override
                                                            public int insert(
                                                                    int amount,
                                                                    TransactionContext tx) {
                                                                return forwarding.insert(
                                                                        amount, tx);
                                                            }
                                                        };
                                                check(
                                                        insert(
                                                                                new SidedEnergyHandler(
                                                                                        machine,
                                                                                        Direction
                                                                                                .NORTH,
                                                                                        extended),
                                                                                20,
                                                                                true)
                                                                        == 20
                                                                && forwarding.getAmountAsLong()
                                                                        == 50,
                                                        "storage_subclass_override_preserved",
                                                        checks);
                                                EnergyHandler[] pollers = new EnergyHandler[256];
                                                for (int i = 0; i < pollers.length; i++) {
                                                    var pollingMachine = new Machine();
                                                    pollingMachine.energyCapacity = 10000 + i;
                                                    pollingMachine.setEnergyRaw(
                                                            pollingMachine.energyCapacity);
                                                    pollers[i] =
                                                            new SidedEnergyHandler(
                                                                    pollingMachine,
                                                                    Direction.NORTH,
                                                                    new ElectricBlockEnergyHandler(
                                                                            pollingMachine));
                                                }
                                                long warmUntil = System.nanoTime() + 1000000000L;
                                                int warmBatches = 0;
                                                do {
                                                    poll(pollers, 100000);
                                                    warmBatches++;
                                                } while (System.nanoTime() < warmUntil);
                                                long[] timings = new long[9];
                                                for (int round = 0;
                                                        round < timings.length;
                                                        round++) {
                                                    long began = System.nanoTime();
                                                    poll(pollers, 1000000);
                                                    timings[round] = System.nanoTime() - began;
                                                }
                                                out.put("benchmark_receivers", pollers.length);
                                                out.put("warmup_batches_100K", warmBatches);
                                                out.put("benchmark_full_poll_nanos_1M", timings);
                                                out.put("phase", "passed");
                                            } catch (Throwable error) {
                                                out.put("phase", "failed");
                                                out.put("error", error.toString());
                                                error.printStackTrace();
                                            }
                                            out.put("checks", checks);
                                            out.put("count", checks.size());
                                            out.put("production_actions", 0);
                                            try {
                                                Files.writeString(
                                                        Path.of("ftb-energy-qa.json"),
                                                        new GsonBuilder()
                                                                .setPrettyPrinting()
                                                                .create()
                                                                .toJson(out));
                                            } catch (Exception error) {
                                                throw new RuntimeException(error);
                                            }
                                            ctx.getSource()
                                                    .sendSuccess(
                                                            () ->
                                                                    Component.literal(
                                                                            "FTB_ENERGY_QA "
                                                                                    + out.get(
                                                                                            "phase")),
                                                            false);
                                            return 1;
                                        }));
    }
}
