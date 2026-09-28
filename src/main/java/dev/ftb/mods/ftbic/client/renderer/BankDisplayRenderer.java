package dev.ftb.mods.ftbic.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.entity.storage.BankDisplayLayout;
import dev.ftb.mods.ftbic.block.entity.storage.BankDisplayLayout.Tile;
import dev.ftb.mods.ftbic.block.entity.storage.BankPortBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.BankPortBlockEntity.FaceStyle;
import dev.ftb.mods.ftbic.client.renderer.BankDisplayRenderState.Panel;
import dev.ftb.mods.ftbic.client.renderer.BankDisplayRenderState.PortFace;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

public class BankDisplayRenderer implements BlockEntityRenderer<BankPortBlockEntity, BankDisplayRenderState> {
    private static final int FULL_BRIGHT = 0xF000F0;
    private static final Identifier TEXTURE = FTBIC.id("textures/block/industrial_bank_display.png");
    private static final RenderType CASING = RenderTypes.entityCutout(TEXTURE);
    private static final RenderType EMISSIVE = RenderTypes.entityTranslucentEmissive(TEXTURE);
    private static final Identifier PORT_TEXTURE = FTBIC.id("textures/block/industrial_bank_port_mark.png");
    private static final RenderType PORT_MARK = RenderTypes.entityCutout(PORT_TEXTURE);

    private record FaceKey(BlockPos pos, Direction face) {}

    private final Map<FaceKey, Tile> layouts = new HashMap<>();
    private Level cachedLevel;
    private long cachedTick = Long.MIN_VALUE;

    public BankDisplayRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public BankDisplayRenderState createRenderState() {
        return new BankDisplayRenderState();
    }

    @Override
    public void extractRenderState(
            BankPortBlockEntity be,
            BankDisplayRenderState state,
            float partialTick,
            Vec3 cameraPos,
            ModelFeatureRenderer.CrumblingOverlay crumbling) {
        BlockEntityRenderer.super.extractRenderState(be, state, partialTick, cameraPos, crumbling);
        Level level = be.getLevel();
        state.panels = List.of();
        state.portFaces = List.of();
        state.charge = be.getDisplayCharge() / 1000F;
        if (level == null) return;
        if (be.getFaceStyle() == FaceStyle.PORT) {
            List<PortFace> faces = new ArrayList<>();
            for (Direction face : Direction.values()) {
                if (BankDisplayLayout.visible(level, be.getBlockPos(), face)) {
                    faces.add(new PortFace(face, faceLight(level, be.getBlockPos(), face)));
                }
            }
            state.portFaces = List.copyOf(faces);
            return;
        }
        if (be.getFaceStyle() == FaceStyle.BASIC) return;
        if (cachedLevel != level || cachedTick != level.getGameTime()) {
            layouts.clear();
            cachedLevel = level;
            cachedTick = level.getGameTime();
        }
        List<Panel> panels = new ArrayList<>();
        for (Direction face : Direction.values()) {
            FaceKey key = new FaceKey(be.getBlockPos(), face);
            if (!layouts.containsKey(key)) {
                BankDisplayLayout.find(level, be.getBlockPos(), face)
                        .forEach((pos, tile) -> layouts.put(new FaceKey(pos, face), tile));
                layouts.putIfAbsent(key, null);
            }
            Tile tile = layouts.get(key);
            if (tile != null) panels.add(new Panel(face, tile, faceLight(level, be.getBlockPos(), face)));
        }
        state.panels = List.copyOf(panels);
    }

    private static int faceLight(Level level, BlockPos pos, Direction face) {
        // The solid bank block is dark internally; overlays need the light just outside each face.
        BlockPos outside = pos.relative(face);
        return LightCoordsUtil.pack(
                level.getBrightness(LightLayer.BLOCK, outside), level.getBrightness(LightLayer.SKY, outside));
    }

