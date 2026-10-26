package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.FTBIC;
import java.util.List;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class FTBICSoundProvider extends SoundDefinitionsProvider {
    public FTBICSoundProvider(PackOutput output) {
        super(output, FTBIC.MOD_ID);
    }

    @Override
    public void registerSounds() {
        add(
                FTBIC.id("radiation"),
                definition()
                        .subtitle("subtitles.ftbic.radiation")
                        .with(
                                sound("minecraft:ambient/nether/basalt_deltas/click2"),
                                sound("minecraft:ambient/nether/basalt_deltas/click5"),
                                sound("minecraft:ambient/nether/basalt_deltas/click7")));
        for (String sound : List.of("macerator", "compressor", "centrifuge", "ore_washer", "generator")) {
            register(sound, 16);
        }
        for (String sound : List.of(
                "reactor_warning",
                "reactor_critical",
                "nuke_arm",
                "nuke_tick",
                "charge_complete",
                "teleport",
                "nuclear_explosion")) {
            register(sound, 32);
        }
    }

    private void register(String name, int distance) {
        add(
                FTBIC.id(name),
                definition()
                        .subtitle("subtitles.ftbic." + name)
                        .with(sound(FTBIC.id(name)).attenuationDistance(distance)));
    }
}
