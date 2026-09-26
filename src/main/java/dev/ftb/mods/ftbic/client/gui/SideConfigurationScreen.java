package dev.ftb.mods.ftbic.client.gui;

import dev.ftb.mods.ftbic.net.FTBICNet;
import dev.ftb.mods.ftbic.net.SideConfigurationPayload;
import dev.ftb.mods.ftbic.screen.ElectricBlockMenu;
import dev.ftb.mods.ftbic.util.SideConfiguration.Face;
import dev.ftb.mods.ftbic.util.SideConfiguration.Mode;
import dev.ftb.mods.ftbic.util.SideConfiguration.Resource;
import java.util.EnumMap;
import java.util.Locale;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

/** A modal GUI layer keeps the machine menu open and server-authoritative. */
public class SideConfigurationScreen extends Screen {
    private static final int PANEL_WIDTH = 280;
    private static final int PANEL_HEIGHT = 238;
    private static final int FACE_SIZE = 38;
    private static final int FACE_SPACING = 42;
    private static Resource lastSelected = Resource.ITEMS;
    private final ElectricBlockMenu menu;
    private Resource selected = lastSelected;
    private final EnumMap<Face, FaceButton> faces = new EnumMap<>(Face.class);
    private final EnumMap<Resource, IndustrialButton> tabs = new EnumMap<>(Resource.class);
    private int left, top;

    public SideConfigurationScreen(ElectricBlockMenu menu) {
        super(Component.translatable("ftbic.sides.title"));
        this.menu = menu;
        if (!menu.blockEntity.supportsResource(selected)) {
            for (Resource resource : Resource.values()) {
                if (menu.blockEntity.supportsResource(resource)) {
                    selected = resource;
                    break;
                }
            }
        }
    }

    private static Component name(Enum<?> value) {
        return Component.translatable("ftbic.sides." + value.name().toLowerCase(Locale.ROOT));
    }

