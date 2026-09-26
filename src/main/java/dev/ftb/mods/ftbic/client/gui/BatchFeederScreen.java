package dev.ftb.mods.ftbic.client.gui;

import dev.ftb.mods.ftbic.block.entity.machine.BatchFeederBlockEntity;
import dev.ftb.mods.ftbic.net.BatchFluidPayload;
import dev.ftb.mods.ftbic.net.FTBICNet;
import dev.ftb.mods.ftbic.net.GhostSlotPayload;
import dev.ftb.mods.ftbic.screen.BatchFeederMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;

public class BatchFeederScreen extends ElectricBlockScreen<BatchFeederMenu> {
    private EditBox fluidAmount;
    private Button applyAmount;

    public BatchFeederScreen(BatchFeederMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 212);
        drawDefaultArrow = false;
    }

    @Override
    protected void init() {
        super.init();
        inventoryLabelY = 119;
        if (!(menu.blockEntity instanceof BatchFeederBlockEntity feeder)) return;
        for (int slot = 0; slot < BatchFeederBlockEntity.PATTERN_SIZE; slot++) {
            final int index = slot;
            addRenderableWidget(
                    new IndustrialButton(Button.builder(Component.translatable("ftbic.batch.slot", slot + 1), b -> {})
                            .bounds(leftPos + 125, topPos + 35 + slot * 18, 18, 18)) {
                        @Override
                        protected boolean isValidClickButton(MouseButtonInfo button) {
                            return button.button() == 0 || button.button() == 1;
                        }

                        @Override
                        public void onPress(InputWithModifiers input) {
                            int action = input.hasShiftDown()
                                    ? 1
                                    : input instanceof MouseButtonEvent mouse && mouse.button() == 1 ? 3 : 0;
                            FTBICNet.sendToServer(new GhostSlotPayload(menu.containerId, index, action));
                        }

                        @Override
                        protected void extractContents(
                                GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
                            drawSlot(g, getX(), getY());
                            var stack = feeder.getBatchItem(index);
                            if (!stack.isEmpty()) {
                                g.item(stack, getX() + 1, getY() + 1);
                                g.itemDecorations(font, stack, getX() + 1, getY() + 1);
                            }
                            g.fill(getX() + 1, getY() + 17, getX() + 17, getY() + 18, IndustrialGui.CYAN);
                            Component label = Component.translatable("ftbic.batch.slot", index + 1);
                            if (!stack.isEmpty())
                                label = label.copy()
                                        .append(": ")
                                        .append(stack.getHoverName())
                                        .append(" x" + stack.getCount());
                            setMessage(label);
                            setTooltip(Tooltip.create(
                                    label.copy().append("\n").append(Component.translatable("ftbic.batch.edit_hint"))));
                        }
                    });
        }
        addRenderableWidget(
                new IndustrialButton(Button.builder(
                                Component.translatable("ftbic.batch.fluid_buffer"),
                                b -> sendFluidAction(BatchFluidPayload.TRANSFER_CONTAINER, 0))
                        .bounds(leftPos + 8, topPos + 35, 18, 54)) {
                    @Override
                    protected void extractContents(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
                        var fluid = feeder.getBufferFluid();
                        drawTank(g, getX(), getY(), fluid, BatchFeederBlockEntity.TANK_CAPACITY);
                        Component name = fluid.isEmpty()
                                ? Component.translatable("ftbic.jade.fluid_empty")
                                : fluid.getHoverName();
                        setTooltip(Tooltip.create(Component.translatable(
                                "ftbic.batch.fluid_buffer_hint",
                                name,
                                fluid.getAmount(),
                                BatchFeederBlockEntity.TANK_CAPACITY)));
                    }
                });
        addRenderableWidget(
                new IndustrialButton(Button.builder(Component.translatable("ftbic.batch.fluid_ingredient"), b -> {})
                        .bounds(leftPos + 151, topPos + 35, 18, 54)) {
                    @Override
                    protected boolean isValidClickButton(MouseButtonInfo button) {
                        return button.button() == 0 || button.button() == 1;
                    }

                    @Override
                    public void onPress(InputWithModifiers input) {
                        boolean clear =
                                input.hasShiftDown() || input instanceof MouseButtonEvent mouse && mouse.button() == 1;
                        sendFluidAction(clear ? BatchFluidPayload.CLEAR : BatchFluidPayload.SELECT, 0);
                    }

                    @Override
                    protected void extractContents(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
                        var fluid = feeder.getBatchFluid();
                        drawTank(g, getX(), getY(), fluid, fluid.getAmount());
                        g.fill(getX() + 1, getY() + 53, getX() + 17, getY() + 54, IndustrialGui.CYAN);
                        Component name =
                                fluid.isEmpty() ? Component.translatable("ftbic.locks.unlocked") : fluid.getHoverName();
                        setTooltip(Tooltip.create(
                                Component.translatable("ftbic.batch.fluid_ingredient_hint", name, fluid.getAmount())));
                    }
                });
        fluidAmount = new EditBox(
                font, leftPos + 62, topPos + 95, 58, 18, Component.translatable("ftbic.batch.fluid_amount"));
        fluidAmount.setMaxLength(5);
        fluidAmount.setFilter(value -> value.matches("[0-9]{0,5}"));
        fluidAmount.setValue(
                feeder.getBatchFluid().isEmpty()
                        ? ""
                        : Integer.toString(feeder.getBatchFluid().getAmount()));
        fluidAmount.setTooltip(Tooltip.create(
                Component.translatable("ftbic.batch.fluid_amount_hint", BatchFeederBlockEntity.TANK_CAPACITY)));
        fluidAmount.active = !feeder.getBatchFluid().isEmpty();
        addRenderableWidget(fluidAmount);
        applyAmount =
                addRenderableWidget(Button.builder(Component.translatable("ftbic.batch.set"), b -> applyFluidAmount())
                        .bounds(leftPos + 126, topPos + 95, 42, 18)
                        .build(IndustrialButton::new));
        applyAmount.active = fluidAmount.active;
    }

    public Rect2i getBatchItemArea(int slot) {
        return new Rect2i(leftPos + 125, topPos + 35 + slot * 18, 18, 18);
    }

    public Rect2i getBatchFluidArea() {
        return new Rect2i(leftPos + 151, topPos + 35, 18, 54);
    }

    private void sendFluidAction(int action, int amount) {
        FTBICNet.sendToServer(new BatchFluidPayload(menu.containerId, action, amount));
    }

    private void applyFluidAmount() {
        if (!fluidAmount.getValue().isEmpty()) {
            int amount = Integer.parseInt(fluidAmount.getValue());
            if (amount >= 1 && amount <= BatchFeederBlockEntity.TANK_CAPACITY)
                sendFluidAction(BatchFluidPayload.SET_AMOUNT, amount);
        }
        setFocused(null);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (fluidAmount != null && menu.blockEntity instanceof BatchFeederBlockEntity feeder) {
            boolean selected = !feeder.getBatchFluid().isEmpty();
            fluidAmount.active = selected;
            applyAmount.active = selected;
            if (!fluidAmount.isFocused())
                fluidAmount.setValue(
                        selected ? Integer.toString(feeder.getBatchFluid().getAmount()) : "");
        }
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (fluidAmount != null && fluidAmount.isFocused()) {
            if (event.key() == GLFW.GLFW_KEY_ENTER || event.key() == GLFW.GLFW_KEY_KP_ENTER) {
                applyFluidAmount();
                return true;
            }
            if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
                setFocused(null);
                return true;
            }
            if (fluidAmount.keyPressed(event) || fluidAmount.canConsumeInput()) return true;
        }
        return super.keyPressed(event);
    }

    @Override
    protected void drawBase(GuiGraphicsExtractor g) {
        IndustrialGui.panel(g, leftPos, topPos, imageWidth, imageHeight);
        g.fill(leftPos + 4, topPos + 4, leftPos + 172, topPos + 19, IndustrialGui.HEADER);
        IndustrialGui.readout(g, leftPos + 27, topPos + 30, 63, 61);
        IndustrialGui.readout(g, leftPos + 120, topPos + 30, 28, 61);
        for (var slot : menu.slots) drawSlot(g, leftPos + slot.x - 1, topPos + slot.y - 1);
        g.text(
                font,
                Component.translatable("ftbic.batch.buffer"),
                leftPos + 31,
                topPos + 23,
                IndustrialGui.TEXT,
                false);
        g.text(
                font,
                Component.translatable("ftbic.batch.pattern"),
                leftPos + 119,
                topPos + 23,
                IndustrialGui.TEXT,
                false);
        g.text(
                font,
                Component.translatable("ftbic.batch.fluid_amount"),
                leftPos + 8,
                topPos + 100,
                IndustrialGui.TEXT,
                false);
        g.text(font, ">", leftPos + 101, topPos + 57, IndustrialGui.TEXT, false);
    }

    @Override
    protected void extractOverlayTooltips(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        super.extractOverlayTooltips(g, mouseX, mouseY);
        if (isIn(mouseX, mouseY, leftPos + 93, topPos + 35, 24, 53)) {
            g.setTooltipForNextFrame(Component.translatable("ftbic.batch.transfer_hint"), mouseX, mouseY);
        }
    }
}
