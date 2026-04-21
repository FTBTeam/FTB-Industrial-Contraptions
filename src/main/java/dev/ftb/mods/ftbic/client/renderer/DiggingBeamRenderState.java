package dev.ftb.mods.ftbic.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

public class DiggingBeamRenderState extends BlockEntityRenderState {
	public boolean paused;
	public float beamHeight;
	public int colorRGB;
	public long gameTime;
	public int offsetX;
	public int offsetZ;
	public int sizeX;
	public int sizeZ;
	public int light;
	public float laserX;
	public float laserZ;
	public int laserY;
	public int quarryY;
}
