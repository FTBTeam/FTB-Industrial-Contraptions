package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.FTBICConfig;
import java.util.Locale;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class EnergyDisplay {
    private static Boolean syncedFullFE;
    private static double syncedRate;

    private EnergyDisplay() {}

    public static void sync(boolean fullFE, double rate) {
        syncedFullFE = fullFE;
        syncedRate = rate;
    }

    public static void clearSync() {
        syncedFullFE = null;
    }

    public static boolean isFE() {
        return syncedFullFE != null ? syncedFullFE : FTBICConfig.ENERGY.FULL_FE_MODE.get();
    }

    public static double conversionRate() {
        return syncedFullFE != null ? syncedRate : ZapFEConversion.rate();
    }

    public static double convert(double zaps) {
        return isFE() ? zaps * conversionRate() : zaps;
    }

    public static String number(double zaps) {
        return FTBICUtils.formatEnergyValue(convert(zaps));
    }

    public static String compactNumber(double zaps) {
        return EnergyItemHandler.formatEnergy(convert(zaps));
    }

    public static MutableComponent amount(double zaps) {
        return withAmountUnit(number(zaps));
    }

    public static MutableComponent perTick(double zaps) {
        return withRateUnit(number(zaps));
    }

    public static MutableComponent precisePerTick(double zaps) {
        double value = convert(zaps);
        String number = value == Math.floor(value) && Double.isFinite(value)
                ? FTBICUtils.formatEnergyValue(value)
                : String.format(Locale.ROOT, "%.2f", value);
        return withRateUnit(number);
    }

    public static MutableComponent compactAmount(double zaps) {
        return withAmountUnit(compactNumber(zaps));
    }

    public static MutableComponent compactPerTick(double zaps) {
        return withRateUnit(compactNumber(zaps));
    }

    private static MutableComponent withAmountUnit(String number) {
        return Component.translatable(isFE() ? "ftbic.unit.fe" : "ftbic.unit.zaps", number);
    }

    private static MutableComponent withRateUnit(String number) {
        return Component.translatable(isFE() ? "ftbic.unit.fe_per_tick" : "ftbic.unit.zaps_per_tick", number);
    }
}
