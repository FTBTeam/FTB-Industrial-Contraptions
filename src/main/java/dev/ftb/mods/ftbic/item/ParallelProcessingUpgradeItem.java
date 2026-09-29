package dev.ftb.mods.ftbic.item;

import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

public class ParallelProcessingUpgradeItem extends UpgradeItem {
    public ParallelProcessingUpgradeItem(Properties properties) {
        super(properties, 2);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            TooltipDisplay display,
            Consumer<Component> tooltip,
            TooltipFlag flag) {
        tooltip.accept(Component.translatable("item.ftbic.parallel_processing_upgrade.description")
                .withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("item.ftbic.parallel_processing_upgrade.energy")
                .withStyle(ChatFormatting.GRAY));
    }
}
