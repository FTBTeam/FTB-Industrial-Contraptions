package dev.ftb.mods.ftbic.client.gui;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.screen.BatterySlot;
import dev.ftb.mods.ftbic.screen.ChargeSlot;
import dev.ftb.mods.ftbic.screen.ElectricBlockMenu;
import dev.ftb.mods.ftbic.screen.PickaxeSlot;
import dev.ftb.mods.ftbic.screen.UpgradeSlot;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import dev.ftb.mods.ftbic.util.SideConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.fluids.FluidStack;

public class ElectricBlockScreen<T extends ElectricBlockMenu> extends AbstractContainerScreen<T> {
    public static final Identifier BASE_TEXTURE = FTBIC.id("textures/gui/base.png");

    protected int energyX = -1;
    protected int energyY = -1;
    protected int energyType = 0;

    protected boolean drawDefaultArrow = true;

    public ElectricBlockScreen(T menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    public ElectricBlockScreen(T menu, Inventory inventory, Component title, int width, int height) {
        super(menu, inventory, title, width, height);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 8;
        if (menu.blockEntity != null) {
            if (menu.blockEntity.supportsInputLocks()) {
                addRenderableWidget(Button.builder(
                                Component.literal("L"),
                                b -> minecraft.pushGuiLayer(new InputLockScreen(menu, menu.blockEntity)))
                        .bounds(Math.max(0, leftPos - 26), topPos + 22, 24, 20)
                        .tooltip(Tooltip.create(Component.translatable("ftbic.locks.title")))
                        .build(IndustrialButton::new));
            }
            boolean supported = false;
            for (var resource : SideConfiguration.Resource.values())
                supported |= menu.blockEntity.supportsResource(resource);
            if (supported) {
                addRenderableWidget(Button.builder(
                                Component.translatable("ftbic.sides.button"),
                                b -> minecraft.pushGuiLayer(new SideConfigurationScreen(menu)))
                        .bounds(Math.max(0, leftPos - 26), topPos, 24, 20)
                        .tooltip(Tooltip.create(Component.translatable("ftbic.sides.title")))
                        .build(IndustrialButton::new));
            }
        }
    }

    private void drawInputLocks(GuiGraphicsExtractor g) {
        var machine = menu.blockEntity;
        if (machine != null && machine.supportsInputLocks()) {
            for (int i = 0; i < machine.inputItems.length; i++) {
                var lock = machine.getInputLock(i);
                if (lock.isEmpty()) continue;
                var slot = menu.slots.get(i);
                int x = leftPos + slot.x, y = topPos + slot.y;
                if (!slot.hasItem()) {
                    g.item(lock, x, y);
                    g.fill(x, y, x + 16, y + 16, 0x998B8B8B);
                }
                g.fill(x, y + 16, x + 16, y + 17, IndustrialGui.CYAN);
            }
        }
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        drawBase(graphics);
        drawDefaultBars(graphics);
        extractOverlays(graphics, mouseX, mouseY, partialTick);
        drawInputLocks(graphics);
        super.extractContents(graphics, mouseX, mouseY, partialTick);
        extractDefaultTooltips(graphics, mouseX, mouseY);
        extractOverlayTooltips(graphics, mouseX, mouseY);
    }

    protected void extractOverlays(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {}

    protected void extractOverlayTooltips(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        extractMachineSlotTooltips(graphics, mouseX, mouseY);
        var machine = menu.blockEntity;
        if (machine != null && machine.supportsInputLocks()) {
            for (int i = 0; i < machine.inputItems.length; i++) {
                var slot = menu.slots.get(i);
                var lock = machine.getInputLock(i);
                if (!slot.hasItem()
                        && !lock.isEmpty()
                        && isIn(mouseX, mouseY, leftPos + slot.x, topPos + slot.y, 16, 16)) {
                    graphics.setTooltipForNextFrame(
                            Component.translatable("ftbic.locks.locked", lock.getHoverName()), mouseX, mouseY);
                }
            }
        }
    }

    protected void extractMachineSlotTooltips(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        Slot slot = this.hoveredSlot;
        if (slot == null || slot.hasItem()) return;
        if (slot instanceof UpgradeSlot) {
            graphics.setTooltipForNextFrame(Component.translatable("ftbic.gui.slot.upgrade"), mouseX, mouseY);
        } else if (slot instanceof ChargeSlot) {
            graphics.setTooltipForNextFrame(Component.translatable("ftbic.gui.slot.charge"), mouseX, mouseY);
        } else if (slot instanceof BatterySlot) {
            graphics.setTooltipForNextFrame(Component.translatable("ftbic.gui.slot.battery"), mouseX, mouseY);
        } else if (slot instanceof PickaxeSlot) {
            graphics.setTooltipForNextFrame(Component.translatable("ftbic.gui.slot.pickaxe"), mouseX, mouseY);
        }
    }

    protected void tankTooltip(
            GuiGraphicsExtractor g, int x, int y, int mouseX, int mouseY, FluidStack fluid, int capacity) {
        if (!isIn(mouseX, mouseY, x, y, 18, 54)) return;
        Component label;
        if (fluid == null || fluid.isEmpty() || capacity <= 0) {
            label = Component.translatable("ftbic.jade.fluid_empty");
        } else {
            Identifier id = BuiltInRegistries.FLUID.getKey(fluid.getFluid());
            String name = id == null ? "unknown" : id.getPath();
            String key =
                    switch (name) {
                        case "water" -> "ftbic.jade.water";
                        case "lava" -> "ftbic.jade.lava";
                        default -> "ftbic.jade.fluid";
                    };
            label = Component.translatable(key, fluid.getAmount(), capacity);
        }
        g.setTooltipForNextFrame(label, mouseX, mouseY);
    }

    protected void energyTooltip(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        extractDefaultTooltips(g, mouseX, mouseY);
    }

    protected void extractDefaultTooltips(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (energyX >= 0 && energyY >= 0 && isIn(mouseX, mouseY, leftPos + energyX, topPos + energyY, 14, 14)) {
            double cap = this.menu.blockEntity == null ? 0 : this.menu.blockEntity.getEnergyCapacity();
            double cur = cap * this.menu.getEnergyFraction();
            graphics.setTooltipForNextFrame(
                    Component.literal(FTBICUtils.formatEnergy(cur).getString() + " / "
                            + FTBICUtils.formatEnergy(cap).getString()),
                    mouseX,
                    mouseY);
        }
        if (drawDefaultArrow && isIn(mouseX, mouseY, leftPos + 80, topPos + 35, 22, 16)) {
            int pct = Math.round(this.menu.getProgressFraction() * 100F);
            graphics.setTooltipForNextFrame(Component.translatable("ftbic.jade.progress", pct), mouseX, mouseY);
        }
    }

    protected void drawBase(GuiGraphicsExtractor graphics) {
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                getScreenTexture(),
                leftPos,
                topPos,
                0F,
                0F,
                this.imageWidth,
                this.imageHeight,
                256,
                256);
    }

    protected Identifier getScreenTexture() {
        return BASE_TEXTURE;
    }

    protected void drawDefaultBars(GuiGraphicsExtractor graphics) {
        if (energyX >= 0 && energyY >= 0) {
            float frac = this.menu.getEnergyFraction();
            drawEnergy(graphics, leftPos + energyX, topPos + energyY, Mth.ceil(frac * 14F));
            if (this.menu.isStarving() && (System.currentTimeMillis() / 400L) % 2L == 0L) {
                graphics.fill(
                        leftPos + energyX, topPos + energyY, leftPos + energyX + 14, topPos + energyY + 14, 0x55FF3030);
            }
        }
        if (drawDefaultArrow) {
            int progPx = Math.max(0, Math.min(24, Math.round(this.menu.getProgressFraction() * 24F)));
            drawArrow(graphics, leftPos + 80, topPos + 34, progPx);
        }
    }

    public void drawEnergy(GuiGraphicsExtractor g, int x, int y, int energy) {
        int e = Mth.clamp(energy, 0, 14);
        switch (energyType) {
            case 1 -> { // horizontal type-1 (battery/antimatter, 14×14)
                if (e < 14) {
                    g.blit(RenderPipelines.GUI_TEXTURED, BASE_TEXTURE, x + e, y, 91F + e, 240F, 14 - e, 14, 256, 256);
                }
                if (e > 0) {
                    g.blit(RenderPipelines.GUI_TEXTURED, BASE_TEXTURE, x, y, 106F, 240F, e, 14, 256, 256);
                }
            }
            case 2 -> { // horizontal type-2
                if (e < 14) {
                    g.blit(RenderPipelines.GUI_TEXTURED, BASE_TEXTURE, x + e, y, 121F + e, 240F, 14 - e, 14, 256, 256);
                }
                if (e > 0) {
                    g.blit(RenderPipelines.GUI_TEXTURED, BASE_TEXTURE, x, y, 136F, 240F, e, 14, 256, 256);
                }
            }
            default -> { // vertical default
                if (e < 14) {
                    g.blit(RenderPipelines.GUI_TEXTURED, BASE_TEXTURE, x, y, 1F, 240F, 14, 14 - e, 256, 256);
                }
                if (e > 0) {
                    g.blit(
                            RenderPipelines.GUI_TEXTURED,
                            BASE_TEXTURE,
                            x,
                            y + (14 - e),
                            16F,
                            240F + (14 - e),
                            14,
                            e,
                            256,
                            256);
                }
            }
        }
    }

    public void drawFuel(GuiGraphicsExtractor g, int x, int y, int fuel) {
        int f = Mth.clamp(fuel, 0, 14);
        if (f < 14) g.blit(RenderPipelines.GUI_TEXTURED, BASE_TEXTURE, x, y, 31F, 240F, 14, 14 - f, 256, 256);
        if (f > 0)
            g.blit(RenderPipelines.GUI_TEXTURED, BASE_TEXTURE, x, y + (14 - f), 46F, 240F + (14 - f), 14, f, 256, 256);
    }

    public void drawSun(GuiGraphicsExtractor g, int x, int y, int fuel) {
        int f = Mth.clamp(fuel, 0, 14);
        if (f < 14) g.blit(RenderPipelines.GUI_TEXTURED, BASE_TEXTURE, x, y, 61F, 240F, 14, 14 - f, 256, 256);
        if (f > 0)
            g.blit(RenderPipelines.GUI_TEXTURED, BASE_TEXTURE, x, y + (14 - f), 76F, 240F + (14 - f), 14, f, 256, 256);
    }

    public void drawSlot(GuiGraphicsExtractor g, int x, int y) {
        g.blit(RenderPipelines.GUI_TEXTURED, BASE_TEXTURE, x, y, 1F, 167F, 18, 18, 256, 256);
    }

    public void drawLockedSlot(GuiGraphicsExtractor g, int x, int y) {
        g.blit(RenderPipelines.GUI_TEXTURED, BASE_TEXTURE, x, y, 20F, 167F, 18, 18, 256, 256);
    }

    public void drawLargeSlot(GuiGraphicsExtractor g, int x, int y) {
        g.blit(RenderPipelines.GUI_TEXTURED, BASE_TEXTURE, x, y, 1F, 186F, 26, 26, 256, 256);
    }

    public void drawArrow(GuiGraphicsExtractor g, int x, int y, int progress) {
        int p = Mth.clamp(progress, 0, 24);
        if (p < 24) g.blit(RenderPipelines.GUI_TEXTURED, BASE_TEXTURE, x + p, y, 87F + p, 167F, 24 - p, 17, 256, 256);
        if (p > 0) g.blit(RenderPipelines.GUI_TEXTURED, BASE_TEXTURE, x, y, 87F, 185F, p, 17, 256, 256);
    }

    public void drawArrowDown(GuiGraphicsExtractor g, int x, int y) {
        g.blit(RenderPipelines.GUI_TEXTURED, BASE_TEXTURE, x, y, 112F, 186F, 14, 14, 256, 256);
    }

    public void drawProgressBar(GuiGraphicsExtractor g, int x, int y, int progress) {
        int p = Mth.clamp(progress, 0, 26);
        if (p < 26) g.blit(RenderPipelines.GUI_TEXTURED, BASE_TEXTURE, x + p, y, 87F + p, 203F, 26 - p, 3, 256, 256);
        if (p > 0) g.blit(RenderPipelines.GUI_TEXTURED, BASE_TEXTURE, x, y, 87F, 207F, p, 3, 256, 256);
    }

    public void drawTank(GuiGraphicsExtractor g, int x, int y, FluidStack fluid, int capacity) {
        // Backing
        g.blit(RenderPipelines.GUI_TEXTURED, BASE_TEXTURE, x, y, 49F, 167F, 18, 54, 256, 256);

        if (!fluid.isEmpty() && capacity > 0) {
            var model = Minecraft.getInstance()
                    .getModelManager()
                    .getFluidStateModelSet()
                    .get(fluid.getFluid().defaultFluidState());
            TextureAtlasSprite sprite = model.stillMaterial().sprite();
            int tint = model.fluidTintSource() == null
                    ? -1
                    : model.fluidTintSource().colorAsStack(fluid);
            int height = Mth.ceil(Math.min(1D, fluid.getAmount() / (double) capacity) * 52);
            g.enableScissor(x + 1, y + 53 - height, x + 17, y + 53);
            for (int bottom = y + 53; bottom > y + 53 - height; bottom -= 16) {
                g.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x + 1, bottom - 16, 16, 16, tint);
            }
            g.disableScissor();
        }

        // Foreground glass overlay
        g.blit(RenderPipelines.GUI_TEXTURED, BASE_TEXTURE, x, y, 68F, 167F, 18, 54, 256, 256);
    }

