package dev.ftb.mods.ftbic.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.BatteryBankBlock;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import java.util.List;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;

final class FTBICBankModels {
    static void generate(BlockModelGenerators models, ElectricBlockInstance bank) {
        String name = bank.id;
        String itemTexture = name + (name.endsWith("cell") ? "_0" : "_item");
        ModelTemplates.CUBE_ALL.create(
                FTBIC.id("block/" + name + "_item"),
                TextureMapping.cube(new Material(FTBIC.id("block/" + itemTexture))),
                models.modelOutput);
        var multipart = MultiPartGenerator.multiPart(bank.block.get());
        for (Direction face : Direction.values()) {
            List<Direction> edges = edges(face);
            for (int mask = 0; mask < 16; mask++) {
                Identifier id = FTBIC.id("block/" + name + "_" + face.getSerializedName() + "_" + mask);
                JsonObject model = faceModel(name + "_" + mask, itemTexture, face);
                models.modelOutput.accept(id, () -> model);
                var condition =
                        BlockModelGenerators.condition().term(BatteryBankBlock.CONNECTION[face.ordinal()], false);
                for (int edge = 0; edge < edges.size(); edge++) {
                    condition.term(BatteryBankBlock.CONNECTION[edges.get(edge).ordinal()], (mask & (1 << edge)) != 0);
                }
                multipart.with(condition, BlockModelGenerators.plainVariant(id));
            }
        }
        models.blockStateOutput.accept(multipart);
    }

    /** Clockwise texture edges are viewed from outside each face. */
    private static List<Direction> edges(Direction face) {
        return switch (face) {
            case DOWN -> List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
            case UP -> List.of(Direction.SOUTH, Direction.EAST, Direction.NORTH, Direction.WEST);
            case NORTH -> List.of(Direction.UP, Direction.WEST, Direction.DOWN, Direction.EAST);
            case SOUTH -> List.of(Direction.UP, Direction.EAST, Direction.DOWN, Direction.WEST);
            case WEST -> List.of(Direction.UP, Direction.SOUTH, Direction.DOWN, Direction.NORTH);
            case EAST -> List.of(Direction.UP, Direction.NORTH, Direction.DOWN, Direction.SOUTH);
        };
    }

    private static JsonObject faceModel(String texture, String particle, Direction face) {
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:block/block");
        model.addProperty("ambientocclusion", false);
        JsonObject textures = new JsonObject();
        textures.addProperty("face", "ftbic:block/" + texture);
        textures.addProperty("particle", "ftbic:block/" + particle);
        model.add("textures", textures);
        JsonObject element = new JsonObject();
        element.add("from", coordinates(0));
        element.add("to", coordinates(16));
        JsonObject faceData = new JsonObject();
        faceData.addProperty("texture", "#face");
        faceData.addProperty("cullface", face.getSerializedName());
        JsonObject faces = new JsonObject();
        faces.add(face.getSerializedName(), faceData);
        element.add("faces", faces);
        JsonArray elements = new JsonArray();
        elements.add(element);
        model.add("elements", elements);
        return model;
    }

    private static JsonArray coordinates(int value) {
        JsonArray coordinates = new JsonArray();
        for (int i = 0; i < 3; i++) coordinates.add(value);
        return coordinates;
    }

    private FTBICBankModels() {}
}
