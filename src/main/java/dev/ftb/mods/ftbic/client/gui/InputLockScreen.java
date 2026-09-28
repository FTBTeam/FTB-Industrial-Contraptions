package dev.ftb.mods.ftbic.client.gui;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.net.FTBICNet;
import dev.ftb.mods.ftbic.net.GhostSlotPayload;
import dev.ftb.mods.ftbic.screen.ElectricBlockMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;

public class InputLockScreen extends Screen {
    private final ElectricBlockMenu menu;
    private final ElectricBlockEntity machine;
    private int left, top, panelHeight;
    private int columns, rowHeight;

    public InputLockScreen(ElectricBlockMenu menu, ElectricBlockEntity machine) {
        super(Component.translatable("ftbic.locks.title"));
        this.menu = menu;
        this.machine = machine;
    }

    public ElectricBlockMenu getMenu() {
        return menu;
    }

    public Rect2i getPanelArea() {
        return new Rect2i(left, top, 244, panelHeight);
    }

    public Rect2i getInputArea(int slot) {
        return new Rect2i(
                left + 10 + slot % columns * 75,
                top + 28 + slot / columns * rowHeight,
                columns == 1 ? 224 : 74,
                rowHeight - 2);
    }

    @Override
    protected void init() {
        columns = machine.inputItems.length > 3 ? 3 : 1;
        rowHeight = columns == 1 ? 24 : 40;
        panelHeight = 96 + ((machine.inputItems.length + columns - 1) / columns) * rowHeight;
        left = (width - 244) / 2;
        top = (height - panelHeight) / 2;
        for (int slot = 0; slot < machine.inputItems.length; slot++) {
            final int index = slot;
            addRenderableWidget(
                    new IndustrialButton(Button.builder(Component.translatable("ftbic.locks.slot", slot + 1), b -> {})
                            .bounds(
                                    left + 10 + slot % columns * 75,
                                    top + 28 + slot / columns * rowHeight,
                                    columns == 1 ? 224 : 74,
                                    rowHeight - 2)) {
                        @Override
                        protected boolean isValidClickButton(MouseButtonInfo button) {
                            return button.button() == 0 || button.button() == 1;
                        }

                        @Override
                        public void onPress(InputWithModifiers input) {
                            boolean clear = input.hasShiftDown()
                                    || input instanceof MouseButtonEvent mouse && mouse.button() == 1;
                            FTBICNet.sendToServer(new GhostSlotPayload(menu.containerId, index, clear ? 1 : 0));
                        }

                        @Override
                        protected void extractContents(
                                GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
                            extractBackground(g);
                            var stack = machine.getInputLock(index);
                            if (!stack.isEmpty()) g.item(stack, getX() + 3, getY() + 3);
                            Component detail = stack.isEmpty()
                                    ? Component.translatable("ftbic.locks.unlocked")
                                    : stack.getHoverName();
                            Component label = Component.translatable("ftbic.locks.assignment", index + 1, detail);
                            String text = label.getString();
                            int maxWidth = columns == 1 ? 196 : 68;
                            if (font.width(text) > maxWidth)
                                text = font.plainSubstrByWidth(text, maxWidth - font.width("...")) + "...";
                            g.text(
                                    font,
                                    text,
                                    getX() + (columns == 1 ? 23 : 3),
                                    getY() + (columns == 1 ? 7 : 25),
                                    IndustrialGui.TEXT,
                                    false);
                            setMessage(label);
                            setTooltip(Tooltip.create(label));
                        }
                    });
        }
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> onClose())
                .bounds(left + 10, top + panelHeight - 26, 224, 18)
                .build(IndustrialButton::new));
    }

    @Override
    public void tick() {
        if (minecraft.player == null || minecraft.player.containerMenu != menu || machine.isRemoved()) onClose();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        IndustrialGui.panel(g, left, top, 244, panelHeight);
        g.fill(left + 4, top + 4, left + 240, top + 23, IndustrialGui.HEADER);
        g.text(font, title, left + 10, top + 9, IndustrialGui.TEXT, false);
        g.text(
                font,
                Component.translatable("ftbic.locks.assign_hint"),
                left + 10,
                top + panelHeight - 55,
                IndustrialGui.MUTED,
                false);
        g.text(
                font,
                Component.translatable("ftbic.locks.clear_hint"),
                left + 10,
                top + panelHeight - 43,
                IndustrialGui.MUTED,
                false);
        super.extractRenderState(g, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
