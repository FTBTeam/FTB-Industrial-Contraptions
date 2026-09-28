package dev.ftb.mods.ftbic.client.gui;

import dev.ftb.mods.ftbic.screen.BankMenu;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BankScreen extends ElectricBlockScreen<BankMenu> {
    public BankScreen(BankMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, Component.translatable("ftbic.bank.title"), 176, 220);
        drawDefaultArrow = false;
    }

    @Override
    protected void init() {
        super.init();
        inventoryLabelY = 121;
    }

    @Override
    protected void drawBase(GuiGraphicsExtractor graphics) {
        IndustrialGui.panel(graphics, leftPos, topPos, imageWidth, imageHeight);
        for (var slot : menu.slots) drawSlot(graphics, leftPos + slot.x - 1, topPos + slot.y - 1);
        graphics.fill(leftPos + 4, topPos + 4, leftPos + 172, topPos + 19, IndustrialGui.HEADER);
        IndustrialGui.readout(graphics, leftPos + 9, topPos + 25, 158, 55);
        graphics.text(
                font,
                Component.translatable("ftbic.bank.stored"),
                leftPos + 15,
                topPos + 30,
                IndustrialGui.CYAN,
                false);
        graphics.text(font, FTBICUtils.formatEnergy(menu.stored()), leftPos + 15, topPos + 43, 0xFFE5EFEF, false);
        graphics.text(
                font,
                "/ " + FTBICUtils.formatEnergy(menu.capacity()).getString(),
                leftPos + 15,
                topPos + 57,
                0xFFBAC7C8,
                false);
        int width = menu.capacity() <= 0D ? 0 : (int) Math.min(150D, 150D * menu.stored() / menu.capacity());
        graphics.fill(leftPos + 12, topPos + 84, leftPos + 164, topPos + 92, IndustrialGui.BORDER);
        graphics.fill(leftPos + 13, topPos + 85, leftPos + 13 + width, topPos + 91, IndustrialGui.CYAN);
        graphics.text(
                font,
                Component.translatable("ftbic.bank.members", menu.cells.get(), menu.ports.get()),
                leftPos + 15,
                topPos + 69,
                0xFFBAC7C8,
                false);
        if (menu.hasChargeSlots()) {
            graphics.text(
                    font,
                    Component.translatable("ftbic.bank.charge"),
                    leftPos + 10,
                    topPos + 103,
                    IndustrialGui.CYAN,
                    false);
        } else {
            graphics.centeredText(
                    font,
                    Component.translatable("ftbic.bank.charge_hint"),
                    leftPos + imageWidth / 2,
                    topPos + 103,
                    0xFFBAC7C8);
        }
    }
}
