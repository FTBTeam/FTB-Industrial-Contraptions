package net.intothevoid.facingqa;

import com.google.gson.GsonBuilder;
import dev.ftb.mods.ftbic.block.*;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.util.SideConfiguration;
import java.nio.file.*;
import java.util.*;
import net.minecraft.commands.Commands;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod("itv_facing_qa")
public class FacingQA {
    private static volatile Direction requestedDefault = Direction.NORTH;
    private static volatile long blackhole;

    static class Machine extends ElectricBlockEntity {
        BlockState exposedState;
        Machine(ElectricBlockInstance type) { super(type, BlockPos.ZERO, type.block.get().defaultBlockState()); }
        @Override public BlockState getBlockState() { return exposedState != null ? exposedState : super.getBlockState(); }
    }

    static void check(boolean value, String name, List<String> checks) {
        if (!value) throw new AssertionError(name);
        checks.add(name);
    }

    static void invalid(Machine machine, List<String> checks, String name) {
        boolean threw = false;
        try { machine.getFacing(Direction.DOWN); } catch (IllegalArgumentException expected) { threw = true; }
        check(threw, name, checks);
    }

    static long poll(Machine[] machines, int count) {
        long sum = 0;
        for (int i = 0; i < count; i++) sum += machines[i & 255].getFacing(requestedDefault).ordinal();
        blackhole = sum;
        return sum;
    }

    public FacingQA() {
        if (!Boolean.getBoolean("itv.facing.fixture")) throw new IllegalStateException("Local fixture flag required");
        NeoForge.EVENT_BUS.addListener(this::commands);
    }

