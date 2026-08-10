package dev.ftb.mods.industrialcontraptions.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import dev.ftb.mods.industrialcontraptions.IC;
import dev.ftb.mods.industrialcontraptions.block.entity.machine.ReactorSimulatorBlockEntity;
import dev.ftb.mods.industrialcontraptions.ICConfig;
import dev.ftb.mods.industrialcontraptions.item.reactor.CoolantItem;
import dev.ftb.mods.industrialcontraptions.item.reactor.FuelRodItem;
import dev.ftb.mods.industrialcontraptions.item.reactor.HeatExchangerItem;
import dev.ftb.mods.industrialcontraptions.item.reactor.HeatVentItem;
import dev.ftb.mods.industrialcontraptions.item.reactor.NeutronReflectorItem;
import dev.ftb.mods.industrialcontraptions.item.reactor.NuclearReactor;
import dev.ftb.mods.industrialcontraptions.item.reactor.ReactorItem;
import dev.ftb.mods.industrialcontraptions.item.reactor.ReactorPlatingItem;
import dev.ftb.mods.industrialcontraptions.net.SimulatorActionPayload;
import dev.ftb.mods.industrialcontraptions.screen.ReactorSimulatorMenu;
import dev.ftb.mods.industrialcontraptions.util.ReactorDesign;
import dev.ftb.mods.industrialcontraptions.util.ReactorPresetLibrary;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class ReactorSimulatorScreen extends ElectricBlockScreen<ReactorSimulatorMenu> {
	public static final Identifier TEXTURE = IC.id("textures/gui/nuclear_reactor.png");
	public static final TagKey<Item> COMPONENT_TAG = TagKey.create(Registries.ITEM, IC.id("reactor_component"));

	private static final int MAIN_W = 224;
	private static final int PICKER_W = 97;
	private static final int TOTAL_W = MAIN_W + PICKER_W;

	private static final int GRID_LEFT = 8;
	private static final int GRID_TOP = 17;
	private static final int GRID_BOTTOM = GRID_TOP + 6 * 18;

	private static final int PANEL_TOP = GRID_BOTTOM;
	private static final int PANEL_H = 90;

	private static final int BUTTON_H = 13;
	private static final int STEPPER_BTN_W = 12;

	private static final int ROW_SPEED_Y = PANEL_TOP + 3;
	private static final int ROW_CONTROL_Y = PANEL_TOP + 19;
	private static final int ROW_STEPPERS_Y = PANEL_TOP + 35;
	private static final int ROW_VERDICT_Y = PANEL_TOP + 52;
	private static final int ROW_STATS_Y = PANEL_TOP + 62;
	private static final int ROW_BUTTONS_Y = PANEL_TOP + 73;

	private static final int CONTROL_BTN_W = 50;
	private static final int CONTROL_BTN_GAP = 4;
	private static final int CONTROL_BTN_START_X = 6;
	private static final int STEPPER_W = 102;
	private static final int BOTTOM_BTN_W = 64;
	private static final int BOTTOM_BTN_GAP = 6;
	private static final int BOTTOM_BTN_START_X = 10;

	private static final int PICKER_X = MAIN_W + 4;
	private static final int PICKER_Y = 17;
	private static final int PICKER_COLS = 4;
	private static final int PICKER_SLOT = 18;
	private static final int PICKER_TITLE_H = 12;
	private static final int PICKER_INNER_X = PICKER_X + 4;
	private static final int PICKER_INNER_Y = PICKER_Y + PICKER_TITLE_H;

	private static final int PRESET_H = 52;
	private static final int PRESET_ICON_W = 16;
	private static final int PRESET_ICON_GAP = 4;

	private final List<ItemStack> pickerItems = new ArrayList<>();
	private int pickerScrollRow = 0;
	private int pickerVisibleRows;
	private int pickerTotalRows;
	private ItemStack pending = ItemStack.EMPTY;

	private final List<ReactorPresetLibrary.Preset> presets = new ArrayList<>();
	private int selectedPreset = 0;
	private boolean dropdownOpen = false;
	private boolean saveMode = false;
	private EditBox saveNameBox;
	private String saveError = "";

	public ReactorSimulatorScreen(ReactorSimulatorMenu menu, Inventory inv, Component title) {
		super(menu, inv, title, TOTAL_W, PANEL_TOP + PANEL_H + 4);
		drawDefaultArrow = false;
		energyX = -1;
		energyY = -1;
	}

	@Override
	protected Identifier getScreenTexture() {
		return TEXTURE;
	}

	@Override
	protected void init() {
		super.init();
		this.titleLabelX = 8;
		this.inventoryLabelX = Short.MAX_VALUE;
		this.inventoryLabelY = Short.MAX_VALUE;

		pickerItems.clear();
		for (Holder<Item> h : BuiltInRegistries.ITEM.getTagOrEmpty(COMPONENT_TAG)) {
			Item it = h.value();
			if (it instanceof ReactorItem) pickerItems.add(new ItemStack(it));
		}
		pickerTotalRows = (pickerItems.size() + PICKER_COLS - 1) / PICKER_COLS;
		int pickerAreaH = imageHeight - PICKER_INNER_Y - 4 - PRESET_H;
		pickerVisibleRows = Math.max(1, pickerAreaH / PICKER_SLOT);
		pickerScrollRow = 0;

		refreshPresets();
		saveMode = false;
		saveError = "";
		dropdownOpen = false;
		pending = ItemStack.EMPTY;

		int boxX = leftPos + PICKER_X + 4;
		int boxY = topPos + getPresetTopY() + 14;
		int boxW = getPresetPanelWidth() - 8;
		saveNameBox = new EditBox(font, boxX, boxY, boxW, 12, Component.translatable("ic.gui.reactor_sim.save_name_label"));
		saveNameBox.setMaxLength(ReactorPresetLibrary.MAX_NAME_LENGTH);
		saveNameBox.setVisible(false);
		saveNameBox.setHint(Component.literal("a-z 0-9 _ - . /").withStyle(ChatFormatting.DARK_GRAY));
		addRenderableWidget(saveNameBox);
	}

	private int getPresetTopY() {
		return imageHeight - 4 - PRESET_H;
	}

	private int getPresetPanelWidth() {
		return imageWidth - PICKER_X - 9;
	}

	private void refreshPresets() {
		presets.clear();
		presets.addAll(ReactorPresetLibrary.listAll());
		if (selectedPreset >= presets.size()) selectedPreset = 0;
	}

	@Override
	public void removed() {
		super.removed();
		pending = ItemStack.EMPTY;
	}

	@Override
	protected void drawBase(GuiGraphicsExtractor g) {
		int x0 = leftPos;
		int y0 = topPos;
		int x1 = leftPos + imageWidth;
		int y1 = topPos + imageHeight;
		g.fill(x0, y0, x1, y1, 0xFF373737);
		g.fill(x0 + 3, y0 + 3, x1 - 3, y1 - 3, 0xFFC6C6C6);
	}

	@Override
	protected void extractOverlays(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
		int gridLeft = leftPos + GRID_LEFT - 1;
		int overlayColor = 0xFF8B8B8B;

		g.fill(leftPos + MAIN_W - 1, topPos + 14, leftPos + MAIN_W + 1, topPos + imageHeight - 4, 0xFF6E6E6E);

		for (int row = 0; row < 6; row++) {
			for (int col = 0; col < 9; col++) {
				drawSlot(g, gridLeft + col * 18, topPos + GRID_TOP + row * 18);
			}
		}

		int active = Math.max(3, Math.min(9, 3 + menu.getChambers()));
		if (active < 9) {
			int maskX = gridLeft + 1 + active * 18;
			int maskW = (9 - active) * 18;
			g.fill(maskX, topPos + GRID_TOP, maskX + maskW, topPos + GRID_BOTTOM, overlayColor);
		}

		drawSpeedRow(g, mouseX, mouseY);
		drawControlRow(g, mouseX, mouseY);
		drawStepperRow(g, leftPos + 8, topPos + ROW_STEPPERS_Y, STEPPER_W,
				Component.translatable("ic.gui.reactor_sim.chambers", menu.getChambers(), 6).getString(), mouseX, mouseY);
		drawStepperRow(g, leftPos + 8 + STEPPER_W + 4, topPos + ROW_STEPPERS_Y, STEPPER_W,
				Component.translatable("ic.gui.reactor_sim.water", String.format("%.2f", menu.getWaterThousandths() / 1000D)).getString(), mouseX, mouseY);
		drawVerdictRow(g);
		drawStatsRow(g);
		drawBottomRow(g, mouseX, mouseY);
		drawPicker(g, mouseX, mouseY);
		drawPresetSection(g, mouseX, mouseY);

		ItemStack hovered = pickerSlotAt(mouseX, mouseY);
		if (!hovered.isEmpty() && pending.isEmpty()) {
			List<Component> lines = new ArrayList<>(Screen.getTooltipFromItem(Minecraft.getInstance(), hovered));
			addReactorEffectLines(hovered, lines);
			g.setComponentTooltipForNextFrame(font, lines, mouseX, mouseY, hovered);
		} else if (pending.isEmpty()) {
			maybeDrawContextTooltip(g, mouseX, mouseY);
			maybeDrawPresetTooltip(g, mouseX, mouseY);
		}

		if (dropdownOpen) drawDropdownOverlay(g, mouseX, mouseY);

		if (!pending.isEmpty()) {
			g.item(pending, mouseX - 8, mouseY - 8);
		}
	}

	private void textCenter(GuiGraphicsExtractor g, String label, int centerX, int y, int color) {
		int w = font.width(label);
		g.text(font, Component.literal(label), centerX - w / 2, y, color, false);
	}

	private void addReactorEffectLines(ItemStack stack, List<Component> out) {
		Item it = stack.getItem();
		if (it instanceof FuelRodItem rod) {
			out.add(Component.empty());
			out.add(Component.translatable("ic.gui.reactor_sim.item.fuel_rod.title").withStyle(ChatFormatting.YELLOW));
			out.add(Component.translatable("ic.gui.reactor_sim.item.fuel_rod.base_pulses", rod.pulses).withStyle(ChatFormatting.GRAY));
			out.add(Component.translatable("ic.gui.reactor_sim.item.fuel_rod.energy", fmt(rod.energyMultiplier)).withStyle(ChatFormatting.AQUA));
			out.add(Component.translatable("ic.gui.reactor_sim.item.fuel_rod.heat", fmt(rod.heatMultiplier)).withStyle(ChatFormatting.RED));
			out.add(Component.translatable("ic.gui.reactor_sim.item.fuel_rod.spread").withStyle(ChatFormatting.DARK_GRAY));
			int heatP1 = (int) Math.round(rod.heatMultiplier * rod.pulses * (rod.pulses + 1));
			int heatMax = (int) Math.round(rod.heatMultiplier * (rod.pulses + 4) * (rod.pulses + 5));
			out.add(Component.translatable("ic.gui.reactor_sim.item.fuel_rod.heat_example", rod.pulses, heatP1, rod.pulses + 4, heatMax).withStyle(ChatFormatting.DARK_GRAY));
			out.add(Component.translatable("ic.gui.reactor_sim.item.fuel_rod.durability", rod.maxDurability).withStyle(ChatFormatting.DARK_GRAY));
		} else if (it instanceof HeatVentItem vent) {
			out.add(Component.empty());
			out.add(Component.translatable("ic.gui.reactor_sim.item.heat_vent.title").withStyle(ChatFormatting.YELLOW));
			if (vent.maxHeat > 0) {
				out.add(Component.translatable("ic.gui.reactor_sim.item.heat_vent.heat_buffer", vent.maxHeat).withStyle(ChatFormatting.GRAY));
			} else {
				out.add(Component.translatable("ic.gui.reactor_sim.item.heat_vent.no_buffer").withStyle(ChatFormatting.DARK_GRAY));
			}
			if (vent.selfCool > 0) {
				out.add(Component.translatable("ic.gui.reactor_sim.item.heat_vent.self_cool", vent.selfCool).withStyle(ChatFormatting.AQUA));
			}
			if (vent.reactorCool > 0) {
				out.add(Component.translatable("ic.gui.reactor_sim.item.heat_vent.reactor_cool", vent.reactorCool).withStyle(ChatFormatting.AQUA));
				out.add(Component.translatable("ic.gui.reactor_sim.item.heat_vent.water_hint").withStyle(ChatFormatting.DARK_GRAY));
			}
			if (vent.componentCool > 0) {
				out.add(Component.translatable("ic.gui.reactor_sim.item.heat_vent.component_cool", vent.componentCool).withStyle(ChatFormatting.AQUA));
			}
		} else if (it instanceof HeatExchangerItem ex) {
			out.add(Component.empty());
			out.add(Component.translatable("ic.gui.reactor_sim.item.exchanger.title").withStyle(ChatFormatting.YELLOW));
			out.add(Component.translatable("ic.gui.reactor_sim.item.exchanger.heat_buffer", ex.maxHeat).withStyle(ChatFormatting.GRAY));
			if (ex.heatTransferToAdjacent > 0) {
				out.add(Component.translatable("ic.gui.reactor_sim.item.exchanger.adjacent", ex.heatTransferToAdjacent).withStyle(ChatFormatting.AQUA));
			}
			if (ex.heatTransferToCore > 0) {
				out.add(Component.translatable("ic.gui.reactor_sim.item.exchanger.core", ex.heatTransferToCore).withStyle(ChatFormatting.AQUA));
			}
			out.add(Component.translatable("ic.gui.reactor_sim.item.exchanger.transfer_direction").withStyle(ChatFormatting.DARK_GRAY));
		} else if (it instanceof CoolantItem cool) {
			out.add(Component.empty());
			out.add(Component.translatable("ic.gui.reactor_sim.item.coolant.title").withStyle(ChatFormatting.YELLOW));
			out.add(Component.translatable("ic.gui.reactor_sim.item.coolant.capacity", cool.maxHeat).withStyle(ChatFormatting.AQUA));
			out.add(Component.translatable("ic.gui.reactor_sim.item.coolant.passive").withStyle(ChatFormatting.GRAY));
			out.add(Component.translatable("ic.gui.reactor_sim.item.coolant.vent_pair").withStyle(ChatFormatting.DARK_GRAY));
		} else if (it instanceof NeutronReflectorItem ref) {
			out.add(Component.empty());
			out.add(Component.translatable("ic.gui.reactor_sim.item.reflector.title").withStyle(ChatFormatting.YELLOW));
			out.add(Component.translatable("ic.gui.reactor_sim.item.reflector.pulse_boost").withStyle(ChatFormatting.AQUA));
			out.add(Component.translatable("ic.gui.reactor_sim.item.reflector.energy_heat_note").withStyle(ChatFormatting.GRAY));
			if (ref.maxDurability > 0) {
				out.add(Component.translatable("ic.gui.reactor_sim.item.reflector.durability", ref.maxDurability).withStyle(ChatFormatting.DARK_GRAY));
			} else {
				out.add(Component.translatable("ic.gui.reactor_sim.item.reflector.infinite").withStyle(ChatFormatting.GOLD));
			}
		} else if (it instanceof ReactorPlatingItem pl) {
			out.add(Component.empty());
			out.add(Component.translatable("ic.gui.reactor_sim.item.plating.title").withStyle(ChatFormatting.YELLOW));
			if (pl.heatCapacity > 0) {
				out.add(Component.translatable("ic.gui.reactor_sim.item.plating.heat_capacity", pl.heatCapacity).withStyle(ChatFormatting.AQUA));
			}
			double pct = (1.0 - pl.explosionResistance) * 100.0;
			out.add(Component.translatable("ic.gui.reactor_sim.item.plating.blast", fmt(pl.explosionResistance), String.format("-%.0f%%", pct)).withStyle(ChatFormatting.GOLD));
			out.add(Component.translatable("ic.gui.reactor_sim.item.plating.multiply_hint").withStyle(ChatFormatting.DARK_GRAY));
		}
	}

	private static String fmt(double v) {
		if (v == Math.floor(v) && !Double.isInfinite(v)) return String.valueOf((long) v);
		return String.format("%.2f", v);
	}

	private void maybeDrawContextTooltip(GuiGraphicsExtractor g, int mouseX, int mouseY) {
		int stY = topPos + ROW_STEPPERS_Y;
		int chX = leftPos + 8;
		int waX = leftPos + 8 + STEPPER_W + 4;
		if (isIn(mouseX, mouseY, chX, stY, STEPPER_W, BUTTON_H)) {
			int cols = 3 + menu.getChambers();
			g.setComponentTooltipForNextFrame(font, List.of(
					Component.translatable("ic.gui.reactor_sim.chambers_title").withStyle(ChatFormatting.YELLOW),
					Component.translatable("ic.gui.reactor_sim.chambers_desc1").withStyle(ChatFormatting.GRAY),
					Component.translatable("ic.gui.reactor_sim.chambers_desc2", cols).withStyle(ChatFormatting.GRAY),
					Component.translatable("ic.gui.reactor_sim.chambers_desc3").withStyle(ChatFormatting.DARK_GRAY)
			), mouseX, mouseY, ItemStack.EMPTY);
			return;
		}
		if (isIn(mouseX, mouseY, waX, stY, STEPPER_W, BUTTON_H)) {
			double factor = menu.getWaterThousandths() / 1000D;
			double max = ICConfig.NUCLEAR.WATER_COOLING_MULTIPLIER.get();
			double mult = 1D + factor * (max - 1D);
			g.setComponentTooltipForNextFrame(font, List.of(
					Component.translatable("ic.gui.reactor_sim.water_title").withStyle(ChatFormatting.YELLOW),
					Component.translatable("ic.gui.reactor_sim.water_desc1").withStyle(ChatFormatting.GRAY),
					Component.translatable("ic.gui.reactor_sim.water_desc2", String.format("%.2f", mult)).withStyle(ChatFormatting.AQUA),
					Component.translatable("ic.gui.reactor_sim.water_desc3").withStyle(ChatFormatting.DARK_GRAY),
					Component.translatable("ic.gui.reactor_sim.water_desc4", String.format("%.2f", max)).withStyle(ChatFormatting.DARK_GRAY)
			), mouseX, mouseY, ItemStack.EMPTY);
			return;
		}
		if (isIn(mouseX, mouseY, leftPos + 8, topPos + ROW_STATS_Y, MAIN_W - 16, 10)
				|| isIn(mouseX, mouseY, leftPos + 8, topPos + ROW_VERDICT_Y, MAIN_W - 16, 10)) {
			g.setComponentTooltipForNextFrame(font, List.of(
					Component.translatable("ic.gui.reactor_sim.stats_title").withStyle(ChatFormatting.YELLOW),
					Component.translatable("ic.gui.reactor_sim.stats_desc1").withStyle(ChatFormatting.GRAY),
					Component.translatable("ic.gui.reactor_sim.stats_desc2").withStyle(ChatFormatting.GRAY),
					Component.translatable("ic.gui.reactor_sim.stats_desc3").withStyle(ChatFormatting.GRAY),
					Component.translatable("ic.gui.reactor_sim.stats_desc4").withStyle(ChatFormatting.GRAY),
					Component.translatable("ic.gui.reactor_sim.stats_desc5").withStyle(ChatFormatting.DARK_GRAY)
			), mouseX, mouseY, ItemStack.EMPTY);
		}
	}

	private ItemStack pickerSlotAt(int mouseX, int mouseY) {
		int innerX = leftPos + PICKER_INNER_X;
		int innerY = topPos + PICKER_INNER_Y;
		for (int row = 0; row < pickerVisibleRows; row++) {
			int actualRow = row + pickerScrollRow;
			if (actualRow >= pickerTotalRows) break;
			for (int col = 0; col < PICKER_COLS; col++) {
				int idx = actualRow * PICKER_COLS + col;
				if (idx >= pickerItems.size()) break;
				int sx = innerX + col * PICKER_SLOT;
				int sy = innerY + row * PICKER_SLOT;
				if (isIn(mouseX, mouseY, sx + 1, sy + 1, 16, 16)) {
					return pickerItems.get(idx);
				}
			}
		}
		return ItemStack.EMPTY;
	}

	private int gridSlotAt(int mouseX, int mouseY) {
		int gridLeft = leftPos + GRID_LEFT;
		int gridTop = topPos + GRID_TOP;
		int col = (mouseX - gridLeft) / 18;
		int row = (mouseY - gridTop) / 18;
		int relX = (mouseX - gridLeft) % 18;
		int relY = (mouseY - gridTop) % 18;
		int active = Math.max(3, Math.min(9, 3 + menu.getChambers()));
		if (col >= 0 && col < active && row >= 0 && row < 6 && relX >= 0 && relY >= 0 && relX < 16 && relY < 16) {
			return row * NuclearReactor.MAX_COLUMNS + col;
		}
		return -1;
	}

	private void drawPicker(GuiGraphicsExtractor g, int mouseX, int mouseY) {
		int py = topPos + PICKER_Y;
		int gridCenterX = leftPos + PICKER_INNER_X + (PICKER_COLS * PICKER_SLOT) / 2;

		textCenter(g, Component.translatable("ic.gui.reactor_sim.components").getString(), gridCenterX, py + 3, 0xFF000000);

		int innerX = leftPos + PICKER_INNER_X;
		int innerY = topPos + PICKER_INNER_Y;
		boolean needsScrollbar = pickerTotalRows > pickerVisibleRows;
		int gridW = PICKER_COLS * PICKER_SLOT;
		int scrollTrackX = innerX + gridW + 2;

		for (int row = 0; row < pickerVisibleRows; row++) {
			int actualRow = row + pickerScrollRow;
			if (actualRow >= pickerTotalRows) break;
			for (int col = 0; col < PICKER_COLS; col++) {
				int idx = actualRow * PICKER_COLS + col;
				if (idx >= pickerItems.size()) break;
				int sx = innerX + col * PICKER_SLOT;
				int sy = innerY + row * PICKER_SLOT;
				drawSlot(g, sx, sy);
				boolean hover = isIn(mouseX, mouseY, sx + 1, sy + 1, 16, 16);
				if (hover) g.fill(sx + 1, sy + 1, sx + 17, sy + 17, 0x80FFFFFF);
				g.item(pickerItems.get(idx), sx + 1, sy + 1);
			}
		}

		if (needsScrollbar) {
			int trackY = innerY;
			int trackH = pickerVisibleRows * PICKER_SLOT;
			g.fill(scrollTrackX, trackY, scrollTrackX + 6, trackY + trackH, 0xFF3C3C3C);
			int thumbH = Math.max(8, trackH * pickerVisibleRows / pickerTotalRows);
			int scrollMax = pickerTotalRows - pickerVisibleRows;
			int thumbY = trackY + (scrollMax <= 0 ? 0 : (trackH - thumbH) * pickerScrollRow / scrollMax);
			g.fill(scrollTrackX, thumbY, scrollTrackX + 6, thumbY + thumbH, 0xFFCECECE);
		}
	}

	private void drawPresetSection(GuiGraphicsExtractor g, int mouseX, int mouseY) {
		int x = leftPos + PICKER_X;
		int y = topPos + getPresetTopY();
		int w = getPresetPanelWidth();

		g.fill(x + 2, y - 1, x + w - 2, y, 0xFF6E6E6E);
		textCenter(g, Component.translatable("ic.gui.reactor_sim.presets").getString(), x + w / 2, y + 1, 0xFF000000);

		if (saveMode) {
			drawSaveRow(g, x, y, w, mouseX, mouseY);
			return;
		}

		int ddX = x + 4;
		int ddY = y + 12;
		int ddW = w - 8;
		int ddH = 13;
		boolean ddHover = isIn(mouseX, mouseY, ddX, ddY, ddW, ddH);
		g.fill(ddX, ddY, ddX + ddW, ddY + ddH, 0xFF333333);
		g.fill(ddX + 1, ddY + 1, ddX + ddW - 1, ddY + ddH - 1, ddHover ? 0xFFBCE0FF : 0xFFE0E0E0);
		String label = presets.isEmpty()
				? Component.translatable("ic.gui.reactor_sim.no_presets").getString()
				: truncate(presets.get(selectedPreset).name(), ddW - 14);
		g.text(font, Component.literal(label), ddX + 4, ddY + 3, 0xFF000000, false);
		int arrowX = ddX + ddW - 8;
		int arrowY = ddY + 5;
		g.fill(arrowX, arrowY, arrowX + 5, arrowY + 1, 0xFF000000);
		g.fill(arrowX + 1, arrowY + 1, arrowX + 4, arrowY + 2, 0xFF000000);
		g.fill(arrowX + 2, arrowY + 2, arrowX + 3, arrowY + 3, 0xFF000000);

		int iconRowY = ddY + ddH + 4;
		int iconsTotalW = 3 * PRESET_ICON_W + 2 * PRESET_ICON_GAP;
		int iconX = x + (w - iconsTotalW) / 2;
		drawLoadIcon(g, iconX, iconRowY, mouseX, mouseY, !presets.isEmpty());
		drawSaveIcon(g, iconX + PRESET_ICON_W + PRESET_ICON_GAP, iconRowY, mouseX, mouseY, !menu.isEditLocked());
		boolean canRemove = !presets.isEmpty() && !presets.get(selectedPreset).builtin();
		drawRemoveIcon(g, iconX + (PRESET_ICON_W + PRESET_ICON_GAP) * 2, iconRowY, mouseX, mouseY, canRemove);
	}

	private void drawSaveRow(GuiGraphicsExtractor g, int x, int y, int w, int mouseX, int mouseY) {
		int boxY = y + 14;
		saveNameBox.setVisible(true);
		saveNameBox.setEditable(true);
		saveNameBox.setX(x + 4);
		saveNameBox.setY(boxY);

		int iconRowY = boxY + 14;
		int iconsTotalW = 2 * PRESET_ICON_W + PRESET_ICON_GAP;
		int iconX = x + (w - iconsTotalW) / 2;
		drawConfirmIcon(g, iconX, iconRowY, mouseX, mouseY);
		drawCancelIcon(g, iconX + PRESET_ICON_W + PRESET_ICON_GAP, iconRowY, mouseX, mouseY);

		if (!saveError.isEmpty()) {
			textCenter(g, saveError, x + w / 2, iconRowY + PRESET_ICON_W + 1, 0xFFFF5555);
		}
	}

	private void drawDropdownOverlay(GuiGraphicsExtractor g, int mouseX, int mouseY) {
		int x = leftPos + PICKER_X;
		int y = topPos + getPresetTopY();
		int w = getPresetPanelWidth();
		int ddX = x + 4;
		int ddY = y + 12;
		int ddW = w - 8;
		int rowH = 11;
		int topCap = topPos + PICKER_INNER_Y;
		int listMaxH = ddY - 2 - topCap;
		int visibleCount = Math.min(presets.size(), Math.max(1, listMaxH / rowH));
		int listVisibleH = visibleCount * rowH;
		int listY = ddY - 2 - listVisibleH;

		g.fill(ddX - 1, listY - 1, ddX + ddW + 1, listY + listVisibleH + 1, 0xFF000000);
		g.fill(ddX, listY, ddX + ddW, listY + listVisibleH, 0xFFE0E0E0);

		for (int i = 0; i < visibleCount; i++) {
			ReactorPresetLibrary.Preset p = presets.get(i);
			int ry = listY + i * rowH;
			boolean hover = isIn(mouseX, mouseY, ddX, ry, ddW, rowH);
			if (hover) g.fill(ddX, ry, ddX + ddW, ry + rowH, 0xFFBCE0FF);
			int color = p.builtin() ? 0xFF1F4E80 : 0xFF000000;
			g.text(font, Component.literal(truncate(p.name(), ddW - 8)), ddX + 3, ry + 2, color, false);
		}
	}

	private int dropdownRowAt(int mouseX, int mouseY) {
		int x = leftPos + PICKER_X;
		int y = topPos + getPresetTopY();
		int ddX = x + 4;
		int ddY = y + 12;
		int ddW = getPresetPanelWidth() - 8;
		int rowH = 11;
		int topCap = topPos + PICKER_INNER_Y;
		int listMaxH = ddY - 2 - topCap;
		int visibleCount = Math.min(presets.size(), Math.max(1, listMaxH / rowH));
		int listVisibleH = visibleCount * rowH;
		int listY = ddY - 2 - listVisibleH;
		for (int i = 0; i < visibleCount; i++) {
			int ry = listY + i * rowH;
			if (isIn(mouseX, mouseY, ddX, ry, ddW, rowH)) return i;
		}
		return -1;
	}

	private String truncate(String s, int maxPx) {
		if (font.width(s) <= maxPx) return s;
		String ellipsis = "...";
		while (!s.isEmpty() && font.width(s + ellipsis) > maxPx) {
			s = s.substring(0, s.length() - 1);
		}
		return s + ellipsis;
	}

	private void drawIconButton(GuiGraphicsExtractor g, int x, int y, int mouseX, int mouseY, boolean enabled) {
		boolean hover = enabled && isIn(mouseX, mouseY, x, y, PRESET_ICON_W, PRESET_ICON_W);
		g.fill(x, y, x + PRESET_ICON_W, y + PRESET_ICON_W, 0xFF333333);
		int fill = !enabled ? 0xFF8B8B8B : (hover ? 0xFFBCE0FF : 0xFFE0E0E0);
		g.fill(x + 1, y + 1, x + PRESET_ICON_W - 1, y + PRESET_ICON_W - 1, fill);
	}

	private void drawLoadIcon(GuiGraphicsExtractor g, int x, int y, int mouseX, int mouseY, boolean enabled) {
		drawIconButton(g, x, y, mouseX, mouseY, enabled);
		int color = enabled ? 0xFF000000 : 0xFF555555;
		int cx = x + PRESET_ICON_W / 2;
		g.fill(cx - 1, y + 4, cx + 1, y + 9, color);
		g.fill(cx - 3, y + 8, cx + 3, y + 10, color);
		g.fill(cx - 2, y + 9, cx + 2, y + 11, color);
		g.fill(cx - 1, y + 10, cx + 1, y + 12, color);
		g.fill(x + 4, y + 12, x + PRESET_ICON_W - 4, y + 13, color);
	}

	private void drawSaveIcon(GuiGraphicsExtractor g, int x, int y, int mouseX, int mouseY, boolean enabled) {
		drawIconButton(g, x, y, mouseX, mouseY, enabled);
		int color = enabled ? 0xFF000000 : 0xFF555555;
		g.fill(x + 3, y + 3, x + PRESET_ICON_W - 3, y + PRESET_ICON_W - 3, color);
		int inner = enabled ? 0xFFE0E0E0 : 0xFF8B8B8B;
		g.fill(x + 4, y + 4, x + PRESET_ICON_W - 4, y + 7, inner);
		g.fill(x + 5, y + 5, x + PRESET_ICON_W - 5, y + 6, color);
		g.fill(x + 5, y + 9, x + PRESET_ICON_W - 5, y + PRESET_ICON_W - 4, inner);
	}

	private void drawRemoveIcon(GuiGraphicsExtractor g, int x, int y, int mouseX, int mouseY, boolean enabled) {
		drawIconButton(g, x, y, mouseX, mouseY, enabled);
		int color = enabled ? 0xFFAA0000 : 0xFF555555;
		for (int i = 0; i < 8; i++) {
			g.fill(x + 4 + i, y + 4 + i, x + 5 + i, y + 5 + i, color);
			g.fill(x + 11 - i, y + 4 + i, x + 12 - i, y + 5 + i, color);
		}
	}

	private void drawConfirmIcon(GuiGraphicsExtractor g, int x, int y, int mouseX, int mouseY) {
		drawIconButton(g, x, y, mouseX, mouseY, true);
		int color = 0xFF005500;
		for (int i = 0; i < 3; i++) {
			g.fill(x + 3 + i, y + 7 + i, x + 4 + i, y + 8 + i, color);
		}
		for (int i = 0; i < 6; i++) {
			g.fill(x + 6 + i, y + 9 - i, x + 7 + i, y + 10 - i, color);
		}
	}

	private void drawCancelIcon(GuiGraphicsExtractor g, int x, int y, int mouseX, int mouseY) {
		drawIconButton(g, x, y, mouseX, mouseY, true);
		int color = 0xFFAA0000;
		for (int i = 0; i < 8; i++) {
			g.fill(x + 4 + i, y + 4 + i, x + 5 + i, y + 5 + i, color);
			g.fill(x + 11 - i, y + 4 + i, x + 12 - i, y + 5 + i, color);
		}
	}

	private void maybeDrawPresetTooltip(GuiGraphicsExtractor g, int mouseX, int mouseY) {
		int x = leftPos + PICKER_X;
		int y = topPos + getPresetTopY();
		int w = getPresetPanelWidth();
		if (saveMode) return;
		int iconRowY = y + 12 + 13 + 4;
		int iconsTotalW = 3 * PRESET_ICON_W + 2 * PRESET_ICON_GAP;
		int iconX = x + (w - iconsTotalW) / 2;
		if (isIn(mouseX, mouseY, iconX, iconRowY, PRESET_ICON_W, PRESET_ICON_W)) {
			g.setComponentTooltipForNextFrame(font, List.of(
					Component.translatable("ic.gui.reactor_sim.load_title").withStyle(ChatFormatting.YELLOW),
					Component.translatable("ic.gui.reactor_sim.load_desc").withStyle(ChatFormatting.GRAY),
					Component.translatable("ic.gui.reactor_sim.load_hint").withStyle(ChatFormatting.DARK_GRAY)
			), mouseX, mouseY, ItemStack.EMPTY);
			return;
		}
		if (isIn(mouseX, mouseY, iconX + PRESET_ICON_W + PRESET_ICON_GAP, iconRowY, PRESET_ICON_W, PRESET_ICON_W)) {
			g.setComponentTooltipForNextFrame(font, List.of(
					Component.translatable("ic.gui.reactor_sim.save_title").withStyle(ChatFormatting.YELLOW),
					Component.translatable("ic.gui.reactor_sim.save_desc").withStyle(ChatFormatting.GRAY),
					Component.translatable("ic.gui.reactor_sim.save_hint").withStyle(ChatFormatting.DARK_GRAY)
			), mouseX, mouseY, ItemStack.EMPTY);
			return;
		}
		if (isIn(mouseX, mouseY, iconX + (PRESET_ICON_W + PRESET_ICON_GAP) * 2, iconRowY, PRESET_ICON_W, PRESET_ICON_W)) {
			boolean removable = !presets.isEmpty() && !presets.get(selectedPreset).builtin();
			List<Component> lines = new ArrayList<>();
			lines.add(Component.translatable("ic.gui.reactor_sim.remove_title").withStyle(ChatFormatting.YELLOW));
			lines.add(Component.translatable("ic.gui.reactor_sim.remove_desc").withStyle(ChatFormatting.GRAY));
			if (!removable) lines.add(Component.translatable("ic.gui.reactor_sim.remove_locked").withStyle(ChatFormatting.DARK_GRAY));
			g.setComponentTooltipForNextFrame(font, lines, mouseX, mouseY, ItemStack.EMPTY);
		}
	}

	private void drawSpeedRow(GuiGraphicsExtractor g, int mouseX, int mouseY) {
		int y = topPos + ROW_SPEED_Y;
		for (int i = 0; i < 4; i++) {
			int x = leftPos + CONTROL_BTN_START_X + i * (CONTROL_BTN_W + CONTROL_BTN_GAP);
			int w = CONTROL_BTN_W;
			boolean sel = menu.getSpeedIndex() == i;
			boolean hover = isIn(mouseX, mouseY, x, y, w, BUTTON_H);
			int outer = sel ? 0xFF1F4E80 : 0xFF333333;
			int inner = sel ? 0xFF4A9BEC : (hover ? 0xFFCECECE : 0xFF9C9C9C);
			g.fill(x, y, x + w, y + BUTTON_H, outer);
			g.fill(x + 1, y + 1, x + w - 1, y + BUTTON_H - 1, inner);
			String label = ReactorSimulatorBlockEntity.SPEED_LABELS[i] + "x";
			textCenter(g, label, x + w / 2, y + 3, sel ? 0xFFFFFFFF : 0xFF000000);
		}
	}

	private void drawControlRow(GuiGraphicsExtractor g, int mouseX, int mouseY) {
		int y = topPos + ROW_CONTROL_Y;
		String[] labels = {
				Component.translatable("ic.gui.reactor_sim.start").getString(),
				Component.translatable("ic.gui.reactor_sim.pause").getString(),
				Component.translatable("ic.gui.reactor_sim.restart").getString(),
				Component.translatable("ic.gui.reactor_sim.clear").getString()
		};
		boolean running = menu.isRunning();
		boolean paused = menu.isPaused();
		for (int i = 0; i < 4; i++) {
			int x = leftPos + CONTROL_BTN_START_X + i * (CONTROL_BTN_W + CONTROL_BTN_GAP);
			int w = CONTROL_BTN_W;
			boolean hover = isIn(mouseX, mouseY, x, y, w, BUTTON_H);
			boolean dim = (i == 0 && running && !paused) || (i == 1 && !running);
			int outer = 0xFF333333;
			int inner = dim ? 0xFF8B8B8B : (hover ? 0xFFBCE0FF : 0xFFE0E0E0);
			g.fill(x, y, x + w, y + BUTTON_H, outer);
			g.fill(x + 1, y + 1, x + w - 1, y + BUTTON_H - 1, inner);
			textCenter(g, labels[i], x + w / 2, y + 3, dim ? 0xFF444444 : 0xFF000000);
		}
	}

	private void drawStepperRow(GuiGraphicsExtractor g, int x, int y, int w, String label, int mouseX, int mouseY) {
		boolean hoverMinus = isIn(mouseX, mouseY, x, y, STEPPER_BTN_W, BUTTON_H);
		boolean hoverPlus = isIn(mouseX, mouseY, x + w - STEPPER_BTN_W, y, STEPPER_BTN_W, BUTTON_H);
		g.fill(x, y, x + STEPPER_BTN_W, y + BUTTON_H, 0xFF333333);
		g.fill(x + 1, y + 1, x + STEPPER_BTN_W - 1, y + BUTTON_H - 1, hoverMinus ? 0xFFBCE0FF : 0xFFE0E0E0);
		textCenter(g, "-", x + STEPPER_BTN_W / 2, y + 3, 0xFF000000);

		int innerLeft = x + STEPPER_BTN_W;
		int innerRight = x + w - STEPPER_BTN_W;
		g.fill(innerLeft, y, innerRight, y + BUTTON_H, 0xFF555555);
		g.fill(innerLeft + 1, y + 1, innerRight - 1, y + BUTTON_H - 1, 0xFF3C3C3C);
		textCenter(g, label, (innerLeft + innerRight) / 2, y + 3, 0xFFFFFFFF);

		g.fill(x + w - STEPPER_BTN_W, y, x + w, y + BUTTON_H, 0xFF333333);
		g.fill(x + w - STEPPER_BTN_W + 1, y + 1, x + w - 1, y + BUTTON_H - 1, hoverPlus ? 0xFFBCE0FF : 0xFFE0E0E0);
		textCenter(g, "+", x + w - STEPPER_BTN_W / 2, y + 3, 0xFF000000);
	}

	private void drawVerdictRow(GuiGraphicsExtractor g) {
		int y = topPos + ROW_VERDICT_Y;
		byte verdict = menu.getVerdict();
		String verdictStr;
		int verdictColor;
		if (verdict == ReactorSimulatorBlockEntity.VERDICT_STABLE) {
			verdictStr = Component.translatable("ic.gui.reactor_sim.verdict_stable").getString(); verdictColor = 0xFF007F1F;
		} else if (verdict == ReactorSimulatorBlockEntity.VERDICT_UNSTABLE) {
			verdictStr = Component.translatable("ic.gui.reactor_sim.verdict_unstable", menu.getUnstableCycle()).getString();
			verdictColor = 0xFFCC0000;
		} else {
			verdictStr = Component.translatable("ic.gui.reactor_sim.verdict_none").getString(); verdictColor = 0xFF606060;
		}
		g.text(font, Component.literal(verdictStr), leftPos + 10, y, verdictColor, false);
	}

	private void drawStatsRow(GuiGraphicsExtractor g) {
		int y = topPos + ROW_STATS_Y;
		long total = menu.getTotalEnergy();
		int perTick = menu.getLastEnergy();
		long cycle = menu.getElapsedCycles();
		String stats = Component.translatable("ic.gui.reactor_sim.stats", perTick, formatShort(total), cycle).getString();
		g.text(font, Component.literal(stats), leftPos + 10, y, 0xFF404040, false);
	}

	private void drawBottomRow(GuiGraphicsExtractor g, int mouseX, int mouseY) {
		int y = topPos + ROW_BUTTONS_Y;
		int startX = leftPos + BOTTOM_BTN_START_X;
		drawTextButton(g, startX, y, BOTTOM_BTN_W, Component.translatable("ic.gui.reactor_sim.analyze").getString(), mouseX, mouseY);
		drawTextButton(g, startX + BOTTOM_BTN_W + BOTTOM_BTN_GAP, y, BOTTOM_BTN_W, Component.translatable("ic.gui.reactor_sim.import_btn").getString(), mouseX, mouseY);
		drawTextButton(g, startX + (BOTTOM_BTN_W + BOTTOM_BTN_GAP) * 2, y, BOTTOM_BTN_W, Component.translatable("ic.gui.reactor_sim.export_btn").getString(), mouseX, mouseY);
	}

	private void drawTextButton(GuiGraphicsExtractor g, int x, int y, int w, String label, int mouseX, int mouseY) {
		boolean hover = isIn(mouseX, mouseY, x, y, w, BUTTON_H);
		g.fill(x, y, x + w, y + BUTTON_H, 0xFF333333);
		g.fill(x + 1, y + 1, x + w - 1, y + BUTTON_H - 1, hover ? 0xFFBCE0FF : 0xFFE0E0E0);
		textCenter(g, label, x + w / 2, y + 3, 0xFF000000);
	}

	private static String formatShort(long n) {
		if (n < 1_000) return String.valueOf(n);
		if (n < 1_000_000) return String.format("%.1fk", n / 1000D);
		if (n < 1_000_000_000) return String.format("%.1fM", n / 1_000_000D);
		return String.format("%.1fB", n / 1_000_000_000D);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		int innerX = leftPos + PICKER_INNER_X;
		int innerY = topPos + PICKER_INNER_Y;
		int gridW = PICKER_COLS * PICKER_SLOT;
		int gridH = pickerVisibleRows * PICKER_SLOT;
		if (isIn((int) mouseX, (int) mouseY, innerX, innerY, gridW + 8, gridH)) {
			int scrollMax = Math.max(0, pickerTotalRows - pickerVisibleRows);
			pickerScrollRow = Math.max(0, Math.min(scrollMax, pickerScrollRow - (int) Math.signum(scrollY)));
			return true;
		}
		return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		int mx = (int) event.x();
		int my = (int) event.y();
		int button = event.button();

		if (dropdownOpen) {
			int row = dropdownRowAt(mx, my);
			if (row >= 0) {
				selectedPreset = row;
				dropdownOpen = false;
				return true;
			}
			dropdownOpen = false;
			return true;
		}

		if (handlePresetClick(mx, my, button)) return true;

		if (button == 0) {
			ItemStack picked = pickerSlotAt(mx, my);
			if (!picked.isEmpty()) {
				pending = picked.copy();
				return true;
			}
		}

		if (button == 1) {
			if (!pending.isEmpty()) {
				pending = ItemStack.EMPTY;
				return true;
			}
			if (!menu.isEditLocked()) {
				int gridSlot = gridSlotAt(mx, my);
				if (gridSlot >= 0) {
					ClientPacketDistributor.sendToServer(SimulatorActionPayload.clearSlot(gridSlot));
					return true;
				}
			}
		}

		int speedY = topPos + ROW_SPEED_Y;
		for (int i = 0; i < 4; i++) {
			int x = leftPos + CONTROL_BTN_START_X + i * (CONTROL_BTN_W + CONTROL_BTN_GAP);
			if (isIn(mx, my, x, speedY, CONTROL_BTN_W, BUTTON_H)) {
				ClientPacketDistributor.sendToServer(SimulatorActionPayload.setSpeed(i));
				return true;
			}
		}

		int ctlY = topPos + ROW_CONTROL_Y;
		byte[] actions = {SimulatorActionPayload.START, SimulatorActionPayload.PAUSE, SimulatorActionPayload.RESTART, SimulatorActionPayload.RESET};
		for (int i = 0; i < 4; i++) {
			int x = leftPos + CONTROL_BTN_START_X + i * (CONTROL_BTN_W + CONTROL_BTN_GAP);
			if (isIn(mx, my, x, ctlY, CONTROL_BTN_W, BUTTON_H)) {
				ClientPacketDistributor.sendToServer(SimulatorActionPayload.simple(actions[i]));
				return true;
			}
		}

		int chX = leftPos + 8;
		int waX = chX + STEPPER_W + 4;
		int stY = topPos + ROW_STEPPERS_Y;
		if (isIn(mx, my, chX, stY, STEPPER_BTN_W, BUTTON_H)) {
			int target = Math.max(0, menu.getChambers() - 1);
			menu.chambersSlot.set(target);
			ClientPacketDistributor.sendToServer(SimulatorActionPayload.setChambers(target));
			return true;
		}
		if (isIn(mx, my, chX + STEPPER_W - STEPPER_BTN_W, stY, STEPPER_BTN_W, BUTTON_H)) {
			int target = Math.min(6, menu.getChambers() + 1);
			menu.chambersSlot.set(target);
			ClientPacketDistributor.sendToServer(SimulatorActionPayload.setChambers(target));
			return true;
		}
		if (isIn(mx, my, waX, stY, STEPPER_BTN_W, BUTTON_H)) {
			int target = Math.max(0, menu.getWaterThousandths() - 100);
			menu.waterSlot.set(target);
			ClientPacketDistributor.sendToServer(SimulatorActionPayload.setWater(target));
			return true;
		}
		if (isIn(mx, my, waX + STEPPER_W - STEPPER_BTN_W, stY, STEPPER_BTN_W, BUTTON_H)) {
			int target = Math.min(1000, menu.getWaterThousandths() + 100);
			menu.waterSlot.set(target);
			ClientPacketDistributor.sendToServer(SimulatorActionPayload.setWater(target));
			return true;
		}

		int btnY = topPos + ROW_BUTTONS_Y;
		int startX = leftPos + BOTTOM_BTN_START_X;
		int gap = BOTTOM_BTN_GAP;
		if (isIn(mx, my, startX, btnY, BOTTOM_BTN_W, BUTTON_H)) {
			ClientPacketDistributor.sendToServer(SimulatorActionPayload.simple(SimulatorActionPayload.ANALYZE));
			return true;
		}
		if (isIn(mx, my, startX + BOTTOM_BTN_W + gap, btnY, BOTTOM_BTN_W, BUTTON_H)) {
			String clip = Minecraft.getInstance().keyboardHandler.getClipboard();
			if (clip != null && !clip.isEmpty()) {
				try {
					ReactorDesign.fromJson(clip);
					ClientPacketDistributor.sendToServer(SimulatorActionPayload.importDesign(clip));
				} catch (IllegalArgumentException ex) {
					sendLocalMessage(Component.translatable("ic.gui.reactor_sim.import_error"), ChatFormatting.RED);
				}
			}
			return true;
		}
		if (isIn(mx, my, startX + (BOTTOM_BTN_W + gap) * 2, btnY, BOTTOM_BTN_W, BUTTON_H)) {
			if (menu.blockEntity instanceof ReactorSimulatorBlockEntity sim) {
				String json = sim.exportDesign().toJson();
				Minecraft.getInstance().keyboardHandler.setClipboard(json);
				sendLocalMessage(Component.translatable("ic.gui.reactor_sim.export_success"), ChatFormatting.GREEN);
			}
			return true;
		}

		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		int mx = (int) event.x();
		int my = (int) event.y();
		if (event.button() == 0 && !pending.isEmpty()) {
			ItemStack drop = pending;
			pending = ItemStack.EMPTY;
			if (!menu.isEditLocked()) {
				int gridSlot = gridSlotAt(mx, my);
				if (gridSlot >= 0 && drop.getItem() instanceof ReactorItem) {
					ClientPacketDistributor.sendToServer(SimulatorActionPayload.setSlot(gridSlot, drop));
				}
			}
			return true;
		}
		return super.mouseReleased(event);
	}

	private boolean handlePresetClick(int mx, int my, int button) {
		if (button != 0) return false;
		int x = leftPos + PICKER_X;
		int y = topPos + getPresetTopY();
		int w = getPresetPanelWidth();

		if (saveMode) {
			int boxY = y + 14;
			int iconRowY = boxY + 14;
			int iconsTotalW = 2 * PRESET_ICON_W + PRESET_ICON_GAP;
			int iconX = x + (w - iconsTotalW) / 2;
			if (isIn(mx, my, iconX, iconRowY, PRESET_ICON_W, PRESET_ICON_W)) {
				tryConfirmSave();
				return true;
			}
			if (isIn(mx, my, iconX + PRESET_ICON_W + PRESET_ICON_GAP, iconRowY, PRESET_ICON_W, PRESET_ICON_W)) {
				exitSaveMode();
				return true;
			}
			if (isIn(mx, my, saveNameBox.getX(), saveNameBox.getY(), saveNameBox.getWidth(), saveNameBox.getHeight())) {
				return false;
			}
			return false;
		}

		int ddX = x + 4;
		int ddY = y + 12;
		int ddW = w - 8;
		int ddH = 13;
		if (isIn(mx, my, ddX, ddY, ddW, ddH)) {
			dropdownOpen = !dropdownOpen;
			return true;
		}

		int iconRowY = ddY + ddH + 4;
		int iconsTotalW = 3 * PRESET_ICON_W + 2 * PRESET_ICON_GAP;
		int iconX = x + (w - iconsTotalW) / 2;
		if (isIn(mx, my, iconX, iconRowY, PRESET_ICON_W, PRESET_ICON_W)) {
			if (!presets.isEmpty()) doLoad();
			return true;
		}
		if (isIn(mx, my, iconX + PRESET_ICON_W + PRESET_ICON_GAP, iconRowY, PRESET_ICON_W, PRESET_ICON_W)) {
			if (!menu.isEditLocked()) enterSaveMode();
			return true;
		}
		if (isIn(mx, my, iconX + (PRESET_ICON_W + PRESET_ICON_GAP) * 2, iconRowY, PRESET_ICON_W, PRESET_ICON_W)) {
			if (!presets.isEmpty() && !presets.get(selectedPreset).builtin()) doRemove();
			return true;
		}
		return false;
	}

	private void doLoad() {
		if (presets.isEmpty()) return;
		ReactorPresetLibrary.Preset p = presets.get(selectedPreset);
		String json = p.design().toJson();
		ClientPacketDistributor.sendToServer(SimulatorActionPayload.importDesign(json));
	}

	private void enterSaveMode() {
		saveMode = true;
		saveError = "";
		saveNameBox.setValue("");
		saveNameBox.setVisible(true);
		setFocused(saveNameBox);
		saveNameBox.setFocused(true);
	}

	private void exitSaveMode() {
		saveMode = false;
		saveError = "";
		saveNameBox.setVisible(false);
		saveNameBox.setFocused(false);
		setFocused(null);
	}

	private void tryConfirmSave() {
		String name = saveNameBox.getValue().trim();
		if (!ReactorPresetLibrary.isValidName(name)) {
			saveError = Component.translatable("ic.gui.reactor_sim.save_error_invalid_name").getString();
			return;
		}
		for (ReactorPresetLibrary.Preset p : presets) {
			if (p.builtin() && p.name().equalsIgnoreCase(name)) {
				saveError = Component.translatable("ic.gui.reactor_sim.save_error_reserved").getString();
				return;
			}
		}
		if (!(menu.blockEntity instanceof ReactorSimulatorBlockEntity sim)) {
			saveError = Component.translatable("ic.gui.reactor_sim.save_error_no_sim").getString();
			return;
		}
		ReactorDesign design = sim.exportDesign();
		if (!ReactorPresetLibrary.save(name, design)) {
			saveError = Component.translatable("ic.gui.reactor_sim.save_error_failed").getString();
			return;
		}
		refreshPresets();
		for (int i = 0; i < presets.size(); i++) {
			if (!presets.get(i).builtin() && presets.get(i).name().equals(name)) {
				selectedPreset = i;
				break;
			}
		}
		exitSaveMode();
		sendLocalMessage(Component.translatable("ic.gui.reactor_sim.save_success", name), ChatFormatting.GREEN);
	}

	private void doRemove() {
		if (presets.isEmpty()) return;
		ReactorPresetLibrary.Preset p = presets.get(selectedPreset);
		if (p.builtin()) return;
		if (ReactorPresetLibrary.remove(p.name())) {
			sendLocalMessage(Component.translatable("ic.gui.reactor_sim.remove_success", p.name()), ChatFormatting.YELLOW);
		}
		refreshPresets();
		selectedPreset = 0;
	}

	private void sendLocalMessage(Component text, ChatFormatting color) {
		var p = Minecraft.getInstance().player;
		if (p != null) p.sendSystemMessage(text.copy().withStyle(color));
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (saveMode && saveNameBox != null && saveNameBox.isFocused()) {
			int key = event.key();
			if (key == InputConstants.KEY_RETURN) {
				tryConfirmSave();
				return true;
			}
			if (key == InputConstants.KEY_ESCAPE) {
				exitSaveMode();
				return true;
			}
			if (saveNameBox.keyPressed(event) || saveNameBox.canConsumeInput()) {
				return true;
			}
		}
		return super.keyPressed(event);
	}
}
