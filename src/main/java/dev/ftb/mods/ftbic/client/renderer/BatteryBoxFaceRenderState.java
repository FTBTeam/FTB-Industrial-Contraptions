package dev.ftb.mods.ftbic.client.renderer;

import java.util.List;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class BatteryBoxFaceRenderState extends BlockEntityRenderState {
    public record Face(Direction direction, boolean output, int light) {}

    public List<Face> changedFaces = List.of();
    public String tier;
    public boolean dark;
}
