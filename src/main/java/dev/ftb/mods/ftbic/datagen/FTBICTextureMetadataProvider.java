package dev.ftb.mods.ftbic.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.ftb.mods.ftbic.FTBIC;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

public class FTBICTextureMetadataProvider implements DataProvider {
    private final Path textures;

    public FTBICTextureMetadataProvider(PackOutput output) {
        textures = output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
                .resolve(FTBIC.MOD_ID)
                .resolve("textures");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        List<CompletableFuture<?>> saves = new ArrayList<>();
        for (String sprite : List.of("button", "button_disabled", "button_highlighted", "button_selected", "panel")) {
            JsonObject scaling = new JsonObject();
            scaling.addProperty("type", "nine_slice");
            scaling.addProperty("width", 16);
            scaling.addProperty("height", 16);
            scaling.addProperty("border", 3);
            JsonObject gui = new JsonObject();
            gui.add("scaling", scaling);
            save(output, saves, "gui/sprites/industrial/" + sprite, "gui", gui);
        }
        animation(output, saves, "block/nuke_side_active", 6, true);
        animation(output, saves, "block/tractor_beam", 2, false);
        animation(output, saves, "item/advanced_heat_vent", 2, true);
        animation(output, saves, "item/component_heat_vent", 3, true);
        animation(output, saves, "item/heat_vent", 4, true);
        animation(output, saves, "item/overclocked_heat_vent", 1, true);
        animation(output, saves, "item/reactor_heat_vent", 3, true);
        for (String axis : List.of("ns", "we")) {
            JsonArray frames = new JsonArray();
            frames.add(frame(0, 25));
            frames.add(frame(1, 2));
            JsonObject animation = new JsonObject();
            animation.add("frames", frames);
            save(output, saves, "block/landmark_" + axis, "animation", animation);
        }
        JsonArray frames = new JsonArray();
        for (int index = 0; index < 9; index++) frames.add(frame(index, 2));
        frames.add(frame(0, 24));
        JsonObject antimatter = new JsonObject();
        antimatter.add("frames", frames);
        save(output, saves, "item/antimatter", "animation", antimatter);

        JsonObject glass = new JsonObject();
        glass.addProperty("ctm_version", 1);
        glass.addProperty("type", "CTM");
        glass.addProperty("layer", "CUTOUT");
        JsonArray ctmTextures = new JsonArray();
        ctmTextures.add("ftbic:block/reinforced_glass_ctm");
        glass.add("textures", ctmTextures);
        save(output, saves, "block/reinforced_glass", "ctm", glass);
        return CompletableFuture.allOf(saves.toArray(CompletableFuture[]::new));
    }

    private void animation(
            CachedOutput output, List<CompletableFuture<?>> saves, String path, int time, boolean interpolate) {
        JsonObject animation = new JsonObject();
        animation.addProperty("frametime", time);
        if (interpolate) animation.addProperty("interpolate", true);
        save(output, saves, path, "animation", animation);
    }

    private static JsonObject frame(int index, int time) {
        JsonObject frame = new JsonObject();
        frame.addProperty("index", index);
        frame.addProperty("time", time);
        return frame;
    }

    private void save(
            CachedOutput output, List<CompletableFuture<?>> saves, String texture, String section, JsonObject data) {
        JsonObject metadata = new JsonObject();
        metadata.add(section, data);
        saves.add(DataProvider.saveStable(output, metadata, textures.resolve(texture + ".png.mcmeta")));
    }

    @Override
    public String getName() {
        return "FTBIC texture metadata";
    }
}
