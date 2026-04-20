package dev.ftb.mods.ftbic.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.entity.machine.DiggingBaseBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.PumpBlockEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class DiggingBeamRenderer implements BlockEntityRenderer<DiggingBaseBlockEntity, DiggingBeamRenderState> {
	private static final Identifier FRAME_TEXTURE = FTBIC.id("textures/block/quarry_frame.png");
	private static final RenderType FRAME_RENDER_TYPE = RenderTypes.entityCutout(FRAME_TEXTURE);
	private static final int COLOR_QUARRY = 0xFF2020;
	private static final int COLOR_PUMP = 0x3366FF;
	private static final Set<Direction> ALL_FACES = EnumSet.allOf(Direction.class);

	private static final Identifier MODEL_TEXTURE = FTBIC.id("textures/block/quarry_model_light.png");
	private static final RenderType MODEL_RENDER_TYPE = RenderTypes.entityCutout(MODEL_TEXTURE);

	private final ModelPart frameWE;
	private final ModelPart frameSN;
	private final ModelPart postW;
	private final ModelPart postE;
	private final ModelPart postS;
	private final ModelPart postN;
	private final ModelPart cornerCap;
	private final ModelPart gantryBarWE;
	private final ModelPart gantryBarSN;
	private final ModelPart gantryBarEnd;
	private final ModelPart gantryHead;

	public DiggingBeamRenderer(BlockEntityRendererProvider.Context context) {
		frameWE = makeCube(0, 0, -8, -1, -1, 16, 2, 2, 64, 32);
		frameSN = makeCube(22, 2, -1, -1, -8, 2, 2, 16, 64, 32);
		postW = makeCube(0, 10, -8, -1, -1, 7, 2, 2, 64, 32);
		postE = makeCube(0, 5, 1, -1, -1, 7, 2, 2, 64, 32);
		postS = makeCube(19, 5, -1, -1, 1, 2, 2, 7, 64, 32);
		postN = makeCube(44, 5, -1, -1, -8, 2, 2, 7, 64, 32);
		cornerCap = makeCube(0, 10, -1.5F, -1.5F, -1.5F, 3, 3, 3, 64, 16);
		gantryBarWE = makeCube(0, 0, -8, -1, -1, 16, 2, 2, 64, 16);
		gantryBarSN = makeCube(0, 0, -1, -1, -8, 2, 2, 16, 64, 16);
		gantryBarEnd = makeCube(0, 10, -1.5F, -1.5F, -1.5F, 3, 3, 3, 64, 16);
		gantryHead = makeCube(32, 0, -4, -4, -4, 8, 8, 8, 64, 16);
	}

	private static ModelPart makeCube(int texU, int texV, float x, float y, float z, float w, float h, float d, int tw, int th) {
		ModelPart.Cube cube = new ModelPart.Cube(texU, texV, x, y, z, w, h, d, 0f, 0f, 0f, false, tw, th, ALL_FACES);
		return new ModelPart(List.of(cube), Collections.emptyMap());
	}

	@Override
	public DiggingBeamRenderState createRenderState() {
		return new DiggingBeamRenderState();
	}

	@Override
	public void extractRenderState(DiggingBaseBlockEntity be, DiggingBeamRenderState state, float partialTick,
			Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumbling) {
		BlockEntityRenderer.super.extractRenderState(be, state, partialTick, cameraPos, crumbling);
		state.paused = be.isEffectivelyPaused();
		state.beamHeight = be.getBlockPos().getY() - (be.getLevel() == null ? 0 : be.getLevel().getMinY());
		state.colorRGB = be instanceof PumpBlockEntity ? COLOR_PUMP : COLOR_QUARRY;
		state.gameTime = be.getLevel() == null ? 0L : be.getLevel().getGameTime();
		state.offsetX = be.offsetX;
		state.offsetZ = be.offsetZ;
		state.sizeX = be.sizeX;
		state.sizeZ = be.sizeZ;
		state.light = 15728880;
		state.laserX = be.laserX;
		state.laserZ = be.laserZ;
		state.laserY = be.laserY;
		state.quarryY = be.getBlockPos().getY();
	}

	@Override
	public void submit(DiggingBeamRenderState state, PoseStack pose, SubmitNodeCollector buffers, CameraRenderState camera) {
		if (state.sizeX > 0 && state.sizeZ > 0) {
			try {
				renderFrame(state, pose, buffers);
				renderGantry(state, pose, buffers);
			} catch (Throwable t) {
				FTBIC.LOGGER.error("renderFrame threw", t);
			}
		}
		if (!state.paused && state.laserY != Integer.MIN_VALUE && state.laserY < state.quarryY) {
			int fall = state.quarryY - state.laserY;
			pose.pushPose();
			pose.translate(state.laserX - 0.5D, 0.5D - fall, state.laserZ - 0.5D);
			float animationTime = Math.floorMod(state.gameTime, 40L);
			BeaconRenderer.submitBeaconBeam(pose, buffers, BeaconRenderer.BEAM_LOCATION,
					1.0F, animationTime, 0, fall,
					state.colorRGB, 0.15F, 0.20F);
			pose.popPose();
		}
	}

	private void renderGantry(DiggingBeamRenderState state, PoseStack pose, SubmitNodeCollector buffers) {
		int light = state.light;
		int overlay = OverlayTexture.NO_OVERLAY;

		double gNearX = state.offsetX + 0.5D;
		double gFarX = state.offsetX + state.sizeX - 0.5D;
		double gNearZ = state.offsetZ + 0.5D;
		double gFarZ = state.offsetZ + state.sizeZ - 0.5D;

		for (int x = 0; x < state.sizeX - 1; x++) {
			pose.pushPose();
			pose.translate(state.offsetX + x + 1.0D, 0.52D, state.laserZ);
			buffers.submitModelPart(gantryBarWE, pose, MODEL_RENDER_TYPE, light, overlay, null);
			pose.popPose();
		}

		for (int z = 0; z < state.sizeZ - 1; z++) {
			pose.pushPose();
			pose.translate(state.laserX, 0.52D, state.offsetZ + z + 1.0D);
			pose.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90F));
			buffers.submitModelPart(gantryBarWE, pose, MODEL_RENDER_TYPE, light, overlay, null);
			pose.popPose();
		}

		pose.pushPose(); pose.translate(gNearX, 0.52D, state.laserZ); buffers.submitModelPart(gantryBarEnd, pose, MODEL_RENDER_TYPE, light, overlay, null); pose.popPose();
		pose.pushPose(); pose.translate(gFarX, 0.52D, state.laserZ); buffers.submitModelPart(gantryBarEnd, pose, MODEL_RENDER_TYPE, light, overlay, null); pose.popPose();
		pose.pushPose(); pose.translate(state.laserX, 0.52D, gNearZ); buffers.submitModelPart(gantryBarEnd, pose, MODEL_RENDER_TYPE, light, overlay, null); pose.popPose();
		pose.pushPose(); pose.translate(state.laserX, 0.52D, gFarZ); buffers.submitModelPart(gantryBarEnd, pose, MODEL_RENDER_TYPE, light, overlay, null); pose.popPose();

		pose.pushPose(); pose.translate(gNearX, 0.5D, gNearZ); buffers.submitModelPart(gantryBarEnd, pose, MODEL_RENDER_TYPE, light, overlay, null); pose.popPose();
		pose.pushPose(); pose.translate(gFarX, 0.5D, gNearZ); buffers.submitModelPart(gantryBarEnd, pose, MODEL_RENDER_TYPE, light, overlay, null); pose.popPose();
		pose.pushPose(); pose.translate(gNearX, 0.5D, gFarZ); buffers.submitModelPart(gantryBarEnd, pose, MODEL_RENDER_TYPE, light, overlay, null); pose.popPose();
		pose.pushPose(); pose.translate(gFarX, 0.5D, gFarZ); buffers.submitModelPart(gantryBarEnd, pose, MODEL_RENDER_TYPE, light, overlay, null); pose.popPose();

		pose.pushPose();
		pose.translate(state.laserX, 0.52D, state.laserZ);
		buffers.submitModelPart(gantryHead, pose, MODEL_RENDER_TYPE, light, overlay, null);
		pose.popPose();
	}

	private void renderFrame(DiggingBeamRenderState state, PoseStack pose, SubmitNodeCollector buffers) {
		int light = state.light;
		int overlay = OverlayTexture.NO_OVERLAY;

		double nearX = state.offsetX + 0.5D;
		double nearZ = state.offsetZ + 0.5D;
		double farX = state.offsetX + state.sizeX - 0.5D;
		double farZ = state.offsetZ + state.sizeZ - 0.5D;

		renderRectangle(state, pose, buffers, 0.5D, light, overlay, nearX, nearZ, farX, farZ);
	}

	private void renderRectangle(DiggingBeamRenderState state, PoseStack pose, SubmitNodeCollector buffers,
			double y, int light, int overlay, double nearX, double nearZ, double farX, double farZ) {
		for (int x = 0; x < state.sizeX - 1; x++) {
			double xc = state.offsetX + x + 1.0D;
			pose.pushPose();
			pose.translate(xc, y, nearZ);
			buffers.submitModelPart(frameWE, pose, FRAME_RENDER_TYPE, light, overlay, null);
			pose.popPose();

			pose.pushPose();
			pose.translate(xc, y, farZ);
			buffers.submitModelPart(frameWE, pose, FRAME_RENDER_TYPE, light, overlay, null);
			pose.popPose();
		}

		for (int z = 0; z < state.sizeZ - 1; z++) {
			double zc = state.offsetZ + z + 1.0D;
			pose.pushPose();
			pose.translate(nearX, y, zc);
			buffers.submitModelPart(frameSN, pose, FRAME_RENDER_TYPE, light, overlay, null);
			pose.popPose();

			pose.pushPose();
			pose.translate(farX, y, zc);
			buffers.submitModelPart(frameSN, pose, FRAME_RENDER_TYPE, light, overlay, null);
			pose.popPose();
		}

		double cNearX = nearX + 0.5D;
		double cFarX = farX + 0.5D;
		double cNearZ = nearZ + 0.5D;
		double cFarZ = farZ + 0.5D;
		renderCornerCap(pose, buffers, cNearX, y, cNearZ, light, overlay);
		renderCornerCap(pose, buffers, cFarX, y, cNearZ, light, overlay);
		renderCornerCap(pose, buffers, cNearX, y, cFarZ, light, overlay);
		renderCornerCap(pose, buffers, cFarX, y, cFarZ, light, overlay);
	}

	private void renderCornerCap(PoseStack pose, SubmitNodeCollector buffers, double x, double y, double z,
			int light, int overlay) {
		pose.pushPose();
		pose.translate(x, y, z);
		buffers.submitModelPart(cornerCap, pose, FRAME_RENDER_TYPE, light, overlay, null);
		pose.popPose();
	}

	@Override
	public boolean shouldRenderOffScreen() {
		return true;
	}

	@Override
	public int getViewDistance() {
		return 256;
	}

	@Override
	public net.minecraft.world.phys.AABB getRenderBoundingBox(DiggingBaseBlockEntity be) {
		int qx = be.getBlockPos().getX();
		int qy = be.getBlockPos().getY();
		int qz = be.getBlockPos().getZ();
		int bottom = be.getLevel() == null ? qy - 1 : be.getLevel().getMinY();
		return new net.minecraft.world.phys.AABB(
				qx + be.offsetX - 0.5, bottom - 0.5, qz + be.offsetZ - 0.5,
				qx + be.offsetX + be.sizeX + 0.5, qy + 1.5, qz + be.offsetZ + be.sizeZ + 0.5);
	}
}
