package dev.ftb.mods.ftbic.integration.jei;

import dev.ftb.mods.ftbic.client.gui.InputLockScreen;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.gui.screens.Screen;

public class InputLockGhostIngredientHandler implements IGhostIngredientHandler<InputLockScreen> {
    @Override
    public <I> List<Target<I>> getTargetsTyped(
            InputLockScreen screen, ITypedIngredient<I> ingredient, boolean doStart) {
        var stack = ingredient.getItemStack().orElse(null);
        var menu = screen.getMenu();
        if (stack == null || stack.isEmpty() || menu.blockEntity == null || !menu.blockEntity.supportsInputLocks())
            return List.of();
        var targets = new ArrayList<Target<I>>();
        for (int slot = 0; slot < menu.blockEntity.inputItems.length; slot++) {
            int index = slot;
            targets.add(MachineGhostIngredientHandler.target(
                    screen.getInputArea(slot),
                    () -> MachineGhostIngredientHandler.sendItem(menu.containerId, index, stack)));
        }
        return targets;
    }

    public static IGuiProperties properties(InputLockScreen screen) {
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
