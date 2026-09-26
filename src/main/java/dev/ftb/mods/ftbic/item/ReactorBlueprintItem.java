package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.block.entity.generator.NuclearReactorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.ReactorSimulatorBlockEntity;
import dev.ftb.mods.ftbic.registry.ModDataComponents;
import dev.ftb.mods.ftbic.util.ReactorDesign;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;

public class ReactorBlueprintItem extends Item {
    public ReactorBlueprintItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static boolean writeBlank(Inventory inventory, ReactorDesign design) {
        if (!design.isValid()) return false;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.is(FTBICItems.REACTOR_BLUEPRINT.get()) && !stack.has(ModDataComponents.REACTOR_DESIGN.get())) {
                stack.set(ModDataComponents.REACTOR_DESIGN.get(), design);
                inventory.setChanged();
                return true;
            }
        }
        return false;
    }

    public static boolean hasBlank(Inventory inventory) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.is(FTBICItems.REACTOR_BLUEPRINT.get()) && !stack.has(ModDataComponents.REACTOR_DESIGN.get())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();
        var player = context.getPlayer();
        var be = level.getBlockEntity(context.getClickedPos());
        if (player == null || !(be instanceof ReactorSimulatorBlockEntity || be instanceof NuclearReactorBlockEntity)) {
            return InteractionResult.PASS;
        }
        if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.SUCCESS;
        ItemStack blueprint = context.getItemInHand();
        ReactorDesign design = blueprint.get(ModDataComponents.REACTOR_DESIGN.get());
        if (be instanceof ReactorSimulatorBlockEntity planner) {
            if (design == null || player.isShiftKeyDown()) {
                ReactorDesign exported = planner.exportDesign();
                if (exported.isValid()) {
                    blueprint.set(ModDataComponents.REACTOR_DESIGN.get(), exported);
                    player.getInventory().setChanged();
                    player.sendOverlayMessage(Component.translatable("item.ftbic.reactor_blueprint.written"));
                }
            } else if (design.isValid() && planner.applyDesign(design)) {
                planner.openMenu(serverPlayer);
            } else {
                player.sendOverlayMessage(Component.translatable("item.ftbic.reactor_blueprint.planner_locked"));
            }
        } else if (be instanceof NuclearReactorBlockEntity reactor) {
            if (design != null && reactor.setPlannedDesign(design)) {
                reactor.openMenu(serverPlayer);
            } else {
                player.sendOverlayMessage(Component.translatable("item.ftbic.reactor_blueprint.blank"));
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.has(ModDataComponents.REACTOR_DESIGN.get());
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            TooltipDisplay display,
            Consumer<Component> tooltip,
            TooltipFlag flag) {
        ReactorDesign design = stack.get(ModDataComponents.REACTOR_DESIGN.get());
        if (design == null) {
            tooltip.accept(
                    Component.translatable("item.ftbic.reactor_blueprint.blank").withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.accept(Component.translatable(
                            "item.ftbic.reactor_blueprint.contents",
                            design.chambers(),
                            design.slots().size())
                    .withStyle(ChatFormatting.AQUA));
            tooltip.accept(
                    Component.translatable("item.ftbic.reactor_blueprint.load").withStyle(ChatFormatting.GRAY));
        }
        tooltip.accept(
                Component.translatable("item.ftbic.reactor_blueprint.write").withStyle(ChatFormatting.GRAY));
    }
}
