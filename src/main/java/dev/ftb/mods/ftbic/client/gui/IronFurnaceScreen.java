package dev.ftb.mods.ftbic.client.gui;

import dev.ftb.mods.ftbic.client.ClientRecipeCache;
import dev.ftb.mods.ftbic.net.FTBICNet;
import dev.ftb.mods.ftbic.net.SetGhostIngredientPayload;
import dev.ftb.mods.ftbic.screen.IronFurnaceMenu;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.FluidStack;

public class IronFurnaceScreen extends AbstractContainerScreen<IronFurnaceMenu> {
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath("minecraft", "textures/gui/container/furnace.png");
    private static final Identifier LIT_PROGRESS_SPRITE =
            Identifier.fromNamespaceAndPath("minecraft", "container/furnace/lit_progress");
    private static final Identifier BURN_PROGRESS_SPRITE =
            Identifier.fromNamespaceAndPath("minecraft", "container/furnace/burn_progress");

    public IronFurnaceScreen(IronFurnaceMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = (imageWidth - font.width(title)) / 2;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(g, mouseX, mouseY, partialTick);
        int xo = leftPos;
        int yo = topPos;
        g.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, xo, yo, 0F, 0F, imageWidth, imageHeight, 256, 256);
        if (menu.isLit()) {
            int h = Mth.ceil(menu.getLitProgress() * 13F) + 1;
            g.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    LIT_PROGRESS_SPRITE,
                    14,
                    14,
                    0,
                    14 - h,
                    xo + 56,
                    yo + 36 + 14 - h,
                    14,
                    h);
        }
        if (menu.blockEntity != null) {
            for (int slot = 0; slot < 2; slot++) {
                var ghost = menu.blockEntity.getInputLock(slot);
                if (ghost.isEmpty()) continue;
                var input = menu.slots.get(slot);
                int x = leftPos + input.x, y = topPos + input.y;
                if (!input.hasItem()) {
                    g.item(ghost, x, y);
                    g.fill(x, y, x + 16, y + 16, 0x998B8B8B);
                }
                g.fill(x, y + 16, x + 16, y + 17, IndustrialGui.CYAN);
            }
        }
        int w = Mth.ceil(menu.getBurnProgress() * 24F);
        g.blitSprite(RenderPipelines.GUI_TEXTURED, BURN_PROGRESS_SPRITE, 24, 16, 0, 0, xo + 79, yo + 34, w, 16);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(g, mouseX, mouseY, partialTick);
        if (menu.blockEntity != null) {
            for (int slot = 0; slot < 2; slot++) {
                var ghost = menu.blockEntity.getInputLock(slot);
                var input = menu.slots.get(slot);
                if (!ghost.isEmpty()
                        && !input.hasItem()
                        && ElectricBlockScreen.isIn(mouseX, mouseY, leftPos + input.x, topPos + input.y, 16, 16)) {
                    g.setTooltipForNextFrame(
                            Component.translatable("ftbic.locks.furnace_locked", ghost.getHoverName()), mouseX, mouseY);
                }
            }
        }
        if (isInArrow(mouseX, mouseY)) {
            int pct = Math.round(menu.getBurnProgress() * 100F);
            g.setTooltipForNextFrame(Component.translatable("ftbic.gui.iron_furnace.progress", pct), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean dragging) {
        int mx = (int) event.x();
        int my = (int) event.y();
        if (event.button() == 1 && menu.blockEntity != null && menu.getCarried().isEmpty()) {
            for (int slot = 0; slot < 2; slot++) {
                var input = menu.slots.get(slot);
                if ((!input.hasItem() || event.hasShiftDown())
                        && !menu.blockEntity.getInputLock(slot).isEmpty()
                        && ElectricBlockScreen.isIn(mx, my, leftPos + input.x, topPos + input.y, 16, 16)) {
                    FTBICNet.sendToServer(
                            new SetGhostIngredientPayload(menu.containerId, slot, ItemStack.EMPTY, FluidStack.EMPTY));
                    return true;
                }
            }
        }
        if (isInArrow(mx, my)) {
            ClientRecipeCache.showRecipesForTypes(List.of(RecipeType.SMELTING));
            return true;
        }
        return super.mouseClicked(event, dragging);
    }

    private boolean isInArrow(int mx, int my) {
        return mx >= leftPos + 79 && mx < leftPos + 79 + 24 && my >= topPos + 34 && my < topPos + 34 + 16;
    }
}
