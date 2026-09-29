package dev.ftb.mods.ftbic.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.SprayPaintable;
import dev.ftb.mods.ftbic.block.entity.storage.BatteryBoxBlockEntity;
import dev.ftb.mods.ftbic.client.renderer.BatteryBoxFaceRenderState.Face;
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
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

/** Reuses each tier's existing input/output art on faces changed by side configuration. */
public class BatteryBoxFaceRenderer implements BlockEntityRenderer<BatteryBoxBlockEntity, BatteryBoxFaceRenderState> {
    private static final Map<String, RenderType> FACE_TEXTURES = new HashMap<>();

    public BatteryBoxFaceRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public BatteryBoxFaceRenderState createRenderState() {
        return new BatteryBoxFaceRenderState();
    }

    @Override
    public void extractRenderState(
            BatteryBoxBlockEntity box,
            BatteryBoxFaceRenderState state,
            float partialTick,
            Vec3 cameraPos,
            ModelFeatureRenderer.CrumblingOverlay crumbling) {
        BlockEntityRenderer.super.extractRenderState(box, state, partialTick, cameraPos, crumbling);
        state.tier = box.electricBlockInstance.id;
        state.dark = box.getBlockState().getValue(SprayPaintable.DARK);
        List<Face> changed = new ArrayList<>();
        Direction front = box.getFacing(Direction.NORTH);
        Level level = box.getLevel();
        for (Direction direction : Direction.values()) {
            boolean output = box.isValidEnergyOutputSide(direction);
            if (output != (direction == front)) {
                int light = state.lightCoords;
                BlockPos outside = box.getBlockPos().relative(direction);
                if (level != null && level.isLoaded(outside)) {
                    light = LightCoordsUtil.pack(
                            level.getBrightness(LightLayer.BLOCK, outside),
                            level.getBrightness(LightLayer.SKY, outside));
                }
                changed.add(new Face(direction, output, light));
            }
        }
        state.changedFaces = List.copyOf(changed);
    }

    @Override
    public void submit(
            BatteryBoxFaceRenderState state, PoseStack pose, SubmitNodeCollector buffers, CameraRenderState camera) {
        for (Face face : state.changedFaces) {
            String path = "textures/block/electric/" + (state.dark ? "dark/" : "light/") + state.tier
                    + (face.output() ? "_out.png" : "_in.png");
            RenderType texture = FACE_TEXTURES.computeIfAbsent(path, id -> RenderTypes.entityCutout(FTBIC.id(id)));
            buffers.submitCustomGeometry(
                    pose,
                    texture,
                    (transform, vertices) -> drawFace(vertices, transform, face.direction(), face.light()));
        }
    }

    private static void drawFace(VertexConsumer vertices, PoseStack.Pose pose, Direction direction, int light) {
        vertex(vertices, pose, direction, 0F, 0F, light);
        vertex(vertices, pose, direction, 1F, 0F, light);
        vertex(vertices, pose, direction, 1F, 1F, light);
        vertex(vertices, pose, direction, 0F, 1F, light);
    }

    private static void vertex(
            VertexConsumer vertices, PoseStack.Pose pose, Direction face, float u, float v, int light) {
        Direction right =
                switch (face) {
                    case NORTH -> Direction.WEST;
                    case EAST -> Direction.NORTH;
                    case WEST -> Direction.SOUTH;
                    default -> Direction.EAST;
                };
        Direction up =
                switch (face) {
                    case UP -> Direction.NORTH;
                    case DOWN -> Direction.SOUTH;
                    default -> Direction.UP;
                };
        float x = 0.5F + face.getStepX() * 0.501F + right.getStepX() * (u - 0.5F) + up.getStepX() * (v - 0.5F);
        float y = 0.5F + face.getStepY() * 0.501F + right.getStepY() * (u - 0.5F) + up.getStepY() * (v - 0.5F);
        float z = 0.5F + face.getStepZ() * 0.501F + right.getStepZ() * (u - 0.5F) + up.getStepZ() * (v - 0.5F);
        vertices.addVertex(pose, x, y, z)
                .setColor(0xFFFFFFFF)
                .setUv(u, 1F - v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, face.getStepX(), face.getStepY(), face.getStepZ());
    }
}
