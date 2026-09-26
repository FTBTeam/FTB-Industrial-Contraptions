package dev.ftb.mods.ftbic.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;

/** Immutable, relative-face settings. Zero preserves the machine's original transfer rules. */
public record SideConfiguration(int items, int fluids, int energy) {
    public static final SideConfiguration DEFAULT = new SideConfiguration(0, 0, 0);
    private static final Codec<Integer> PACKED = Codec.intRange(0, 262143).validate(value -> {
        for (int face = 0; face < 6; face++) {
            if ((value >> (face * 3) & 7) >= Mode.values().length) return DataResult.error(() -> "Invalid side mode");
        }
        return DataResult.success(value);
    });
    public static final Codec<SideConfiguration> CODEC = RecordCodecBuilder.create(i -> i.group(
                    PACKED.fieldOf("items").forGetter(SideConfiguration::items),
                    PACKED.fieldOf("fluids").forGetter(SideConfiguration::fluids),
                    PACKED.fieldOf("energy").forGetter(SideConfiguration::energy))
            .apply(i, SideConfiguration::new));

    public enum Resource {
        ITEMS,
        FLUIDS,
        ENERGY
    }

    public enum Mode {
        DEFAULT("*", 0xFFB0B0B0),
        DISABLED("X", 0xFFEE7777),
        INPUT("+", 0xFF66BBFF),
        OUTPUT("-", 0xFFFFBB55),
        BOTH("+/-", 0xFFBB88EE);
        public final String symbol;
        public final int color;

        Mode(String symbol, int color) {
            this.symbol = symbol;
            this.color = color;
        }

        public boolean allows(boolean input) {
            return this == DEFAULT || this == BOTH || this == (input ? INPUT : OUTPUT);
        }
    }

    public enum Face {
        FRONT,
        BACK,
        LEFT,
        RIGHT,
        TOP,
        BOTTOM;

        public Direction direction(Direction front) {
            Direction top = front.getAxis() == Direction.Axis.Y
                    ? (front == Direction.UP ? Direction.SOUTH : Direction.NORTH)
                    : Direction.UP;
            Direction right = front.getAxis() == Direction.Axis.Y ? Direction.WEST : front.getCounterClockWise();
            return switch (this) {
                case FRONT -> front;
                case BACK -> front.getOpposite();
                case LEFT -> right.getOpposite();
                case RIGHT -> right;
                case TOP -> top;
                case BOTTOM -> top.getOpposite();
            };
        }

        public static Face relative(Direction front, Direction side) {
            for (Face face : values()) if (face.direction(front) == side) return face;
            throw new IllegalArgumentException("Unmapped machine face");
        }
    }

    public Mode mode(Resource resource, Face face) {
        int packed =
                switch (resource) {
                    case ITEMS -> items;
                    case FLUIDS -> fluids;
                    case ENERGY -> energy;
                };
        int mode = packed >> (face.ordinal() * 3) & 7;
        return mode < Mode.values().length ? Mode.values()[mode] : Mode.DEFAULT;
    }

    public SideConfiguration with(Resource resource, Face face, Mode mode) {
        int shift = face.ordinal() * 3;
        int value =
                switch (resource) {
                    case ITEMS -> items;
                    case FLUIDS -> fluids;
                    case ENERGY -> energy;
                };
        value = (value & ~(7 << shift)) | (mode.ordinal() << shift);
        return switch (resource) {
            case ITEMS -> new SideConfiguration(value, fluids, energy);
            case FLUIDS -> new SideConfiguration(items, value, energy);
            case ENERGY -> new SideConfiguration(items, fluids, value);
        };
    }
}
