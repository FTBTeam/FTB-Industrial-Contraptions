package dev.ftb.mods.ftbic.integration.jei;

import dev.ftb.mods.ftbic.client.gui.QuarryFilterScreen;
import dev.ftb.mods.ftbic.util.QuarryFilter;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.BlockItem;

public class QuarryFilterGhostIngredientHandler implements IGhostIngredientHandler<QuarryFilterScreen> {
    @Override
    public <I> List<Target<I>> getTargetsTyped(
            QuarryFilterScreen screen, ITypedIngredient<I> ingredient, boolean doStart) {
        var stack = ingredient.getItemStack().orElse(null);
        if (stack == null || !(stack.getItem() instanceof BlockItem)) return List.of();
        var targets = new ArrayList<Target<I>>();
        for (int slot = 0; slot < QuarryFilter.BLOCK_SLOTS; slot++) {
            int index = slot;
            targets.add(MachineGhostIngredientHandler.target(
                    screen.getBlockArea(slot),
                    () -> MachineGhostIngredientHandler.sendItem(
                            screen.getMenu().containerId, QuarryFilter.BLOCK_SLOTS + index, stack)));
        }
        return targets;
    }

    public static IGuiProperties properties(QuarryFilterScreen screen) {
        var area = screen.getPanelArea();
        return new Properties(
                screen.getClass(),
                area.getX(),
                area.getY(),
                area.getWidth(),
                area.getHeight(),
                screen.width,
                screen.height);
    }

    private record Properties(
            Class<? extends Screen> screenClass,
            int guiLeft,
            int guiTop,
            int guiXSize,
            int guiYSize,
            int screenWidth,
            int screenHeight)
            implements IGuiProperties {}

    @Override
    public void onComplete() {}
}