    @Override
    public void submit(
            BankDisplayRenderState state, PoseStack pose, SubmitNodeCollector buffers, CameraRenderState camera) {
        // Each port draws only its own tile, preserving normal block-entity culling.
        List<Panel> panels = state.panels;
        if (!state.portFaces.isEmpty()) {
            List<PortFace> faces = state.portFaces;
            buffers.submitCustomGeometry(pose, PORT_MARK, (transform, vertices) -> {
                for (PortFace face : faces) {
                    portFace(vertices, transform, face.direction(), face.light());
                }
            });
        }
        float charge = state.charge;
        if (panels.isEmpty()) return;
        buffers.submitCustomGeometry(pose, CASING, (transform, vertices) -> {
            for (Panel panel : panels) {
                Tile tile = panel.tile();
                int light = panel.light();
                rect(
                        vertices,
                        transform,
                        panel.face(),
                        0.25F - tile.column(),
                        0.1875F - tile.row(),
                        tile.width() - 0.25F - tile.column(),
                        tile.height() - 0.1875F - tile.row(),
                        0.501F,
                        0,
                        light);
                rect(
                        vertices,
                        transform,
                        panel.face(),
                        0.3125F - tile.column(),
                        0.25F - tile.row(),
                        tile.width() - 0.3125F - tile.column(),
                        tile.height() - 0.25F - tile.row(),
                        0.502F,
                        1,
                        light);
            }
        });
        if (charge <= 0F) return;
        buffers.submitCustomGeometry(pose, EMISSIVE, (transform, vertices) -> {
            for (Panel panel : panels) {
                Tile tile = panel.tile();
                float left = 0.3125F - tile.column();
                float right = tile.width() - 0.3125F - tile.column();
                float bottom = 0.25F - tile.row();
                float top = tile.fillTop(charge);
                rect(vertices, transform, panel.face(), left, bottom, right, top, 0.503F, 2, FULL_BRIGHT);
                rect(
                        vertices,
                        transform,
                        panel.face(),
                        left,
                        Math.max(bottom, top - 0.0625F),
                        right,
                        top,
                        0.504F,
                        3,
                        FULL_BRIGHT);
            }
        });
    }

    private static void portFace(VertexConsumer vertices, PoseStack.Pose pose, Direction face, int light) {
        portVertex(vertices, pose, face, 0F, 0F, light);
        portVertex(vertices, pose, face, 1F, 0F, light);
        portVertex(vertices, pose, face, 1F, 1F, light);
        portVertex(vertices, pose, face, 0F, 1F, light);
    }

    private static void portVertex(
            VertexConsumer vertices, PoseStack.Pose pose, Direction face, float u, float v, int light) {
        Direction right = BankDisplayLayout.right(face);
        Direction up = BankDisplayLayout.up(face);
        float depth = 0.501F;
        float x = 0.5F + face.getStepX() * depth + right.getStepX() * (u - 0.5F) + up.getStepX() * (v - 0.5F);
        float y = 0.5F + face.getStepY() * depth + right.getStepY() * (u - 0.5F) + up.getStepY() * (v - 0.5F);
        float z = 0.5F + face.getStepZ() * depth + right.getStepZ() * (u - 0.5F) + up.getStepZ() * (v - 0.5F);
        vertices.addVertex(pose, x, y, z)
                .setColor(0xFFFFFFFF)
                .setUv(u, 1F - v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, face.getStepX(), face.getStepY(), face.getStepZ());
    }

    private static void rect(
            VertexConsumer vertices,
            PoseStack.Pose pose,
            Direction face,
            float left,
            float bottom,
            float right,
            float top,
            float depth,
            int swatch,
            int light) {
        left = Math.max(0F, left);
        bottom = Math.max(0F, bottom);
        right = Math.min(1F, right);
        top = Math.min(1F, top);
        if (left >= right || bottom >= top) return;
        vertex(vertices, pose, face, left, bottom, depth, swatch, light);
        vertex(vertices, pose, face, right, bottom, depth, swatch, light);
        vertex(vertices, pose, face, right, top, depth, swatch, light);
        vertex(vertices, pose, face, left, top, depth, swatch, light);
    }

    private static void vertex(
            VertexConsumer vertices,
            PoseStack.Pose pose,
            Direction face,
            float u,
            float v,
            float depth,
            int swatch,
            int light) {
        Direction right = BankDisplayLayout.right(face);
        Direction up = BankDisplayLayout.up(face);
        float x = 0.5F + face.getStepX() * depth + right.getStepX() * (u - 0.5F) + up.getStepX() * (v - 0.5F);
        float y = 0.5F + face.getStepY() * depth + right.getStepY() * (u - 0.5F) + up.getStepY() * (v - 0.5F);
        float z = 0.5F + face.getStepZ() * depth + right.getStepZ() * (u - 0.5F) + up.getStepZ() * (v - 0.5F);
        vertices.addVertex(pose, x, y, z)
                .setColor(0xFFFFFFFF)
                .setUv((swatch + 0.5F) / 4F, 0.5F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, face.getStepX(), face.getStepY(), face.getStepZ());
    }
}