    @Override
    protected void init() {
        left = (width - PANEL_WIDTH) / 2;
        top = (height - PANEL_HEIGHT) / 2;
        faces.clear();
        tabs.clear();
        int tab = 0;
        for (Resource resource : Resource.values()) {
            if (!menu.blockEntity.supportsResource(resource)) continue;
            var button = new IndustrialButton(Button.builder(name(resource), b -> {
                        selected = resource;
                        lastSelected = resource;
                        refresh();
                    })
                    .bounds(left + 10 + tab++ * 88, top + 29, 84, 20));
            tabs.put(resource, addRenderableWidget(button));
        }
        for (Face face : Face.values()) {
            // Viewed while facing the machine: front at the center, back at lower left.
            int column =
                    switch (face) {
                        case LEFT, BACK -> 0;
                        case TOP, FRONT, BOTTOM -> 1;
                        case RIGHT -> 2;
                    };
            int row =
                    switch (face) {
                        case TOP -> 0;
                        case LEFT, FRONT, RIGHT -> 1;
                        case BACK, BOTTOM -> 2;
                    };
            faces.put(
                    face,
                    addRenderableWidget(
                            new FaceButton(face, left + 10 + column * FACE_SPACING, top + 56 + row * FACE_SPACING)));
        }
        addRenderableWidget(Button.builder(
                        Component.translatable("ftbic.sides.reset"),
                        b -> FTBICNet.sendToServer(new SideConfigurationPayload(menu.containerId, -1, -1, -1)))
                .bounds(left + 10, top + 214, 166, 18)
                .tooltip(Tooltip.create(Component.translatable("ftbic.sides.reset_hint")))
                .build(IndustrialButton::new));
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> onClose())
                .bounds(left + 182, top + 214, 88, 18)
                .build(IndustrialButton::new));
        refresh();
    }

    private void configure(Face face, InputWithModifiers input) {
        if (input.hasShiftDown()) {
            setMode(face, Mode.DEFAULT);
            return;
        }
        var machine = menu.blockEntity;
        int current = machine.getSideConfiguration().mode(selected, face).ordinal();
        int step = input instanceof MouseButtonEvent mouse && mouse.button() == 1 ? -1 : 1;
        Mode[] modes = Mode.values();
        for (int offset = 1; offset <= modes.length; offset++) {
            Mode next = modes[Math.floorMod(current + step * offset, modes.length)];
            if (machine.supportsSideMode(selected, face, next)) {
                setMode(face, next);
                return;
            }
        }
    }

    private void setMode(Face face, Mode mode) {
        FTBICNet.sendToServer(
                new SideConfigurationPayload(menu.containerId, selected.ordinal(), face.ordinal(), mode.ordinal()));
    }

    private void refresh() {
        var machine = menu.blockEntity;
        for (var entry : tabs.entrySet()) entry.getValue().setSelected(entry.getKey() == selected);
        for (var entry : faces.entrySet()) {
            Face face = entry.getKey();
            Mode mode = machine.getSideConfiguration().mode(selected, face);
            Direction worldSide = face.direction(machine.getFacing(Direction.NORTH));
            entry.getValue().updateMode(mode, worldSide);
        }
    }

    @Override
    public void tick() {
        if (minecraft.player == null || minecraft.player.containerMenu != menu || menu.blockEntity.isRemoved()) {
            onClose();
            return;
        }
        refresh();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        IndustrialGui.panel(g, left, top, PANEL_WIDTH, PANEL_HEIGHT);
        g.fill(left + 4, top + 4, left + 276, top + 24, IndustrialGui.HEADER);
        g.fill(left + 8, top + 8, left + 10, top + 19, IndustrialGui.CYAN);
        g.text(font, title, left + 16, top + 10, IndustrialGui.TEXT, false);
        IndustrialGui.readout(g, left + 6, top + 52, 130, 130);
        g.fill(left + 70, top + 75, left + 72, top + 159, IndustrialGui.BORDER);
        g.fill(left + 29, top + 116, left + 113, top + 118, IndustrialGui.BORDER);
        drawLegend(g, mouseX, mouseY);
        g.text(
                font,
                Component.translatable("ftbic.sides.cycle_hint"),
                left + 10,
                top + 187,
                IndustrialGui.MUTED,
                false);
        g.text(
                font,
                Component.translatable("ftbic.sides.shift_hint"),
                left + 10,
                top + 199,
                IndustrialGui.MUTED,
                false);
        super.extractRenderState(g, mouseX, mouseY, partialTick);
    }

    private void drawLegend(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        g.text(font, Component.translatable("ftbic.sides.modes"), left + 148, top + 56, IndustrialGui.TEXT, false);
        int y = top + 73;
        for (Mode mode : Mode.values()) {
            boolean supported = false;
            for (Face face : Face.values()) supported |= menu.blockEntity.supportsSideMode(selected, face, mode);
            if (!supported) continue;
            g.fill(left + 148, y, left + 170, y + 13, IndustrialGui.READOUT);
            g.fill(left + 149, y + 1, left + 169, y + 12, mode.color);
            g.text(font, mode.symbol, left + 159 - font.width(mode.symbol) / 2, y + 2, IndustrialGui.TEXT, false);
            g.text(font, name(mode), left + 175, y + 2, IndustrialGui.TEXT, false);
            if (mouseX >= left + 148 && mouseX < left + 270 && mouseY >= y && mouseY < y + 13) {
                g.setTooltipForNextFrame(
                        mode == Mode.DEFAULT ? Component.translatable("ftbic.sides.default_hint") : name(mode),
                        mouseX,
                        mouseY);
            }
            y += 17;
        }
        g.text(
                font,
                Component.translatable("ftbic.sides.front_view"),
                left + 148,
                top + 168,
                IndustrialGui.MUTED,
                false);
    }

    private final class FaceButton extends IndustrialButton {
        private final Face face;
        private Mode mode = Mode.DEFAULT;
        private Direction direction;

        private FaceButton(Face face, int x, int y) {
            super(Button.builder(name(face), b -> {}).bounds(x, y, FACE_SIZE, FACE_SIZE));
            this.face = face;
        }

        private void updateMode(Mode mode, Direction direction) {
            if (this.mode == mode && this.direction == direction) return;
            this.mode = mode;
            this.direction = direction;
            Component description = Component.translatable(
                    "ftbic.sides.face_hint",
                    name(face),
                    Component.translatable(
                            "ftbic.sides.direction." + direction.name().toLowerCase(Locale.ROOT)),
                    name(mode));
            setMessage(description);
            setTooltip(Tooltip.create(description
                    .copy()
                    .append("\n")
                    .append(Component.translatable("ftbic.sides.cycle_hint"))
                    .append("\n")
                    .append(Component.translatable("ftbic.sides.shift_hint"))));
        }

        @Override
        protected boolean isValidClickButton(MouseButtonInfo button) {
            return button.button() == 0 || button.button() == 1;
        }

        @Override
        public void onPress(InputWithModifiers input) {
            configure(face, input);
        }

        @Override
        protected void extractContents(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
            extractBackground(g);
            g.fill(getX() + 4, getY() + 4, getX() + FACE_SIZE - 4, getY() + 23, IndustrialGui.READOUT);
            g.fill(getX() + 5, getY() + 5, getX() + FACE_SIZE - 5, getY() + 22, mode.color);
            g.text(
                    font,
                    mode.symbol,
                    getX() + FACE_SIZE / 2 - font.width(mode.symbol) / 2,
                    getY() + 9,
                    IndustrialGui.TEXT,
                    false);
            String label = name(face).getString();
            if (font.width(label) > FACE_SIZE - 6)
                label = font.plainSubstrByWidth(label, FACE_SIZE - 6 - font.width("...")) + "...";
            g.text(font, label, getX() + FACE_SIZE / 2 - font.width(label) / 2, getY() + 26, IndustrialGui.TEXT, false);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
