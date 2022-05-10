package dev.ftb.mods.ftbic.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.ElectricBlock;
import dev.ftb.mods.ftbic.block.entity.machine.DiggingBaseBlockEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.List;

public class DiggingBlockRenderer implements BlockEntityRenderer<DiggingBaseBlockEntity> {
	private static final ResourceLocation QUARRY_FRAME_TEXTURE = new ResourceLocation(FTBIC.MOD_ID, "textures/block/quarry_frame.png");
	private static final ResourceLocation QUARRY_MODEL_LIGHT_TEXTURE = new ResourceLocation(FTBIC.MOD_ID, "textures/block/quarry_model_light.png");
	private static final ResourceLocation QUARRY_MODEL_DARK_TEXTURE = new ResourceLocation(FTBIC.MOD_ID, "textures/block/quarry_model_dark.png");

	private final ModelPart quarryFrameWE;
	private final ModelPart quarryFrameSN;
	private final ModelPart quarryFrameW;
	private final ModelPart quarryFrameE;
	private final ModelPart quarryFrameS;
	private final ModelPart quarryFrameN;
	private final ModelPart quarryBar;
	private final ModelPart quarryBarEnd;
	private final ModelPart quarryHead;

	private static ModelPart make(BlockEntityRendererProvider.Context context, int tw, int th, int tx, int ty, float x, float y, float z, float w, float h, float d) {
		return new ModelPart(List.of(
				new ModelPart.Cube(tx, ty, x, y, z, w, h, d, 0f, 0f, 0f, false, tw, th)
		), Collections.emptyMap());
	}

	public DiggingBlockRenderer(BlockEntityRendererProvider.Context context) {
		quarryFrameWE = make(context, 64, 32, 0, 0, -8, -1, -1, 16, 2, 2);
		quarryFrameSN = make(context, 64, 32, 22, 2, -1, -1, -8, 2, 2, 16);
		quarryFrameW = make(context, 64, 32, 0, 10, -8, -1, -1, 7, 2, 2);
		quarryFrameE = make(context, 64, 32, 0, 5, 1, -1, -1, 7, 2, 2);
		quarryFrameS = make(context, 64, 32, 19, 5, -1, -1, 1, 2, 2, 7);
		quarryFrameN = make(context, 64, 32, 44, 5, -1, -1, -8, 2, 2, 7);
		quarryBar = make(context, 64, 16, 0, 0, -8, -1, -1, 16, 2, 2);
		quarryBarEnd = make(context, 64, 16, 0, 10, -1.5F, -1.5F, -1.5F, 3, 3, 3);
		quarryHead = make(context, 64, 16, 32, 0, -4, -4, -4, 8, 8, 8);
	}

	private int getLight(DiggingBaseBlockEntity entity, BlockPos.MutableBlockPos pos, double x, double y, double z) {
		Level level = entity.getLevel();

		if (level == null) {
			return 0;
		}

		pos.set(entity.getBlockPos().getX() + x, entity.getBlockPos().getY() + y, entity.getBlockPos().getZ() + z);

		/*
		int l = level.getBrightness(LightLayer.SKY, pos);
		return l << 20 | l << 4;
		 */

		return LevelRenderer.getLightColor(level, pos);
	}

