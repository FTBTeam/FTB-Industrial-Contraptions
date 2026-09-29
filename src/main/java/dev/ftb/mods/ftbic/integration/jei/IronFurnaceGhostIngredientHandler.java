package dev.ftb.mods.ftbic.integration.jei;

import dev.ftb.mods.ftbic.client.gui.IronFurnaceScreen;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.renderer.Rect2i;

public class IronFurnaceGhostIngredientHandler implements IGhostIngredientHandler<IronFurnaceScreen> {
    @Override
    public <I> List<Target<I>> getTargetsTyped(
            IronFurnaceScreen screen, ITypedIngredient<I> ingredient, boolean doStart) {
        var stack = ingredient.getItemStack().orElse(null);
        var menu = screen.getMenu();
        if (stack == null || stack.isEmpty() || menu.blockEntity == null) return List.of();
        var targets = new ArrayList<Target<I>>();
        for (int slot = 0; slot < 2; slot++) {
            int index = slot;
            var input = menu.slots.get(slot);
            targets.add(MachineGhostIngredientHandler.target(
                    new Rect2i(screen.getLeftPos() + input.x - 1, screen.getTopPos() + input.y - 1, 18, 18),
                    () -> MachineGhostIngredientHandler.sendItem(menu.containerId, index, stack)));
        }
        return targets;
    }

    @Override
    public void onComplete() {}
}
