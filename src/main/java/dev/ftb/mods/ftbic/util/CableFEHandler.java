package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.CableBlock;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/** Stateless FE input: the receiver participates in the source's transaction. */
public final class CableFEHandler implements EnergyHandler {
    // External handlers may forward back into this network synchronously.
    private static final Set<ServerLevel> TRANSFERRING = new HashSet<>();
    private static final Map<ServerLevel, TickMemo> MEMOS = new WeakHashMap<>();
    private final ServerLevel level;
    private final BlockPos pos;
    private final Direction side;

    public CableFEHandler(ServerLevel level, BlockPos pos, Direction side) {
        this.level = level;
        this.pos = pos.immutable();
        this.side = side;
    }

    @Override
    public long getAmountAsLong() {
        return 0;
    }

    @Override
    public long getCapacityAsLong() {
        return level.getBlockState(pos).getBlock() instanceof CableBlock cable
                ? ZapFEConversion.zapsToFEFloor(cable.tier.transferRate())
                : 0;
    }

    @Override
    public int extract(int amount, TransactionContext transaction) {
        return 0;
    }

    @Override
    public int insert(int amount, TransactionContext transaction) {
        if (amount <= 0
                || !FTBICConfig.ENERGY.FULL_FE_MODE.get()
                || !(level.getBlockState(pos).getBlock() instanceof CableBlock cable)
                || !TRANSFERRING.add(level)) return 0;
        try {
            int offered = Math.min(amount, ZapFEConversion.zapsToFEFloor(cable.tier.transferRate()));
            if (offered <= 0) return 0;
            CableRouteCache.Route route = CableRouteCache.get(level, pos, cable.tier);
            TickMemo memo = memo(level);
            if (memo.saturated.contains(route)) return 0;
            int remaining = offered;
            BlockPos source = side == null ? null : pos.relative(side);
            boolean skippedSource = false;
            BitSet filled = new BitSet();
            for (CableRouteCache.Endpoint endpoint : route.endpoints()) {
                if (filled.get(endpoint.receiver())) continue;
                if (endpoint.pos().equals(source)) {
                    skippedSource = true;
                    continue;
                }
                long face = faceKey(endpoint);
                if (memo.full.contains(face)) continue;
                EnergyHandler receiver = endpoint.energy().getCapability();
                if (receiver == null) continue;
                int accepted = receiver.insert(remaining, transaction);
                if (accepted > 0) {
                    filled.set(endpoint.receiver());
                    remaining -= accepted;
                    if (endpoint.pulse() != null)
                        new TransferPulse(level, endpoint.pulse()).updateSnapshots(transaction);
                } else {
                    memo.full.add(face);
                }
                if (remaining <= 0) break;
            }
            if (remaining == offered && !skippedSource) memo.saturated.add(route);
            return offered - remaining;
        } finally {
            TRANSFERRING.remove(level);
        }
    }

    private static TickMemo memo(ServerLevel level) {
        long now = level.getGameTime();
        TickMemo memo = MEMOS.get(level);
        if (memo == null || memo.tick != now) {
            memo = new TickMemo(now);
            MEMOS.put(level, memo);
        }
        return memo;
    }

    private static long faceKey(CableRouteCache.Endpoint endpoint) {
        Direction face = endpoint.energy().context();
        return endpoint.pos().asLong() * 7L + (face == null ? 6 : face.ordinal());
    }

    private static final class TickMemo {
        private final long tick;
        private final Set<CableRouteCache.Route> saturated = Collections.newSetFromMap(new IdentityHashMap<>());
        private final LongOpenHashSet full = new LongOpenHashSet();

        private TickMemo(long tick) {
            this.tick = tick;
        }
    }

    private static final class TransferPulse extends SnapshotJournal<Boolean> {
        private final ServerLevel level;
        private final CableRouteCache.PulseNode pulse;

        private TransferPulse(ServerLevel level, CableRouteCache.PulseNode pulse) {
            this.level = level;
            this.pulse = pulse;
        }

        @Override
        protected Boolean createSnapshot() {
            return false;
        }

        @Override
        protected void revertToSnapshot(Boolean snapshot) {}

        @Override
        protected void onRootCommit(Boolean snapshot) {
            pulse.recordTransfer(level);
        }
    }
}
