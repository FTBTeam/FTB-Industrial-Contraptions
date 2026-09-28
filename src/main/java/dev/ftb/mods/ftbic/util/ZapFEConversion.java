package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.FTBICConfig;

public final class ZapFEConversion {
    private ZapFEConversion() {}

    public static double rate() {
        return FTBICConfig.ENERGY.ZAP_TO_FE_CONVERSION_RATE.get();
    }

    public static int zapsToFEFloor(double zaps) {
        double rate = rate();
        if (zaps <= 0D || rate <= 0D) return 0;
        double fe = zaps * rate;
        if (fe >= (double) Integer.MAX_VALUE) return Integer.MAX_VALUE;
        int floor = (int) Math.floor(fe);
        if (floor > 0 && floor / rate > zaps) return floor - 1;
        if (floor < Integer.MAX_VALUE && (floor + 1) / rate <= zaps) return floor + 1;
        return floor;
    }

    public static double feToZaps(long fe) {
        if (fe <= 0L) return 0D;
        return fe / rate();
    }
}
