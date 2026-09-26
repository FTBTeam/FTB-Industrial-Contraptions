package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.CableBlock;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;
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
            int remaining = offered;
            BlockPos source = side == null ? null : pos.relative(side);
            BitSet filled = new BitSet();
            for (CableRouteCache.Endpoint endpoint :
                    CableRouteCache.get(level, pos, cable.tier).endpoints()) {
                if (filled.get(endpoint.receiver()) || endpoint.pos().equals(source)) continue;
                EnergyHandler receiver = endpoint.energy().getCapability();
                if (receiver == null) continue;
                int accepted = receiver.insert(remaining, transaction);
                if (accepted > 0) {
                    filled.set(endpoint.receiver());
                    remaining -= accepted;
                    if (endpoint.pulse() != null)
                        new TransferPulse(level, endpoint.pulse()).updateSnapshots(transaction);
                }
                if (remaining <= 0) break;
            }
            return offered - remaining;
        } finally {
            TRANSFERRING.remove(level);
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