    void commands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("facingqa")
            .requires(s -> s.getEntity() == null && s.getServer().getLocalIp().equals("127.0.0.1"))
            .executes(ctx -> {
                var out = new LinkedHashMap<String,Object>(); var checks = new ArrayList<String>();
                try {
                    if (!ctx.getSource().getServer().getPlayerList().getPlayers().isEmpty()) throw new IllegalStateException("No players permitted");
                    var type = FTBICElectricBlocks.MACERATOR;
                    var machine = new Machine(type);
                    for (Direction facing : Direction.Plane.HORIZONTAL) {
                        machine.setBlockState(type.block.get().defaultBlockState().setValue(type.facingProperty, facing));
                        for (Direction fallback : Direction.values()) {
                            check(machine.getFacing(fallback) == facing, "rotation_" + facing + "_fallback_" + fallback, checks);
                            check(machine.getFacing(fallback) == facing, "repeat_" + facing + "_" + fallback, checks);
                        }
                    }
                    var previousProperty = type.facingProperty;
                    Direction previousFacing = machine.getBlockState().getValue(previousProperty);
                    try {
                        type.facingProperty = null;
                        for (Direction fallback : Direction.values()) check(machine.getFacing(fallback) == fallback, "null_property_" + fallback, checks);
                        type.facingProperty = BlockStateProperties.FACING;
                        invalid(machine, checks, "changed_property_throws");
                        invalid(machine, checks, "failed_lookup_not_cached");
                    } finally { type.facingProperty = previousProperty; }
                    check(machine.getFacing(Direction.DOWN) == previousFacing, "property_restore_returns_current_state", checks);
                    // Vanilla rejects incompatible setBlockState calls; exercise the documented fallback through an accessor override.
                    machine.exposedState = Blocks.STONE.defaultBlockState();
                    for (Direction fallback : Direction.values()) check(machine.getFacing(fallback) == fallback, "non_electric_state_" + fallback, checks);
                    machine.exposedState = null;
                    machine.setBlockState(type.block.get().defaultBlockState().setValue(type.facingProperty, Direction.SOUTH));
                    check(machine.getFacing(Direction.UP) == Direction.SOUTH, "electric_state_restore", checks);
                    var noRotation = new Machine(FTBICElectricBlocks.LV_SOLAR_PANEL);
                    for (Direction fallback : Direction.values()) check(noRotation.getFacing(fallback) == fallback, "no_rotation_" + fallback, checks);
                    boolean verticalTested = false;
                    for (var field : FTBICElectricBlocks.class.getFields()) {
                        if (field.getType() != ElectricBlockInstance.class) continue;
                        var vertical = (ElectricBlockInstance)field.get(null);
                        if (vertical.facingProperty != BlockStateProperties.FACING) continue;
                        var allSides = new Machine(vertical);
                        for (Direction facing : Direction.values()) {
                            allSides.setBlockState(vertical.block.get().defaultBlockState().setValue(vertical.facingProperty, facing));
                            check(allSides.getFacing(Direction.NORTH) == facing, "vertical_" + facing, checks);
                        }
                        verticalTested = true; break;
                    }
                    check(verticalTested, "six_way_machine_exercised", checks);
                    var overridden = new Machine(type) { @Override public Direction getFacing(Direction fallback) { return Direction.UP; } };
                    check(overridden.getFacing(Direction.SOUTH) == Direction.UP, "subclass_override_preserved", checks);
                    var modes = SideConfiguration.Mode.values();
                    for (var resource : SideConfiguration.Resource.values()) {
                        for (var face : SideConfiguration.Face.values()) {
                            for (int code = 0; code < 8; code++) {
                                int packed = code << (face.ordinal() * 3);
                                var config = new SideConfiguration(packed, packed, packed);
                                var expected = code < modes.length ? modes[code] : SideConfiguration.Mode.DEFAULT;
                                check(config.mode(resource, face) == expected, "mode_" + resource + "_" + face + "_" + code, checks);
                            }
                        }
                    }
                    Machine[] machines = new Machine[256]; int i = 0;
                    for (; i < machines.length; i++) {
                        machines[i] = new Machine(type);
                        machines[i].setBlockState(type.block.get().defaultBlockState().setValue(type.facingProperty, Direction.from2DDataValue(i % 4)));
                    }
                    long warmUntil = System.nanoTime() + 1_000_000_000L;
                    do { poll(machines, 100_000); } while (System.nanoTime() < warmUntil);
                    long[] nanos = new long[9];
                    for (int round = 0; round < nanos.length; round++) {
                        long started = System.nanoTime(); long sum = poll(machines, 5_000_000);
                        nanos[round] = System.nanoTime() - started;
                        check(sum == 17_500_000, "benchmark_result_" + round, checks);
                    }
                    out.put("benchmark_5M_facing_queries_nanos", nanos); out.put("phase", "passed");
                    var configurations = new SideConfiguration[256];
                    for (int c = 0; c < configurations.length; c++) configurations[c] = new SideConfiguration(0, 0, (c % 5) * 0x9249);
                    warmUntil = System.nanoTime() + 1_000_000_000L;
                    do { pollModes(configurations, 100_000); } while (System.nanoTime() < warmUntil);
                    long[] modeNanos = new long[9];
                    for (int round = 0; round < modeNanos.length; round++) {
                        long started = System.nanoTime(); long sum = pollModes(configurations, 5_000_000);
                        modeNanos[round] = System.nanoTime() - started;
                        check(sum == 9_960_936L, "mode_benchmark_result_" + round, checks);
                    }
                    out.put("benchmark_5M_mode_queries_nanos", modeNanos);
                } catch (Throwable error) { out.put("phase", "failed"); out.put("error", error.toString()); error.printStackTrace(); }
                out.put("checks", checks); out.put("count", checks.size()); out.put("production_actions", 0);
                try { Files.writeString(Path.of("facing-qa.json"), new GsonBuilder().setPrettyPrinting().create().toJson(out)); }
                catch (Exception error) { throw new RuntimeException(error); }
                ctx.getSource().sendSuccess(() -> Component.literal("FACING_QA " + out.get("phase")), false);
                return 1;
            }));
    }

    static long pollModes(SideConfiguration[] configurations, int count) {
        long sum = 0;
        for (int i = 0; i < count; i++) sum += configurations[i & 255].mode(SideConfiguration.Resource.ENERGY, SideConfiguration.Face.FRONT).ordinal();
        blackhole = sum;
        return sum;
    }
}