    public void drawButtonPause(GuiGraphicsExtractor g, int x, int y) {
        g.blit(RenderPipelines.GUI_TEXTURED, BASE_TEXTURE, x, y, 112F, 167F, 18, 18, 256, 256);
    }

    public void drawButtonStart(GuiGraphicsExtractor g, int x, int y) {
        g.blit(RenderPipelines.GUI_TEXTURED, BASE_TEXTURE, x, y, 131F, 167F, 18, 18, 256, 256);
    }

    public void drawButtonFrame(GuiGraphicsExtractor g, int x, int y) {
        g.blit(RenderPipelines.GUI_TEXTURED, BASE_TEXTURE, x, y, 150F, 167F, 18, 18, 256, 256);
    }

    public void drawPauseButton(GuiGraphicsExtractor g, int x, int y, int mouseX, int mouseY, boolean paused) {
        if (paused) drawButtonStart(g, x, y);
        else drawButtonPause(g, x, y);
        if (isIn(mouseX, mouseY, x, y, 18, 18)) drawButtonFrame(g, x, y);
    }

    public void drawSmallButton(GuiGraphicsExtractor g, int x, int y, int u) {
        g.blit(RenderPipelines.GUI_TEXTURED, BASE_TEXTURE, x, y, u, 201F, 9, 10, 256, 256);
    }

