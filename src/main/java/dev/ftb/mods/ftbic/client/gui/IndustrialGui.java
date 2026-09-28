package dev.ftb.mods.ftbic.client.gui;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

/** Shared colors and framing for machine controls. */
final class IndustrialGui {
    static final int TEXT = 0xFF292E31;
    static final int MUTED = 0xFF41494D;
    static final int HEADER = 0xFFBEC6C6;
    static final int CYAN = 0xFF63CFD5;
    static final int READOUT = 0xFF22272B;
    static final int BORDER = 0xFF667373;
    private static final Identifier PANEL = FTBIC.id("industrial/panel");

    private IndustrialGui() {}

    static void panel(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, PANEL, x, y, width, height);
    }

    static void readout(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, TEXT);
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, BORDER);
        graphics.fill(x + 2, y + 2, x + width - 2, y + height - 2, READOUT);
    }
}
