package dev.ftb.mods.ftbic.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

/** The bundled pack overrides the normal blockstates only while ConTeX is present. */
public class FTBICContexProvider implements DataProvider {
    private final Path pack;

    public FTBICContexProvider(PackOutput output) {
        pack = output.getOutputFolder().resolve("contex_compat");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        List<CompletableFuture<?>> saves = new ArrayList<>();
        for (String block : List.of(
                "reinforced_stone",
                "reinforced_glass",
                "lv_reinforced_cable",
                "mv_reinforced_cable",
                "hv_reinforced_cable",
                "ev_reinforced_cable",
                "iv_reinforced_cable",
                "burnt_reinforced_cable")) {
            String texture = block.equals("reinforced_glass") ? "reinforced_glass" : "reinforced_stone";
            JsonObject definition = new JsonObject();
            definition.addProperty("neoforge:definition_type", "contex:definition");
            definition.addProperty("strategy", "contex:compact");
            JsonObject predicate = new JsonObject();
            predicate.addProperty("type", "contex:match_tag");
            predicate.addProperty("tag", "ftbic:reinforced");
            JsonObject meta = new JsonObject();
            meta.addProperty("type", "contex:full");
            if (!block.equals("reinforced_glass")) meta.add("predicate", predicate);
            meta.addProperty("occlusion_mode", "solid_or_self");
            meta.add("state_predicate", new JsonObject());
            JsonObject textures = new JsonObject();
            textures.addProperty("main_texture", "ftbic:block/" + texture);
            for (String edge : List.of("hor", "vert", "cross", "full")) {
                textures.addProperty("compact/" + edge, "ftbic:block/" + texture + "_" + edge);
            }
            JsonArray textureList = new JsonArray();
            textureList.add(textures);
            meta.add("textures", textureList);
            JsonArray metadata = new JsonArray();
            metadata.add(meta);
            definition.add("contex_meta", metadata);
            JsonObject model = new JsonObject();
            model.addProperty("model", "ftbic:block/" + block);
            JsonObject variants = new JsonObject();
            variants.add("", model);
            definition.add("variants", variants);
            saves.add(DataProvider.saveStable(
                    output, definition, pack.resolve("assets/ftbic/blockstates/" + block + ".json")));
        }
        JsonObject description = new JsonObject();
        description.addProperty(
                "description",
                "FTB Industrial Contraptions ConTeX compatibility: connected textures for reinforced stone, glass, and reinforced cables.");
        description.addProperty("pack_format", 84);
        JsonObject metadata = new JsonObject();
        metadata.add("pack", description);
        saves.add(DataProvider.saveStable(output, metadata, pack.resolve("pack.mcmeta")));
        return CompletableFuture.allOf(saves.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "FTBIC ConTeX compatibility pack";
    }
}