    public void drawSmallPauseButton(GuiGraphicsExtractor g, int x, int y, int mouseX, int mouseY, boolean paused) {
        drawSmallButton(g, x, y, paused ? 124 : 114);
        if (isIn(mouseX, mouseY, x, y, 9, 10)) drawSmallButton(g, x, y, 134);
    }

    public void drawSmallRedstoneButton(GuiGraphicsExtractor g, int x, int y, int mouseX, int mouseY, boolean enabled) {
        drawSmallButton(g, x, y, enabled ? 164 : 154);
        if (isIn(mouseX, mouseY, x, y, 9, 10)) drawSmallButton(g, x, y, 134);
    }

    public void drawSmallQuestionButton(GuiGraphicsExtractor g, int x, int y, int mouseX, int mouseY) {
        drawSmallButton(g, x, y, 144);
        if (isIn(mouseX, mouseY, x, y, 9, 10)) drawSmallButton(g, x, y, 134);
    }

    public void drawNuclearBar(GuiGraphicsExtractor g, int x, int y, boolean active) {
        g.blit(RenderPipelines.GUI_TEXTURED, BASE_TEXTURE, x, y, 87F, 218F, 54, 10, 256, 256);
        if (active) {
            g.blit(RenderPipelines.GUI_TEXTURED, BASE_TEXTURE, x, y, 87F, 229F, 54, 10, 256, 256);
        }
    }

    public void drawHeatBar(GuiGraphicsExtractor g, int x, int y, float heat) {
        g.blit(RenderPipelines.GUI_TEXTURED, BASE_TEXTURE, x, y, 142F, 218F, 54, 10, 256, 256);
        if (heat > 0F) {
            int w = Mth.ceil(heat * 52F);
            g.blit(RenderPipelines.GUI_TEXTURED, BASE_TEXTURE, x + 1, y, 143F, 229F, w, 10, 256, 256);
        }
    }

    public static boolean isIn(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }
}
