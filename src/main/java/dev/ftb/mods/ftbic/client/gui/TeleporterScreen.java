package dev.ftb.mods.ftbic.client.gui;

import dev.ftb.mods.ftbic.block.entity.machine.TeleporterBlockEntity;
import dev.ftb.mods.ftbic.net.ConfigureTeleporterPayload;
import dev.ftb.mods.ftbic.net.SelectTeleporterPayload;
import dev.ftb.mods.ftbic.screen.TeleporterMenu;
import dev.ftb.mods.ftbic.util.TeleporterEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class TeleporterScreen extends ElectricBlockScreen<TeleporterMenu> {
	private static List<TeleporterEntry> CURRENT_ENTRIES = new ArrayList<>();

	public static void setEntries(List<TeleporterEntry> entries) {
		CURRENT_ENTRIES = entries == null ? new ArrayList<>() : entries;
	}

	private static final int GUI_HEIGHT = 196;
	private static final int EXTRA = GUI_HEIGHT - 166;

	private static final int NAME_X = 8, NAME_Y = 18, NAME_W = 90, NAME_H = 14;
	private static final int PUBLIC_BTN_X = 102, PUBLIC_BTN_Y = 20;
	private static final int DEST_Y = 38;
	private static final int UNLINK_BTN_X = 148, UNLINK_BTN_Y = 37;

	private static final int DROPDOWN_X = 8, DROPDOWN_Y = 52, DROPDOWN_W = 160, DROPDOWN_H = 12;
	private static final int OVERLAY_Y = 64;
	private static final int OVERLAY_ROW_H = 11;
	private static final int OVERLAY_VISIBLE_ROWS = 5;
	private static final int OVERLAY_H = OVERLAY_VISIBLE_ROWS * OVERLAY_ROW_H + 2;
	private static final int SCROLLBAR_W = 6;

	private EditBox nameBox;
	private String lastSentName = "";
	private boolean dropdownOpen;
	private int scroll;

	public TeleporterScreen(TeleporterMenu menu, Inventory inv, Component title) {
		super(menu, inv, title, 176, GUI_HEIGHT);
		drawDefaultArrow = false;
		energyX = 156;
		energyY = 18;
	}

	@Override
	protected void init() {
		super.init();
		TeleporterBlockEntity be = teleporter();
		String initialName = be == null ? "" : be.name;
		lastSentName = initialName;

		nameBox = new EditBox(font, leftPos + NAME_X, topPos + NAME_Y, NAME_W, NAME_H,
				Component.literal("Name"));
		nameBox.setMaxLength(32);
		nameBox.setValue(initialName);
		nameBox.setHint(Component.translatable("block.ftbic.teleporter.name_hint").withStyle(ChatFormatting.DARK_GRAY));
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

	private String formatEntry(String name, net.minecraft.core.BlockPos pos, ResourceKey<Level> dim) {
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
		if (event.key() == com.mojang.blaze3d.platform.InputConstants.KEY_RETURN
				&& nameBox != null && nameBox.isFocused()) {
			if (!nameBox.getValue().equals(lastSentName)) {
				sendConfig(false);
			}
			setFocused(null);
			return true;
		}
		return super.keyPressed(event);
	}

	@Override
	protected void drawBase(GuiGraphicsExtractor g) {
		g.blit(RenderPipelines.GUI_TEXTURED, BASE_TEXTURE, leftPos, topPos, 0F, 0F, imageWidth, 70, 256, 256);
		int filled = 0;
		while (filled < EXTRA) {
			int h = Math.min(4, EXTRA - filled);
			g.blit(RenderPipelines.GUI_TEXTURED, BASE_TEXTURE, leftPos, topPos + 70 + filled, 0F, 30F, imageWidth, h, 256, 256);
			filled += h;
		}
		g.blit(RenderPipelines.GUI_TEXTURED, BASE_TEXTURE, leftPos, topPos + 70 + EXTRA, 0F, 70F, imageWidth, 96, 256, 256);
	}

	@Override
	protected void extractOverlays(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
		TeleporterBlockEntity be = teleporter();

		drawSmallRedstoneButton(g, leftPos + PUBLIC_BTN_X, topPos + PUBLIC_BTN_Y, mouseX, mouseY,
				be != null && be.isPublic);
		g.text(font, Component.literal(be != null && be.isPublic ? "Public" : "Private"),
				leftPos + PUBLIC_BTN_X + 11, topPos + PUBLIC_BTN_Y + 1, 0xFF404040, false);

		boolean hasLink = be != null && be.linkedPos != null && be.linkedDimension != null;
		int statusMax = UNLINK_BTN_X - 8 - 4;
		String statusLabel;
		if (hasLink) {
			String display = be.linkedName != null && !be.linkedName.isEmpty() ? be.linkedName : "Unnamed";
			statusLabel = truncate("Linked: " + formatEntry(display, be.linkedPos, be.linkedDimension), statusMax);
		} else {
			statusLabel = truncate("Not linked — click below", statusMax);
		}
		g.text(font, Component.literal(statusLabel), leftPos + 8, topPos + DEST_Y, 0xFF404040, false);

		if (hasLink) {
			drawSmallQuestionButton(g, leftPos + UNLINK_BTN_X, topPos + UNLINK_BTN_Y, mouseX, mouseY);
		}

		drawDropdownButton(g, mouseX, mouseY);

		if (dropdownOpen) {
			drawDropdownOverlay(g, mouseX, mouseY);
		}

		extractCustomTooltips(g, mouseX, mouseY);
	}

	private void drawDropdownButton(GuiGraphicsExtractor g, int mouseX, int mouseY) {
		int x = leftPos + DROPDOWN_X, y = topPos + DROPDOWN_Y;
		g.fill(x, y, x + DROPDOWN_W, y + DROPDOWN_H, 0xFF555555);
		g.fill(x + 1, y + 1, x + DROPDOWN_W - 1, y + DROPDOWN_H - 1, 0xFFEEEEEE);
		boolean hovered = isIn(mouseX, mouseY, x, y, DROPDOWN_W, DROPDOWN_H);
		if (hovered) {
			g.fill(x + 1, y + 1, x + DROPDOWN_W - 1, y + DROPDOWN_H - 1, 0xFFDDEEFF);
		}
		String arrow = dropdownOpen ? "▲" : "▼";
		String label = CURRENT_ENTRIES.isEmpty() ? "No teleporters available" : "Choose destination";
		String text = truncate(label, DROPDOWN_W - 20);
		g.text(font, Component.literal(text), x + 4, y + 2, 0xFF202020, false);
		g.text(font, Component.literal(arrow), x + DROPDOWN_W - 10, y + 2, 0xFF202020, false);
	}

	private void drawDropdownOverlay(GuiGraphicsExtractor g, int mouseX, int mouseY) {
		int x = leftPos + DROPDOWN_X, y = topPos + OVERLAY_Y;
		int w = DROPDOWN_W;
		int h = OVERLAY_H;

		g.fill(x - 1, y - 1, x + w + 1, y + h + 1, 0xFF555555);
		g.fill(x, y, x + w, y + h, 0xFFEEEEEE);

		List<TeleporterEntry> entries = CURRENT_ENTRIES;
		boolean needsScrollbar = entries.size() > OVERLAY_VISIBLE_ROWS;
		int rowWidth = needsScrollbar ? w - SCROLLBAR_W - 2 : w - 2;

		if (entries.isEmpty()) {
			g.text(font, Component.literal("No teleporters found").withStyle(ChatFormatting.DARK_GRAY),
					x + 3, y + 3, 0xFF606060, false);
		}

		int rows = Math.min(OVERLAY_VISIBLE_ROWS, entries.size());
		for (int i = 0; i < rows; i++) {
			int idx = scroll + i;
			if (idx >= entries.size()) break;
			TeleporterEntry e = entries.get(idx);
			int rowY = y + 1 + i * OVERLAY_ROW_H;
			boolean hovered = isIn(mouseX, mouseY, x + 1, rowY, rowWidth, OVERLAY_ROW_H);
			if (hovered) {
				g.fill(x + 1, rowY, x + 1 + rowWidth, rowY + OVERLAY_ROW_H, 0xFFB8D6FF);
			}
			String label = truncate("▸ " + formatEntry(e.name(), e.pos(), e.dimension()), rowWidth - 4);
			g.text(font, Component.literal(label), x + 4, rowY + 2, 0xFF202020, false);
		}

		if (needsScrollbar) {
			int trackX = x + w - SCROLLBAR_W - 1;
			int trackY = y + 1;
			int trackH = h - 2;
			g.fill(trackX, trackY, trackX + SCROLLBAR_W, trackY + trackH, 0xFFB0B0B0);
			int thumbH = Math.max(10, trackH * OVERLAY_VISIBLE_ROWS / entries.size());
			int scrollMax = entries.size() - OVERLAY_VISIBLE_ROWS;
			int thumbY = trackY + (scrollMax <= 0 ? 0 : (trackH - thumbH) * scroll / scrollMax);
			g.fill(trackX, thumbY, trackX + SCROLLBAR_W, thumbY + thumbH, 0xFF606060);
		}
	}

	private void extractCustomTooltips(GuiGraphicsExtractor g, int mouseX, int mouseY) {
		TeleporterBlockEntity be = teleporter();
		if (isIn(mouseX, mouseY, leftPos + PUBLIC_BTN_X, topPos + PUBLIC_BTN_Y, 9, 10)) {
			Component tip = be != null && be.isPublic
					? Component.literal("Public — anyone can link to this teleporter. Click to make private.")
					: Component.literal("Private — only you can link to this teleporter. Click to make public.");
			g.setTooltipForNextFrame(tip, mouseX, mouseY);
			return;
		}
		boolean hasLink = be != null && be.linkedPos != null && be.linkedDimension != null;
		if (hasLink && isIn(mouseX, mouseY, leftPos + UNLINK_BTN_X, topPos + UNLINK_BTN_Y, 9, 10)) {
			g.setTooltipForNextFrame(Component.literal("Unlink destination"), mouseX, mouseY);
			return;
		}
		if (isIn(mouseX, mouseY, leftPos + NAME_X, topPos + NAME_Y, NAME_W, NAME_H)) {
			g.setTooltipForNextFrame(Component.literal("Give this teleporter a name so you can find it in other teleporters' lists."), mouseX, mouseY);
			return;
		}
		if (dropdownOpen) {
			List<TeleporterEntry> entries = CURRENT_ENTRIES;
			int x = leftPos + DROPDOWN_X, y = topPos + OVERLAY_Y;
			boolean needsScrollbar = entries.size() > OVERLAY_VISIBLE_ROWS;
			int rowWidth = needsScrollbar ? DROPDOWN_W - SCROLLBAR_W - 2 : DROPDOWN_W - 2;
			int rows = Math.min(OVERLAY_VISIBLE_ROWS, entries.size());
			for (int i = 0; i < rows; i++) {
				int idx = scroll + i;
				if (idx >= entries.size()) break;
				int rowY = y + 1 + i * OVERLAY_ROW_H;
				if (isIn(mouseX, mouseY, x + 1, rowY, rowWidth, OVERLAY_ROW_H)) {
					TeleporterEntry e = entries.get(idx);
					String cost = dev.ftb.mods.ftbic.util.FTBICUtils.formatEnergy(e.energyUse()).getString();
					g.setTooltipForNextFrame(Component.literal(formatEntry(e.name(), e.pos(), e.dimension())
							+ "\nCost: " + cost + " per jump\nClick to link"), mouseX, mouseY);
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
			int x = leftPos + DROPDOWN_X, y = topPos + OVERLAY_Y;
			List<TeleporterEntry> entries = CURRENT_ENTRIES;
			boolean needsScrollbar = entries.size() > OVERLAY_VISIBLE_ROWS;
			int rowWidth = needsScrollbar ? DROPDOWN_W - SCROLLBAR_W - 2 : DROPDOWN_W - 2;
			if (isIn(mx, my, x, y, DROPDOWN_W, OVERLAY_H)) {
				int rows = Math.min(OVERLAY_VISIBLE_ROWS, entries.size());
				for (int i = 0; i < rows; i++) {
					int idx = scroll + i;
					if (idx >= entries.size()) break;
					int rowY = y + 1 + i * OVERLAY_ROW_H;
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
			int max = Math.max(0, CURRENT_ENTRIES.size() - OVERLAY_VISIBLE_ROWS);
			scroll = Math.max(0, Math.min(max, scroll - (int) Math.signum(dy)));
			return true;
		}
		return super.mouseScrolled(mouseX, mouseY, dx, dy);
	}
}
