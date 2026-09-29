package dev.ftb.mods.ftbic.block.entity.storage;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.BatteryBankBlock;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Coplanar exposed ports share rectangular screens. Cells never become display pixels. */
public final class BankDisplayLayout {
    public record Tile(int column, int row, int width, int height) {
        public float fillTop(float fraction) {
            return 0.25F + fraction * (height - 0.5F) - row;
        }
    }

    private BankDisplayLayout() {}

    public static Direction right(Direction face) {
        return switch (face) {
            case NORTH -> Direction.WEST;
            case EAST -> Direction.NORTH;
            case WEST -> Direction.SOUTH;
            default -> Direction.EAST;
        };
    }

    public static Direction up(Direction face) {
        return switch (face) {
            case UP -> Direction.NORTH;
            case DOWN -> Direction.SOUTH;
            default -> Direction.UP;
        };
    }

    public static boolean exposed(Level level, BlockPos pos, Direction face) {
        if (!level.isLoaded(pos)
                || !(level.getBlockEntity(pos) instanceof BankPortBlockEntity port)
                || port.getFaceStyle() != BankPortBlockEntity.FaceStyle.GAUGE) return false;
        return visible(level, pos, face);
    }

    public static boolean visible(Level level, BlockPos pos, Direction face) {
        BlockPos front = pos.relative(face);
        if (!level.isLoaded(front)) return false;
        BlockState cover = level.getBlockState(front);
        return !(cover.getBlock() instanceof BatteryBankBlock) && !cover.isSolidRender();
    }

    public static Map<BlockPos, Tile> find(Level level, BlockPos origin, Direction face) {
        Map<BlockPos, Tile> result = new HashMap<>();
        if (!exposed(level, origin, face)) return result;
        Direction right = right(face);
        Direction up = up(face);
        Direction[] steps = {right, right.getOpposite(), up, up.getOpposite()};
        Set<BlockPos> remaining = new HashSet<>();
        ArrayDeque<BlockPos> pending = new ArrayDeque<>();
        remaining.add(origin);
        pending.add(origin);
        int limit = FTBICConfig.ENERGY.BANK_MAX_BLOCKS.get();
        while (!pending.isEmpty()) {
            BlockPos pos = pending.removeFirst();
            for (Direction step : steps) {
                BlockPos next = pos.relative(step);
                if (remaining.size() < limit && !remaining.contains(next) && exposed(level, next, face)) {
                    remaining.add(next);
                    pending.add(next);
                }
            }
        }
        // Deterministic partition for L shapes and holes: never stretch a screen across a missing port.
        Comparator<BlockPos> order = Comparator.comparingInt((BlockPos pos) -> project(pos, up))
                .thenComparingInt(pos -> project(pos, right));
        while (!remaining.isEmpty()) {
            BlockPos bottomLeft = remaining.stream().min(order).orElseThrow();
            int width = 1;
            while (remaining.contains(bottomLeft.relative(right, width))) width++;
            int height = 1;
            boolean completeRow = true;
            while (completeRow) {
                for (int x = 0; x < width; x++) {
                    if (!remaining.contains(bottomLeft.relative(right, x).relative(up, height))) {
                        completeRow = false;
                        break;
                    }
                }
                if (completeRow) height++;
            }
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    BlockPos pos = bottomLeft.relative(right, x).relative(up, y);
                    remaining.remove(pos);
                    result.put(pos, new Tile(x, y, width, height));
                }
            }
        }
        return result;
    }

    private static int project(BlockPos pos, Direction direction) {
        return pos.getX() * direction.getStepX()
                + pos.getY() * direction.getStepY()
                + pos.getZ() * direction.getStepZ();
    }
}
