package dev.ftb.mods.ftbic.client;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class FTBICClientConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue MACHINE_SOUND_VOLUME;
    public static final ModConfigSpec.IntValue MAX_MACHINE_SOUNDS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("sounds");
        MACHINE_SOUND_VOLUME = builder.comment(
                        "Operating loop volume, in addition to the Blocks volume slider. Zero disables machine loops.")
                .defineInRange("machine_volume", 0.35D, 0D, 1D);
        MAX_MACHINE_SOUNDS = builder.comment(
                        "Maximum nearby machine loops playing at once. The nearest active machines take priority.")
                .defineInRange("max_machine_sounds", 8, 1, 32);
        builder.pop();
        SPEC = builder.build();
    }

    private FTBICClientConfig() {}
}
