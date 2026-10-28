package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

final class FTBICMachineModels {
    private static final ModelTemplate ORIENTABLE_2D = new ModelTemplate(
            Optional.of(FTBIC.id("block/orientable_2d")),
            Optional.empty(),
            TextureSlot.SIDE,
            TextureSlot.FRONT,
            TextureSlot.TOP,
            TextureSlot.BOTTOM);
    private static final ModelTemplate ORIENTABLE_3D = new ModelTemplate(
            Optional.of(FTBIC.id("block/orientable_3d")), Optional.empty(), TextureSlot.SIDE, TextureSlot.FRONT);
    private static final Set<String> CUSTOM_TOP = Set.of(
            "centrifuge", "advanced_centrifuge", "compressor", "advanced_compressor", "reprocessor", "nuclear_reactor");

    private static final Set<String> LEGACY_MODELS = Set.of(
            "advanced_centrifuge",
            "advanced_compressor",
            "advanced_macerator",
            "advanced_powered_furnace",
            "antimatter_constructor",
            "basic_generator",
            "canning_machine",
            "centrifuge",
            "charge_pad",
            "compressor",
            "ev_battery_box",
            "ev_rectifier",
            "ev_solar_panel",
            "ev_transformer",
            "extruder",
            "geothermal_generator",
            "hv_battery_box",
            "hv_rectifier",
            "hv_solar_panel",
            "hv_transformer",
            "iv_rectifier",
            "lv_battery_box",
            "lv_rectifier",
            "lv_solar_panel",
            "lv_transformer",
            "macerator",
            "mv_battery_box",
            "mv_rectifier",
            "mv_solar_panel",
            "mv_transformer",
            "nuclear_reactor",
            "powered_crafting_table",
            "powered_furnace",
            "pump",
            "quarry",
            "reprocessor",
            "roller",
            "teleporter",
            "wind_mill");

    static void generate(BlockModelGenerators models, ElectricBlockInstance machine) {
        for (String color : List.of("light", "dark")) {
            for (String suffix : machine.canBeActive ? List.of("_off", "_on") : List.of("")) {
                generate(models, machine, color, suffix);
            }
        }
    }

    private static void generate(
            BlockModelGenerators models, ElectricBlockInstance machine, String color, String suffix) {
        String name = machine.id;
        String base =
                machine.advanced && !name.equals("charge_pad") && !name.equals("reprocessor") ? "advanced" : "basic";
        ModelTemplate template;
        TextureMapping textures = new TextureMapping();
        if (name.equals("reactor_simulator")) {
            template = ModelTemplates.CUBE_ALL;
            textures.put(TextureSlot.ALL, texture(color, "advanced_side"));
        } else if (machine.facingProperty == BlockStateProperties.FACING) {
            template = ORIENTABLE_3D;
            boolean battery = name.endsWith("battery_box");
            textures.put(TextureSlot.SIDE, texture(color, name + (battery ? "_in" : "_out")))
                    .put(TextureSlot.FRONT, texture(color, name + (battery ? "_out" : "_in")));
        } else {
            template = ORIENTABLE_2D;
            String side = name.equals("antimatter_constructor") ? name + "_side" : base + "_side";
            String front = name + "_front" + suffix;
            String top = CUSTOM_TOP.contains(name) ? name + "_top" : base + "_top";
            if (name.endsWith("solar_panel") || name.equals("powered_crafting_table")) {
                front = side;
                top = name + "_top";
            } else if (name.equals("charge_pad") || name.equals("teleporter")) {
                front = side;
                top = name + "_top" + suffix;
            } else if (name.endsWith("macerator")) {
                front = name + "_front";
                top = name + "_top" + suffix;
            }
            textures.put(TextureSlot.SIDE, texture(color, side))
                    .put(TextureSlot.FRONT, texture(color, front))
                    .put(TextureSlot.TOP, texture(color, top))
                    .put(TextureSlot.BOTTOM, texture(color, base + "_bottom"));
        }
        String path = "electric/" + color + "/" + name + suffix;
        template.create(FTBIC.id("block/" + path), textures, models.modelOutput);
        // Keep the original model paths available to resource packs.
        if (LEGACY_MODELS.contains(name)) template.create(FTBIC.id(path), textures, models.modelOutput);
    }

    private static Material texture(String color, String name) {
        return new Material(FTBIC.id("block/electric/" + color + "/" + name));
    }

    private FTBICMachineModels() {}
}
