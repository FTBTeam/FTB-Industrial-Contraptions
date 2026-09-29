package dev.ftb.mods.ftbic.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.CableBlock;
import dev.ftb.mods.ftbic.block.entity.SuperconductingCableBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class SuperconductingCableRenderer
        implements BlockEntityRenderer<SuperconductingCableBlockEntity, SuperconductingCableRenderState> {
    private static final RenderType BEAM =
            RenderTypes.entityTranslucentEmissive(FTBIC.id("textures/block/superconducting_cable_pulse.png"));
    private static final int FULL_BRIGHT = 0xF000F0;

    public SuperconductingCableRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public boolean shouldRender(SuperconductingCableBlockEntity entity, Vec3 cameraPosition) {
        return entity.isGlowing() && BlockEntityRenderer.super.shouldRender(entity, cameraPosition);
    }

    @Override
    public SuperconductingCableRenderState createRenderState() {
        return new SuperconductingCableRenderState();
    }

    @Override
    public void extractRenderState(
            SuperconductingCableBlockEntity entity,
            SuperconductingCableRenderState state,
            float partialTick,
            Vec3 cameraPos,
            ModelFeatureRenderer.CrumblingOverlay crumbling) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTick, cameraPos, crumbling);
        state.connections = 0;
        state.strength = entity.pulseStrength(partialTick);
        if (state.strength <= 0F || entity.getLevel() == null) return;
        for (Direction direction : Direction.values()) {
            if (entity.getBlockState().getValue(CableBlock.CONNECTION[direction.ordinal()])) {
                state.connections |= 1 << direction.ordinal();
            }
        }
        // A two-second brightness cycle keeps neighboring segments pulsing together.
        double cycle = ((entity.getLevel().getGameTime() % 40) + partialTick) * Math.TAU / 40D;
        state.strength *= 0.7F + 0.3F * (float) Math.cos(cycle);
    }

    @Override
    public void submit(
            SuperconductingCableRenderState state,
            PoseStack pose,
            SubmitNodeCollector collector,
            CameraRenderState camera) {
        int connections = state.connections;
        int color = ((int) (state.strength * 255F) << 24) | 0xFFFFFF;
        if (connections == 0) return;
        collector.submitCustomGeometry(pose, BEAM, (transform, vertices) -> {
            for (Direction direction : Direction.values()) {
                if ((connections & (1 << direction.ordinal())) == 0) continue;
                float start = direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 0.5F : 0F;
                float end = start + 0.5F;
                beam(vertices, transform, direction.getAxis(), start, end, color);
            }
        });
    }

    private static void beam(
            VertexConsumer vertices, PoseStack.Pose pose, Direction.Axis axis, float start, float end, int color) {
        float radius = 1.05F / 16F;
        for (int side = 0; side < 4; side++) {
            float a0 = (side == 0 || side == 3) ? -radius : radius;
            float b0 = side < 2 ? -radius : radius;
            int next = (side + 1) % 4;
            float a1 = (next == 0 || next == 3) ? -radius : radius;
            float b1 = next < 2 ? -radius : radius;
            if (axis == Direction.Axis.Y) {
                vertex(vertices, pose, axis, start, a0, b0, 0F, color);
                vertex(vertices, pose, axis, end, a0, b0, 0F, color);
                vertex(vertices, pose, axis, end, a1, b1, 1F, color);
                vertex(vertices, pose, axis, start, a1, b1, 1F, color);
            } else {
                vertex(vertices, pose, axis, start, a1, b1, 1F, color);
                vertex(vertices, pose, axis, end, a1, b1, 1F, color);
                vertex(vertices, pose, axis, end, a0, b0, 0F, color);
                vertex(vertices, pose, axis, start, a0, b0, 0F, color);
            }
        }
    }

    private static void vertex(
            VertexConsumer vertices,
            PoseStack.Pose pose,
            Direction.Axis axis,
            float along,
            float a,
            float b,
            float v,
            int color) {
        float x = axis == Direction.Axis.X ? along : 0.5F + a;
        float y = axis == Direction.Axis.Y ? along : 0.5F + (axis == Direction.Axis.X ? a : b);
        float z = axis == Direction.Axis.Z ? along : 0.5F + b;
        // Sample the bright center strip uniformly along the beam; only opacity animates.
        vertices.addVertex(pose, x, y, z)
                .setColor(color)
                .setUv(0.5F, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(FULL_BRIGHT)
                .setNormal(pose, 0F, 1F, 0F);
    }
}
