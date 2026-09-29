package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.CableBlock;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.block.entity.SuperconductingCableBlockEntity;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;

public final class CableRouteCache {
    private static final Map<ResourceKey<Level>, LevelRoutes> LEVELS = new ConcurrentHashMap<>();
    private static long scans;

    private CableRouteCache() {}

    public static long scanCount() {
        return scans;
    }

    public static void forget(Level level) {
        LEVELS.remove(level.dimension());
    }

    static Route get(ServerLevel level, BlockPos start, EnergyTier tier) {
        long network = ElectricBlockEntity.getCurrentElectricNetwork(level, start);
        LevelRoutes routes = LEVELS.get(level.dimension());
        if (routes == null || routes.level() != level || routes.network() != network) {
            routes = new LevelRoutes(level, network, new Long2ObjectOpenHashMap<>());
            LEVELS.put(level.dimension(), routes);
        }
        int maxLength = FTBICConfig.ENERGY.MAX_CABLE_LENGTH.get();
        Route route = routes.routes().get(start.asLong());
        if (route == null || !route.matches(level, tier, maxLength)) {
            route = scan(level, start, tier, maxLength);
            routes.routes().put(start.asLong(), route);
        }
        return route;
    }

    private static Route scan(ServerLevel level, BlockPos start, EnergyTier tier, int maxLength) {
        scans++;
        LongOpenHashSet loaded = new LongOpenHashSet();
        LongOpenHashSet unloaded = new LongOpenHashSet();
        Map<BlockPos, Integer> receivers = new HashMap<>();
        List<Endpoint> endpoints = new ArrayList<>();
        Set<BlockPos> visited = new HashSet<>();
        ArrayDeque<Step> queue = new ArrayDeque<>();
        queue.add(new Step(start, 1, null));
        visited.add(start);
        while (!queue.isEmpty()) {
            Step step = queue.removeFirst();
            if (step.distance() > maxLength || !isLoaded(level, step.pos(), loaded, unloaded)) continue;
            BlockState state = level.getBlockState(step.pos());
            PulseNode pulse = level.getBlockEntity(step.pos()) instanceof SuperconductingCableBlockEntity
                    ? new PulseNode(step.pos(), step.pulse())
                    : step.pulse();
            for (Direction direction : Direction.values()) {
                if (!state.getValue(CableBlock.CONNECTION[direction.ordinal()])) continue;
                BlockPos neighbor = step.pos().relative(direction);
                if (!isLoaded(level, neighbor, loaded, unloaded)) continue;
                if (level.getBlockState(neighbor).getBlock() instanceof CableBlock next) {
                    if (next.tier == tier && visited.add(neighbor)) {
                        queue.addLast(new Step(neighbor, step.distance() + 1, pulse));
                    }
                    continue;
                }
                int receiver = receivers.computeIfAbsent(neighbor, key -> receivers.size());
                endpoints.add(new Endpoint(
                        neighbor,
                        receiver,
                        BlockCapabilityCache.create(
                                Capabilities.Energy.BLOCK, level, neighbor, direction.getOpposite()),
                        pulse));
            }
        }
        return new Route(
                tier, maxLength, loaded.toLongArray(), unloaded.toLongArray(), endpoints.toArray(Endpoint[]::new));
    }

    private static boolean isLoaded(ServerLevel level, BlockPos pos, LongOpenHashSet loaded, LongOpenHashSet unloaded) {
        long chunk = ChunkPos.pack(pos);
        boolean present = hasChunk(level, chunk);
        (present ? loaded : unloaded).add(chunk);
        return present;
    }

    private static boolean hasChunk(ServerLevel level, long chunk) {
        return level.hasChunk(ChunkPos.getX(chunk), ChunkPos.getZ(chunk));
    }

    record Route(EnergyTier tier, int maxLength, long[] loadedChunks, long[] unloadedChunks, Endpoint[] endpoints) {
        boolean matches(ServerLevel level, EnergyTier tier, int maxLength) {
            if (this.tier != tier || this.maxLength != maxLength) return false;
            for (long chunk : loadedChunks) if (!hasChunk(level, chunk)) return false;
            for (long chunk : unloadedChunks) if (hasChunk(level, chunk)) return false;
            return true;
        }
    }

    record Endpoint(
            BlockPos pos, int receiver, BlockCapabilityCache<EnergyHandler, Direction> energy, PulseNode pulse) {}

    record PulseNode(BlockPos pos, PulseNode parent) {
        void recordTransfer(Level level) {
            for (PulseNode node = this; node != null; node = node.parent) {
                if (level.getBlockEntity(node.pos) instanceof SuperconductingCableBlockEntity cable)
                    cable.recordTransfer();
            }
        }
    }

    private record Step(BlockPos pos, int distance, PulseNode pulse) {}

    private record LevelRoutes(ServerLevel level, long network, Long2ObjectOpenHashMap<Route> routes) {}
}
