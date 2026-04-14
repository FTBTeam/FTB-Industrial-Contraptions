package dev.ftb.mods.ftbic.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

/**
 * Render state for {@link DiggingBeamRenderer} — captures whether the digging machine is currently
 * paused (no beam) and the depth of the bottom of the mining column relative to the BE.
 */
public class DiggingBeamRenderState extends BlockEntityRenderState {
	public boolean paused;
	public float beamHeight;
	public int colorRGB;
	public long gameTime;
}
