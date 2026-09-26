package dev.ftb.mods.ftbic.client.gui;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.entity.generator.NuclearReactorBlockEntity;
import dev.ftb.mods.ftbic.integration.jei.ClientRecipeCache;
import dev.ftb.mods.ftbic.item.ReactorBlueprintItem;
import dev.ftb.mods.ftbic.item.reactor.NuclearReactor;
import dev.ftb.mods.ftbic.item.reactor.ReactorItem;
import dev.ftb.mods.ftbic.net.FTBICNet;
import dev.ftb.mods.ftbic.net.ReactorDesignPayload;
import dev.ftb.mods.ftbic.screen.NuclearReactorMenu;
import dev.ftb.mods.ftbic.util.EnergyDisplay;
import dev.ftb.mods.ftbic.util.ReactorDesign;
import dev.ftb.mods.ftbic.util.ReactorPresetLibrary;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class NuclearReactorScreen extends ElectricBlockScreen<NuclearReactorMenu> {
    public static final Identifier NUCLEAR_REACTOR_TEXTURE = FTBIC.id("textures/gui/nuclear_reactor.png");

    private static final int GRID_TOP = 17;
    private static final int GRID_BOTTOM = GRID_TOP + 6 * 18;
    private static final int CONTROL_STRIP_TOP = GRID_BOTTOM;
    private static final int CONTROL_STRIP_H = 19;
    private static final int INVENTORY_Y = CONTROL_STRIP_TOP + CONTROL_STRIP_H;
    private static final int HOTBAR_Y = INVENTORY_Y + 54;
    private static final int PANEL_X = 180;
    private static final int PANEL_W = 140;
    private static final int MATERIAL_TEXT_W = PANEL_W - 28;
    private final Inventory inventory;
    private List<ReactorPresetLibrary.Preset> presets = List.of();
    private int selectedPreset;
    private int materialPage;
    private ReactorDesign cachedDesign;
    private ItemStack[] ghosts = new ItemStack[0];
    private long blueprintErrorUntil;

    private record Material(Item item, int installed, int available, int required) {}

    public NuclearReactorScreen(NuclearReactorMenu menu, Inventory inv, Component title) {
        super(menu, inv, title, 324, HOTBAR_Y + 22);
        inventory = inv;
        drawDefaultArrow = false;
        energyX = -1;
        energyY = -1;
    }

    @Override
    protected Identifier getScreenTexture() {
        return NUCLEAR_REACTOR_TEXTURE;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 8;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = CONTROL_STRIP_TOP + 5;
        presets = ReactorPresetLibrary.listAll();
        selectedPreset = Math.min(selectedPreset, Math.max(0, presets.size() - 1));
    }

    @Override
    protected void drawBase(GuiGraphicsExtractor g) {
        g.blit(RenderPipelines.GUI_TEXTURED, getScreenTexture(), leftPos, topPos, 0F, 0F, 176, imageHeight, 256, 256);
        g.fill(leftPos + 176, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF373737);
        g.fill(leftPos + 178, topPos + 2, leftPos + imageWidth - 2, topPos + imageHeight - 2, 0xFFC6C6C6);
    }

    private ReactorDesign design() {
        return menu.blockEntity instanceof NuclearReactorBlockEntity reactor ? reactor.getPlannedDesign() : null;
    }

    private List<Material> materials() {
        var counts = new LinkedHashMap<Item, int[]>();
        for (int i = 0; i < ghosts.length; i++) {
            if (ghosts[i].isEmpty()) continue;
            Item item = ghosts[i].getItem();
            int[] count = counts.computeIfAbsent(item, key -> new int[3]);
            count[2]++;
            ItemStack installed = menu.blockEntity.getStackInSlot(i);
            if (installed.is(item) && !((ReactorItem) item).isItemBroken(installed)) count[0]++;
        }
        for (int i = 0; i < 36; i++) {
            ItemStack stack = inventory.getItem(i);
            int[] count = counts.get(stack.getItem());
            if (count != null && !((ReactorItem) stack.getItem()).isItemBroken(stack)) count[1] += stack.getCount();
        }
        var result = new ArrayList<Material>();
        counts.forEach((item, count) -> result.add(new Material(item, count[0], count[1], count[2])));
        return result;
    }

    private void designButton(
            GuiGraphicsExtractor g, int x, int y, int width, Component text, int mx, int my, boolean enabled) {
        x += leftPos + PANEL_X;
        y += topPos;
        g.fill(x, y, x + width, y + 14, 0xFF444444);
        g.fill(x + 1, y + 1, x + width - 1, y + 13, enabled && isIn(mx, my, x, y, width, 14) ? 0xFFBCE0FF : 0xFFE0E0E0);
        String label = fitLabel(text, width - 4);
        if (font.width(text) > width - 4) {
            if (isIn(mx, my, x, y, width, 14)) g.setTooltipForNextFrame(text, mx, my);
        }
        g.text(
                font,
                label,
                x + (width - font.width(label)) / 2,
                y + 3,
                enabled ? IndustrialGui.TEXT : IndustrialGui.MUTED,
                false);
    }

    private String fitLabel(Component text, int width) {
        String label = text.getString();
        return font.width(label) <= width ? label : font.plainSubstrByWidth(label, width - font.width("...")) + "...";
    }

    private boolean canBuild() {
        return design() != null
                && menu.isPaused()
                && !menu.allowRedstone()
                && design().chambers() + 3 <= menu.getActiveColumns();
    }

    private void drawDesign(GuiGraphicsExtractor g, int mx, int my) {
        ReactorDesign current = design();
        if (current != cachedDesign) {
            cachedDesign = current;
            ghosts = current == null ? new ItemStack[0] : current.previewItems();
            materialPage = 0;
        }
        int x = leftPos + PANEL_X;
        g.text(font, Component.translatable("ftbic.reactor.design.title"), x + 4, topPos + 7, 0xFF303030, false);
        designButton(g, 4, 20, 14, Component.literal("<"), mx, my, presets.size() > 1);
        designButton(g, 122, 20, 14, Component.literal(">"), mx, my, presets.size() > 1);
        String name = presets.isEmpty() ? "-" : presets.get(selectedPreset).name();
        String presetLabel = fitLabel(Component.literal(name), 98);
        g.text(font, presetLabel, x + (PANEL_W - font.width(presetLabel)) / 2, topPos + 23, IndustrialGui.TEXT, false);
        if (font.width(name) > 98 && isIn(mx, my, x + 20, topPos + 20, 100, 14)) {
            g.setTooltipForNextFrame(Component.literal(name), mx, my);
        }
        designButton(
                g, 4, 37, 64, Component.translatable("ftbic.reactor.design.load_short"), mx, my, !presets.isEmpty());
        designButton(g, 72, 37, 64, Component.translatable("ftbic.reactor.design.paste_short"), mx, my, true);
        designButton(g, 4, 54, 132, Component.translatable("ftbic.reactor.design.build"), mx, my, canBuild());
        designButton(g, 4, 71, 94, Component.translatable("ftbic.reactor.design.blueprint"), mx, my, current != null);
        designButton(g, 102, 71, 34, Component.translatable("ftbic.reactor.design.clear"), mx, my, current != null);
        if (current == null) {
            g.text(
                    font,
                    Component.translatable("ftbic.reactor.design.no_design"),
                    x + 4,
                    topPos + 96,
                    0xFF606060,
                    false);
            return;
        }
        int actualChambers = menu.getActiveColumns() - 3;
        int actualWater = menu.coolingThousandths.get() / 10;
        int plannedWater = (int) Math.round(current.water() * 100D);
        g.text(
                font,
                Component.translatable("ftbic.reactor.design.chambers", actualChambers, current.chambers()),
                x + 4,
                topPos + 91,
                actualChambers < current.chambers() ? 0xFFAA2020 : 0xFF303030,
                false);
        g.text(
                font,
                Component.translatable("ftbic.reactor.design.cooling", actualWater, plannedWater),
                x + 4,
                topPos + 103,
                actualWater == plannedWater ? 0xFF303030 : 0xFFAA6020,
                false);
        int conflicts = 0;
        int missing = 0;
        for (int i = 0; i < ghosts.length; i++) {
            ItemStack existing = menu.blockEntity.getStackInSlot(i);
            boolean wanted = !ghosts[i].isEmpty();
            boolean conflict = !existing.isEmpty()
                    && (!wanted
                            || !existing.is(ghosts[i].getItem())
                            || ((ReactorItem) ghosts[i].getItem()).isItemBroken(existing));
            if (conflict) conflicts++;
            if (wanted && (existing.isEmpty() || conflict)) missing++;
            int sx = leftPos + 8 + (i % NuclearReactor.MAX_COLUMNS) * 18;
            int sy = topPos + 18 + (i / NuclearReactor.MAX_COLUMNS) * 18;
            if (existing.isEmpty() && wanted) {
                g.item(ghosts[i], sx, sy);
                g.fill(sx, sy, sx + 16, sy + 16, 0x998B8B8B);
            }
            if (conflict || (wanted && i % NuclearReactor.MAX_COLUMNS >= menu.getActiveColumns())) {
                g.fill(sx - 1, sy - 1, sx + 17, sy, 0xFFFF5555);
                g.fill(sx - 1, sy + 16, sx + 17, sy + 17, 0xFFFF5555);
                g.fill(sx - 1, sy, sx, sy + 16, 0xFFFF5555);
                g.fill(sx + 16, sy, sx + 17, sy + 16, 0xFFFF5555);
            }
        }
        g.text(
                font,
                Component.translatable("ftbic.reactor.design.remaining", missing, conflicts),
                x + 4,
                topPos + 115,
                conflicts > 0 ? 0xFFAA2020 : 0xFF303030,
                false);
        g.text(font, Component.translatable("ftbic.reactor.design.materials"), x + 4, topPos + 130, 0xFF303030, false);
        List<Material> entries = materials();
        int pages = Math.max(1, (entries.size() + 2) / 3);
        materialPage = Math.min(materialPage, pages - 1);
        for (int row = 0; row < 3; row++) {
            int index = materialPage * 3 + row;
            if (index >= entries.size()) break;
            Material material = entries.get(index);
            int y = topPos + 143 + row * 19;
            g.item(new ItemStack(material.item()), x + 4, y);
            int needed = material.required() - material.installed();
            g.text(
                    font,
                    fitLabel(new ItemStack(material.item()).getHoverName(), MATERIAL_TEXT_W),
                    x + 24,
                    y,
                    0xFF303030,
                    false);
            g.text(
                    font,
                    fitLabel(
                            Component.translatable("ftbic.reactor.design.count_short", material.available(), needed),
                            MATERIAL_TEXT_W),
                    x + 24,
                    y + 9,
                    material.available() >= needed ? 0xFF267026 : 0xFFAA2020,
                    false);
        }
        designButton(g, 4, 202, 14, Component.literal("<"), mx, my, pages > 1);
        designButton(g, 122, 202, 14, Component.literal(">"), mx, my, pages > 1);
        String pageLabel = (materialPage + 1) + " / " + pages;
        g.text(font, pageLabel, x + (PANEL_W - font.width(pageLabel)) / 2, topPos + 205, IndustrialGui.TEXT, false);
    }

    @Override
    protected void extractOverlays(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        int activeColumns = this.menu.getActiveColumns();
        int gridLeft = leftPos + 7;
        int slotSize = 18;
        int overlayColor = 0xFF8B8B8B;

        for (int col = 0; col < 9; col++) {
            drawSlot(g, gridLeft + col * 18, topPos + GRID_TOP + 3 * 18);
        }

        if (activeColumns < 9) {
            int maskX = gridLeft + 1 + activeColumns * slotSize;
            int maskW = (9 - activeColumns) * slotSize;
            g.fill(maskX, topPos + GRID_TOP + 1, maskX + maskW, topPos + GRID_BOTTOM + 1, overlayColor);
        }

        g.fill(
                leftPos + 7,
                topPos + CONTROL_STRIP_TOP,
                leftPos + 169,
                topPos + CONTROL_STRIP_TOP + CONTROL_STRIP_H,
                overlayColor);

        int stripY = topPos + CONTROL_STRIP_TOP + (CONTROL_STRIP_H - 10) / 2;
        drawNuclearBar(g, leftPos + 115, topPos + 5, this.menu.isRunning() && !this.menu.isPaused());
        drawSmallPauseButton(g, leftPos + 105, topPos + 5, mouseX, mouseY, this.menu.isPaused());
        drawSmallQuestionButton(g, leftPos + 94, topPos + 5, mouseX, mouseY);

        drawHeatBar(g, leftPos + 115, stripY, this.menu.getHeatFraction());
        drawSmallRedstoneButton(g, leftPos + 105, stripY, mouseX, mouseY, this.menu.allowRedstone());

        g.centeredText(
                font,
                (this.menu.isPaused()
                                ? Component.translatable("ftbic.reactor.paused")
                                : EnergyDisplay.perTick(this.menu.getEnergyOutput()))
                        .withStyle(ChatFormatting.WHITE),
                leftPos + 142,
                topPos + 6,
                0xFFFFFF);
        g.centeredText(
                font,
                Component.translatable("ftbic.reactor.heat_percentage", Math.round(this.menu.getHeatFraction() * 100F))
                        .withStyle(ChatFormatting.WHITE),
                leftPos + 142,
                stripY + 1,
                0xFFFFFF);

        int invX = leftPos + 8;
        int invY = topPos + INVENTORY_Y;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                drawSlot(g, invX + col * 18 - 1, invY + row * 18 - 1);
            }
        }
        int hotbarY = topPos + HOTBAR_Y;
        for (int col = 0; col < 9; col++) {
            drawSlot(g, invX + col * 18 - 1, hotbarY - 1);
        }
        drawDesign(g, mouseX, mouseY);
    }

    @Override
    protected void extractOverlayTooltips(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        if (isIn(mouseX, mouseY, leftPos + PANEL_X + 4, topPos + 37, 64, 14)) {
            g.setTooltipForNextFrame(Component.translatable("ftbic.reactor.design.load"), mouseX, mouseY);
        }
        if (isIn(mouseX, mouseY, leftPos + PANEL_X + 72, topPos + 37, 64, 14)) {
            g.setTooltipForNextFrame(Component.translatable("ftbic.reactor.design.paste"), mouseX, mouseY);
        }
        if (isIn(mouseX, mouseY, leftPos + PANEL_X + 4, topPos + 54, 132, 14)) {
            g.setTooltipForNextFrame(
                    Component.translatable(
                            canBuild() ? "ftbic.reactor.design.build_hint" : "ftbic.reactor.design.build_requirements"),
                    mouseX,
                    mouseY);
        }
        if (isIn(mouseX, mouseY, leftPos + PANEL_X + 4, topPos + 91, 132, 24)) {
            g.setTooltipForNextFrame(Component.translatable("ftbic.reactor.design.environment_hint"), mouseX, mouseY);
        }
        if (isIn(mouseX, mouseY, leftPos + PANEL_X + 4, topPos + 71, 94, 14)) {
            g.setTooltipForNextFrame(Component.translatable("item.ftbic.reactor_blueprint.need_blank"), mouseX, mouseY);
        }
        if (isIn(mouseX, mouseY, leftPos + PANEL_X + 102, topPos + 71, 34, 14)) {
            g.setTooltipForNextFrame(Component.translatable("ftbic.reactor.design.clear_hint"), mouseX, mouseY);
        }
        List<Material> entries = materials();
        for (int row = 0; row < 3; row++) {
            int index = materialPage * 3 + row;
            if (index < entries.size()
                    && isIn(mouseX, mouseY, leftPos + PANEL_X + 4, topPos + 143 + row * 19, 132, 18)) {
                Material material = entries.get(index);
                g.setTooltipForNextFrame(
                        new ItemStack(material.item())
                                .getHoverName()
                                .copy()
                                .append("\n")
                                .append(Component.translatable(
                                        "ftbic.reactor.design.count",
                                        material.available(),
                                        material.required() - material.installed())),
                        mouseX,
                        mouseY);
            }
        }
        for (int i = 0; i < ghosts.length; i++) {
            int x = leftPos + 8 + (i % NuclearReactor.MAX_COLUMNS) * 18;
            int y = topPos + 18 + (i / NuclearReactor.MAX_COLUMNS) * 18;
            if (isIn(mouseX, mouseY, x, y, 16, 16)) {
                ItemStack existing = menu.blockEntity.getStackInSlot(i);
                if (!ghosts[i].isEmpty()
                        && (existing.isEmpty()
                                || !existing.is(ghosts[i].getItem())
                                || ((ReactorItem) ghosts[i].getItem()).isItemBroken(existing))) {
                    g.setTooltipForNextFrame(
                            Component.translatable("ftbic.reactor.design.expected", ghosts[i].getHoverName()),
                            mouseX,
                            mouseY);
                } else if (ghosts[i].isEmpty() && !existing.isEmpty()) {
                    g.setTooltipForNextFrame(Component.translatable("ftbic.reactor.design.unexpected"), mouseX, mouseY);
                }
            }
        }
        int stripY = topPos + CONTROL_STRIP_TOP + (CONTROL_STRIP_H - 10) / 2;
        if (isIn(mouseX, mouseY, leftPos + 115, topPos + 5, 54, 10)) {
            Component label = this.menu.isPaused()
                    ? Component.translatable(
                            "ftbic.reactor.tooltip.paused", EnergyDisplay.perTick(this.menu.getEnergyOutput()))
                    : Component.translatable(
                            "ftbic.reactor.tooltip.output", EnergyDisplay.perTick(this.menu.getEnergyOutput()));
            g.setTooltipForNextFrame(label, mouseX, mouseY);
        }
        if (isIn(mouseX, mouseY, leftPos + 115, stripY, 54, 10)) {
            int pct = Math.round(this.menu.getHeatFraction() * 100F);
            g.setTooltipForNextFrame(Component.translatable("ftbic.jade.reactor_heat", pct), mouseX, mouseY);
        }
        if (isIn(mouseX, mouseY, leftPos + 105, topPos + 5, 9, 10)) {
            Component label = this.menu.isPaused()
                    ? Component.translatable("ftbic.reactor.tooltip.resume")
                    : Component.translatable("ftbic.reactor.tooltip.pause");
            g.setTooltipForNextFrame(label, mouseX, mouseY);
        }
        if (isIn(mouseX, mouseY, leftPos + 105, stripY, 9, 10)) {
            Component label = this.menu.allowRedstone()
                    ? Component.translatable("ftbic.reactor.tooltip.redstone_enabled")
                    : Component.translatable("ftbic.reactor.tooltip.redstone_disabled");
            g.setTooltipForNextFrame(label, mouseX, mouseY);
        }
        if (isIn(mouseX, mouseY, leftPos + 94, topPos + 5, 9, 10)) {
            g.setTooltipForNextFrame(Component.translatable("ftbic.reactor.tooltip.show_jei"), mouseX, mouseY);
        }
        if (System.currentTimeMillis() < blueprintErrorUntil) {
            g.setTooltipForNextFrame(
                    Component.translatable("item.ftbic.reactor_blueprint.need_blank")
                            .withStyle(ChatFormatting.RED),
                    leftPos + PANEL_X + 8,
                    topPos + 65);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean dragging) {
        int mx = (int) event.x();
        int my = (int) event.y();
        if (isIn(mx, my, leftPos + PANEL_X, topPos, PANEL_W, imageHeight)) {
            int x = mx - leftPos - PANEL_X;
            int y = my - topPos;
            if (isIn(x, y, 4, 20, 14, 14) && !presets.isEmpty())
                selectedPreset = Math.floorMod(selectedPreset - 1, presets.size());
            else if (isIn(x, y, 122, 20, 14, 14) && !presets.isEmpty())
                selectedPreset = (selectedPreset + 1) % presets.size();
            else if (isIn(x, y, 4, 37, 64, 14) && !presets.isEmpty())
                loadDesign(presets.get(selectedPreset).design().toJson());
            else if (isIn(x, y, 72, 37, 64, 14))
                loadDesign(Minecraft.getInstance().keyboardHandler.getClipboard());
            else if (isIn(x, y, 4, 54, 132, 14) && canBuild()) send(2);
            else if (isIn(x, y, 4, 71, 94, 14) && design() != null) {
                blueprintErrorUntil =
                        ReactorBlueprintItem.hasBlank(inventory) ? 0L : System.currentTimeMillis() + 4000L;
                send(4);
            } else if (isIn(x, y, 102, 71, 34, 14) && design() != null) send(3);
            else if (isIn(x, y, 4, 202, 14, 14))
                materialPage =
                        Math.floorMod(materialPage - 1, Math.max(1, (materials().size() + 2) / 3));
            else if (isIn(x, y, 122, 202, 14, 14))
                materialPage = (materialPage + 1) % Math.max(1, (materials().size() + 2) / 3);
            return true;
        }
        int stripY = topPos + CONTROL_STRIP_TOP + (CONTROL_STRIP_H - 10) / 2;
        if (isIn(mx, my, leftPos + 105, topPos + 5, 9, 10)) {
            send(0);
            return true;
        }
        if (isIn(mx, my, leftPos + 105, stripY, 9, 10)) {
            send(1);
            return true;
        }
        if (isIn(mx, my, leftPos + 94, topPos + 5, 9, 10)) {
            ClientRecipeCache.setSearchFilter("@ftbic reactor");
            return true;
        }
        return super.mouseClicked(event, dragging);
    }

    private void send(int buttonId) {
        Minecraft.getInstance()
                .player
                .connection
                .send(new ServerboundContainerButtonClickPacket(menu.containerId, buttonId));
    }

    private void loadDesign(String json) {
        try {
            ReactorDesign.fromJson(json);
            FTBICNet.sendToServer(new ReactorDesignPayload(menu.containerId, json));
        } catch (IllegalArgumentException e) {
            Minecraft.getInstance().player.sendOverlayMessage(Component.translatable("ftbic.reactor.design.invalid"));
        }
    }
}