	@Override
	public void render(DiggingBaseBlockEntity entity, float delta, PoseStack matrices, MultiBufferSource source, int light1, int overlay) {
		VertexConsumer frameConsumer = source.getBuffer(RenderType.entityCutout(QUARRY_FRAME_TEXTURE));
		BlockPos.MutableBlockPos lpos = new BlockPos.MutableBlockPos();

		for (int x = 0; x < entity.sizeX; x++) {
			matrices.pushPose();
			matrices.translate(entity.offsetX + x + 0.5D, 0.5D, entity.offsetZ - 0.5D);
			quarryFrameWE.render(matrices, frameConsumer, getLight(entity, lpos, entity.offsetX + x, 0.5D, entity.offsetZ - 0.5D), overlay);
			matrices.popPose();

			matrices.pushPose();
			matrices.translate(entity.offsetX + x + 0.5D, 0.5D, entity.offsetZ + 0.5D + entity.sizeZ);
			quarryFrameWE.render(matrices, frameConsumer, getLight(entity, lpos, entity.offsetX + x, 0.5D, entity.offsetZ + 0.5D + entity.sizeZ), overlay);
			matrices.popPose();
		}

		for (int z = 0; z < entity.sizeZ; z++) {
			matrices.pushPose();
			matrices.translate(entity.offsetX - 0.5D, 0.5D, entity.offsetZ + z + 0.5D);
			quarryFrameSN.render(matrices, frameConsumer, getLight(entity, lpos, entity.offsetX - 0.5D, 0.5D, entity.offsetZ + z), overlay);
			matrices.popPose();

			matrices.pushPose();
			matrices.translate(entity.offsetX + 0.5D + entity.sizeX, 0.5D, entity.offsetZ + z + 0.5D);
			quarryFrameSN.render(matrices, frameConsumer, getLight(entity, lpos, entity.offsetX + 0.5D + entity.sizeX, 0.5D, entity.offsetZ + z), overlay);
			matrices.popPose();
		}

		int lightNW = getLight(entity, lpos, entity.offsetX - 0.5D, 0.5D, entity.offsetZ - 0.5D);
		int lightNE = getLight(entity, lpos, entity.offsetX + 0.5D + entity.sizeX, 0.5D, entity.offsetZ - 0.5D);
		int lightSW = getLight(entity, lpos, entity.offsetX - 0.5D, 0.5D, entity.offsetZ + 0.5D + entity.sizeZ);
		int lightSE = getLight(entity, lpos, entity.offsetX + 0.5D + entity.sizeX, 0.5D, entity.offsetZ + 0.5D + entity.sizeZ);

		matrices.pushPose();
		matrices.translate(entity.offsetX - 0.5D, 0.5D, entity.offsetZ - 0.5D);
		quarryFrameS.render(matrices, frameConsumer, lightNW, overlay);
		quarryFrameE.render(matrices, frameConsumer, lightNW, overlay);
		matrices.popPose();

		matrices.pushPose();
		matrices.translate(entity.offsetX + 0.5D + entity.sizeX, 0.5D, entity.offsetZ - 0.5D);
		quarryFrameS.render(matrices, frameConsumer, lightNE, overlay);
		quarryFrameW.render(matrices, frameConsumer, lightNE, overlay);
		matrices.popPose();

		matrices.pushPose();
		matrices.translate(entity.offsetX - 0.5D, 0.5D, entity.offsetZ + 0.5D + entity.sizeZ);
		quarryFrameN.render(matrices, frameConsumer, lightSW, overlay);
		quarryFrameE.render(matrices, frameConsumer, lightSW, overlay);
		matrices.popPose();

		matrices.pushPose();
		matrices.translate(entity.offsetX + 0.5D + entity.sizeX, 0.5D, entity.offsetZ + 0.5D + entity.sizeZ);
		quarryFrameN.render(matrices, frameConsumer, lightSE, overlay);
		quarryFrameW.render(matrices, frameConsumer, lightSE, overlay);
		matrices.popPose();

		VertexConsumer modelConsumer = source.getBuffer(RenderType.entityCutout(entity.getBlockState().getValue(ElectricBlock.DARK) ? QUARRY_MODEL_DARK_TEXTURE : QUARRY_MODEL_LIGHT_TEXTURE));

		matrices.pushPose();
		matrices.translate(entity.offsetX - 0.5D, 0.5D, entity.offsetZ - 0.5D);
		quarryBarEnd.render(matrices, frameConsumer, lightNW, overlay);
		matrices.popPose();

		matrices.pushPose();
		matrices.translate(entity.offsetX + 0.5D + entity.sizeX, 0.5D, entity.offsetZ - 0.5D);
		quarryBarEnd.render(matrices, frameConsumer, lightNE, overlay);
		matrices.popPose();

		matrices.pushPose();
		matrices.translate(entity.offsetX - 0.5D, 0.5D, entity.offsetZ + 0.5D + entity.sizeZ);
		quarryBarEnd.render(matrices, frameConsumer, lightSW, overlay);
		matrices.popPose();

		matrices.pushPose();
		matrices.translate(entity.offsetX + 0.5D + entity.sizeX, 0.5D, entity.offsetZ + 0.5D + entity.sizeZ);
		quarryBarEnd.render(matrices, frameConsumer, lightSE, overlay);
		matrices.popPose();

		float lx = Mth.lerp(delta, entity.prevLaserX, entity.laserX);
		float ly = entity.laserY - entity.getBlockPos().getY();
		float lz = Mth.lerp(delta, entity.prevLaserZ, entity.laserZ);
		int headLight = getLight(entity, lpos, lx, 0.5D, lz);

		matrices.pushPose();
		matrices.translate(lx, 0.5D, lz);
		quarryHead.render(matrices, modelConsumer, headLight, overlay);
		matrices.popPose();

		quarryBar.yRot = 0F;

		for (int x = 0; x <= entity.sizeX; x++) {
			matrices.pushPose();
			matrices.translate(entity.offsetX + x, 0.5D, lz);
			quarryBar.render(matrices, modelConsumer, headLight, overlay);
			matrices.popPose();
		}

		quarryBar.yRot = (float) (Math.PI / 2D);

		for (int z = 0; z <= entity.sizeZ; z++) {
			matrices.pushPose();
			matrices.translate(lx, 0.5D, entity.offsetZ + z);
			quarryBar.render(matrices, modelConsumer, headLight, overlay);
			matrices.popPose();
		}

		matrices.pushPose();
		matrices.translate(entity.offsetX - 0.5D, 0.5D, lz);
		quarryBarEnd.render(matrices, modelConsumer, getLight(entity, lpos, entity.offsetX - 0.5D, 0.5D, lz), overlay);
		matrices.popPose();

		matrices.pushPose();
		matrices.translate(entity.offsetX + 0.5D + entity.sizeX, 0.5D, lz);
		quarryBarEnd.render(matrices, modelConsumer, getLight(entity, lpos, entity.offsetX + 0.5D + entity.sizeX, 0.5D, lz), overlay);
		matrices.popPose();

		matrices.pushPose();
		matrices.translate(lx, 0.5D, entity.offsetZ - 0.5D);
		quarryBarEnd.render(matrices, modelConsumer, getLight(entity, lpos, lx, 0.5D, entity.offsetZ - 0.5D), overlay);
		matrices.popPose();

		matrices.pushPose();
		matrices.translate(lx, 0.5D, entity.offsetZ + 0.5D + entity.sizeZ);
		quarryBarEnd.render(matrices, modelConsumer, getLight(entity, lpos, lx, 0.5D, entity.offsetZ + 0.5D + entity.sizeZ), overlay);
		matrices.popPose();

		if (!entity.paused) {
			matrices.pushPose();
			matrices.translate(lx, ly, lz);
			renderBeaconBeam(matrices, source, delta, 1.0F, entity.getLevel().getGameTime(), 0, -ly + 0.5F, entity.getLaserColor(), 0.2F, 0.25F);
			matrices.popPose();
		}
	}

