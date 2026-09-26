package dev.ftb.mods.ftbic.client.renderer;

import dev.ftb.mods.ftbic.block.entity.storage.BankDisplayLayout.Tile;
import java.util.List;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class BankDisplayRenderState extends BlockEntityRenderState {
    public record Panel(Direction face, Tile tile, int light) {}

    public record PortFace(Direction direction, int light) {}

    public List<Panel> panels = List.of();
    public List<PortFace> portFaces = List.of();
    public float charge;
}
