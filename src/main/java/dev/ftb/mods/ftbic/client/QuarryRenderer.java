package dev.ftb.mods.ftbic.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import dev.ftb.mods.ftbic.block.entity.machine.QuarryBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;

public class QuarryRenderer extends BlockEntityRenderer<QuarryBlockEntity> {
	private static final RenderType BEAM_1 = RenderType.beaconBeam(BeaconRenderer.BEAM_LOCATION, false);
	private static final RenderType BEAM_2 = RenderType.beaconBeam(BeaconRenderer.BEAM_LOCATION, true);

	public QuarryRenderer(BlockEntityRenderDispatcher arg) {
		super(arg);
	}

	@Override
	public void render(QuarryBlockEntity entity, float delta, PoseStack stack, MultiBufferSource source, int light1, int light2) {
		if (!entity.laserOn) {
			return;
		}

		double x = Mth.lerp(delta, entity.prevLaserX, entity.laserX);
		double z = Mth.lerp(delta, entity.prevLaserZ, entity.laserZ);

		stack.pushPose();
		stack.translate(x, entity.laserY, z);
		renderBeaconBeam(stack, source, delta, 1.0F, entity.getLevel().getGameTime(), 0, 1024, new float[]{1F, 0F, 0F}, 0.2F, 0.25F);
		stack.popPose();
	}

	public static void renderBeaconBeam(PoseStack arg, MultiBufferSource arg2, float delta, float g, long tick, int y, int height, float[] color, float h, float k) {
		int m = y + height;
		arg.pushPose();
		arg.translate(0.5D, 0.0D, 0.5D);
		float n = (float) Math.floorMod(-tick, 40L) + delta;
		float o = height < 0 ? n : -n;
		float p = Mth.frac(o * 0.2F - (float) Mth.floor(o * 0.1F));
		float q = color[0];
		float r = color[1];
		float s = color[2];
		arg.pushPose();
		arg.mulPose(Vector3f.YP.rotationDegrees(n * 2.25F - 45.0F));
		float af;
		float ai;
		float aj = -h;
		float aa = -h;
		float ap = -1.0F + p;
		float aq = (float) height * g * (0.5F / h) + ap;
		renderPart(arg, arg2.getBuffer(BEAM_1), q, r, s, 1.0F, y, m, 0.0F, h, h, 0.0F, aj, 0.0F, 0.0F, aa, 0.0F, 1.0F, aq, ap);
		arg.popPose();
		af = -k;
		float ag = -k;
		ai = -k;
		aj = -k;
		ap = -1.0F + p;
		aq = (float) height * g + ap;
		renderPart(arg, arg2.getBuffer(BEAM_2), q, r, s, 0.125F, y, m, af, ag, k, ai, aj, k, k, k, 0.0F, 1.0F, aq, ap);
		arg.popPose();
	}

	private static void renderPart(PoseStack arg, VertexConsumer arg2, float f, float g, float h, float i, int j, int k, float l, float m, float n, float o, float p, float q, float r, float s, float t, float u, float v, float w) {
		PoseStack.Pose lv = arg.last();
		Matrix4f lv2 = lv.pose();
		Matrix3f lv3 = lv.normal();
		renderQuad(lv2, lv3, arg2, f, g, h, i, j, k, l, m, n, o, t, u, v, w);
		renderQuad(lv2, lv3, arg2, f, g, h, i, j, k, r, s, p, q, t, u, v, w);
		renderQuad(lv2, lv3, arg2, f, g, h, i, j, k, n, o, r, s, t, u, v, w);
		renderQuad(lv2, lv3, arg2, f, g, h, i, j, k, p, q, l, m, t, u, v, w);
	}

	private static void renderQuad(Matrix4f arg, Matrix3f arg2, VertexConsumer arg3, float f, float g, float h, float i, int j, int k, float l, float m, float n, float o, float p, float q, float r, float s) {
		addVertex(arg, arg2, arg3, f, g, h, i, k, l, m, q, r);
		addVertex(arg, arg2, arg3, f, g, h, i, j, l, m, q, s);
		addVertex(arg, arg2, arg3, f, g, h, i, j, n, o, p, s);
		addVertex(arg, arg2, arg3, f, g, h, i, k, n, o, p, r);
	}

	private static void addVertex(Matrix4f arg, Matrix3f arg2, VertexConsumer arg3, float f, float g, float h, float i, int j, float k, float l, float m, float n) {
		arg3.vertex(arg, k, (float) j, l).color(f, g, h, i).uv(m, n).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(arg2, 0.0F, 1.0F, 0.0F).endVertex();
	}

	@Override
	public boolean shouldRenderOffScreen(QuarryBlockEntity arg) {
		return true;
	}
}
