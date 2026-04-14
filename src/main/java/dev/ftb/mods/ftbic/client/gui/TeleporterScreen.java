package dev.ftb.mods.ftbic.client.gui;

import dev.ftb.mods.ftbic.net.SelectTeleporterPayload;
import dev.ftb.mods.ftbic.screen.TeleporterMenu;
import dev.ftb.mods.ftbic.util.TeleporterEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.ArrayList;
import java.util.List;

/**
 * Destination-picker for the Teleporter. Lists every peer the server shipped via
 * {@link dev.ftb.mods.ftbic.net.TeleporterListPayload}; clicking a row sends a
 * {@link SelectTeleporterPayload} so the server-side TeleporterBlockEntity can bind to it.
 */
public class TeleporterScreen extends ElectricBlockScreen<TeleporterMenu> {
	/** Static so the {@link dev.ftb.mods.ftbic.net.TeleporterListPayload} handler can populate it without a screen reference. */
	private static List<TeleporterEntry> CURRENT_ENTRIES = new ArrayList<>();

	public static void setEntries(List<TeleporterEntry> entries) {
		CURRENT_ENTRIES = entries == null ? new ArrayList<>() : entries;
	}

	private static final int ROW_HEIGHT = 12;
	private static final int LIST_X = 8;
	private static final int LIST_Y = 18;
	private static final int LIST_WIDTH = 160;
	private static final int LIST_VISIBLE_ROWS = 10;

	private int scroll = 0;

	public TeleporterScreen(TeleporterMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
		drawDefaultArrow = false;
		energyX = -1;
		energyY = -1;
		// Clear stale entries from a previous screen — the server will ship the fresh list momentarily.
		CURRENT_ENTRIES = new ArrayList<>();
	}

	@Override
	protected void extractOverlays(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
List<TeleporterEntry> entries = CURRENT_ENTRIES;
		int rows = Math.min(LIST_VISIBLE_ROWS, entries.size());
		for (int i = 0; i < rows; i++) {
			int idx = scroll + i;
			if (idx >= entries.size()) break;
			TeleporterEntry e = entries.get(idx);
			int rowY = topPos + LIST_Y + i * ROW_HEIGHT;
			boolean hovered = mouseX >= leftPos + LIST_X && mouseX < leftPos + LIST_X + LIST_WIDTH
					&& mouseY >= rowY && mouseY < rowY + ROW_HEIGHT;
			ChatFormatting color = hovered ? ChatFormatting.YELLOW : ChatFormatting.WHITE;
			String label = e.name() + " (" + e.pos().getX() + ", " + e.pos().getY() + ", " + e.pos().getZ() + ")";
			g.text(font, Component.literal(label).withStyle(color),
					leftPos + LIST_X + 2, rowY + 2, 0xFFFFFF, false);
		}

		if (entries.isEmpty()) {
			g.text(font, Component.translatable("block.ftbic.teleporter.empty")
							.withStyle(ChatFormatting.GRAY),
					leftPos + LIST_X + 2, topPos + LIST_Y + 4, 0xAAAAAA, false);
		}
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean dragging) {
		int mx = (int) event.x();
		int my = (int) event.y();
		List<TeleporterEntry> entries = CURRENT_ENTRIES;
		int rows = Math.min(LIST_VISIBLE_ROWS, entries.size());
		for (int i = 0; i < rows; i++) {
			int idx = scroll + i;
			if (idx >= entries.size()) break;
			int rowY = topPos + LIST_Y + i * ROW_HEIGHT;
			if (mx >= leftPos + LIST_X && mx < leftPos + LIST_X + LIST_WIDTH
					&& my >= rowY && my < rowY + ROW_HEIGHT) {
				TeleporterEntry e = entries.get(idx);
				ClientPacketDistributor.sendToServer(new SelectTeleporterPayload(e.dimension(), e.pos()));
				onClose();
				return true;
			}
		}
		return super.mouseClicked(event, dragging);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double dx, double dy) {
		int max = Math.max(0, CURRENT_ENTRIES.size() - LIST_VISIBLE_ROWS);
		scroll = Math.max(0, Math.min(max, scroll - (int) Math.signum(dy)));
		return true;
	}
}
