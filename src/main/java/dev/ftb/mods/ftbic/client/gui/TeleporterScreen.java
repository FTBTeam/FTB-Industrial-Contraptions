package dev.ftb.mods.ftbic.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import dev.ftb.mods.ftbic.block.entity.machine.TeleporterBlockEntity;
import dev.ftb.mods.ftbic.net.ClearTeleporterPayload;
import dev.ftb.mods.ftbic.net.ConfigureTeleporterPayload;
import dev.ftb.mods.ftbic.net.SelectTeleporterPayload;
import dev.ftb.mods.ftbic.screen.TeleporterMenu;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import dev.ftb.mods.ftbic.util.TeleporterEntry;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class TeleporterScreen extends ElectricBlockScreen<TeleporterMenu> {
    private List<TeleporterEntry> entries() {
        return menu.peers == null ? List.of() : menu.peers;
    }

    private static final int GUI_HEIGHT = 196;
    private static final int NAME_X = 9, NAME_Y = 24, NAME_W = 88, NAME_H = 14;
    private static final int PUBLIC_BTN_X = 102, PUBLIC_BTN_Y = 26;
    private static final int DEST_Y = 47;
    private static final int UNLINK_BTN_X = 153, UNLINK_BTN_Y = 46;
    private static final int DROPDOWN_X = 8, DROPDOWN_Y = 64, DROPDOWN_W = 160, DROPDOWN_H = 13;
    private static final int CLEAR_BTN_Y = 81, CLEAR_BTN_H = 13;
    private static final int CLEAR_ITEMS_BTN_X = 8, CLEAR_ITEMS_BTN_W = 76;
    private static final int CLEAR_FLUIDS_BTN_X = 92, CLEAR_FLUIDS_BTN_W = 76;
    private static final int OVERLAY_Y = 58;
    private static final int OVERLAY_ROW_H = 11;
    private static final int OVERLAY_VISIBLE_ROWS = 2;
    private static final int OVERLAY_HEADER_H = 13;
    private static final int SCROLLBAR_W = 6;

    private EditBox nameBox;
    private String lastSentName = "";
    private boolean dropdownOpen;
    private int scroll;

    public TeleporterScreen(TeleporterMenu menu, Inventory inv, Component title) {
        super(menu, inv, title, 176, GUI_HEIGHT);
        drawDefaultArrow = false;
        energyX = 156;
        energyY = 26;
    }

    @Override
    protected void init() {
        super.init();
        inventoryLabelY = 99;
        TeleporterBlockEntity be = teleporter();
        String initialName = be == null ? "" : be.name;
        lastSentName = initialName;

        nameBox = new EditBox(
                font,
                leftPos + NAME_X,
                topPos + NAME_Y,
                NAME_W,
                NAME_H,
                Component.translatable("ftbic.gui.teleporter.name_label"));
        nameBox.setMaxLength(32);
        nameBox.setValue(initialName);
        nameBox.setHint(
                Component.translatable("block.ftbic.teleporter.name_hint").withStyle(ChatFormatting.DARK_GRAY));
        addRenderableWidget(nameBox);
    }

    private TeleporterBlockEntity teleporter() {
        return this.menu.blockEntity instanceof TeleporterBlockEntity t ? t : null;
    }

    private ResourceKey<Level> myDim() {
        TeleporterBlockEntity be = teleporter();
        return be == null || be.getLevel() == null ? null : be.getLevel().dimension();
    }

    private String truncate(String s, int maxPx) {
        if (font.width(s) <= maxPx) return s;
        String ellipsis = "…";
        while (!s.isEmpty() && font.width(s + ellipsis) > maxPx) {
            s = s.substring(0, s.length() - 1);
        }
        return s + ellipsis;
    }

    private String formatEntry(String name, BlockPos pos, ResourceKey<Level> dim) {
        String base = name + " (" + pos.getX() + "," + pos.getY() + "," + pos.getZ() + ")";
        ResourceKey<Level> self = myDim();
        if (self != null && dim != null && dim != self) {
            base += " [" + dim.identifier().getPath() + "]";
        }
        return base;
    }

    private void sendConfig(boolean unlink) {
        TeleporterBlockEntity be = teleporter();
        if (be == null) return;
        String newName = nameBox == null ? be.name : nameBox.getValue();
        be.name = newName;
        ClientPacketDistributor.sendToServer(new ConfigureTeleporterPayload(newName, be.isPublic, unlink));
        lastSentName = newName;
    }

    private void togglePublic() {
        TeleporterBlockEntity be = teleporter();
        if (be == null) return;
        be.isPublic = !be.isPublic;
        sendConfig(false);
    }

    @Override
    public void removed() {
        if (nameBox != null && !nameBox.getValue().equals(lastSentName)) {
            sendConfig(false);
        }
        super.removed();
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (nameBox != null && nameBox.isFocused()) {
            int key = event.key();
            if (key == InputConstants.KEY_RETURN) {
                if (!nameBox.getValue().equals(lastSentName)) {
                    sendConfig(false);
                }
                setFocused(null);
                return true;
            }
            if (key == InputConstants.KEY_ESCAPE) {
                setFocused(null);
                return true;
            }
            if (nameBox.keyPressed(event) || nameBox.canConsumeInput()) {
                return true;
            }
        }
        return super.keyPressed(event);
    }

    @Override
    protected void drawBase(GuiGraphicsExtractor g) {
        IndustrialGui.panel(g, leftPos, topPos, imageWidth, imageHeight);
        for (var slot : menu.slots) drawSlot(g, leftPos + slot.x - 1, topPos + slot.y - 1);
        g.fill(leftPos + 4, topPos + 4, leftPos + 172, topPos + 19, IndustrialGui.HEADER);
        IndustrialGui.readout(g, leftPos + 7, topPos + 22, 93, 19);
        IndustrialGui.readout(g, leftPos + 7, topPos + 43, 162, 17);
    }

    @Override
    protected void extractOverlays(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        TeleporterBlockEntity be = teleporter();

        drawSmallRedstoneButton(
                g, leftPos + PUBLIC_BTN_X, topPos + PUBLIC_BTN_Y, mouseX, mouseY, be != null && be.isPublic);
        g.text(
                font,
                Component.translatable(
                        be != null && be.isPublic
                                ? "ftbic.gui.teleporter.public_label"
                                : "ftbic.gui.teleporter.private_label"),
                leftPos + PUBLIC_BTN_X + 11,
                topPos + PUBLIC_BTN_Y + 1,
                0xFF404040,
                false);

        boolean hasLink = be != null && be.linkedPos != null && be.linkedDimension != null;
        int statusMax = UNLINK_BTN_X - 12 - 4;
        String statusLabel;
        if (hasLink) {
            String display = be.linkedName != null && !be.linkedName.isEmpty()
                    ? be.linkedName
                    : Component.translatable("ftbic.gui.teleporter.unnamed").getString();
            statusLabel = truncate(
                    Component.translatable(
                                    "ftbic.gui.teleporter.linked_format",
                                    formatEntry(display, be.linkedPos, be.linkedDimension))
                            .getString(),
                    statusMax);
        } else {
            statusLabel = truncate(
                    Component.translatable("ftbic.gui.teleporter.not_linked").getString(), statusMax);
        }
        g.text(font, Component.literal(statusLabel), leftPos + 12, topPos + DEST_Y, IndustrialGui.CYAN, false);

        if (hasLink) {
            drawSmallQuestionButton(g, leftPos + UNLINK_BTN_X, topPos + UNLINK_BTN_Y, mouseX, mouseY);
        }

        drawDropdownButton(g, mouseX, mouseY);
        drawClearButton(
                g,
                leftPos + CLEAR_ITEMS_BTN_X,
                topPos + CLEAR_BTN_Y,
                CLEAR_ITEMS_BTN_W,
                Component.translatable("ftbic.gui.teleporter.clear_storage").getString(),
                mouseX,
                mouseY);
        drawClearButton(
                g,
                leftPos + CLEAR_FLUIDS_BTN_X,
                topPos + CLEAR_BTN_Y,
                CLEAR_FLUIDS_BTN_W,
                Component.translatable("ftbic.gui.teleporter.clear_fluids").getString(),
                mouseX,
                mouseY);

        if (dropdownOpen) {
            drawDropdownOverlay(g, mouseX, mouseY);
        }

        extractCustomTooltips(g, mouseX, mouseY);
    }

    private void drawClearButton(GuiGraphicsExtractor g, int x, int y, int w, String label, int mouseX, int mouseY) {
        boolean hovered = isIn(mouseX, mouseY, x, y, w, CLEAR_BTN_H);
        g.fill(x, y, x + w, y + CLEAR_BTN_H, IndustrialGui.BORDER);
        g.fill(x + 1, y + 1, x + w - 1, y + CLEAR_BTN_H - 1, hovered ? 0xFFD4F1F1 : 0xFFE2E8E8);
        int textX = x + (w - font.width(label)) / 2;
        g.text(font, Component.literal(label), textX, y + 2, 0xFF202020, false);
    }

    private void drawDropdownButton(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        int x = leftPos + DROPDOWN_X, y = topPos + DROPDOWN_Y;
        g.fill(x, y, x + DROPDOWN_W, y + DROPDOWN_H, IndustrialGui.BORDER);
        g.fill(x + 1, y + 1, x + DROPDOWN_W - 1, y + DROPDOWN_H - 1, 0xFFE2E8E8);
        boolean hovered = isIn(mouseX, mouseY, x, y, DROPDOWN_W, DROPDOWN_H);
        if (hovered) {
            g.fill(x + 1, y + 1, x + DROPDOWN_W - 1, y + DROPDOWN_H - 1, 0xFFD4F1F1);
        }
        String arrow = dropdownOpen ? "▲" : "▼";
        String label = entries().isEmpty()
                ? Component.translatable("ftbic.gui.teleporter.no_teleporters").getString()
                : Component.translatable("ftbic.gui.teleporter.choose_destination")
                        .getString();
        String text = truncate(label, DROPDOWN_W - 20);
        g.text(font, Component.literal(text), x + 4, y + 2, 0xFF202020, false);
        g.text(font, Component.literal(arrow), x + DROPDOWN_W - 10, y + 2, 0xFF202020, false);
    }

    private void drawDropdownOverlay(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        int x = overlayX(), y = overlayY();
        int w = DROPDOWN_W;
        int h = overlayHeight();

        IndustrialGui.panel(g, x - 3, y - 3, w + 6, h + 6);
        g.fill(x, y, x + w, y + h, 0xFFE2E8E8);
        g.fill(x, y, x + w, y + OVERLAY_HEADER_H, IndustrialGui.READOUT);
        g.text(
                font,
                Component.translatable("ftbic.gui.teleporter.choose_destination"),
                x + 4,
                y + 2,
                IndustrialGui.CYAN,
                false);

        List<TeleporterEntry> entries = entries();
        int visibleRows = overlayVisibleRows();
        boolean needsScrollbar = entries.size() > visibleRows;
        int rowWidth = needsScrollbar ? w - SCROLLBAR_W - 2 : w - 2;

        if (entries.isEmpty()) {
            g.text(
                    font,
                    Component.translatable("ftbic.gui.teleporter.no_teleporters_found")
                            .withStyle(ChatFormatting.DARK_GRAY),
                    x + 3,
                    overlayRowsY() + 3,
                    0xFF606060,
                    false);
        }

        int rows = Math.min(visibleRows, entries.size());
        for (int i = 0; i < rows; i++) {
            int idx = scroll + i;
            if (idx >= entries.size()) break;
            TeleporterEntry e = entries.get(idx);
            int rowY = overlayRowsY() + 1 + i * OVERLAY_ROW_H;
            boolean hovered = isIn(mouseX, mouseY, x + 1, rowY, rowWidth, OVERLAY_ROW_H);
            if (hovered) {
                g.fill(x + 1, rowY, x + 1 + rowWidth, rowY + OVERLAY_ROW_H, 0xFFB5E4E6);
            }
            String label = truncate("▸ " + formatEntry(e.name(), e.pos(), e.dimension()), rowWidth - 4);
            g.text(font, Component.literal(label), x + 4, rowY + 2, 0xFF202020, false);
        }

        if (needsScrollbar) {
            int trackX = x + w - SCROLLBAR_W - 1;
            int trackY = overlayRowsY() + 1;
            int trackH = visibleRows * OVERLAY_ROW_H;
            g.fill(trackX, trackY, trackX + SCROLLBAR_W, trackY + trackH, 0xFFB0B0B0);
            int thumbH = Math.max(10, trackH * visibleRows / entries.size());
            int scrollMax = entries.size() - visibleRows;
            int thumbY = trackY + (scrollMax <= 0 ? 0 : (trackH - thumbH) * scroll / scrollMax);
            g.fill(trackX, thumbY, trackX + SCROLLBAR_W, thumbY + thumbH, 0xFF606060);
        }
    }

    private int overlayX() {
        return leftPos + DROPDOWN_X;
    }

    private int overlayVisibleRows() {
        return OVERLAY_VISIBLE_ROWS;
    }

    private int overlayY() {
        return topPos + OVERLAY_Y;
    }

    private int overlayRowsY() {
        return overlayY() + OVERLAY_HEADER_H;
    }

    private int overlayHeight() {
        return OVERLAY_VISIBLE_ROWS * OVERLAY_ROW_H + 2 + OVERLAY_HEADER_H;
    }

    private void extractCustomTooltips(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        TeleporterBlockEntity be = teleporter();
        if (isIn(mouseX, mouseY, leftPos + PUBLIC_BTN_X, topPos + PUBLIC_BTN_Y, 9, 10)) {
            Component tip = be != null && be.isPublic
                    ? Component.translatable("ftbic.gui.teleporter.public_tooltip")
                    : Component.translatable("ftbic.gui.teleporter.private_tooltip");
            g.setTooltipForNextFrame(tip, mouseX, mouseY);
            return;
        }
        boolean hasLink = be != null && be.linkedPos != null && be.linkedDimension != null;
        if (hasLink && isIn(mouseX, mouseY, leftPos + UNLINK_BTN_X, topPos + UNLINK_BTN_Y, 9, 10)) {
            g.setTooltipForNextFrame(Component.translatable("ftbic.gui.teleporter.unlink_tooltip"), mouseX, mouseY);
            return;
        }
        if (isIn(mouseX, mouseY, leftPos + NAME_X, topPos + NAME_Y, NAME_W, NAME_H)) {
            g.setTooltipForNextFrame(Component.translatable("ftbic.gui.teleporter.name_tooltip"), mouseX, mouseY);
            return;
        }
        if (dropdownOpen) {
            List<TeleporterEntry> entries = entries();
            int x = overlayX();
            int visibleRows = overlayVisibleRows();
            boolean needsScrollbar = entries.size() > visibleRows;
            int rowWidth = needsScrollbar ? DROPDOWN_W - SCROLLBAR_W - 2 : DROPDOWN_W - 2;
            int rows = Math.min(visibleRows, entries.size());
            for (int i = 0; i < rows; i++) {
                int idx = scroll + i;
                if (idx >= entries.size()) break;
                int rowY = overlayRowsY() + 1 + i * OVERLAY_ROW_H;
                if (isIn(mouseX, mouseY, x + 1, rowY, rowWidth, OVERLAY_ROW_H)) {
                    TeleporterEntry e = entries.get(idx);
                    String cost = FTBICUtils.formatEnergy(e.energyUse()).getString();
                    g.setTooltipForNextFrame(
                            Component.translatable(
                                    "ftbic.gui.teleporter.entry_tooltip",
                                    formatEntry(e.name(), e.pos(), e.dimension()),
                                    cost),
                            mouseX,
                            mouseY);
                    break;
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean dragging) {
        int mx = (int) event.x();
        int my = (int) event.y();
        TeleporterBlockEntity be = teleporter();

        if (dropdownOpen) {
            int x = overlayX(), y = overlayY();
            List<TeleporterEntry> entries = entries();
            int visibleRows = overlayVisibleRows();
            boolean needsScrollbar = entries.size() > visibleRows;
            int rowWidth = needsScrollbar ? DROPDOWN_W - SCROLLBAR_W - 2 : DROPDOWN_W - 2;
            if (isIn(mx, my, x, y, DROPDOWN_W, overlayHeight())) {
                if (my < overlayRowsY()) {
                    dropdownOpen = false;
                    return true;
                }
                int rows = Math.min(visibleRows, entries.size());
                for (int i = 0; i < rows; i++) {
                    int idx = scroll + i;
                    if (idx >= entries.size()) break;
                    int rowY = overlayRowsY() + 1 + i * OVERLAY_ROW_H;
                    if (isIn(mx, my, x + 1, rowY, rowWidth, OVERLAY_ROW_H)) {
                        TeleporterEntry e = entries.get(idx);
                        if (nameBox != null && !nameBox.getValue().equals(lastSentName)) {
                            sendConfig(false);
                        }
                        ClientPacketDistributor.sendToServer(new SelectTeleporterPayload(e.dimension(), e.pos()));
                        dropdownOpen = false;
                        return true;
                    }
                }
                return true;
            }
            dropdownOpen = false;
        }

        if (isIn(mx, my, leftPos + DROPDOWN_X, topPos + DROPDOWN_Y, DROPDOWN_W, DROPDOWN_H)) {
            dropdownOpen = !dropdownOpen;
            scroll = 0;
            return true;
        }

        if (isIn(mx, my, leftPos + CLEAR_ITEMS_BTN_X, topPos + CLEAR_BTN_Y, CLEAR_ITEMS_BTN_W, CLEAR_BTN_H)) {
            ClientPacketDistributor.sendToServer(new ClearTeleporterPayload(true, false));
            return true;
        }
        if (isIn(mx, my, leftPos + CLEAR_FLUIDS_BTN_X, topPos + CLEAR_BTN_Y, CLEAR_FLUIDS_BTN_W, CLEAR_BTN_H)) {
            ClientPacketDistributor.sendToServer(new ClearTeleporterPayload(false, true));
            return true;
        }

        if (isIn(mx, my, leftPos + PUBLIC_BTN_X, topPos + PUBLIC_BTN_Y, 9, 10)) {
            togglePublic();
            return true;
        }

        boolean hasLink = be != null && be.linkedPos != null && be.linkedDimension != null;
        if (hasLink && isIn(mx, my, leftPos + UNLINK_BTN_X, topPos + UNLINK_BTN_Y, 9, 10)) {
            be.linkedPos = null;
            be.linkedDimension = null;
            be.linkedName = "";
            sendConfig(true);
            return true;
        }

        return super.mouseClicked(event, dragging);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double dx, double dy) {
        if (dropdownOpen) {
            int max = Math.max(0, entries().size() - overlayVisibleRows());
            scroll = Math.max(0, Math.min(max, scroll - (int) Math.signum(dy)));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, dx, dy);
    }
}