	public static void renderBeaconBeam(PoseStack matrices, MultiBufferSource source, float delta, float g, long tick, int y, float height, float[] color, float h, float k) {
		float m = y + height;
		matrices.pushPose();
		float n = (float) Math.floorMod(-tick, 40L) + delta;
		float p = Mth.frac(n * 0.2F - (float) Mth.floor(n * 0.1F));
		float q = color[0];
		float r = color[1];
		float s = color[2];
		matrices.pushPose();
		matrices.mulPose(Vector3f.YP.rotationDegrees(45.0F));
		float af;
		float ai;
		float aj = -h;
		float aa = -h;
		float ap = -1.0F + p;
		float aq = height * g * (0.5F / h) + ap;
		renderPart(matrices, source.getBuffer(RenderType.beaconBeam(BeaconRenderer.BEAM_LOCATION, false)), q, r, s, 1.0F, y, m, 0.0F, h, h, 0.0F, aj, 0.0F, 0.0F, aa, 0.0F, 1.0F, aq, ap);
		matrices.popPose();
		af = -k;
		float ag = -k;
		ai = -k;
		aj = -k;
		ap = -1.0F + p;
		aq = height * g + ap;
		renderPart(matrices, source.getBuffer(RenderType.beaconBeam(BeaconRenderer.BEAM_LOCATION, true)), q, r, s, 0.125F, y, m, af, ag, k, ai, aj, k, k, k, 0.0F, 1.0F, aq, ap);
		matrices.popPose();
	}

	private static void renderPart(PoseStack matrices, VertexConsumer consumer, float f, float g, float h, float i, float j, float k, float l, float m, float n, float o, float p, float q, float r, float s, float t, float u, float v, float w) {
		PoseStack.Pose lv = matrices.last();
		Matrix4f mp = lv.pose();
		Matrix3f mn = lv.normal();
		renderQuad(mp, mn, consumer, f, g, h, i, j, k, l, m, n, o, t, u, v, w);
		renderQuad(mp, mn, consumer, f, g, h, i, j, k, r, s, p, q, t, u, v, w);
		renderQuad(mp, mn, consumer, f, g, h, i, j, k, n, o, r, s, t, u, v, w);
		renderQuad(mp, mn, consumer, f, g, h, i, j, k, p, q, l, m, t, u, v, w);
	}

	private static void renderQuad(Matrix4f mp, Matrix3f mn, VertexConsumer consumer, float f, float g, float h, float i, float j, float k, float l, float m, float n, float o, float p, float q, float r, float s) {
		addVertex(mp, mn, consumer, f, g, h, i, k, l, m, q, r);
		addVertex(mp, mn, consumer, f, g, h, i, j, l, m, q, s);
		addVertex(mp, mn, consumer, f, g, h, i, j, n, o, p, s);
		addVertex(mp, mn, consumer, f, g, h, i, k, n, o, p, r);
	}

	private static void addVertex(Matrix4f arg, Matrix3f arg2, VertexConsumer arg3, float f, float g, float h, float i, float j, float k, float l, float m, float n) {
		arg3.vertex(arg, k, j, l).color(f, g, h, i).uv(m, n).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(arg2, 0.0F, 1.0F, 0.0F).endVertex();
	}

	@Override
	public boolean shouldRenderOffScreen(DiggingBaseBlockEntity entity) {
		return true;
	}

	@Override
	public int getViewDistance() {
		return 256;
	}
}
