package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.material.RefiningCatalog;
import dev.ftb.mods.ftbic.registry.ModDataComponents;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

public class RefiningItem extends Item {
    public RefiningItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        Identifier material = stack.get(ModDataComponents.REFINING_MATERIAL.get());
        return material == null
                ? super.getName(stack)
                : Component.translatable(getDescriptionId() + ".material", RefiningCatalog.name(material));
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            TooltipDisplay display,
            Consumer<Component> tooltip,
            TooltipFlag flag) {
        Identifier material = stack.get(ModDataComponents.REFINING_MATERIAL.get());
        if (material != null)
            tooltip.accept(Component.literal(material.toString()).withStyle(ChatFormatting.DARK_GRAY));
    }

    public static ItemStack stack(Item item, Identifier material, int count) {
        ItemStack stack = new ItemStack(item, count);
        stack.set(ModDataComponents.REFINING_MATERIAL.get(), material);
        return stack;
    }
}
