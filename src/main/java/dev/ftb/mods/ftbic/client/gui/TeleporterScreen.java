package dev.ftb.mods.ftbic.client.gui;

import dev.ftb.mods.ftbic.block.entity.machine.TeleporterBlockEntity;
import dev.ftb.mods.ftbic.net.ConfigureTeleporterPayload;
import dev.ftb.mods.ftbic.net.SelectTeleporterPayload;
import dev.ftb.mods.ftbic.screen.TeleporterMenu;
import dev.ftb.mods.ftbic.util.TeleporterEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class TeleporterScreen extends ElectricBlockScreen<TeleporterMenu> {
	private static List<TeleporterEntry> CURRENT_ENTRIES = new ArrayList<>();

	public static void setEntries(List<TeleporterEntry> entries) {
		CURRENT_ENTRIES = entries == null ? new ArrayList<>() : entries;
	}

	private static final int ROW_HEIGHT = 10;
	private static final int LIST_X = 8;
	private static final int LIST_Y = 44;
	private static final int LIST_WIDTH = 160;
	private static final int LIST_VISIBLE_ROWS = 3;

	private static final int NAME_X = 8;
	private static final int NAME_Y = 16;
	private static final int NAME_W = 100;
	private static final int NAME_H = 12;

	private static final int PUBLIC_BTN_X = 112;
	private static final int PUBLIC_BTN_Y = 17;
	private static final int UNLINK_BTN_X = 160;
	private static final int UNLINK_BTN_Y = 30;

	private EditBox nameBox;
	private String lastSentName = "";
	private boolean lastSentPublic;
	private int scroll = 0;

	public TeleporterScreen(TeleporterMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
		drawDefaultArrow = false;
		energyX = 156;
		energyY = 17;
		CURRENT_ENTRIES = new ArrayList<>();
	}

	@Override
	protected void init() {
		super.init();
		TeleporterBlockEntity be = teleporter();
		String initialName = be == null ? "" : be.name;
		lastSentName = initialName;
		lastSentPublic = be != null && be.isPublic;

		nameBox = new EditBox(font, leftPos + NAME_X, topPos + NAME_Y, NAME_W, NAME_H,
				Component.translatable("block.ftbic.teleporter.name_label"));
		nameBox.setMaxLength(32);
		nameBox.setValue(initialName);
		nameBox.setResponder(s -> {});
		addRenderableWidget(nameBox);
	}

	private TeleporterBlockEntity teleporter() {
		return this.menu.blockEntity instanceof TeleporterBlockEntity t ? t : null;
	}

	private void sendConfig(boolean unlink) {
		if (nameBox == null) return;
		String newName = nameBox.getValue();
		TeleporterBlockEntity be = teleporter();
		boolean newPublic = be != null && be.isPublic;
		ClientPacketDistributor.sendToServer(new ConfigureTeleporterPayload(newName, newPublic, unlink));
		lastSentName = newName;
		lastSentPublic = newPublic;
	}

	private void togglePublic() {
		TeleporterBlockEntity be = teleporter();
		if (be == null) return;
		be.isPublic = !be.isPublic;
		ClientPacketDistributor.sendToServer(new ConfigureTeleporterPayload(
				nameBox == null ? be.name : nameBox.getValue(), be.isPublic, false));
		lastSentPublic = be.isPublic;
	}

	@Override
	public void removed() {
		if (nameBox != null && !nameBox.getValue().equals(lastSentName)) {
			sendConfig(false);
		}
		super.removed();
	}

	@Override
	protected void extractOverlays(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
		TeleporterBlockEntity be = teleporter();

		g.text(font, Component.translatable("block.ftbic.teleporter.public_label"),
				leftPos + PUBLIC_BTN_X + 11, topPos + PUBLIC_BTN_Y + 1, 0x404040, false);
		drawSmallRedstoneButton(g, leftPos + PUBLIC_BTN_X, topPos + PUBLIC_BTN_Y, mouseX, mouseY,
				be != null && be.isPublic);

		String linkText;
		boolean hasLink = be != null && be.linkedPos != null && be.linkedDimension != null;
		if (hasLink) {
			String display = be.linkedName != null && !be.linkedName.isEmpty() ? be.linkedName : "???";
			linkText = display + " (" + be.linkedPos.getX() + "," + be.linkedPos.getY() + "," + be.linkedPos.getZ() + ")";
		} else {
			linkText = Component.translatable("block.ftbic.teleporter.unlinked").getString();
		}
		g.text(font, Component.literal(linkText), leftPos + 8, topPos + 32, 0x404040, false);

		if (hasLink) {
			drawSmallQuestionButton(g, leftPos + UNLINK_BTN_X, topPos + UNLINK_BTN_Y, mouseX, mouseY);
		}

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
			String dimPath = e.dimension().identifier().getPath();
			String label = e.name() + " (" + e.pos().getX() + "," + e.pos().getY() + "," + e.pos().getZ() + ") [" + dimPath + "]";
			g.text(font, Component.literal(label).withStyle(color),
					leftPos + LIST_X + 2, rowY + 1, 0x404040, false);
		}

		if (entries.isEmpty()) {
			g.text(font, Component.translatable("block.ftbic.teleporter.empty")
							.withStyle(ChatFormatting.DARK_GRAY),
					leftPos + LIST_X + 2, topPos + LIST_Y + 1, 0x404040, false);
		}
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean dragging) {
		int mx = (int) event.x();
		int my = (int) event.y();
		TeleporterBlockEntity be = teleporter();

		if (isIn(mx, my, leftPos + PUBLIC_BTN_X, topPos + PUBLIC_BTN_Y, 9, 10)) {
			togglePublic();
			return true;
		}

		boolean hasLink = be != null && be.linkedPos != null && be.linkedDimension != null;
		if (hasLink && isIn(mx, my, leftPos + UNLINK_BTN_X, topPos + UNLINK_BTN_Y, 9, 10)) {
			be.linkedPos = null;
			be.linkedDimension = null;
			be.linkedName = "";
			ClientPacketDistributor.sendToServer(new ConfigureTeleporterPayload(
					nameBox == null ? be.name : nameBox.getValue(), be.isPublic, true));
			return true;
		}

		List<TeleporterEntry> entries = CURRENT_ENTRIES;
		int rows = Math.min(LIST_VISIBLE_ROWS, entries.size());
		for (int i = 0; i < rows; i++) {
			int idx = scroll + i;
			if (idx >= entries.size()) break;
			int rowY = topPos + LIST_Y + i * ROW_HEIGHT;
			if (mx >= leftPos + LIST_X && mx < leftPos + LIST_X + LIST_WIDTH
					&& my >= rowY && my < rowY + ROW_HEIGHT) {
				TeleporterEntry e = entries.get(idx);
				if (nameBox != null && !nameBox.getValue().equals(lastSentName)) {
					sendConfig(false);
				}
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
