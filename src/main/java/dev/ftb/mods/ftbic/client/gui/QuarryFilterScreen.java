package dev.ftb.mods.ftbic.client.gui;

import dev.ftb.mods.ftbic.block.entity.machine.QuarryBlockEntity;
import dev.ftb.mods.ftbic.net.FTBICNet;
import dev.ftb.mods.ftbic.net.QuarryFilterPayload;
import dev.ftb.mods.ftbic.screen.QuarryMenu;
import dev.ftb.mods.ftbic.util.QuarryFilter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class QuarryFilterScreen extends Screen {
    private final QuarryMenu menu;
    private final QuarryBlockEntity quarry;
    private final EditBox[] tagFields = new EditBox[QuarryFilter.TAG_SLOTS];
    private int left, top;
    private Button modeButton, oreButton;

    public QuarryFilterScreen(QuarryMenu menu, QuarryBlockEntity quarry) {
        super(Component.translatable("ftbic.quarry_filter.title"));
        this.menu = menu;
        this.quarry = quarry;
    }

    public QuarryMenu getMenu() {
        return menu;
    }

    public Rect2i getPanelArea() {
        return new Rect2i(left, top, 244, 206);
    }

    public Rect2i getBlockArea(int slot) {
        return new Rect2i(left + 14 + slot % 3 * 22, top + 43 + slot / 3 * 22, 18, 18);
    }

    private void send(int action, int slot, String value) {
        FTBICNet.sendToServer(new QuarryFilterPayload(menu.containerId, action, slot, value));
    }

    @Override
    protected void init() {
        left = (width - 244) / 2;
        top = (height - 206) / 2;
        for (int slot = 0; slot < QuarryFilter.BLOCK_SLOTS; slot++) {
            final int index = slot;
            Rect2i area = getBlockArea(slot);
            addRenderableWidget(
                    new IndustrialButton(Button.builder(Component.empty(), b -> {})
                            .bounds(area.getX(), area.getY(), area.getWidth(), area.getHeight())) {
                        @Override
                        protected boolean isValidClickButton(MouseButtonInfo button) {
                            return button.button() == 0 || button.button() == 1;
                        }

                        @Override
                        public void onPress(InputWithModifiers input) {
                            boolean clear = input.hasShiftDown()
                                    || input instanceof MouseButtonEvent mouse && mouse.button() == 1;
                            send(clear ? QuarryFilterPayload.CLEAR_BLOCK : QuarryFilterPayload.SET_BLOCK, index, "");
                        }

                        @Override
                        protected void extractContents(
                                GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
                            extractBackground(g);
                            var stack = quarry.getFilter().blockAt(index);
                            if (!stack.isEmpty()) g.item(stack, getX() + 1, getY() + 1);
                            setTooltip(Tooltip.create(
                                    stack.isEmpty()
                                            ? Component.translatable("ftbic.quarry_filter.empty_slot")
                                            : stack.getHoverName()));
                        }
                    });
        }
        for (int i = 0; i < tagFields.length; i++) {
            int slot = i;
            EditBox field = new EditBox(
                    font,
                    left + 92,
                    top + 39 + i * 26,
                    138,
                    18,
                    Component.translatable("ftbic.quarry_filter.tag", i + 1));
            field.setMaxLength(65);
            field.setValue(quarry.getFilter().tags().get(i));
            field.setTooltip(Tooltip.create(Component.translatable("ftbic.quarry_filter.tag_hint")));
            tagFields[i] = addRenderableWidget(field);
        }
        modeButton =
                addRenderableWidget(Button.builder(Component.empty(), b -> send(QuarryFilterPayload.TOGGLE_MODE, 0, ""))
                        .bounds(left + 12, top + 122, 106, 20)
                        .build(IndustrialButton::new));
        oreButton =
                addRenderableWidget(Button.builder(Component.empty(), b -> send(QuarryFilterPayload.TOGGLE_ORES, 0, ""))
                        .bounds(left + 126, top + 122, 106, 20)
                        .build(IndustrialButton::new));
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> closePanel())
                .bounds(left + 12, top + 177, 220, 18)
                .build(IndustrialButton::new));
    }

    private void commitTags() {
        for (int i = 0; i < tagFields.length; i++) {
            String value = tagFields[i].getValue().trim();
            if (!value.equals(quarry.getFilter().tags().get(i))) send(QuarryFilterPayload.SET_TAG, i, value);
        }
        setFocused(null);
    }

    private void closePanel() {
        commitTags();
        onClose();
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
            closePanel();
            return true;
        }
        if (event.key() == GLFW.GLFW_KEY_ENTER || event.key() == GLFW.GLFW_KEY_KP_ENTER) {
            commitTags();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public void tick() {
        if (minecraft.player == null || minecraft.player.containerMenu != menu || quarry.isRemoved()) {
            onClose();
            return;
        }
        var filter = quarry.getFilter();
        modeButton.setMessage(Component.translatable(
                filter.whitelist() ? "ftbic.quarry_filter.whitelist" : "ftbic.quarry_filter.blacklist"));
        oreButton.setMessage(Component.translatable(
                filter.oreOnly() ? "ftbic.quarry_filter.ores_on" : "ftbic.quarry_filter.ores_off"));
        for (int i = 0; i < tagFields.length; i++)
            if (!tagFields[i].isFocused()) {
                String saved = filter.tags().get(i);
                if (!tagFields[i].getValue().equals(saved)) tagFields[i].setValue(saved);
            }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        IndustrialGui.panel(g, left, top, 244, 206);
        g.fill(left + 4, top + 4, left + 240, top + 23, IndustrialGui.HEADER);
        g.text(font, title, left + 10, top + 9, IndustrialGui.TEXT, false);
        g.text(
                font,
                Component.translatable("ftbic.quarry_filter.blocks"),
                left + 13,
                top + 30,
                IndustrialGui.TEXT,
                false);
        g.text(
                font,
                Component.translatable("ftbic.quarry_filter.tags"),
                left + 92,
                top + 28,
                IndustrialGui.TEXT,
                false);
        g.text(
                font,
                Component.translatable(
                        quarry.hasFilterUpgrade() ? "ftbic.quarry_filter.active" : "ftbic.quarry_filter.inactive"),
                left + 12,
                top + 151,
                quarry.hasFilterUpgrade() ? IndustrialGui.CYAN : IndustrialGui.MUTED,
                false);
        super.extractRenderState(g, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
