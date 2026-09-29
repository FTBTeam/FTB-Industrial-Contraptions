package dev.ftb.mods.ftbic.client.gui;

import dev.ftb.mods.ftbic.block.entity.machine.HydroponicBlockEntity;
import dev.ftb.mods.ftbic.client.ClientRecipeCache;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import dev.ftb.mods.ftbic.screen.HydroponicMenu;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public final class HydroponicScreen extends ElectricBlockScreen<HydroponicMenu> {
    private final boolean advanced;
    private Button modeButton;

    public HydroponicScreen(HydroponicMenu menu, Inventory inv, Component title) {
        super(menu, inv, title, HydroponicMenu.WIDTH, menu.screenHeight());
        advanced = menu.isAdvanced();
        drawDefaultArrow = false;
        energyX = advanced ? 10 : 68;
        energyY = advanced ? 110 : 86;
    }

    @Override
    protected void init() {
        super.init();
        inventoryLabelX = 26;
        inventoryLabelY = advanced ? 140 : 111;
        if (advanced) {
            modeButton = addRenderableWidget(Button.builder(
                            Component.translatable(menu.mutationMode() ? "ftbic.hydro.mutation" : "ftbic.hydro.growth"),
                            button -> {
                                minecraft
                                        .getConnection()
                                        .send(new ServerboundContainerButtonClickPacket(menu.containerId, 0));
                            })
                    .bounds(leftPos + 126, topPos + 132, 80, 18)
                    .build(IndustrialButton::new));
        }
    }

    @Override
    protected void drawBase(GuiGraphicsExtractor g) {
        IndustrialGui.panel(g, leftPos, topPos, imageWidth, imageHeight);
        g.fill(leftPos + 6, topPos + 5, leftPos + imageWidth - 6, topPos + 23, IndustrialGui.HEADER);
        IndustrialGui.readout(g, leftPos + 34, topPos + (advanced ? 28 : 44), 146, advanced ? 94 : 34);
        for (Slot slot : menu.slots) drawSlot(g, leftPos + slot.x - 1, topPos + slot.y - 1);
    }

    @Override
    protected void extractOverlays(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        if (!(menu.blockEntity instanceof HydroponicBlockEntity machine)) return;
        if (modeButton != null) {
            modeButton.setMessage(
                    Component.translatable(menu.mutationMode() ? "ftbic.hydro.mutation" : "ftbic.hydro.growth"));
            modeButton.setTooltip(menu.mutationMode() ? Tooltip.create(Component.literal("1+2 / 3+4")) : null);
        }
        int lanes = machine.isAdvanced() ? 4 : 1;
        for (int lane = 0; lane < lanes; lane++) {
            int y = topPos + menu.laneY(lane);
            if (machine.isAdvanced())
                g.text(
                        font,
                        Component.literal(Integer.toString(lane + 1)),
                        leftPos + 38,
                        y + 4,
                        IndustrialGui.CYAN,
                        false);
            drawArrow(g, leftPos + 90, y, Math.round(menu.laneFraction(lane) * 24));
        }
        g.text(font, Component.literal("H2O"), leftPos + 8, topPos + 32, IndustrialGui.TEXT, false);
        drawTank(g, leftPos + 10, topPos + 45, machine.getInputFluid(), machine.getTankCapacity());
    }

    @Override
    protected void extractOverlayTooltips(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        super.extractOverlayTooltips(g, mouseX, mouseY);
        if (menu.blockEntity instanceof HydroponicBlockEntity machine)
            tankTooltip(
                    g, leftPos + 10, topPos + 45, mouseX, mouseY, machine.getInputFluid(), machine.getTankCapacity());
        if (!(menu.blockEntity instanceof HydroponicBlockEntity machine)) return;
        Slot hovered = hoveredSlot;
        if (hovered == null || hovered.hasItem()) return;
        int index = menu.slots.indexOf(hovered);
        if (index < 0 || index >= machine.getSlotCount()) return;
        String key;
        if (index < machine.inputItems.length) {
            key = index % 2 == 0 ? "ftbic.hydro.seed" : "ftbic.hydro.soil";
        } else {
            int output = index - machine.inputItems.length;
            key = machine.isAdvanced()
                    ? switch (output % 3) {
                        case 0 -> "ftbic.hydro.product";
                        case 1 -> "ftbic.hydro.returned_seed";
                        default -> "ftbic.hydro.byproduct";
                    }
                    : switch (output) {
                        case 0 -> "ftbic.hydro.product";
                        case 1 -> "ftbic.hydro.returned_seed";
                        default -> "ftbic.hydro.byproduct";
                    };
        }
        g.setTooltipForNextFrame(Component.translatable(key), mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean dragging) {
        int x = (int) event.x(), y = (int) event.y();
        for (int lane = 0; lane < (advanced ? 4 : 1); lane++) {
            if (isIn(x, y, leftPos + 90, topPos + menu.laneY(lane), 24, 17)) {
                ClientRecipeCache.showRecipesForTypes(
                        advanced
                                ? List.of(
                                        FTBICRecipes.HYDROPONIC_GROWTH.TYPE.get(),
                                        FTBICRecipes.HYDROPONIC_MUTATION.TYPE.get())
                                : List.of(FTBICRecipes.HYDROPONIC_GROWTH.TYPE.get()));
                return true;
            }
        }
        return super.mouseClicked(event, dragging);
    }
}
