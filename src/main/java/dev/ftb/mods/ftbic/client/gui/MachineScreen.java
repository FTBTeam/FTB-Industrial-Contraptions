package dev.ftb.mods.ftbic.client.gui;

import dev.ftb.mods.ftbic.block.entity.machine.BasicMachineBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.ChargePadBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.FluidMachineBlockEntity;
import dev.ftb.mods.ftbic.integration.jei.ClientRecipeCache;
import dev.ftb.mods.ftbic.screen.MachineMenu;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.FluidStack;

public class MachineScreen extends ElectricBlockScreen<MachineMenu> {
    public MachineScreen(MachineMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        energyX = 8;
        energyY = 27;
        if (menu.blockEntity instanceof ChargePadBlockEntity) drawDefaultArrow = false;
    }

    @Override
    protected void drawBase(GuiGraphicsExtractor g) {
        super.drawBase(g);
        if (drawDefaultArrow) {
            if (menu.blockEntity instanceof FluidMachineBlockEntity) {
                IndustrialGui.readout(g, leftPos + 51, topPos + 23, 55, 44);
            } else {
                IndustrialGui.readout(g, leftPos + 30, topPos + 23, 116, 44);
            }
        }
    }

    @Override
    protected void extractOverlays(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        if (this.menu.blockEntity == null) return;
        int inputs = this.menu.blockEntity.inputItems.length;
        int outputs = this.menu.blockEntity.outputItems.length;

        int inputCols = Math.max(1, Math.min(2, inputs));
        int inputXStart = this.menu.blockEntity instanceof ChargePadBlockEntity ? 69 : 59 - (inputCols - 1) * 18;
        int inputRows = Math.max(1, (int) Math.ceil(inputs / (double) inputCols));
        int yStart = 35 - ((inputRows - 1) * 9);
        for (int i = 0; i < inputs; i++) {
            int col = i % inputCols;
            int row = i / inputCols;
            drawSlot(g, leftPos + inputXStart + col * 18, topPos + yStart - 1 + row * 18);
        }

        if (menu.parallelCapacity.get() > 1) {
            g.centeredText(
                    font,
                    Component.translatable(
                            "ftbic.gui.machine.parallel", menu.runningOperations.get(), menu.parallelCapacity.get()),
                    leftPos + 92,
                    topPos + 55,
                    0xFF294C53);
        }
        boolean centrifuge = menu.blockEntity instanceof FluidMachineBlockEntity;
        int outputCols = centrifuge ? 1 : Math.max(1, Math.min(2, outputs));
        int outputRows = Math.max(1, (int) Math.ceil(outputs / (double) outputCols));
        int oyStart = 35 - ((outputRows - 1) * 9);
        for (int i = 0; i < outputs; i++) {
            int col = i % outputCols;
            int row = i / outputCols;
            drawSlot(g, leftPos + 107 + col * 18, topPos + oyStart - 1 + row * 18);
        }
        if (menu.blockEntity instanceof FluidMachineBlockEntity machine) {
            drawTank(g, leftPos + 30, topPos + 17, machine.getInputFluid(), FluidMachineBlockEntity.TANK_CAPACITY);
            drawTank(g, leftPos + 130, topPos + 17, machine.getOutputFluid(), FluidMachineBlockEntity.TANK_CAPACITY);
            g.fill(leftPos + 31, topPos + 70, leftPos + 47, topPos + 72, 0xFF66BBFF);
            g.fill(leftPos + 131, topPos + 70, leftPos + 147, topPos + 72, 0xFFFFBB55);
        }
        if (this.menu.blockEntity instanceof BasicMachineBlockEntity) {
            drawSlot(g, leftPos + 7, topPos + 52);
            for (int i = 0; i < 4; i++) {
                drawSlot(g, leftPos + 151, topPos + 7 + i * 18);
            }
        }
    }

    @Override
    protected void extractOverlayTooltips(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        super.extractOverlayTooltips(g, mouseX, mouseY);

        if (menu.blockEntity instanceof FluidMachineBlockEntity machine) {
            centrifugeTankTooltip(g, mouseX, mouseY, 30, "ftbic.gui.centrifuge.input_tank", machine.getInputFluid());
            centrifugeTankTooltip(g, mouseX, mouseY, 130, "ftbic.gui.centrifuge.output_tank", machine.getOutputFluid());
        }
        if (menu.parallelCapacity.get() > 1 && isIn(mouseX, mouseY, leftPos + 80, topPos + 54, 24, 10)) {
            g.setTooltipForNextFrame(
                    Component.translatable(
                            "ftbic.gui.machine.parallel_tooltip",
                            menu.runningOperations.get(),
                            menu.parallelCapacity.get()),
                    mouseX,
                    mouseY);
        }
        if (drawDefaultArrow && isIn(mouseX, mouseY, leftPos + 80, topPos + 34, 24, 17)) {
            int pct = Math.round(this.menu.getProgressFraction() * 100F);
            g.setTooltipForNextFrame(Component.translatable("ftbic.gui.machine.progress", pct), mouseX, mouseY);
        }
    }

    private void centrifugeTankTooltip(
            GuiGraphicsExtractor g, int mouseX, int mouseY, int x, String key, FluidStack fluid) {
        if (isIn(mouseX, mouseY, leftPos + x, topPos + 17, 18, 55)) {
            Component name = fluid.isEmpty() ? Component.translatable("ftbic.jade.fluid_empty") : fluid.getHoverName();
            g.setTooltipForNextFrame(
                    Component.translatable(key, name, fluid.getAmount(), FluidMachineBlockEntity.TANK_CAPACITY),
                    mouseX,
                    mouseY);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean dragging) {
        int mx = (int) event.x();
        int my = (int) event.y();
        if (drawDefaultArrow && isIn(mx, my, leftPos + 80, topPos + 34, 24, 17)) {
            List<RecipeType<?>> types = this.menu.getJeiRecipeTypes();
            if (!types.isEmpty()) {
                ClientRecipeCache.showRecipesForTypes(types);
                return true;
            }
        }
        return super.mouseClicked(event, dragging);
    }
}
