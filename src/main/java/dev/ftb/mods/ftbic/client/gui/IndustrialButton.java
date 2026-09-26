package dev.ftb.mods.ftbic.client.gui;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/** A standard button with industrial framing, optional detail text and a mode indicator. */
class IndustrialButton extends Button {
    private static final Identifier NORMAL = FTBIC.id("industrial/button");
    private static final Identifier HIGHLIGHTED = FTBIC.id("industrial/button_highlighted");
    private static final Identifier SELECTED = FTBIC.id("industrial/button_selected");
    private static final Identifier DISABLED = FTBIC.id("industrial/button_disabled");
    private final Component heading;
    private Component detail = Component.empty();
    private boolean hasDetail;
    private boolean selected;
    private int indicator;

    IndustrialButton(Builder builder) {
        super(builder);
        heading = getMessage();
    }

    void setSelected(boolean selected) {
        this.selected = selected;
    }

    void setDetail(Component detail, int indicator) {
        this.detail = detail;
        this.indicator = indicator;
        hasDetail = true;
        setMessage(heading.copy().append(": ").append(detail));
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        extractBackground(graphics);
        extractLabel(graphics);
    }

    protected void extractBackground(GuiGraphicsExtractor graphics) {
        Identifier sprite = !active ? DISABLED : selected ? SELECTED : isHoveredOrFocused() ? HIGHLIGHTED : NORMAL;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, getX(), getY(), getWidth(), getHeight());
        if (selected && isFocused()) {
            graphics.fill(
                    getX() + 4,
                    getY() + getHeight() - 4,
                    getX() + getWidth() - 4,
                    getY() + getHeight() - 3,
                    IndustrialGui.CYAN);
        }
    }

    private void extractLabel(GuiGraphicsExtractor graphics) {
        int color = !active ? IndustrialGui.MUTED : selected ? IndustrialGui.CYAN : IndustrialGui.TEXT;
        if (hasDetail) {
            var font = Minecraft.getInstance().font;
            graphics.fill(getX() + 3, getY() + 3, getX() + 5, getY() + getHeight() - 3, indicator);
            graphics.enableScissor(getX() + 8, getY() + 2, getX() + getWidth() - 4, getY() + getHeight() - 2);
            graphics.text(font, heading, getX() + 9, getY() + 4, color, false);
            graphics.text(font, detail, getX() + 9, getY() + 17, IndustrialGui.MUTED, false);
            graphics.disableScissor();
        } else {
            setFGColor(color);
            extractScrollingStringOverContents(
                    graphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE),
                    getMessage()
                            .copy()
                            .withStyle(style -> style.withColor(color).withShadowColor(0)),
                    4);
        }
    }
}
