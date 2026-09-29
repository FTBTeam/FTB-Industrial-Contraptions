package dev.ftb.mods.ftbic.block.entity.storage;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.BatteryBankBlock;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/** Bounded traversal; storage belongs to each cell and survives topology changes. */
public final class BankTopology {
    public record Snapshot(double stored, double capacity, int cells, int ports) {}

    private BankTopology() {}

    public static int chargeLevel(Snapshot snapshot) {
        if (snapshot.capacity() <= 0D || snapshot.stored() <= 0D) return 0;
        return Math.max(1, Math.min(1000, (int) Math.round(1000D * snapshot.stored() / snapshot.capacity())));
    }

    public static Snapshot snapshot(Level level, BlockPos origin) {
        return snapshot(members(level, origin));
    }

    public static Snapshot snapshot(List<ElectricBlockEntity> members) {
        double stored = 0D;
        double capacity = 0D;
        int cells = 0;
        int ports = 0;
        for (ElectricBlockEntity member : members) {
            stored += member.getEnergy();
            capacity += member.getEnergyCapacity();
            if (member instanceof BankCellBlockEntity) cells++;
            if (member instanceof BankPortBlockEntity) ports++;
        }
        return new Snapshot(stored, capacity, cells, ports);
    }

    public static List<BankCellBlockEntity> cells(Level level, BlockPos origin) {
        return cells(members(level, origin));
    }

    public static List<BankCellBlockEntity> cells(List<ElectricBlockEntity> members) {
        List<BankCellBlockEntity> cells = new ArrayList<>();
        for (ElectricBlockEntity member : members) {
            if (member instanceof BankCellBlockEntity cell) cells.add(cell);
        }
        return cells;
    }

    public static List<ElectricBlockEntity> members(Level level, BlockPos origin) {
        List<ElectricBlockEntity> members = new ArrayList<>();
        ArrayDeque<BlockPos> pending = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();
        pending.add(origin);
        visited.add(origin);
        int limit = FTBICConfig.ENERGY.BANK_MAX_BLOCKS.get();
        while (!pending.isEmpty()) {
            BlockPos pos = pending.removeFirst();
            if (!level.isLoaded(pos) || !(level.getBlockState(pos).getBlock() instanceof BatteryBankBlock)) continue;
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof BankCellBlockEntity || entity instanceof BankPortBlockEntity) {
                members.add((ElectricBlockEntity) entity);
            }
            for (Direction direction : Direction.values()) {
                BlockPos next = pos.relative(direction);
                if (visited.size() < limit
                        && level.isLoaded(next)
                        && level.getBlockState(next).getBlock() instanceof BatteryBankBlock
                        && visited.add(next)) pending.add(next);
            }
        }
        return members;
    }
}
