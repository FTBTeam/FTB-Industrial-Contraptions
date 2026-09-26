package dev.ftb.mods.ftbic.integration.jei;

import dev.ftb.mods.ftbic.block.entity.machine.BatchFeederBlockEntity;
import dev.ftb.mods.ftbic.client.gui.BatchFeederScreen;
import dev.ftb.mods.ftbic.client.gui.ElectricBlockScreen;
import dev.ftb.mods.ftbic.net.FTBICNet;
import dev.ftb.mods.ftbic.net.SetGhostIngredientPayload;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;

public class MachineGhostIngredientHandler<T extends ElectricBlockScreen<?>> implements IGhostIngredientHandler<T> {
    @Override
    public <I> List<Target<I>> getTargetsTyped(T screen, ITypedIngredient<I> ingredient, boolean doStart) {
        var menu = screen.getMenu();
        var machine = menu.blockEntity;
        if (machine == null) return List.of();
        var targets = new ArrayList<Target<I>>();
        if (ingredient.getIngredient() instanceof ItemStack stack && !stack.isEmpty()) {
            if (screen instanceof BatchFeederScreen feeder) {
                for (int slot = 0; slot < BatchFeederBlockEntity.PATTERN_SIZE; slot++) {
                    int index = slot;
                    targets.add(target(feeder.getBatchItemArea(slot), () -> sendItem(menu.containerId, index, stack)));
                }
            } else if (machine.supportsInputLocks()) {
                for (int slot = 0; slot < machine.inputItems.length; slot++) {
                    int index = slot;
                    var input = menu.slots.get(slot);
                    targets.add(target(
                            new Rect2i(screen.getLeftPos() + input.x - 1, screen.getTopPos() + input.y - 1, 18, 18),
                            () -> sendItem(menu.containerId, index, stack)));
                }
            }
        }
        if (screen instanceof BatchFeederScreen feeder) {
            FluidStack fluid =
                    switch (ingredient.getIngredient()) {
                        case FluidStack stack -> stack;
                        case ItemStack stack -> FluidUtil.getFirstStackContained(stack);
                        default -> FluidStack.EMPTY;
                    };
            if (!fluid.isEmpty())
                targets.add(target(
                        feeder.getBatchFluidArea(),
                        () -> FTBICNet.sendToServer(new SetGhostIngredientPayload(
                                menu.containerId,
                                BatchFeederBlockEntity.PATTERN_SIZE,
                                ItemStack.EMPTY,
                                fluid.copy()))));
        }
        return targets;
    }

    static void sendItem(int menu, int slot, ItemStack stack) {
        FTBICNet.sendToServer(new SetGhostIngredientPayload(menu, slot, stack.copy(), FluidStack.EMPTY));
    }

    static <I> Target<I> target(Rect2i area, Runnable action) {
        return new Target<>() {
            @Override
            public Rect2i getArea() {
                return area;
            }

            @Override
            public void accept(I ingredient) {
                action.run();
            }
        };
    }

    @Override
    public void onComplete() {}
}
