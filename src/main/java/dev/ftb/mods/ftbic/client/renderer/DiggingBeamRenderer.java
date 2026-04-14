package dev.ftb.mods.ftbic.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftbic.block.entity.machine.DiggingBaseBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.PumpBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;

/**
 * BlockEntityRenderer for Quarry + Pump. Renders an animated beacon-style beam straight down from
 * the machine through its mining column, giving players a visual cue of the active operation. Beam
 * is colour-coded: green for Quarry, blue for Pump. Disabled while {@code paused}.
 */
public class DiggingBeamRenderer implements BlockEntityRenderer<DiggingBaseBlockEntity, DiggingBeamRenderState> {
	private static final int COLOR_QUARRY = 0x33EE55;
	private static final int COLOR_PUMP = 0x3366FF;

	public DiggingBeamRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public DiggingBeamRenderState createRenderState() {
		return new DiggingBeamRenderState();
	}

	@Override
	public void extractRenderState(DiggingBaseBlockEntity be, DiggingBeamRenderState state, float partialTick,
			Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumbling) {
		BlockEntityRenderer.super.extractRenderState(be, state, partialTick, cameraPos, crumbling);
		state.paused = be.paused;
		// Beam reaches from BE.y down to ~bedrock; render in 32-block chunks to keep it visible.
		state.beamHeight = be.getBlockPos().getY() - (be.getLevel() == null ? 0 : be.getLevel().getMinY());
		state.colorRGB = be instanceof PumpBlockEntity ? COLOR_PUMP : COLOR_QUARRY;
		state.gameTime = be.getLevel() == null ? 0L : be.getLevel().getGameTime();
	}

	@Override
	public void submit(DiggingBeamRenderState state, PoseStack pose, SubmitNodeCollector buffers, CameraRenderState camera) {
		if (state.paused || state.beamHeight <= 0F) return;
		// Translate down by beamHeight so the beam's anchor sits at the bottom of the mining column,
		// then render upward through the BE block. submitBeaconBeam draws upward from the anchor.
		pose.pushPose();
		pose.translate(0F, -state.beamHeight, 0F);
		BeaconRenderer.submitBeaconBeam(pose, buffers, BeaconRenderer.BEAM_LOCATION,
				1.0F, 1.0F, (int) Math.ceil(state.beamHeight),
				state.colorRGB, (int) state.gameTime, 0.15F, 0.20F);
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
}
